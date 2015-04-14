package gui.goose.menus;

import gui.goose.resources.UICustomItem;
import gui.goose.resources.UIGooseView;
import goose.contactsManager.Contact;
import goose.contactsManager.ContactsManager;
import gui.goose.resources.*;
import javax.microedition.lcdui.*;

public class UIGooseFieldTextMenuView extends Form implements UIGooseView{

	
	private Display display;
	
	public static final String EDIT_MENU ="Edit Menu";
	public static final String NEW_MENU ="New contact";
	public static final String LOGIN_MENU ="Login";
	public static final String SEARCH_MENU ="Search";
	
	private TextField textFieldName;
	private TextField textFieldSurname;
	private TextField textFieldPhone;
	private int viewIdentifier;
	
	private int[] arrayPattern;
	
	 public UIGooseFieldTextMenuView(int viewIdentifier, UIGooseManager uiGooseManager, 
			 Display display, Contact contact) {
		super("");
		
		this.viewIdentifier = viewIdentifier;
		this.display  = display;
		
		textFieldName  = new TextField("Name", "", 50, TextField.ANY);
		textFieldSurname  = new TextField("Surname", "", 50, TextField.ANY);
		textFieldPhone  = new TextField("Phone", "", 50, TextField.NUMERIC);
	
		
		switch (viewIdentifier) {
		
			
		case SourceFiles.CONTACT_SEARCH_MENU:
				this.setTitle(SEARCH_MENU);
				UICustomImage uiCustomSearch = new UICustomImage("", SourceFiles.SOURCE_SEARCH);
				this.append(uiCustomSearch.getImageItem());
			
				break;	
			
		case SourceFiles.CONTACT_NEW_MENU:
				this.setTitle(NEW_MENU);
				UICustomImage uiCustomNew = new UICustomImage("", SourceFiles.SOURCE_BUDDY_BIG);
				this.append(uiCustomNew.getImageItem());
				break;
		case SourceFiles.CONTACT_EDIT_MENU:
				this.setTitle(EDIT_MENU);
				
				//System.out.println("name: "+contact.getName());
				UICustomItem uiCustomDelete = new UICustomItem("Delete", uiGooseManager.width, uiGooseManager.height, uiGooseManager);
				uiCustomDelete.setImage(SourceFiles.SOURCE_DELETE);
				this.append(uiCustomDelete);
				textFieldName.setString(contact.getName());
				textFieldSurname.setString(contact.getSurname());
				String phoneNumber = contact.getPhoneNumber();
				if(phoneNumber.startsWith("+")){
		            String newNumber ="00" + phoneNumber.substring(1);
		            phoneNumber = newNumber;
				}
				textFieldPhone.setString(phoneNumber);
				
				break;
		case SourceFiles.LOGIN_MENU:
				this.setTitle(LOGIN_MENU);
				UICustomImage uiCustomLogin = new UICustomImage("", SourceFiles.SOURCE_BUDDY_BIG);
				this.append(uiCustomLogin.getImageItem());
			break;
			
		}
		this.append(textFieldName);
		this.append(textFieldSurname);
		this.append(textFieldPhone);
	
		
		if (viewIdentifier != SourceFiles.LOGIN_MENU) {
			this.addCommand(SourceFiles.backCommand);
			//this.addCommand(SourceFiles.logCommand);
		}
		
		this.addCommand(SourceFiles.okCommand);
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

	public TextFieldValues getTextField () {
		TextFieldValues ret = new TextFieldValues();
		
		ret.name = textFieldName.getString().toLowerCase();
		ret.surname = textFieldSurname.getString().toLowerCase();
		ret.phoneNumber="";
		
		ret.isSearch=false;
		String number = "";
        number = textFieldPhone.getString();
      
        
        //Convert number to international
        if(number.startsWith("00")){
            String newNumber ="+"+number.substring(2);
            number=newNumber;
        }
        //Check if number matches international phone number pattern
		if (textFieldName.size() != 0 && textFieldSurname.size() != 0 
				&& textFieldPhone.size() != 0) {
	        if (isPhonenumber(number)) {
	            ret.phoneNumber = number.toLowerCase();
	        }
	        else{
	        	ret.phoneNumber = "";
	        }
		}
		else if (this.viewIdentifier == SourceFiles.CONTACT_SEARCH_MENU){
			if (textFieldPhone.size() != 0) {
				if (isPhonenumber(number)) {
		        	ret.phoneNumber=number;
		        	ret.isSearch=true;
		        }
			}
		}
		
		return ret;
	}

	
	public String[] getValuePattern(String name, String surname, String phone) {
		int size = 0;
		String[] stringArray = new String[3];
		int[] arrayAux = new int[3]; 
		
		if (name.length() != 0) {
			stringArray[size] = name.toLowerCase();
			arrayAux[size] = ContactsManager.NAME;
			size ++; 
			
		}
		if (surname.length() != 0) {
			stringArray[size] = surname.toLowerCase();
			
			arrayAux[size] = ContactsManager.SURNAME; 
			size ++;
			
		}
		if (phone.length() != 0) {
			stringArray[size] = phone.toLowerCase();
			
			arrayAux[size] = ContactsManager.PHONENUMBER;
			size++;			
		}
		
		
		arrayPattern = new int[size];
		String[] arrayString = new String[size];
		for (int i = 0; i< size; i++) {
			arrayString[i] = stringArray[i];
			arrayPattern[i] = arrayAux[i];
		}
		return arrayString;
	}
	
	public int[] getSearchPattern() {
		return arrayPattern;
	}
	
	private boolean isPhonenumber(String phoneNumber){
	       if (phoneNumber.startsWith("+")){
	               if(phoneNumber.length()>15){
	                       return false;
	               }
	               for (int i=1; i<phoneNumber.length(); i++){
	            	   if (phoneNumber.charAt(i) < '0' || phoneNumber.charAt(i)>'9'){
	            		   return false;
	            	   }
	               }
	       }
	       else{
	    	   return false;
	       }
	       return true;
	   }
}
