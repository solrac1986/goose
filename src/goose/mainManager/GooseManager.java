/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package goose.mainManager;

import org.garret.perst.StorageFactory;
import org.garret.perst.Storage;
import goose.contactsManager.ContactsManager;
import goose.forwardingManager.ForwardingManager;
import goose.forwardingManager.GooseMessage;
import goose.forwardingManager.MessageTools;
import goose.networkManager.NetworkManager;
import goose.mainManager.IManager;
import goose.contactsManager.Contact;
import goose.exceptions.GooseException;
import goose.exceptions.GooseNetworkException;
import goose.exceptions.GooseSearchException;
import java.util.Vector;
import javax.bluetooth.ServiceRecord;


/**
 *
 * @author Narseo
 */
public class GooseManager implements IGooseAPI {


    /*
     * Size of the pool in bytes. Page pool should containt at least 4kb pages. So minimal page pool size should be at least 40kb.
     * Larger pool usually leads to better performance (unless it could not fit in memorz and cause swapping).
     * It may be necessary to research a little bit more this parameter.
     */
    //private static final int PAGE_POOL_SIZE = Storage.INFINITE_PAGE_POOL; //The page is extended on demand
    private static final int PAGE_POOL_SIZE = 256*1024;
    private static final String DATABASE_NAME = "Goose1.dbs";
//    private static final int TIME_LIMIT = 2*1000; //2 seconds
//    private static final String LANGUAGE = "en";
    
    public static String firstname;
    public static String surname;
    public static String phoneNumber;

    public static EventStack eventStack = new EventStack();


    public static Storage db = null;
    public Root root = null;
    private String cipherkey = null;

    /*
     * Managers reference
     */
    private NetworkManager ntMgr = null;
    private ForwardingManager fwMgr = null;
    private ContactsManager ctMgr = null;
    
    public static final int CONTACTS_MANAGER = 0;
    public static final int FORWARDING_MANAGER = 1;
    public static final int NETWORK_MANAGER = 2;
    
    public static final int NORM_PRIORITY=3;
    /*
     * Main Goose manager. It initializes/stops other managers and is also responsible for managing Perst DataBase
     */
    public GooseManager(){
	    try{
			initialiseDatabase();
	    }
	    catch(Exception e){
//	    	//log.debug("Error initialising goose: "+e.getMessage());
	    }
    }

    
    public void startManager() throws GooseException{
    	ntMgr = new NetworkManager(this);
	    ctMgr = new ContactsManager(db);
	    fwMgr = new ForwardingManager(db, this);
    	startManagers();
    	try{
//    		//log.debug("PERFORMING GC");
    		System.gc();
    	}
    	catch(Exception e){
//    		//log.debug(e.getMessage());
    	}
    }
    
    
    public void stopManager() throws GooseException{
    	try{
    		//Stop the other managers
	    	stopManagers();
	    	//Close DataBase
	    	closeDB();
    	}
    	catch(Exception e){
    		throw new GooseException ("Error closing GooseManager and DataBase: "+e.getMessage());
    	}
    }
    
    public void pauseManager() throws GooseException{
        try{
        	ntMgr.pauseListener(NetworkManager.ALL);
        	fwMgr.pauseManager();
        	ctMgr.pauseManager();
        }
        catch (Exception e){
            //Handle exception
//        	//log.debug("ERROR pausing GOOSEMANAGER thread");
        	throw new GooseException("Error pausing GooseManager: "+e.getMessage());
        }
    }
     
    public void restartManager() throws GooseException{
        try{
        	ntMgr.restartListener(NetworkManager.ALL);
        	fwMgr.restartManager();
        	ctMgr.restartManager();
           
        }
        catch (Exception e){
        	
           //Handle exception
//           //log.debug("ERROR restarting thread");
           throw new GooseException ("Error restarting GooseManager: "+e.getMessage());
        }
    }
    
    
    /*
     * Method that must be used to initialize the DB.
     *
     */
    private void initialiseDatabase() throws GooseException{
    	try{
            //Get Instance of Perst storage
            db = StorageFactory.getInstance().createStorage();
            //Use UTF-8 Encoding for string encoding
            db.setProperty("perst.string.encoding", "UTF-8");
            //Open DB as a simple Database
            db.open(DATABASE_NAME, PAGE_POOL_SIZE);

            root = (Root) db.getRoot();
            if (root == null){
                //If the object was not specified, then storage is not yet initialized and it is necessary to register a new Root object
                root = new Root(db);
                //Register new root object
                db.setRoot(root);
//                //log.debug("New root object created");
            }
            ctMgr= new ContactsManager(db);
            ntMgr = new NetworkManager(this);
            fwMgr = new ForwardingManager(db, this); 
//            //log.debug("Data Base initialised");
            }
    	catch(Exception e){
    		throw new GooseException("Error initialising DB: "+e.getMessage());
    	}
    }

    /*
     * Starts all the Managers (if required)
     */
    private void startManagers() throws GooseException{
    	ctMgr.startManager();
    	fwMgr.startManager();
    	ntMgr.startManager();
//        //log.debug("Managers started");
    }

    /*
     * Stops all the managers (if required);
     */
    private void stopManagers(){
    	try{
	        ntMgr.stopManager();
	        ctMgr.stopManager();
	        fwMgr.stopManager();
	        ntMgr = null;
	        ctMgr = null;
	        fwMgr = null;
//	        //log.debug("Manager stopped");
    	}
    	catch (Exception e){
//    		//log.debug("Exception closing managers: "+e.getMessage());
    	}
        
    }

    public Root getDBRoot() throws GooseException{
    	try{
	        if (root != null){
	            return root;
	        }
	        else{
	            throw new GooseException ("DB root element not found");
	        }
    	}
    	catch(Exception e){
    		throw new GooseException("Error retrieving DB Root: "+e.getMessage());
    	}
    }


    /*
     * Returns the battery level of the device. Some midlets may not allow the developer to have
     * access to this information unles the midlet is trusted.
     * 
     * I think the battery level returned is in the range 0-3. 0 min and 3 full battery
     * 
     */
    private String getBatteryLevel() throws GooseException {
        String ret = System.getProperty("batterylevel");
        if (ret == null){
            throw new GooseException ("Battery Level cannot be retrieved");
        }
        return ret;
    }

    /*
     * This method provides an approximation of the total amount of memory
     * available for future allocated objects. It is necessary to call
     * the GarbageCollector before in order to have a good estimation.
     *
     * It is necessary to note that it is not possible to know the total
     * memory available on a device.
     *
     */
    private float availableRuntimeMemory() throws GooseException{
        //By calling the garbage collector we are forcing the JVM to perform
        //a time consuming task. It is necessary to do not execute the available runtime memory
        //method very often.
        Runtime.getRuntime().gc();

        long freeMemory = Runtime.getRuntime().freeMemory(); //Bytes
        long totalMemory = Runtime.getRuntime().totalMemory();

        if (totalMemory == 0){
            throw new GooseException("Error retrieving available memory");
        }

        return freeMemory/totalMemory;
    }


    //This is the cipherkey to encode the data stored in the DB.
    public void setCipherkey (String cipherkey){
        this.cipherkey = cipherkey;
    }


    
    //GooseAPI methods:    
	public void setUserDetails(String firstname, String surname, String phoneNumber) throws GooseException{
		this.firstname=firstname;
		this.surname = surname;
		
        //J2ME does not support regular expressions. So we need to do it on our own.
        if (phoneNumber.startsWith("+")){
            for (int i=1; i<phoneNumber.length(); i++){
                if (phoneNumber.charAt(i) < '0' || phoneNumber.charAt(i)>'9'){
                	this.phoneNumber = "";
                    throw new GooseException("Phone number not valid");
                }
            }
            this.phoneNumber=phoneNumber;
        }
        else{
            this.phoneNumber = "";
            throw new GooseException("Phone number not valid");
        }
	}
	
	/*
	 * gets the resources status available in the device. It includes nearby devices, battery level and available memory
	 * 
	 * @return Current ResourcesStatus
	 */
	public ResourcesStatus getResourcesStatus(){
		ResourcesStatus rs = new ResourcesStatus();
//    	Vector nearbyDevices = new Vector();
//    	String batteryLevel = null;
//    	float availableRuntimeMemory;
    	/*
    	try{
    		//Do whatever has to be done with the battery
    		batteryLevel = getBatteryLevel();
    		//log.debug("Battery Level: "+batteryLevel);
    		availableRuntimeMemory = availableRuntimeMemory();

    		//GET THE RESOURCES
//    		rs.batteryLevel=batteryLevel;
//    		rs.availableRuntimeMemory = availableRuntimeMemory;
    	}
    	catch(Exception e1){
    		//log.debug("Exception retrieving the resources: "+e1.getMessage());
    		rs.batteryLevel = null;
    		rs.memoryAvailable = 0;
    	}
    	*/
    	//Get and store the resources
    	try{
    		//We are storing ServiceRecord
    		rs.nearbyDevices = ntMgr.discover();
    	}
    	catch(Exception e){
    		rs.nearbyDevices = null;
    	}
    	
    	return rs;
	}
	
    /*
     * Method that communicates to Goose Manager that a new message was created. In this case, it will be managed
     * by GooseManager since it is necessary to manage the available resources.
     *    
     */
    public void send(Vector recipientList, GooseMessage m) throws GooseNetworkException{
    	//Get information about the available resources
    	ResourcesStatus rs = getResourcesStatus();
    	try{
    		fwMgr.sendNewMessage(recipientList, m,rs);
    	}
    	catch(Exception e){
    		//TODO. Do something about it
    		throw new GooseNetworkException("Error sending the message: "+e.getMessage());
    	}
    }
    
    public GooseMessage createNewMessage(Vector recipientList, String txMode, int priority, String contentType, String content) throws GooseException{    	
    	//Get message parameters
    	long TTL = MessageTools.getTTL(txMode);
    	String recipientsString = MessageTools.writeRecipientsListFromContactsVector(recipientList);
//    	//log.debug("Recipient List: "+recipientsString);
    	  	
    	//Create message with the information provided by the UI and send it to the forwarding manager
    	String [] messageData = {
    			String.valueOf(ctMgr.getNumberOfContacts()),
    			this.firstname,
    			this.surname,
    			this.phoneNumber,
    			ntMgr.getLocalAddress(),
    			recipientsString,
    			txMode, 
    			String.valueOf(TTL),
    			String.valueOf(priority), 
    			fwMgr.createGUID(),
    			ntMgr.getLocalAddress(), //The previous relay for a new message is the current device
    			contentType,
    			content, 
    			""//Folder, initially empty
    	};
    	//Create the message with all this information
    	return new GooseMessage(db, messageData, false, -1);
    }

    /*
     * Creates a new contact and stores it in the DB
     */
    public Contact createContact (String [] dataInput) throws GooseException{
    	if(dataInput.length==4||dataInput.length==3){
	    	try{
		    	int userId = ctMgr.currentIndex();
		    	Contact ct = new Contact(db, userId, dataInput);
		    	ctMgr.addContact(ct);
		    	return ct;
	    	}
	    	catch(Exception e){
	    		throw new GooseException ("Error when saving new Contact in GooseManager.createContact(): "+e.getMessage());
	    	}
    	}
    	else if (dataInput.length==5){
	    	try{
		    	int userId = ctMgr.currentIndex();
		    	String [] newValues = {dataInput[0], dataInput[1], dataInput[2], dataInput[3]};		    	
		    	Contact ct = new Contact(db, userId, newValues);
		    	ctMgr.addContact(ct);
		    	return ct;
	    	}
	    	catch(Exception e){
	    		throw new GooseException ("Error when saving new Contact in GooseManager.createContact(): "+e.getMessage());
	    	}   		
    	}
    	else{
    		throw new GooseException ("Invalid data Input when creating a new Contact");
    	}
    }
   
    /*
     * Get number of contacts stored
     */
    public int getNumberOfContacts(){
    	return ctMgr.getNumberOfContacts();
    }

    /*
     * Method that asks Gpose Manager that a new search contact action must be performed
     * (non-Javadoc)
     * @see goose.mainManager.IGooseAPI#searchContact(java.lang.String, int)
     */
    public Vector searchContact(String searchParameterValue, int searchPattern) throws GooseSearchException{
    	try{
    		return ctMgr.exclusiveSearch(searchParameterValue, searchPattern);
    	}
    	catch(Exception e){
    		e.printStackTrace();
    		throw new GooseSearchException("Error searching for contact: "+e.getMessage());
    	}
    }
    
    /*
     * Method that searchs a contact by multiple parameters
     */
    public Vector searchContact(String [] searchParameterValue, int [] searchPattern) throws GooseSearchException{
    	try{
    		return ctMgr.exclusiveSearch(searchParameterValue, searchPattern);
    	}
    	catch(Exception e){
    		e.printStackTrace();
    		throw new GooseSearchException("Error searching for contact: "+e.getMessage());
    	}
    }
    

    //Looks in the contact list which btAddress belong to contacts in the address book.
    //It will store the contacts in the temp. Cache
    public Vector searchNearbyContacts (Vector serviceRecord) throws GooseSearchException {
    	try{
    		return ctMgr.searchNearbyContacts(serviceRecord);
    	}
    	catch(Exception e){
    		e.printStackTrace();
    		throw new GooseSearchException("Error searching for nearby contacts: "+e.getMessage());
    	}
    }

    //Returns all the contacts to the user
    public Vector getContacts() throws GooseSearchException{
    	try{
    		return ctMgr.getContacts();
    	}
    	catch(Exception e){
    		e.printStackTrace();
    		throw new GooseSearchException("Error searching for nearby contacts: "+e.getMessage());
    	}    	
    }
    
    public static Storage getDatabase(){
    	return db;
    }
    
    public Vector getTopContacts(int number){
    	return ctMgr.getTopContacts(number);
    }

    //Edits a contact from the address book
    public void editContact (Contact contact, String [] newParameters) throws GooseSearchException{
    	try{
    		ctMgr.editContact(contact, newParameters);
    	}
    	catch(Exception e){
    		e.printStackTrace();
    		throw new GooseSearchException("Error searching for nearby contacts: "+e.getMessage());    		
    	}
    }

    //Deletes a contact
    public void deleteContact (Contact contact){
    	try{
    		ctMgr.deleteContact(contact);
    	}
    	catch(Exception e){
    		e.printStackTrace();
//    		//log.debug("Error deleting contact: "+e.getMessage());
    	}
    }
    
    public Vector searchMessage(String searchParameterValue, int searchParameter) throws GooseSearchException{
    	try{
    		return fwMgr.searchMessage(searchParameterValue, searchParameter);
    	}
    	catch(Exception e){
    		e.printStackTrace();
    		throw new GooseSearchException("Message not found: "+e.getMessage());
    	}
    }

    public Vector getInboxMessages() throws GooseSearchException{
    	try{
    		return fwMgr.getSortedInboxMessages();
    	}
    	catch(Exception e){
    		throw new GooseSearchException("Inbox couldn't be read: "+e.getMessage());
    	}
    }

    public Vector getSentMessages() throws GooseSearchException{
    	try{
    		return fwMgr.getSortedSentMessages();
    	}
    	catch(Exception e){
    		throw new GooseSearchException("SentMessages Folder couldn't be read: "+e.getMessage());
    	}
    }

    /*
     * Deletes a GooseMessage from Perst Database
     * (non-Javadoc)
     * @see goose.mainManager.IGooseAPI#deleteMessage(goose.forwardingManager.GooseMessage)
     */
    public void deleteMessage(GooseMessage m) throws GooseSearchException{
    	try{
    		fwMgr.delete(m);
    	}
    	catch(Exception e){
    		throw new GooseSearchException("Error deleting message: "+m.GUID+", "+e.getMessage());
    	}
    }

    public void exchangeContactDetails(ServiceRecord sr) throws GooseNetworkException{
    	fwMgr.currentIndex();
    	String [] dataInput = {
    			String.valueOf(fwMgr.currentIndex()),
    			this.firstname,
    			this.surname,
    			this.phoneNumber,
    			ntMgr.getLocalAddress(),
    			sr.getHostDevice().getBluetoothAddress(),
    			GooseMessage.CONTACTS_EXCHANGE,
    			String.valueOf(GooseMessage.DEFAULT_UCAST_TTL),
    			String.valueOf(GooseMessage.HIGH),
    			fwMgr.createGUID(),
    			sr.getHostDevice().getBluetoothAddress(),
    			GooseMessage.CONTACTS,
    			MessageTools.writeContactMessage(this.firstname, this.surname, this.phoneNumber, ntMgr.getLocalAddress(), ntMgr.getConnectionURL()),
    			""
    	};
    	GooseMessage m = new GooseMessage(db, dataInput, false, -1);
    	ntMgr.send(m, sr.getConnectionURL(0, false), NetworkManager.BTID);  	
    }
    
    
    
    public void sendContactsACK(String connectionURL, String remoteBTAddr) throws GooseNetworkException{

    	String [] dataInput = {
    			String.valueOf(fwMgr.currentIndex()),
    			this.firstname,
    			this.surname,
    			this.phoneNumber,
    			ntMgr.getLocalAddress(),
    			remoteBTAddr,
    			GooseMessage.CONTACTS_EXCHANGE_ACK,
    			String.valueOf(GooseMessage.DEFAULT_UCAST_TTL),
    			String.valueOf(GooseMessage.HIGH),
    			fwMgr.createGUID(),
    			ntMgr.getLocalAddress(),
    			GooseMessage.CONTACTS,
    			MessageTools.writeContactMessage(this.firstname, this.surname, this.phoneNumber, ntMgr.getLocalAddress(), ntMgr.getConnectionURL()),
    			""
    	};    	
        
        
    	GooseMessage m = new GooseMessage(db, dataInput, false, -1);
    	ntMgr.send(m, connectionURL, NetworkManager.BTID);    	
    }
    
    

    public void sendRequestPersonalProfile(ServiceRecord sr) throws GooseNetworkException{
    	fwMgr.currentIndex();
    	String [] dataInput = {
    			String.valueOf(fwMgr.currentIndex()),
    			this.firstname,
    			this.surname,
    			this.phoneNumber,
    			ntMgr.getLocalAddress(),
    			sr.getHostDevice().getBluetoothAddress(),
    			GooseMessage.CONTACT_PROFILE,
    			String.valueOf(GooseMessage.DEFAULT_UCAST_TTL),
    			String.valueOf(GooseMessage.HIGH),
    			fwMgr.createGUID(),
    			ntMgr.getLocalAddress(),
    			GooseMessage.CONTACTS,
    			MessageTools.writeContactMessage(this.firstname, this.surname, this.phoneNumber, ntMgr.getLocalAddress(), ntMgr.getConnectionURL()),
    			""
    	};    
    	GooseMessage m = new GooseMessage(db, dataInput, false, -1);
    	ntMgr.send(m, sr.getConnectionURL(0, false), NetworkManager.BTID);  
    }

    public void sendPersonalProfileACK(String connectionURL, String remoteBTAddr, String dataString) throws GooseNetworkException{
    	
    	String [] dataInput = {
    			String.valueOf(fwMgr.currentIndex()),
    			this.firstname,
    			this.surname,
    			this.phoneNumber,
    			ntMgr.getLocalAddress(),
    			remoteBTAddr,
    			GooseMessage.CONTACT_PROFILE_ACK,
    			String.valueOf(GooseMessage.DEFAULT_UCAST_TTL),
    			String.valueOf(GooseMessage.HIGH),
    			fwMgr.createGUID(),
    			ntMgr.getLocalAddress(),
    			GooseMessage.CONTACTS,
    			MessageTools.writeProfileMessage(dataString),
    			""
    	};
    	GooseMessage m = new GooseMessage(db, dataInput, false, -1);
    	ntMgr.send(m, connectionURL, NetworkManager.BTID);
    	
    }


    

    /*
     * (non-Javadoc)
     * @see goose.mainManager.IGooseAPI#getNearbyDevices()
     */
    public Vector getNearbyDevices(){
    	//If the last bluetooth discovery was more than a min ago, perform a discovery
    	if (GooseTools.getTime()-ntMgr.getDiscoveryTimeStamp()>60000){
    		return ntMgr.discover();
    	}
    	else{
    		return ntMgr.getCachedDevices();
    	}    	
    }

    public Vector getLastContactsUsed(int number) {
        return ctMgr.getLastContactsUsed(number);
    }
    
    /*
     * Returns a GooseManager identified by managerId. Throws a GooseException in case that the parameter does not match with any ManagerID.
     * 
     * @param managerId Identifies the manager to return
     */
    public IManager getManager(int managerId) throws GooseException{
    	switch(managerId){
    		case CONTACTS_MANAGER:
    			return ctMgr;
    		case FORWARDING_MANAGER:
    			return fwMgr;
    		case NETWORK_MANAGER:
    			return ntMgr;
    		default:
    			throw new GooseException ("Cannot return manager");
    	}
    }
    
    /*
     * (non-Javadoc)
     * @see goose.mainManager.IGooseAPI#seekFriend(java.lang.String, java.lang.String, java.lang.String)
     */
    public void seekFriend(String name, String surname, String phoneNumber){
    	fwMgr.currentIndex();
//    	//log.debug("GONNA PERFORM SEEKFRIEND. local phonenumber: "+this.phoneNumber);
    	String [] dataInput = {
    			String.valueOf(fwMgr.currentIndex()),
    			this.firstname,
    			this.surname,
    			this.phoneNumber,
    			ntMgr.getLocalAddress(),
    			GooseMessage.BCAST,
    			GooseMessage.FRIEND_SEARCH,
    			String.valueOf(GooseMessage.DEFAULT_MCAST_TTL),
    			String.valueOf(GooseMessage.LOW),
    			fwMgr.createGUID(),
    			ntMgr.getLocalAddress(),
    			GooseMessage.SEARCH,
    			MessageTools.writeSearchContactMessage(name, surname, phoneNumber),
    			GooseMessage.FORWARD_FOLDER
    	};
        
    	GooseMessage m = new GooseMessage(db, dataInput, false, -1);
    	Vector recipients = new Vector();
    	try{
    		send(recipients, m);    	
    	}
    	catch(Exception e){
//    		//log.debug("Error sending Search Friend message: "+e.getMessage());
    	}
    }
    
    public String getLocalBluetoothAddress(){
    	return ntMgr.getLocalAddress();
    }
    

    public void setMessageAsRead(String UUID) throws GooseException{
    	fwMgr.setMessageAsRead(UUID);
    }
    
    /*
     * Closes the Database and all its instances
     */
    public void closeDB (){
        db.close();
        root=null;
        db=null;
//        //log.debug("GooseManager Main DB closed");
    }


	public void deleteAllContacts() {
		// TODO Auto-generated method stub
		ctMgr.deleteAllContacts();
		
	}


	public long getMessageTimeIn(String UUID) {
		return fwMgr.getMessageTimeIn(UUID);
	}

}
