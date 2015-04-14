package gui.goose.menus;


import goose.contactsManager.Contact;
import gui.goose.resources.SourceFiles;
import gui.goose.resources.UICustomItem;
import gui.goose.resources.UIGooseView;


import java.util.Date;
import java.util.Vector;

import javax.microedition.lcdui.*;

import gui.goose.resources.*;

public class UIGooseContactDetailsMenuView extends Form implements UIGooseView {

	private Display display;
	
	public Vector vMenu = null;
	
	
	public UIGooseContactDetailsMenuView(UIGooseManager uiGooseManager, Contact contact) {
		
		super(null);
		
		display = Display.getDisplay(uiGooseManager);
		
		
		Date date = new Date();
		Contact auxContact = null;
		auxContact = contact;
		vMenu = new Vector();
		
		long now = date.getTime();
		long num = (now - auxContact.getTimeStamp()) / (1000 );
		String time = "";
		int days =0;
		int hours = 0;
		int minutes = 0;
		if (num<60){
			time=num+"s";
		}
		else if (num>=60 && num <=60*60){
			minutes = (int)num/60;
			time=minutes+"m";
		}
		else if (num>=60*60 && num<=60*60*24){
			hours = (int)num/(60*60);
			time = hours+"h";
		}
		else{
			days = (int)num/(24*60*60);
			time=days+" days ";
		}
		
		time+= " ago";
		this.setTitle("Contact details");
		
		
		vMenu.addElement(new UICustomItem(" " + auxContact.getName() + " " + auxContact.getSurname(), uiGooseManager.width, uiGooseManager.height, uiGooseManager));
		((UICustomItem)vMenu.lastElement()).setImage(SourceFiles.SOURCE_BUDDY);
		this.append((UICustomItem)vMenu.lastElement());

		
		vMenu.addElement(new UICustomItem(" " + auxContact.getPhoneNumber(), uiGooseManager.width, uiGooseManager.height, uiGooseManager));
		((UICustomItem)vMenu.lastElement()).setImage(SourceFiles.SOURCE_PHONE);
		this.append((UICustomItem)vMenu.lastElement());
		
		
	
		vMenu.addElement(new UICustomItem(auxContact.getBluetoothAddress(), uiGooseManager.width, uiGooseManager.height, uiGooseManager));
		((UICustomItem)vMenu.lastElement()).setImage(SourceFiles.SOURCE_BT);
		this.append((UICustomItem)vMenu.lastElement());
			
		
		vMenu.addElement(new UICustomItem(time, uiGooseManager.width, uiGooseManager.height, uiGooseManager));
		((UICustomItem)vMenu.lastElement()).setImage(SourceFiles.SOURCE_TIME);
		this.append((UICustomItem)vMenu.lastElement());	
			

		vMenu.addElement(new UICustomItem("Score: " + Integer.toString(auxContact.getScore()), uiGooseManager.width, uiGooseManager.height, uiGooseManager));
		((UICustomItem)vMenu.lastElement()).setImage(SourceFiles.SOURCE_SCORE);
		this.append((UICustomItem)vMenu.lastElement());
		
		
		
		this.addCommand(SourceFiles.backCommand);
		//this.addCommand(SourceFiles.logCommand);
		this.setCommandListener(uiGooseManager);
		
		
	}

	public void destroy() {
		// TODO Auto-generated method stub
		this.deleteAll();
		vMenu = null;
	}

	public void show() {
		// TODO Auto-generated method stub
		display.setCurrent(this);
	}
}
