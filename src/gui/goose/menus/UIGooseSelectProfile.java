package gui.goose.menus;

import java.io.IOException;

import goose.contactsManager.Contact;
import gui.goose.resources.*;

import javax.microedition.lcdui.Choice;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.List;

public class UIGooseSelectProfile extends Form implements UIGooseView {

	private Display display;
	public static List list;
	
	
	public UIGooseSelectProfile(UIGooseManager uiGooseManager) {
		super(null);
		
		display = Display.getDisplay(uiGooseManager);
		list = new List("Select Profile", Choice.IMPLICIT);
		 Image uiCustomImage = null;
			try {
				uiCustomImage = Image.createImage(SourceFiles.SOURCE_CONTACT_NEW_SMALL);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				
			}
		list.append("New user", uiCustomImage);
		list.setCommandListener(uiGooseManager);
		//list.addCommand(SourceFiles.logCommand);
		
	}
	
	
	public void destroy() {
		// TODO Auto-generated method stub
		list.deleteAll();
	}

	public void show() {
		// TODO Auto-generated method stub
		display.setCurrent(list);
	}

}
