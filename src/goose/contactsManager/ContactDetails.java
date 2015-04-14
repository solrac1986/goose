package goose.contactsManager;

/**
*
* @author Narseo
*/
public class ContactDetails {
	public String name = "";
	public String surname ="";
	public String phoneNumber = "";
	public String btAddress ="";
	
	public ContactDetails(String name, String surname, String phoneNumber, String btAddress){
		this.name=name;
		this.surname=surname;
		this.phoneNumber=phoneNumber;
		this.btAddress=btAddress;
	}

}
