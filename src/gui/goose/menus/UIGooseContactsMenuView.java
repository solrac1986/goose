package gui.goose.menus;

import goose.contactsManager.Contact;
import gui.goose.resources.UIGooseView;

import java.io.IOException;
import java.util.Vector;
import javax.microedition.lcdui.*;

import gui.goose.resources.*;



public class UIGooseContactsMenuView extends Form implements UIGooseView {

	//private int viewIdentifier = null;
	
	private Display display = null;
	private List listView = null;
	
	public Vector vList = null;
	
	public static final String CONTACT_LIST_MENU="Contacts List";
	
	public UIGooseContactsMenuView(int viewIdentifier, CommandListener cmdListener,
				Display display, Vector contactList){
		super(null);
		this.display = display;
		
		
		listView = new List("Contact List", Choice.IMPLICIT);
		String name, surname;
		
		Image uiCustomImage = null;
		try {
			uiCustomImage = Image.createImage(SourceFiles.SOURCE_BUDDY);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			
		}
		
		for(int i = 0; i < contactList.size(); i++) {
			name = (((Contact)(contactList.elementAt(i))).getName());
			surname = (((Contact)(contactList.elementAt(i))).getSurname());
			listView.append(name + " " + surname, uiCustomImage);
		}
		
		listView.addCommand(SourceFiles.backCommand);
		//listView.addCommand(SourceFiles.logCommand);
		listView.setCommandListener(cmdListener);
		
		
		
		
	}
	
	
	public void destroy() {
		// TODO Auto-generated method stub
		
		listView.deleteAll();
	}

	public void show() {
		// TODO Auto-generated method stub
		this.setTitle(CONTACT_LIST_MENU);
		display.setCurrent(listView);
		//Display display = Display.getDisplay(uiGooseManager);
		
	}


}
