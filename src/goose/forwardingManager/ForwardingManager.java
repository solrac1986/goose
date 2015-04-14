/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package goose.forwardingManager;

import java.util.Date;
import java.util.Stack;
import java.util.Vector;
import javax.bluetooth.ServiceRecord;
import org.garret.perst.Storage;

import gui.goose.resources.*;
import goose.mainManager.IncomingMessageEvent;
import goose.mainManager.IManager;
import goose.contactsManager.Contact;
import goose.contactsManager.ContactsManager;
import goose.exceptions.*;
import goose.networkManager.bluetooth.Bluetooth;
import goose.mainManager.ResourcesStatus;
import goose.networkManager.NetworkManager;
import goose.mainManager.GooseManager;
import goose.mainManager.GooseTools;
import microlog.Logger;

/**
 *
 * @author Narseo
 */
public class ForwardingManager extends Thread implements IManager{

/*
    private static final int ALL_MESSAGES = 0;
    private static final int SENT_FOLDER = 1;
    private static final int INBOX_FOLDER = 2;
    private static final int FORWARDING_FOLDER = 3;
*/    
    public static final int MESSAGE_ID = 0;
    public static final int SENDER_NAME = 1;
    public static final int SENDER_SURNAME = 7;
    public static final int SENDER_PHONE = 2;
    public static final int SENDER_BTADDR = 3;
    public static final int RECIPIENT = 4;
    public static final int TX_MODE = 5;
    public static final int GUID = 6;
    
    //To track the garbage collector
    public static long lastGc = 0;
    
    private GooseManager goose = null;
    
    private ReplicaControlTable rct = null;
    
    //For managing the events (incoming messages)
	private Stack eventStack = null;
	private boolean alive;
	private ForwardingManagerDatabase fwdb = null;
	
	private long lastTimeCleaning = GooseTools.getTime();
	private long lastTimeRtxForced = GooseTools.getTime();
	
    /*
     * Microlog Logger
     */
    private Logger log = Logger.getLogger();

    public ForwardingManager(Storage dbRef, GooseManager gooseMgrRef){
    	try{
            this.alive = true;
            this.goose = gooseMgrRef;
            this.rct = new ReplicaControlTable();
            this.eventStack = new Stack();
            this.fwdb = new ForwardingManagerDatabase(dbRef);
//            //log.debug("Forwarding Manager Created");
            
    		
    	}
    	catch(Exception e){
//    		//log.debug("Error starting Forwarding Manager: "+e.getMessage());
    	}        
    }

    public ForwardingManager(Storage dbRef){
        this.alive = true;
        this.fwdb = new ForwardingManagerDatabase(dbRef);
        this.rct = new ReplicaControlTable();
        this.eventStack = new Stack();
//        //log.debug("Forwarding Manager Created");
    }
    
    public void startManager(){
        try{
        	lastGc = GooseTools.getTime();
	    	eventStack = new Stack();
            start();
//            //log.debug("Forwarding Manager Running");
        }
        catch (Exception e){
            //Handle exception
//        	//log.debug("Error starting ForwardingManager Thread");
        }
    }

    public void run(){
        while (alive){
        	long now = GooseTools.getTime();
            //Commit is the most time consuming opperation. Decide when to perform it or not
            try{
            	if ((now-lastTimeCleaning)>15000){
            		lastTimeCleaning = now;
                	cleanForwardingTable();
                	cleanStatusTable();
            	}
            }
           
            catch(Exception e){
            	//log.debug("FM Exception cleaning Forwarding Table: "+e.getMessage());
            }
            try{
            	if ((now-lastTimeRtxForced)>(120000+GooseTools.getPositiveRandomLong()%120000)){
            		lastTimeRtxForced = now;
                	makeForwardingDecission();
            	}            	
            }
            catch(Exception e){
            	//log.debug("Error making forwarding decissions: "+e.getMessage());
            }
            try{
	        	if(fwdb.isModified()){
	                fwdb.commit();
//	                //log.debug("FM DB commit");
	            }
            }
            catch(Exception e){
            	//log.debug("Error when commit changes in Forwarding Manager database");
            }
			try{
	            //Check if a new event was stored
				if(this.eventStack.empty()==false){
						
//					//log.debug("FM. Getting a new event, ready to process it");
					//TODO, Notify it to the appropriate manager. It will be necessary to check what kind of message it is and so on
					IncomingMessageEvent tmp = (IncomingMessageEvent)getLastEvent();
					GooseMessage m = tmp.getMessage();
					//We update this values here, it doesnt matter if they are latter forwarded since we wont retrieve them unless they are for us from the folder
					//The other devices will also update these values every time they receive a message. So it is ok.
					m.newMessage=true;
					
					m.recTime = now;
//					rct.update(m.GUID, m.previousRelays);
					if (m.transmissionMode.equalsIgnoreCase(GooseMessage.CONTACTS_EXCHANGE_ACK)){
						//log.debug("Contacts exchange ACK received. Ready to store the contact");
						storeContact(m.content);						
						IncomingMessageEvent event = new IncomingMessageEvent(IncomingMessageEvent.CONTACT_DETAILS_EXCHANGE_ACK, "Contact details exchange completed with "+m.senderName+" "+m.senderSurname, m);
						goose.eventStack.storeNewEvent(event);
					}
					else if(m.transmissionMode.equalsIgnoreCase(GooseMessage.CONTACTS_EXCHANGE)){
						//log.debug("Contacts Exchange request received");
						IncomingMessageEvent event = new IncomingMessageEvent(IncomingMessageEvent.CONTACT_DETAILS_EXCHANGE, "The user "+m.senderName+" "+m.senderSurname+" wants to exchange his contact details with you", m);
						goose.eventStack.storeNewEvent(event);
					}
					else if(m.transmissionMode.equalsIgnoreCase(GooseMessage.CONTACT_PROFILE)){
						//log.debug("Contacts Profile request received");
						IncomingMessageEvent event = new IncomingMessageEvent(IncomingMessageEvent.CONTACT_PROFILE, "The user "+m.senderName+" "+m.senderSurname+" wants to update your personal profile", m);
						goose.eventStack.storeNewEvent(event);
					}
					else if(m.transmissionMode.equalsIgnoreCase(GooseMessage.CONTACT_PROFILE_ACK)){
						//log.debug("Contacts profile ACK  received");
						IncomingMessageEvent event = new IncomingMessageEvent(IncomingMessageEvent.CONTACT_PROFILE_ACK, "The user "+m.senderName+" "+m.senderSurname+" has updated the profile", m);
						goose.eventStack.storeNewEvent(event);
					}
					else{
						//log.debug("New "+m.transmissionMode+" message retrieved by the forwarding Manager from "+m.senderName);
											
						newIncomingMessage(m);
						//Now force to tx nearby devices. We do it now since we know that we can have a nearby device at least
						//log.debug("Forcing rtx to nearby devices");
//						makeForwardingDecission();
					}
				}
			}
			catch(Exception e){
				//log.debug("Error retrieving message"+e.getMessage());
			}
            //Commit changes every 30 secs?
            try{
                long sleepTime = 250+ GooseTools.getPositiveRandomLong()%250;
            	Thread.sleep(sleepTime);
            }
            catch(Exception e){
            	//log.debug("FM Error sleeping Forwarding Manager thread: "+e.getMessage());
            }
            try{       
            	Runtime r = Runtime.getRuntime();
            	
            	long freeMemory = r.freeMemory()*100;
            	long totalMemory = r.totalMemory();
            	long percentFreeMemory = freeMemory/totalMemory;
            	
            	if ((now-lastGc)>30000 || percentFreeMemory < 20){
            		String stringData;
            		Date date = new Date();
            		Runtime rt = Runtime.getRuntime();
            		stringData = UIRecordStoreTesting.SEPARATOR + UIRecordStoreTesting.TYPE;
            		stringData += UIRecordStoreTesting.MEMORY; 
            		stringData += UIRecordStoreTesting.SEPARATOR;
            		stringData += UIRecordStoreTesting.TIME + date.getTime();
            		stringData += UIRecordStoreTesting.SEPARATOR;
            		stringData += UIRecordStoreTesting.MEMORY_EVENT + UIRecordStoreTesting.MEMORY_GARBAGE;
            		stringData+= UIRecordStoreTesting.SEPARATOR;
            		stringData += UIRecordStoreTesting.MEMORY_BEFORE + (rt.totalMemory()-rt.freeMemory());
            		System.gc();
            		lastGc = GooseTools.getTime();
            		stringData+= UIRecordStoreTesting.SEPARATOR;
            		stringData += UIRecordStoreTesting.MEMORY_AFTER + (rt.totalMemory()-rt.freeMemory());
            		System.out.println("MEmoria: "+ stringData);
            		UIRecordStoreTesting.writeTrace(stringData.getBytes());
            	}
            }
            catch(Exception e){
            	//log.debug("FM ERROR PERFORMING GARBAGE COLLECTION");
            }
        }
    }
    
    	
    
    private void storeContact (String content){
		String param = content;
		String [] contactDetails = MessageTools.readContactMessage(param);
		ContactsManager ctMgrTmp = null;
		try{
			ctMgrTmp = (ContactsManager)goose.getManager(GooseManager.CONTACTS_MANAGER);
			Contact c = new Contact(goose.db,
					ctMgrTmp.currentIndex(),
					contactDetails[0],
					contactDetails[1],
					contactDetails[2],
					contactDetails[3]);
			ctMgrTmp.addContact(c);
		}
		catch(Exception e){
			//log.debug("Error storing incoming message: "+e.getMessage());
		}
    }

    public void stopManager(){
        alive = false;
        fwdb.closeDB();
        //log.debug("Forwarding Manager Stopped");
        log=null;
    }

    public void pauseManager(){
        try{
            this.wait();
        }
        catch (Exception e){
            //Handle exception
        	//log.debug("ERROR pausing thread");
        }
    }
    
    


    public void restartManager(){
        try{
            this.notify();
        }
        catch (Exception e){
            //Handle exception
        	//log.debug("ERROR restarting thread");
        }
    }
    
    private void makeForwardingDecission(){
//    	//log.debug("Inside MakeForwardingDecission");
    	
    	Vector nearbyServices = goose.getNearbyDevices();
    	
    	byte[] data;
    	String dataString;
    	Date date = new Date();
    	dataString = UIRecordStoreTesting.SEPARATOR + UIRecordStoreTesting.TYPE + UIRecordStoreTesting.FWMGR;
    	dataString+= UIRecordStoreTesting.SEPARATOR;
    	dataString += UIRecordStoreTesting.TIME + date.getTime();
    	dataString+= UIRecordStoreTesting.SEPARATOR;
    	dataString += UIRecordStoreTesting.DEVICES_FOUND + nearbyServices.size();
    	
    	
    	if (nearbyServices.size()==0){
//    		//log.debug("MakeForwardingDecission. No nearby devices to forward the information, forwarding decissions aborted");
    		//return;
    	}
    	
    	long now = GooseTools.getTime();
    	ForwardingDecissionsTable fd = new ForwardingDecissionsTable();
    	ReplicaControlTable auxRCT = new ReplicaControlTable();
    	
    	NetworkManager ntMgrRef = null;
    	ContactsManager ctMgrRef = null;
    	
    	try{
        	ntMgrRef = (NetworkManager) goose.getManager(GooseManager.NETWORK_MANAGER);
        	
    	}
    	catch(Exception e){
    		//log.debug("Error retrieving Network Manager");
    		return;
    	}
    	try{
        	ctMgrRef = (ContactsManager) goose.getManager(GooseManager.CONTACTS_MANAGER);
        	
    	}
    	catch(Exception e){
    		//log.debug("Error retrieving Contacts Manager");
    		return;
    	}
    	
    	Vector knownNearbyServices = new Vector();
    	try{
        	knownNearbyServices = ctMgrRef.searchNearbyKnownServiceRecord(nearbyServices);    		
//        	//log.debug("Number of known nearby devices found: "+knownNearbyServices.size());
    	}
    	catch(Exception e){
//    		//log.debug("Error retrieving known devices: "+e.getMessage());
    		return;
    	}

//    	//log.debug("Getting known devices from address book and forwarding messages: "+nearbyServices.size());
    	
    	Vector fwMsg = new Vector();
    	
    	try{
    		fwMsg = getForwardingMessages();
    	}
    	catch(Exception e){
//    		//log.debug("makeForwardingDecission. Error getting formarding Messages: "+e.getMessage());
    		return;
    	}
    	int numbcast = 0;
    	int nummcast = 0;
    	int numucast = 0;
    	long totalSize = 0;
    	//dataString+= UIRecordStoreTesting.SEPARATOR;
//    	//log.debug("makeForwardingDecission.Ready to iterate over the forwardingMessages. Total number: "+fwMsg.size());
    	for (int i=0; i<fwMsg.size(); i++){
    		GooseMessage m = (GooseMessage)fwMsg.elementAt(i);
    		totalSize += m.toString().length();
//    		//log.debug("makeForwardingDecission. Forwarding Mode: "+m.transmissionMode+" Message uuid: "+m.GUID);
    		if (m.transmissionMode.equalsIgnoreCase(GooseMessage.BCAST)){
    			numbcast++;
//    			//log.debug("makeForwardingDecission. Retransmiting BCAST Message to all the nearby devices");
    			for (int j=0; j<nearbyServices.size(); j++){
    				ServiceRecord tmpSr = (ServiceRecord)nearbyServices.elementAt(j);
    				if (rct.exists(m.GUID, tmpSr.getHostDevice().getBluetoothAddress())==false){
//    					//log.debug("makeForwardingDecission. Message was not sent to: "+tmpSr.getHostDevice().getBluetoothAddress());
    					fd.put(tmpSr, m.GUID);
//    					//log.debug("makeForwardingDecission. Message stored in forwarding decission table, now lets wait for the auxRCT");
    					auxRCT.put(m.GUID, tmpSr.getHostDevice().getBluetoothAddress());
//    					//log.debug("makeForwardingDecission. Message stored in the RCT");
    				}    				
    			}
    		}
    	
    		else{//Only known nodes for MCAST and SearchFriendsMessages
//    			//log.debug("Inside else");
    			if(m.transmissionMode.equalsIgnoreCase(GooseMessage.UCAST)) {
    				numucast++;
    			}
    			else if (m.transmissionMode.equalsIgnoreCase(GooseMessage.MCAST)) {
    				nummcast++;
    			}
				data = dataString.getBytes();
    			UIRecordStoreTesting.writeTrace(data);
    			for (int j=0; j<knownNearbyServices.size(); j++){
    				ServiceRecord tmpSr = (ServiceRecord)knownNearbyServices.elementAt(j);
//    				//log.debug("Service record to try: "+tmpSr.getHostDevice().getBluetoothAddress());
    				if(rct.exists(m.GUID, tmpSr.getHostDevice().getBluetoothAddress())==false){		
//    					//log.debug("Message "+m.GUID+" was not found in RCT");
	    				Vector messageRecipients = MessageTools.readRecipientList(m.recipient);
//	    				//log.debug("Message Recipient retrieved");
	    				for (int h=0; h<messageRecipients.size(); h++){
	    					String recipientBtAddr = (String)messageRecipients.elementAt(h);
//	    					//log.debug("Trying on a new message recipient: "+recipientBtAddr);
	    					if (tmpSr.getHostDevice().getBluetoothAddress().equalsIgnoreCase(recipientBtAddr)){
//	    						//log.debug("makeForwardingDecission. Message was not sent to: "+tmpSr.getHostDevice().getBluetoothAddress());
	    						fd.put(tmpSr, m.GUID);
//	        					//log.debug("makeForwardingDecission. Message stored in forwarding decission table, now lets wait for the auxRCT");
	        					auxRCT.put(m.GUID, tmpSr.getHostDevice().getBluetoothAddress());
//	        					//log.debug("makeForwardingDecission. Message stored in the RCT");
	    					}
	    				}
    				}
    			}
    		}    		
    	}
    	dataString += UIRecordStoreTesting.SEPARATOR;
    	dataString += UIRecordStoreTesting.BATTERY_LEVEL + System.getProperty("batterylevel");
    	dataString += UIRecordStoreTesting.SEPARATOR;
		dataString += UIRecordStoreTesting.MESSAGE_TYPE + UIRecordStoreTesting.BCAST;
		dataString += numbcast;
		dataString += UIRecordStoreTesting.SEPARATOR;
		dataString += UIRecordStoreTesting.MESSAGE_TYPE + UIRecordStoreTesting.MCAST;
		dataString += nummcast;
		dataString += UIRecordStoreTesting.SEPARATOR;
		dataString += UIRecordStoreTesting.MESSAGE_TYPE + UIRecordStoreTesting.UCAST;
		dataString += numucast;
		dataString += UIRecordStoreTesting.SEPARATOR;
		if (totalSize == 0) {
			dataString += UIRecordStoreTesting.SIZE_MESSAGE + 0;
		}
		else {
			dataString += UIRecordStoreTesting.SIZE_MESSAGE + (totalSize / (numbcast + nummcast + numucast));
		}
		
		
		
		
    	//Now add the messages from the status table that must be sent
    	Vector statusMessages = new Vector();
    	try{
    		statusMessages = getStatusMessages();
    		for (int i=0; i<nearbyServices.size(); i++){
    			ServiceRecord tmpSr = (ServiceRecord)nearbyServices.elementAt(i);
    			
        		for (int j=0; j<statusMessages.size(); j++){
        			GooseMessage m = (GooseMessage)statusMessages.elementAt(j);
        			if(rct.exists(m.GUID, tmpSr.getHostDevice().getBluetoothAddress())==false){	
        	    		fd.put(tmpSr, m.GUID);
        	    		auxRCT.put(m.GUID, tmpSr.getHostDevice().getBluetoothAddress());
        			}        				
        		}
    		}
    		
    		
    	}
    	
    	catch(Exception e){
    		//log.debug("makingForwardingDecissions. Error getting status messages: "+e.getMessage());
    	}
    	int rankingAfter = 0;
    	// Write TTL messages and GUID
    	
    	
    	
    	//Now send to every chosen device and increase the ranking. In addition, update rct and the nextrelays
    	//for every device, check what messages do we need to forward them.
//		//log.debug("makeForwardingDecissions. Ready to send the messages");
    	for(int i=0; i<nearbyServices.size(); i++){
//    		//log.debug("makeForwardingDecissions. New Loop Entry");
    		ServiceRecord tmpSr = (ServiceRecord)nearbyServices.elementAt(i);
    		Vector messagesToSend = new Vector();

    		try {
    			rankingAfter = ctMgrRef.getUserRank(tmpSr.getHostDevice().getBluetoothAddress(), ContactsManager.BTADDRESS);
//    			rankingAfter = ((Contact)(contact.elementAt(0))).getScore();
    		}catch(Exception e) {
    			////log.debug("");
    			rankingAfter = -1;
    		}
    		
    		try{
    			
    			messagesToSend = fd.getMessages(tmpSr);
//    			//log.debug("Number of messages retrieved to "+tmpSr.getHostDevice().getBluetoothAddress()+": "+messagesToSend.size());
    		}
    		catch(Exception e){
//    			//log.debug("makeForwardingDecission. Exception: "+e.getMessage());
    		}
    		for (int j=0; j<messagesToSend.size(); j++){
    			try{
        			String msgUUID = (String)messagesToSend.elementAt(j);
    			
	    			Vector foundMsgs = searchMessage(msgUUID, ForwardingManager.GUID);
	    			GooseMessage tmpGm = (GooseMessage)foundMsgs.elementAt(0);
	    			
//	    			//log.debug("Message "+tmpGm.transmissionMode+" to send: "+tmpGm.GUID);
	    			Vector nextRelays = auxRCT.getRecipient(tmpGm.GUID);
//	    			//log.debug("NextRelays retrieved from RCT: "+nextRelays.size());
//	    			//log.debug("PreviousRelays: "+tmpGm.previousRelays);
//	    			//log.debug("localBTAddr: "+goose.getLocalBluetoothAddress());
	    			String relaysString = MessageTools.writeNextRelaysList(goose.getLocalBluetoothAddress(), nextRelays, tmpGm.previousRelays);
//	    			//log.debug("The previous relays for his message: "+relaysString);
	    			tmpGm.previousRelays=relaysString;
	    			try{
	        			long timeOut = now;
	    				long timeIn = rct.getTimeIn(tmpGm.GUID);
//	    				//log.debug("Time the message entered the system: "+timeIn);
	        			long newTTL = tmpGm.TTL - (timeOut-timeIn);
	        			tmpGm.TTL = newTTL;
//	        			//log.debug("New TTL. "+tmpGm.TTL);
						dataString += UIRecordStoreTesting.SEPARATOR + UIRecordStoreTesting.UUID;
						dataString += tmpGm.GUID;
						dataString += UIRecordStoreTesting.SEPARATOR+ UIRecordStoreTesting.TTL;
						dataString += tmpGm.TTL;
	    			}
	    			catch(Exception e){
//	    				//log.debug(e.getMessage());
	    				//We do not update TTL. 
	    			}
	    			
	    			try{
//	    				//log.debug("Sending message "+tmpGm.GUID+" to "+tmpSr.getHostDevice().getBluetoothAddress());
	    				ntMgrRef.send(tmpGm, tmpSr.getConnectionURL(0, false), NetworkManager.BTID);
	    				//log.debug("MakeForwardingDecission. Message "+tmpGm.GUID+", txMode: "+tmpGm.transmissionMode+" is sent over bluetooth to "+tmpSr.getHostDevice().getBluetoothAddress()+", now updating RCT");
	    				rct.put(tmpGm.GUID, tmpSr.getHostDevice().getBluetoothAddress());
//	    				//log.debug("MakeForwardingDecission. Updating Contact Rank");
	    				ctMgrRef.updateContactRank(tmpSr.getHostDevice().getBluetoothAddress(), ContactsManager.RELAY_INCREASE_RANK);
//	    				//log.debug("Contact Rank updated");
	    			}
	    			catch(Exception e){
	    				//log.debug(e.getMessage());
	    			}
	    			
    			}
    			catch(Exception exc){
//    				//log.debug("makeForwardingDecissions. Exception retriving messages from table: "+exc.getMessage());
    			}
    		}
    		dataString += UIRecordStoreTesting.SEPARATOR;
    		dataString += UIRecordStoreTesting.LAST_ITEM;
    		dataString += UIRecordStoreTesting.SEPARATOR;
    		dataString += UIRecordStoreTesting.MAC + tmpSr.getHostDevice().getBluetoothAddress();
			//dataString += UIRecordStoreTesting.SEPARATOR;
			//dataString += UIRecordStoreTesting.RANKING_BEFORE+ rankingBefore;
			dataString += UIRecordStoreTesting.SEPARATOR;
			dataString += UIRecordStoreTesting.RANKING_AFTER+ rankingAfter;
    	}
    	if (nearbyServices.size() == 0) {
			
    		dataString += UIRecordStoreTesting.SEPARATOR;
    		dataString += UIRecordStoreTesting.MAC + "-";
			//dataString += UIRecordStoreTesting.SEPARATOR;
			//dataString += UIRecordStoreTesting.RANKING_BEFORE+ rankingBefore;
			dataString += UIRecordStoreTesting.SEPARATOR;
			dataString += UIRecordStoreTesting.RANKING_AFTER+ "0";
			data = dataString.getBytes();
    		UIRecordStoreTesting.writeTrace(data);
    	}
    	else{
    		data = dataString.getBytes();
    		UIRecordStoreTesting.writeTrace(data);
    	}
    }

    public ReplicaControlTable getRCT (){
    	return this.rct;
    }
    
    public void cleanStatusTable(){
    	Vector statusMessages = new Vector();
    	try{
    		getStatusMessages();
    	}
    	catch(Exception e){
    		////log.debug("Error retrieving status messages: "+e.getMessage());
    		return;
    	}    	
    	if (statusMessages.size()>=10){
    		for (int j=10; j<statusMessages.size(); j++){
    			GooseMessage tmp = (GooseMessage)statusMessages.elementAt(j);
    			delete(tmp);
    		}
    	}
    }
    
    public void cleanForwardingTable(){
    	int threshold = 7;
    	Vector forwardingMessages = new Vector();
    	try{
    		forwardingMessages = getForwardingMessages();
    	}
    	catch(Exception e){
//    		//log.debug("cleanForwardingTable. Error retrieving forwarding messages "+e.getMessage());
    	}
    	if (forwardingMessages.size()<=threshold){
    		return;
    	}
    	//log.debug("cleanForwardingTable. Messages in ForwardingTable "+forwardingMessages.size()+". Ready to delete");
    	long now = GooseTools.getTime();
        int storedItems = 0;
    	

        ContactsManager ctMgrRef = null;
        
        try{
        	ctMgrRef = (ContactsManager)goose.getManager(GooseManager.CONTACTS_MANAGER);
        }
        catch(Exception e){
//        	//log.debug("cleanForwardingTable. ContactsManager not available: "+e.getMessage());
        	return;
       	}

        //Vector to track the messagesGuid of the messages stored in the forwarding folder
    	String [] messagesGuid = new String [forwardingMessages.size()];
    	//Vector to track the folder of the messages stored in the forwarding folder (They might be shared with inbox/outbox)
    	String [] messagesFolder = new String[forwardingMessages.size()];
    	//Vector to track the messagesRank of the messages stored in the forwarding folder
    	int [] messagesRank = new int [forwardingMessages.size()];
    		
    	for (int i=0; i<forwardingMessages.size(); i++){
    		GooseMessage tmp = (GooseMessage)forwardingMessages.elementAt(i);
    			//We rank the messages by their TTL, priority and number of the user contacts that are in the recipient list
    		int rank=0;
    		try{
    			long timeIn = tmp.recTime;
    			long timeInDevice = now - timeIn;
//    			//log.debug("CleaningForwardingTable. Message "+tmp.GUID+" was in device for "+(tmp.TTL-tmp.recTime));
    			
    			if (tmp.TTL-timeInDevice<=0){
    					//log.debug("CleaningForwardingTable. Message "+tmp.GUID+" TTL expired. Rank = -1");
    					rank=-1;
    			}
    			else{
    				Vector recipientList = MessageTools.readRecipientList(tmp.recipient);
    				rank+=tmp.priority;
    				rank+=ctMgrRef.getNumberOfContacts(recipientList);
//    				//log.debug("CleaningForwardingTable. Message "+tmp.GUID+" has a rank to delete value: "+rank);
    			}
    		}
    		catch(Exception e){
//    				//log.debug("CleaningForwardingTable. Entry not found in RCT (maybe RCT restarted): "+e.getMessage()+", rank= 0");
    				rank=0;
//    				//log.debug("CleaningForwardingTable. Ready to update RCT");
    				Vector recipientList = MessageTools.readRecipientList(tmp.previousRelays);
    				rct.put(tmp.GUID, recipientList);
//    				//log.debug("CleaningForwardingTable. Updated RCT");
    		}    		
    			
    		//Sorting the object
    		if(storedItems==0){
    				messagesGuid[0]=tmp.GUID;
    				messagesRank[0]=rank;
    				messagesFolder[0]=tmp.folder;
    		}
    		else{
    			boolean added = false;
    			for (int j=0; j<storedItems; j++){
    				if (rank>messagesRank[j]){
    					for (int h=storedItems-1; h>=j; h--){
    						messagesRank[h+1]=messagesRank[h];
    						messagesGuid[h+1]=messagesGuid[h];
    						messagesFolder[h+1]=messagesFolder[h];
    					}
    					messagesRank[j]=rank;
    					messagesGuid[j]=tmp.GUID;
    					messagesFolder[j]=tmp.folder;
    					added=true;
    					break;
    				}
    			}
    			if(!added){
    				messagesRank[storedItems]=rank;
    				messagesGuid[storedItems]= tmp.GUID;
    				messagesFolder[storedItems]=tmp.folder;
    			}
    		}
    		storedItems++;
    	}	
    		
    		//Now iterate and delete those useless
    	for (int i=0; i<forwardingMessages.size(); i++){
    		if(i>=threshold || messagesRank[i]< 0){
    			if (messagesFolder[i].equalsIgnoreCase(GooseMessage.SENT_FORWD_FOLDERS)){
    				//modify message folder to sent only
    				fwdb.modifyMessageFolder(messagesGuid[i], GooseMessage.SENT_FOLDER);
    				//log.debug("Cleaning, Object "+messagesGuid[i]+" moved to sent folder only with rank "+messagesRank[i]);
    			}
    			else if (messagesFolder[i].equalsIgnoreCase(GooseMessage.INBOX_FORW_FOLDERS)){
    				//Modifiy message folder to inbox only
    				//log.debug("Cleaning, Object "+messagesGuid[i]+" moved to inbox folder only with rank "+messagesRank[i]);
    				fwdb.modifyMessageFolder(messagesGuid[i], GooseMessage.INBOX_FOLDER);
    			}
    			else{
        			try{
        				delete(messagesGuid[i]);
        				//log.debug("Cleaning, Deleting message "+messagesGuid[i]+" with rank "+messagesRank[i]);
        			}
        			catch(Exception e){
        				//log.debug("Error deleting message: "+messagesGuid[i]+" when cleaning"); 					
        			}    					
    			}
    		}
    	}
    }
    
    /*
     * Method invoked when a new message was received. It sends the message to the GUI if necessary, stores the message in the appropriate folder and updates the RCT and the users rank
     */
    public void newIncomingMessage (GooseMessage m){
    	m.recTime=GooseTools.getTime();
    	String stringData;
		Runtime rt = Runtime.getRuntime();
		stringData = UIRecordStoreTesting.SEPARATOR + UIRecordStoreTesting.TYPE;
		stringData += UIRecordStoreTesting.MEMORY; 
		stringData += UIRecordStoreTesting.SEPARATOR;
		stringData += UIRecordStoreTesting.MEMORY_EVENT + UIRecordStoreTesting.MEMORY_MESSAGE;
		stringData+= UIRecordStoreTesting.SEPARATOR;
		stringData += UIRecordStoreTesting.MEMORY_BEFORE + (rt.totalMemory()-rt.freeMemory());
    	try{
        	ContactsManager ctMgrRef;
        	try{
        		ctMgrRef = (ContactsManager)goose.getManager(GooseManager.CONTACTS_MANAGER);
        	}
        	catch(Exception e){
        		//log.debug("newIncomingMessage. Error retrieving contacts manager. We could not handle the error");
        		return;
        	}
        	
    		if(m.senderBTMacAddress.equalsIgnoreCase(goose.getLocalBluetoothAddress())){
    			//log.debug("HEY; I HAVE RECEIVED A "+m.transmissionMode+" MESSAGE I HAVE CREATED!");
    			return;
    		}
    		if (m.transmissionMode.equalsIgnoreCase(GooseMessage.STATUS_UPDATE)){
    			if (ctMgrRef.exclusiveSearch(m.senderBTMacAddress, ContactsManager.BTADDRESS).size()<=0)
    			{
    				//log.debug("Status update from unknown people received. Not going to be processed");
    				return;
    			}
    			else{
    				//log.debug("Status update from a contact received");
    			}
    		}
        	Vector previousRelays = new Vector();
        	boolean toDelete = false;
    		//Check if the message is already stored in forwarding folder and also in the rtc
    		try{
    			Vector messagesFound = searchMessage(m.GUID, ForwardingManager.GUID);
    	    	if (messagesFound.size()>0){

//    	        	//log.debug("checking RCT?");
    	        	if(rct.exists(m.GUID, m.senderBTMacAddress)){
    	    			//get previous relays and update RCT to do not forward to them in the future
    	        		previousRelays = MessageTools.readRecipientList(m.previousRelays);
    	        		//log.debug("newIncomingMessage. Existing message, previousRelays list to be updated "+previousRelays);
    	    			for (int i=0; i<previousRelays.size(); i++){
    	    				rct.put(m.GUID, (String)previousRelays.elementAt(i));
    	    			}
    	    			//log.debug("Object was found in RCT; So it wont be processed");
    	    			
    	    		}
//    	    		//log.debug("Hey, I HAVE RECEIVED A MESSAGE I ALREADY HAVE!");
    	    		return;
    	    	}
    		}
    		catch(Exception e){
    			//log.debug("newIncomingMessage. Exception "+e.getMessage()+" but still ok");
    		}
    		
    		IncomingMessageEvent event = null;
        	//If the message has been already received, it must be stored in the RCT and as a consequence, no need to perform
    
//        	//log.debug("Getting CMGR?");

        	Contact sender = null;
        	
        	//Check if the message is for us. We need to do a loop since the address can be lower/uper-case
        	boolean isForMe = false;
        	Vector recipients = new Vector();
        	if (m.transmissionMode.equalsIgnoreCase(GooseMessage.BCAST)
        			||m.transmissionMode.equalsIgnoreCase(GooseMessage.FRIEND_SEARCH)
        			||m.transmissionMode.equalsIgnoreCase(GooseMessage.STATUS_UPDATE)
        			||m.transmissionMode.equalsIgnoreCase(GooseMessage.FRIEND_SEARCH_ACK)){
        		isForMe=true;
//        		//log.debug("newIncomingMessage. BCAST; FSEARCH; so it is for me");
        	}
        	else{
//        		//log.debug("Check recipients?");
            	recipients = MessageTools.readRecipientList(m.recipient);
//            	//log.debug("newIncomingMessage. Number of recipients of the message: "+recipients.size());
            	
            	for(int i=0; i<recipients.size(); i++){
            		String tmp = (String)recipients.elementAt(i);
//            		//log.debug("Recipient "+i+": "+tmp);
            		if (tmp.equalsIgnoreCase(Bluetooth.getLocalAddress())
            				||tmp.equalsIgnoreCase(GooseMessage.LOCAL)
            				||tmp.equalsIgnoreCase(GooseMessage.BCAST)){
            			isForMe=true;
            		}
            	}    		
        	}
        	//UPDATE RCT
//        	//log.debug("RCT update?");
    		rct.update(m.GUID, m.previousRelays);
    		m.newMessage=true;
    		
        	if(isForMe){
        		Vector senderContact = new Vector();
        		try{
    				senderContact = ctMgrRef.exclusiveSearch(m.senderName, ContactsManager.NAME);
    			}
    			catch(Exception e){
//    				//log.debug("newIncomingMessage. Sender couldn't be retrieved");
    				try{
    					sender = new Contact(m.senderName, m.senderSurname, m.senderBTMacAddress, m.senderPhoneNumber);
    				}
    				catch(Exception e2){
    					//log.debug(e2.getMessage());
    				}
    			}
    			//We check if the sender match name and surname
    			for (int i=0; i<senderContact.size(); i++){
    				Contact tmp = (Contact)senderContact.elementAt(i);
    				if (tmp.getSurname()==m.senderSurname){
    					sender = tmp;
    				}
    			}
    			if (sender==null){
//    				//log.debug("Sender not found in contact list. Creating a new temp sender");
    				sender = new Contact(m.senderName, m.senderSurname, m.senderBTMacAddress, m.senderPhoneNumber);
    			}
//    			//log.debug("newIncomingMessage. Sender retrieved: "+sender.getName());
    			//If i am the unicast receiver
    			
        		if(m.transmissionMode.equalsIgnoreCase(GooseMessage.UCAST)){
//        			//log.debug("newIncomingMessage. UCAST MESSAGE RECEIVED");
        			m.folder=GooseMessage.INBOX_FOLDER;
        			ctMgrRef.updateContactRank(sender.getBluetoothAddress(), ContactsManager.UNICAST_INCREASE_RANK);    

            		m.folder=GooseMessage.INBOX_FOLDER;
            		m.recTime=GooseTools.getTime();
            		addMessage(m);
            		fwdb.commit();
            		event = new IncomingMessageEvent(IncomingMessageEvent.UCAST_MESSAGE, "New UCAST message received from "+m.senderName+" "+m.senderSurname, m);
            		
        		}
        		else if (m.transmissionMode.equalsIgnoreCase(GooseMessage.STATUS_UPDATE)){
        			m.folder=GooseMessage.STATUS_UPDATE_FOLDERS;
        			ctMgrRef.updateContactRank(sender.getBluetoothAddress(), ContactsManager.BROADCAST_INCREASE_RANK);
        			addMessage(m);
        			fwdb.commit();
        			event = new IncomingMessageEvent (IncomingMessageEvent.STATUS_UPDATE, "New Status update received from "+m.senderName+" "+m.senderSurname, m);
        		}
        		//If I am one of many receivers (mcast, ucast)
        		else if (m.transmissionMode.equalsIgnoreCase(GooseMessage.FRIEND_SEARCH)){
//    				//log.debug("newIncomingMessage. New search friends message received. Look if it is stored");
    				
    				m.folder=GooseMessage.FORWARD_FOLDER;
    				m.recTime=GooseTools.getTime();
    				if(!processFriendSearchMessage(m)){
    					addMessage(m);
            			fwdb.commit();
    				}
    				ctMgrRef.updateContactRank(sender.getBluetoothAddress(), ContactsManager.MULTICAST_INCREASE_RANK);
    				toDelete=true;
    				//TODO. I have modified this on the 6th of August. It worked before
//            		addMessage(m);   
//            		fwdb.commit();
        		}
        		else if (m.transmissionMode.equalsIgnoreCase(GooseMessage.FRIEND_SEARCH_ACK)){
        			//In this case, the message recipient is included in the same way as in a Unicast message
        			//Retrieve contact details and store them.
//        			//log.debug("newIncomingMessage. New friend search ACK message received");
        			
//        			//log.debug("Message ACK Recipient in the ACK. "+m.recipient);

        			String[] dataInput = new String[4];
        			dataInput = MessageTools.readContactsACKMessage(m.content);
        			
        			
        			String guiMsg = "Contact details for "+dataInput[0]+" "+dataInput[1]+" received";
            		event = new IncomingMessageEvent(IncomingMessageEvent.FRIEND_SEEK_ACK,guiMsg,m);
            		if (m.recipient.equalsIgnoreCase(goose.getLocalBluetoothAddress())==false){
//            			//log.debug("Device is not the recipient of the ACK so store message for future retx");
                		m.folder=GooseMessage.FORWARD_FOLDER;
                		m.recTime=GooseTools.getTime();
                		addMessage(m);  
                		fwdb.commit(); 
                		toDelete=true;
            		}      			
            		else{
//            			//log.debug("Should not create event again");
                		goose.eventStack.storeNewEvent(event);
                		toDelete=true;

//            			//log.debug("Device is the recipient of the ACK so no need to store the msg");
            		}
        		}
        		else{
        			m.folder=GooseMessage.INBOX_FORW_FOLDERS;
        			rct.put(m.GUID, previousRelays);
        			
        			if(m.transmissionMode.equalsIgnoreCase(GooseMessage.MCAST)){
//        				//log.debug("newIncomingMessage. MCAST Message received, ready to update contact rank");
        				try{
        					ctMgrRef.updateContactRank(sender.getBluetoothAddress(), ContactsManager.MULTICAST_INCREASE_RANK);        					
        				}
        				catch(Exception e1){
        					//log.debug("Exception updating sender contact rank: "+e1.getMessage());
        				}
//        				//log.debug("Contact rank updated");
                		event = new IncomingMessageEvent(IncomingMessageEvent.MCAST_MESSAGE, "New Multicast Message from "+m.senderName+" "+m.senderSurname, m);
//                		//log.debug("Event created");
        			}
        			else if(m.transmissionMode.equalsIgnoreCase(GooseMessage.BCAST)){
//        				//log.debug("newIncomingMessage. BCAST Message received");
        				ctMgrRef.updateContactRank(sender.getBluetoothAddress(), ContactsManager.BROADCAST_INCREASE_RANK);

                		event = new IncomingMessageEvent(IncomingMessageEvent.BCAST_MESSAGE, "New Broadcast Message from "+m.senderName+" "+m.senderSurname, m);
                		
        			}
        			m.recTime=GooseTools.getTime();
        			addMessage(m);
            		fwdb.commit();
        		}
        		
        		stringData+= UIRecordStoreTesting.SEPARATOR;
        		stringData += UIRecordStoreTesting.MEMORY_AFTER + (rt.totalMemory()-rt.freeMemory());
        		UIRecordStoreTesting.writeTrace(stringData.getBytes());

        	}
        	else{
        		//We are being used as a relay
        		//log.debug("newIncomingMessage. We have received a message that is not for us. Store and forward");
        		m.folder=GooseMessage.FORWARD_FOLDER;
        		
        		//We append our btAddress to the previousRelays
        		m.previousRelays = m.previousRelays+"|"+goose.getLocalBluetoothAddress();
        		m.recTime=GooseTools.getTime();
        		addMessage(m);
        		fwdb.commit();
    			rct.put(m.GUID, previousRelays);    		
        	}
//        	//log.debug("Update previous relays rank");
        	//Update previous relays rank if they exist in the address book
        	for(int i=0; i<previousRelays.size(); i++){
        		try{
            		ctMgrRef.updateContactRank((String)previousRelays.elementAt(i),ContactsManager.PREVIOUS_RELAYS_INCREASE_RANK);    		        			
        		}
        		catch(Exception e2){
        			//log.debug("Error updating contact rank for "+(String)previousRelays.elementAt(i)+": "+e2.getMessage());
        		}
        	}
    		//Add the message to the database
        	if(toDelete==false){
        		m.recTime=GooseTools.getTime();
        		addMessage(m);   
        		fwdb.commit();
        		goose.eventStack.storeNewEvent(event);
//        		//log.debug("Event created, waiting to be processed by the GUI");
        	}		
    	}
    	catch(Exception e){
    		//log.debug("Exception in newIncomingMessage: "+e.getMessage());
    	}
    }
    
    public long getMessageTimeIn(String UUID){
    	try{
    		return rct.getTimeIn(UUID);
    	}
    	catch(Exception e){
    		return GooseTools.getTime();
    	}
    }
    
    private boolean processFriendSearchMessage(GooseMessage m){
    	try{
    		String [] searchParameterValue = MessageTools.readSearchContactMessageValue(m.content);
    		int [] searchPattern = MessageTools.readSearchContactMessagePattern(m.content, searchParameterValue.length);
    		ContactsManager ctMgrRf = (ContactsManager)goose.getManager(GooseManager.CONTACTS_MANAGER);	
	    	Vector ctVector = ctMgrRf.exclusiveSearch(searchParameterValue, searchPattern);
	    	if(ctVector.size()<=0){	
	    		//log.debug("Search friend request. Contact not found in Local Address Book");
	    	}	
	    	else{
	//    	Append the contactsFound to the message body (only the first one)
		    	Contact tmpCt = (Contact)ctVector.elementAt(0) ;
		    	//log.debug("Contact phone number to return in the ACK. "+tmpCt.getPhoneNumber());
		    	String messageBody = tmpCt.toString();
		    	GooseMessage reply = createSearchFriendACK(messageBody, m.senderBTMacAddress);
//		    	//log.debug("Destination phonenumber to return the ACK message: "+m.phoneNumber);
		    	Contact destination = new Contact(m.name, m.surname, m.btAddress, "");
		    	Vector recipientList = new Vector();
		    	recipientList.addElement(destination);
		    	//log.debug("Recipient List updated");
		//    	delete old message from the forwarding folder if it is there
		//    	 delete(m.GUID);
		    	ResourcesStatus rs = goose.getResourcesStatus();
		    	//log.debug("processFriendSearchMessage. ResourcesStatus retrieved");
		    	try{
		    		sendNewMessage(recipientList, reply,rs);
		    	}
		    	catch(Exception e){
		//    	TODO. Do something about it
		    		//log.debug("Error sending the message: "+e.getMessage());
		    	}
		    	//log.debug("Seek friends ACK sent");
		    	return true;
	    	}
    	}
    	catch(Exception e){
    		//log.debug("Error processing an incoming Friend Search request: "+e.getMessage());
    	}
   	return false;

    }
    
    
    private GooseMessage createSearchFriendACK(String messageBody, String btDestination){
    	String [] dataInput = {
    			String.valueOf(currentIndex()),
    			goose.firstname,
    			goose.surname,
    			goose.phoneNumber,
    			goose.getLocalBluetoothAddress(),
    			btDestination,
    			GooseMessage.FRIEND_SEARCH_ACK,
    			String.valueOf(GooseMessage.DEFAULT_MCAST_TTL),
    			String.valueOf(GooseMessage.LOW),
    			createGUID(),
    			goose.getLocalBluetoothAddress(),
    			GooseMessage.SEARCH,
    			messageBody,
    			GooseMessage.FORWARD_FOLDER, 
    	};
    	
    	return new GooseMessage(goose.db, dataInput, false, -1);
    }
    
    public void addMessage(GooseMessage m/*, int database*/){
    	//this sleep will make that the database wont add a new element if the database was not updated
    	if(fwdb.isModified()){
    		sleepThread(25+GooseTools.getPositiveRandomLong()%50);
    	}
    	fwdb.addMessage(m);
    }

    private void sleepThread(long msecs){
    	try{
    		Thread.sleep(msecs);
    	}
    	catch(Exception e){
    		//log.debug("Error stopping thread");
    	}    	
    }
    
    public Vector searchMessage(String searchParameterValue, int searchParameter) throws GooseException{
    	return fwdb.searchMessage(searchParameterValue, searchParameter);
    }

    /*
     * It returns the message with the highest message ID 
     * 
     * NOTE. This method will return the newest one in the stack)
     * 
     */
    public GooseMessage exclusiveSearch(String searchParameterValue, int searchParameter) throws GooseException{
    	return fwdb.exclusiveSearch(searchParameterValue, searchParameter); 	
    }
    
    /*
     * Returns the messages from all the folders
     */
    public Vector getItems(){
    	return fwdb.getItems();
    }
    
    /*
     * Returns the messages stored in the inbox folder
     * 
     * TODO. It can be maybe improved by including a new index for every folder but it can increase the complexity
     */
    public Vector getInboxMessages(){
    	return fwdb.getInboxMessages();
    }
    
    public Vector getSortedInboxMessages() throws GooseException{
    	return fwdb.getSortedInboxMessages();
    }

    /*
     * Returns the messages stored in the inbox folder
     * 
     * TODO. It can be maybe improved by including a new index for every folder but it can increase the complexity
     */
    public Vector getSentMessages(){
    	return fwdb.getSentMessages();
    }    
    
    public Vector getSortedSentMessages() throws GooseException{
    	return fwdb.getSortedSentMessages();
    }
    
    /*
     * Returns the messages stored in the forwarding folder that will be used for sending future messages
     * 
     * TODO. It can be maybe improved by including a new index for every folder but it can increase the complexity
     */
    public Vector getForwardingMessages() {
    	Vector returnVect= fwdb.getForwardingMessages();
//    	//log.debug("ForwardingManager.getForwardingMessages() number of forwarding messages retrieved: "+returnVect.size());
    	return returnVect;
    }     
    
    /*
     * Returns the messages stored in the status messages
     */
    public Vector getStatusMessages() throws GooseException {
    	Vector returnVect = fwdb.getStatusMessages();
		return returnVect;
    }
    
    /*
     * Deletes a single message m
     * 
     */
    public void delete(GooseMessage m){
    	//this sleep will make that the database wont add a new element if the database was not updated
    	if(fwdb.isModified()){
    		sleepThread(100);
    	}
    	fwdb.delete(m);
    }
    
    /*
     * Deletes a message with a particular GUID
     */
    public void delete (String MessageGUID) throws GooseException{
    	try{
    		GooseMessage msg = fwdb.exclusiveSearch(MessageGUID, GUID);
    		fwdb.delete(msg);
    	}
    	catch(Exception e){
    		//log.debug("Message "+MessageGUID+" not found. Impossible to delete: "+e.getMessage());
    		
    	}
    }
    

    /*
     * Deletes individually a vector of messages that are stored in the DB
     * 
     */
    public void delete(Vector tmpMsg){
    	//this sleep will make that the database wont add a new element if the database was not updated
    	if(fwdb.isModified()){
    		sleepThread(100);
    	}
    	for (int i=0; i<tmpMsg.size(); i++){
    		GooseMessage msg = (GooseMessage)tmpMsg.elementAt(i);
    		fwdb.delete(msg);
    	}
    }
    
    /*
     * Deletes all the messages stored in the database (all the folders)
     */
    public void deleteAll(){
    	//this sleep will make that the database wont add a new element if the database was not updated
    	if(fwdb.isModified()){
    		sleepThread(1500);
    	}
    	Vector tmpMsgs = fwdb.getItems();
    	delete(tmpMsgs);
    }
    
    /*
     * Deletes all the messages included in the inbox folder (even if they are stored in another folder)
     */
    public void deleteInboxFolder(){
    	//this sleep will make that the database wont add a new element if the database was not updated
    	if(fwdb.isModified()){
    		sleepThread(100);
    	}
    	Vector tmpMsgs = getInboxMessages();
    	delete(tmpMsgs);
    }
    
    /*
     * Deletes all the messages included in the sent folder (even if they are stored in another folder)
     */
    public void deleteSentFolder(){
    	//this sleep will make that the database wont add a new element if the database was not updated
    	if(fwdb.isModified()){
    		sleepThread(100);
    	}
    	Vector tmpMsgs = getSentMessages();
    	delete(tmpMsgs);
    }

    /*
     * Deletes all the messages included in the forwarding folder
     */
    public void deleteForwardingFolder(){
    	//this sleep will make that the database wont add a new element if the database was not updated
    	if(fwdb.isModified()){
    		sleepThread(1500);
    	}
    	try {
    		Vector tmpMsgs = getForwardingMessages();
        	delete(tmpMsgs);
    	}catch(Exception e) {
    		//log.debug("deleteFwMFolder. error retrieving forwarding folder: "+e.getMessage());
    	}
    	
    }
    
    /*
     * Deletes the oldest message from an specified folder;
     */
    public GooseMessage deleteOldestMessage (String folder) throws GooseException{

    	GooseMessage oldest = null;
    	Vector tmpMsg = getMessagesFromFolder(folder);
    	
    	

    	//this sleep will make that the database wont add a new element if the database was not updated
    	if(fwdb.isModified()){
    		sleepThread(1500);
    	}

		if (tmpMsg.size()>0)
		{
			oldest = (GooseMessage)tmpMsg.elementAt(0);
	    	delete(oldest);
	    	return oldest;
		}
		else{
			throw new GooseException("Not message found, not deleted");
		}
    }
    
    private Vector getMessagesFromFolder(String folder){
    	if(folder.equalsIgnoreCase(GooseMessage.INBOX_FOLDER)
    			|| folder.equalsIgnoreCase(GooseMessage.INBOX_FORW_FOLDERS)
    			|| folder.equalsIgnoreCase(GooseMessage.INBOX_SENT_FOLDERS)
    			|| folder.equalsIgnoreCase(GooseMessage.ALL_FOLDERS))
    	{
    		return getInboxMessages();
    	}
    	else if(folder.equalsIgnoreCase(GooseMessage.SENT_FOLDER)
    			|| folder.equalsIgnoreCase(GooseMessage.INBOX_SENT_FOLDERS)
    			|| folder.equalsIgnoreCase(GooseMessage.SENT_FORWD_FOLDERS)
    			|| folder.equalsIgnoreCase(GooseMessage.ALL_FOLDERS))
    	{
    		return getSentMessages();
    	}
    	else
    	{
    		return getForwardingMessages();
    	}
    }
    
    /*
     * Returns the number of messages stored on a particular folder
     */
    public int getNumberOfMessagesInFolder (String folder){
    	Vector tmpMsg = getMessagesFromFolder(folder);
    	return tmpMsg.size();
    }
    
    /*
     * Method that iterates in all the address book and stores the contacts in a vector.
     */
    public int getNumberOfStoredItems(){
        return fwdb.getNumberOfStoredItems();
    }
    
    public int currentIndex(){
    	return fwdb.currentIndex();
    }
    
    /*
     * Creates an unique GUID for a new message (BTMAC+timestamp).
     */
    public String createGUID(){
        return String.valueOf(GooseTools.getTime())+Bluetooth.getLocalAddress();
    }
    

    
    public void sendNewMessage(Vector recipient, GooseMessage m,ResourcesStatus rs) throws GooseNetworkException, GooseException{
    	Vector nearbyDevices = rs.nearbyDevices;    	
    	NetworkManager ntMgrRef = null;
    	ContactsManager ctMgrRef = null;    	
    	
    	if (rs.nearbyDevices.size() == 0&& m.transmissionMode.equalsIgnoreCase(GooseMessage.STATUS_UPDATE)) {
    		m.folder=GooseMessage.STATUS_UPDATE_FOLDERS;
    		m.recTime=GooseTools.getTime();
    		addMessage(m);
    		//log.debug("FwMgr. Status update after addMessage");
    		return;
    	}
    	else if (rs.nearbyDevices.size()==0 && m.transmissionMode.equalsIgnoreCase(GooseMessage.UCAST)==false){
    		/*
    		 * If not nearbyDevices and no unicast, store it for future forwarding
    		 */
    		
    		//log.debug("No nearby devices found, message is going to be stored for future tx");
    		if (m.transmissionMode.equalsIgnoreCase(GooseMessage.MCAST)||m.transmissionMode.equalsIgnoreCase(GooseMessage.FRIEND_SEARCH)){
    			//If it is a personal message, increase the priority to increase the chances of being forwarded in the future.
    			m.priority=GooseMessage.HIGH;
//    			//log.debug("Message priority: "+m.priority);
    		}
    		m.folder=GooseMessage.SENT_FORWD_FOLDERS;
    		m.recTime=GooseTools.getTime();
    		addMessage(m);
    		//log.debug("Message cannot be sent");
    		return;
    	}
    	else if (rs.nearbyDevices.size()==0){
    		m.folder = GooseMessage.SENT_FORWD_FOLDERS;
    		m.recTime=GooseTools.getTime();
    		addMessage(m);
    		//log.debug("No nearby devices to send the message "+m.GUID+", message stored in "+m.folder);
    		return;    		
    	}
    	    	
    	//Get the contact and network manager from goose.
    	try{
    		ntMgrRef = (NetworkManager)goose.getManager(GooseManager.NETWORK_MANAGER);
    		if(ntMgrRef == null){
    			throw new GooseException("Error getting networkManager from GooseManager");
    		}
    		ctMgrRef = (ContactsManager)goose.getManager(GooseManager.CONTACTS_MANAGER);
    		if(ctMgrRef == null){
    			throw new GooseException("Error getting contactsManager from GooseManager");
    		}
    	}
    	catch(Exception e1){
			throw new GooseException("Error getting networkManager from GooseManager");
    	}

    	if (m.transmissionMode.equalsIgnoreCase(GooseMessage.CONTACTS_EXCHANGE)||
    			m.transmissionMode.equalsIgnoreCase(GooseMessage.CONTACTS_EXCHANGE_ACK)){
    		try{
    			directForwardingAlgorithm(recipient, rs.nearbyDevices, m, ntMgrRef, ctMgrRef);
    		}
    		catch(Exception e3){
    			//If it failed, store the message for future forwarding and increase its priority since it will be a unicast message
    			//log.debug("DirectForwarding Failed during contacts exchange. Probably host is not longer available: "+e3.getMessage());
    			//We do not do anything else with the message, just forget about it
    		}        		
    	}
    	else if (m.transmissionMode.equalsIgnoreCase(GooseMessage.CONTACT_PROFILE)||
    			m.transmissionMode.equalsIgnoreCase(GooseMessage.CONTACT_PROFILE_ACK)){
    		try{
    			directForwardingAlgorithm(recipient, rs.nearbyDevices, m, ntMgrRef, ctMgrRef);
    		}
    		catch(Exception e3){
    			//If it failed, store the message for future forwarding and increase its priority since it will be a unicast message
    			//log.debug("DirectForwarding Failed during profile exchange. Probably host is not longer available: "+e3.getMessage());
    			//We do not do anything else with the message, just forget about it
    		}        		
    	}
    	//UCAST MODE
    	else if (m.transmissionMode.equalsIgnoreCase(GooseMessage.UCAST)){		
    		//DirectForwardingAlgorithm  		
    		try{
    			m.previousRelays = goose.getLocalBluetoothAddress();
    			
//    			//log.debug("Calling DFA from sendNewMessage for a UCAST message");
    			directForwardingAlgorithm(recipient, rs.nearbyDevices, m, ntMgrRef, ctMgrRef);
    			//Add the message to the sent folder
    			m.folder = GooseMessage.SENT_FORWD_FOLDERS;
    			m.recTime=GooseTools.getTime();
	   			addMessage(m);
//    			//log.debug("Message stored in the sent folder");
    		}
    		catch(Exception e3){
    			//If it failed, store the message for future forwarding and increase its priority since it will be a unicast message
    			//log.debug("DirectForwarding Failed, try to send by epidemics: "+e3.getMessage());
    			m.priority=GooseMessage.VERY_HIGH;
    			m.folder = GooseMessage.SENT_FORWD_FOLDERS;
    			m.previousRelays = goose.getLocalBluetoothAddress();
    			
    			m.recTime=GooseTools.getTime();
    			addMessage(m);
    		}    			
    	}
    	/*
    	 * Now we try to send all the messages that were not sent by direct forwarding and that are not 
    	 * broadcast to all the nearby devices that are in our contact list in order to achive a high delivery ratio
    	 */
    	else if (m.transmissionMode.equalsIgnoreCase(GooseMessage.FRIEND_SEARCH)||
    			m.transmissionMode.equalsIgnoreCase(GooseMessage.FRIEND_SEARCH_ACK))
    	{

  			 m.previousRelays = goose.getLocalBluetoothAddress();
  			 
//    		//log.debug("Tx-mode is :"+m.transmissionMode+" when it is supposed to be "+GooseMessage.FRIEND_SEARCH+" or "+GooseMessage.FRIEND_SEARCH_ACK);
	    		try{
	    			//log.debug("SendNewMessage over MFA, Tx-mode "+m.transmissionMode+"; CONTENT: "+m.content);
	    			 multihopForwardingAlgorithm(recipient, nearbyDevices, m, ntMgrRef, ctMgrRef);
	    		}
	    		catch(Exception e){
	    			throw new GooseException ("Error forwarding "+m.transmissionMode+" with the multihop forwarding algorithm: "+e.getMessage());
	    		}  
    	}
    	else if (m.transmissionMode.equalsIgnoreCase(GooseMessage.BCAST)==false){
    		//check if any recipient is nearby in order to send by DFA
    		try{
//    			//log.debug("Tx-mode is :"+m.transmissionMode+" when it is supposed NOT to be "+GooseMessage.BCAST);
	    		
//    			//log.debug("It is going to send over MFA; through known devices");
    			m.previousRelays = goose.getLocalBluetoothAddress();
    			  
    			if (m.transmissionMode.equalsIgnoreCase(GooseMessage.STATUS_UPDATE)){
    				m.folder=GooseMessage.STATUS_UPDATE;
    				epidemicForwardingAlgorithm(nearbyDevices, m, ntMgrRef, ctMgrRef);
    			}
    			else{
    				m.folder=GooseMessage.SENT_FORWD_FOLDERS;
        			multihopForwardingAlgorithm(recipient, nearbyDevices, m, ntMgrRef, ctMgrRef);
    			}
    			m.recTime=GooseTools.getTime();
	   			addMessage(m);
    		}
    		catch(Exception e){
    			throw new GooseException ("Error forwarding "+m.transmissionMode+"with the multihop forwarding algorithm: "+e.getMessage());
    		}   	
    	}
    	//Broadcast messages and status update. Send to all of them
    	else{
//    		//log.debug("Error, "+m.transmissionMode+" when it is suppposed to be a Broadcast messages, it is going to be sent using EFA");
    		try{
    			m.previousRelays = goose.getLocalBluetoothAddress();
    			
	   			 epidemicForwardingAlgorithm(nearbyDevices, m, ntMgrRef, ctMgrRef);
	   			 m.folder=GooseMessage.FORWARD_FOLDER;
	   			 m.recTime=GooseTools.getTime();
	   			 addMessage(m);
	   			 //log.debug("Sending using EFA");
	   		}
	   		catch(Exception e){
	   			throw new GooseException ("Error forwarding with the epidemic forwarding algorithm: "+e.getMessage());
	   		}      		
    	}
    }    
    
    /*
     * Sends a message in a fully epidemic manner
     */
    private void epidemicForwardingAlgorithm (Vector nearbyDevices, GooseMessage m, NetworkManager ntMgrRef, ContactsManager ctMgrRef) throws GooseNetworkException{
    	Vector sent = new Vector();
    	//Add the previousRelays field. Added 8thAugust
    	/*String previousRelays = m.previousRelays;
    	
    	for (int i=0; i<nearbyDevices.size(); i++){
    		ServiceRecord tmpSr = (ServiceRecord)nearbyDevices.elementAt(i);
    		previousRelays = previousRelays+"|"+tmpSr.getHostDevice().getBluetoothAddress();
    	}
    	//log.debug("EFA: PreviousRelays Field: "+previousRelays);
    	m.previousRelays=previousRelays;
    	*/
    	for(int i=0; i<nearbyDevices.size(); i++){
    		ServiceRecord tmpSr = (ServiceRecord)nearbyDevices.elementAt(i);
			try{
				ntMgrRef.send(m, tmpSr.getConnectionURL(0, false), NetworkManager.BTID);
				sent.addElement(tmpSr);
				//We add the one we have sent the message to
				String previousRelays = m.previousRelays+"|"+tmpSr.getHostDevice().getBluetoothAddress();
				m.previousRelays=previousRelays;
			}
			catch(Exception e){
				//log.debug("Error sending data to: "+tmpSr.getHostDevice().getBluetoothAddress());
			}    		
    	}
		//Update the RCT and the rank with those next hops that succeeded
		for (int i= 0; i<sent.size(); i++){
			ServiceRecord tmpSr = (ServiceRecord)sent.elementAt(i);
			try{
                ctMgrRef.updateContactRank(tmpSr.getHostDevice().getBluetoothAddress(), ContactsManager.BROADCAST_INCREASE_RANK);
            }
            catch(Exception e3){
                //log.debug("Exception updating contact rank for :" +tmpSr.getHostDevice().getBluetoothAddress()+", "+e3.getMessage());
            }
			
			//Add a new entry to the RCT
			try{
				rct.put(m.GUID, tmpSr.getHostDevice().getBluetoothAddress());			
			}
			catch(Exception e2){
				//log.debug("Exception updating RCT for a DFA message: "+m.GUID+", exception: "+e2.getMessage());
			}
		}
    }
    
    /*
     * Sends a message in a epidemic manner but taking into account the next relays in order to implement a trust mechanism
     */
    private void multihopForwardingAlgorithm(Vector recipient, Vector nearbyDevices, GooseMessage m, NetworkManager ntMgrRef, ContactsManager ctMgrRef) throws GooseNetworkException{
    	//That is a Vector that stores the devices to whom we will send the information
    	//log.debug("MFA; Message tx mode: "+m.transmissionMode+" is going to be sent with the multihop forwarding alg");
    	Vector toSend = new Vector();
    	//check if any recipient is nearby in order to send by DFA
    	Vector sent = new Vector();
//    	//log.debug("Message tx mode: "+m.transmissionMode);
		try{
			if (m.transmissionMode.equalsIgnoreCase(GooseMessage.MCAST)){
//				//log.debug("MFA; Looking for multicast trusted nodes");				
				toSend = getNextMCASTTrustedRelays(recipient, nearbyDevices, ctMgrRef, m.transmissionMode);
			}
			if(m.transmissionMode.equalsIgnoreCase(GooseMessage.FRIEND_SEARCH)
					|| m.transmissionMode.equalsIgnoreCase(GooseMessage.FRIEND_SEARCH_ACK)){
//				//log.debug("MFA; Looking for search friends message trusted nodes");
				m.folder=GooseMessage.FORWARD_FOLDER;
				toSend = getNextSEARCHTrustedRelays(nearbyDevices, ctMgrRef);
			}
			//log.debug("MFA, Potential relays found: "+toSend.size());
		}
		catch(Exception e){
			throw new GooseNetworkException ("MFA: Error getting next hop devices, none trusted nodes was retrieved");
		}

//		//log.debug("Previous relays field updated "+m.previousRelays);
		for (int i=0; i<toSend.size(); i++){
			if (i>=10){
				break;
			}
			ServiceRecord tmpSr = (ServiceRecord)toSend.elementAt(i);
			try{
//				//log.debug("Message is going to be sent to: "+tmpSr.getHostDevice().getBluetoothAddress());
				ntMgrRef.send(m, tmpSr.getConnectionURL(0, false), NetworkManager.BTID);
				sent.addElement(tmpSr);			
				//log.debug("Message sent to: "+tmpSr.getHostDevice().getBluetoothAddress());
				
				//Update Previous Relay field
				String previousRelays = m.previousRelays+"|"+tmpSr.getHostDevice().getBluetoothAddress();
				
				m.previousRelays=previousRelays;
			}
			catch(Exception e){
				//log.debug("MFA: Error sending data to: "+tmpSr.getHostDevice().getBluetoothAddress());
			}
		}
//		//log.debug("Ready to update the RCT for: "+m.GUID);
		//Update the RCT and the rank with those next hops that succeeded
		for (int i= 0; i<sent.size(); i++){
//			//log.debug("MFA; Updating RCT");
			ServiceRecord tmpSr = (ServiceRecord)sent.elementAt(i);
			ctMgrRef.updateContactRank(tmpSr.getHostDevice().getBluetoothAddress(), ContactsManager.MULTICAST_INCREASE_RANK);
			

			//Add a new entry to the RCT
			try{
				rct.put(m.GUID, tmpSr.getHostDevice().getBluetoothAddress());			
			}
			catch(Exception e2){
				//log.debug("Exception updating RCT for a DFA message: "+m.GUID+", exception: "+e2.getMessage());
			}
		}
    }
    
    private Vector getNextSEARCHTrustedRelays(Vector nearbyDevices, ContactsManager ctMgrRef) {
    	//Vector that stores the contacts that we have nearby in order to support security. It is a vector of ServiceRecords and they are already sorted by their rank.
    	//Get the potential contacts that are nearby
//    	//log.debug("inside nextrustedrelays for search friends messages");
    	try{
    		//Search for the contacts that are nearby
    		return ctMgrRef.searchNearbyKnownServiceRecord(nearbyDevices);
    	}
    	catch(Exception e){
    		//log.debug("getNextSEARCHTrustedRelays. Error retrieving new contact: "+e.getMessage());
    		return new Vector();
    	}  	
	}

	/*
     * Sends a message in a epidemic manner but taking into account the next relays in order to implement a trust mechanism
     */
    private void directForwardingAlgorithm(Vector recipient, Vector nearbyDevices, GooseMessage m, NetworkManager ntMgrRef, ContactsManager ctMgrRef) throws GooseNetworkException{
    	Contact tmpCt = null;
    	//log.debug("DFA: Message "+m.GUID+" is going to be sent over DFA");
    	try{
    		//UCAST, so only a contact
			tmpCt = (Contact)recipient.elementAt(0);
//			//log.debug("DFA: Recipient: "+m.recipient);
//			//log.debug("DFA. Nearby devices size: "+nearbyDevices.size());
    		//Try to send over Bluetooth if the recipient is in the contact list.
			//Check if any of the nearby service records contains the recipient
			for (int i=0; i<nearbyDevices.size(); i++){
				
				ServiceRecord srTmp = (ServiceRecord)nearbyDevices.elementAt(i);
				if (tmpCt.getBluetoothAddress().equalsIgnoreCase(srTmp.getHostDevice().getBluetoothAddress())){
					//If nearby device is there, send over bluetooth in a single hope forwarding
//					//log.debug("Recipient was found in the nearby devices. Conn URL "+srTmp.getConnectionURL(0, false));
					ntMgrRef.send(m, srTmp.getConnectionURL(0, false), NetworkManager.BTID);
					ctMgrRef.updateContactRank(tmpCt.getBluetoothAddress(), ContactsManager.UNICAST_INCREASE_RANK);			
					//log.debug("Message sent with DFA");
					return; //Sent over Bluetooth
				}
			}
			/*
			 *NOTE. This is the code to enable tx data over SMS. Not included now to do not spend money stupidly
			
			if (m.priority>=3 && 
					m.contentType.equalsIgnoreCase(GooseMessage.TEXT) &&
					m.content.length()<=280&&
					tmpCt.getPhoneNumber().equalsIgnoreCase("")==false){
				//log.debug("Message is going to be sent over SMS");
				//If all the conditions to be sent over SMS are true, send over SMS
				ntMgrRef.send(m, tmpCt.getPhoneNumber(), NetworkManager.SMSID);
				ctMgrRef.updateContactRank(tmpCt.getBluetoothAddress(), ContactsManager.UNICAST_INCREASE_RANK);
				//log.debug("Message sent over SMS");				
				return;
			}*/
			
			//Add a new entry to the RCT
			try{
		    	rct.put(m.GUID, tmpCt.getBluetoothAddress());				
			}
			catch(Exception e2){
				//log.debug("Exception updating RCT for a DFA message: "+m.GUID+", exception: "+e2.getMessage());
			}
		}
    	catch(Exception e){
    		//log.debug("Error attemping to forward the message in a UCAST way with a single hop tx to "+tmpCt.getBluetoothAddress()+": "+e.getMessage());
    		throw new GooseNetworkException("DFA exception. "+e.getMessage());
    	}  
    }
    
    /*
     * Returns the list of trusted relays (i.e. nearby contacts that were discovered by bluetooth plus some random unknown devices)
     */
    private Vector getNextMCASTTrustedRelays(Vector recipient, Vector nearbyServiceRecordVector, ContactsManager ctMgrRef, String txMode) throws GooseException{
    	//Vector that stores the contacts that we have nearby in order to support security. It is a vector of ServiceRecords and they are already sorted by their rank.
    	//Get the potential contacts that are nearby
//    	//log.debug("inside nextrustedrelays");
    	Vector nearbyContacts = null;
    	try{
    		//Search for the contacts that are nearby
    		nearbyContacts = ctMgrRef.searchNearbyContacts(nearbyServiceRecordVector); 
//    		//log.debug("nextTrustedRelays. Number of nearbycontacts: "+nearbyContacts.size());
//    		//log.debug("Check if they are repeated");
    		for (int i=0; i<nearbyContacts.size(); i++){
    			Contact tmp = (Contact)nearbyContacts.elementAt(i);
    			//log.debug(tmp.getName()+"|"+tmp.getSurname()+"|"+tmp.getBluetoothAddress());
    		}
    	}
    	catch(Exception e2){
    		throw new GooseException ("Error retrieving the nearby contacts: "+e2.getMessage());
    	}

    	//Return object
    	Vector nextRelays = new Vector();
    	//First, add those devices that are recipients or in the contact list
    	for (int i=0; i<nearbyServiceRecordVector.size(); i++){
    		//First we check in any of the nearby devices is a recipient
    		ServiceRecord tmpSr = (ServiceRecord)nearbyServiceRecordVector.elementAt(i);
    		boolean added = false;
    		for (int j=0; j<recipient.size(); j++){
    			Contact tmpCt = (Contact)recipient.elementAt(j);
    			if (tmpSr.getHostDevice().getBluetoothAddress().equalsIgnoreCase(tmpCt.getBluetoothAddress()))
    			{
//    				//log.debug("Recipient found in the list of nearby devices: "+tmpSr.getConnectionURL(0, false));
    				//NearbyDevice is a recipient
    				nextRelays.addElement(tmpSr);
    				added = true;
    				//Remove this element from the list in order to do not look for it later
    				nearbyServiceRecordVector.removeElementAt(i);
    			}    			
    		}
    		//If the device wasn't added, look for it in the contacts list
    		if(added == false){
    			for (int h=0; h<nearbyContacts.size(); h++){
    				ServiceRecord tmpCtSr = (ServiceRecord)nearbyContacts.elementAt(h);
    				if (tmpCtSr.equals(tmpSr)){
//    					//log.debug("A new Service Record was added from the contactslist"+tmpCtSr.getConnectionURL(0, false));
    					nextRelays.addElement(tmpSr);
        				//Remove this element from the list in order to do not look for it later
        				nearbyServiceRecordVector.removeElementAt(i);
    				}
    			}
    		}    		
    	}
//    	//log.debug("Number of devices to send the message before including the random ones: "+nextRelays.size());
    	//If it is a search friend message, we do not initially rely on unknown nearby devices
    	if(txMode.equalsIgnoreCase(GooseMessage.MCAST))
    	{
    		for (int j=0; j<nearbyServiceRecordVector.size(); j++){
    			if (nextRelays.size()>5){
    				break;
    			}
	    		ServiceRecord tmpSr = (ServiceRecord)nearbyServiceRecordVector.elementAt(j);
//	    		//log.debug("Adding a new random nearby device: "+tmpSr.getHostDevice().getBluetoothAddress());
	    		nextRelays.addElement(tmpSr);
    		}
    	}
    	//log.debug("Total number of trusted devices to send the message: "+nextRelays.size());
    	return nextRelays;
    }
    /*
     * Class that stores a new event in the ForwardingManager in order to be processed
     */
	public synchronized void storeNewEvent(IncomingMessageEvent event){
		if(eventStack == null){
			eventStack = new Stack();
		}
		eventStack.push(event);
	}
	
	public synchronized void setMessageAsRead(String UUID)throws GooseException{
		fwdb.setMessageAsRead(UUID);
	}
	
	/*
	 * Class that gets the last message received
	 */
	public synchronized IncomingMessageEvent getLastEvent() throws GooseException{
		if(eventStack != null){
			IncomingMessageEvent event = (IncomingMessageEvent) eventStack.pop();
			return event;
		}
		else{
			throw new GooseException("No more events available on the stack");
		}
	}
}
