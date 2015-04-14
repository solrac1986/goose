package gui.goose.menus;

import java.util.Date;
import java.util.Vector;

import gui.goose.resources.*;
import goose.forwardingManager.GooseMessage;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.TextField;

import microlog.Logger;

public class UIGooseUpdateStatusMenuView extends Form implements UIGooseView{

	private int MAX_SIZE = 50;
	
	private Display display;
	private TextField textFieldStatus;
	
	//Debuggin
	
	private static Logger log = Logger.getLogger();
	
	
	public UIGooseUpdateStatusMenuView(UIGooseManager uiGooseManager) {
		super(null);
		
		display = Display.getDisplay(uiGooseManager);
		
		textFieldStatus = new TextField("Status: ", "",  MAX_SIZE, TextField.ANY);
		
		this.append(textFieldStatus);
		
		this.addCommand(SourceFiles.backCommand);
		this.addCommand(SourceFiles.okCommand);
		//this.addCommand(SourceFiles.logCommand);
		this.setCommandListener(uiGooseManager);
		
	}
	
	
	
	public void destroy() {
		// TODO Auto-generated method stub
		this.deleteAll();
	}

	public void show() {
		// TODO Auto-generated method stub
		display.setCurrent(this);
	}
	
	public String updateStatus (UIGooseManager uiGooseManager) {
		
		Date date = new Date();
		
		String contentgoose =  textFieldStatus.getString();
		String message =contentgoose;
		////log.debug("UIGoose. content message sent: "+ message);
		return message;
		

		
	}
	

}
