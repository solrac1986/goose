package gui.goose.resources;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.Font;

public final  class SourceFiles {

	public static final String CONTACTS_VIEW ="CONTACTS_VIEW";
	
	
	
	public static final  String SOURCE_BUDDY_BIG = "/BuddyGooseBig.png";
	public static final  String SOURCE_BUDDY = "/BuddyGooseSmall.png";
	public static final  String SOURCE_PHONE = "/PhoneNumberGooseSmall.png";
	public static final  String SOURCE_BT = "/BluetoothGooseSmall.png";
	public static final  String SOURCE_TIME = "/TimeStampGooseSmall.png";
	public static final  String SOURCE_SCORE = "/RankContactsSmall.png";
	public static final  String SOURCE_RANK_WATCH = "/RankContactsWatchBig.png";
	
	public static final  String SOURCE_LIST = "/ContactsGooseBig.png";
	public static final  String SOURCE_SEARCH = "/SearchGooseBig.png";
	public static final  String SOURCE_NEW = "/NewContactGooseBig.png";
	public static final  String SOURCE_TOP = "/RankContactsBig.png";
	
	public static final  String SOURCE_DELETE  = "/BinGooseBig.png";
	
	 public static final   String SOURCE_START = "/SearchGooseBig.png";
	 public static final   String SOURCE_BUDDY_UNKNOWN = "/SeekFriendsGooseSmall.png";
	 public static final   String SOURCE_CONTACT_NEW_SMALL = "/NewContactGooseSmall.png";
	 
	 public static final  String SOURCE_SWITCH_OFF = "/SwitchOffBig.png";
	 
	 public static final   String SOURCE_SEEK_MENU= "/SeekFriendsGooseBig.png";
	//Contact details
	
	public static final   String SOURCE_CONTACTS = "/ContactsGooseBig.png";
	public static final   String SOURCE_MESSAGES = "/MessagesGooseBig.png";
	public static final   String SOURCE_SCAN  = "/BluetoothGooseBig.png";
	
	 public static final   String SOURCE_INBOX = "/ReceivedMessageGooseBig.png";
	 public static final   String SOURCE_OUTBOX = "/SentMessagesGooseBig.png";
	 public static final   String SOURCE_NEW_MESSAGE = "/NewMessageGooseBig.png";
	
	 public static final   String SOURCE_VOICE = "/VoiceMessageGooseSmall.png";
	 public static final   String SOURCE_TEXT = "/TextGooseMessageSmall.png";
	 public static final   String SOURCE_ADD_PLUS = "/GreenPlusIconGooseSmall.png";
	 public static final   String SOURCE_BCAST = "/BroadcastGooseSmall.png";
	
	 public static final   String SOURCE_BUDDY_GREEN = "/BuddyGooseSmall.png";
	 public static final   String SOURCE_BUDDY_RED = "/BuddyGooseRedSmall.png";
	
	 public static final   String SOURCE_MICROPHONE = "/VoiceMessageGooseBig.png";
	 public static final   String SOURCE_TEXT_BIG = "/TextGooseMessageBig.png";
	
	
	 public static final   String SOURCE_ANSWER = "/ReplyGooseBig.png";
	 public static final String SOURCE_SENDING = "/SentMessageGooseSmall.png"; 
	
	 public static final   String SOURCE_RECORD_VOICE = "/RecordGooseBig.png";
	 public static final   String SOURCE_STOP_VOICE = "/StopGooseBig.png";
	 public static final   String SOURCE_LISTEN_VOICE = "/PlayVoiceMessageGooseBig.png";
	
	 //Configuration images
	 public static final String SOURCE_CONFIGURATION = "/ConfigurationGooseBig.png";
	 public static final String SOURCE_DELETE_CONTACTS = "/DeleteGooseContactsBig.png";
	 public static final String SOURCE_DELETE_MESSAGES ="/DeleteGooseMessagesBig.png";
	 public static final String SOURCE_EDIT_PROFILE ="/EditContactGooseBig.png";
	 
	 //Goose Wall
	 public static final String SOURCE_WALL_GOOSE= "/WallGooseBig.png";
	 public static final String SOURCE_UPDATE_STAUTS = "/UpdateWallGooseBig.png";
	
	
	 
	 //Command
	 
	 public static final Command backCommand = new Command("Back", Command.BACK, 1);
	 public static final Command mainMenu = new Command("MainMenu", Command.OK, 0);
	 //public static final Command exitCommand = new Command("switch off", Command.STOP, 0);
	 //public static final Command stopCommand =  new Command("STOP", Command.STOP, 0);
	 public static final Command okCommand = new Command("Ok", Command.SCREEN, 1);
	 public static final Command newCommand = new Command("New Message", Command.SCREEN, 1);
	 //public static final Command editCommand = new Command("EditContact", Command.SCREEN, 1);
	 //public static final Command deleteCommand = new Command("Delete", Command.SCREEN, 1);
	 public static final Command sendCommand = new Command("Message", Command.SCREEN, 1);
	 public static final Command pauseCommand = new Command("Pause", Command.BACK, 0);
	 
	 // Alert push Command
	 public static final Command OK_ALERT_COMMAND = new Command("Yes", Command.OK, 0);
	 public static final Command CANCEL_ALERT_COMMAND = new Command("No", Command.CANCEL, 0);

	 
	 // DEbuggin 
	 public static final Command logCommand = new Command("LOG", Command.SCREEN, 3);
	 
	 
	 public static final int LOGIN_MENU = 0;
	 public static final int MAIN_MENU = 1;
	 public static final int CONTACT_MENU = 2;
	 public static final int MESSAGES_MENU = 3;
	 public static final int EXCHANGE_MENU = 4;
	 public static final int SEEK_MENU = 5;
	 public static final int CONTACT_LIST_MENU = 6;
	 public static final int CONTACT_NEW_MENU = 7;
	 public static final int CONTACT_SEARCH_MENU = 8;
	 public static final int CONTACT_TOP_MENU = 9;
	 public static final int CONTACT_EDIT_MENU = 10;
	 public static final int CONTACT_DETAIL_MENU = 11;
	 public static final int CONTACT_SEARCH_RESULTS_MENU = 12;
	 public static final int INOUT_BOX_MENU = 13;
	 public static final int MESSAGE_DETAIL_MENU = 14;
	 public static final int NEW_MESSAGE_MENU = 15;
	 public static final int VOICE_MESSAGE_MENU = 16;
	 public static final int TEXT_MESSAGE_MENU = 17;
	 public static final int LISTEN_MESSAGE_MENU = 18;
	 public static final int EXCHANGE_RESULTS_MENU = 19;
	 public static final int CONTACT_TOP_RESULTS = 20;
	 public static final int MESSAGES_INBOX = 21;
	 public static final int MESSAGES_OUTBOX = 22;
	 public static final int MESSAGES_NEW = 23;
	 public static final int MESSAGES_NEW_TEXT = 24;
	 public static final int MESSAGES_NEW_VOICE = 25;
	 public static final int MESSAGES_NEW_ADD = 26;
	 public static final int SEEK_MENU_START = 27;
	 public static final int CONFIGURATION  = 28;
	 public static final int PERSONAL_PROFILE = 29;
	 public static final int CONTACT_LAST  = 30;
	 public static final int CONTACT_PROFILE  = 31;
	 public static final int SELECT_PROFILE_MENU  = 32;
	 public static final int GOOSE_WALL  = 33;
	 public static final int UPDATE_STATUS  = 34;
	 public static final int SELECT_PROFILE = 35;
	 
	 //public static final int LOGIN_MENU = 0;
	 
	 
	 //Alert type:
	 
	 public static final int PROFILE_REQUEST  = -1;
	 
	 //GOOSE WALL
	 
	 public static String GOOSE_UPDATE_STATUS = "goose_update_status";
	 public static String gooseWallContent = "//goosecontent:";
	 public static String GOOSE_IDENTIFIER = "2555";
	 //Font to not read messages
	 
	 public static Font fontUnread = Font.getFont(Font.FACE_PROPORTIONAL, Font.STYLE_BOLD, Font.SIZE_LARGE);
	 
	 public static Font fontGooseStatusText = Font.getFont(Font.FACE_MONOSPACE, Font.STYLE_ITALIC, Font.SIZE_SMALL);
	 
	 //Font small
	 
	 public static Font fontSmall = Font.getFont(Font.FACE_PROPORTIONAL, Font.STYLE_BOLD, Font.SIZE_SMALL);
	
	
	 
	 //File Path:
	 
	 public static String stringPath = "dataTesting.txt";
	 public static String stringPathContacts = "dataContacts.txt";
	 
	 //Record Store name:
	 
	 public static String RECORD_STORE_GOOSE_WALL = "RecordStoreGooseWall";
	 public static String RECORD_STORE_ALL_PROFILE = "RecordStoreAllProfile";
	 
	 public static String STARTER = "//";
	 public static String SPACER = ";";
	 public static String SPACER_END = ";//;";
	 
}
