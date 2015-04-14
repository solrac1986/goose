package gui.goose.menus;

import java.util.Date;
import java.util.Vector;

import goose.exceptions.GooseException;
import goose.exceptions.GooseNetworkException;
import goose.forwardingManager.GooseMessage;
import gui.goose.exceptions.GooseAlert;
import gui.goose.resources.UIRecordStoreTesting;



public class UIGooseThreadSendMessage implements Runnable{

	private UIGooseManager uiGooseManager;
	private Vector vSendTo = null;
	private String txMode;
	private int value;
	private String contentType;
	private String content;
	
	
	public UIGooseThreadSendMessage(UIGooseManager uiGooseManager, Vector vSendTo, String txMode, int value, 
			String contentType, String content) {
		this.vSendTo = vSendTo;
		this.txMode = txMode;
		this.value = value;
		this.contentType = contentType;
		this.content = content;
		this.uiGooseManager = uiGooseManager;
	}
	
	
	public void run() {
		// TODO Auto-generated method stub
		setPriority();
		UIGooseManager.onBluetooth = false;
	}
	
	private void setPriority() {
		
		GooseMessage gooseMessage = new GooseMessage();
		UIGooseManager.onBluetooth = true;
		String stringData;
		byte[] data;
		Date date = new Date();
		String aux;
		
		if (txMode.equalsIgnoreCase(GooseMessage.UCAST)){
			aux = UIRecordStoreTesting.UCAST;
		}
		else if (txMode.equalsIgnoreCase(GooseMessage.MCAST)){
			aux = UIRecordStoreTesting.MCAST;
		}
		else if (txMode.equalsIgnoreCase(GooseMessage.STATUS_UPDATE)){
			aux = UIRecordStoreTesting.STATUS_MESSAGE;
		}
		else {
			aux = UIRecordStoreTesting.BCAST;
		}

		try {
			gooseMessage = uiGooseManager.gooseManager.createNewMessage(vSendTo,
					txMode, value, contentType, content);
			stringData = UIRecordStoreTesting.SEPARATOR + UIRecordStoreTesting.TYPE + UIRecordStoreTesting.MESSAGE_SENT;
			stringData+= UIRecordStoreTesting.SEPARATOR;
			stringData += UIRecordStoreTesting.TIME + date.getTime();
			stringData+= UIRecordStoreTesting.SEPARATOR;
			stringData += UIRecordStoreTesting.TIME_SENT + date.getTime();
			stringData+= UIRecordStoreTesting.SEPARATOR;
			stringData += UIRecordStoreTesting.MESSAGE_TYPE + aux;
			stringData+= UIRecordStoreTesting.SEPARATOR;
			stringData += UIRecordStoreTesting.NAME_IN + gooseMessage.recipient;
			stringData+= UIRecordStoreTesting.SEPARATOR;
			stringData += UIRecordStoreTesting.NAME_OUT + gooseMessage.senderBTMacAddress;
			stringData+= UIRecordStoreTesting.SEPARATOR;
			stringData += UIRecordStoreTesting.PREVIOUS_RELAYS+ gooseMessage.previousRelays;
			stringData+= UIRecordStoreTesting.SEPARATOR;
			stringData += UIRecordStoreTesting.SIZE_MESSAGE+ gooseMessage.length();
			stringData+= UIRecordStoreTesting.SEPARATOR;
			stringData += UIRecordStoreTesting.SIZE_CONTENT + gooseMessage.content.length();
			stringData+= UIRecordStoreTesting.SEPARATOR;
			stringData += UIRecordStoreTesting.SIZE_HEADER + (gooseMessage.length() - gooseMessage.content.length());
			data = stringData.getBytes();
			//log.debug("Message Ucast: "+stringData);
			UIRecordStoreTesting.writeTrace(data);
		}catch (Exception e) {
			
			uiGooseManager.display.setCurrent(GooseAlert.createAlert("UIGoose.Create " 
					+ e.getMessage()), (UIGooseMessageMainMenuView)uiGooseManager.currentObject);
		}
		
		try {
			//Thread threadGooseSend = new Thread();
			uiGooseManager.gooseManager.send(vSendTo, gooseMessage);
		}catch (Exception e) {
			e.printStackTrace();
			uiGooseManager.display.setCurrent(GooseAlert.createAlert("UIGoose.Sending" 
					+ e.getMessage()), (UIGooseMessageMainMenuView)uiGooseManager.currentObject);
			
		}
		uiGooseManager = null;
	}

}
