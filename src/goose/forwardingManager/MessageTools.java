package goose.forwardingManager;

import goose.contactsManager.Contact;
import goose.exceptions.GooseException;
import goose.exceptions.GooseSearchException;
import goose.contactsManager.ContactsManager;
//import microlog.Logger;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.UTFDataFormatException;
import java.util.Vector;

import javax.bluetooth.ServiceRecord;

import microlog.Logger;

public class MessageTools {

	/*
	 * Serializes a message into an byte array for tx-ing in the network
	 * 
	 */
	public static byte[] serializeMessage(GooseMessage m) throws GooseException{
    	ByteArrayOutputStream bout = new ByteArrayOutputStream();
    	DataOutputStream dout = new DataOutputStream(bout);
    	
    	try{
	    	dout.writeInt(m.messageId);
	    	dout.writeUTF(m.senderName);
	    	dout.writeUTF(m.senderSurname);
	    	dout.writeUTF(m.senderPhoneNumber);
	    	dout.writeUTF(m.senderBTMacAddress);
	    	dout.writeUTF(m.recipient);
	    	dout.writeUTF(m.transmissionMode);
	    	dout.writeLong(m.TTL);
	    	dout.writeInt(m.priority);
	    	dout.writeUTF(m.GUID);
	    	dout.writeUTF(m.previousRelays);
	    	dout.writeUTF(m.contentType);
	    	dout.writeUTF(m.content);
	    	dout.writeUTF(m.folder);
	    	dout.writeBoolean(m.newMessage);
	    	dout.writeLong(m.recTime);
	    	dout.flush();
    	}
    	catch(Exception e){
    		throw new GooseException("Error serializing object");
    	}
    	
    	return bout.toByteArray();
    }
    
	/*
	 * Deserializes a message from a byte array into a Message object
	 */
    public static GooseMessage deserializeMessage(byte [] data) throws GooseException{
    	ByteArrayInputStream bin = new ByteArrayInputStream(data);
    	DataInputStream din = new DataInputStream(bin);
    	
    	GooseMessage m = new GooseMessage();
    	
    	try{
    		m.messageId = din.readInt();
    		m.senderName = din.readUTF();
    		m.senderSurname = din.readUTF();
    		m.senderPhoneNumber = din.readUTF();
    		m.senderBTMacAddress = din.readUTF();
    		m.recipient = din.readUTF();
    		m.transmissionMode = din.readUTF();
    		m.TTL = din.readLong();
    		m.priority = din.readInt();
    		m.GUID = din.readUTF();
    		m.previousRelays = din.readUTF();
    		m.contentType = din.readUTF();
    		m.content = din.readUTF();
    		m.folder = din.readUTF();
    		m.newMessage=din.readBoolean();
    		m.recTime=din.readLong();
    	}
    	catch(Exception e){
    		throw new GooseException("Error serializing object");
    	}   	
    	
    	return m;
    }
    
    private static Logger log = Logger.getLogger();
    /*
     * Converts a byte array into a string
     */
    public static String convertByteArrayToString(byte [] data) throws GooseException{
//    	try{
//    		//log.debug("Tam data sound: "+data.length);
//	    	ByteArrayInputStream bin = new ByteArrayInputStream(data);
//	    	if (bin == null) {
//	    		//log.debug("ByteArray = null");
//	    	}
//	    	//log.debug("Bytes availables: "+bin.available());
//	    	DataInputStream din = new DataInputStream(bin);
//	    	if (din == null) {
//	    		//log.debug("DataInputStream = null");
//	    	}
	    	//return din.readUTF();
	    	return new String(data);
//    	}
//    	catch(EOFException eof){
//    		throw new GooseException ("Error EOF converting byte array to string:"+ eof.getMessage());
//    	}
//    	catch(IOException ioe){
//    		throw new GooseException ("Error IO converting byte array to string:"+ ioe.getMessage());
//    	}
    }
    
    /*
     * Converts a string into a byte array
     * 
     */
    public static byte[] convertStringToByteArray(String data) throws GooseException{
    	try{
    		/*
        	ByteArrayOutputStream bout = new ByteArrayOutputStream();
        	DataOutputStream dout = new DataOutputStream(bout);
        	dout.writeUTF(data);
        	dout.flush();
        	return bout.toByteArray();
        	*/
    		//log.debug("UIGoose. Siye data toconvert: "+data.length());
    		return data.getBytes();
    		/*
    		ByteArrayInputStream bin = new ByteArrayInputStream(data.getBytes());
    		DataInputStream din = new DataInputStream(bin);
    		byte[] dataByte = null;
    		din.read(dataByte);
    		return dataByte;
    		*/
    	}
    	catch(Exception e){
    		throw new GooseException("Error converting from string to byte array");
    	}
    }
    
    /*
     * Write into a single String a list of recipients that will contain this message;
     * 
     * @param	contacts	Vector of ServiceRecords containing a List of recipients for this message
     * @return	String containing all the Bluetooth addresses of this message. Every bluetooth address is separated by "|". In case that it is a Broadcast message or that an error happened, it returns BCAST.
     */
    public static String writePreviousRelays(Vector servRecord){
    	if (servRecord == null || servRecord.size()==0){
    		return "";
    	}
    	String urlList = "";
    	for (int i=0; i<servRecord.size(); i++){
			ServiceRecord tmp = (ServiceRecord)servRecord.elementAt(i);
			if (i==(servRecord.size()-1)){
				urlList+=tmp.getHostDevice().getBluetoothAddress();
			}
			else{
				urlList+=tmp.getHostDevice().getBluetoothAddress()+"|";
			}    		
    	}
    	return urlList;
    }
 
    /*
     * Write into a single String a list of recipients that will contain this message;
     * 
     * @param	contacts	Vector of ServiceRecords containing a List of recipients for this message
     * @return	String containing all the Bluetooth addresses of this message. Every bluetooth address is separated by "|". In case that it is a Broadcast message or that an error happened, it returns BCAST.
     */
    public static String writeRecipientsListFromContactsVector(Vector contacts){
    	if (contacts == null || contacts.size()==0){
    		return "BCAST";
    	}
    	String urlList = "";
    	for (int i=0; i<contacts.size(); i++){
			Contact tmp = (Contact)contacts.elementAt(i);
			if (i==(contacts.size()-1)){
				urlList+=tmp.getBluetoothAddress();
			}
			else{
				urlList+=tmp.getBluetoothAddress()+"|";
			}    		
    	}
    	return urlList;
    }
    /*
     * Write into a single String a list of recipients that will contain this message. It will add previous messages for messages that are being forwarded to other devices
     * 
     * @param	contacts	Vector of Contacts containing a List of recipients for this message
     * @return	String containing all the Bluetooth addresses of this message. Every bluetooth address is separated by "|".
     */
    public static String writeRecipientsList(String localBTAddr, Vector contacts, String previous){
//    	Logger log = Logger.getLogger();
    	Vector v = readRecipientList(previous);
    	String urlList = previous;
//    	//log.debug("writeRecipientList: urlList "+previous);/*
    	if(v.size()>0){
    		urlList=urlList+"|";
    	}
//    	//log.debug("writeRecipientList: urlList "+previous);*/
    	for (int i=0; i<contacts.size(); i++){
			Contact tmp = (Contact)contacts.elementAt(i);

    		if(v.contains(tmp.getBluetoothAddress())==false){
    			if (i==(contacts.size()-1)){
    				urlList+=tmp.getBluetoothAddress();
    			}
    			else{
    				urlList+=tmp.getBluetoothAddress()+"|";
    			}    			
    		}    		
    	}
    	return urlList;
    } 

    
    /*
     * Write into a single String a list of recipients that will contain this message. It will add previous messages for messages that are being forwarded to other devices
     * 
     * @param	contacts	Vector of Contacts containing a List of recipients for this message
     * @return	String containing all the Bluetooth addresses of this message. Every bluetooth address is separated by "|".
     */
    public static String writeNextRelaysList(String localBTAddr, Vector contacts, String previous){
//    	Logger log = Logger.getLogger();
    	Vector v = readRecipientList(previous);
//    	//log.debug("Number of elements read from the previous relays list: "+v.size());
    	String urlList = previous;
//    	//log.debug("writeRecipientList: urlList "+previous);
    	if(v.size()>0){
    		urlList=urlList+"|";
    	}
//    	//log.debug("writeRecipientList: urlList "+previous);
    	for (int i=0; i<contacts.size(); i++){
			String tmp = (String)contacts.elementAt(i);

    		if(v.contains(tmp)==false){
    			if (i==(contacts.size()-1)){
    				urlList+=tmp;
    			}
    			else{
    				urlList+=tmp+"|";
    			}    			
    		}    		
    	}
    	return urlList;
    } 
    
    /*
     * Method that reads the whole list of recipients for a particular message from a String and stores them individually in a Vector
     * 
     * @param	urlList	String containing bluetooth mac addresses of the previous recipients, separated by "|"
     * @return	Vector of String containing the bluetooth addresses of the urlList
     */
    public static Vector readRecipientList(String btMacList){
		Vector stringList = new Vector();
		int index = 0;
		while ((index+12)<=btMacList.length()){
			//A bluetooth address is 12 characters long
			String tmp = btMacList.substring(index, index+12);
			stringList.addElement(tmp);
			index+=13;
		}
		return stringList;	
    }    
    
    public static long getTTL (String txMode){
    	if (txMode.equalsIgnoreCase(GooseMessage.UCAST)){
    		return GooseMessage.DEFAULT_UCAST_TTL;
    	}
    	else if (txMode.equalsIgnoreCase(GooseMessage.MCAST) || txMode.equalsIgnoreCase(GooseMessage.FRIEND_SEARCH)){
    		return GooseMessage.DEFAULT_MCAST_TTL;
    	}
    	else{
    		return GooseMessage.DEFAULT_BCAST_TTL;
    	}
    }
    
    public static String [] readContactMessage (String content){
//    	Logger log = Logger.getLogger();
//    	//log.debug(content);
    	int nameIndexStart = content.indexOf(GooseMessage.name, 0)+GooseMessage.name.length();
    	int nameIndexEnd = content.indexOf("|", 0);
    	String name = content.substring(nameIndexStart, nameIndexEnd);
//    	//log.debug(name);
    	
    	int surnameIndexStart = content.indexOf(GooseMessage.surname, 0)+GooseMessage.surname.length();
    	int surnameIndexEnd = content.indexOf("|", surnameIndexStart);
    	String surname = content.substring(surnameIndexStart, surnameIndexEnd);
    	
    	int phoneNumberIndexStart = content.indexOf(GooseMessage.phoneNumber, 0)+GooseMessage.phoneNumber.length();
    	int phoneNumberIndexEnd =  content.indexOf("|", phoneNumberIndexStart);
    	String phoneNumber = content.substring(phoneNumberIndexStart,phoneNumberIndexEnd);
    	
    	int btAddrIndexStart = content.indexOf(GooseMessage.btAddress, 0)+GooseMessage.btAddress.length();
    	int btAddrIndexEnd =content.indexOf("|", btAddrIndexStart);
    	String btAddress = content.substring(btAddrIndexStart, btAddrIndexEnd);
  
    	int urlIndexStart = content.indexOf(GooseMessage.connectionURL, 0)+GooseMessage.connectionURL.length();
    	int urlIndexEnd =content.indexOf("|", urlIndexStart);
    	String connURL = content.substring(urlIndexStart, urlIndexEnd);
    	
    	String [] contactInfo = {name, surname, phoneNumber, btAddress, connURL};
    	return contactInfo;
    }
    
    public static String[] readContactsACKMessage (String content){
//    	Logger log = Logger.getLogger();
//    	//log.debug(content);
    	int nameIndexStart = content.indexOf(GooseMessage.name, 0)+GooseMessage.name.length();
    	int nameIndexEnd = content.indexOf("|", 0);
    	String name = content.substring(nameIndexStart, nameIndexEnd);
//    	//log.debug(name);
    	
    	int surnameIndexStart = content.indexOf(GooseMessage.surname, 0)+GooseMessage.surname.length();
    	int surnameIndexEnd = content.indexOf("|", surnameIndexStart);
    	String surname = content.substring(surnameIndexStart, surnameIndexEnd);
    	
    	int phoneNumberIndexStart = content.indexOf(GooseMessage.phoneNumber, 0)+GooseMessage.phoneNumber.length();
    	int phoneNumberIndexEnd =  content.indexOf("|", phoneNumberIndexStart);
    	String phoneNumber = content.substring(phoneNumberIndexStart,phoneNumberIndexEnd);
    	if (phoneNumber.equalsIgnoreCase("none")){
//    		//log.debug("The ACK message does not contain information about the phoneNumber");
    		phoneNumber="";
    	}
    	
    	int btAddrIndexStart = content.indexOf(GooseMessage.btAddress, 0)+GooseMessage.btAddress.length();
    	int btAddrIndexEnd =content.indexOf("|", btAddrIndexStart);
    	String btAddress = content.substring(btAddrIndexStart, btAddrIndexEnd);
    	if (btAddress.equalsIgnoreCase("none")){
//    		//log.debug("The ACK message does not contain information about the btAddr");
    		btAddress="";
    	}  
    	
    	String [] contactInfo = {name, surname, phoneNumber, btAddress};
    	return contactInfo;    	
    }
    
    /*
    public static void printContact(Contact c){
    	Logger log = Logger.getLogger();
    	//log.debug("Name: "+c.getName());
    	//log.debug("Surname: "+c.getSurname());
    	//log.debug("PhoneNumber: "+c.getPhoneNumber());
    	//log.debug("BTAddr: "+c.getBluetoothAddress());
    }*/
    
    public static String [] readSearchContactMessageValue (String content)throws GooseSearchException{
    	Vector returnString = new Vector();
    	int nameIndexStart = content.indexOf(GooseMessage.name, 0)+GooseMessage.name.length();
    	int nameIndexEnd = content.indexOf("|", 0);
    	String name = content.substring(nameIndexStart, nameIndexEnd);
    	if (name.equalsIgnoreCase("none")==false){
    		returnString.addElement(name);
    	}
    	
    	int surnameIndexStart = content.indexOf(GooseMessage.surname, 0)+GooseMessage.surname.length();
    	int surnameIndexEnd = content.indexOf("|", surnameIndexStart);
    	String surname = content.substring(surnameIndexStart, surnameIndexEnd);
    	if (surname.equalsIgnoreCase("none")==false){
    		returnString.addElement(surname);
    	}    	
    	
    	int phoneNumberIndexStart = content.indexOf(GooseMessage.phoneNumber, 0)+GooseMessage.phoneNumber.length();
    	int phoneNumberIndexEnd =  content.indexOf("|", phoneNumberIndexStart);
    	String phoneNumber = content.substring(phoneNumberIndexStart,phoneNumberIndexEnd);
    	if (phoneNumber.equalsIgnoreCase("none")==false){
    		returnString.addElement(phoneNumber);
    	}     	
    	
    	String [] contactInfo = new String[returnString.size()];
    	if(returnString.size()==0){
    		throw new GooseSearchException("No parameter introduced on a SearchFriends");
    	}
    	for (int i=0; i<returnString.size(); i++){
    		contactInfo[i]=(String)returnString.elementAt(i);
    	}
    	return contactInfo;
    }
    
    public static int [] readSearchContactMessagePattern (String content, int length){
    	int [] returnValues = new int[length];
    	int index=0;
    	int nameIndexStart = content.indexOf(GooseMessage.name, 0)+GooseMessage.name.length();
    	int nameIndexEnd = content.indexOf("|", 0);
    	String name = content.substring(nameIndexStart, nameIndexEnd);
    	if (name.equalsIgnoreCase("none")==false){
    		returnValues[index]=ContactsManager.NAME;
    		index++;
    	}
    	
    	int surnameIndexStart = content.indexOf(GooseMessage.surname, 0)+GooseMessage.surname.length();
    	int surnameIndexEnd = content.indexOf("|", surnameIndexStart);
    	String surname = content.substring(surnameIndexStart, surnameIndexEnd);
    	if (surname.equalsIgnoreCase("none")==false){
    		returnValues[index]=ContactsManager.SURNAME;
    		index++;
    	}    	
    	
    	int phoneNumberIndexStart = content.indexOf(GooseMessage.phoneNumber, 0)+GooseMessage.phoneNumber.length();
    	int phoneNumberIndexEnd =  content.indexOf("|", phoneNumberIndexStart);
    	String phoneNumber = content.substring(phoneNumberIndexStart,phoneNumberIndexEnd);
    	if (phoneNumber.equalsIgnoreCase("none")==false){
    		returnValues[index]=ContactsManager.PHONENUMBER;
    		index++;
    	}     	
    	
    	return returnValues;
    }
    
    public static String writeContactMessage (String name, String surname, String phoneNumber, String btAddress, String connectionURL){
    	return GooseMessage.name+name+"|"+GooseMessage.surname+surname+"|"+GooseMessage.phoneNumber+phoneNumber+"|"+GooseMessage.btAddress+btAddress+"|"+GooseMessage.connectionURL+connectionURL+"|";
    }
    
    public static String writeSearchContactMessage (String name, String surname, String phoneNumber){
    	return GooseMessage.name+name+"|"+GooseMessage.surname+surname+"|"+GooseMessage.phoneNumber+phoneNumber+"|";
    }
    
    public static String writeProfileMessage (String data){
    	return data;
    }

}
