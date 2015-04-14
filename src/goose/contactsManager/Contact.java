/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package goose.contactsManager;

import goose.forwardingManager.GooseMessage;
import goose.exceptions.GooseException;
import goose.mainManager.GooseTools;
import org.garret.perst.*;


/**
 *
 * @author Narseo
 */
public class Contact extends Persistent{

    private int id; //Integer key
    private String name; //String key
    private String surname; //String key
    private String phoneNumber;
    private String btAddress;
    //We will use the timeStamp in miliseconds sin 1st Jan 1970
    private long timeStamp;
    private int score;
    private String connectionURL;
//    private Logger log = Logger.getLogger();
    
    /*
     * A default constructor is needed for Perst to be able to instantiate instances of loaded objects
     */
    public Contact(){}
    
    /*
     * Creates a new default Contact. Timestamp, Id and
     */
    public Contact (Storage db, int userId, String [] dataInput) throws GooseException{
        super(db);
        this.id = userId;
        setTimeStamp();
        this.score = 0;
        this.connectionURL=null;
        this.name = dataInput[0].toLowerCase();
        this.surname = dataInput[1].toLowerCase();
        
        if(dataInput.length==4){
            this.btAddress = dataInput[3].toLowerCase();        	
        }
        else{
        	this.btAddress="";
        }    
        
        if (isPhonenumber(dataInput[2])){
            this.phoneNumber = dataInput[2];
        }
        else{
        	this.phoneNumber="";
        	throw new GooseException("Invalid Phone Number: "+dataInput[2]);
        } 
    }
    
    

    /*
     * Constructor used for unknown senders (i.e. people we do not have in our address book
     */
    public Contact (String name, String surname, String btAddress, String phoneNumber){//throws GooseException{
    	this.name=name;
    	this.surname=surname;
    	this.btAddress=btAddress;
        if (isPhonenumber(phoneNumber)){
            this.phoneNumber = phoneNumber;
        }
        else{
        	this.phoneNumber="";
            //throw new GooseException("Invalid Phone Number: "+phoneNumber);
        }
        setTimeStamp();
        this.score=0;
    }
    
    public Contact (Storage db, int id, String name, String surname, String phoneNumber, String btAddress){
        this.id=id;
        this.name=name;
        this.surname=surname;
        this.phoneNumber=phoneNumber;
        this.btAddress=btAddress;
        this.timeStamp=GooseTools.getTime();
        this.score=0; 	
    }
    
    public Contact(Storage db, int id, String contactDetails){
    	try{
    		String [] contactInfo = readSearchContactMessageValue(contactDetails);
    		this.id=id;
    		this.name=contactInfo[0];
    		this.surname=contactInfo[1];
    		this.phoneNumber=contactInfo[2];
    		this.btAddress=contactInfo[3];
            this.timeStamp=GooseTools.getTime();
    		this.score=0;    		
    	}
    	catch(Exception e){
//    		//log.debug("Error retrieving contact info");
    	}
    }
    
    private String [] readSearchContactMessageValue (String content){
    	String [] returnValues = new String[4];
    	
    	int nameIndexStart = content.indexOf(GooseMessage.name, 0)+GooseMessage.name.length();
    	int nameIndexEnd = content.indexOf("|", 0);
    	returnValues[0] = content.substring(nameIndexStart, nameIndexEnd);
    	
    	
    	int surnameIndexStart = content.indexOf(GooseMessage.surname, 0)+GooseMessage.surname.length();
    	int surnameIndexEnd = content.indexOf("|", surnameIndexStart);
    	returnValues[1] = content.substring(surnameIndexStart, surnameIndexEnd);    	
    	
    	int phoneNumberIndexStart = content.indexOf(GooseMessage.phoneNumber, 0)+GooseMessage.phoneNumber.length();
    	int phoneNumberIndexEnd =  content.indexOf("|", phoneNumberIndexStart);
    	returnValues[2] = content.substring(phoneNumberIndexStart,phoneNumberIndexEnd);     	
    	
    	int btAddrIndexStart = content.indexOf(GooseMessage.btAddress, 0)+GooseMessage.btAddress.length();
    	int btAddrIndexEnd = content.indexOf("|", btAddrIndexStart);
    	returnValues[3] = content.substring(btAddrIndexStart, btAddrIndexEnd);

    	return returnValues;
    }
    
    public int getId(){
        return this.id;
    }

    public String getName(){
        return this.name != null ? this.name : ""; 
    }

    public String getSurname(){
        return this.surname != null ? this.surname : ""; 
    }

    public String getPhoneNumber(){
        return this.phoneNumber != null ? this.phoneNumber : ""; 
    }

    public String getBluetoothAddress(){
        return this.btAddress != null ? this.btAddress : ""; 
    }

    public long getTimeStamp(){
        return this.timeStamp; 
    }

    public int getScore(){
        return this.score;
    }
    
    public String getConnectionURL() throws GooseException{
        if (this.connectionURL==null){
        	throw new GooseException("No connectionURL available for this contact");
        }
        return this.connectionURL;
    }

    public void setId(int id){
        this.id = id;
    }

    public void setName(String name){
        this.name=name.toLowerCase();
    }

    public void setSurname(String surname){
        this.surname=surname.toLowerCase();
    }

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
    }
    
    
    public void setPhonenumber(String phoneNumber) throws GooseException{
        //J2ME does not support regular expressions. So we need to do it on our own.
    	if(isPhonenumber(phoneNumber)){
    		this.phoneNumber=phoneNumber;
    	}
        else{
            this.phoneNumber = "";
            throw new GooseException("Phone number not valid");
        }
    }

    public void setBluetoothAddress (String btAddress){
        this.btAddress = btAddress.toLowerCase();
    }
    
    public void setTimeStamp(){
        this.timeStamp=GooseTools.getTime();
    }

    public void setScore(int score){
        this.score = score;
    }
    
    public void setConnectionURL(String connectionURL){
        this.connectionURL=connectionURL;
    }

    public void writeObject (IOutputStream out){
        //It must be auto-increased. Should we add the index?
        out.writeInt(id);
        out.writeString(name);
        out.writeString(surname);
        out.writeString(phoneNumber);
        out.writeString(btAddress);
        out.writeLong(timeStamp);
        out.writeInt(score);
    }

    public void readObject (IInputStream in){
        id = (int) in.readInt();
        name = (String)in.readString();
        surname = (String)in.readString();
        phoneNumber = (String) in.readString();
        btAddress = (String) in.readString();
        timeStamp = (long) in.readLong();
        score = (int) in.readInt();
    }

    public boolean supportsBT(){
        return this.btAddress != null && this.btAddress!= "" ? true : false; 
    }
    
    public boolean supportsGSM(){
    	return true;
    }
    
    /*
     * This method converts a full contact into a string in which the fields are separated by ':'
     */
    public String toString(){
    	String btAddress = this.btAddress;
    	String phoneNumber = this.phoneNumber;
    	if (this.btAddress.equalsIgnoreCase("")){
    		btAddress="none";
    	}
    	if (this.phoneNumber.equalsIgnoreCase("")){
    		phoneNumber="none";
    	}
    	
        return GooseMessage.name+this.name+"|"+GooseMessage.surname+this.surname+"|"+GooseMessage.phoneNumber+phoneNumber+"|"+GooseMessage.btAddress+btAddress+"|";
    }
}
