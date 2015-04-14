package gui.goose.menus;

import gui.goose.resources.UIGooseView;

import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Form;
import java.util.Vector;

import gui.goose.resources.SourceFiles;
import gui.goose.resources.UICustomItem;


public class UIGooseContactMainMenuView extends Form implements UIGooseView{

	private Display display;
	public Vector vMenu = null;
	
	public UIGooseContactMainMenuView(UIGooseManager uiGooseManager) {
		super(null);
		
		vMenu = new Vector();
		
		this.display = uiGooseManager.display;
		
		this.setTitle("Contacts Menu");
		
		vMenu.addElement(new UICustomItem("Friends", uiGooseManager.width, uiGooseManager.height, uiGooseManager));
		((UICustomItem)vMenu.lastElement()).setImage(SourceFiles.SOURCE_CONTACTS);
		this.append((UICustomItem)vMenu.lastElement());
		
		vMenu.addElement(new UICustomItem("New contact", uiGooseManager.width, uiGooseManager.height, uiGooseManager));
		((UICustomItem)vMenu.lastElement()).setImage(SourceFiles.SOURCE_NEW);
		this.append((UICustomItem)vMenu.lastElement());
		
		vMenu.addElement(new UICustomItem("Search", uiGooseManager.width, uiGooseManager.height, uiGooseManager));
		((UICustomItem)vMenu.lastElement()).setImage(SourceFiles.SOURCE_SEARCH);
		this.append((UICustomItem)vMenu.lastElement());
		
		vMenu.addElement(new UICustomItem("Seek Friends", uiGooseManager.width, uiGooseManager.height, uiGooseManager));
		((UICustomItem)vMenu.lastElement()).setImage(SourceFiles.SOURCE_SEEK_MENU);
		this.append((UICustomItem)vMenu.lastElement());
		
		vMenu.addElement(new UICustomItem("Top contacts", uiGooseManager.width, uiGooseManager.height, uiGooseManager));
		((UICustomItem)vMenu.lastElement()).setImage(SourceFiles.SOURCE_TOP);
		this.append((UICustomItem)vMenu.lastElement());
		

		vMenu.addElement(new UICustomItem("Last contacts", uiGooseManager.width, uiGooseManager.height, uiGooseManager));
		((UICustomItem)vMenu.lastElement()).setImage(SourceFiles.SOURCE_RANK_WATCH);
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
