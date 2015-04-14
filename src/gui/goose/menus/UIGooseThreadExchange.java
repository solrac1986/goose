package gui.goose.menus;

import goose.contactsManager.Contact;
import goose.exceptions.GooseSearchException;
import gui.goose.exceptions.GooseAlert;
import gui.goose.resources.SourceFiles;

import java.io.IOException;
import java.util.Vector;

import javax.bluetooth.ServiceRecord;
import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.AlertType;
import javax.microedition.lcdui.Choice;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.List;

import microlog.Logger;

public class UIGooseThreadExchange implements Runnable{

	private UIGooseManager uiGooseManager;
	private Display display;
	private Logger log = Logger.getLogger();
	
	public int numberKnownDevices;


	
	public Vector knownDevices = new Vector();
	public Vector vectorServiceRecord = new Vector();
	public Vector unknownDevices= new Vector();
	
	public UIGooseThreadExchange (UIGooseManager uiGooseManager) {
		
		display = Display.getDisplay(uiGooseManager);
		this.uiGooseManager  =uiGooseManager;
	}
	
	
	public void run() {
		// TODO Auto-generated method stub
		searchNearbyDevices();
	}

	
	private void searchNearbyDevices() {
		
		
		
		List list = new List("Nearby Devices", Choice.IMPLICIT);
		unknownDevices  = new Vector();
		//Debuggin
		try {
			uiGooseManager.contacts = uiGooseManager.gooseManager.getContacts();
			////log.debug("Number of contacts on the Database: "+uiGooseManager.contacts.size());
		} catch (GooseSearchException e) {
			// TODO Auto-generated catch block
			//log.debug("UIGooseManager. Exception in searchNearbyDevices"+ e.getMessage());
			uiGooseManager.contacts = new Vector();
		}
		
		
		vectorServiceRecord = uiGooseManager.gooseManager.getNearbyDevices();
		////log.debug("searchNearby devices found: " +vectorServiceRecord.size());
		
		if(vectorServiceRecord==null){
			//log.debug("vectorServiceRecord vector = null");
		}
		
		Image imageKnown = null;
		Image imageUnknown = null;
		try {
			imageKnown = Image.createImage(SourceFiles.SOURCE_BUDDY);
			imageUnknown = Image.createImage(SourceFiles.SOURCE_BUDDY_UNKNOWN);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println(e.getMessage());
			//log.debug("Error in images in searchNearby devices error: "+e.getMessage());
		}
		
		try {

			Vector nearbyBTAddress = new Vector();
			nearbyBTAddress = vectorServiceRecord;
			// Only for debuggin then delete this "for()";
			/*for (int i=0; i<vectorServiceRecord.size(); i++){
				ServiceRecord tmp = (ServiceRecord)vectorServiceRecord.elementAt(i);
				////log.debug("MAC Found: "+tmp.getHostDevice().getBluetoothAddress());
				//nearbyBTAddress.addElement(tmp);
			}
			*/
			////log.debug("Ready to look for known devices, elements discovered by bt: ");
			for (int j=0; j<nearbyBTAddress.size(); j++){
				ServiceRecord tmpSr = (ServiceRecord)nearbyBTAddress.elementAt(j);
				//log.debug("Device found: "+j+": "+tmpSr.getHostDevice().getBluetoothAddress());
			}
			try{
				knownDevices = uiGooseManager.gooseManager.searchNearbyContacts(nearbyBTAddress);
				//TODO: It fails here when it finds more than a device. Test it
			}
			catch(Exception e){
				//log.debug("Error retrieving known devices: "+e.getMessage());
				knownDevices = new Vector();
			}
			////log.debug("Known devices: "+knownDevices.size());
			if(knownDevices==null){
				//log.debug("KnownDevices vector = null");
			}
			for(int i=0; i<knownDevices.size(); i++){
				Contact tmp = (Contact)knownDevices.elementAt(i);
				String contactName = tmp.getName()+" "+tmp.getSurname();
				////log.debug("Known element to add: "+contactName);
				list.append(contactName, imageKnown);
			}
			////log.debug("Starting to look for unknown devices");
			boolean deviceFound = false;
			for (int i=0; i<vectorServiceRecord.size(); i++){
				ServiceRecord tmp = (ServiceRecord)vectorServiceRecord.elementAt(i);
				////log.debug("Inside for element serviceRecord URL : "+ (tmp.getConnectionURL(0, false)));
				if (knownDevices.size() == 0) {
					unknownDevices.addElement(tmp);	
					//String contactName = extractName(tmp.getConnectionURL(0, false));
					////log.debug("UnKnown element to add: "+ tmp.getHostDevice().getFriendlyName(false));
					list.append(tmp.getHostDevice().getFriendlyName(false), imageUnknown);
					continue;
				}
				for (int j=0; j<knownDevices.size(); j++){
					Contact knownContact = (Contact)knownDevices.elementAt(j);
					if (knownContact.getBluetoothAddress().equalsIgnoreCase(tmp.getHostDevice().getBluetoothAddress())){
						deviceFound = true;
						break;
					}
				}
				if (deviceFound == false) {
					unknownDevices.addElement(tmp);	
					//String contactName = extractName(tmp.getConnectionURL(0, false));
					////log.debug("UnKnown element to add: "+ tmp.getHostDevice().getFriendlyName(false));
					list.append(tmp.getHostDevice().getFriendlyName(false), imageUnknown);
				}
				deviceFound = false;
			}
			list.addCommand(SourceFiles.backCommand);
			//list.addCommand(SourceFiles.logCommand);
			list.setCommandListener(uiGooseManager);
			display.setCurrent(list);
			if(list.size() == 0) {
				Alert alert = new Alert("Info", "No near devices found", null, AlertType.WARNING);
				uiGooseManager.viewIdentifier = SourceFiles.MAIN_MENU;
				uiGooseManager.currentObject = new UIGooseMainMenuView(SourceFiles.MAIN_MENU, uiGooseManager,
						display, uiGooseManager.width, uiGooseManager.height, uiGooseManager);
				uiGooseManager.currentObject.show();
				display.setCurrent(alert, (UIGooseMainMenuView)uiGooseManager.currentObject);
			}
			
			//list.append("---Search finished---", null);
			////log.debug("Searching for known/unknown devices process is finished");
			
			nearbyBTAddress = null;
			
			numberKnownDevices = knownDevices.size();
			//Known -> Vector Contact
			//unknown-> Vector ServiceRecord
			//List -> String [knwon.size + unknown.size]
			
			this.uiGooseManager = null;
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			display.setCurrent(GooseAlert.createAlert("Search error in nearby devices in list" + e.getMessage()), list);
		}	
	}
}
