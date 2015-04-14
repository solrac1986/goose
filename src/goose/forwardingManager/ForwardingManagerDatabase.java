package goose.forwardingManager;

import java.util.Vector;
import goose.exceptions.GooseException;
import goose.mainManager.GooseTools;
import goose.mainManager.Root;
import goose.networkManager.bluetooth.Bluetooth;
import org.garret.perst.Iterator;
import org.garret.perst.Key;
import org.garret.perst.Storage;
import microlog.Logger;

public class ForwardingManagerDatabase {
    /*
     * For tracking changes in the messages DB
     */
    private boolean dbModified;
    /*
     * For enabling Perst db management
     */
    private Storage dbRef;
    /*
     * Logger
     */
    private Logger log = Logger.getLogger();
    
    public ForwardingManagerDatabase(Storage dbRef){
        this.dbModified = false;
        this.dbRef = dbRef;
    }
    
    
    public synchronized boolean isModified(){
    	return dbModified;
    }
    
    public synchronized void commit(){
//    	//log.debug("Database modified. Waiting to commit changes");
        dbRef.commit();
        dbModified = false;
//        //log.debug("FMdb. Database Updated. Space used: "+dbRef.getUsedSize()); 		
    }
    
    public synchronized void addMessage(GooseMessage m){

//    	//log.debug("Inside add Message");
    	if(m.GUID==null || m.GUID == ""){
//    		//log.debug("Add Message, empty GUID. Create a new one");
    		m.GUID = String.valueOf(GooseTools.getTime())+Bluetooth.getLocalAddress();
    	}
    	
    	if (exists(m)){
//    		//log.debug("FMdb.addMessage MESSAGE already exist");
    		return;    		
    	}
    	
    	dbRef.storeObject(m);
    	Root root = (Root) dbRef.getRoot();
       	
    	//all the messages will be stored in the all messages index in any case;
    	root.messageId.put(new Key(m.messageId), m);
    	root.senderName.put(new Key(m.senderName), m);
    	root.senderSurname.put(new Key(m.senderSurname), m);
    	root.senderPhone.put(new Key(m.senderPhoneNumber), m);
    	root.senderBTAddr.put(new Key(m.senderBTMacAddress), m);
    	root.recipient.put(new Key(m.recipient), m);
    	root.txMode.put(new Key(m.transmissionMode), m);
    	root.GUID.put(new Key(m.GUID), m);
    	root.folder.put(new Key(m.folder), m);
    	
    	
    	dbModified = true;
//    	//log.debug("FMdb addMessage. Message "+m.GUID+" stored in folder "+m.folder);
    	//log.debug("Msg added. Total messages: "+getItems().size()+"|Sent Folder: "+getSentMessages().size()+"|Received: "+getInboxMessages().size()+"| Forwarding: "+getForwardingMessages().size());
    }
    
    public synchronized Vector searchMessage(String searchParameterValue, int searchParameter) throws GooseException{
    	Root root = (Root)dbRef.getRoot();
    	Vector results = new Vector();
    	Iterator iterator = null;
    	
    	switch(searchParameter){
    	case ForwardingManager.MESSAGE_ID:
    		iterator = root.messageId.prefixIterator(searchParameterValue.toLowerCase());
    		break;
    	case ForwardingManager.SENDER_NAME:
    		iterator = root.senderName.prefixIterator(searchParameterValue.toLowerCase());
    		break;
    	case ForwardingManager.SENDER_SURNAME:
    		iterator = root.senderSurname.prefixIterator(searchParameterValue.toLowerCase());
    		break;
    	case ForwardingManager.SENDER_PHONE:
    		iterator = root.senderPhone.prefixIterator(searchParameterValue.toLowerCase());
    		break;
    	case ForwardingManager.SENDER_BTADDR:
    		iterator = root.senderBTAddr.prefixIterator(searchParameterValue.toLowerCase());
    		break;
    	case ForwardingManager.RECIPIENT:
    		iterator = root.recipient.prefixIterator(searchParameterValue.toLowerCase());
    		break;
    	case ForwardingManager.TX_MODE:
    		iterator = root.txMode.prefixIterator(searchParameterValue.toLowerCase());
    		break;
    	case ForwardingManager.GUID:
    		iterator = root.GUID.prefixIterator(searchParameterValue.toLowerCase());
    		break;
    	default:
    		throw new GooseException("FM. Unknown parameter search");
    	}	
    	while(iterator.hasNext()){
    		GooseMessage tmp = (GooseMessage)iterator.next();
    		results.addElement(tmp);    		
    	}
//    	//log.debug(results.size()+" messages found");
    	return results;
    }
    
    public synchronized void modifyMessageFolder(String messageUUID, String newFolder){
    	try{
    		GooseMessage m = exclusiveSearch(messageUUID, ForwardingManager.GUID);
    		
    		Root root = (Root)dbRef.getRoot();
    		
        	root.messageId.remove(new Key(m.messageId), m);
        	root.senderName.remove(new Key(m.senderName), m);
        	root.senderSurname.remove(new Key(m.senderSurname), m);
        	root.senderPhone.remove(new Key(m.senderPhoneNumber), m);
        	root.senderBTAddr.remove(new Key(m.senderBTMacAddress), m);
        	root.recipient.remove(new Key(m.recipient), m);
        	root.txMode.remove(new Key(m.transmissionMode), m);
        	root.GUID.remove(new Key(m.GUID), m);
        	root.folder.remove(new Key(m.folder), m);
        	      
        	dbRef.deallocateObject(m);
        	//We change the newMessage field
    		m.folder=newFolder;
        	dbRef.storeObject(m);        	
        	root.messageId.put(new Key(m.messageId), m);
        	root.senderName.put(new Key(m.senderName), m);
        	root.senderSurname.put(new Key(m.senderSurname), m);
        	root.senderPhone.put(new Key(m.senderPhoneNumber), m);
        	root.senderBTAddr.put(new Key(m.senderBTMacAddress), m);
        	root.recipient.put(new Key(m.recipient), m);
        	root.txMode.put(new Key(m.transmissionMode), m);
        	root.GUID.put(new Key(m.GUID), m);
        	root.folder.put(new Key(m.folder), m);
        	
    	    dbModified=true;
    	    
    	}
    	catch(Exception e){
//    		//log.debug("Error modifying message folder");
    	}
    }
    
    /*
     * It returns the message with the highest message ID 
     * 
     * NOTE. This method will return the newest one in the stack)
     * 
     */
    public synchronized GooseMessage exclusiveSearch(String searchParameterValue, int searchParameter) throws GooseException{
    	Root root = (Root)dbRef.getRoot();

    	Iterator iterator = null;
    	
    	switch(searchParameter){
    	case ForwardingManager.MESSAGE_ID:
    		iterator = root.messageId.prefixIterator(searchParameterValue.toLowerCase());
    		break;
    	case ForwardingManager.SENDER_NAME:
    		iterator = root.senderName.prefixIterator(searchParameterValue.toLowerCase());
    		break;
    	case ForwardingManager.SENDER_SURNAME:
    		iterator = root.senderSurname.prefixIterator(searchParameterValue.toLowerCase());
    		break;
    	case ForwardingManager.SENDER_PHONE:
    		iterator = root.senderPhone.prefixIterator(searchParameterValue.toLowerCase());
    		break;
    	case ForwardingManager.SENDER_BTADDR:
    		iterator = root.senderBTAddr.prefixIterator(searchParameterValue.toLowerCase());
    		break;
    	case ForwardingManager.RECIPIENT:
    		iterator = root.recipient.prefixIterator(searchParameterValue.toLowerCase());
    		break;
    	case ForwardingManager.TX_MODE:
    		iterator = root.txMode.prefixIterator(searchParameterValue.toLowerCase());
    		break;
    	case ForwardingManager.GUID:
    		iterator = root.GUID.prefixIterator(searchParameterValue.toLowerCase());
    		break;
    	default:
    		throw new GooseException("FM. Unknown parameter search");
    	}	
    	
    	GooseMessage tmp = null;
    	while(iterator.hasNext()){
    		//The last element is the one with the lowest index(proved) so it is the newest one
    		tmp = (GooseMessage)iterator.next();
    	}
    	if (tmp==null){
//    		//log.debug("Message search failed");
    		throw new GooseException("Message search failed");
    	}
    	return tmp;    	
    }
    
    /*
     * Returns the messages from all the folders
     */
    public synchronized Vector getItems() {
    	Root root = (Root)dbRef.getRoot();
    	Vector results = new Vector();
    	Iterator iterator = root.messageId.iterator();
    	
    	for (int i =0; iterator.hasNext(); i++){
    		GooseMessage tmp = (GooseMessage)iterator.next();
    		results.addElement(tmp);
    	}
    	return results;
    }
    
    /*
     * Returns the messages stored in the inbox folder
     * 
     * TODO. It can be maybe improved by including a new index for every folder but it can increase the complexity
     */
    public synchronized Vector getInboxMessages(){
    	Vector results = new Vector();
    	Vector tmp = getItems();
    	for (int i=0; i<tmp.size(); i++){
    		GooseMessage tmpMsg = (GooseMessage)tmp.elementAt(i);
    		if (tmpMsg.folder.equalsIgnoreCase(GooseMessage.INBOX_FOLDER)
    				|| tmpMsg.folder.equalsIgnoreCase(GooseMessage.INBOX_FORW_FOLDERS)
    				|| tmpMsg.folder.equalsIgnoreCase(GooseMessage.INBOX_SENT_FOLDERS)
    				|| tmpMsg.folder.equalsIgnoreCase(GooseMessage.ALL_FOLDERS)){
    			results.addElement(tmpMsg);
    		}
    	}
    	return results;
    }
    
    public synchronized boolean exists(GooseMessage m){
//
//    	//log.debug("Inside exists");
        Root root = (Root)dbRef.getRoot();


    	Iterator iterator = root.messageId.iterator();
    	
    	for (int i =0; iterator.hasNext(); i++){
    		GooseMessage tmp = (GooseMessage)iterator.next();
            if (m.GUID.equalsIgnoreCase(tmp.GUID)){   
//                //log.debug("OBJECT already stored in DB");   
                return true;
            }
    	}
//        //log.debug("No record of this object in db");
        return false;

    }

    
    public synchronized Vector getSortedInboxMessages() throws GooseException{
    	Vector results = new Vector();
    	Vector tmp = getItems();
//    	//log.debug("getSortedInboxMessages. Number of total messages stored:"+tmp.size());
    	for (int i=0; i<tmp.size(); i++){
    		GooseMessage tmpMsg = (GooseMessage)tmp.elementAt(i);
    		if (tmpMsg.folder.equalsIgnoreCase(GooseMessage.INBOX_FOLDER)
    				|| tmpMsg.folder.equalsIgnoreCase(GooseMessage.INBOX_FORW_FOLDERS)
    				|| tmpMsg.folder.equalsIgnoreCase(GooseMessage.INBOX_SENT_FOLDERS)
    				|| tmpMsg.folder.equalsIgnoreCase(GooseMessage.ALL_FOLDERS)){
//    			//log.debug("getSortedInboxMessages.Inbox message found");
    			results.addElement(tmpMsg);
    		}
    	}
//    	//log.debug("getSortedInboxMessages.Number of messages found in inbox:"+results.size()+". Ready to sort them");
    	//We sort them by time just to help in the display
    	try{
    		return sortMessagesByTimeStamp(results);
    	}
    	catch(Exception e){
    		throw new GooseException ("Error sorting inbox messages by date. "+e.getMessage());
    	}
    }
    
    /*
     * Returns the messages stored in the inbox folder
     * 
     * TODO. It can be maybe improved by including a new index for every folder but it can increase the complexity
     */
    public synchronized Vector getSentMessages(){
    	Vector results = new Vector();
    	Vector tmp = getItems();
    	for (int i=0; i<tmp.size(); i++){
    		GooseMessage tmpMsg = (GooseMessage)tmp.elementAt(i);
    		if (tmpMsg.folder.equalsIgnoreCase(GooseMessage.SENT_FOLDER)
    				|| tmpMsg.folder.equalsIgnoreCase(GooseMessage.ALL_FOLDERS)
    				|| tmpMsg.folder.equalsIgnoreCase(GooseMessage.SENT_FORWD_FOLDERS)
    				|| tmpMsg.folder.equalsIgnoreCase(GooseMessage.INBOX_SENT_FOLDERS)){
    			results.addElement(tmpMsg);
    		}
    	}
    	return results;
    }    
   
    /*
     * Returns the messages stored in the inbox folder
     * 
     * TODO. It can be maybe improved by including a new index for every folder but it can increase the complexity
     */
    public synchronized Vector getSortedSentMessages() throws GooseException{
    	Vector results = new Vector();
    	Vector tmp = getItems();
    	for (int i=0; i<tmp.size(); i++){
    		GooseMessage tmpMsg = (GooseMessage)tmp.elementAt(i);
    		if (tmpMsg.folder.equalsIgnoreCase(GooseMessage.SENT_FOLDER)
    				|| tmpMsg.folder.equalsIgnoreCase(GooseMessage.ALL_FOLDERS)
    				|| tmpMsg.folder.equalsIgnoreCase(GooseMessage.SENT_FORWD_FOLDERS)
    				|| tmpMsg.folder.equalsIgnoreCase(GooseMessage.INBOX_SENT_FOLDERS)){
    			results.addElement(tmpMsg);
    		}
    	}
    	return sortMessagesByTimeStamp(results);
    }    

    /*
     * Status Messages
     */
    public synchronized Vector getStatusMessages() throws GooseException {
    	Vector results = new Vector();
    	Vector tmp = getItems();
    	for (int i=0; i<tmp.size(); i++){
    		GooseMessage tmpMsg = (GooseMessage)tmp.elementAt(i);
    		if (tmpMsg.folder.equalsIgnoreCase(GooseMessage.STATUS_UPDATE_FOLDERS)){
    			results.addElement(tmpMsg);
    		}
    	}
    	////log.debug("Number of Status Messages: "+results.size());
    	if (results.size()>=10){
    		results.setSize(10);
    	}
    	return sortMessagesByTimeStamp(results);
    }
    
    /*
     * Returns the messages stored in the forwarding folder that will be used for sending future messages
     * 
     * TODO. It can be maybe improved by including a new index for every folder but it can increase the complexity
     */
    public synchronized Vector getForwardingMessages() {
    	Vector results = new Vector();
    	Vector tmp = getItems();
//    	//log.debug("getForwardingMessages: total number of messages stored: "+tmp.size());
    	for (int i=0; i<tmp.size(); i++){
    		GooseMessage tmpMsg = (GooseMessage)tmp.elementAt(i);
    		if (tmpMsg.folder.equalsIgnoreCase(GooseMessage.FORWARD_FOLDER)
    				|| tmpMsg.folder.equalsIgnoreCase(GooseMessage.ALL_FOLDERS)
    				|| tmpMsg.folder.equalsIgnoreCase(GooseMessage.SENT_FORWD_FOLDERS)
    				|| tmpMsg.folder.equalsIgnoreCase(GooseMessage.INBOX_FORW_FOLDERS)){
//    			//log.debug("getForwardingMessages. New Message addedd to the forwarding table vector");
    			results.addElement(tmpMsg);
    		}
    	}
//    	//log.debug("getForwardingMessages: total number of forwarding folder messages: "+results.size());
    	return results;
    }    
    
    /*
     * Deletes a single message m
     * 
     */
    public synchronized void delete(GooseMessage m){
    	Root root = (Root)dbRef.getRoot();
    	root.messageId.remove(new Key(m.messageId), m);
    	root.senderName.remove(new Key(m.senderName), m);
    	root.senderSurname.remove(new Key(m.senderSurname), m);
    	root.senderPhone.remove(new Key(m.senderPhoneNumber), m);
    	root.senderBTAddr.remove(new Key(m.senderBTMacAddress), m);
    	root.recipient.remove(new Key(m.recipient), m);
    	root.txMode.remove(new Key(m.transmissionMode), m);
    	root.GUID.remove(new Key(m.GUID), m);
    	root.folder.remove(new Key(m.folder), m);
    	      
    	dbRef.deallocateObject(m);
    	dbModified=true;
//    	//log.debug("Message "+m.GUID+" ready to delete");
//    	//log.debug("Total messages: "+getItems().size()+"|Sent Folder: "+getSentMessages().size()+"|Received: "+getInboxMessages().size()+"|Forwarding: "+getForwardingMessages().size());
    }
    
    
    public synchronized int currentIndex(){
    	Vector v = getItems();
    	if (v.size()>0)
    	{
    		GooseMessage c = (GooseMessage)v.elementAt(v.size()-1);
    		return c.messageId+1;
    	}
    	else{
    		return 0;
    	}
    }
    
    public synchronized void setMessageAsRead(String UUID) throws GooseException{
    	try{
    		Vector vec = searchMessage(UUID, ForwardingManager.GUID);
    		GooseMessage m = (GooseMessage)vec.elementAt(0);
    		
    		Root root = (Root)dbRef.getRoot();
    		
        	root.messageId.remove(new Key(m.messageId), m);
        	root.senderName.remove(new Key(m.senderName), m);
        	root.senderSurname.remove(new Key(m.senderSurname), m);
        	root.senderPhone.remove(new Key(m.senderPhoneNumber), m);
        	root.senderBTAddr.remove(new Key(m.senderBTMacAddress), m);
        	root.recipient.remove(new Key(m.recipient), m);
        	root.txMode.remove(new Key(m.transmissionMode), m);
        	root.GUID.remove(new Key(m.GUID), m);
        	root.folder.remove(new Key(m.folder), m);
        	      
        	dbRef.deallocateObject(m);
        	//We change the newMessage field
        	m.newMessage=false;

        	dbRef.storeObject(m);
        	
        	root.messageId.put(new Key(m.messageId), m);
        	root.senderName.put(new Key(m.senderName), m);
        	root.senderSurname.put(new Key(m.senderSurname), m);
        	root.senderPhone.put(new Key(m.senderPhoneNumber), m);
        	root.senderBTAddr.put(new Key(m.senderBTMacAddress), m);
        	root.recipient.put(new Key(m.recipient), m);
        	root.txMode.put(new Key(m.transmissionMode), m);
        	root.GUID.put(new Key(m.GUID), m);
        	root.folder.put(new Key(m.folder), m);
        	
    	    dbModified=true;
    	}
    	catch(Exception e){
    		throw new GooseException("Error setting message as read");
    	}
    }
    
    private Vector sortMessagesByTimeStamp(Vector messages) throws GooseException{
    	if (messages == null || messages.size()==0){
    		throw new GooseException("Sorting GooseMessages by date. Vector is not initialised or is empty");
    	}
        boolean swapped = true;

        // Setup our outer loop for detecting if a swap was made
        while (swapped) {
                swapped = false;

                // Loop through the array, bubbling up.
                for(int i=0; i < messages.size() - 1; i++)
                {
                	GooseMessage current = (GooseMessage)messages.elementAt(i);
                	GooseMessage next = (GooseMessage)messages.elementAt(i+1);
                	
                	
                        // This is where you compare your strings for equality (using compareTo)
                		//Having a higher recTime means that it was received earlier. We put the newest ones in the beggining
                        if(current.recTime< next.recTime)
                        {
                        	GooseMessage temp = (GooseMessage)messages.elementAt(i);
   
                                // You can't use elementAt for swapping, use setElementAt()
                        		messages.setElementAt(messages.elementAt(i+1),i);
                        		messages.setElementAt(temp,i+1);

                                // Yes a swap was made, so another pass is needed.
                                swapped = true;
                        }
                }
        }
        // After all sorting is finished, just list all the items in the array.
        return messages;
    }
    
    /*
     * Method that iterates in all the address book and stores the contacts in a vector.
     */
    public synchronized int getNumberOfStoredItems(){
        Root root = (Root)dbRef.getRoot();
    	return root.messageId.size();
    }
    
    public synchronized Storage getDbRef(){
    	return dbRef;
    }
    
    public void closeDB (){
//    	TODO: Probably if we close it here, we will close it for the other managers
//        dbRef.close();
    	dbRef=null;
//    	//log.debug("ContactsManager DB stopped");
    }
}
