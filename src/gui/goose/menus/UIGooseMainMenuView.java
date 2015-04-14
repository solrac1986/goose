package gui.goose.menus;

import gui.goose.resources.SourceFiles;
import gui.goose.resources.UIGooseView;
import javax.microedition.lcdui.*;

import gui.goose.resources.*;

import java.util.Vector;

public class UIGooseMainMenuView extends Form implements UIGooseView{

	private Display display;
	
	
	
	public Vector vMenu;
	
	private static final String MAIN_MENU = "Main Menu";
	
	
	
	public UIGooseMainMenuView(int viewIdentifier, CommandListener cmd, 
			Display display, int width, int height, UIGooseManager uiGooseManager) {
		super(null);
		this.display = display;
		
		vMenu = new Vector();
		
		this.setTitle(MAIN_MENU);
		
		
		//this.addCommand(SourceFiles.backCommand);
		//this.addCommand(SourceFiles.logCommand);
		//this.addCommand(SourceFiles.pauseCommand);
		this.setCommandListener(cmd);
		
		
		vMenu.addElement(new UICustomItem("Contacts", width, height, uiGooseManager));
		((UICustomItem)vMenu.lastElement()).setImage(SourceFiles.SOURCE_LIST);
		this.append((UICustomItem)vMenu.lastElement());
		
		
		vMenu.addElement(new UICustomItem("Messages", width, height, uiGooseManager));
		((UICustomItem)vMenu.lastElement()).setImage(SourceFiles.SOURCE_MESSAGES);
		this.append((UICustomItem)vMenu.lastElement());
		
		
		vMenu.addElement(new UICustomItem("My wedge", width, height, uiGooseManager));
		((UICustomItem)vMenu.lastElement()).setImage(SourceFiles.SOURCE_SCAN);
		this.append((UICustomItem)vMenu.lastElement());
		
		
		vMenu.addElement(new UICustomItem("Configuration", width, height, uiGooseManager));
		((UICustomItem)vMenu.lastElement()).setImage(SourceFiles.SOURCE_CONFIGURATION);
		this.append((UICustomItem)vMenu.lastElement());
	
		
		
		vMenu.addElement(new UICustomItem("Switch Off", width, height, uiGooseManager));
		((UICustomItem)vMenu.lastElement()).setImage(SourceFiles.SOURCE_SWITCH_OFF);
		this.append((UICustomItem)vMenu.lastElement());
	
		
		
	
		
		
		
		
	}
	
	public void destroy() {
		// TODO Auto-generated method stub
		vMenu = null;
		this.deleteAll();
	}

	public void show() {
		// TODO Auto-generated method stub
//		try {
//			Thread.sleep(500);
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		//UICustomItem.repaintScreen(vMenu);
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		display.setCurrent(this);
	}

}
