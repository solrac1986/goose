package gui.goose.menus;





import goose.exceptions.GooseException;
import goose.forwardingManager.GooseMessage;
import goose.forwardingManager.MessageTools;
import goose.mainManager.GooseTools;
import gui.goose.exceptions.GooseAlert;
import gui.goose.resources.SourceFiles;
import gui.goose.resources.UICustomItem;
import gui.goose.resources.UIGooseView;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.Vector;
import javax.microedition.lcdui.*;
import javax.microedition.media.Manager;
import javax.microedition.media.MediaException;
import javax.microedition.media.Player;
import javax.microedition.media.PlayerListener;
import javax.microedition.media.control.RecordControl;

import microlog.Logger;






public class UIGooseMessageDetailsMenuView extends Form implements UIGooseView, PlayerListener{

	private Display display;
	
//	Debuggin
	private Logger log = Logger.getLogger();
	
	public static boolean isAudio = false;
	public boolean isPlaying = false;
	
	//Listen message
	private Player player;
	private byte[] recordedSoundArray = null;
	private RecordControl rc;
	private ByteArrayOutputStream output;
	private StringItem errorItem;
	private UICustomItem uiPlay;

	public Vector vMenu = null;
	private GooseMessage gooseMessage;
	
	public UIGooseMessageDetailsMenuView(UIGooseManager uiGooseManager, int typeBox,GooseMessage message) {
		super(null);
		
		display = Display.getDisplay(uiGooseManager);
		vMenu = new Vector();
		fillDetailsMessage(typeBox, message, uiGooseManager);
		
		this.setCommandListener(uiGooseManager);
		this.addCommand(SourceFiles.backCommand);
		////this.addCommand(SourceFiles.logCommand);
	}
	
	
	public void destroy() {
		// TODO Auto-generated method stub
		this.deleteAll();
	}

	public void show() {
		// TODO Auto-generated method stub
		display.setCurrent(this);
	}

	public GooseMessage getMessage() {
		return gooseMessage;
	}
	
	private void fillDetailsMessage(int typeBox, GooseMessage message, UIGooseManager uiGooseManager) {
		
		this.deleteAll();
		//log.debug("UIGoose. Inside detail message");
		isAudio = false;
		this.setTitle("Message: ");
		gooseMessage = message;
		StringItem stringName = new StringItem("From: ", "");
		stringName.setLayout(Item.LAYOUT_EXPAND | Item.LAYOUT_NEWLINE_AFTER);
		StringItem stringContent = new StringItem("Content: ", "");
		stringContent.setLayout(Item.LAYOUT_EXPAND | Item.LAYOUT_NEWLINE_AFTER);
		
		StringItem stringTimeMessage = new StringItem("", "");
		
		GooseMessage gooseMessage = message; 
		
		if (typeBox == SourceFiles.MESSAGES_INBOX) {
			String timeMessage = getTimeMessage(message, uiGooseManager);
			stringTimeMessage.setText(timeMessage);
			stringName.setText(gooseMessage.senderName+ " "+ gooseMessage.senderSurname);
			
		}
		else  {
			
			stringName.setLabel("To: ");
			stringName.setText(gooseMessage.recipient);
			//TODO: Add time when message arrived 
		}
		
		if (gooseMessage.contentType.equalsIgnoreCase(GooseMessage.AUDIO)) {
			//Listen message
			fillListenMessage(uiGooseManager, gooseMessage, typeBox);
			return;
		}
		else if (gooseMessage.contentType.equalsIgnoreCase(GooseMessage.TEXT)) {
			stringContent.setText(gooseMessage.content);
		}
		UICustomItem uiAnswer, uiDelete;
		uiAnswer = new UICustomItem("Reply", uiGooseManager.width, uiGooseManager.height, uiGooseManager);
		uiAnswer.setImage(SourceFiles.SOURCE_ANSWER);
		vMenu.addElement(uiAnswer);
		uiDelete = new UICustomItem("Delete", uiGooseManager.width, uiGooseManager.height, uiGooseManager);
		uiDelete.setImage(SourceFiles.SOURCE_DELETE);
		
		vMenu.addElement(uiDelete);
		
		this.append(stringName);
		//Add time when message was sent or received
		if (typeBox == SourceFiles.MESSAGES_INBOX) {
			this.append(stringTimeMessage);
		}
		
		this.append(stringContent);

		if (typeBox == SourceFiles.MESSAGES_INBOX) {
			this.append((UICustomItem)uiAnswer);
		}
		this.append((UICustomItem)uiDelete);
		/*if (typeBox == SourceFiles.MESSAGES_INBOX) {
			uiAnswer.setLabel("Respond");
		}
		else if (typeBox == SourceFiles.MESSAGES_OUTBOX) {
			uiAnswer.setLabel("Resend");
		}*/
	}
	
	private void fillListenMessage (UIGooseManager uiGooseManager, GooseMessage gooseMessage, int typeBox) {
		
		//UICustomItem uiStop;
		
		
		isAudio = true;
		isPlaying = false;
		
		this.setTitle("Message voice");
		//log.debug("UIGoose. Message type voice");
		StringItem stringItem = new StringItem("", "");
		
		if (typeBox == SourceFiles.MESSAGES_INBOX){
			stringItem = new StringItem("From: ", gooseMessage.senderName);
		}
		else {
			stringItem = new StringItem("To: ", gooseMessage.recipient);
		}
		
		if (gooseMessage.contentType.equalsIgnoreCase(GooseMessage.AUDIO)) {
			//log.debug("UIGoose. AudioMessage type getting recorded sound form message");
			try {
				recordedSoundArray = MessageTools.convertStringToByteArray(gooseMessage.content);
			} catch (GooseException e) {
				// TODO Auto-generated catch block
				//log.debug("UIGoose. Error when converting sound: "+e.getMessage());
				uiGooseManager.display.setCurrent(GooseAlert.createAlert("Listen message error" 
						+ e.getMessage()), this);
			}
		}
		//log.debug("UIGoose. After reading the sound stream size: "+recordedSoundArray.length);
		UICustomItem uiAnswer, uiDelete;
		uiAnswer = new UICustomItem("Reply", uiGooseManager.width, uiGooseManager.height, uiGooseManager);
		uiAnswer.setImage(SourceFiles.SOURCE_ANSWER);
		uiDelete = new UICustomItem("Delete", uiGooseManager.width, uiGooseManager.height, uiGooseManager);
		uiDelete.setImage(SourceFiles.SOURCE_DELETE);
		
		uiPlay = new UICustomItem("Play", uiGooseManager.width, uiGooseManager.height, uiGooseManager);
		
		
		uiPlay.setImage(SourceFiles.SOURCE_LISTEN_VOICE);
		vMenu.addElement(uiPlay);
		vMenu.addElement(uiAnswer);
		vMenu.addElement(uiDelete);
		//uiPlay.setPreferredSize(40, 40);
		
		
		this.append(stringItem);
		
		this.append((UICustomItem)uiPlay);
		if (typeBox == SourceFiles.MESSAGES_INBOX) {
			this.append((UICustomItem)uiAnswer);
		}
		this.append((UICustomItem)uiDelete);
		
		
	}
	
	public void playMessage() {
		
		if (isPlaying) {
			return;
		}
		uiPlay.setImage(SourceFiles.SOURCE_STOP_VOICE);
		uiPlay.setText("STOP");
		isPlaying = true;
		try {

        	display.vibrate(500);	
            ByteArrayInputStream recordedInputStream = new ByteArrayInputStream
                  (recordedSoundArray);
            player = Manager.createPlayer(recordedInputStream,"audio/amr");
            player.addPlayerListener(this);
            player.prefetch();
            player.start();
        }  catch (IOException ioe) {
            errorItem.setLabel("Error");
            errorItem.setText(ioe.toString());
            //log.debug("ioe");
            display.setCurrent(GooseAlert.createAlert(errorItem 
					+ ioe.getMessage()), this);
        } catch (MediaException me) {
            errorItem.setLabel("Error");
            errorItem.setText(me.toString());
            display.setCurrent(GooseAlert.createAlert(errorItem 
					+ me.getMessage()), this);
        }
	}
	
	public void stopMessage() {
		if (isPlaying) {
			uiPlay.setImage(SourceFiles.SOURCE_LISTEN_VOICE);
			uiPlay.setText("Play");
			isPlaying = false;
		}
		try{
			display.vibrate(500);
            //Thread.sleep(5000);
            rc.commit();               
            recordedSoundArray = output.toByteArray();                
            player.close();
		}catch (Exception e){
    		errorItem.setLabel("error stoping");
    		errorItem.setText(e.toString());
    		 display.setCurrent(GooseAlert.createAlert(errorItem 
						+ e.getMessage()), this);
			}
		
	}
	
	private String getTimeMessage(GooseMessage gooseMessage, UIGooseManager uiGooseManager) {
		long timeMessage;
		
		timeMessage =( GooseTools.getTime() - uiGooseManager.gooseManager.getMessageTimeIn(gooseMessage.GUID) )  / 1000;
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

	
	public void playerUpdate(Player player, String event, Object eventData) {
		// TODO Auto-generated method stub
		if (event.equalsIgnoreCase(PlayerListener.END_OF_MEDIA)) {
			display.vibrate(500);
			uiPlay.setImage(SourceFiles.SOURCE_LISTEN_VOICE);
			uiPlay.setText("Play");
			uiPlay.setRepaint();
		}
		
	}
	
}
