 package gui.goose.menus;

import goose.contactsManager.Contact;
import goose.exceptions.GooseSearchException;
import goose.forwardingManager.GooseMessage;
import goose.forwardingManager.MessageTools;
import goose.mainManager.IncomingMessageEvent;
import gui.goose.exceptions.GooseAlert;
import gui.goose.resources.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.Vector;

import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.AlertType;
import javax.microedition.lcdui.Choice;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.Gauge;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.Item;
import javax.microedition.lcdui.List;
import javax.microedition.lcdui.StringItem;
import javax.microedition.lcdui.TextField;
import javax.microedition.media.*;
import javax.microedition.media.control.RecordControl;
import javax.microedition.media.control.VolumeControl;


import microlog.Logger;

//import microlog.Logger;



public class UIGooseNewMessageMenuView extends Form implements UIGooseView, PlayerListener{

	private final int MAX_PRIORITY  = 5;
	
	//Size message voice
	private final int SIZE_VOICE_LIMIT = 18000;
	
	public Vector vMenu;
	public boolean booleanBCAST = false;
	
	private Logger log = Logger.getLogger();
	
	
	public int value = 1;
	public String contentType = GooseMessage.TEXT;
	public String txMode = GooseMessage.UCAST;
	public String content = "";
	
	//Rec message
	private Player player;
	private byte[] recordedSoundArray = null;
	private RecordControl rc;
	private ByteArrayOutputStream output;
	private StringItem errorItem = null;
	private UICustomItem uiStop;
	private UICustomItem uiPlay;
	
	public static boolean isRecording = false;
	public static boolean isPlaying = false;
	
	
	private boolean bAdd;
	private boolean bVoice = false;
	private boolean bText = false;
	private Display display;
	public Vector vSendTo = new Vector();
	
	private TextField textFieldMessage;
	
	public UIGooseNewMessageMenuView (UIGooseManager uiGooseManager) {
		super(null);
		
		display = Display.getDisplay(uiGooseManager);
		this.setCommandListener(uiGooseManager);
		bAdd = false;
		vSendTo = new Vector();
		fillNewMessage(uiGooseManager, null);
		
	}
	
	
	
	public void destroy() {
		// TODO Auto-generated method stub
		this.deleteAll();
		vMenu = null;
	}

	public void show() {
		// TODO Auto-generated method stub
		if (! bAdd) {
			display.setCurrent(this);
		}
		
	}
	
	
	public void fillNewMessage(UIGooseManager uiGooseManager, Contact recipient) {
		
		
		
		this.deleteAll();
		this.setTitle("New message");
		
		vMenu = new Vector();
		bVoice = bText = false;
		
		
		
		UICustomItem uiVoice;
		//TODO: Add recipient, respond message inbox directly.
		if (recipient != null) {
			vSendTo.addElement(recipient);
		}
		
		UICustomItem uiText;
	
		textFieldMessage = new TextField("Text", "", 250,TextField.ANY);
		
		uiStop = new UICustomItem("REC", uiGooseManager.width, uiGooseManager.height, uiGooseManager);
		uiStop.setImage(SourceFiles.SOURCE_RECORD_VOICE);
		uiText = new UICustomItem("Text", uiGooseManager.width, uiGooseManager.height, uiGooseManager);
		uiText.setImage(SourceFiles.SOURCE_TEXT_BIG);
		uiText.setLayout(Item.LAYOUT_NEWLINE_BEFORE | Item.LAYOUT_LEFT | Item.LAYOUT_CENTER);
		vMenu.addElement(uiText);
		uiVoice = new UICustomItem("Voice", uiGooseManager.width, uiGooseManager.height, uiGooseManager);
		uiVoice.setImage(SourceFiles.SOURCE_MICROPHONE);
		uiVoice.setLayout(Item.LAYOUT_LEFT | Item.LAYOUT_BOTTOM | Item.LAYOUT_CENTER);
		vMenu.addElement(uiVoice);
		
		this.append(uiText);
		//this.append(new Spacer(50,50));
		this.append(uiVoice);
		
		this.addCommand(SourceFiles.backCommand);
		if (uiGooseManager.backViewIdentifier == SourceFiles.MESSAGES_NEW_TEXT
				|| uiGooseManager.backViewIdentifier == SourceFiles.MESSAGES_NEW_VOICE) {
			this.removeCommand(SourceFiles.okCommand);
		}
		
		this.setCommandListener(uiGooseManager);
//		DEbuggin
		//this.addCommand(SourceFiles.logCommand);
		// end debuggin
		
		
	}
	
	public void fillNewMessageVoice(UIGooseManager uiGooseManager) {
		
		isRecording = false;
		isPlaying = false;
		
		bAdd = false;
		bVoice = true;
		
		this.deleteAll();
		this.setTitle("New voice message ");
		vMenu = new Vector();
		
		UICustomItem uiAdd;
		
		//vSendTo = addMenu(uiGooseManager, vSendTo);
		
		String text = "Send To:  ";
		
		if (booleanBCAST) {
			text = "BCAST";
		}
		else {
			if (vSendTo.size() != 0) {
				Contact aux = (Contact)vSendTo.elementAt(0);
				text  = aux.getName();
				if (vSendTo.size() > 1) {
					text += "...";
				}
			}
			 
//			Contact aux;
//			for (int i = 0; i < vSendTo.size(); i++) {
//				aux = (Contact)vSendTo.elementAt(i);
//				text += aux.getName() + ", ";
//			}
		}
		
		//stringItem.setText(text);
		uiAdd = new UICustomItem(text, uiGooseManager.width, uiGooseManager.height, uiGooseManager);
		uiAdd.setImage(SourceFiles.SOURCE_ADD_PLUS);
		uiPlay = new UICustomItem("Play", uiGooseManager.width, uiGooseManager.height, uiGooseManager);
		uiPlay.setImage(SourceFiles.SOURCE_LISTEN_VOICE);
		
		//uiStop.setLayout(Item.LAYOUT_NEWLINE_BEFORE | Item.LAYOUT_LEFT | Item.LAYOUT_CENTER);
		//uiPlay.setLayout(Item.LAYOUT_LEFT | Item.LAYOUT_BOTTOM | Item.LAYOUT_CENTER);
		vMenu.addElement(uiAdd);
		vMenu.addElement(uiStop);
		vMenu.addElement(uiPlay);
		
		this.append((UICustomItem)uiAdd);
		
		// bar graph
		Gauge gauge = new Gauge("Progress Bar", false, 60, 0);
		gauge.setLayout(Gauge.CONTINUOUS_RUNNING);
		gauge.setLayout(Item.LAYOUT_BOTTOM);
		//this.append(gauge);
		this.append((UICustomItem)uiStop);	
		this.append((UICustomItem)uiPlay);
		this.addCommand(SourceFiles.backCommand);
		this.addCommand(SourceFiles.okCommand);
		
//		DEbuggin
		//this.addCommand(SourceFiles.logCommand);
		// end debuggin
		
		
		
	}
	
	public void fillNewMessageText(UIGooseManager uiGooseManager) {
		
		bAdd = false;
		bText = true;
		bVoice = false;
		
		this.deleteAll();
		this.setTitle("New text message");
		
		vMenu = new Vector();
		UICustomItem uiAdd;
		
		
		//vSendTo = addMenu(uiGooseManager, vSendTo);
		
		String text = "Send To:  ";
		Contact aux;
		if (booleanBCAST) {
			text = "BCAST";
		}
		else {
			if (vSendTo.size() != 0) {
				aux = (Contact)vSendTo.elementAt(0);
				text  = aux.getName();
				if (vSendTo.size() > 1) {
					text += "...";
				}
			}
			 
//			for (int i = 0; i < vSendTo.size(); i++) {
//				aux = (Contact)vSendTo.elementAt(i);
//				text += aux.getName() + ", ";
//			}
		}
		
		//stringItem.setText(text);
		uiAdd = new UICustomItem(text, uiGooseManager.width, uiGooseManager.height, uiGooseManager);
		uiAdd.setImage(SourceFiles.SOURCE_ADD_PLUS);
		
		
		vMenu.addElement(uiAdd);
		this.append((UICustomItem)uiAdd);
		//this.append(stringItem);
		this.append(textFieldMessage);
		
		this.addCommand(SourceFiles.backCommand);
		this.addCommand(SourceFiles.okCommand);
		
//		DEbuggin
		//this.addCommand(SourceFiles.logCommand);
		// end debuggin
	
	}

	
	public void addMenu(UIGooseManager uiGooseManager) {
		
		bAdd = true;
		List list = new List("Contact List", Choice.IMPLICIT);
		try {
			uiGooseManager.contacts = uiGooseManager.gooseManager.getContacts();
		} catch (GooseSearchException e) {
			// TODO Auto-generated catch block		
			uiGooseManager.display.setCurrent(GooseAlert.createAlert("Search error" + e.getMessage()),
					list);
		}
		Image uiCustomImageGreen = null;
		Image uiCustomImageRed = null;
		Image imageBroadcast = null;
		try {
			uiCustomImageGreen = Image.createImage(SourceFiles.SOURCE_BUDDY_GREEN);
			uiCustomImageRed = Image.createImage(SourceFiles.SOURCE_BUDDY_RED);
			imageBroadcast = Image.createImage(SourceFiles.SOURCE_BCAST);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("Erro in images addmenu " + e.getMessage());
		}
		//listContact = new Vector();
		String name;
		String surname;
		Contact aux = new Contact();
		list.append("broadcast", imageBroadcast);
		for(int i = 0; i < uiGooseManager.contacts.size(); i++) {
			aux = (Contact)(uiGooseManager.contacts.elementAt(i));
			name = (aux.getName());
			surname = (aux.getSurname());
			if (vSendTo.contains(aux)) {
				list.append(name + " " + surname, uiCustomImageRed);
				//vSendTo.removeElement(aux);
			}
			else {
				//vSendTo.addElement(aux);
				list.append(name + " " + surname, uiCustomImageGreen);
			}
		}
		list.addCommand(SourceFiles.backCommand);
		list.setCommandListener(uiGooseManager);
		//DEbuggin
		list.addCommand(SourceFiles.logCommand);
		display.setCurrent(list);
		
	}
	
	
	public void setPriority(UIGooseManager uiGooseManager) {
		
		
		//log.debug("UIGoose. Starting to send message calcultating priority");
		
		
		this.deleteAll();
		StringItem stringItem = new StringItem("", "Sending.....");
		this.append(stringItem);
		show();
		
	
		if (bVoice){
			////log.debug("UIGoose. bVoice");
			contentType = GooseMessage.AUDIO;
			
			try {
				 
				content = MessageTools.convertByteArrayToString(recordedSoundArray) ;
				//log.debug("UIGoose. Tam voice message: " + content.length());
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				//log.debug("UIGoose. Error: "+e.getMessage());
				display.setCurrent(GooseAlert.createAlert("Send error" 
						+ e.getMessage()), this);
				
			}
		}
		else  {
			////log.debug("UIGoose. bText");
			contentType = GooseMessage.TEXT;
			content = textFieldMessage.getString();
		}
		
		if (booleanBCAST || vSendTo.size() == 0) {
			////log.debug("UIGoose. bBCAST");
			if (value == 0) {
				value = 1;
			}
			txMode = GooseMessage.BCAST;
		}

		else if (vSendTo.size() > 1) {
			////log.debug("UIGoose. MCAST");
			if (value == 0) {
				value = 2;
			}
			txMode = GooseMessage.MCAST;
		}
		
		else {
			////log.debug("UIGoose. UCAST");
			if (value == 0) {
				value = 3;
			}
			txMode = GooseMessage.UCAST;
		}

	}
	
	public  void recordMessage() {
		if (isPlaying || isRecording) {
			return;
		}
		
		uiStop.setImage(SourceFiles.SOURCE_STOP_VOICE);
		uiStop.setText("STOP");
		isRecording = true;
		
		//Start to record
		try {
			display.vibrate(500);
			
			player = Manager.createPlayer("capture://audio?encoding=amr");
			player.realize();
			player.addPlayerListener(this);
			VolumeControl control = (VolumeControl) player.getControl("VolumeControl");
			if (control!=null)
			{
//			 Set new volume level to 50% of maximum
			control.setLevel(50);
			}
			rc = (RecordControl)player.getControl("RecordControl");
			rc.setRecordSizeLimit(SIZE_VOICE_LIMIT);
			output = new ByteArrayOutputStream();
			rc.setRecordStream(output);                
            rc.startRecord();
            player.start();
		 } catch (IOException ioe) {
                errorItem.setLabel("Error");
                errorItem.setText(ioe.toString());
                display.setCurrent(GooseAlert.createAlert(errorItem 
						+ ioe.getMessage()), this);
            } catch (MediaException me) {
                errorItem.setLabel("Error");
                errorItem.setText(me.toString());
                display.setCurrent(GooseAlert.createAlert(errorItem 
						+ me.getMessage()),this);
            } catch (Exception ie) {
                errorItem.setLabel("Error");
                errorItem.setText(ie.toString());
                display.setCurrent(GooseAlert.createAlert(errorItem 
						+ ie.getMessage()), this);
            }
		////log.debug(uiStop.getText());
		
	}
	
	public void stopMessage() {
		if (isRecording) {
			uiStop.setImage(SourceFiles.SOURCE_RECORD_VOICE);
			uiStop.setText("REC");
			isRecording = false;
			try{
				display.vibrate(500);
	            //Thread.sleep(5000);
	            rc.commit();               
	            recordedSoundArray = output.toByteArray();     
	            player.stop();
	            player.close();
			}catch (Exception e){
				//log.debug("UIGoose. error when salving sound recorded: "+e.getMessage());
	    		errorItem.setLabel("error stoping");
	    		errorItem.setText(e.toString());
	    		 display.setCurrent(GooseAlert.createAlert(errorItem 
							+ e.getMessage()), this);
			}
		}
		else if (isPlaying) {
			uiPlay.setImage(SourceFiles.SOURCE_LISTEN_VOICE);
			uiPlay.setText("Play");
			isPlaying = false;
			try{
				display.vibrate(500);
	            //Thread.sleep(5000);
	            player.stop();
	            player.close();
			}catch (Exception e){
				//log.debug("UIGoose. error when salving sound recorded: "+e.getMessage());
	    		errorItem.setLabel("error stoping");
	    		errorItem.setText(e.toString());
	    		 display.setCurrent(GooseAlert.createAlert(errorItem 
							+ e.getMessage()), this);
			}
		}
		
	}
	
	
	
	public void playMessage() {
		if( isPlaying || isRecording) {
			return;
		}
		uiPlay.setImage(SourceFiles.SOURCE_STOP_VOICE);
		uiPlay.setText("STOP");
		isPlaying = true;
		isRecording = false;
		try {
        	display.vibrate(500);
            ByteArrayInputStream recordedInputStream = new ByteArrayInputStream
                  (recordedSoundArray);
            player = Manager.createPlayer(recordedInputStream,"audio/amr");
            player.addPlayerListener(this);
           // VolumeControl control = (VolumeControl) player.getControl("VolumeControl");
            player.prefetch();
            player.start();
        }  catch (IOException ioe) {
            errorItem.setLabel("Error");
            errorItem.setText(ioe.toString());
            display.setCurrent(GooseAlert.createAlert(errorItem 
					+ ioe.getMessage()), this);
        } catch (MediaException me) {
            errorItem.setLabel("Error");
            errorItem.setText(me.toString());
            display.setCurrent(GooseAlert.createAlert(errorItem 
					+ me.getMessage()), this);
        }
    
	}



	public void playerUpdate(Player player, String event, Object eventData) {
		// TODO Auto-generated method stub
		if (event.equalsIgnoreCase(PlayerListener.END_OF_MEDIA)) {
			display.vibrate(500);
			if (isPlaying) {
				uiPlay.setImage(SourceFiles.SOURCE_LISTEN_VOICE);
				uiPlay.setText("Play");
				uiPlay.setRepaint();
				isPlaying = false;
			}
			else {
				uiStop.setImage(SourceFiles.SOURCE_RECORD_VOICE);
				uiStop.setText("Rec");
				uiStop.setRepaint();
				isRecording = false;
			}
		}
		
	}
	
}
