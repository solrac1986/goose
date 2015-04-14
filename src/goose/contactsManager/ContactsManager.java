/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package goose.contactsManager;

import goose.contactsManager.Contact;
import goose.exceptions.GooseException;
import java.util.Vector;
import goose.mainManager.GooseTools;
import org.garret.perst.*;
import goose.mainManager.IManager;


/**
 *
 * @author Narseo
 */
public class ContactsManager extends Thread implements IManager{
    //We do not need the cache. Perst DB already stores in memory the last objects
    //private Vector contactsCache;
	
	private ContactsManagerDatabase ctMgrDb = null;
    private boolean quit;

    public static final int NAME = 0;
    public static final int SURNAME = 1;
    public static final int PHONENUMBER = 2;
    public static final int BTADDRESS = 3;
    public static final int RANK = 4;
    public static final int NAME_SURNAME = 5;
    public static final int TIME_STAMP = 6;
    
    public static final int DISCOVERY_INCREASE_RANK = 1;
    public static final int UNICAST_INCREASE_RANK = 10;
    public static final int MULTICAST_INCREASE_RANK = 5;
    public static final int BROADCAST_INCREASE_RANK = 3;
    public static final int RELAY_INCREASE_RANK = 3;
    public static final int TEMPORAL_DECREASE_6H_RANK = -1;
    public static final int TEMPORAL_DECREASE_MORE12H_RANK = -3;
    public static final int PREVIOUS_RELAYS_INCREASE_RANK = 2;
    
    public ContactsManager(Storage dbRef){
            quit = false;
            ctMgrDb = new ContactsManagerDatabase(dbRef);
    }


    public void startManager(){
        try{
            start();
//            //log.debug("ContactsManager Running");
        }
        catch (Exception e){
            //Handle exception
//        	//log.debug("Error starting ContactsManager");
        }   	
    }
    
    public void run(){
//    	//log.debug("Contacts Manager Run method");
        while (!quit){
            //Commit is the most time consuming opperation. Decide when to perform it or not
            if(ctMgrDb.isModified()){
            	ctMgrDb.commit();
//            	//log.debug("ctMgrDb commit. Contacts stored: "+getNumberOfContacts());
            }

            //Commit changes every 30 secs?
            try{
                long sleepTime = 150 + GooseTools.getPositiveRandomLong()%100;
            	Thread.sleep(sleepTime);
            }
            catch(Exception e){
                //TODO: Handle exception
//            	//log.debug("EXC in ContactsManager when Thread.sleep(): "+e.getMessage());
            }
        }

    }

    public void stopManager(){
        quit = true;
        ctMgrDb.closeDB();
//    	//log.debug("ContactManager thread stopped");
//        log=null;
    }

    public void pauseManager(){
        try{
        	this.wait();
        	////log.debug("ContactManager thread paused");
        }
        catch (Exception e){
            //Handle exception

//        	//log.debug("thread.wait() Error setting to sleep ContactManager thread. Pause method");
        }
    }

    public void restartManager(){
        try{
            this.notify();
//            //log.debug("");
        }
        catch (Exception e){
            //Handle exception
//        	//log.debug("thread.notify(), Error resuming ContactsManager thread");
        }
    }

    private void sleepThread(long msecs){
    	try{
    		Thread.sleep(msecs);
    	}
    	catch(Exception e){
//    		//log.debug("Error sleeping thread");
    	}    	
    }
    
    /*
     * Adds a new contact to the DB
     * 
     */
    public void addContact (Contact contact){
//    	this sleep will make that the database wont add a new element if the database was not updated
    	if(ctMgrDb.isModified()){
    		sleepThread(100);
    	}
//    	//log.debug("Adding new contact: "+contact.getName());
    	ctMgrDb.add(contact);
    }

    /*
     * Searchs for a set of contacts that match a particular criterion
     * 
     */
    public Vector searchContact (String searchParameterValue, int searchPattern) throws GooseException{
		return ctMgrDb.search(searchParameterValue, searchPattern);	
    }

    /*
     * Searchs for a set of contacts that match a particular criterion
     * 
     */
    public Vector exclusiveSearch (String searchParameterValue, int searchPattern) throws GooseException{
    	return ctMgrDb.exclusiveSearch(searchParameterValue, searchPattern);
    }

    public Vector exclusiveSearch (String [] searchParameterValue, int [] searchPattern) throws GooseException{
    	return ctMgrDb.exclusiveSearch(searchParameterValue, searchPattern);
    }
    
    /*
     * Edits a contact from the AddressBook. The String Array only includes the fields
     * - name
     * - surname
     * - phoneNumber
     *
     */
    public void editContact (Contact contact, String [] newValues){
    	//this sleep will make that the database wont add a new element if the database was not updated
    	if(ctMgrDb.isModified()){
    		sleepThread(100);
    	}
    	ctMgrDb.modify(contact, newValues);
    }
    
    /*
     * Deletes all the contacts stored by the contact Manager. It is necessary to iterate each one of them and then delete them
     */
    public void deleteAllContacts(){
    	Vector v = getContacts();
    	for(int i=0; i<v.size(); i++){
    		deleteContact((Contact)v.elementAt(i));
    	}    	
    	ctMgrDb.commit();
    }
    
    /* 
     * Deletes a contact. It removes every entry from all the index that this contact will be
     *
     */
    public void deleteContact (Contact contact){
    	//this sleep will make that the database wont add a new element if the database was not updated
    	if(ctMgrDb.isModified()){
    		sleepThread(100);
    	}
    	ctMgrDb.deleteItem(contact);
    }

    /*
     * Method that iterates in all the address book and stores the contacts in a vector.
     */
    public int getNumberOfContacts(){
        return ctMgrDb.getNumberOfStoredItems();
    }

    /*
     * Method that returns a whole list of contacts by their ID
     */
    public Vector getContacts(){
    	try{
    		return ctMgrDb.getContactsSortedByName();
    	}
    	catch(Exception e){
//    		//log.debug(e.getMessage());
    		return new Vector();
    	}
    }
    
    /*
     * Returns the top "number" contacts by their rank. It is a way of knowing the most important nodes on someone´s social network
     * 
     * @param number Number of maximum contacts to be retrieved
     */
    public Vector getTopContacts(int number){
    	return ctMgrDb.getTopContacts(number);
    }
    /*
     * Returns the index of the element to be stored
     */
    public int currentIndex(){
    	return ctMgrDb.currentIndex();
    }

    /*
     * Returns the rank for a particular user. It supports different search criteria
     *
     */
    public int getUserRank (String searchParameterValue, int searchPattern) throws GooseException{
    	return ctMgrDb.getUserRank(searchParameterValue, searchPattern);	
    }
    
    /*
     * Returns the max rank from an array of contacts
     *
     */
    public Contact getMaxRankContact(Vector contacts){
        int max=0;
        Contact maxContact = null;
        for (int i=0; i< contacts.size(); i++){
        	Contact tmp = (Contact)contacts.elementAt(i);
            if(tmp.getScore()>max){
            	maxContact = tmp;
                max = tmp.getScore();
            }
        }
        return maxContact;
    }
    

    
    /*
     * Method that updates the last time a contact, identified by its Bluetooth Adress, was seen
     *
     */
    public void updateContactTimeStamp (String btAddress){
       ctMgrDb.updateContactTimeStamp(btAddress);
    }

    /*
     * Method that updates the last time a contact, identified by its Bluetooth Adress, was seen
     *
     */
    public void updateDiscoveryInformation (String btAddress){    	
       ctMgrDb.updateDiscoveryInformation(btAddress);
    }
    
    /*
     * Updates a contact rank value (aka score)
     */
    public void updateContactRank(String btAddress, int increase){
    	ctMgrDb.updateContactRank(btAddress, increase);	
    }
    
    /*
     * Returns the number of contacts stored that are in the vector v (BluetoothAddress)
     */
    public int getNumberOfContacts(Vector btAddress){
    	int number=0;
    	for (int i=0; i<btAddress.size(); i++){
    		String tmpBTAddr = (String)btAddress.elementAt(i);
    		try{
    			number+=ctMgrDb.exclusiveSearch(tmpBTAddr, ContactsManager.BTADDRESS).size();
    		}
    		catch(Exception e){
//    			//log.debug(e.getMessage());
    			return 0;
    		}
    	}
    	return number;
    }
    
    
    /*
     * Updates a contact bluetooth address
     */
    public void updateContactBluetoothAddress(String oldBtAddress, String newBtAddress){
        ctMgrDb.updateContactBluetoothAddress(oldBtAddress, newBtAddress);
    }
    

    /*
     * Returns the last "number" contacts used by their time stamp.It is way of knowing the most important nodes on someone´s social network
     *
     * @param number Number of maximum contacts to be retrieved
     */
    public Vector getLastContactsUsed(int number) {
        return ctMgrDb.getLastContactsUsed(number);
    }
    
    /*
     * Checks if any of the nearby devices discovered by Bluetooth is in the address book
     *
     * It must be optimized. It takes O(n^2)
     * 
     * @param	serviceRecordVector	It is a vector containing the different Goose ServiceRecord's found when performing Bluetooth Discovery
     * @return 	A vector with the contacts that are nearby
     */
    public synchronized Vector searchNearbyContacts (Vector serviceRecord){
    	Vector returnVector = ctMgrDb.searchNearbyContacts(serviceRecord);
//    	//log.debug("searchNearbyContacts. Number of known nearby contacts found: "+returnVector.size());
        return returnVector;
    }


	public Vector searchNearbyKnownServiceRecord(Vector serviceRecord) {
		// TODO Auto-generated method stub
		return ctMgrDb.searchNearbyServiceRecord(serviceRecord);
	}

}
