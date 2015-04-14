package goose.mainManager;


import goose.forwardingManager.GooseMessage;
import goose.forwardingManager.MessageTools;

public class IncomingMessageEvent {
	
	private int type;
	private GooseMessage m;
	private String alertMessage;
	
	public static final int UCAST_MESSAGE = 0;
	public static final int MCAST_MESSAGE = 1;
	public static final int UCAST_SMS_MESSAGE=2;
	public static final int BCAST_MESSAGE=3;
	public static final int CONTACT_DETAILS_EXCHANGE = 4;
	public static final int FRIEND_SEEK_ACK = 5;
	public static final int CONTACT_DETAILS_EXCHANGE_ACK = 6;
	public static final int STATUS_UPDATE = 7;
	public static final int CONTACT_PROFILE_ACK = 8;
	public static final int CONTACT_PROFILE = 9;
	
	public static final int BT_MSG = 6;
	

	public IncomingMessageEvent(int type, byte [] serializedMessage){
		this.type = type;
		this.alertMessage=null;
		try{
			this.m = MessageTools.deserializeMessage(serializedMessage);
		}
		catch(Exception e){
			this.m = null;
		}
	}
	
	public IncomingMessageEvent(int type, GooseMessage m){
		this.type = type;
		this.m = m;
		this.alertMessage=null;
	}
	
	public IncomingMessageEvent(int type, String alertMessage, GooseMessage m){
		this.type = type;
		this.alertMessage=alertMessage;
		this.m=m;
	}
	
	public IncomingMessageEvent(int type, String alertMessage){
		this.type=type;
		this.alertMessage=alertMessage;
		this.m=null;
	}
	
	public byte[] getData(){
		try{
			return MessageTools.serializeMessage(m);			
		}
		catch(Exception e){
			return null;
		}
	}
	
	public String getAlertMessage(){
		return this.alertMessage;
	}
	
	public GooseMessage getMessage(){
		return this.m;
	}
	
	public int getType(){
		return this.type;
	}
	
	
}
