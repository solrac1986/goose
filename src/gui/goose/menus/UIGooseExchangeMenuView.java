package gui.goose.menus;

import goose.contactsManager.Contact;
import goose.exceptions.GooseNetworkException;


import gui.goose.exceptions.GooseAlert;
import gui.goose.resources.SourceFiles;
import gui.goose.resources.UIGooseView;

import javax.bluetooth.ServiceRecord;
import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.AlertType;
import javax.microedition.lcdui.Choice;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.List;
import microlog.Logger;


public class  UIGooseExchangeMenuView extends Form implements UIGooseView{
	
	
	private Logger log;
	private Display display;
	
	public UIGooseThreadExchange gooseThreadExchange;
	private List list;
	
	public UIGooseExchangeMenuView(UIGooseManager uiGooseManager){
		super(null);
		
		log = Logger.getLogger();
		display = Display.getDisplay(uiGooseManager);
		
		list = new List("Nearby Devices", Choice.IMPLICIT);
		list.append("Searching.....", null);
		list.addCommand(SourceFiles.backCommand);
		//list.addCommand(SourceFiles.logCommand);
		list.setCommandListener(uiGooseManager);
		show();
		
		//vectorServiceRecord = new Vector();
		//searchNearbyDevices(uiGooseManager);
		gooseThreadExchange = new UIGooseThreadExchange(uiGooseManager);
		Thread threadExchange = new Thread(gooseThreadExchange);
		threadExchange.start();
		
	}
	
	
	
	
	public void destroy() {
		// TODO Auto-generated method stub
		list.deleteAll();	
		gooseThreadExchange.knownDevices = null;
		gooseThreadExchange.vectorServiceRecord = null;
	}



	public void show() {
		// TODO Auto-generated method stub
		display.setCurrent(list);	
		System.out.println(list.getTitle());
		
	}
	
	
	
	
	
	
	public Contact exchangeDetails(UIGooseManager uiGooseManager) {
		
		
		int indexUnknownDevices = uiGooseManager.index - gooseThreadExchange.numberKnownDevices;
		if (uiGooseManager.index < gooseThreadExchange.numberKnownDevices) {
			// We have selected a known device we will go to send a message
			////log.debug("UIGooseManager. enter exchange details known device");
			uiGooseManager.serviceRecordProfileRequest = (ServiceRecord)gooseThreadExchange.vectorServiceRecord.elementAt(uiGooseManager.index);
			Contact contact = (Contact)gooseThreadExchange.knownDevices.elementAt(uiGooseManager.index);
			destroy();
			
			this.gooseThreadExchange = null;
			return contact;
		}
		else {
			
			////log.debug("Starting exchange datails enter in the function indexUknown: " +indexUnknownDevices);
			try {
				//String name = ((ServiceRecord)vectorServiceRecord.elementAt(indexUnknownDevices)).getHostDevice().getFriendlyName(false);
				////log.debug("Starting exchange datails name:" + name);
				uiGooseManager.gooseManager.exchangeContactDetails((ServiceRecord)gooseThreadExchange.vectorServiceRecord.elementAt(indexUnknownDevices));
			} catch (GooseNetworkException e) {
				//log.debug("Exception when sending exchange message error: "+e.getMessage());
				display.setCurrent(GooseAlert.createAlert("Search error exchange " + e.getMessage()),
						list);
			}
			catch(Exception e){
				//log.debug("Exception exchanging details: "+e.getMessage());
			}
			destroy();	
			uiGooseManager.viewIdentifier = SourceFiles.MAIN_MENU; 
			 uiGooseManager.currentObject = new UIGooseMainMenuView(SourceFiles.MAIN_MENU, uiGooseManager,
					 uiGooseManager.display, uiGooseManager.width, uiGooseManager.height, uiGooseManager);
			
			 this.gooseThreadExchange = null;
			 return null;
		}
		
		
	}



	
	
	
}
