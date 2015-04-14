package goose.mainManager;

import goose.forwardingManager.GooseMessage;
import goose.contactsManager.Contact;
import goose.exceptions.*;

import java.util.Vector;
import javax.bluetooth.ServiceRecord;

/**
 * It describes the API that can be used by the UI to communicate with Goose
 *
 * @author Narseo
 */
public interface IGooseAPI {

	/*
	 * Sends a ACK message with the local contact details to a particular device
	 *
	 *@param remoteConnectionURL this parameter is the connection URL of the remote device interested on exchanging the contact details. It is retrieved from the Message body by MessageTools.readContactMessage(String). It is the element in the index 4 of the array returned.
	 */
	public void sendContactsACK(String remoteConnectionURL, String remoteBTAddr) throws GooseNetworkException;
	
	/*
	 * Method that should be invoked to delete all the contacts stored in the Contact Manager
	 */
	public void deleteAllContacts();
	
	 /*
     * Returns the last contact used by the goose user
     *
     * @Return a Vector(Contacts) Vector of size number last contacts ranked by their time Stamp.
     */
    public Vector getLastContactsUsed(int number);
 
   
    
    

    /*
     * Send the local device personal profile ACK details with a particular device
     *   
     * @param sr Service Record of the nearby device to conect and profile to send
     */
    public void sendPersonalProfileACK(String connectionURL, String remoteBTAddr, String dataString) throws GooseNetworkException;


    /*
     * Send the local device personal profile details with a particular device
     * 
     * @param sr Service Record of the nearby device to conect and profile to send
     */
    public void sendRequestPersonalProfile(ServiceRecord sr) throws GooseNetworkException;



    
    
    
	/*
	 * It allows the user to store his user details when launching Goose (name, surname, phonenumber). It is used as a login way and also to exchange contact details with other users. In fact, it is necessary to transmit the information in the GooseMessage header.
	 * 
	 * It is also pointless to store it permanently on the device since it will not be able to authenticate against a server. A way of authenticating could be by asking the user to introduce his/her phone number and compare it with the MSISDN. However, this information is not stored on the SIM card (it is on the operator) and it is not accessible unles we use the JSR-177.
	 * 
	 * Some people can hack the MSISDN from the HTTP headers since some operators include it there.
	 * @param name User Name.
	 * @param surname User Surname
	 * @param phonenumber User Phone Number.
	 * @throws GooseException It throws a GooseException in case the phone number is not an international phone number (00... ) and length < 15.
	 */
	public void setUserDetails(String name, String surname, String phonenumber) throws GooseException;
	
	/*
	 * Returns the time the message entered the system
	 * @param UUID Message UUID to identify it
	 * @return time stamp (long) retrieved from the RCT
	 */
	public long getMessageTimeIn(String UUID);
	
    /*
     * Method that communicates to Goose Manager that a new message was created. 
     * 
     * In this case, GooseManager will take into account the resources but this functionallity is not still provided since it is very platform-dependant.
     *
     * @param recipient Vector of Contacts
     * @param m GooseMessage to send
     * @throws GooseNetworkException It is thrown in case it was impossible to perform the transmission. More details in the exception message.
     */
    public void send(Vector recipient, GooseMessage m) throws GooseNetworkException;

    /*
     * Method invoked when a friend search is going to be performed. If the user didn't introduce any of those parameters, they must be a string with the value "none";
     *
     * When the ACK message is received (FRIEND_SEEK_ACK type for the IncomingMessageEvent), the UI must tell the user if he/she wants to add this users since it may be potentially wrong
     * 
     * @param name Name of the person to look for
     * @param surname Surname of the person to look for
     * @param phoneNumber Phone Number of the person to look for
     */
    public void seekFriend(String name, String surname, String phoneNumber);

    /*
     * Searchs for a Contact entry in the ContactsManager Database.
     * 
     * @param searchParameterValue Value to look for
     * @param searchPattern Index to look into (name, surname, phone number..). All those are defined in the object ContactsManager
     * @return Returns a Vector(Contact) with the entries that matched the search
     * @throws GooseSearchException Exception that is thrown in case that no user was found
     */
    public Vector searchContact(String searchParameterValue, int searchPattern) throws GooseSearchException;

    /*
     * Method that searchs a contact by multiple parameters. Similar to searchContact(string, int) but a search is more precisse since there are more parameters to check.
     * 
     * @param searchParameterValue Array of String with the values to look for
     * @param searchPattern Array of int with the patterns (index) to look for. 
     * @return Returns a Vector(Contact) with the entries that matched the search
     * @throws GooseSearchException exception thrown in case that the length of the parameters is different or in case that the search failed
     */
    public Vector searchContact(String [] searchParameterValue, int [] searchPattern) throws GooseSearchException;
    
    /*
     *	Looks in the contact list which btAddress belong to contacts in the address book. It will store the contacts in the temp. Cache
     *
     * @param serviceRecord Vector(serviceRecord) with all the information of the remote device
     * @return Vector(Contact) with the search results
     */
    public Vector searchNearbyContacts (Vector serviceRecord) throws GooseSearchException, GooseException, GooseNetworkException;

    /*
     * Returns all the contacts stored in the DataBase
     * 
     * @return Vector(Contact)
     */
    public Vector getContacts() throws GooseSearchException, GooseException;

    /*
     * Allows editing a contact from the address book
     * 
     * @param contact Contact to modify
     * @param newParameters String array with the new parameters to introduce. Name in index 0, surname 1, phone number 2
     * 
     */
    public void editContact (Contact contact, String [] newParameters) throws GooseSearchException;

    /*
     * Deletes a contact
     * 
     * @param contact Contact to delete
     * @throws GooseException In case a contact was not found and the contact removal failed
     */
    public void deleteContact (Contact contact) throws GooseException;
    
    /*
     * Allows creating a New Message.
     * 
     * @param	recipientList	Vector of Contacts that contains the whole list of destinations for this particular message.
     * @param 	txMode	String that indicates the txMode for this particular message.
     * @param	priority	Int that indicates the Forwarding Manager the priority for this particular message. It can be explicitly indicated by the user or by the UI taking into account the nature of the message (e.g. MCAST; UCAST; BCAST...)
     * @param	contentType	String indicating the type of information that will be forwarded
     * @param	content	Content of the message to transmit. In the case of voice messages, they must be converted to a String 
     * @return GooseMessage Message created 
     */
    public GooseMessage createNewMessage(Vector recipientList, String txMode, int priority, String contentType, String content) throws GooseException;

    /*
     * Searchs for a a message in the ForwardingManagerDatabase
     * 
     * @param searchParameterValue value to search
     * @param searchParameter parameter to look into
     * @return Vector(GooseMessage) with the results
     */
    public Vector searchMessage(String searchParameterValue, int searchParameter) throws GooseSearchException;

    /*
     * Returns the inbox messages
     * 
     * @return Vector(GooseMessage)
     */
    public Vector getInboxMessages() throws GooseSearchException;

    /*
     * Returns the sent messages
     * 
     * @return Vector(GooseMessage)
     */
    public Vector getSentMessages() throws GooseSearchException;
    
    /*
     * Returns the top contacts by their rank
     * 
     * @param number Number of contacts to retrieve
     * @return Vector(Contacts) Vector of size number with the top contacts ranked by their score. If the number of elements stored in the database is smaller than 10, some elements are null and it is necessary to check it on the UI
     */
    public Vector getTopContacts(int number);
    
    /*
     * Exchanges the local device contact details with a particular device
     * 
     * @param sr Service Record of the nearby device to conect
     */
    public void exchangeContactDetails(ServiceRecord sr) throws GooseNetworkException;
    
    /*
     * Returns the nearby devices 
     * 
     * @return Vector(ServiceRecord) of the nearby devices discovered
     */
    public Vector getNearbyDevices();

    /*
     * Deletes a message from the ForwardingManagerDatabase
     * @param m Message to delete
     */
    public void deleteMessage(GooseMessage m) throws GooseSearchException;
    
    /*
     * Sets the message identified by its UUID as already read. This is to show in the GUI which messages were unread.
     * 
     * It will later commit the changes to the database.
     * 
     * @param UUID The UUID of the message to be updated
     */
    public void setMessageAsRead(String UUID) throws GooseException;
}
