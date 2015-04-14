package gui.goose.menus;

import java.util.Date;

import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.AlertType;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.StringItem;

import goose.contactsManager.Contact;
import gui.goose.resources.SourceFiles;
import gui.goose.resources.UIGooseView;

public class UIGooseViewProfile extends Form implements UIGooseView{

	
	private Display display;
	
	
	public UIGooseViewProfile(UIGooseManager uiGooseManager,byte[] data) {
		super(null);
		
		display = Display.getDisplay(uiGooseManager);
		
		getProfile(data);
		/*
		if (! getProfile(contact, uiGooseRecordStore)) {
			uiGooseManager.viewIdentifier = SourceFiles.MAIN_MENU;
			uiGooseManager.currentObject = new UIGooseMainMenuView(SourceFiles.MAIN_MENU, uiGooseManager,
					display, uiGooseManager.width, uiGooseManager.height, uiGooseManager);
			Alert alert = new Alert("Info", "No personal profile found", null, AlertType.INFO);
			uiGooseManager.currentObject.show();
			uiGooseManager.display.setCurrent(alert, (UIGooseNewMessageMenuView)uiGooseManager.currentObject);
		}
		
		*/
		this.setTitle("Profile: ");
		
		this.addCommand(SourceFiles.backCommand);
		//this.addCommand(SourceFiles.logCommand);
		this.setCommandListener(uiGooseManager);
		
		
	
		
		
		
	}
	
	public void destroy() {
		// TODO Auto-generated method stub
		this.deleteAll();
	}

	public void show() {
		// TODO Auto-generated method stub
		display.setCurrent(this);
	}
	
	
	private boolean getProfile( byte[] data) {
		//byte[] data;
	
		//String surname= contact.getSurname() + SourceFiles.SPACER;
		//String phone = SourceFiles.STARTER + "phonenumber:" + contact.getPhoneNumber();
		//String searchuserID = surname+phone;
		//data = uiGooseRecordStore.readAllProfiles(searchuserID, SourceFiles.RECORD_STORE_ALL_PROFILE);
		/*
		if(data == null) {
			StringItem stringItemName = new StringItem("", "");
			stringItemName.setLabel("Name");
			stringItemName.setText(contact.getName());
			this.append(stringItemName);
			
			
			StringItem stringItemSurname = new StringItem("", "");
			stringItemSurname.setLabel("Surname");
			stringItemSurname.setText(contact.getSurname());
			this.append(stringItemSurname);
			
			
			StringItem stringItemPhone = new StringItem("", "");
			stringItemPhone.setLabel("Phone Number");
			stringItemPhone.setText(contact.getPhoneNumber());
			this.append(stringItemPhone);	
			return false;
		}
		*/
		String dataString = new String(data);
		
		int index;
		int indexEnd;
		StringItem stringItemName = new StringItem("", "");
		stringItemName.setLabel("Name");
		index = dataString.indexOf("//name:")+"//name:".length();
		indexEnd = dataString.indexOf(";//surname:");
		stringItemName.setText(dataString.substring(index, indexEnd));
		this.append(stringItemName);
		
		
		StringItem stringItemSurname = new StringItem("", "");
		stringItemSurname.setLabel("Surname");
		index = dataString.indexOf("//surname:")+"//surname:".length();
		indexEnd = dataString.indexOf(";//phonenumber:");
		stringItemSurname.setText(dataString.substring(index, indexEnd));
		this.append(stringItemSurname);
		
		
		StringItem stringItemPhone = new StringItem("", "");
		stringItemPhone.setLabel("Phone Number");
		index = dataString.indexOf("//phonenumber:")+"//phonenumber:".length();
		indexEnd = dataString.indexOf(";//date:");
		String number = dataString.substring(index, indexEnd);
		stringItemPhone.setText(number);
		this.append(stringItemPhone);
		
		
		StringItem stringItemBirthday = new StringItem("", "");
		stringItemBirthday.setLabel("Birthday");
		index = dataString.indexOf("//date:")+"//date:".length();
		indexEnd = dataString.indexOf(";//sex");
		if (index != indexEnd) {
			Date date = new Date(Long.parseLong(dataString.substring(index, indexEnd)));
			String aux;
			aux = date.toString().substring(0,  11);
			aux+= date.toString().substring(date.toString().length()-4);
			stringItemBirthday.setText(aux);
		}
		else {
			stringItemBirthday.setText(" ");
		}
		
		this.append(stringItemBirthday);
		
		
		StringItem stringItemSex = new StringItem("", "");
		stringItemSex.setLabel("Sex");
		index = dataString.indexOf("//sex:")+"//sex:".length();
		indexEnd = dataString.indexOf(";//city:");
		if (index != indexEnd) {
			stringItemSex.setText(dataString.substring(index, indexEnd));
		}
		else {
			stringItemSex.setText(" ");
		}
		this.append(stringItemSex);
		
		
		StringItem stringItemCity = new StringItem("", "");
		stringItemCity.setLabel("City");
		index = dataString.indexOf("//city:")+"//city:".length();
		indexEnd = dataString.indexOf(";//ocupation:");
		if (index != indexEnd) {
			stringItemCity.setText(dataString.substring(index, indexEnd));
		}
		else {
			stringItemCity.setText(" ");
		}
		this.append(stringItemCity);
		
		
		StringItem stringItemOcupation = new StringItem("", "");
		stringItemOcupation.setLabel("Ocupation");
		index = dataString.indexOf("//ocupation:")+"//ocupation:".length();
		indexEnd = dataString.indexOf(";//about:");
		if (index != indexEnd) {
			stringItemOcupation.setText(dataString.substring(index, indexEnd));
		}
		else {
			stringItemOcupation.setText(" ");
		}
		this.append(stringItemOcupation);
		
		
		StringItem stringItemAbout = new StringItem("", "");
		stringItemAbout.setLabel("About you");
		index = dataString.indexOf("//about:")+"//about:".length();
		indexEnd = dataString.indexOf(SourceFiles.SPACER_END);
		if (index != indexEnd) {
			stringItemAbout.setText(dataString.substring(index, indexEnd));
		}
		else {
			stringItemAbout.setText(" ");
		}	
		this.append(stringItemAbout);
		
		return true;
	
	}

}
