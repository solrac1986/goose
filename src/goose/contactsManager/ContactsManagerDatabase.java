package goose.contactsManager;

import java.util.Vector;

import javax.bluetooth.RemoteDevice;
import javax.bluetooth.ServiceRecord;
import goose.exceptions.GooseException;
import goose.mainManager.Root;
import org.garret.perst.Iterator;
import org.garret.perst.Key;
import org.garret.perst.Storage;

/**
*
* @author Narseo
*/
public class ContactsManagerDatabase {

    private Storage dbRef;
    private boolean dbModified;
    
    ContactsManagerDatabase (Storage db){
    	this.dbRef=db;
        this.dbModified = false;
//        //log.debug("Contacts Manager Database Created");
    }
    
    public synchronized boolean isModified(){
    	return dbModified;
    }
    
    public synchronized void commit(){
        dbRef.commit();
        dbModified = false;
//        //log.debug("Database Updated. Space used: "+dbRef.getUsedSize()); 		
    }
    
    
    /*
     * Adds a new contact to the DB
     * 
     */
    public synchronized void add (Contact contact){
    	if (exists(contact)){
    		return;
    	}
    	dbRef.storeObject(contact);
        Root root = (Root)dbRef.getRoot();
        
        //Include objects into the index.
        root.userId.put(new Key(contact.getId()), contact);
        root.surname.put(new Key(contact.getSurname()), contact);
        root.name.put(new Key(contact.getName()), contact);
        root.phoneNumber.put(new Key(contact.getPhoneNumber()), contact);
        root.btAddress.put(new Key(contact.getBluetoothAddress()), contact);
        dbModified = true;
    }
    
    /*
     * Contacts are not unique by their name, surname and phone number. It is necessary to create 
     * a deeper search and compare properly other parameters.
     * 
     */
    public synchronized boolean exists(Contact contact){
    	Root root = (Root)dbRef.getRoot();
    	Iterator iterator = null;
    	iterator = root.name.prefixIterator(contact.getName());
    	while(iterator.hasNext()){ 
            Contact rec = (Contact)iterator.next();
            if (rec.getSurname().equalsIgnoreCase(contact.getSurname())
            		&& rec.getPhoneNumber().equalsIgnoreCase(contact.getPhoneNumber())){
//            	//log.debug("Object already stored in DB");
            	return true;
            }
    	}
    	return false;
    }
    
    /*
     * Searchs for a set of contacts that match a particular criterion
     * 
     */
    public synchronized Vector search (String searchParameterValue, int searchPattern) throws GooseException{
    	Root root = (Root)dbRef.getRoot();
    	Vector results = new Vector(); 
    	Iterator iterator = null;
    	switch(searchPattern){
    	case ContactsManager.NAME:
    		iterator = root.name.prefixIterator(searchParameterValue);
    		break;
    	case ContactsManager.BTADDRESS:
    		iterator = root.btAddress.prefixIterator(searchParameterValue.toLowerCase());
    		break;
    	case ContactsManager.PHONENUMBER:
    		iterator = root.phoneNumber.prefixIterator(searchParameterValue);
    		break;
    	case ContactsManager.SURNAME:
    		iterator = root.surname.prefixIterator(searchParameterValue);
    		break;
    	default:
    		throw new GooseException("No results found");
    	}	
    	
    	while(iterator.hasNext()){ 
            Contact rec = (Contact)iterator.next();
            results.addElement(rec);
    	}
		return results;	
    }
    
    /*
     * It does not work for a single parameter. In this case, the method to be invoked is exclusiveSearch(String searchParameterValue, int searchPattern);
     */
    public synchronized Vector exclusiveSearch (String [] searchParameterValue, int [] searchPattern) throws GooseException {

    	if(searchParameterValue.length!=searchPattern.length &&
    			searchParameterValue.length<=1){
    		throw new GooseException ("The length of the arrays is different");
    	}
    	Root root = (Root) dbRef.getRoot();
    	Iterator iterator = null;
    	Vector ret = new Vector();
    	
    	switch(searchPattern[0]){
	    	case ContactsManager.NAME:
	    		iterator = root.name.prefixIterator(searchParameterValue[0]);
//	    		//log.debug("Index is name");
	    		break;
	    	case ContactsManager.BTADDRESS:
	    		iterator = root.btAddress.prefixIterator(searchParameterValue[0]);
//	    		//log.debug("Index is btAddr");
	    		break;
	    	case ContactsManager.PHONENUMBER:
	    		iterator = root.phoneNumber.prefixIterator(searchParameterValue[0]);
//	    		//log.debug("Index is phonenumber");
	    		break;
	    	case ContactsManager.SURNAME:
	    		iterator = root.surname.prefixIterator(searchParameterValue[0]);
//	    		//log.debug("Index is surname");
	    		break;
	    	default:
	    		throw new GooseException("No results found");
	    	}
    	
        for (int i = 0; iterator.hasNext(); i++) { 
            Contact rec = (Contact)iterator.next();
            boolean equals = true;
            for (int j=0; j<searchParameterValue.length; j++){
            	switch(searchPattern[j]){
	            	case ContactsManager.BTADDRESS:
	            		if(!rec.getBluetoothAddress().equalsIgnoreCase(searchParameterValue[j])){
	            			equals = false;
	            		}
	            		break;
	            	case ContactsManager.PHONENUMBER:
	            		if(!rec.getPhoneNumber().equalsIgnoreCase(searchParameterValue[j])){
	            			equals=false;
	            		}
	            		break;
	            	case ContactsManager.SURNAME:
	            		if(!rec.getSurname().equalsIgnoreCase(searchParameterValue[j])){
	            			equals=false;
	            		}
	        			break;
		        	case ContactsManager.NAME:
		        		if(!rec.getName().equalsIgnoreCase(searchParameterValue[j])){
		        			equals=false;
		        		}
		    			break;
		    		}
            }
        	if(equals){
//        		//log.debug("Exact search element found");
        		ret.addElement(rec);
        	}
        }
        return ret;
    }
    
    /*
     * Searchs for a set of contacts that match a particular criterion
     * 
     */
    public synchronized Vector exclusiveSearch (String searchParameterValue, int searchPattern) throws GooseException{
    	Root root = (Root)dbRef.getRoot();
    	Vector results = new Vector(); 
    	Iterator iterator = null;
    	switch(searchPattern){
    	case ContactsManager.NAME:
    		iterator = root.name.prefixIterator(searchParameterValue.toLowerCase());
        	
            for (int i = 0; iterator.hasNext(); i++) { 
                Contact rec = (Contact)iterator.next();
                if (rec.getName().equalsIgnoreCase(searchParameterValue)){
                	results.addElement(rec);
                }
            }
            
    		break;
    	case ContactsManager.BTADDRESS:
    		iterator = root.btAddress.prefixIterator(searchParameterValue.toLowerCase());
    		
            for (int i = 0; iterator.hasNext(); i++) { 
                Contact rec = (Contact)iterator.next();
                
                if (rec.getBluetoothAddress().equalsIgnoreCase(searchParameterValue)){
                	results.addElement(rec);
                }
            }
    		break;
    	case ContactsManager.PHONENUMBER:
    		iterator = root.phoneNumber.prefixIterator(searchParameterValue.toLowerCase());
        	
            for (int i = 0; iterator.hasNext(); i++) { 
                Contact rec = (Contact)iterator.next();
                if (rec.getPhoneNumber().equalsIgnoreCase(searchParameterValue)){
                	results.addElement(rec);
                }
            }
    		break;
    	case ContactsManager.SURNAME:
    		iterator = root.surname.prefixIterator(searchParameterValue.toLowerCase());
        	
            for (int i = 0; iterator.hasNext(); i++) { 
                Contact rec = (Contact)iterator.next();
                if (rec.getSurname().equalsIgnoreCase(searchParameterValue)){
                	results.addElement(rec);
                }
            }
    		break;
    	default:
    		throw new GooseException("No results found");
    	}	

		return results;	
    }
    
    /*
     * Edits a contact from the AddressBook. The String Array only includes the fields
     * - name
     * - surname
     * - phoneNumber
     *
     */
    public synchronized void modify (Contact contact, String [] newValues){
       //Probably, there's a more efficient way of doing it by using the modify() method
       Root root = (Root)dbRef.getRoot();
       root.userId.remove(new Key(contact.getId()), contact);
       root.surname.remove(new Key(contact.getSurname()), contact);
       root.name.remove(new Key(contact.getName()), contact);
       root.phoneNumber.remove(new Key(contact.getPhoneNumber()), contact);
       root.btAddress.remove(new Key(contact.getBluetoothAddress()), contact);

       //TODO. Is it the problem?
       dbRef.deallocateObject(contact);
       //dbRef.commit();
       
       //Now, update contact values
       //The userId will continue being the same
       contact.setName(newValues[0]);
       contact.setSurname(newValues[1]);
       try{
            contact.setPhonenumber(newValues[2]);
       }
       catch(GooseException e){
//           //log.debug("Phone Number is not valid");           
       }
       //Store the object in the db
       dbRef.storeObject(contact);

       //Insert again the object in the index
       root.userId.put(new Key(contact.getId()), contact);
       root.surname.put(new Key(contact.getSurname()), contact);
       //TODO: Error here!!!
       root.name.put(new Key(contact.getName()), contact);
       root.phoneNumber.put(new Key(contact.getPhoneNumber()), contact);
       root.btAddress.put(new Key(contact.getBluetoothAddress()), contact);
       dbModified = true;
    }
    
    /* 
     * Deletes a contact. It removes every entry from all the index that this contact will be
     *
     */
    public synchronized void deleteItem (Contact contact){
    	Root root = (Root)dbRef.getRoot();
    	root.userId.remove(new Key (contact.getId()), contact);
    	root.surname.remove(new Key (contact.getSurname()), contact);
    	root.name.remove(new Key(contact.getName()), contact);
    	root.phoneNumber.remove(new Key(contact.getPhoneNumber()), contact);
    	root.btAddress.remove(new Key(contact.getBluetoothAddress()), contact);
        dbRef.deallocateObject(contact);
        dbModified = true;
    }
    
    
    /*
     * Method that iterates in all the address book and stores the contacts in a vector.
     */
    public synchronized int getNumberOfStoredItems(){
        //TODO: See how to do it since for-each loops are not supported in 1.3. It must be like the Iterator example that can be found in perst introduction manual on page 16
    	Root root = (Root)dbRef.getRoot();
    	return root.userId.size();
    }
    
    /*
     * Method that returns a whole list of contacts by their ID
     */
    public synchronized Vector getItems(){
    	
    	Root root = (Root)dbRef.getRoot();
    	Vector results = new Vector();
    	Iterator iterator = root.userId.iterator();
    	
        for (int i = 0; iterator.hasNext(); i++) { 
            Contact rec = (Contact)iterator.next();
            results.addElement((Contact)rec);
        }    	
        return results;
    }
    
    /*
     * Returns the index of the element to be stored
     */
    public synchronized int currentIndex(){
    	Vector v = getItems();
    	if (v.size()>0)
    	{
    		Contact c = (Contact)v.elementAt(v.size()-1);
    		return c.getId()+1;
    	}
    	else{
    		return 0;
    	}
    }
    
    /*
     * Returns the rank for a particular user. It supports different search criteria
     *
     */
    public synchronized int getUserRank (String searchParameterValue, int searchPattern) throws GooseException{
    	Vector results = new Vector();
    	try{
    		results = exclusiveSearch(searchParameterValue, searchPattern);
    	}
    	catch(Exception e){
//    		//log.debug("Error getting contact Rank");
    		//return -1;
    		throw new GooseException("Error getting contact Rank");
    		
    	}
    	if (results.size()>0){
    		Contact tmp = (Contact)results.elementAt(0);
    		return tmp.getScore();
    	}
    	else{
    		throw new GooseException("Error getting contact Rank. Not element found"); 
    	}	
    }
    
    /*
     * Method that updates the last time a contact, identified by its Bluetooth Adress, was seen
     *
     */
    public synchronized void updateContactTimeStamp (String btAddress){
       Vector results = null;
       try{
           results = search (btAddress, ContactsManager.BTADDRESS);
           for (int i=0; i<results.size(); i++){
        	   Contact contact = (Contact)results.elementAt(i);
        	   if (contact.getBluetoothAddress().equalsIgnoreCase(btAddress)){
        		   modifySingleParameter(contact, "", ContactsManager.TIME_STAMP);		   
        	   }
           }
       }
       catch (GooseException e){
//           System.out.println("Error, no contacts match btAddress: "+btAddress);
       }
       dbModified = true;
    }
    
        /*
     * Modifies a parameter from a Contact in the database
     */
    public synchronized void modifySingleParameter(Contact contact, String value, int param){
    	
		   Root root = (Root)dbRef.getRoot();
		   root.userId.remove(new Key(contact.getId()), contact);
	       root.surname.remove(new Key(contact.getSurname()), contact);
	       root.name.remove(new Key(contact.getName()), contact);
	       root.phoneNumber.remove(new Key(contact.getPhoneNumber()), contact);
	       root.btAddress.remove(new Key(contact.getBluetoothAddress()), contact);

	       //TODO. Is it the problem?
	       dbRef.deallocateObject(contact);
	       //dbRef.commit();
	       
	       switch(param){
	       case ContactsManager.NAME:
	    	   contact.setName(value);
	    	   break;
	       case ContactsManager.SURNAME:
	    	   contact.setSurname(value);
	    	   break;
	       case ContactsManager.PHONENUMBER:
	    	   try{
		    	   contact.setPhonenumber(value);
	    	   }
	    	   catch(Exception e){
//	    		   //log.debug("Invalid phone Number");
	    		   //We do not modify the existing phone number at all
	    		   
	    	   }
	    	   break;
	       case ContactsManager.BTADDRESS:
	    	   contact.setBluetoothAddress(value);
	    	   break;
	       case ContactsManager.RANK:
	    	   contact.setScore(Integer.parseInt(value));
	    	   break;
	       case ContactsManager.TIME_STAMP:
	    	   contact.setTimeStamp();
	    	   break;
	       default:
//	    	   //log.debug("Unknown option");
	    	   return;
	       }
	       
	       //Store the object in the db
	       dbRef.storeObject(contact);

	       //Insert again the object in the index
	       root.userId.put(new Key(contact.getId()), contact);
	       root.surname.put(new Key(contact.getSurname()), contact);
	       root.name.put(new Key(contact.getName()), contact);
	       root.phoneNumber.put(new Key(contact.getPhoneNumber()), contact);
	       root.btAddress.put(new Key(contact.getBluetoothAddress()), contact);
	       dbModified = true;
    }
    
    /*
    private boolean isPhonenumber(String phoneNumber){
    	if (phoneNumber.startsWith("+")){
    		if(phoneNumber.length()>15){
    			return false;
    		}
    		for (int i=1; i<phoneNumber.length(); i++){
                if (phoneNumber.charAt(i) < '0' || phoneNumber.charAt(i)>'9'){ 
                	return false;
                }
    		}
    	}
    	else{
    		return false;
    	}
    	return true;
    }*/
    
    /*
     * Checks if any of the nearby devices discovered by Bluetooth is in the address book
     *
     * It must be optimized. It takes O(n^2)
     * 
     * @param	serviceRecordVector	It is a vector containing the different Goose ServiceRecord's found when performing Bluetooth Discovery
     * @return 	A vector with the contacts that are nearby
     */
    public synchronized Vector searchNearbyContacts (Vector serviceRecordVector){
        Vector results = new Vector();
        //Iterate the nearby devices
        for (int i=0; i<serviceRecordVector.size(); i++){
        	//Check if there are any on the AddressBook implemented in Perst
        	ServiceRecord tmpSr = (ServiceRecord)serviceRecordVector.elementAt(i);
        	RemoteDevice rmt = tmpSr.getHostDevice();
            try{
                Vector tmpContacts = search(rmt.getBluetoothAddress(), ContactsManager.BTADDRESS);
//                //log.debug("Device found with btAddr: "+rmt.getBluetoothAddress()+", number of matches: "+tmpContacts.size());
                for (int j=0; j<tmpContacts.size(); j++){
                	Contact tmp = (Contact)tmpContacts.elementAt(j);
                	
                    results.addElement(tmp);
//                	//log.debug("Know device found, Name: "+tmp.getName()+" "+tmp.getSurname());
                }
            }
            catch(GooseException e){
//                //log.debug("Device "+rmt.getBluetoothAddress()+" not found in the Address Book");
            }
        }
        try{
        	return sortContactsByRank(results);
        }
        catch(Exception e){
        	//There was an exception looking for them. Return an empty vector
        	return new Vector();
        }
    }
    

    public synchronized Vector getLastContactsUsed(int number){
          try {
              Vector contacts = sortContactsByTime(getItems());
              contacts.setSize(number);
              return contacts;           
          }
          catch(Exception e){
//              //log.debug("ContactsManagerDataBase.getLastContactsUsed. Exception: "+e.getMessage());
              return new Vector();
          }       
      }
    

    /*
       * Returns a Vector with the contacts sorted in reverse order by their time stamp.
       *
       * Insertion sort is implemented
       */
      private synchronized Vector sortContactsByTime(Vector contacts) throws GooseException{
          if (contacts == null || contacts.size()==0){
              throw new GooseException("Vector is not initialised or is empty");
          }

          int firstOutOfOrder, location;
          Contact tmp;
         
          for (firstOutOfOrder=1; firstOutOfOrder<contacts.size(); firstOutOfOrder++){
              Contact current = (Contact)contacts.elementAt(firstOutOfOrder);
              Contact previous = (Contact)contacts.elementAt(firstOutOfOrder-1);
              if (current.getTimeStamp()>previous.getTimeStamp()){
                  tmp=current;
                  location = firstOutOfOrder;
                  Contact newContact=null;
                  Contact prevContact = null;
                  do{//Keep moving down the array until we find exactly where it's supposed to go
                      newContact = (Contact)contacts.elementAt(location-1);
                      contacts.setElementAt(newContact, location);
                      location--;
                      if(location>0){
                          prevContact = (Contact)contacts.elementAt(location -1);
                      }
                      else{
                          break;
                      }
                  }while(location>0 && prevContact.getTimeStamp()<tmp.getTimeStamp());
                 
                  contacts.setElementAt(tmp, location);
              }           
          } 
          return contacts;
      }
      
    /*
     * Returns a vector of service records from the nearby devices that includes only service records that belong to contacts we have in the address book
     */
	public synchronized Vector searchNearbyServiceRecord(Vector serviceRecord) {
		// TODO Auto-generated method stub
		Vector results = new Vector();
		for (int i=0; i<serviceRecord.size(); i++){
			ServiceRecord tmpSr = (ServiceRecord)serviceRecord.elementAt(i);
			String btAddr = tmpSr.getHostDevice().getBluetoothAddress();
			try{
				Vector tmpContacts = search(btAddr, ContactsManager.BTADDRESS);
				if (tmpContacts.size()>=1){
//					//log.debug("Looking for trusted relays. New node: "+tmpSr.getHostDevice().getBluetoothAddress()+" added");
					results.addElement(tmpSr);
				}
			}
			catch(Exception e){
//				//log.debug("Error looking for trusted relays: "+e.getMessage());
				return new Vector();
			}
		}
		return results;
	}
    
    /*
     * Returns a Vector with the contacts sorted in reverse order by their rank. 
     * 
     * Insertion sort is implemented
     */
    private synchronized Vector sortContactsByRank(Vector contacts) throws GooseException{
    	if (contacts == null || contacts.size()==0){
    		throw new GooseException("Vector is not initialised or is empty");
    	}

    	int firstOutOfOrder, location;
    	Contact tmp;
    	
    	for (firstOutOfOrder=1; firstOutOfOrder<contacts.size(); firstOutOfOrder++){
    		Contact current = (Contact)contacts.elementAt(firstOutOfOrder);
    		Contact previous = (Contact)contacts.elementAt(firstOutOfOrder-1);
    		if (current.getScore()>previous.getScore()){
    			tmp=current;
    			location = firstOutOfOrder;
        		Contact newContact=null;
        		Contact prevContact = null;
        		do{//Keep moving down the array until we find exactly where it's supposed to go
        			newContact = (Contact)contacts.elementAt(location-1);
        			contacts.setElementAt(newContact, location);
        			location--;
        			if(location>0){
        				prevContact = (Contact)contacts.elementAt(location -1);
        			}
        			else{
        				break;
        			}
        		}while(location>0 && prevContact.getScore()<tmp.getScore());
        		
        		contacts.setElementAt(tmp, location);
    		}    		
    	}  
    	return contacts;
    }
    
    private synchronized Vector sortContactsByName(Vector contacts) throws GooseException{
    	if (contacts == null || contacts.size()==0){
    		throw new GooseException("Vector is not initialised or is empty");
    	}
        boolean swapped = true;

        // Setup our outer loop for detecting if a swap was made
        while (swapped) {
                swapped = false;

                // Loop through the array, bubbling up.
                for(int i=0; i < contacts.size() - 1; i++)
                {
                	Contact current = (Contact)contacts.elementAt(i);
                	Contact next = (Contact)contacts.elementAt(i+1);
                	
                	
                        // This is where you compare your strings for equality (using compareTo)
                        if(current.getName().compareTo(next.getName()) > 0)
                        {
                        	Contact temp = (Contact)contacts.elementAt(i);
   
                                // You can't use elementAt for swapping, use setElementAt()
                                contacts.setElementAt(contacts.elementAt(i+1),i);
                                contacts.setElementAt(temp,i+1);

                                // Yes a swap was made, so another pass is needed.
                                swapped = true;
                        }
                }
        }
        // After all sorting is finished, just list all the items in the array.
        return contacts;
    }

    public synchronized Vector getContactsSortedByName() throws GooseException{
    	try{
    		return sortContactsByName(getItems());
    	}
    	catch(Exception e){
//    		//log.debug("ContactsManagerDatabase. getContactsSortedByName: Error sorting database by name: "+e.getMessage());
    		throw new GooseException(e.getMessage());
    	}
    }
    
    public synchronized Vector getTopContacts(int number){
    	try{
        	Vector contacts = sortContactsByRank(getItems());
        	contacts.setSize(number);
        	return contacts;    		
    	}
    	catch(Exception e){
//    		//log.debug(e.getMessage());
    		return new Vector();
    	}    	
    }
    
    /*
     * Updates a contact bluetooth address
     */
    public synchronized void updateContactBluetoothAddress(String oldBtAddress, String newBtAddress){
        try{
        	Vector results = null;
            results = search (oldBtAddress, ContactsManager.BTADDRESS);
            for (int i=0; i<results.size(); i++){
         	   Contact contact = (Contact)results.elementAt(i);
         	   if (contact.getBluetoothAddress().equalsIgnoreCase(oldBtAddress)){
         		   modifySingleParameter(contact, newBtAddress, ContactsManager.BTADDRESS);		   
         	   }
            }    	
        }
        catch (GooseException e){
//            //log.debug("Error, no contacts match btAddress: "+oldBtAddress);
        }
    }
    
    /*
     * Method that updates the last time a contact, identified by its Bluetooth Adress, was seen
     *
     */
    public synchronized void updateDiscoveryInformation (String btAddress){    	
       Vector results = null;
       try{
           results = search (btAddress, ContactsManager.BTADDRESS);
           for (int i=0; i<results.size(); i++){
        	   Contact contact = (Contact)results.elementAt(i);
        	   if (contact.getBluetoothAddress().equalsIgnoreCase(btAddress)){
        		   modifySingleParameter(contact, "", ContactsManager.TIME_STAMP);
             	   int prevScore = contact.getScore();
             	   int newScore = prevScore+ContactsManager.DISCOVERY_INCREASE_RANK;
             	  modifySingleParameter(contact, Integer.toString(newScore), ContactsManager.RANK);
        	   }
           }
       }
       catch (GooseException e){
//           System.out.println("Error, no contacts match btAddress: "+btAddress);
       }
    }
    
    /*
     * Updates a contact rank value (aka score)
     */
    public synchronized void updateContactRank(String btAddress, int increase){
    	
        Vector results = null;
        try{
            results = search (btAddress, ContactsManager.BTADDRESS);
            for (int i=0; i<results.size(); i++){
         	   Contact contact = (Contact)results.elementAt(i);
         	   int prevScore = contact.getScore();
         	   int newScore = prevScore+increase;
         	   if (contact.getBluetoothAddress().equalsIgnoreCase(btAddress)){
         		   try{
             		   modifySingleParameter(contact, Integer.toString(newScore), ContactsManager.RANK);         			   
         		   }
         		   catch(Exception e){
//         	        	//log.debug("Error updating contact rank for: "+btAddress+", exception: "+e.getMessage());
         		   }
         	   }
            }
        }
        catch(Exception e2){
//        	//log.debug("Error updating contact rank for: "+btAddress+", exception: "+e2.getMessage());
        }
    }
    
    public synchronized void closeDB (){
//    	TODO: Probably if we close it here, we will close it for the other managers
//        dbRef.close();
    	dbRef=null;
//    	//log.debug("ContactsManager DB stopped");
    }


}
