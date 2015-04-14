package gui.goose.menus;


import gui.goose.resources.SourceFiles;
import gui.goose.resources.UIGooseView;


import javax.microedition.lcdui.*;


import javax.microedition.lcdui.Form;

public class UIGooseSeekFriendMenuView extends Form implements UIGooseView{

	private Display display;
	
	
	public TextField textFieldName;
	public TextField textFieldSurname;
	public TextField textFieldPhone;
	
	public UIGooseSeekFriendMenuView(UIGooseManager uiGooseManager) {
		super(null);
		
		display = Display.getDisplay(uiGooseManager);
		this.setTitle("Search friend: ");
		this.setCommandListener(uiGooseManager);
		this.addCommand(SourceFiles.backCommand);
		this.addCommand(SourceFiles.okCommand);
		//this.addCommand(SourceFiles.logCommand);
		
		textFieldName  = new TextField("Name", "", 50, TextField.ANY);
		textFieldSurname  = new TextField("Surname", "", 50, TextField.ANY);
		textFieldPhone  = new TextField("Phone", "", 50, TextField.NUMERIC);
		
		this.append(textFieldName);
		this.append(textFieldSurname);
		this.append(textFieldPhone);
		
	}
	
	
	public void destroy() {
		// TODO Auto-generated method stub
		this.deleteAll();
	}

	public void show() {
		// TODO Auto-generated method stub
		display.setCurrent(this);
	}
	
	public String[] sendSeekFriendRequest(UIGooseManager uiGooseManager) {
		
		String[] stringValue = new String[3];
		stringValue[0] = "none";
		stringValue[1] = "none";
		stringValue[2] = "none";
		
		if (textFieldName.size() != 0) {
			stringValue[0] = textFieldName.getString().toLowerCase();
		}
		if (textFieldSurname.size() != 0){
			stringValue[1] = textFieldSurname.getString().toLowerCase();
		}
		if (textFieldPhone.size() != 0) {
			stringValue[2] = textFieldPhone.getString().toLowerCase();
			if(stringValue[2].startsWith("00")){
	            String newNumber ="+"+stringValue[2].substring(2);
	            stringValue[2]=newNumber;
	        }
		}
		if (! isPhonenumber(stringValue[2])) {
			stringValue[2] = "none";	
		}
		
		return stringValue;
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
