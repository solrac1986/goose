/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package goose.networkManager.sms;

import java.io.IOException;
import java.util.Vector;
import javax.microedition.io.Connector;
import javax.wireless.messaging.MessageConnection;
import javax.wireless.messaging.Message;
import javax.wireless.messaging.TextMessage;
import goose.forwardingManager.ForwardingManager;
import goose.forwardingManager.GooseMessage;
import goose.contactsManager.ContactsManager;
import goose.mainManager.GooseTools;
import goose.mainManager.IncomingMessageEvent;
import goose.contactsManager.Contact;
import goose.exceptions.*;
//import goose.contactsManager.*;
import microlog.Logger;
import goose.mainManager.GooseManager;

/**
 *
 * @author Narseo
 */
public class SMSListener extends Thread{

    private MessageConnection msgConn;
    private boolean alive;
    private int port;
    private GooseManager gm;
    private Logger log = Logger.getLogger();
    private String url = null;

    public SMSListener(int port, GooseManager gm){
        alive=true;
        this.port=port;
        this.gm=gm;
    }

    public void startListener(){
        try{
            //NOTE:Choosing the port may be tricky and some problems may arise from it
        	url="sms://:"+port;
            msgConn = (MessageConnection)Connector.open(url);
            //Start a message-reading thread
            alive=true;
            start();
        }
        catch(IOException e){
            //Handle Exception
        	//log.debug("Error while creating SMS Listener");
        }
    }

    public void pauseListener(){
        try{
            this.wait();
        }
        catch(Exception e){
            //TODO: Handle exception
        }
    }

    public void restartListener(){
        try{
            this.notify();
        }
        catch(Exception e){
            //TODO: Handle exception
        }
    }

    //Method that stops the listener. It releases the connection and signal the reader thread to terminate
    public void stopListener(){
        alive = false;
        try{
            msgConn.setMessageListener(null);
            msgConn.close();
        }
        catch(IOException e){
            //TODO: Handle stop listener error
        }
    }      
        
    public void run(){

//    	//log.debug("SMS listener running: "+url);
        while (alive){
                    try{
                    	
                        Message msg = msgConn.receive();
                        if(msg instanceof TextMessage)
                        {                        	
//                        	//log.debug("Text Message-received");
                        	TextMessage sms = (TextMessage)msg;
//                        	//log.debug("sender: "+sms.getAddress());
//                        	//log.debug("content: "+sms.getPayloadText());
                        	
                    		ForwardingManager fwMgrRef = (ForwardingManager)gm.getManager(GooseManager.FORWARDING_MANAGER);
                    		ContactsManager ctMgrRef = (ContactsManager)gm.getManager(GooseManager.CONTACTS_MANAGER);
                    		Vector senders = ctMgrRef.exclusiveSearch(sms.getAddress(), ContactsManager.PHONENUMBER);
                    		Contact contact = (Contact)senders.elementAt(0);
                    		
                    		//It is necessary to re-build the whole SMS
                    		String msgGUID = sms.getAddress()+sms.getTimestamp().toString();
                            String [] dataInput = {
                            		String.valueOf(fwMgrRef.currentIndex()),
                            		contact.getName(),
                            		contact.getSurname(),
                            		sms.getAddress(),
                            		contact.getBluetoothAddress(),
                            		GooseMessage.LOCAL,
                            		GooseMessage.UCAST,
                            		String.valueOf(0),
                            		String.valueOf(GooseManager.NORM_PRIORITY),
                            		msgGUID,
                            		"",
                            		GooseMessage.TEXT,
                            		sms.getPayloadText(),
                            		GooseMessage.INBOX_FOLDER
                            		};
//                            //log.debug("New message received from: "+sms.getAddress()+" at "+sms.getTimestamp());
//                            //log.debug("Content: ");
//                            //log.debug(sms.getPayloadText());

                       		GooseMessage m = new GooseMessage(gm.db, dataInput, true, GooseTools.getTime());
                       		IncomingMessageEvent event = new IncomingMessageEvent(IncomingMessageEvent.UCAST_MESSAGE, m);
                       		gm.eventStack.storeNewEvent(event);
                        }
                    }
                    catch(IOException ioe){
                        //TODO: Handle error
//                    	//log.debug("Exception: "+ioe.getMessage());
                    }
                    catch (GooseException ge){
                    	//TODO. Handle error, maybe nothing will happen.
//                    	//log.debug("Exception: "+ge.getMessage());
                    }
                    catch(Exception e){
//                    	//log.debug("Exception: "+e.getMessage());
                    }
                }
            }
        
}

