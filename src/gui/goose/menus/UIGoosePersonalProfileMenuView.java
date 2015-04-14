package gui.goose.menus;

import java.util.Date;
import java.util.TimeZone;
import javax.microedition.lcdui.*;

import microlog.Logger;

import java.lang.Object;

import gui.goose.exceptions.GooseAlert;
import gui.goose.resources.*;

public class UIGoosePersonalProfileMenuView extends Form implements UIGooseView{

	private Display display;
	
	private TextField textFieldName;
	private TextField textFieldSurname;
	private TextField textFieldPhoneNumber;
	private DateField dateFieldBirthday;
	private TextField textFieldSex;
	private TextField textFieldCity;
	private TextField textFieldOcupation;
	private TextField textFieldAboutYou;
	
	private String currentuserID ="";
	
	private static Logger log = Logger.getLogger();
	
	public UIGoosePersonalProfileMenuView(UIGooseManager uiGooseManager) {
		super(null);
		
		display = Display.getDisplay(uiGooseManager);
		
		this.setTitle("Personal profile");
		this.setCommandListener(uiGooseManager);
		this.addCommand(SourceFiles.backCommand);
		this.addCommand(SourceFiles.okCommand);
		//DEbuggin
		//this.addCommand(SourceFiles.logCommand);
		
		//Date date = new Date();
		textFieldName = new TextField("Name:", "", 50, TextField.ANY);
		textFieldSurname = new TextField("Surname:", "", 50, TextField.ANY);
		textFieldPhoneNumber = new TextField("Phone: ", "", 50, TextField.NUMERIC);
		dateFieldBirthday = new DateField("Birthday: ", DateField.DATE, TimeZone.getTimeZone("GMT"));
		textFieldSex = new TextField("Sex (M/F):", "", 1, TextField.ANY);
		textFieldCity = new TextField("City:", "", 50, TextField.ANY);
		textFieldOcupation = new TextField("Ocupation:", "", 50, TextField.ANY);
		textFieldAboutYou = new TextField("About you: ", "", 500, TextField.ANY);
		
		//TODO: Set the name / surname / phonenumber with setUserData
		//TODO: send last contact user phone

		this.currentuserID = uiGooseManager.userID;
		byte[] data = uiGooseManager.uiGooseRecordStore.readAllProfiles(uiGooseManager.userID , SourceFiles.RECORD_STORE_ALL_PROFILE);
		//byte [] data = uiGooseManager.uiGooseRecordStore.readRecordStore(); 
		if ( data == null  || data.length == 0) {
			int index;
			int indexEnd;
			System.out.println("userId: "+uiGooseManager.userID);
			index = uiGooseManager.userID.indexOf("//name:")+"//name:".length();
			System.out.println("index: "+index);
			indexEnd = uiGooseManager.userID.indexOf(";//surname:");
			System.out.println("indexEnd: "+indexEnd);
			textFieldName.setString(uiGooseManager.userID.substring(index, indexEnd));
			System.out.println("Peta despues textField");
			index = uiGooseManager.userID.indexOf("//surname:")+"//surname:".length();
			indexEnd = uiGooseManager.userID.indexOf(";//phonenumber:");
			
			textFieldSurname.setString(uiGooseManager.userID.substring(index, indexEnd));
		
			index = uiGooseManager.userID.indexOf("//phonenumber:")+"//phonenumber:".length();
			//dataString.substring(index);
			//indexEnd = uiGooseManager.userID.indexOf(";//date:");
			String number = uiGooseManager.userID.substring(index);
			if(number.startsWith("+")){
				String newNumber ="00"+number.substring(1);
		        number=newNumber;
		    }
			
			textFieldPhoneNumber.setString(number);
		
			this.append(textFieldName);
			this.append(textFieldSurname);
			this.append(textFieldPhoneNumber);
			this.append(dateFieldBirthday);
			this.append(textFieldSex);
			this.append(textFieldCity);
			this.append(textFieldOcupation);
			this.append(textFieldAboutYou);
			return;
		}
		
		String dataString = new String(data);
		
		int index;
		int indexEnd;
		index = dataString.indexOf("//name:")+"//name:".length();
		//dataString.substring(index);
		indexEnd = dataString.indexOf(";//surname:");
		System.out.println(dataString.substring(index, indexEnd));
		textFieldName.setString(dataString.substring(index, indexEnd));
		
		
		index = dataString.indexOf("//surname:")+"//surname:".length();
		//dataString.substring(index);
		indexEnd = dataString.indexOf(";//phonenumber:");
		
		textFieldSurname.setString(dataString.substring(index, indexEnd));
		
		
		index = dataString.indexOf("//phonenumber:")+"//phonenumber:".length();
		//dataString.substring(index);
		indexEnd = dataString.indexOf(";//date:");
		String number = dataString.substring(index, indexEnd);
		if(number.startsWith("+")){
			String newNumber ="00"+number.substring(1);
	        number=newNumber;
	    }
		
		textFieldPhoneNumber.setString(number);
	
		
		index = dataString.indexOf("//date:")+"//date:".length();
		indexEnd = dataString.indexOf(";//sex:");
		String dateString = dataString.substring(index, indexEnd);
		if (dateString.length() != 0 && dateString != null) {
			
			long longDate = Long.parseLong(dateString);
			Date date = new Date (longDate);
			dateFieldBirthday.setDate(date);
		}
		
		
		
		index = dataString.indexOf("//sex:")+"//sex:".length();
		indexEnd = dataString.indexOf(";//city:");
		textFieldSex.setString(dataString.substring(index, indexEnd));
		
		
		
		index = dataString.indexOf("//city:")+"//city:".length();
		indexEnd = dataString.indexOf(";//ocupation:");
		textFieldCity.setString(dataString.substring(index, indexEnd));
		
	
		index = dataString.indexOf("//ocupation:")+"//ocupation:".length();
		indexEnd = dataString.indexOf(";//about:");
		textFieldOcupation.setString(dataString.substring(index, indexEnd));
	
		
	
		index = dataString.indexOf("//about:")+"//about:".length();
		indexEnd = dataString.indexOf(SourceFiles.SPACER_END);
		textFieldAboutYou.setString(dataString.substring(index, indexEnd));

		
		this.append(textFieldName);
		this.append(textFieldSurname);
		this.append(textFieldPhoneNumber);
		this.append(dateFieldBirthday);
		this.append(textFieldSex);
		this.append(textFieldCity);
		this.append(textFieldOcupation);
		this.append(textFieldAboutYou);
		
		
	}

	public void destroy() {
		// TODO Auto-generated method stub
		this.deleteAll();
	}

	public void show() {
		// TODO Auto-generated method stub
		display.setCurrent(this);
	}
	
	
	public boolean checkPhoneNumber() {
		String number = textFieldPhoneNumber.getString();
		 if(number.startsWith("00")){
			 
	         String newNumber ="+"+number.substring(2);
	         number=newNumber;
	         if (isPhonenumber(number)) { 
		         newNumber = number.toLowerCase();
		         return true;
		     }
	         else {
	        	 return false;
	         }
		 }
		 else {
			 return false;
		 }
		 
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
	
	//private Date convertStringToDate(String time) {
		   /* Date date = new Date(); 
		    
		    //DateFormat dateformat = new SimpleDateFormat("dd/MM/yyyy");
		    try
		    {
		    //Date date = dateformat.parse(time);
		    System.out.println("The date and Time: " + date);
		    }catch (Exception e)
		    {
		    System.out.println(e.getMessage());
		    }*/
	//}

	public boolean setProfile(UIGooseRecordStore uiGooseRecordStore, UIGooseManager uiGooseManager){
		String number = textFieldPhoneNumber.getString(); 
		if(number.startsWith("00")){
			String newNumber ="+"+number.substring(2);
	        number=newNumber;
	    }
		if (textFieldSex.size() != 0) {
			if ( textFieldSex.getString().equalsIgnoreCase("m")  
					|| textFieldSex.getString().equalsIgnoreCase("f") ) {
				//log.debug("Sex: "+textFieldSex.getString());
			}
			else{
				uiGooseManager.display.setCurrent(GooseAlert.createAlert("Only [F/M] in sex field"), 
						(UIGoosePersonalProfileMenuView)uiGooseManager.currentObject);
				return false;				
			}
		}
		
		byte[] data;
		String dataString = "";
		Date date = new Date();
		if (dateFieldBirthday.getDate() != null) {
			date = dateFieldBirthday.getDate();
		}
		System.out.println("name:" +textFieldName.getString());
		dataString += SourceFiles.STARTER +"name:"+ textFieldName.getString()+SourceFiles.SPACER;
		dataString += SourceFiles.STARTER +"surname:"+ textFieldSurname.getString()+SourceFiles.SPACER;
		dataString += SourceFiles.STARTER +"phonenumber:"+ number+SourceFiles.SPACER;
		if (dateFieldBirthday.getDate() != null) {
			dataString += SourceFiles.STARTER +"date:"+ date.getTime()+SourceFiles.SPACER;
		}
		else {
			dataString += SourceFiles.STARTER +"date:"+ ""+SourceFiles.SPACER;
		}
		dataString += SourceFiles.STARTER +"sex:"+ textFieldSex.getString()+SourceFiles.SPACER;
		dataString += SourceFiles.STARTER +"city:"+ textFieldCity.getString()+SourceFiles.SPACER;
		dataString += SourceFiles.STARTER +"ocupation:"+ textFieldOcupation.getString()+SourceFiles.SPACER;
		dataString += SourceFiles.STARTER +"about:"+ textFieldAboutYou.getString()+SourceFiles.SPACER_END;
		//data = new byte[dataString.length()];
		data = dataString.getBytes();
		
		if (data.length != 0) {
			int index =0;
			int indexEnd = dataString.indexOf(";//date:");
			uiGooseManager.userID = dataString.substring(index, indexEnd);
			
			uiGooseRecordStore.writeAllProfiles(data, SourceFiles.RECORD_STORE_GOOSE_WALL, 
					this.currentuserID, SourceFiles.PERSONAL_PROFILE);
					
			//uiGooseManager.uiGooseRecordStore.writeRecordStore(data);
			return true;
		}
		return false;
		
	}
	
}
