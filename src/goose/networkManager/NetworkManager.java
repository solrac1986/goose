/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package goose.networkManager;

import goose.networkManager.bluetooth.Bluetooth;
import goose.networkManager.sms.SMS;
import goose.forwardingManager.GooseMessage;
import goose.forwardingManager.MessageTools;
import goose.contactsManager.ContactsManager;
import goose.mainManager.GooseManager;
import goose.mainManager.IManager;
import goose.exceptions.*;
import gui.goose.resources.UIRecordStoreTesting;

import java.util.Vector;
import javax.bluetooth.ServiceRecord;
import microlog.Logger;

/**
 *
 * @author Narseo
 */
public class NetworkManager implements IManager{

    private Bluetooth btConn;
    private SMS smsConn;
    private GooseManager gm;
    private Logger log = Logger.getLogger();


    public static final int ALL = 0;
    public static final int SMSID = 1;
    public static final int BTID = 2;


    public NetworkManager(GooseManager gm){
    	try{
            this.gm = gm;
            btConn = new Bluetooth(gm);
            UIRecordStoreTesting.macCurrentUser = btConn.getLocalAddress();
            smsConn = new SMS(gm);
//            //log.debug("Network Manager Created");    		
    	}
    	catch(Exception e){
//    		//log.debug(e.getMessage());
    	}
    }

    public void startManager () throws GooseException{
    	try{
            btConn.startListener();
            smsConn.startListener();
    	}
    	catch(Exception e){
    		throw new GooseException("Error initialising manager: "+e.getMessage());	
    	}
    }

    public void stopManager(){
        //I think it is not necessary to sync any thread here.
        btConn.stopListener();
        smsConn.stopListener();
//        //log.debug("Network Manager Stopped");
//        log=null;
    }

    public void startListener (int ID) throws GooseNetworkException{
        switch(ID){
            case ALL:
                smsConn.startListener();
                try{
                btConn.startListener();
                }
                catch(Exception e){
                	throw new GooseNetworkException("NetworkManager.startListener(), Error running bluetooth");
                }
                break;
            case SMSID:
                smsConn.startListener();
                break;
            case BTID:
                btConn.startListener();
                break;
            default:
                System.out.println("Unidentified Connectivity. Unable to start any listener");
                break;
        }
    }

    public void stopListener (int ID){
        switch(ID){
            case ALL:
                smsConn.stopListener();
                btConn.stopListener();
                break;
            case SMSID:
                smsConn.stopListener();
                break;
            case BTID:
                btConn.stopListener();
                break;
            default:
                System.out.println("Unidentified Connectivity. Unable to stop any listener");
                break;
        }
    }

    public void pauseListener (int ID){
        switch(ID){
            case ALL:
                smsConn.pauseListener();
                btConn.pauseListener();
                break;
            case SMSID:
                smsConn.pauseListener();
                break;
            case BTID:
                btConn.pauseListener();
                break;
            default:
                System.out.println("Unidentified Connectivity. Unable to pause any listener");
                break;
        }
    }

    public void restartListener (int ID){
        switch(ID){
            case ALL:
                smsConn.restartListener();
                btConn.restartListener();
                break;
            case SMSID:
                smsConn.restartListener();
                break;
            case BTID:
                btConn.restartListener();
                break;
            default:
                System.out.println("Unidentified Connectivity. Unable to restart any listener");
                break;
        }
    }
    

    public void pauseManager(){
        try{
        	this.wait();
        }
        catch (Exception e){
            //Handle exception

//        	//log.debug("thread.wait() Error setting to sleep NetworkManager thread. Pause method");
        }
    }
    
    public String getConnectionURL(){
    	return btConn.getConnectionURL();
    }

    public void restartManager(){
        try{
            this.notify();
//            //log.debug("");
        }
        catch (Exception e){
            //Handle exception
//        	//log.debug("thread.notify(), Error resuming NetworkManager thread");
        }
    }
    
    /*
     * Returns the nearby devices that support Bluetooth in the way of service record
     */
    public Vector discover(){
    	Vector disc = btConn.discoverGooseDevices();
    	try{
    		ContactsManager ctMgrRef = (ContactsManager)gm.getManager(GooseManager.CONTACTS_MANAGER);
	    	for (int i=0; i<disc.size(); i++){
	    		ServiceRecord tmpSr = (ServiceRecord)disc.elementAt(i);
	    		ctMgrRef.updateDiscoveryInformation(tmpSr.getHostDevice().getBluetoothAddress());
	    	}
    	}
    	catch(Exception e){
    		//log.debug("Exception updating contacts timeStamp "+e.getMessage());
    	}
    	return disc;
    }
    
    /*
     * Returns the local Bluetooth address
     */
    public String getLocalAddress(){
    	return btConn.getLocalAddress();
    }
    
    public IConnectivity getConnectivity(int networkInterface) throws GooseNetworkException{
    	switch(networkInterface){
    	case BTID:
    		return btConn;
    	case SMSID:
    		return smsConn;
    	default:
    		throw new GooseNetworkException("Invalid Network Interface");
    	}    	
    }
    
    /*
     * Returns the timeStamp of the last bluetooth discovery
     */
    public long getDiscoveryTimeStamp(){
    	return btConn.getDiscoveryTimeStamp();
    }
    
    public Vector getCachedDevices(){
    	return btConn.getCachedDevices();
    }
    
    public ServiceRecord searchServiceRecord(String recipientBtAddr) throws GooseSearchException{
    	Vector srCache = getCachedDevices();
    	for (int i=0; i<srCache.size(); i++){
    		ServiceRecord tmpSr = (ServiceRecord)srCache.elementAt(i);
    		if (tmpSr.getHostDevice().getBluetoothAddress().equalsIgnoreCase(recipientBtAddr)){
    			return tmpSr;
    		}    		
    	}
    	//If not found in the cache, search for nearby devices
    	srCache = discover();
    	for (int i=0; i<srCache.size(); i++){
    		ServiceRecord tmpSr = (ServiceRecord)srCache.elementAt(i);
    		if (tmpSr.getHostDevice().getBluetoothAddress().equalsIgnoreCase(recipientBtAddr)){
    			return tmpSr;
    		}    		
    	}
    	throw new GooseSearchException ("Service Record not found");
    	
    }
    
    /*
     * Sends a message to a contact.
     */
    public void send(GooseMessage m, String destAddr, int networkInterface)throws GooseNetworkException{
    	switch(networkInterface){
    	case BTID:
    		try{
	    		byte[] msg = MessageTools.serializeMessage(m);
	    		//In the case of a GooseMessage, the destAddr is in the way of a ConnectionURL retrieved from the ServiceRecord
	    		btConn.send(msg, destAddr);
    		}
    		catch(Exception e1){
    			throw new GooseNetworkException ("Error seding the data to "+destAddr+" over Bluetooth");
    		}
    		break;
    	case SMSID:
    		try{
    			smsConn.send(m.content, m.recipient);
    		}
    		catch(Exception e2){
    			throw new GooseNetworkException("Error sending data to "+destAddr+" over SMS");
    		}
    		break;
    	default:
    		throw new GooseNetworkException("Invalid Network Interface");
    	}
    }
}
