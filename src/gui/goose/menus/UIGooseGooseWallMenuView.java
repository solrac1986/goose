package gui.goose.menus;

import java.io.IOException;
import java.util.Date;
import java.util.Vector;

import goose.exceptions.GooseException;
import goose.forwardingManager.ForwardingManager;
import goose.forwardingManager.GooseMessage;
import goose.mainManager.GooseManager;
import goose.mainManager.GooseTools;
import gui.goose.resources.*;

import javax.microedition.lcdui.Choice;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.List;
import javax.microedition.lcdui.StringItem;

import microlog.Logger;

public class UIGooseGooseWallMenuView extends Form implements UIGooseView {

	
	public Vector vMenu = new Vector();
	
	
	private Display display;
	
	//Debuggin:
	private static Logger log = Logger.getLogger();
	
	
	public UIGooseGooseWallMenuView(UIGooseManager uiGooseManager) {
		super(null);
		display = Display.getDisplay(uiGooseManager);
		
		
		this.addCommand(SourceFiles.backCommand);
		//this.addCommand(SourceFiles.logCommand);
		this.setCommandListener(uiGooseManager);
		
		Vector vector = new Vector();
		//TODO:
		//vector =  uiGooseManager.uiGooseRecordStore.readNewTweete(SourceFiles.RECORD_STORE_GOOSE_WALL);
		
		try {
			ForwardingManager fwMgr = (ForwardingManager)uiGooseManager.gooseManager.getManager(GooseManager.FORWARDING_MANAGER);
			vector = fwMgr.getStatusMessages();
		} catch (GooseException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			//log.debug("UIGoose. Error when taking goose status: "+e.getMessage());
		}
		if (vector.size() == 0) {
			//log.debug("UIGoose. No goose update messages");
			this.append("No status updates");
			show();
			return;
		}
		//log.debug("UIGoose. Trying to take name and date size vector: "+vector.size());
		
		
		
		
		Image uiCustomImage = null;
		try {
			uiCustomImage = Image.createImage(SourceFiles.SOURCE_BUDDY);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			
		}
		
		
		String stringData;
		String name;
		String surname;
		String date;
		String goosecontent;
		GooseMessage gooseMessage;
		int index;
		int indexEnd;
		StringItem[] stringItem = new StringItem[10];
		StringItem[] stringItemTitle = new StringItem[10];
		StringItem[] dateString = new StringItem[10];
		
		for (int i = 0; i< vector.size(); i++){
			
			gooseMessage = ((GooseMessage)vector.elementAt(i));
			stringData = gooseMessage.content;
			//log.debug("UIGOose. content message: "+stringData);
			//index = stringData.indexOf("//name:")+"//name:".length();
			//indexEnd = stringData.indexOf(";//surname:");
			name = gooseMessage.senderName;
			//name = "Carlos";
			stringItemTitle[i] = new StringItem("", "");
			stringItemTitle[i].setText(name+ ":");
			//index = stringData.indexOf(SourceFiles.gooseWallContent)+SourceFiles.gooseWallContent.length();
			//indexEnd = stringData.indexOf(SourceFiles.SPACER_END);
			//goosecontent = stringData.substring(index, indexEnd);
			stringItem[i] = new StringItem("","");
			stringItem[i].setText(" "+gooseMessage.content+ ". ");
			stringItem[i].setFont(SourceFiles.fontGooseStatusText);
			dateString[i] = new StringItem("","");
			dateString[i].setText(" "+getTimeMessage(gooseMessage, uiGooseManager)+"\n");

			//log.debug("Before displaying elements");
			this.append(uiCustomImage);
			this.append(stringItemTitle[i]);
			this.append(stringItem[i]);
			this.append(dateString[i]);
			//listView.append(name + "\n" + date + "\n" + goosecontent  , uiCustomImage);
		}
		
	}
	
	public void destroy() {
		// TODO Auto-generated method stub
		//listView.deleteAll();
		vMenu = null;
		this.deleteAll();
	}

	public void show() {
		// TODO Auto-generated method stub
		display.setCurrent(this);
	}
	
	private String getTimeMessage(GooseMessage gooseMessage, UIGooseManager uiGooseManager) {
		long timeMessage;
		
		timeMessage =( GooseTools.getTime() - gooseMessage.recTime)  / 1000;
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
