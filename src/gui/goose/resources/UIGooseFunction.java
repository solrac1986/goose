package gui.goose.resources;

import goose.contactsManager.Contact;
import goose.contactsManager.ContactsManager;
import goose.forwardingManager.ForwardingManager;
import goose.forwardingManager.GooseMessage;
import goose.forwardingManager.MessageTools;
import goose.mainManager.GooseManager;
import goose.mainManager.GooseTools;
import goose.mainManager.IncomingMessageEvent;
import goose.exceptions.GooseException;
import goose.exceptions.GooseSearchException;
import gui.goose.exceptions.GooseAlert;
import gui.goose.menus.UIGooseFieldTextMenuView;
import gui.goose.menus.UIGooseMainMenuView;
import gui.goose.menus.UIGooseManager;
import gui.goose.menus.UIGooseMessageMainMenuView;
import gui.goose.menus.UIGooseNewMessageMenuView;
import gui.goose.menus.UIGooseResultsContactMenuView;
import gui.goose.menus.UIGooseViewProfile;

//import java.io.ByteArrayOutputStream;
//import java.io.IOException;
import java.util.Date;
import java.util.Vector;

import javax.bluetooth.ServiceRecord;
import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.AlertType;
//import javax.microedition.lcdui.Form;
//import javax.microedition.lcdui.StringItem;
//import javax.microedition.media.Manager;
//import javax.microedition.media.MediaException;
//import javax.microedition.media.Player;
//import javax.microedition.media.control.RecordControl;
import javax.microedition.midlet.MIDletStateChangeException;
import javax.microedition.rms.RecordStore;

import microlog.Logger;



import gui.goose.menus.*;;

public class UIGooseFunction {

	private static Logger log = Logger.getLogger();
	
	
	public static void setContact(UIGooseManager uiGooseManager, String name, String surName, String phone) {
		
		if (uiGooseManager.viewIdentifier == SourceFiles.LOGIN_MENU) {
			 try {
				 uiGooseManager.gooseManager.setUserDetails(name,
							surName, phone);
				}catch (GooseException e) {
					e.printStackTrace();
					uiGooseManager.display.setCurrent(GooseAlert.createAlert("Login Error" + e.getMessage()),
							((UIGooseFieldTextMenuView)uiGooseManager.currentObject));
				}
				byte[] data;
				
				String dataString = "";
				
				dataString += SourceFiles.STARTER +"name:"+ name+SourceFiles.SPACER;
				dataString += SourceFiles.STARTER +"surname:"+ surName+SourceFiles.SPACER;
				dataString += SourceFiles.STARTER +"phonenumber:"+ phone+SourceFiles.SPACER;
				dataString += SourceFiles.STARTER +"date:"+SourceFiles.SPACER;
				dataString += SourceFiles.STARTER +"sex:"+SourceFiles.SPACER;
				dataString += SourceFiles.STARTER +"city:"+SourceFiles.SPACER;
				dataString += SourceFiles.STARTER +"ocupation:"+SourceFiles.SPACER;
				dataString += SourceFiles.STARTER +"about:"+SourceFiles.SPACER_END;
				data = dataString.getBytes();
				int index = 0;
				int indexEnd = dataString.indexOf(";//date:");
				uiGooseManager.userID = dataString.substring(index, indexEnd);
				//uiGooseManager.uiGooseRecordStore.writeRecordStore(data);
				uiGooseManager.bIsLoggedBefore =  uiGooseManager.uiGooseRecordStore.writeAllProfiles(data, SourceFiles.RECORD_STORE_GOOSE_WALL, 
						uiGooseManager.userID, SourceFiles.LOGIN_MENU);
			}
		 else {
			 Contact editContact = null;
			 
			 if (uiGooseManager.viewIdentifier == SourceFiles.CONTACT_EDIT_MENU) {
				 
				 editContact = ((Contact)(uiGooseManager.contacts.elementAt(uiGooseManager.index)));
				 String[] dataInput = new String[3];
				 dataInput[0]=name;
				 dataInput[1]=surName;
				 dataInput[2]=phone;
				 try {
						uiGooseManager.gooseManager.editContact(editContact, dataInput);
					} catch (GooseSearchException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						uiGooseManager.display.setCurrent(GooseAlert.createAlert("Edit error " + e.getMessage()),
								((UIGooseFieldTextMenuView)uiGooseManager.currentObject));
					}
					uiGooseManager.contacts.setElementAt(editContact, uiGooseManager.index);
			 }
			 else if (uiGooseManager.viewIdentifier == SourceFiles.CONTACT_NEW_MENU) {
				 
				 try {
					 	
					 	String[] dataInput = new String[3];
					 	dataInput[0]=name;
					 	dataInput[1]=surName;
					 	dataInput[2]=phone;
					 	//System.out.println("NEw contact numbewr phone" + dataInput[2]);
						editContact = uiGooseManager.gooseManager.createContact(dataInput);
						uiGooseManager.contacts.addElement(editContact);
						uiGooseManager.contacts = sortContactsByName(uiGooseManager.contacts);
						uiGooseManager.backViewIdentifier = SourceFiles.MAIN_MENU;
						uiGooseManager.backObject  = new UIGooseMainMenuView(SourceFiles.MAIN_MENU, uiGooseManager,
								 uiGooseManager.display, uiGooseManager.width, uiGooseManager.height, uiGooseManager);
				 } catch (GooseException e) {
						// TODO Auto-generated catch block
						uiGooseManager.display.setCurrent(GooseAlert.createAlert("Create contact error " + e.getMessage()),
								((UIGooseFieldTextMenuView)uiGooseManager.currentObject));
						System.out.println(e.getMessage());
					}					
			 }
		 }
	}
	
	public static Vector searchFuncion (UIGooseManager uiGooseManager, String name, String surname, String phone ) {
		Vector vSearch = new Vector();
	
		String[] auxArray = ((UIGooseFieldTextMenuView)uiGooseManager.currentObject).getValuePattern(name, surname, phone);
		
		int[] auxPattern = ((UIGooseFieldTextMenuView)uiGooseManager.currentObject).getSearchPattern();
		
		if (uiGooseManager.viewIdentifier == SourceFiles.CONTACT_SEARCH_MENU) {
				
			 if (auxArray.length == 1) {
				System.out.println("search function number "+ auxArray[0]);
				
				System.out.println("search function number "+ auxPattern[0]);
				 try {
						vSearch = uiGooseManager.gooseManager.searchContact(auxArray[0], 
								auxPattern[0]);
					} catch (GooseSearchException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					uiGooseManager.display.setCurrent(GooseAlert.createAlert("Search error " + e.getMessage()),
							((UIGooseFieldTextMenuView)uiGooseManager.currentObject));
					} 
					 System.out.println("searchFunction tmaamnyo "+ vSearch.size());
					// System.out.println("searchFunction value "+ ((Contact)vSearch.elementAt(0)).getSurname());
			 }
			 else if (auxArray.length != 0) {
				 //System.out.println("searchFunction value "+auxArray[0]);
				 //System.out.println("searchFunction value "+auxArray[1]);
				 //System.out.println("searchFunction value "+auxArray[2]);
				 try {
						vSearch = uiGooseManager.gooseManager.searchContact(auxArray, 
								auxPattern);
					} catch (GooseSearchException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					uiGooseManager.display.setCurrent(GooseAlert.createAlert("Search error " + e.getMessage()),
							((UIGooseFieldTextMenuView)uiGooseManager.currentObject));
					} 
			 }
		}
			 return vSearch;
	}
	
	public static  void menuSelect(UIGooseManager uiGooseManager, int menuSelected, Object object) {
		System.out.println("menu selected: "+menuSelected);
		switch (uiGooseManager.viewIdentifier){
		
		case SourceFiles.MAIN_MENU:
			uiGooseManager.backViewIdentifier = uiGooseManager.viewIdentifier;
			uiGooseManager.contacts = null;
			uiGooseManager.backObject  = uiGooseManager.currentObject;
			switch (menuSelected){
			case 0:
				try {
					uiGooseManager.contacts = uiGooseManager.gooseManager.getContacts();
				}catch(GooseSearchException e) {
					//log.debug("Exception in uigoosefunction when retrieving contacts");
				}
				uiGooseManager.viewIdentifier = SourceFiles.CONTACT_MENU;
				UIGooseContactMainMenuView uiMenuContactView = new UIGooseContactMainMenuView(uiGooseManager);
				uiGooseManager.currentObject = uiMenuContactView;
				
				break;
			case 1:
				uiGooseManager.viewIdentifier = SourceFiles.MESSAGES_MENU;
				UIGooseMessageMainMenuView uiMenuMessageView = new UIGooseMessageMainMenuView(uiGooseManager);
				uiGooseManager.currentObject = uiMenuMessageView;
				break;
			case 2:
				uiGooseManager.viewIdentifier = SourceFiles.EXCHANGE_MENU;
				UIGooseSearchMainMenuView uiMenuSearchView = new UIGooseSearchMainMenuView(uiGooseManager);
				uiGooseManager.currentObject = uiMenuSearchView;
				break;
			case 3:
				uiGooseManager.viewIdentifier = SourceFiles.CONFIGURATION;
				uiGooseManager.currentObject = new UIGooseConfigurationMenuView(uiGooseManager);
				break;
			case 4:
				//Switch off application
				try {
						uiGooseManager.destroyApp(true);
					} catch (MIDletStateChangeException e) {
						// TODO Auto-generated catch block
						uiGooseManager.display.setCurrent(GooseAlert.createAlert("Error when switching off Goose", AlertType.ERROR),
								(UIGooseMainMenuView)uiGooseManager.currentObject);
					}
				break;
			
			default:
				return;
			}
			//uiGooseManager.backObject  = uiGooseManager.currentObject;
			//UIGooseMenuView uiMenuView = new UIGooseMenuView(uiGooseManager.viewIdentifier, uiGooseManager);
//			//uiGooseManager.currentObject = uiMenuView;
			/*try {
				Thread.sleep(50);
				
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				System.out.println("erro sleep: "+ e1.getMessage());
			}*/
			uiGooseManager.currentObject.show();
			
		break;
		
		case SourceFiles.CONTACT_MENU:
			uiGooseManager.backViewIdentifier = SourceFiles.CONTACT_MENU;
			//uiGooseManager.backObject.destroy();
			//uiGooseManager.backObject  = null;	
			uiGooseManager.backObject  = new UIGooseContactMainMenuView(uiGooseManager);
			
			switch(menuSelected) {
			case 0:
				uiGooseManager.viewIdentifier = SourceFiles.CONTACT_LIST_MENU;
				UIGooseContactsMenuView uiContacts = new UIGooseContactsMenuView(uiGooseManager.viewIdentifier, uiGooseManager, uiGooseManager.display, uiGooseManager.contacts);
				uiGooseManager.currentObject = uiContacts;
				
				break;
			case 1:
				uiGooseManager.viewIdentifier  = SourceFiles.CONTACT_NEW_MENU;
				UIGooseFieldTextMenuView uiNewContact = new UIGooseFieldTextMenuView(uiGooseManager.viewIdentifier, uiGooseManager, uiGooseManager.display, null);
				uiGooseManager.currentObject = uiNewContact;
				break;
			case 2:
				uiGooseManager.viewIdentifier = SourceFiles.CONTACT_SEARCH_MENU;
				UIGooseFieldTextMenuView uiSearch = new UIGooseFieldTextMenuView(uiGooseManager.viewIdentifier, uiGooseManager, uiGooseManager.display, null);
				uiGooseManager.currentObject = uiSearch;
				break;
			case 3: 
				uiGooseManager.viewIdentifier = SourceFiles.SEEK_MENU;
				UIGooseSeekMainMenuView uiMenuSeekView = new UIGooseSeekMainMenuView(uiGooseManager);
				uiGooseManager.currentObject = uiMenuSeekView;
				break;
			case 4:
				uiGooseManager.viewIdentifier = SourceFiles.CONTACT_TOP_RESULTS;
				UIGooseResultsContactMenuView uiTopResults = new UIGooseResultsContactMenuView(uiGooseManager.viewIdentifier, uiGooseManager, null); 
				uiGooseManager.currentObject = uiTopResults;
				break;
				
			case 5:
				//TODO: View last contact used in goose
				uiGooseManager.viewIdentifier = SourceFiles.CONTACT_LAST;
				UIGooseResultsContactMenuView uiLastResults = new UIGooseResultsContactMenuView(uiGooseManager.viewIdentifier, uiGooseManager, null); 
				uiGooseManager.currentObject = uiLastResults;
				break;
			
			default:
				return;
			}
			uiGooseManager.currentObject.show();
			
			break;
		
		case SourceFiles.MESSAGES_MENU:
			uiGooseManager.backViewIdentifier = SourceFiles.MESSAGES_MENU;
			uiGooseManager.backObject.destroy();
			uiGooseManager.backObject  = null;	
			uiGooseManager.backObject  = new UIGooseMessageMainMenuView(uiGooseManager);;
			switch(menuSelected){
			case 0:
				uiGooseManager.viewIdentifier = SourceFiles.MESSAGES_NEW;
				uiGooseManager.currentObject = new UIGooseNewMessageMenuView(uiGooseManager);
				break;
			case 1:
				uiGooseManager.viewIdentifier = SourceFiles.UPDATE_STATUS;
				uiGooseManager.currentObject = new UIGooseUpdateStatusMenuView(uiGooseManager);
				break;
			case 2:
				uiGooseManager.viewIdentifier = SourceFiles.GOOSE_WALL;
				uiGooseManager.currentObject = new UIGooseGooseWallMenuView(uiGooseManager);
				break;
			case 3:
				uiGooseManager.viewIdentifier = SourceFiles.MESSAGES_INBOX;
				uiGooseManager.currentObject = new UIGooseInOutMessagesMenuView(uiGooseManager);
				break;
			case 4:
				uiGooseManager.viewIdentifier = SourceFiles.MESSAGES_OUTBOX;
				uiGooseManager.currentObject = new UIGooseInOutMessagesMenuView(uiGooseManager);
				break;		
			default:
				return;
			}
			uiGooseManager.currentObject.show();
			break;
			
		case SourceFiles.MESSAGES_NEW:
			uiGooseManager.backViewIdentifier = uiGooseManager.viewIdentifier;
			uiGooseManager.backObject.destroy();
			uiGooseManager.backObject  = null;	
			uiGooseManager.backObject  = uiGooseManager.currentObject;
			if ( UIGooseManager.onBluetooth) {
				Alert alert = new Alert("Warning", "You are already sending a message", null, AlertType.INFO);
				uiGooseManager.currentObject.show();
				uiGooseManager.display.setCurrent(alert, (UIGooseNewMessageMenuView)uiGooseManager.currentObject);
				return;
			}
			switch(menuSelected){
			case 0:
				//Text
				uiGooseManager.viewIdentifier = SourceFiles.MESSAGES_NEW_TEXT;
				((UIGooseNewMessageMenuView)uiGooseManager.currentObject).fillNewMessageText(uiGooseManager);
				break;
			case 1:
				//Voice
				uiGooseManager.viewIdentifier = SourceFiles.MESSAGES_NEW_VOICE;
				((UIGooseNewMessageMenuView)uiGooseManager.currentObject).fillNewMessageVoice(uiGooseManager);
				break;
			default: 
				return;
			}
			uiGooseManager.currentObject.show();
			break;
		case SourceFiles.MESSAGES_NEW_TEXT:
			
			//AddMenu
			if (!((UIGooseNewMessageMenuView)uiGooseManager.currentObject).booleanBCAST){
				uiGooseManager.backViewIdentifier = uiGooseManager.viewIdentifier;
				uiGooseManager.backObject.destroy();
				uiGooseManager.backObject  = null;	
				uiGooseManager.backObject  = uiGooseManager.currentObject;
				uiGooseManager.viewIdentifier = SourceFiles.MESSAGES_NEW_ADD;
				((UIGooseNewMessageMenuView)uiGooseManager.currentObject).addMenu(uiGooseManager);
			}
			uiGooseManager.currentObject.show();
			break;
			
		case SourceFiles.MESSAGES_NEW_VOICE:
			switch(menuSelected) {
			case 0:
				if (! ((UIGooseNewMessageMenuView)uiGooseManager.currentObject).booleanBCAST ) {
					uiGooseManager.backViewIdentifier = uiGooseManager.viewIdentifier;
					uiGooseManager.backObject.destroy();
					uiGooseManager.backObject  = null;	
					uiGooseManager.backObject  = uiGooseManager.currentObject;
					uiGooseManager.viewIdentifier = SourceFiles.MESSAGES_NEW_ADD;
					((UIGooseNewMessageMenuView)uiGooseManager.currentObject).addMenu(uiGooseManager);
				}
				break;
			case 1:
				if ( ! UIGooseNewMessageMenuView.isRecording){
					((UIGooseNewMessageMenuView)uiGooseManager.currentObject).recordMessage();
				}
				else {
					((UIGooseNewMessageMenuView)uiGooseManager.currentObject).stopMessage();
				}
				break;
			case 2:
				if (! UIGooseNewMessageMenuView.isPlaying) {
					((UIGooseNewMessageMenuView)uiGooseManager.currentObject).playMessage();
				}
				else {
					((UIGooseNewMessageMenuView)uiGooseManager.currentObject).stopMessage();
				}
				
				break;
			default:
				break;
				
			}
			break;
			
		case SourceFiles.EXCHANGE_MENU:
			//
			uiGooseManager.backObject.destroy();
			uiGooseManager.backObject = null;
			
			((UIGooseSearchMainMenuView)uiGooseManager.currentObject).setTitle("Searching...");
			uiGooseManager.currentObject.show();
			switch(menuSelected) {
			case 0:
				uiGooseManager.viewIdentifier = SourceFiles.EXCHANGE_RESULTS_MENU; 
				UIGooseExchangeMenuView uiExchangeResults = new UIGooseExchangeMenuView(uiGooseManager); 
				uiGooseManager.currentObject = uiExchangeResults;
				uiGooseManager.backViewIdentifier = SourceFiles.MAIN_MENU;
				uiGooseManager.backObject  = new UIGooseMainMenuView(SourceFiles.MAIN_MENU, uiGooseManager,
						 uiGooseManager.display, uiGooseManager.width, uiGooseManager.height, uiGooseManager);
				
				break;
			default:
				return;
			}
			//uiGooseManager.currentObject.show();
			break;
			
		case SourceFiles.SEEK_MENU:
			uiGooseManager.backObject.destroy();
			uiGooseManager.backObject = null;
			uiGooseManager.backViewIdentifier = SourceFiles.SEEK_MENU;
			uiGooseManager.backObject  = new UIGooseSeekMainMenuView(uiGooseManager);;
			switch(menuSelected) {
			case 0:
				uiGooseManager.viewIdentifier = SourceFiles.SEEK_MENU_START;
				UIGooseSeekFriendMenuView uiSeekMenu = new UIGooseSeekFriendMenuView(uiGooseManager); 
				uiGooseManager.currentObject = uiSeekMenu;
				break;
			default:
				return;
			}
			uiGooseManager.currentObject.show();
			break;
			
		case SourceFiles.MESSAGE_DETAIL_MENU:
			if ( ! UIGooseMessageDetailsMenuView.isAudio) {
				////log.debug("UIGoose. Selected one button number: "+menuSelected);
				
				switch(menuSelected){
				case 0:
					
					//TODO: Send new message
					GooseMessage gooseMessageReceived = ((UIGooseMessageDetailsMenuView)uiGooseManager.currentObject).getMessage();
					Contact auxContact = new Contact();
					auxContact.setName( gooseMessageReceived.senderName);
					auxContact.setSurname(gooseMessageReceived.senderSurname);
					try {
							auxContact.setPhonenumber(gooseMessageReceived.senderPhoneNumber);
						} catch (GooseException e) {
							// TODO Auto-generated catch block
							//log.debug("Error when creating phoneNumber in new Contact to respond message  " +e.getMessage());	
						}
					auxContact.setBluetoothAddress(gooseMessageReceived.senderBTMacAddress);
					 uiGooseManager.backObject = null;
					 uiGooseManager.backObject = new UIGooseMainMenuView(SourceFiles.MAIN_MENU, uiGooseManager,
							 uiGooseManager.display, uiGooseManager.width, uiGooseManager.height, uiGooseManager);
					 uiGooseManager.backViewIdentifier = SourceFiles.MAIN_MENU;
						
					 uiGooseManager.viewIdentifier = SourceFiles.MESSAGES_NEW;
					////log.debug("Creating new Menu new message");
					uiGooseManager.currentObject = new UIGooseNewMessageMenuView(uiGooseManager);
						
					((UIGooseNewMessageMenuView)uiGooseManager.currentObject).vSendTo.addElement(auxContact);
					////log.debug("adding contact to recipients(sendTo)");
					
					((UIGooseNewMessageMenuView)uiGooseManager.currentObject).fillNewMessage(uiGooseManager, null);
					//((UIGooseNewMessageMenuView)uiGooseManager.currentObject).fillNewMessageText(uiGooseManager);
					uiGooseManager.currentObject.show();
					break;
				case 1:
					//TODO: Delete Message
					GooseMessage gooseMessage = ((UIGooseMessageDetailsMenuView)uiGooseManager.currentObject).getMessage();
					uiGooseManager.backObject = null;
					uiGooseManager.viewIdentifier = SourceFiles.MAIN_MENU;
					uiGooseManager.currentObject = new UIGooseMainMenuView(SourceFiles.MAIN_MENU, uiGooseManager,
							 uiGooseManager.display, uiGooseManager.width, uiGooseManager.height, uiGooseManager);
					uiGooseManager.currentObject.show();
					try {
						uiGooseManager.gooseManager.deleteMessage(gooseMessage);
					}catch (GooseSearchException e) {
						 uiGooseManager.display.setCurrent(GooseAlert.createAlert(e.getMessage()),
									(UIGooseMainMenuView)uiGooseManager.currentObject);
					}
					break;
				default:
					return;
				}
			}
			else {
				//Voice detail message
				switch(menuSelected) {
				case 0:
					if (((UIGooseMessageDetailsMenuView)uiGooseManager.currentObject).isPlaying) {
						((UIGooseMessageDetailsMenuView)uiGooseManager.currentObject).stopMessage();
					}
					else {
						((UIGooseMessageDetailsMenuView)uiGooseManager.currentObject).playMessage();
					}
					break;
				case 1:
					GooseMessage gooseMessageReceived = ((UIGooseMessageDetailsMenuView)uiGooseManager.currentObject).getMessage();
					Contact auxContact = new Contact();
					auxContact.setName( gooseMessageReceived.senderName);
					auxContact.setSurname(gooseMessageReceived.senderSurname);
					try {
							auxContact.setPhonenumber(gooseMessageReceived.senderPhoneNumber);
						} catch (GooseException e) {
							// TODO Auto-generated catch block
							//log.debug("Error when creating phoneNumber in new Contact to respond message  " +e.getMessage());	
						}
					auxContact.setBluetoothAddress(gooseMessageReceived.senderBTMacAddress);
					 uiGooseManager.backObject = null;
					 uiGooseManager.backObject = new UIGooseMainMenuView(SourceFiles.MAIN_MENU, uiGooseManager,
							 uiGooseManager.display, uiGooseManager.width, uiGooseManager.height, uiGooseManager);
					 uiGooseManager.backViewIdentifier = SourceFiles.MAIN_MENU;
						
					 uiGooseManager.viewIdentifier = SourceFiles.MESSAGES_NEW;
					////log.debug("Creating new Menu new message");
					uiGooseManager.currentObject = new UIGooseNewMessageMenuView(uiGooseManager);
						
					((UIGooseNewMessageMenuView)uiGooseManager.currentObject).vSendTo.addElement(auxContact);
					////log.debug("adding contact to recipients(sendTo)");
					((UIGooseNewMessageMenuView)uiGooseManager.currentObject).fillNewMessage(uiGooseManager, null);
					//((UIGooseNewMessageMenuView)uiGooseManager.currentObject).fillNewMessageVoice(uiGooseManager);
					uiGooseManager.currentObject.show();
					
					break;
				case 2:
					GooseMessage gooseMessage = ((UIGooseMessageDetailsMenuView)uiGooseManager.currentObject).getMessage();
					uiGooseManager.backObject = null;
					uiGooseManager.viewIdentifier = SourceFiles.MAIN_MENU;
					uiGooseManager.currentObject = new UIGooseMainMenuView(SourceFiles.MAIN_MENU, uiGooseManager,
							 uiGooseManager.display, uiGooseManager.width, uiGooseManager.height, uiGooseManager);
					uiGooseManager.currentObject.show();
					try {
						uiGooseManager.gooseManager.deleteMessage(gooseMessage);
					}catch (GooseSearchException e) {
						 uiGooseManager.display.setCurrent(GooseAlert.createAlert(e.getMessage()),
									(UIGooseMainMenuView)uiGooseManager.currentObject);
					}
					break;
			
				default:
					break;
				}
				
			}
			break;
			
		case SourceFiles.CONFIGURATION:
			String typeDeleted = "";
			uiGooseManager.backViewIdentifier = SourceFiles.CONFIGURATION;
			uiGooseManager.backObject  = new UIGooseConfigurationMenuView(uiGooseManager);;
			switch(menuSelected) {
			case 0:
				ContactsManager ctMgr;
				try {
					typeDeleted = "contacts and profiles";
					ctMgr = (ContactsManager)uiGooseManager.gooseManager.getManager(GooseManager.CONTACTS_MANAGER);
					ctMgr.deleteAllContacts();
					uiGooseManager.uiGooseRecordStore.deleteProfiles();
					//uiGooseManager.uiGooseRecordStore.recordDeleted(arg0, arg1)
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					//log.debug("UIGoose. Error getting Contacts");
				}
				
				break;
				
			case 1:
				ForwardingManager fwMgr;
				try {
					typeDeleted = "messages";
					fwMgr = (ForwardingManager)uiGooseManager.gooseManager.getManager(GooseManager.FORWARDING_MANAGER);
					fwMgr.deleteAll();
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					//log.debug("UIGoose. Error getting Forwarding Manager");
				}
				break;
			case 2:
				uiGooseManager.viewIdentifier = SourceFiles.PERSONAL_PROFILE;
				uiGooseManager.currentObject = new UIGoosePersonalProfileMenuView(uiGooseManager);
				uiGooseManager.currentObject.show();
				return;
			case 3:
				//TODO: WRITE TEST in .txt
				UIRecordStoreTesting.readTrace(uiGooseManager);
				UIGooseFunction.writeFileContacts(uiGooseManager);
				try {
					RecordStore.deleteRecordStore(UIRecordStoreTesting.NAME_TESTING_RECORD_STORE);
					UIRecordStoreTesting.openRecordStoreTesting();
				}catch(Exception e){
					
				}
				return;
			case 4:
				typeDeleted = "test";
				UIRecordStoreTesting.deleteRecordStoreTesting();
				break;
			default:
				return;
			}
			uiGooseManager.viewIdentifier = SourceFiles.MAIN_MENU;
			uiGooseManager.currentObject = new UIGooseMainMenuView(SourceFiles.MAIN_MENU, uiGooseManager,
					 uiGooseManager.display, uiGooseManager.width, uiGooseManager.height, uiGooseManager);
			Alert alert = new Alert("Information", "Deleting " + typeDeleted +" Completed", null, AlertType.INFO);
			uiGooseManager.currentObject.show();
			uiGooseManager.display.setCurrent(alert, (UIGooseMainMenuView)uiGooseManager.currentObject);
			
		}
		String stringData;
		Date date = new Date();
		Runtime rt = Runtime.getRuntime();
		stringData = UIRecordStoreTesting.SEPARATOR + UIRecordStoreTesting.TYPE;
		stringData += UIRecordStoreTesting.MEMORY; 
		stringData += UIRecordStoreTesting.SEPARATOR;
		stringData += UIRecordStoreTesting.TIME + date.getTime();
		stringData += UIRecordStoreTesting.SEPARATOR;
		stringData += UIRecordStoreTesting.MEMORY_EVENT + UIRecordStoreTesting.MEMORY_GARBAGE;
		stringData+= UIRecordStoreTesting.SEPARATOR;
		stringData += UIRecordStoreTesting.MEMORY_BEFORE + (rt.totalMemory()-rt.freeMemory());
		invokeGC();       
		stringData+= UIRecordStoreTesting.SEPARATOR;
		stringData += UIRecordStoreTesting.MEMORY_AFTER + (rt.totalMemory()-rt.freeMemory());
		UIRecordStoreTesting.writeTrace(stringData.getBytes());
		System.out.println("MEmoria: "+ stringData);
		
	}
	
	
	
	
	public static void callEditMenu(UIGooseManager uiGooseManager) {
		
		
		 
		 System.out.println("ViewIdentifier: "+uiGooseManager.viewIdentifier);
		 Contact contact = new Contact();
		 if (uiGooseManager.backViewIdentifier == SourceFiles.CONTACT_LIST_MENU
				 || uiGooseManager.backViewIdentifier == SourceFiles.CONTACT_EDIT_MENU) {
			 contact = (Contact)uiGooseManager.contacts.elementAt(uiGooseManager.index);
			 //System.out.println("Contact retrieved in contact list menu: "+contact.getName());
		 }
		 else {
			 contact = ((UIGooseResultsContactMenuView)uiGooseManager.backObject).getSelectedContact(uiGooseManager.index);
		 }
		 uiGooseManager.backObject = null;
		 uiGooseManager.backObject = uiGooseManager.currentObject;
		 uiGooseManager.backViewIdentifier = uiGooseManager.viewIdentifier;
		 uiGooseManager.viewIdentifier = SourceFiles.CONTACT_EDIT_MENU;
		 uiGooseManager.currentObject = new UIGooseFieldTextMenuView(uiGooseManager.viewIdentifier, uiGooseManager, uiGooseManager.display, contact);
		 
		 uiGooseManager.currentObject.show();
	}
	
	
	
	public static void deleteContact(UIGooseManager uiGooseManager) {
		//System.out.println("deleteContact index" + uiGooseManager.index);
		System.out.println("deleteContact viewIdentifier" + uiGooseManager.viewIdentifier);
		Contact aux = (Contact)uiGooseManager.contacts.elementAt(uiGooseManager.index);
		uiGooseManager.contacts.removeElementAt(uiGooseManager.index);
		
		try {
			uiGooseManager.gooseManager.deleteContact(aux);
		} catch (Exception e) {
			e.printStackTrace();
			uiGooseManager.display.setCurrent(GooseAlert.createAlert("Delete error " + e.getMessage()),
					(UIGooseFieldTextMenuView)uiGooseManager.currentObject);
		}
		
		uiGooseManager.backObject.destroy();
		uiGooseManager.backObject = null;
		uiGooseManager.backViewIdentifier = SourceFiles.MAIN_MENU;
		uiGooseManager.backObject = new UIGooseMainMenuView(SourceFiles.MAIN_MENU, uiGooseManager,
				 uiGooseManager.display, uiGooseManager.width, uiGooseManager.height, uiGooseManager);
		uiGooseManager.viewIdentifier = SourceFiles.CONTACT_MENU;
		uiGooseManager.currentObject = new UIGooseContactMainMenuView(uiGooseManager);
		//uiGooseManager.viewIdentifier = SourceFiles.CONTACT_LIST_MENU;
		//uiGooseManager.currentObject = new UIGooseContactsMenuView(uiGooseManager.viewIdentifier, uiGooseManager, uiGooseManager.display, uiGooseManager.contacts);
		System.out.println("deleteContact viewIdentifier" + uiGooseManager.viewIdentifier);
		uiGooseManager.currentObject.show();
	
	}
	
	
	public static void messagePush(int alertType, UIGooseManager uiGooseManager) {
		//log.debug("MessagePush. Entering messagePush. AlertType: "+alertType);
		uiGooseManager.currentObject = null;
		uiGooseManager.viewIdentifier = SourceFiles.MAIN_MENU;
		uiGooseManager.currentObject = new UIGooseMainMenuView(SourceFiles.MAIN_MENU, uiGooseManager,
				 uiGooseManager.display, uiGooseManager.width, uiGooseManager.height, uiGooseManager);
		////log.debug("MessagePush. Switch for alertType");
		switch(alertType){
		case IncomingMessageEvent.CONTACT_DETAILS_EXCHANGE:
			//log.debug("MessagePush. CONTACT DETAILS EXCHANGE REQUEST RECEIVED");
			try {	
					String [] values1 = MessageTools.readContactMessage(uiGooseManager.contentMessage);
					////log.debug("Sending Contacts details in exchange operation with name:"+values1[0]);
					////log.debug("URL: "+ values1[4]);
					uiGooseManager.gooseManager.sendContactsACK(values1[4], values1[3]);
					uiGooseManager.gooseManager.createContact(values1);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					//log.debug(e.getMessage());
				}
					
			
			break;
		
		case IncomingMessageEvent.CONTACT_DETAILS_EXCHANGE_ACK:	
			//log.debug("MessagePush. CONTACT DETAILS ACK RECEIVED");
			
			try {
				String [] values2 = MessageTools.readContactMessage(uiGooseManager.contentMessage);
					uiGooseManager.gooseManager.createContact(values2);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					//log.debug(e.getMessage());
				}			
				
			break;
			
		case IncomingMessageEvent.CONTACT_PROFILE:
			try {	
				
				String [] valuesProfile = MessageTools.readContactMessage(uiGooseManager.contentMessage);
				byte[] data = uiGooseManager.uiGooseRecordStore.readAllProfiles(uiGooseManager.userID, SourceFiles.RECORD_STORE_GOOSE_WALL);
				if (data == null) {
					data = (uiGooseManager.userID+ SourceFiles.SPACER+"//date:" +""+
							SourceFiles.SPACER+ "//sex:"+""+SourceFiles.SPACER+"//city:"+""+SourceFiles.SPACER+"//ocupation:"+""+
							SourceFiles.SPACER+"//about:"+""+SourceFiles.SPACER_END).getBytes();
				}
				String dataString = new String(data);
				//log.debug("UIGoose. Content message profile: "+dataString);
				uiGooseManager.gooseManager.sendPersonalProfileACK(valuesProfile[4], valuesProfile[3], dataString);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				//log.debug(e.getMessage());
			}
			break;

		case IncomingMessageEvent.CONTACT_PROFILE_ACK:
			String auxString = uiGooseManager.contentMessage; //+ SourceFiles.STARTER + "btAddress:" +
//			newEvent.getMessage().senderBTMacAddress+ "||;";
			byte [] data = auxString.getBytes();
			//log.debug("UIGoose. Content Message profile ACK: "+auxString);
			uiGooseManager.backObject = uiGooseManager.currentObject;
			uiGooseManager.backViewIdentifier = uiGooseManager.viewIdentifier;
			uiGooseManager.viewIdentifier = SourceFiles.CONTACT_PROFILE;
			uiGooseManager.currentObject =  new UIGooseViewProfile(uiGooseManager, data);
			uiGooseManager.currentObject.show();
			//this.uiGooseRecordStore.writeAllProfiles(data, SourceFiles.RECORD_STORE_ALL_PROFILE, 
			//	newEvent.getMessage().senderBTMacAddress, viewIdentifier);
			break;
		
			// TODO:  more cases if we want to go to menu inediatly
		default:
			return;
		}
		uiGooseManager.currentObject.show();
	}
	
	
	public static void addSelectedRecipient(UIGooseManager uiGooseManager) {
		Vector aux = ((UIGooseNewMessageMenuView)uiGooseManager.currentObject).vSendTo;
		// We ave one element more due the element BCAST
		if (uiGooseManager.index == 0) {
			((UIGooseNewMessageMenuView)uiGooseManager.currentObject).booleanBCAST = true;
			switch (uiGooseManager.backViewIdentifier) {
			case SourceFiles.MESSAGES_NEW_TEXT:
				uiGooseManager.viewIdentifier = SourceFiles.MESSAGES_NEW_TEXT;
				((UIGooseNewMessageMenuView)uiGooseManager.currentObject).fillNewMessageText(uiGooseManager);
				break;
			case SourceFiles.MESSAGES_NEW_VOICE:
				uiGooseManager.viewIdentifier = SourceFiles.MESSAGES_NEW_VOICE;
				((UIGooseNewMessageMenuView)uiGooseManager.currentObject).fillNewMessageVoice(uiGooseManager);
				break;
			default:
				return;
			}
			uiGooseManager.currentObject.show();
			return;
		}
		Contact uiContact = (Contact)uiGooseManager.contacts.elementAt(uiGooseManager.index - 1);
		
		if ( aux.contains(uiContact)) {
			((UIGooseNewMessageMenuView)uiGooseManager.currentObject).vSendTo.removeElement(uiContact);
		}
		else  {
			((UIGooseNewMessageMenuView)uiGooseManager.currentObject).vSendTo.addElement(uiContact);
		}
		//System.out.println("addmenu baCKviewidentifier "+uiGooseManager.backViewIdentifier );
		switch (uiGooseManager.backViewIdentifier) {
		case SourceFiles.MESSAGES_NEW_TEXT:
			uiGooseManager.viewIdentifier = SourceFiles.MESSAGES_NEW_TEXT;
			((UIGooseNewMessageMenuView)uiGooseManager.currentObject).fillNewMessageText(uiGooseManager);
			break;
		case SourceFiles.MESSAGES_NEW_VOICE:
			uiGooseManager.viewIdentifier = SourceFiles.MESSAGES_NEW_VOICE;
			((UIGooseNewMessageMenuView)uiGooseManager.currentObject).fillNewMessageVoice(uiGooseManager);
			break;
		default:
			return;
		}
		uiGooseManager.currentObject.show();
		
	}
	
	public static void sendMessage(UIGooseManager uiGooseManager) {
		((UIGooseNewMessageMenuView)uiGooseManager.currentObject).setPriority(uiGooseManager);
		uiGooseManager.currentObject.show();
		//log.debug("Finished setPriority");
		String content = ((UIGooseNewMessageMenuView)uiGooseManager.currentObject).content;
		String contentType = ((UIGooseNewMessageMenuView)uiGooseManager.currentObject).contentType;
		int value = ((UIGooseNewMessageMenuView)uiGooseManager.currentObject).value;
		String txMode = ((UIGooseNewMessageMenuView)uiGooseManager.currentObject).txMode;
		Vector vAux = ((UIGooseNewMessageMenuView)uiGooseManager.currentObject).vSendTo;
		
		UIGooseThreadSendMessage uiGooseThreadSendMessage  = new UIGooseThreadSendMessage(uiGooseManager, 
				vAux, txMode, value, contentType, content);
		Thread uiGooseThreadSend = new Thread(uiGooseThreadSendMessage);
		uiGooseThreadSend.start();
		vAux = null;
		uiGooseManager.viewIdentifier = SourceFiles.MESSAGES_MENU;
		uiGooseManager.backViewIdentifier = SourceFiles.MAIN_MENU;
		uiGooseManager.backObject = new UIGooseMainMenuView(SourceFiles.MAIN_MENU, uiGooseManager,
				 uiGooseManager.display, uiGooseManager.width, uiGooseManager.height, uiGooseManager);
		uiGooseManager.currentObject= new UIGooseMessageMainMenuView(uiGooseManager);
		
		uiGooseManager.currentObject.show();
	}
	
	public static void addSeekFriend(UIGooseManager uiGooseManager) {
		

		
		String[] dataInput = new String[4];
		dataInput = MessageTools.readContactsACKMessage(uiGooseManager.contentMessage);
		uiGooseManager.viewIdentifier=SourceFiles.MAIN_MENU;
		
//		dataInput = MessageTools.readContactMessage(uiGooseManager.contentMessage);
		uiGooseManager.currentObject = new UIGooseMainMenuView(SourceFiles.MAIN_MENU, uiGooseManager,
				 uiGooseManager.display, uiGooseManager.width, uiGooseManager.height, uiGooseManager);
		try {
			uiGooseManager.gooseManager.createContact(dataInput);
			uiGooseManager.currentObject.show();
			} catch (GooseException e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
				//log.debug("Error when creating new contact in seekFriend ACK, after OK = TRUE ");
			}
	}
	
	public static void invokeGC(){
		try{
			System.gc();
			ForwardingManager.lastGc = GooseTools.getTime();
		}
		catch(Exception e){
			
		}
	}
	
	 private static Vector sortContactsByName(Vector contacts) throws GooseException{
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
	
	
	
	 public static byte[] readFile(String path,UIGooseManager uiGooseManager) {
		 
		 //log.debug("UIGoose. enterin in readFile function");
		 try{
			 uiGooseManager.c = javax.microedition.io.Connector.open("file:///Memory card/"+path, 
					 javax.microedition.io.Connector.READ_WRITE);
			 uiGooseManager.fc = (javax.microedition.io.file.FileConnection) uiGooseManager.c;
			 //log.debug("UIGoose. path opened "+uiGooseManager.fc.getClass());
				 if(!uiGooseManager.fc.exists()){
					 //log.debug("UIGoose. No file exits");
					 return null;
				 }
				// fc.create();
				 uiGooseManager.is = uiGooseManager.fc.openInputStream();
				 byte[] data =new byte[100];
				 uiGooseManager.is.read(data);
				 uiGooseManager.fc.close();
				 uiGooseManager.is.close();
				 return data;
		 }catch(Exception e) {
			 //log.debug("UIGoose. Error when reading data: "+e.getMessage());
			 return null;
		 }
	 }
	
	 private static int byteoffset = 0;
	 
	 public static void openFileToWrite(String path, UIGooseManager uiGooseManager) {
		 try{
			 uiGooseManager.c = javax.microedition.io.Connector.open("file:///Memory card/"+path, 
					 javax.microedition.io.Connector.READ_WRITE);
			 uiGooseManager.fc = (javax.microedition.io.file.FileConnection) uiGooseManager.c;
			
			 if(!uiGooseManager.fc.exists()){
				 uiGooseManager.fc.create();
			 }
			 else {
				 uiGooseManager.fc.truncate(0);
			 }
			
			 uiGooseManager.os = uiGooseManager.fc.openOutputStream();
			 ////log.debug("UIGoose. path opened "+uiGooseManager.fc.getClass());
		 }catch(Exception e) {
			 //log.debug("UIGoose. Error when opening data: "+e.getMessage());
			 return;
		 }
	 }
	 
	 
	 public static void closeFileToWrite(UIGooseManager uiGooseManager) {
		 try {
			 uiGooseManager.fc.close();
			 uiGooseManager.os.close();
		 }catch(Exception e) {
			 //log.debug("Error when closing file to write: "+e.getMessage());
		 }
	 }
	 
	 public static boolean writeFile(String path,byte[] data, UIGooseManager uiGooseManager) {
		 
		 try{
			 
			 uiGooseManager.os.write(data);
			 UIGooseFunction.byteoffset += data.length;
			 ////log.debug("Writing data in the file");
			 uiGooseManager.os.flush();
			
			 //log.debug("UIGoose. data wrote: "+data.length+ "index: "+ UIGooseFunction.byteoffset);
				 return true;
		 }catch(Exception e) {
			 //log.debug("UIGoose. Error when writing data: "+e.getMessage());
			 return false;
		 }
	 }
	 
 public static boolean writeFileContacts(UIGooseManager uiGooseManager) {
	 	javax.microedition.io.Connection cContacts = null;
		java.io.OutputStream osContacts;
		javax.microedition.io.file.FileConnection fcContacts= null;
		 try{
			 cContacts = javax.microedition.io.Connector.open("file:///Memory card/"+SourceFiles.stringPathContacts, 
					 javax.microedition.io.Connector.READ_WRITE);
			 fcContacts = (javax.microedition.io.file.FileConnection) cContacts;
			 
			 if(!fcContacts.exists()){
				 fcContacts.create();
			 }
			 else {
				 fcContacts.truncate(0);
			 }
			 osContacts = fcContacts.openOutputStream();
			 Vector vectorAux = uiGooseManager.gooseManager.getContacts();
			 Contact contactAux;
			 byte[] data;
			 osContacts.write(("Contacts"+"\t"+"\t"+"\t" +"Mac address"+"\t"+"\t"+"\t"+ "Ranking"
					 +"\n").getBytes());
			 for (int i=0; i< vectorAux.size(); i++) {
				 contactAux = (Contact)vectorAux.elementAt(i);
				 data = (contactAux.getName()+ "  " + contactAux.getSurname() + '\t').getBytes();
				 osContacts.write(data);
				 data = ("\t"+"\t"+"\t"+contactAux.getBluetoothAddress()).getBytes();
				 osContacts.write(data);
				 data = ("\t"+"\t"+"\t"+contactAux.getScore()).getBytes();
				 osContacts.write(data);
				 osContacts.write(("\n").getBytes());
				 //UIGooseFunction.byteoffset += data.length;
				 ////log.debug("Writing data in the file");
				 osContacts.flush();
			 }
			 fcContacts.close();
			 osContacts.close();
			 //log.debug("UIGoose. data wrote: "+ "index: "+ UIGooseFunction.byteoffset);
				 return true;
		 }catch(Exception e) {
			 //log.debug("UIGoose. Error when writing data contacts : "+e.getMessage());
			 return false;
		 }
	 }
	 
	 public static void updateGooseWall(GooseMessage gooseMessage, UIGooseManager uiGooseManager) {
		 /*
		 Contact contact = new Contact(gooseMessage.senderName, gooseMessage.senderSurname, gooseMessage.senderBTMacAddress, gooseMessage.senderPhoneNumber);
		 
		 String gooseMessageContent = "//name:" +  gooseMessage.senderName +  SourceFiles.SPACER+
			 gooseMessage.content.substring(SourceFiles.GOOSE_IDENTIFIER.length());
		 
		 byte data[] = gooseMessageContent.getBytes();
		 
		 uiGooseManager.uiGooseRecordStore.writeNewTweete(data, SourceFiles.RECORD_STORE_GOOSE_WALL, uiGooseManager, contact);
		 */
		 
	 }

	 
	 
}
