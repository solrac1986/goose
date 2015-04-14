/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package goose.networkManager.sms;

import goose.networkManager.IConnectivity;
import goose.exceptions.*;
import java.util.Vector;
import goose.mainManager.GooseManager;

/**
 *
 * @author Narseo
 */
public class SMS implements IConnectivity{

    private int port=6222;

    private SMSListener listener;

    public SMS(GooseManager gm){
        listener = new SMSListener(port, gm);
    }

    public boolean startListener(){
        listener.startListener();
        return true;
    }

    public void stopListener(){
        listener.stopListener();
    }

    public void pauseListener(){
        listener.pauseListener();
    }

    public void restartListener(){
        listener.restartListener();
    }

    public void send(String gooseMessage, String destination){
        SMSSender.sendSMS(gooseMessage, destination, port);
    }

    public Vector discoverDevices() throws GooseNetworkException{
    	throw new GooseNetworkException("Interface does not support device discovering");
    }
    
    public void setPort(int port){
        this.port=port;
    }

}
