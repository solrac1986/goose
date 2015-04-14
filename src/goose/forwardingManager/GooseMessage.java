/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package goose.forwardingManager;

import org.garret.perst.Persistent;
import org.garret.perst.IOutputStream;
import org.garret.perst.IInputStream;
import org.garret.perst.Storage;

/**
 *
 * @author Narseo
 */
public class GooseMessage extends Persistent{
    public int messageId;
    public String senderName;
    public String senderSurname;
    public String senderPhoneNumber;
    public String senderBTMacAddress;
    public String recipient;
    public String transmissionMode;
    public long TTL; //ms
    public int priority; //1 to 5 scale
    public String GUID;
    public String previousRelays;
    public String contentType;
    public String content;
    public String folder;
    public boolean newMessage;
    public long recTime; //recTime = -1 until it was received. It must be updated
    
    //recipient. To nofity that the received message over bluetooth is for us
    public static final String LOCAL = "LOCAL";
    
    //transmission mode
    public static final String UCAST = "UCAST";
    public static final String MCAST = "MCAST";
    public static final String BCAST = "BCAST";
    public static final String FRIEND_SEARCH = "FRIEND_SEARCH";
    public static final String FRIEND_SEARCH_ACK = "FRIEND_SEARCH_ACK";
    public static final String CONTACTS_EXCHANGE_ACK = "CONTACTS_ACK";
    public static final String CONTACTS_EXCHANGE = "CONTACTS";
    public static final String STATUS_UPDATE = "STATUS";
    public static final String CONTACT_PROFILE_ACK = "PROFILE_ACK";
    public static final String CONTACT_PROFILE = "PROFILE";


    
    //PRIORITY
    public static final int VERY_LOW = 1;
    public static final int LOW = 2;
    public static final int MEDIUM = 3;
    public static final int HIGH = 4;
    public static final int VERY_HIGH = 5;

    //FOLDER
    public static final String INBOX_FOLDER = "in";
    public static final String SENT_FOLDER = "st";
    public static final String FORWARD_FOLDER = "fw";
    public static final String ALL_FOLDERS ="al";
    public static final String INBOX_SENT_FOLDERS = "is";
    public static final String INBOX_FORW_FOLDERS = "if";
    public static final String SENT_FORWD_FOLDERS = "sf";
    public static final String STATUS_UPDATE_FOLDERS = "su";
    
    //ContentType
    public static final String TEXT = "text/plain";
    public static final String CONTACTS = "text/contacts";
    public static final String AUDIO = "audio";
    public static final String SEARCH = "text/search";
    
    //TTL
	public static final long DEFAULT_UCAST_TTL=20*60*1000; 
	public static final long DEFAULT_MCAST_TTL=30*60*1000; 
	public static final long DEFAULT_BCAST_TTL=30*60*1000; 
	
	//ContactsMessage strings
	public static final String name = "name:";
	public static final String surname = "surname:";
	public static final String phoneNumber = "phonenumber:";
    public static final String btAddress = "btaddress:";
    public static final String connectionURL ="connurl:";

    /*
     * A default constructor is needed for Perst to be able to instantiate instances of loaded objects
     */
    public GooseMessage (){}
    
    //TODO, Initialize all the fields as well
    public GooseMessage (Storage db, String [] dataInput, boolean newMessage, long recTime){
        super(db);
        this.messageId = Integer.parseInt(dataInput[0]);
        this.senderName = dataInput[1].toLowerCase();
        this.senderSurname = dataInput[2].toLowerCase();
        this.senderPhoneNumber = dataInput [3].toLowerCase();
        this.senderBTMacAddress = dataInput [4].toLowerCase();
        this.recipient = dataInput [5].toLowerCase();
        this.transmissionMode = dataInput[6].toLowerCase();
        this.TTL = Long.parseLong(dataInput[7]);
        this.priority = Integer.parseInt (dataInput[8]);
        this.GUID = dataInput[9].toLowerCase();
        this.previousRelays = dataInput[10].toLowerCase();
        this.contentType = dataInput[11].toLowerCase();
        this.content = dataInput[12].toLowerCase();
        this.folder = dataInput[13].toLowerCase();
        this.newMessage=newMessage;
        this.recTime=recTime;
    }
    
    public void writeObject (IOutputStream out){
        //It must be possible to increase the index automatically by using FieldIndex.append that can be found on the reflection library perst-jsr75-reflect.java
        out.writeInt(messageId);
        out.writeString(senderName);
        out.writeString(senderSurname);
        out.writeString(senderPhoneNumber);
        out.writeString(senderBTMacAddress);
        out.writeString(recipient);
        out.writeString(transmissionMode);
        out.writeLong(TTL);
        out.writeInt(priority);
        out.writeString(GUID);
        out.writeString(previousRelays);
        out.writeString(contentType);
        out.writeString(content);
        out.writeString(folder);
        out.writeBoolean(newMessage);
        out.writeLong(recTime);
    }

    public void readObject(IInputStream in){
        messageId = in.readInt();
        senderName = in.readString();
        senderSurname = in.readString();
        senderPhoneNumber = in.readString();
        senderBTMacAddress = in.readString();
        recipient = in.readString();
        transmissionMode = in.readString();
        TTL = in.readLong();
        priority = in.readInt();
        GUID = in.readString();
        previousRelays = in.readString();
        contentType = in.readString();
        content = in.readString();
        folder = in.readString();
        newMessage=in.readBoolean();
        recTime = in.readLong();
    }
}
