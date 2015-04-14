package gui.goose.menus;


import java.util.Vector;

import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Form;

import gui.goose.resources.SourceFiles;
import gui.goose.resources.UICustomItem;
import gui.goose.resources.UIGooseView;

public class UIGooseSearchMainMenuView extends Form implements UIGooseView{

	
	private Display display;
	public Vector vMenu = null;
	
	public UIGooseSearchMainMenuView(UIGooseManager uiGooseManager) {
		super(null);
		
		vMenu = new Vector();
		
		this.display = uiGooseManager.display;
		
		this.setTitle("Search Menu");
		
		vMenu.addElement(new UICustomItem("Start", uiGooseManager.width, uiGooseManager.height, uiGooseManager));
		((UICustomItem)vMenu.lastElement()).setImage(SourceFiles.SOURCE_START);
		this.append((UICustomItem)vMenu.lastElement());
		
		this.addCommand(SourceFiles.backCommand);
		//this.addCommand(SourceFiles.logCommand);
		//this.addCommand(SourceFiles.okCommand);
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
