package gui.goose.menus;

import gui.goose.resources.*;

import java.util.Vector;


import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Form;


public class UIGooseConfigurationMenuView extends Form implements UIGooseView{

	

	private Display display;
	public Vector vMenu = new Vector();

	
	public UIGooseConfigurationMenuView(UIGooseManager uiGooseManager){
		super(null);
		
//		log = Logger.getLogger();
		display = Display.getDisplay(uiGooseManager);
		
		this.setTitle("Configuration");
		
		vMenu.addElement(new UICustomItem("Del. Contacts", uiGooseManager.width, uiGooseManager.height, uiGooseManager));
		((UICustomItem)vMenu.lastElement()).setImage(SourceFiles.SOURCE_DELETE_CONTACTS);
		this.append((UICustomItem)vMenu.lastElement());
		
		vMenu.addElement(new UICustomItem("Del. Messages", uiGooseManager.width, uiGooseManager.height, uiGooseManager));
		((UICustomItem)vMenu.lastElement()).setImage(SourceFiles.SOURCE_DELETE_MESSAGES);
		this.append((UICustomItem)vMenu.lastElement());
		
		vMenu.addElement(new UICustomItem("Edit Profile", uiGooseManager.width, uiGooseManager.height, uiGooseManager));
		((UICustomItem)vMenu.lastElement()).setImage(SourceFiles.SOURCE_EDIT_PROFILE);
		this.append((UICustomItem)vMenu.lastElement());
	
		vMenu.addElement(new UICustomItem("Write test", uiGooseManager.width, uiGooseManager.height, uiGooseManager));
		((UICustomItem)vMenu.lastElement()).setImage(SourceFiles.SOURCE_EDIT_PROFILE);
		this.append((UICustomItem)vMenu.lastElement());
		
		vMenu.addElement(new UICustomItem("Delete test", uiGooseManager.width, uiGooseManager.height, uiGooseManager));
		((UICustomItem)vMenu.lastElement()).setImage(SourceFiles.SOURCE_DELETE_MESSAGES);
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
