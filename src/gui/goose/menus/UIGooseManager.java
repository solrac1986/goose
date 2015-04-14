package gui.goose.menus;

import java.io.InputStream;
import java.util.Date;
import java.util.Vector;


import microlog.Logger;
import microlog.appender.FormAppender;
import goose.contactsManager.*;
import gui.goose.resources.*;


import goose.exceptions.GooseException;
import goose.exceptions.GooseNetworkException;
import goose.forwardingManager.GooseMessage;
import goose.forwardingManager.MessageTools;
import goose.mainManager.*;
import goose.networkManager.bluetooth.Bluetooth;
import gui.goose.exceptions.GooseAlert;

import javax.bluetooth.ServiceRecord;

import javax.microedition.lcdui.*;
import javax.microedition.midlet.MIDlet;
import javax.microedition.midlet.MIDletStateChangeException;




public class UIGooseManager extends MIDlet implements  Runnable, CommandListener {
	
	public static boolean onBluetooth = false;
	
	
	//Painting focus menu
	public static int focus = 0;
	
	// Save personal profile
	//public RecordStore recordStore = null;
	
	//Debuggin
	public Logger log = null;
	public Form formlog;
	private boolean alive = true;
	
	// end DEbuggin *****
	
	//********* Testing and trace ********
	public javax.microedition.io.Connection c = null;
	public java.io.OutputStream os;
	public java.io.InputStream is;
	public  javax.microedition.io.file.FileConnection fc= null;
	
	
	//**************************************
	
	
	
	private String currentMenu;
	//private Command okCmd;
	
	public  int viewIdentifier;
	public int backViewIdentifier;
	public Vector contacts = null;
	public int index;
	
	//Messages
	public Vector vMessages = null;
	//Seek friend
	public String contentMessage;
	
	public Contact contactChat;
	
	//ServiceRecord send profile request
	public ServiceRecord serviceRecordProfileRequest;
	public String userID = "";
	
	private boolean bLogin = false;
	public boolean bIsLoggedBefore = false;
	public GooseManager gooseManager;

	public UIGooseRecordStore uiGooseRecordStore;
	
	public static long timeStampBegin;
	
	private StringItem stringItem;
	
	//Alert type in PUSH
	public int alertType = 0;
	public GooseMessage gooseMessage = null;
	
	
	public  int width;
	public  int height;
	public  boolean screenSize;
	
	//Screen
	public UIGooseView currentObject;
	public UIGooseView backObject;
	
	public UIGooseContactMainMenuView uiMenuContactView = new UIGooseContactMainMenuView(this);
	
	public  Display display;
	
	
	public UIGooseManager() {
		// TODO Auto-generated constructor stub
		
		
		
		//********************************LOG*************
		//log =  Logger.getLogger();
		//currentMenu ="";
		// stringItem = new StringItem("GooseTest", "BluetoothUT");
	//	formlog = new Form(null, new Item[] {stringItem});
	//	FormAppender fa = new FormAppender();
	//	fa.setLogForm(formlog);
	//	log.addAppender(fa);
		
		
		//*********************end log ******************
		
		
		
		
		gooseManager = new GooseManager();
		
		
	}

	public void destroyApp(boolean arg0) throws MIDletStateChangeException {
		// TODO Auto-generated method stub
		alive = false;
		display = null;
		uiGooseRecordStore.closeAll();
		
		try {
			this.gooseManager.stopManager();
			//UIRecordStoreTesting.readTrace(this);
			UIRecordStoreTesting.closeRecordStoreTesting();
		}catch (Exception e) {
			//log.debug("Error when shutting down gooseManager from MIDLET " +e.getMessage());
			display.setCurrent(GooseAlert.createAlert("Log out error" + e.getMessage()),
					null);
		}
		
		try {
			 if (os != null) {
				 os.close();
			 }
			 if (c != null) {
				 c.close();
			 }
		 }catch(Exception ex){
			 //ex.printStackTrace();
			 //log.debug("UIGoose. Error when writing the file: "+ex.getMessage());
		 }
		notifyDestroyed();
	}

	protected void pauseApp() {
		// TODO Auto-generated method stub
		//display = null; 
		//pause();
		notifyPaused();
		this.pause();
	}

	protected void startApp() throws MIDletStateChangeException {
		// TODO Auto-generated method stub
		//display = Display.getDisplay(this);
		
		
		timeStampBegin = GooseTools.getTime();
		
		UISplashScreen uiSplashScreen = new UISplashScreen();
		uiSplashScreen.createClass(this);
		display = Display.getDisplay(this);
		display.setCurrent(uiSplashScreen);
		uiGooseRecordStore = new UIGooseRecordStore();
		//Testing application
		UIRecordStoreTesting.openRecordStoreTesting();
		
		try {
			Thread.sleep(1000);
		}catch (Exception e) {
			//log.debug("Splash wait error" + e.getMessage());
		}
		
		uiSplashScreen = null;
		
		UIGooseSelectProfile uiGooseSelectProfile = new UIGooseSelectProfile(this);
		
		uiGooseRecordStore.setCurrentMenu(SourceFiles.SELECT_PROFILE_MENU);
		
		byte[] dataReturn = uiGooseRecordStore.readAllProfiles(userID, 
				SourceFiles.RECORD_STORE_ALL_PROFILE);
		//byte[] dataReturn = this.uiGooseRecordStore.readRecordStore();
		/*if (dataReturn == null) {
			Contact contact = null;
			viewIdentifier = SourceFiles.LOGIN_MENU;
			UIGooseFieldTextMenuView uiGooseField = new UIGooseFieldTextMenuView(viewIdentifier, 
					this,  display, contact);
			currentObject = uiGooseField;
			uiGooseSelectProfile = null;

		}
		else {*/
		viewIdentifier = SourceFiles.SELECT_PROFILE_MENU;
		currentObject = uiGooseSelectProfile;
		//}
		currentObject.show();
		
		Thread thGooseManager = new Thread(this);
		thGooseManager.start();
		//run();
		
	}

	
	public void pause()  {
		// TODO Auto-generated method stub
		try {
			this.wait();
		}
		catch (Exception e) {
			display.setCurrent(GooseAlert.createAlert("Pause midlet error" + e.getMessage()),
					null);
			System.exit(-1);
		}
	}

	
	
	
	public void run () {
		System.out.println("function run before login login value: " +bLogin);
		//display.setCurrent(form);
		while ( !bLogin) {
			
		}
		System.out.println("function run after login login value: " +bLogin);
		
		//((UIGooseFieldTextMenuView)currentObject).destroy();
		try{
			gooseManager.startManager();
		}
		catch(Exception e){
			//TODO: Handle the exception in case it was impossible to run the manager
			//log.debug("Error starting gooseManager from MIDLET : "+e.getMessage());  
			Form form = new Form("Error");
			//Alert alert = new Alert("Error", "Switch ON the Bluetooth", null, AlertType.ERROR);
			//display.setCurrent(alert, null);
			UICustomImage uiCustomImage = new UICustomImage("Switch ON the Bluetooth", SourceFiles.SOURCE_PHONE);
			ImageItem imageItem = uiCustomImage.getImageItem();
			form.append(imageItem);
			display.setCurrent(form);
			
			try {
				Thread.sleep(3000);
				//destroyApp(true);
				display = null;
				notifyDestroyed();
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				//log.debug("Error closing  MIDLET : "+e1.getMessage());  
			}
		}
		
		
		if (bIsLoggedBefore == false) {
			backViewIdentifier = SourceFiles.MAIN_MENU;
			viewIdentifier = SourceFiles.PERSONAL_PROFILE;
			currentObject = new UIGoosePersonalProfileMenuView(this);
			
		}
		else {
			viewIdentifier = SourceFiles.MAIN_MENU;
			currentObject = new UIGooseMainMenuView(SourceFiles.MAIN_MENU, this,
					display, width, height, this);

		}
		
		currentObject.show();

		
		while (alive){
			//System.out.println("Thread running");
			if (gooseManager.eventStack.isModified()){
				try {
					
					IncomingMessageEvent newEvent = gooseManager.eventStack.getLastEvent();
					////log.debug("New "+newEvent.getAlertMessage()+" event ready to be processed by the GUI");
					handleIncomingMessageEvent(newEvent);
				} catch (GooseException e) {
					// TODO Auto-generated catch block
					//log.debug(e.getMessage());
				}
			}
			
			try{
				Thread.sleep(50);
			}catch(Exception e){
				//log.debug(e.getMessage());
			}
			
		}	
	}
	
	public void handleIncomingMessageEvent(IncomingMessageEvent newEvent){
		//log.debug("New incoming message event: "+newEvent.getAlertMessage());
		 currentObject = new UIGooseMainMenuView(SourceFiles.MAIN_MENU, this,
					display, width, height, this);
		 viewIdentifier = SourceFiles.MAIN_MENU;
		 currentObject.show();
		 byte[] data;
		 String stringData;
		 Date date = new Date();
		 AlertType alertDisplay = AlertType.CONFIRMATION;
		String alertMessage = null;
		 switch(newEvent.getType()){
		case IncomingMessageEvent.UCAST_MESSAGE:
			gooseMessage = newEvent.getMessage();
			alertType = IncomingMessageEvent.UCAST_MESSAGE;
			alertMessage = newEvent.getAlertMessage();
			alertDisplay = AlertType.CONFIRMATION;
			stringData = UIRecordStoreTesting.SEPARATOR + UIRecordStoreTesting.TYPE + UIRecordStoreTesting.NEWEVENT;
			stringData+= UIRecordStoreTesting.SEPARATOR;
			stringData += UIRecordStoreTesting.TIME + date.getTime();
			stringData+= UIRecordStoreTesting.SEPARATOR;
			stringData += UIRecordStoreTesting.TIME_SENT + gooseMessage.recTime;
			stringData+= UIRecordStoreTesting.SEPARATOR;
			stringData += UIRecordStoreTesting.MESSAGE_TYPE + UIRecordStoreTesting.UCAST;
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
			break;
		case IncomingMessageEvent.MCAST_MESSAGE:
			gooseMessage = newEvent.getMessage();
			alertType = IncomingMessageEvent.MCAST_MESSAGE;
			alertMessage = newEvent.getAlertMessage();
			////log.debug("Alert created for a MCAST message");
			alertDisplay = AlertType.CONFIRMATION;
			stringData =UIRecordStoreTesting.SEPARATOR + UIRecordStoreTesting.TYPE + UIRecordStoreTesting.NEWEVENT;
			stringData+= UIRecordStoreTesting.SEPARATOR;
			stringData += UIRecordStoreTesting.TIME + date.getTime();
			stringData+= UIRecordStoreTesting.SEPARATOR;
			stringData += UIRecordStoreTesting.TIME_SENT + gooseMessage.recTime;
			stringData+= UIRecordStoreTesting.SEPARATOR;
			stringData += UIRecordStoreTesting.MESSAGE_TYPE + UIRecordStoreTesting.MCAST;
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
			//log.debug("Message Mcast: "+stringData);
			UIRecordStoreTesting.writeTrace(data);
			break;
		case IncomingMessageEvent.BCAST_MESSAGE:
			gooseMessage = newEvent.getMessage();
			alertType = IncomingMessageEvent.BCAST_MESSAGE;
			alertMessage = newEvent.getAlertMessage();
			////log.debug("Alert created for a BCAST message");
			////log.debug("UIGoose. message folder: "+ newEvent.getMessage().folder);
			alertDisplay = AlertType.CONFIRMATION;
			stringData =UIRecordStoreTesting.SEPARATOR + UIRecordStoreTesting.TYPE + UIRecordStoreTesting.NEWEVENT;
			stringData+= UIRecordStoreTesting.SEPARATOR;
			stringData += UIRecordStoreTesting.TIME + date.getTime();
			stringData+= UIRecordStoreTesting.SEPARATOR;
			stringData += UIRecordStoreTesting.TIME_SENT + gooseMessage.recTime;
			stringData+= UIRecordStoreTesting.SEPARATOR;
			stringData += UIRecordStoreTesting.MESSAGE_TYPE + UIRecordStoreTesting.BCAST;
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
			//log.debug("Message Bcast: "+stringData);
			UIRecordStoreTesting.writeTrace(data);
			break;
		case IncomingMessageEvent.STATUS_UPDATE:
			GooseMessage message = newEvent.getMessage();
			UIGooseFunction.updateGooseWall(message, this);
			alertType = IncomingMessageEvent.STATUS_UPDATE;
			alertMessage =message.senderName + " has updated the Goose status";
			alertDisplay = AlertType.INFO;
			stringData =UIRecordStoreTesting.SEPARATOR + UIRecordStoreTesting.TYPE + UIRecordStoreTesting.NEWEVENT;
			stringData+= UIRecordStoreTesting.SEPARATOR;
			stringData += UIRecordStoreTesting.TIME + date.getTime();
			stringData+= UIRecordStoreTesting.SEPARATOR;
			stringData += UIRecordStoreTesting.TIME_SENT + gooseMessage.recTime;
			stringData+= UIRecordStoreTesting.SEPARATOR;
			stringData += UIRecordStoreTesting.MESSAGE_TYPE + UIRecordStoreTesting.STATUS_MESSAGE;
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
			UIRecordStoreTesting.writeTrace(data);
			break;
		case IncomingMessageEvent.FRIEND_SEEK_ACK:
			alertType = IncomingMessageEvent.FRIEND_SEEK_ACK;
			alertMessage = newEvent.getAlertMessage();
			contentMessage = newEvent.getMessage().content + " " + ". Do you want store it?";
			alertDisplay = AlertType.CONFIRMATION;
			stringData =UIRecordStoreTesting.SEPARATOR + UIRecordStoreTesting.TYPE + UIRecordStoreTesting.NEWEVENT;
			stringData+= UIRecordStoreTesting.SEPARATOR;
			stringData += UIRecordStoreTesting.TIME + date.getTime();
			stringData+= UIRecordStoreTesting.SEPARATOR;
			stringData += UIRecordStoreTesting.TIME_SENT + gooseMessage.recTime;
			stringData+= UIRecordStoreTesting.SEPARATOR;
			stringData += UIRecordStoreTesting.MESSAGE_TYPE + UIRecordStoreTesting.FRIEND_SEARCH;
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
			UIRecordStoreTesting.writeTrace(data);
			break;
		case IncomingMessageEvent.CONTACT_DETAILS_EXCHANGE:
			alertType = IncomingMessageEvent.CONTACT_DETAILS_EXCHANGE;
			contentMessage = newEvent.getMessage().content;
			alertMessage = newEvent.getAlertMessage();
			alertDisplay = AlertType.CONFIRMATION;
			break;
		case IncomingMessageEvent.CONTACT_DETAILS_EXCHANGE_ACK:
			alertType = IncomingMessageEvent.CONTACT_DETAILS_EXCHANGE_ACK;
			contentMessage = newEvent.getMessage().content;
			alertMessage = newEvent.getAlertMessage();
			alertDisplay = AlertType.INFO;
			break;
		case IncomingMessageEvent.CONTACT_PROFILE:
			alertType = IncomingMessageEvent.CONTACT_PROFILE;
			contentMessage = newEvent.getMessage().content;
			alertMessage = newEvent.getAlertMessage();
			alertDisplay = AlertType.CONFIRMATION;
			break;
		case IncomingMessageEvent.CONTACT_PROFILE_ACK:
			alertType = IncomingMessageEvent.CONTACT_PROFILE_ACK;
			contentMessage = newEvent.getMessage().content;
			alertMessage = newEvent.getAlertMessage();
			alertDisplay = AlertType.CONFIRMATION;
			
			break;
		default:
			return;
		}

		display.vibrate(750);
		if (alertMessage != null) {
			////log.debug("Message Push alertType "+ alertType);
			Alert gooseAlert = GooseAlert.createAlert(alertMessage, alertDisplay);
			if (alertDisplay == AlertType.CONFIRMATION) {
				gooseAlert.addCommand(SourceFiles.OK_ALERT_COMMAND);
				gooseAlert.addCommand(SourceFiles.CANCEL_ALERT_COMMAND);
				gooseAlert.setCommandListener(this);
			}
			else if (alertType ==  IncomingMessageEvent.CONTACT_PROFILE_ACK || 
					alertType ==  IncomingMessageEvent.UCAST_MESSAGE || 
					  alertType ==  IncomingMessageEvent.MCAST_MESSAGE ||
					  alertType ==  IncomingMessageEvent.BCAST_MESSAGE) {
				gooseAlert.addCommand(SourceFiles.OK_ALERT_COMMAND);
				gooseAlert.setCommandListener(this);
			}
			display.setCurrent(gooseAlert, (UIGooseMainMenuView)currentObject);
			
		}
	}

	public void commandAction(Command cmd, Displayable disp) {
        // TODO Auto-generated method stub
       if (cmd == SourceFiles.okCommand) {
    	   okFunction();
       }
       else if (cmd == SourceFiles.pauseCommand) {
    	   pauseApp();
       }
       else if (cmd == SourceFiles.backCommand) {
    	   //Debuggin
    	   if (currentMenu.equals("debuggin")) {
    		   currentObject = new UIGooseMainMenuView(SourceFiles.MAIN_MENU, this,
						display, width, height, this);
    		   viewIdentifier=SourceFiles.MAIN_MENU;
    		   backViewIdentifier=SourceFiles.MAIN_MENU;
    		   currentObject.show();
    	   }
    	   else {
    		   backFunction();
    	  }
    	   
       }
       else if (cmd == SourceFiles.logCommand) {
    	   	formlog.addCommand(SourceFiles.backCommand);
			formlog.setCommandListener(this);
			currentMenu = "debuggin";
			viewIdentifier = SourceFiles.MAIN_MENU;
			display.setCurrent(formlog);
       }
       else if (cmd == SourceFiles.CANCEL_ALERT_COMMAND) {
    	   
    	   if (alertType == IncomingMessageEvent.FRIEND_SEEK_ACK) {
    		   currentObject.show();  
    	   }
    	   else if (alertType == SourceFiles.PROFILE_REQUEST) {
    		   viewIdentifier = SourceFiles.MESSAGES_NEW_TEXT;
				////log.debug("Creating new Menu new message");
				currentObject = new UIGooseNewMessageMenuView(this);
				((UIGooseNewMessageMenuView)currentObject).vSendTo.addElement(contactChat);
				((UIGooseNewMessageMenuView)currentObject).fillNewMessageText(this);
				currentObject.show();
    	   }
    	   else {
    		   currentObject.show();  
    	   }
    	   
       }
       else if (cmd == SourceFiles.OK_ALERT_COMMAND) {
    	  //log.debug("Request accepted type :" + alertType);
    	   //if (SourceFiles.MESSAGES_NEW_TEXT  == viewIdentifier || SourceFiles.MESSAGES_NEW_VOICE  == viewIdentifier) {
    		   
    	  // }
    	   //TODO: delete else if to messagepush when Firend SEEK Friend
    	  if (alertType == IncomingMessageEvent.FRIEND_SEEK_ACK ) {
    		   UIGooseFunction.addSeekFriend(this);
    	   }
    	   else if (alertType == IncomingMessageEvent.CONTACT_DETAILS_EXCHANGE || alertType == IncomingMessageEvent.CONTACT_DETAILS_EXCHANGE_ACK){
    		   UIGooseFunction.messagePush(alertType, this);
    	   }

    	   else if (alertType ==  IncomingMessageEvent.CONTACT_PROFILE_ACK) {
    		   UIGooseFunction.messagePush(alertType, this);
    	   }
    	   else  if (alertType ==  IncomingMessageEvent.UCAST_MESSAGE || 
					  alertType ==  IncomingMessageEvent.MCAST_MESSAGE ||
					  alertType ==  IncomingMessageEvent.BCAST_MESSAGE) {
    		   backObject = currentObject;
    		   backViewIdentifier = viewIdentifier;
    		   viewIdentifier = SourceFiles.MESSAGE_DETAIL_MENU;
    		   currentObject = new UIGooseMessageDetailsMenuView(this, SourceFiles.MESSAGES_INBOX, gooseMessage);;
    		   currentObject.show();
    	   }
    	   else if (alertType == IncomingMessageEvent.CONTACT_PROFILE){
    		   //log.debug("Contact Profile request received");
    		   UIGooseFunction.messagePush(alertType, this);
    	   }
    	   else if (alertType == SourceFiles.PROFILE_REQUEST) {
    		   // Exchange profile
    		  
    		   try {
    			   gooseManager.sendRequestPersonalProfile(this.serviceRecordProfileRequest);
    		   } catch (GooseNetworkException e) {
    			   // TODO Auto-generated catch block
    			   //log.debug("UIGoose. Error when sending profile request: "+e.getMessage());
    		   }
    		   currentObject.show();
    	   }
    	   
    	   
    	   else {    		   
    		   currentObject.show();
    	   }
    	   
       }
       
       else {
    	   indexList();
       }
       
    }

	 

	 public  void actionKey(int keyCode, Object object) {
		
		 switch(keyCode) {
		 case (-5):
			UIGooseManager.focus = 0;
			switch(viewIdentifier){
			case SourceFiles.MAIN_MENU:
				if (((UIGooseMainMenuView)currentObject).vMenu.size() != 0) {
					index =((UIGooseMainMenuView)currentObject).vMenu.indexOf((UICustomItem)object); 
				}
				break;
			case SourceFiles.CONTACT_MENU :
				if (((UIGooseContactMainMenuView)currentObject).vMenu.size() != 0) {
					index =((UIGooseContactMainMenuView)currentObject).vMenu.indexOf((UICustomItem)object); 
				}
				break;
			case SourceFiles.MESSAGES_MENU:
				if (((UIGooseMessageMainMenuView)currentObject).vMenu.size() != 0) {
					index =((UIGooseMessageMainMenuView)currentObject).vMenu.indexOf((UICustomItem)object); 
				}
				break;
			case SourceFiles.SEEK_MENU:
				if (((UIGooseSeekMainMenuView)currentObject).vMenu.size() != 0) {
					index =((UIGooseSeekMainMenuView)currentObject).vMenu.indexOf((UICustomItem)object); 
				}
				break;
			case SourceFiles.EXCHANGE_MENU:
				if (((UIGooseSearchMainMenuView)currentObject).vMenu.size() != 0) {
					index =((UIGooseSearchMainMenuView)currentObject).vMenu.indexOf((UICustomItem)object); 
				}
				break;
				
			case SourceFiles.CONTACT_DETAIL_MENU:
			   
			    ((UIGooseContactDetailsMenuView)currentObject).vMenu.indexOf((UICustomItem)object);
			  
			
			    UIGooseFunction.callEditMenu(this);
			    return;
			    //break;   
			
			case SourceFiles.CONTACT_EDIT_MENU:
				
				UIGooseFunction.deleteContact(this);
				return;
				//break;
			case SourceFiles.MESSAGES_NEW:
				if (((UIGooseNewMessageMenuView)currentObject).vMenu.size() != 0) {
					index =((UIGooseNewMessageMenuView)currentObject).vMenu.indexOf((UICustomItem)object); 
				}
				break;
			case SourceFiles.MESSAGES_NEW_TEXT:
				if (((UIGooseNewMessageMenuView)currentObject).vMenu.size() != 0) {
					index =((UIGooseNewMessageMenuView)currentObject).vMenu.indexOf((UICustomItem)object); 
				}
				break;
			case SourceFiles.MESSAGES_NEW_VOICE:
				if (((UIGooseNewMessageMenuView)currentObject).vMenu.size() != 0) {
					index =((UIGooseNewMessageMenuView)currentObject).vMenu.indexOf((UICustomItem)object); 
				}
				break;
			case SourceFiles.MESSAGE_DETAIL_MENU:
				if (((UIGooseMessageDetailsMenuView)currentObject).vMenu.size() != 0) {
					index =((UIGooseMessageDetailsMenuView)currentObject).vMenu.indexOf((UICustomItem)object); 
				}
				break;
			case SourceFiles.CONFIGURATION:
				if (((UIGooseConfigurationMenuView)currentObject).vMenu.size() != 0) {
					index =((UIGooseConfigurationMenuView)currentObject).vMenu.indexOf((UICustomItem)object); 
				}
				break;
				default:
					break;
			}
		 UIGooseFunction.menuSelect(this, index, object);
		 
		 break;
		 
		 default:
			 break;
		 }
}
	 
	 
	 public void okFunction() {
		 
		 
		 switch(viewIdentifier) {
		 
		case SourceFiles.LOGIN_MENU:
			 TextFieldValues txtFieldValues = ((UIGooseFieldTextMenuView)currentObject).getTextField();		 
			 
			 if (txtFieldValues.phoneNumber.length() != 0) {
				 
				 UIGooseFunction.setContact(this, txtFieldValues.name, txtFieldValues.surname, txtFieldValues.phoneNumber);
				 bLogin = true;
				 txtFieldValues=null;
			 }
			 else {
				 //Change the color of  the field error, incorrect phone number
				 display.setCurrent(GooseAlert.createAlert("Error in phone number", AlertType.INFO),
							(UIGooseFieldTextMenuView)currentObject);
				 bLogin = false;
			 }
			 
			 break;
		 case SourceFiles.CONTACT_SEARCH_MENU:
			 TextFieldValues txtFieldValuesSearch = ((UIGooseFieldTextMenuView)currentObject).getTextField();
			 
			 
			 if (txtFieldValuesSearch.phoneNumber!=null || txtFieldValuesSearch.name!=null
					 || txtFieldValuesSearch.surname!=null) {
				 Vector vSearch =  UIGooseFunction.searchFuncion(this, txtFieldValuesSearch.name, txtFieldValuesSearch.surname, txtFieldValuesSearch.phoneNumber);
				
				 backObject = null;
				 backObject = currentObject;
				 this.viewIdentifier = SourceFiles.CONTACT_SEARCH_RESULTS_MENU;
				 backViewIdentifier = SourceFiles.CONTACT_SEARCH_MENU;
				 UIGooseResultsContactMenuView uiResults = new UIGooseResultsContactMenuView(viewIdentifier, this, vSearch); 
				 
				 currentObject = uiResults;
				 currentObject.show();
			 }
			 else {
				 //Change the color of  the field error, incorrect phone number
			 }
			 txtFieldValuesSearch=null;
			 break;
			 
		 case SourceFiles.CONTACT_NEW_MENU:	 
			 TextFieldValues txtFieldValuesNew = ((UIGooseFieldTextMenuView)currentObject).getTextField();		 
		
			 if (txtFieldValuesNew.phoneNumber!=null) {
				 backObject = null;
				 backObject = currentObject;
				 UIGooseFunction.setContact(this, txtFieldValuesNew.name, txtFieldValuesNew.surname, txtFieldValuesNew.phoneNumber);
				 UIGooseContactsMenuView uiContactsMenu = new UIGooseContactsMenuView(viewIdentifier, this, display, contacts);
				 currentObject = uiContactsMenu;
				 viewIdentifier = SourceFiles.CONTACT_LIST_MENU;
				 backViewIdentifier = SourceFiles.MAIN_MENU;
				 currentObject.show();
			 }
			 break;
			 
		 case SourceFiles.CONTACT_EDIT_MENU:
			 TextFieldValues txtFieldValuesEdit = ((UIGooseFieldTextMenuView)currentObject).getTextField();		 
			 
			 if (txtFieldValuesEdit.phoneNumber.length() == 0) {
				 return;
			 }
				 backObject = null;
				 backObject = currentObject;
				backViewIdentifier = SourceFiles.CONTACT_EDIT_MENU;
				UIGooseFunction.setContact(this, txtFieldValuesEdit.name, txtFieldValuesEdit.surname, txtFieldValuesEdit.phoneNumber);
				UIGooseContactsMenuView uiListContact = new UIGooseContactsMenuView(viewIdentifier, this, display, contacts);
				currentObject = uiListContact;
				viewIdentifier = SourceFiles.CONTACT_LIST_MENU;
				currentObject.show();
			 
			 break;
		 
		 case SourceFiles.MESSAGES_NEW_TEXT:
			 UIGooseFunction.sendMessage(this);
			 break;
			 
		 case SourceFiles.MESSAGES_NEW_VOICE:
			 UIGooseFunction.sendMessage(this);
			 break;
			 
		 case SourceFiles.SEEK_MENU_START:
			 String[] aux = new String[3];
			 aux =((UIGooseSeekFriendMenuView)currentObject).sendSeekFriendRequest(this);
			 UIGooseThreadSeekFriend uiGooseThreadSeekFriend  = new UIGooseThreadSeekFriend(this,
					 aux[0], aux[1], aux[2]);
			 Thread seekThread = new Thread(uiGooseThreadSeekFriend );
			 seekThread.start();
			 viewIdentifier = SourceFiles.MAIN_MENU;
			 currentObject = null;
			 currentObject = new UIGooseMainMenuView(SourceFiles.MAIN_MENU, this,
						display, width, height, this);
			 currentObject.show();
			 break;
		 
		 case SourceFiles.PERSONAL_PROFILE:
			 if(((UIGoosePersonalProfileMenuView)currentObject).checkPhoneNumber()){
				 //TODO: SAVE new personal profiles 
				 
				 if (((UIGoosePersonalProfileMenuView)currentObject).setProfile(uiGooseRecordStore, this)) {
					 Alert alert = new Alert("Information:", "New personal profile saved", null, AlertType.INFO);
					 if (backViewIdentifier == SourceFiles.MAIN_MENU) {
						// We are in the loggin menu
						 viewIdentifier = SourceFiles.MAIN_MENU;
						currentObject = new UIGooseMainMenuView(SourceFiles.MAIN_MENU, this,
										display, width, height, this);
						currentObject.show();
						display.setCurrent(alert, ((UIGooseMainMenuView)currentObject));
					 }
					 else {
						 viewIdentifier = SourceFiles.CONFIGURATION;
						 currentObject = new UIGooseConfigurationMenuView(this);
						 currentObject.show();
						 display.setCurrent(alert, ((UIGooseConfigurationMenuView)currentObject));
					 }
				 }
			 }
			 break;
			 
		 case SourceFiles.UPDATE_STATUS:
			 String message = ((UIGooseUpdateStatusMenuView)currentObject).updateStatus(this);
			 if(message.length()!=0){
				 UIGooseThreadSendMessage uiGooseThreadSendMessage  = new UIGooseThreadSendMessage(this, 
							new Vector(), GooseMessage.STATUS_UPDATE, 1, GooseMessage.TEXT, message);
					
				Thread threadSend = new Thread (uiGooseThreadSendMessage);
				//log.debug("after create thread");	
				threadSend.start();
				//log.debug("after start thread");	
				viewIdentifier = backViewIdentifier;
				 backViewIdentifier = SourceFiles.MAIN_MENU;
				 currentObject= backObject;
				 backViewIdentifier = SourceFiles.MAIN_MENU;
				 backObject = new UIGooseMainMenuView(SourceFiles.MAIN_MENU, this,
							 display, width, height, this);
				 currentObject.show(); 
			 }
			
			 break;
			 default: 
				 break;
		 }
		 
		 
		 
	 }
	 
	 
	 
	 public void indexList() {
		 
		 List aux = (List)display.getCurrent();
		 index = aux.getSelectedIndex();
		 
		 switch(viewIdentifier) {
		 case SourceFiles.CONTACT_LIST_MENU:
			 backObject = null;
			 backObject = currentObject;
			 backViewIdentifier = viewIdentifier;
			 System.out.println("BackViewIdentifier in indexList contact_list" + backViewIdentifier);
			 viewIdentifier = SourceFiles.CONTACT_DETAIL_MENU;
			 Contact auxContact = (Contact)contacts.elementAt(index);
			 currentObject = new UIGooseContactDetailsMenuView(this, auxContact);
			 break;
			 
		 case SourceFiles.CONTACT_SEARCH_RESULTS_MENU:
			 backObject = null;
			 backObject = currentObject;
			 backViewIdentifier = viewIdentifier;
			 viewIdentifier = SourceFiles.CONTACT_DETAIL_MENU;
			 Contact auxSearch = ((UIGooseResultsContactMenuView)currentObject).getSelectedContact(index);
			 currentObject = new UIGooseContactDetailsMenuView(this, auxSearch);
			 break;
			 
		 case SourceFiles.CONTACT_TOP_RESULTS:
			 System.out.println("Contact top result selected contact");
			 backObject = null;
			 backObject = currentObject;
			 backViewIdentifier = viewIdentifier;
			 viewIdentifier = SourceFiles.CONTACT_DETAIL_MENU;
			 Contact auxTop = ((UIGooseResultsContactMenuView)currentObject).getSelectedContact(index);
			 currentObject = new UIGooseContactDetailsMenuView(this, auxTop);
			 break;
			 
		 case SourceFiles.CONTACT_LAST:
			 System.out.println("Contact top result selected contact");
			 backObject = null;
			 backObject = currentObject;
			 backViewIdentifier = viewIdentifier;
			 viewIdentifier = SourceFiles.CONTACT_DETAIL_MENU;
			 Contact auxLast = ((UIGooseResultsContactMenuView)currentObject).getSelectedContact(index);
			 currentObject = new UIGooseContactDetailsMenuView(this, auxLast);
			 break;
		 
		 case SourceFiles.EXCHANGE_RESULTS_MENU:
			contactChat = ((UIGooseExchangeMenuView)currentObject).exchangeDetails(this);
			//log.debug("Entering Exchange menu to send exchange to: "+index);
			
			if (contactChat == null) {
				 viewIdentifier = SourceFiles.MAIN_MENU;
				 currentObject = new UIGooseMainMenuView(SourceFiles.MAIN_MENU, this,
						display, width, height, this);
				
				break;
			}
			
			Alert gooseAlert = GooseAlert.createAlert("Do you want to see this user profile", AlertType.CONFIRMATION);
			alertType = SourceFiles.PROFILE_REQUEST;
			gooseAlert.setCommandListener(this);
			gooseAlert.addCommand(SourceFiles.OK_ALERT_COMMAND);
			gooseAlert.addCommand(SourceFiles.CANCEL_ALERT_COMMAND);
			 viewIdentifier = SourceFiles.MAIN_MENU;
			 currentObject = new UIGooseMainMenuView(SourceFiles.MAIN_MENU, this,
					display, width, height, this);
			 currentObject.show();
			display.setCurrent(gooseAlert, (UIGooseMainMenuView)currentObject);
			return;
				
			 
		 case SourceFiles.MESSAGES_NEW_ADD:
			 UIGooseFunction.addSelectedRecipient(this); 
			 break;
		 
		 case SourceFiles.MESSAGES_INBOX:
			 System.out.println("Messages in box selected to details");
			 backObject = null;
			 backObject = currentObject;
			 backViewIdentifier = viewIdentifier;
			 viewIdentifier = SourceFiles.MESSAGE_DETAIL_MENU;
			 GooseMessage gooseMessageIn = ((UIGooseInOutMessagesMenuView)currentObject).getSelectedMessage(index);
			 try {
					gooseManager.setMessageAsRead(gooseMessageIn.GUID);
				} catch (GooseException e) {
					// TODO Auto-generated catch block
					//log.debug("UIGoosemanager. Error when setting message as readed");
				}
			 currentObject = new UIGooseMessageDetailsMenuView(this, SourceFiles.MESSAGES_INBOX, gooseMessageIn);
			 break;
			 
		 case SourceFiles.MESSAGES_OUTBOX:
			 System.out.println("Messages out box selected to details");
			 backObject = null;
			 backObject = currentObject;
			 backViewIdentifier = viewIdentifier;
			 viewIdentifier = SourceFiles.MESSAGE_DETAIL_MENU;
			 GooseMessage gooseMessageOut = ((UIGooseInOutMessagesMenuView)currentObject).getSelectedMessage(index);
			 currentObject = new UIGooseMessageDetailsMenuView(this, SourceFiles.MESSAGES_OUTBOX, gooseMessageOut);
			 break;
		 
		 case SourceFiles.SELECT_PROFILE_MENU:
			 byte[] data;
			 //data = uiGooseRecordStore.readRecordStore();
			 
			 String stringList=((UIGooseSelectProfile)currentObject).list.getString(index);
			 int indexString =stringList.indexOf(" ");
			 String surname = "//surname:"+stringList.substring(indexString+1);
			 String name ="//name:" +stringList.substring(0, indexString)+ SourceFiles.SPACER;
			 
			 data = uiGooseRecordStore.getSelectedProfile(index, name+surname);
			 ////log.debug("selected profile number: "+index);
			 
			 if (data == null || index == 0) {
				 //log.debug("No profile found ");
				 Contact contact = null;
					viewIdentifier = SourceFiles.LOGIN_MENU;
					UIGooseFieldTextMenuView uiGooseField = new UIGooseFieldTextMenuView(viewIdentifier, 
							this,  display, contact);
					uiGooseField.show();
					currentObject = uiGooseField;
					currentObject.show();
			 }
			 else {
				int index;
				int indexEnd;
				String dataString = new String(data);
				////log.debug("UIGoose. Selected item: "+ dataString);
				index = 0;
				indexEnd = dataString.indexOf(";//date:");
				userID = dataString.substring(index, indexEnd);
				////log.debug("UserID: "+userID);
				index = userID.indexOf("//name:")+"//name:".length();
				indexEnd = userID.indexOf(";//surname:");
				String nameID = userID.substring(index, indexEnd);
				index = userID.indexOf("//surname:")+"//surname:".length();
				indexEnd = userID.indexOf(";//phonenumber:");
				String surnameID = userID.substring(index, indexEnd);
				index = userID.indexOf("//phonenumber:")+"//phonenumber:".length();
				//indexEnd = userID.indexOf(";//surname:");
				String phonenumberID = userID.substring(index);
				
				try {
					 gooseManager.setUserDetails(nameID,
							 surnameID, phonenumberID);
					}catch (GooseException e) {
						e.printStackTrace();
						display.setCurrent(GooseAlert.createAlert("Login Error" + e.getMessage()),
								((UIGooseFieldTextMenuView)currentObject));
					}
				backObject = null;
				bIsLoggedBefore = true;
				bLogin = true;
			 }
			 return;
			
		 }
	
		 currentObject.show();
		
		 
		 
		 
	 }
	 
	 public void backFunction() {
		 System.out.println("backFunction viewIdentifier" + viewIdentifier);
		 System.out.println("backFunction backMenu:" + backViewIdentifier);
		
		 if (viewIdentifier == backViewIdentifier) {
			 backObject = null;
			 viewIdentifier = SourceFiles.MAIN_MENU;
			 currentObject = new UIGooseMainMenuView(SourceFiles.MAIN_MENU, this,
					display, width, height, this);
		 }
//		 else if (viewIdentifier == SourceFiles.MAIN_MENU){
//			 this.pause();
//		 }
		 if (viewIdentifier == SourceFiles.MESSAGES_NEW_ADD) {
			 switch(backViewIdentifier) {
			 case SourceFiles.MESSAGES_NEW_TEXT:
				 	viewIdentifier = SourceFiles.MESSAGES_NEW_TEXT;
					((UIGooseNewMessageMenuView)currentObject).fillNewMessageText(this);
					backViewIdentifier = SourceFiles.MAIN_MENU;
					backObject = new UIGooseMessageMainMenuView(this);
				 break;
			 case SourceFiles.MESSAGES_NEW_VOICE:
				 viewIdentifier = SourceFiles.MESSAGES_NEW_VOICE;
					((UIGooseNewMessageMenuView)currentObject).fillNewMessageVoice(this);
					backViewIdentifier = SourceFiles.MAIN_MENU;
					backObject = new UIGooseMessageMainMenuView(this);
				 break;
				 
			 }
			 currentObject.show();
		 }
		 else if (viewIdentifier == SourceFiles.MAIN_MENU) {
			 currentObject = new UIGooseMainMenuView(SourceFiles.MAIN_MENU, this,
						display, width, height, this);
 		   currentObject.show();
		 }
		 else if (viewIdentifier == SourceFiles.CONTACT_EDIT_MENU && 
				 backViewIdentifier == SourceFiles.CONTACT_DETAIL_MENU) {
			backViewIdentifier = SourceFiles.MAIN_MENU;
			 backObject = new UIGooseMainMenuView(SourceFiles.MAIN_MENU, this,
						display, width, height, this);
			 currentObject = new UIGooseContactMainMenuView(this);
			 viewIdentifier = SourceFiles.CONTACT_MENU;
		 }/*
		 else if (viewIdentifier == SourceFiles.CONTACT_SEARCH_RESULTS_MENU && 
				 backViewIdentifier == SourceFiles.CONTACT_MENU) {
			backViewIdentifier = SourceFiles.MAIN_MENU;
			 backObject = new UIGooseMainMenuView(SourceFiles.MAIN_MENU, this,
						display, width, height, this);
			 currentObject = new UIGooseContactMainMenuView(this);
			 viewIdentifier = SourceFiles.CONTACT_MENU;
		 }*/
		 else if (backViewIdentifier == SourceFiles.MESSAGES_INBOX) {
			 viewIdentifier = backViewIdentifier;
			 currentObject = new UIGooseInOutMessagesMenuView(this);
		 }
		 else if (viewIdentifier ==  SourceFiles.MESSAGES_NEW_TEXT || viewIdentifier == SourceFiles.MESSAGES_NEW_VOICE) {
			 
			 currentObject = new UIGooseNewMessageMenuView(this);
			 viewIdentifier = backViewIdentifier;
		 }
		 else {
			 currentObject.destroy();
			 currentObject = null;
			 viewIdentifier = backViewIdentifier;
			 currentObject = backObject;
		 }
		
		 currentObject.show();
		 
	 }
		 
		
		
		
		
		 
		 
	 
	 
	 
	 
}
