package gui.goose.menus;

import gui.goose.resources.UIGooseView;

import java.util.Vector;

import gui.goose.resources.SourceFiles;
import gui.goose.resources.UICustomItem;


import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Form;

public class UIGooseSeekMainMenuView extends Form implements UIGooseView{

	private Display display;
	public Vector vMenu = null;
	
	public UIGooseSeekMainMenuView (UIGooseManager uiGooseManager) {
		super(null);
		
		vMenu = new Vector();
		
		this.display = uiGooseManager.display;
		
		this.setTitle("Seek friends Menu");
		
		vMenu.addElement(new UICustomItem("Send request", uiGooseManager.width, uiGooseManager.height, uiGooseManager));
		((UICustomItem)vMenu.lastElement()).setImage(SourceFiles.SOURCE_SEEK_MENU);
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
