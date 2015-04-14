package gui.goose.menus;



import goose.exceptions.GooseSearchException;
import goose.forwardingManager.GooseMessage;
import goose.mainManager.GooseTools;
import goose.contactsManager.Contact;
import goose.contactsManager.ContactsManager;
import gui.goose.resources.SourceFiles;
import gui.goose.resources.UIGooseView;
import microlog.Logger;
import java.util.Date;
import java.util.Vector;
import javax.microedition.lcdui.*;





public class UIGooseInOutMessagesMenuView extends Form implements UIGooseView{

	private final int END_INDEX = 25;
	private final int BEGIN_INDEX = 0;
	
	private Display display = null;
	
	//Debuggin
	private Logger log = Logger.getLogger();
	private int typeMenu;
	private List vList = null;
	public Vector vMessages = null;
	
	public UIGooseInOutMessagesMenuView(UIGooseManager uiGooseManager){
		super(null);
		
		display = Display.getDisplay(uiGooseManager);
		vList = new List("", Choice.IMPLICIT);
		
		vList.addCommand(SourceFiles.backCommand);
		//vList.addCommand(SourceFiles.logCommand);
		vList.setCommandListener(uiGooseManager);
		typeMenu = uiGooseManager.viewIdentifier;
		
		vMessages =  new Vector();
		switch(uiGooseManager.viewIdentifier) {
		case SourceFiles.MESSAGES_INBOX:
			////log.debug("UIGoose. Entering in Inbox messages");
			vList.setTitle("Received messages");
			try {
				vMessages = uiGooseManager.gooseManager.getInboxMessages();
			}catch (GooseSearchException e) {
				e.printStackTrace(); 
				uiGooseManager.viewIdentifier = SourceFiles.MESSAGES_MENU;
				uiGooseManager.backViewIdentifier = SourceFiles.MAIN_MENU;
				uiGooseManager.backObject = new UIGooseMainMenuView(SourceFiles.MAIN_MENU, uiGooseManager,
						 uiGooseManager.display, uiGooseManager.width, uiGooseManager.height, uiGooseManager);
				uiGooseManager.currentObject= new UIGooseMessageMainMenuView(uiGooseManager);
				uiGooseManager.currentObject.show();
				//display.setCurrent(GooseAlert.createAlert("Get inBox messages error " 
					//	+ e.getMessage()), (UIGooseInOutMessagesMenuView)uiGooseManager.currentObject);
			}
			if (vMessages.size() == 0) {
//				//log.debug("UIGoose. NO messages when trying go inside Folder InBox number messages: "+vMessages.size());
				vList.append("No messages", null);
				show();
				return;
			}
			fillBoxMessages(uiGooseManager);
			break;
		case SourceFiles.MESSAGES_OUTBOX:
			////log.debug("UIGoose. Entering in Outbox messages");
			vList.setTitle("Sent messages");
			try {
				vMessages = uiGooseManager.gooseManager.getSentMessages();
			}catch (GooseSearchException e) {
				//e.printStackTrace(); 
				uiGooseManager.viewIdentifier = SourceFiles.MESSAGES_MENU;
				uiGooseManager.backViewIdentifier = SourceFiles.MAIN_MENU;
				uiGooseManager.backObject = new UIGooseMainMenuView(SourceFiles.MAIN_MENU, uiGooseManager,
						 uiGooseManager.display, uiGooseManager.width, uiGooseManager.height, uiGooseManager);
				uiGooseManager.currentObject= new UIGooseMessageMainMenuView(uiGooseManager);
				uiGooseManager.currentObject.show();
				//display.setCurrent(GooseAlert.createAlert("Get OutBox messages error " 
					//	+ e.getMessage()), (UIGooseMainMenuView)uiGooseManager.currentObject);
			}
			if (vMessages.size() == 0) {
//				//log.debug("UIGoose. NO messages when trying go inside Folder OutBox  number messages: "+vMessages.size());
				vList.append("No messages", null);
				show();
				return;
			}
			fillBoxMessages(uiGooseManager);
			break;
		}
		//System.out.println("before read inOut box messages");
		
	
	}
	
	
	public void destroy() {
		// TODO Auto-generated method stub
		vList.deleteAll();
	}

	public void show() {
		// TODO Auto-generated method stub
		display.setCurrent(vList);
	}
	
	public GooseMessage getSelectedMessage(int index) {
		return (GooseMessage)vMessages.elementAt(index);
	}
	
	private void fillBoxMessages(UIGooseManager uiGooseManager) {
		
		//*********************************************
		// ADD contact if not in list
		//**********************************
		////log.debug("UIGoose. Inside BoxMessages number of messages: "+ vMessages.size());
		Image imageText = null;
		Image imageVoice = null;
		try {
			imageText = Image.createImage(SourceFiles.SOURCE_TEXT);
			imageVoice = Image.createImage(SourceFiles.SOURCE_VOICE);
		}catch (Exception e) {
			e.printStackTrace();
			//log =  Logger.getLogger();
			////log.debug("Imagen error in inBox (Messages)" + e.getMessage());
		}
		
		String contentMessage;
		String timeMessage;
		
		Image imageMessage = imageText;
		GooseMessage gooseMessage;
		for (int i = 0; i < vMessages.size(); i++ ) {
			gooseMessage = ((GooseMessage)vMessages.elementAt(i));
	
			if (gooseMessage.contentType == GooseMessage.CONTACT_PROFILE_ACK ||
					gooseMessage.contentType == GooseMessage.CONTACT_PROFILE) {
				continue;
			}
			
			/*
			////log.debug("UIGoose. message number: "+i+" type "+(gooseMessage.contentType));
			if (END_INDEX > (gooseMessage.content).length()) {
				contentMessage = (gooseMessage.content);
				////log.debug("content message: "+ contentMessage);
			}
			else {
				contentMessage = (gooseMessage.content).substring(BEGIN_INDEX, END_INDEX);
			}
			*/
			//((GooseMessage)vMessages).
			if (gooseMessage.contentType.equalsIgnoreCase(GooseMessage.TEXT)) {
				imageMessage  = imageText;
				contentMessage = gooseMessage.content;
			}
			else {//if(gooseMessage.contentType.equalsIgnoreCase(GooseMessage.AUDIO)) {
				imageMessage = imageVoice;
				contentMessage = "";
			}
			if (typeMenu == SourceFiles.MESSAGES_INBOX) {
				timeMessage = getTimeMessage(gooseMessage, uiGooseManager);
					vList.append(gooseMessage.senderName.toUpperCase() +" "+ timeMessage +"\n"+ contentMessage, 
							imageMessage);
					if (gooseMessage.newMessage) {
						////log.debug("New message unreaded");
						vList.setFont(i, SourceFiles.fontUnread);
					}
				}
			else {
				try{
				Vector recipientDetails = uiGooseManager.gooseManager.searchContact(gooseMessage.recipient,
						ContactsManager.BTADDRESS);
				vList.append(((Contact)recipientDetails.elementAt(0)).getName()+"\n" + contentMessage, 
						imageMessage);
				}
				catch(Exception e){
					vList.append(gooseMessage.recipient+ "\n" + contentMessage, 
							imageMessage);
					}
				}
			/*
			else{
				//log.debug("Unknown message");
			}*/
			
		}
		////log.debug("UIGoose. Exit BoxMessages");
	}

	private String getTimeMessage(GooseMessage gooseMessage, UIGooseManager uiGooseManager) {
		
		long timeMessage;
		timeMessage =( GooseTools.getTime() - gooseMessage.recTime)  / 1000;
		//log.debug("UIGoose. Time message: "+ timeMessage);
		String time = "";
		int days =0;
		int hours = 0;
		int minutes = 0;
		if (timeMessage<60){
			time=timeMessage+"s";
		}
		else if (timeMessage>=60 && timeMessage <=60*60){
			minutes = (int)timeMessage/60;
			time=minutes+"m";
		}
		else if (timeMessage>=60*60 && timeMessage<=60*60*24){
			hours = (int)timeMessage/(60*60);
			time = hours+"h";
		}
		else{
			days = (int)timeMessage/(24*60*60);
			time=days+" days ";
		}
		time+= " ago";
		return time;
	}

}
