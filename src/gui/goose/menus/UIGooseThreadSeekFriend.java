package gui.goose.menus;



public class UIGooseThreadSeekFriend implements Runnable{

	
	public int numberKnownDevices  = 0;
	
	private String Name;
	private String surName;
	private String phoneNumber;
	
	private UIGooseManager  uiGooseManager;
	
	public UIGooseThreadSeekFriend(UIGooseManager uiGooseManager, String name, String surname, 
			String phone) {
		
		Name = name;
		surName = surname;
		phoneNumber = phone;
		//display = Display.getDisplay(uiGooseManager);
		this.uiGooseManager = uiGooseManager;
	}
	
	public void destroy() {
		this.uiGooseManager = null;
	}
	
	public void run() {
		// TODO Auto-generated method stub
		sendSeekFriendRequest();
	}
	
	public void sendSeekFriendRequest() {
		
		String name= "none";
		String surname = "none";
		String number = "none";
		
		
		
		if (Name.length() != 0) {
			name = Name.toLowerCase();
		}
		if (surName.length() != 0){
			surname = surName.toLowerCase();
		}
		if (phoneNumber.length() != 0) {
			number = phoneNumber.toLowerCase();
			if(number.startsWith("00")){
	            String newNumber ="+"+number.substring(2);
	            number=newNumber;
	        }
		}
		if(Name.equalsIgnoreCase("none")&&surName.equalsIgnoreCase("none")&&phoneNumber.equalsIgnoreCase("none")){
			return;
		}
		if (! isPhonenumber(number)) {
			number = "none";	
		}
		
		
		uiGooseManager.gooseManager.seekFriend(name, surname, number);
		
		uiGooseManager = null;
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
