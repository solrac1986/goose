/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package goose.networkManager.sms;

import javax.wireless.messaging.TextMessage;
import javax.wireless.messaging.MessageConnection;
import javax.microedition.io.Connector;
import microlog.Logger;
/**
 *
 * @author Narseo
 */
public class SMSSender {

    public static void sendSMS (String message, String destination, int port){

    	Logger log = Logger.getLogger();
    	String url = "sms://"+destination+":"+port;
//    	String url = "sms://"+destination;
//    	//log.debug("Sending sms to: "+url);;
        try{        	
            MessageConnection msgConn = (MessageConnection)Connector.open(url);
            TextMessage txtMsg = (TextMessage)msgConn.newMessage(MessageConnection.TEXT_MESSAGE);
            txtMsg.setAddress(url);
            txtMsg.setPayloadText(message);
            msgConn.send(txtMsg);
            //log.debug("SMS Sent");
            msgConn.close();
        }
        catch(Exception e){
            //TODO: Handle Exception
        	//log.debug("Error sending sms: "+e.getMessage());
        	
        }
    }


}
