/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package goose.networkManager.bluetooth;



import java.util.Vector;

import javax.bluetooth.BluetoothStateException;
import javax.bluetooth.LocalDevice;
import javax.bluetooth.DiscoveryAgent;
import microlog.Logger;
import goose.exceptions.*;
import goose.mainManager.GooseManager;
import goose.mainManager.GooseTools;
import goose.networkManager.IConnectivity;
import javax.bluetooth.ServiceRecord;
import javax.microedition.io.StreamConnectionNotifier;

/**
 *
 * @author Narseo
 */
public class Bluetooth implements IConnectivity{

    //Object that contains the local address and the local name of the device
    private static LocalDevice localDev = null;
    public static String GooseUUID = "8f05bcf0544811de8a390800200c9a66";   
//    public static String ExchangerUUID = "8f05bcf0544811de8a390800200c9a77";
    public static String connServiceName = "GooseBT";
    public static int MTU = 256;
    public static long lastDiscoveryTimeStamp = 0;
    
    private BluetoothListener gooseListener = null;
    private Logger log = Logger.getLogger();
    //It is used as a cache for the last discovered devices
    private Vector lastDiscovery = new Vector();
    
    public Bluetooth (GooseManager gm) throws GooseNetworkException{
        try{
            localDev = LocalDevice.getLocalDevice();
            gooseListener = new BluetoothListener(gm, GooseUUID);
        }
        catch (BluetoothStateException e){
	        throw new GooseNetworkException("Bluetooth Interface OFF");
        }

    }

    
    public boolean startListener() throws GooseNetworkException{
        if (!isDiscoverable()){
            //Set in discoverable mode
        	initialiseBluetooth();
            setDiscoverable();
        }
        try{
            gooseListener.startListener();
            return true;
        }
        catch(Exception e){
            //log.debug("Error initializing the BT Listener");
            throw new GooseNetworkException("Error starting bluetooth");
        }
    }

    public String getConnectionURL(){
    	return gooseListener.getConnectionURL();
    }
    
    public void stopListener(){
        try{
            gooseListener.stopListener();
        }
        catch (Exception e){
        	//log.debug("Error stoping BT Listener thread");
        }

    }

    public void pauseListener(){
        gooseListener.pauseListener();
    }

    public void restartListener(){
        gooseListener.restartListener();
    }

    /*
     * Returns a vector with the ServiceRecord that contains the bluetooth address of the nearby devices
     */
    public Vector discoverGooseDevices(){
        try{
            BluetoothDeviceDiscovery.startDiscovery();
            //This is a blocking process so we will only get the remote devices once it is finished
            lastDiscovery = BluetoothDeviceDiscovery.getServiceRecord();
            updateDiscoveryTimeStamp();
            return lastDiscovery;
        }
        catch (Exception e){
//        	//log.debug("Error discovering devices");
            return null;
        }
    }

    public void updateDiscoveryTimeStamp(){
        lastDiscoveryTimeStamp = GooseTools.getTime();	
    }
    
    public long getDiscoveryTimeStamp(){
    	return lastDiscoveryTimeStamp;
    }
    
    /*
     * (non-Javadoc)
     * @see goose.networkManager.IConnectivity#discoverDevices()
     */
    public Vector discoverDevices(){
        try{
            BluetoothDeviceDiscovery.startDiscovery();
            //We do not update the timeStamp in this case since it does not retrieve only goose devices
            //This is a blocking process so we will only get the remote devices once it is finished
            return BluetoothDeviceDiscovery.getAllNearbyDevices();
        }
        catch (Exception e){
        	//log.debug("Error discovering devices");
            return null;
        }   	
    }
    
    public Vector getCachedDevices(){
    	return lastDiscovery;
    }
    
    /*
     * 
     */
    public Vector getNearbyGooseBluetoothAddress(){
    	return BluetoothDeviceDiscovery.getGooseDevices();
    }
    
    public static String getLocalAddress(){
    	try{
	    	if (localDev == null){
	            localDev = LocalDevice.getLocalDevice();
	    	}
    	}
    	catch(Exception e){
//    		System.out.println("Error retrieving local BT Address");
    	}
        return localDev.getBluetoothAddress();
    }

    public String getLocalBluetoothName(){
        return localDev.getFriendlyName();
    }

    public void initialiseBluetooth(){
    	
    }
    
    public void setDiscoverable(){
        try{
            localDev.setDiscoverable(DiscoveryAgent.GIAC);
            retrieveDiscoveryMode();
        }
        catch(BluetoothStateException e){
            e.printStackTrace();
        }
    }

    public void send(byte [] gooseMessage, String connectionURL){
        BluetoothSender.send(localDev, gooseMessage, connectionURL);
    }

    //Returns true if the device is in GIAC discoverable mode
    public boolean isDiscoverable(){
        int discMode = localDev.getDiscoverable();

        if (discMode != DiscoveryAgent.GIAC){
            return false;
        }
        return true;
    }

    private void retrieveDiscoveryMode (){
        int discMode = localDev.getDiscoverable();
        //TODO: Print in the log
        switch(discMode){
            case DiscoveryAgent.GIAC:
//            	//log.debug("GIAC");
                break;
            case DiscoveryAgent.LIAC:
//            	//log.debug("LIAC");
                break;
            case DiscoveryAgent.NOT_DISCOVERABLE:
            	//log.debug("NOT DISCOVERABLE");
                break;
            default:
            	//log.debug("UNKNOWN");
                break;
        }
    }
    
    public static ServiceRecord getServiceRecord (StreamConnectionNotifier scn){
    	return localDev.getRecord(scn);
    }
}
