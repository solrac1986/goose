package goose.unitaryTests;

import goose.mainManager.*;
import goose.networkManager.NetworkManager;
import goose.contactsManager.Contact;
import goose.contactsManager.ContactsManager;
import goose.exceptions.GooseException;
import goose.forwardingManager.*;
import microlog.Logger;
import java.util.*;
import javax.bluetooth.*;



public class GooseUnitaryTest extends Thread{
	private Logger log = Logger.getLogger();
	private GooseManager g = null;
	private boolean alive = true;
	private String name = "Narseo"+GooseTools.getTime()%100;
	private String surname = "Vallina"+GooseTools.getTime()%100;
	private String phoneNumber = "+34985663335"+GooseTools.getTime()%100;
	
	public GooseUnitaryTest(){
        try{
        	g = new GooseManager();
        }
        catch(Exception e){
        	
        	log.debug("Error creating GooseManager: "+e.getMessage());
        	e.printStackTrace();
        }		
	}
	
	public void startTest(){
        try{
        	g.setUserDetails(name, surname, phoneNumber);
        }
        catch(Exception e){
        	log.debug("Error setting contact details");
        	e.printStackTrace();
        }
        try{
        	g.startManager();
        }
        catch(Exception e){
        	log.debug("Error starting managers: "+e.getMessage());
        	e.printStackTrace();
        }
        //Insert a contact
		//Adding contacts
        /*
		String [] dataInput0 = {"narseo", "vallina", "+34985663335", "0123456789AB"};
		try{
			g.createContact(dataInput0);
			
		}
		catch(Exception e){
			log.debug(e.getMessage());
		}

		String [] dataInput1 = {"pelayo", "vallina", "+34985691796", "0123456789AC"};
		try{
			g.createContact(dataInput1);
			
		}
		catch(Exception e2){
			log.debug("Error storing object: "+e2.getMessage());
		}		
		try{
			g.createContact(dataInput1);	
		}
		catch(Exception e2){
			log.debug("Error storing object: "+e2.getMessage());
		}		
		
		String [] dataInput2 = {"alberto", "garcia", "+34982858239", "0123452789AD"};
		try{
			g.createContact(dataInput2);
			
		}
		catch(Exception e2){
			log.debug("Error storing object: "+e2.getMessage());
		}
		
		
		String [] dataInput3 = {"narseo", "vallina", "+34985858239", "0123456789AD"};
		try{
			g.createContact(dataInput3);
			
		}
		catch(Exception e2){
			log.debug("Error storing object: "+e2.getMessage());
		}
		
		String [] dataInput4 = {"migue", "gitanillo", "+34985258239", "0133456789AD"};
		try{
			g.createContact(dataInput4);
			
		}
		catch(Exception e2){
			log.debug("Error storing object: "+e2.getMessage());
		}
		
		try{
		Vector all = g.getContacts();
		
		for (int i=0; i<all.size(); i++){
			Contact tmp = (Contact)all.elementAt(i);
			log.debug(i+"|"+tmp.getId()+" "+tmp.getName());
		}
		}
		catch(Exception e){
			log.debug("Error retrieving the contacts");
		}*/
		
		/*
		Vector top = g.getTopContacts(2);
		for (int i=0; i<top.size(); i++){
			Contact tmp = (Contact)top.elementAt(i);
			log.debug(i+" "+tmp.getName()+" Rank: "+tmp.getScore());
		}
		
		
		Vector c = new Vector();
		/*
		try{
			String [] searchValues = {"narseo", "+34985663335"};
			int [] searchPattern = {ContactsManager.NAME, ContactsManager.PHONENUMBER};
			c = g.searchContact(searchValues, searchPattern);
		}
		catch(Exception e){
			log.debug(e.getMessage());
		}*/
		/*
		
		try{
			c= g.searchContact("+34985663335", ContactsManager.PHONENUMBER);
		}
		catch(Exception e){
			log.debug(e.getMessage());
		}
		
		log.debug("Performing Search. Contacts found: "+c.size());
		log.debug("Delete found contacts");
		Contact tmpCt = null;
		for (int i=0; i<c.size(); i++){
			tmpCt = (Contact)c.elementAt(i);
			log.debug("Object index to delete: "+tmpCt.getId());
			g.deleteContact(tmpCt);
		}
		log.debug("Number of contacts stored: "+g.getNumberOfContacts());
		

		
		Vector rec = new Vector();
		GooseMessage m = null;
		try{
			m = g.createNewMessage(rec, GooseMessage.BCAST, GooseMessage.LOW, GooseMessage.TEXT, "Hola Caracola over Bluetooth!!!");
		}
		catch(Exception e){
			log.debug("Error creating message: "+e.getMessage());
		}*/
		/*
		try{
			g.send(rec, m);
		}
		catch(Exception e){
			log.debug("Error sending message: "+e.getMessage());
		}*/
		//Create new Incoming Event:
		/*
		try{
		Thread.sleep(15000);
		}
		catch(Exception e){
			
		}
		log.debug("Forcing a message");
        String [] dataInput = {
        		String.valueOf(GooseTools.getPositiveRandomInt()),
        		"narseo",
        		"vallina",
        		"+4498766726",
        		"btAddr1",
        		GooseMessage.LOCAL,
        		GooseMessage.UCAST,
        		String.valueOf(0),
        		String.valueOf(GooseManager.NORM_PRIORITY),
        		"GUID",
        		"",
        		GooseMessage.TEXT,
        		"content",
        		GooseMessage.INBOX_FOLDER
        		};

   		GooseMessage m1 = new GooseMessage(g.db, dataInput, true, GooseTools.getTime());
   		IncomingMessageEvent event = new IncomingMessageEvent(IncomingMessageEvent.UCAST_MESSAGE, m1);
   		ForwardingManager fwMgr = null;
   		try{
   			fwMgr = (ForwardingManager)g.getManager(GooseManager.FORWARDING_MANAGER);
   		  	fwMgr.storeNewEvent(event);	
   		}
   		catch(Exception e){
   			log.debug("forwarding manager error, trying to retrieve it from GooseManager in the UT");
   			
   		}*/
        /*
        try{
        NetworkManager ntMgr = (NetworkManager)g.getManager(GooseManager.NETWORK_MANAGER);
        String content = MessageTools.writeContactMessage(name, surname, phoneNumber, ntMgr.getLocalAddress(), ntMgr.getConnectionURL());
        log.debug(content);
        String [] param = MessageTools.readContactMessage(content);
        for (int i=0; i<param.length; i++){
        	log.debug(param[i]);
        }
        }
        catch(Exception e){
        	log.debug ("Exception building content message: "+e.getMessage());
        }*/
//   		start();
        
//        updateContactRankUnknownContact();
        
//        contactsStorageTest();
//        contactsSeekTest();
//        messageStorageTest();
        forwardingDecissionsTableTest();
	}
	
	private void forwardingDecissionsTableTest() {
		// TODO Auto-generated method stub
		NetworkManager ntMgr = null;
		ForwardingDecissionsTable fdt = new ForwardingDecissionsTable();
		try {
			ntMgr = (NetworkManager)g.getManager(GooseManager.NETWORK_MANAGER);
		} catch (GooseException e) {
			// TODO Auto-generated catch block
			log.debug("Error retrieving networkmanager");
			return;
		}
		Vector sr = ntMgr.discover();
		
		for (int i=0; i<sr.size(); i++){
			ServiceRecord tmp = (ServiceRecord)sr.elementAt(i);		
			fdt.put(tmp, "msg1");
			fdt.put(tmp, "msg3");
			if (i%2==0){
				fdt.put(tmp, "msg2");
			}
		}
		for (int i=0; i<sr.size(); i++){
			ServiceRecord tmp = (ServiceRecord)sr.elementAt(i);	
			try{
				Vector msgUUID = fdt.getMessages(tmp);
				for (int j=0; j<msgUUID.size(); j++){
					log.debug("Msgs to send to "+tmp.getHostDevice().getBluetoothAddress()+" "+(String)msgUUID.elementAt(j));
				}
			}
			catch(Exception e){
				log.debug("Error getting messages for: "+tmp.getHostDevice().getBluetoothAddress());
			}
		}	
		
		
	}

	public void printContacts(Vector c1){
		for (int i=0; i<c1.size(); i++){
			Contact c = (Contact)c1.elementAt(i);
			log.debug(c.toString());
//			log.debug("Name: "+c.getName()+" || surname: "+c.getSurname()+" || phone: "+c.getPhoneNumber()+" || btAddr: "+c.getBluetoothAddress());		
		}
	}
	
	
	public void contactsSeekTest(){
        String content1 = MessageTools.writeSearchContactMessage("name1", "none", "none");
        String content2 = MessageTools.writeSearchContactMessage("narseo", "vallina", "none");
        String content3 = MessageTools.writeSearchContactMessage("none", "surname2", "none");
        String content4 = MessageTools.writeSearchContactMessage("name14", "surname14", "+14");
        String content5 = MessageTools.writeSearchContactMessage("none", "surname11", "none");
        String content6 = MessageTools.writeSearchContactMessage("name3", "surname23", "+15");
        
        log.debug("Search content 1: "+content1);
        log.debug("Search content 2: "+content2);
        log.debug("Search content 3: "+content3);
        log.debug("Search content 4: "+content4);
        log.debug("Search content 5: "+content5);
        log.debug("Search content 6: "+content6);
        
        try{
        	
            String [] searchValues1 = MessageTools.readSearchContactMessageValue(content1);
            String [] searchValues2 = MessageTools.readSearchContactMessageValue(content2);
            String [] searchValues3 = MessageTools.readSearchContactMessageValue(content3);
            String [] searchValues4 = MessageTools.readSearchContactMessageValue(content4);
            String [] searchValues5 = MessageTools.readSearchContactMessageValue(content5);
            String [] searchValues6 = MessageTools.readSearchContactMessageValue(content6);
            
            int [] searchPatterns1 = MessageTools.readSearchContactMessagePattern(content1, searchValues1.length);
            int [] searchPatterns2 = MessageTools.readSearchContactMessagePattern(content2, searchValues2.length);
            int [] searchPatterns3 = MessageTools.readSearchContactMessagePattern(content3, searchValues3.length);
            int [] searchPatterns4 = MessageTools.readSearchContactMessagePattern(content4, searchValues4.length);
            int [] searchPatterns5 = MessageTools.readSearchContactMessagePattern(content5, searchValues5.length);
            int [] searchPatterns6 = MessageTools.readSearchContactMessagePattern(content6, searchValues6.length);
        	
            ContactsManager ctMgr = (ContactsManager)g.getManager(GooseManager.CONTACTS_MANAGER);
            
            Vector c1 = ctMgr.exclusiveSearch(searchValues1, searchPatterns1);
            log.debug("Number of contacts found for "+content1+", "+c1.size());
            printContacts(c1);
            
            Vector c2 = ctMgr.exclusiveSearch(searchValues2, searchPatterns2);
            log.debug("Number of contacts found for "+content2+", "+c2.size());
            printContacts(c2);
            
            Vector c3 = ctMgr.exclusiveSearch(searchValues3, searchPatterns3);
            log.debug("Number of contacts found for "+content3+", "+c3.size());
            printContacts(c1);
            
            Vector c4 = ctMgr.exclusiveSearch(searchValues4, searchPatterns4);
            log.debug("Number of contacts found for "+content4+", "+c4.size());
            printContacts(c4);
            
            Vector c5 = ctMgr.exclusiveSearch(searchValues5, searchPatterns5);
            log.debug("Number of contacts found for "+content5+", "+c5.size());
            printContacts(c5);
            
            Vector c6 = ctMgr.exclusiveSearch(searchValues6, searchPatterns6);
            log.debug("Number of contacts found for "+content6+", "+c6.size());
            printContacts(c6);
            
            
        }
        catch(Exception e){
        	log.debug("Exception with the contacts test: "+e.getMessage());
        }	
	}
	
	
	public void contactsStorageTest(){
		try{
			ContactsManager ctMgr = (ContactsManager)g.getManager(GooseManager.CONTACTS_MANAGER);
			for (int i=0; i<1; i++){
				Contact c = new Contact(g.db, i, "name"+i, "surname"+i, "+"+i, "bt"+i);
				ctMgr.addContact(c);
				Thread.sleep(100);
			}
			Thread.sleep(5000);
			log.debug("Number of contacts: "+ctMgr.getContacts().size());
			
		}
		catch(Exception e){
			log.debug(e.getMessage());
		}
	}
	public void messageStorageTest(){
		try{
			ForwardingManager fwMgr = (ForwardingManager)g.getManager(GooseManager.FORWARDING_MANAGER);
			for (int i=0; i<25; i++){
				String folder = null;
				String msgUUID = String.valueOf(GooseTools.getTime());
				String btAddr = "recipient"+i;
				if (i%5==0){
					folder = GooseMessage.SENT_FOLDER;
				}
				else if(i%5==1){
					folder = GooseMessage.INBOX_FOLDER;
				}
				else if (i%5==2){
					folder = GooseMessage.FORWARD_FOLDER;
									}
				else if (i%5==3){
					folder = GooseMessage.SENT_FORWD_FOLDERS;
				}
				else{
					folder = GooseMessage.INBOX_FORW_FOLDERS;
				}
				String [] dataInput = {
						"1",
						"sender name"+i,
						"sender surname"+i,
						"+"+i,
						"bt"+i,
						btAddr,
						GooseMessage.BCAST,
						"60000",
						"2",
						msgUUID,
						"prevRelay"+i,
						GooseMessage.TEXT,
						"message"+i,
						folder					
				};
				
				GooseMessage newMsg = new GooseMessage(g.db, dataInput, false, GooseTools.getTime());
				fwMgr.addMessage(newMsg);
				
				if (i%5>=2){
					fwMgr.getRCT().put(msgUUID, btAddr);
				}

				Thread.sleep(5000);
				log.debug("Message stored: "+i);
			}
			Thread.sleep(2000);
			log.debug("******************************************");
			log.debug("TEST FINISHED");
			log.debug("******************************************");
			log.debug("Total messages stored: "+fwMgr.getItems().size());
			log.debug("Messages in inbox: "+fwMgr.getInboxMessages().size());
			log.debug("Messages in sent: "+fwMgr.getSentMessages().size());
			log.debug("Messages in forwarding: "+fwMgr.getForwardingMessages().size());
			
			for (int i=0; i<fwMgr.getInboxMessages().size(); i++){
				GooseMessage tmp = (GooseMessage)fwMgr.getInboxMessages().elementAt(i);
				
				log.debug("Message in Inbox: "+tmp.GUID);
			}
			for (int i=0; i<fwMgr.getSentMessages().size(); i++){
				GooseMessage tmp = (GooseMessage)fwMgr.getSentMessages().elementAt(i);
				
				log.debug("Message in Sent: "+tmp.GUID);
			}
			for (int i=0; i<fwMgr.getForwardingMessages().size(); i++){
				GooseMessage tmp = (GooseMessage)fwMgr.getForwardingMessages().elementAt(i);
				
				log.debug("Message in Forwarding: "+tmp.GUID);
			}
		}
		catch(Exception e){
			log.debug(e.getMessage());
		}
	}
	
	public void updateContactRankUnknownContact(){
		try {
//			Contact c = new Contact("name", "surname","bt", "+76");
//			log.debug("Contact created");
			ContactsManager ctMgr = (ContactsManager)g.getManager(GooseManager.CONTACTS_MANAGER);
			ctMgr.updateContactRank("bt", 5);
			log.debug("Rank updated");
		} catch (GooseException e) {
			// TODO Auto-generated catch block
			log.debug(e.getMessage());
		}
		
	}
	
	
	public void run(){
		log.debug("Test Thread Waiting for incoming messages");
		while(alive){
			if(g.eventStack.isModified()){
				log.debug("New Message Received");
				try{
					IncomingMessageEvent event = g.eventStack.getLastEvent();
					log.debug("New goose Message: "+event.getAlertMessage());
					if (event.getMessage().transmissionMode.equalsIgnoreCase(GooseMessage.CONTACTS_EXCHANGE)){
						log.debug("Send ACK");
						String messageContent= event.getMessage().content;
						log.debug(messageContent);
						String [] senderValues = MessageTools.readContactMessage(messageContent);
				        for (int i=0; i<senderValues.length; i++){
				        	log.debug(senderValues[i]);
				        }
						log.debug("New contact to be stored: "+senderValues[0]+" "+senderValues[1]);
						g.sendContactsACK(senderValues[4], senderValues[3]);
						log.debug("Phone Number: "+senderValues[2]);
						g.createContact(senderValues);
						log.debug("Contact reply Sent");
					}
					if(event.getMessage().transmissionMode.equalsIgnoreCase(GooseMessage.CONTACTS_EXCHANGE_ACK)){
						log.debug("ContactsExchange ACK Received from: ");
						String[] values = MessageTools.readContactMessage(event.getMessage().content);
						log.debug(values[0]+" "+values[1]+", phone: "+values[2]);
						log.debug("Phone Number: "+values[2]);
						g.createContact(values);
						log.debug("Contact ACK Received, Contact Added");
					}
				}
				catch(Exception e){
					log.debug("exception receiving message: "+e.getMessage());
				}
			}
		}
	}
	public void exchangeContactDetails(){
		log.debug("Ready to perform contacts details exchange");
		Vector sr = g.getNearbyDevices();
		if (sr.size()>0){
			ServiceRecord srTmp = (ServiceRecord)sr.elementAt(0);
			log.debug(srTmp.getConnectionURL(0, false));
			try{
				
				g.exchangeContactDetails(srTmp);				
			}catch(Exception e){
				log.debug("Error Exchanging contact details: "+e.getMessage());
			}
		}else{
			log.debug("ServiceRecord vector retrieved when contactsexchange is empty");
		}
	}
	public void cleanContactsDB(){
		g.deleteAllContacts();
	}
	
	public void stopTest(){
		try{
			log.debug("Stoping Goose Manager");
			g.stopManager();
			this.alive=false;
		}
		catch(Exception e){
			log.debug("Error stoping thread: "+e.getMessage());
		}
	}

}
