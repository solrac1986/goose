package gui.goose.menus;

import gui.goose.resources.UIGooseView;

import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Form;
import java.util.Vector;

import gui.goose.resources.SourceFiles;
import gui.goose.resources.UICustomItem;


public class UIGooseMessageMainMenuView extends Form implements UIGooseView{

	private Display display;
	public Vector vMenu = null;
	
	public UIGooseMessageMainMenuView(UIGooseManager uiGooseManager) {
		super(null);
		
		vMenu = new Vector();
		
		this.display = uiGooseManager.display;
		
		this.setTitle("Messages Menu");
		
		vMenu.addElement(new UICustomItem("New", uiGooseManager.width, uiGooseManager.height, uiGooseManager));
		((UICustomItem)vMenu.lastElement()).setImage(SourceFiles.SOURCE_NEW_MESSAGE);
		this.append((UICustomItem)vMenu.lastElement());
		
		vMenu.addElement(new UICustomItem("Update Status", uiGooseManager.width, uiGooseManager.height, uiGooseManager));
		((UICustomItem)vMenu.lastElement()).setImage(SourceFiles.SOURCE_UPDATE_STAUTS);
		this.append((UICustomItem)vMenu.lastElement());
		
		
		vMenu.addElement(new UICustomItem("Goose Wall", uiGooseManager.width, uiGooseManager.height, uiGooseManager));
		((UICustomItem)vMenu.lastElement()).setImage(SourceFiles.SOURCE_WALL_GOOSE);
		this.append((UICustomItem)vMenu.lastElement());
		
		vMenu.addElement(new UICustomItem("Received", uiGooseManager.width, uiGooseManager.height, uiGooseManager));
		((UICustomItem)vMenu.lastElement()).setImage(SourceFiles.SOURCE_INBOX);
		this.append((UICustomItem)vMenu.lastElement());
		
		vMenu.addElement(new UICustomItem("Sent", uiGooseManager.width, uiGooseManager.height, uiGooseManager));
		((UICustomItem)vMenu.lastElement()).setImage(SourceFiles.SOURCE_OUTBOX);
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
