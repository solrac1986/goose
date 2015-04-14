/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package goose.networkManager.bluetooth;


import java.io.IOException;
import java.util.Vector;

import javax.bluetooth.DeviceClass;
import javax.bluetooth.DiscoveryAgent;
import javax.bluetooth.DiscoveryListener;
import javax.bluetooth.LocalDevice;
import javax.bluetooth.RemoteDevice;
import javax.bluetooth.ServiceRecord;
import javax.bluetooth.BluetoothStateException;
import javax.bluetooth.UUID;
import microlog.Logger;

/**
 *
 * @author Narseo
 */
public class BluetoothDeviceDiscovery implements DiscoveryListener{

    //Object used for waiting
    private static final Object lock = new Object();
    //vector containing the devices discovered
    private static Vector vecDevices = new Vector();        
    //Keeps track of the servicesDiscovered and stores the ServiceRecord for it.
    private static Vector servicesDisc = new Vector();    
    private static Logger log = Logger.getLogger();    
    private static DiscoveryAgent discAgent;
    
    
    private static BluetoothDeviceDiscovery btDevDisc = new BluetoothDeviceDiscovery();
    
    public BluetoothDeviceDiscovery(){
    }    
    
    //main method of the application. It performs a Bluetooth scanning
    public static void startDiscovery () throws IOException {

    	removePreviousElements();
    	//Initialise the transaction list    	
        LocalDevice localDev = LocalDevice.getLocalDevice();
        //find devices. We need to get a DiscoveryAgent from the LocalDevice
        discAgent = localDev.getDiscoveryAgent();
        BluetoothDeviceDiscovery btDeviceDiscovery = new BluetoothDeviceDiscovery();

        try{
            //GIAC allows the device to be discoverable for an indefinite amount of time. LIAC is for a limited time
            discAgent.startInquiry(DiscoveryAgent.GIAC, btDeviceDiscovery);
            try{
                synchronized(lock){
                    lock.wait();
                }
            }
            catch(InterruptedException e){
                e.printStackTrace();
                //log.debug("startDiscovery. Bluetooth exception : "+e.getMessage());
            }            
        }
        catch(BluetoothStateException e){
            //Do whatever it has to be done here. Write the log or whatever
        }
        try{        	
            if(vecDevices.size() <= 0){
            	  //log.debug("BluetoothDiscovery. No Devices Found .");
            }
            else{
            	for (int i=0; i<vecDevices.size(); i++){
            		RemoteDevice tmp = (RemoteDevice)vecDevices.elementAt(i);
//            		//log.debug("Searching services for: "+tmp.getBluetoothAddress());
            		
                    UUID[] uuidSet = new UUID[1];
                    uuidSet[0]=new UUID(Bluetooth.GooseUUID, false);
//                    uuidSet[1]=new UUID(Bluetooth.ExchangerUUID, false);
//                    uuidSet[1]=new UUID(0x1101);
//                    L2CAP
//                    uuidSet[2] = new UUID(0x0100);
//                    uuidSet[3] = new UUID(0x0003);
                    
                    discAgent.searchServices(null, uuidSet, tmp, btDevDisc);

                    try {
	                    synchronized(lock){
	                    	lock.wait();
	                    }
                    }
                    catch(Exception e){
                    	//log.debug(e.getMessage());
                    }
            	}//for
            }          	  
        }
        catch(Exception e){
        	//log.debug(e.getMessage());
        }
        //log.debug("BluetoothDiscovery. inquiryCompleted .#DevDisc: "+servicesDisc.size());
        
//        //log.debug("Services search completed. "+servicesDisc.size()+" services found");
    }
    
    
    /*
     * Callback Method performed every time a new bluetooth device is discovered during a scanning
     * (non-Javadoc)
     * @see javax.bluetooth.DiscoveryListener#deviceDiscovered(javax.bluetooth.RemoteDevice, javax.bluetooth.DeviceClass)
     */
    public void deviceDiscovered(RemoteDevice remoteDevice, DeviceClass deviceClass){    	
    	if((deviceClass.getMajorDeviceClass() & 0x200)!=0){    		
//	        printDeviceInfo(remoteDevice, deviceClass);	        
	        if(!vecDevices.contains(remoteDevice)){
	            vecDevices.addElement(remoteDevice);
	        }
    	}
    }
    
    public static void removePreviousElements(){
    	vecDevices.removeAllElements();
    	servicesDisc.removeAllElements();
    }

    //
    public void inquiryCompleted (int discType){
        switch(discType){
            case DiscoveryListener.INQUIRY_COMPLETED:
                break;
            case DiscoveryListener.INQUIRY_TERMINATED:
            	removePreviousElements();
                break;
            case DiscoveryListener.INQUIRY_ERROR:
            	//log.debug("ERROR. Bluetooth Inquiry Error");
            	removePreviousElements();
                break;
            default:
                break;
        }

        //Print in the log the time and the number of devices discovered
        
        synchronized(lock){
        	lock.notify();
        }        
    }
    

    /*
     * The following method is called when a service search is completed or was terminated because of an error. Legal status values include:
     * <code>SERVICE_SEARCH_COMPLETED</code>
     * <code>SERVICE_SEARCH_TERMINATED</code>
     * <code>SERVICE_SEARCH_ERROR</code>
     * <code>SERVICE_SEARCH_DEVICE_NOT_REACHABLE</code>
     * <code>SERVICE_SEARCH_NO_RECORDS</code>
     * (non-Javadoc)
     * @see javax.bluetooth.DiscoveryListener#serviceSearchCompleted(int, int)
     */
    public void serviceSearchCompleted(int pTransId, int pRespCode){
        //Print in the log the time and the number of services discovered
        synchronized(lock){
        	lock.notifyAll();
        }     
    }

    /*
     * Called when service(s) are found during a service search.
     * This method  provides the arraz of services that have been found.
     * (non-Javadoc)
     * @see javax.bluetooth.DiscoveryListener#servicesDiscovered(int, javax.bluetooth.ServiceRecord[])
     */
    public void servicesDiscovered (int pTransId, ServiceRecord [] pServiceRecords){
    	/*
    	 * If this is the first record found, then store this record and cancel the remaining searches
    	 */    	
    	for (int i=0; i<pServiceRecords.length; i++){
    		servicesDisc.addElement(pServiceRecords[i]);
    	}  	    	
        synchronized(lock){
        	lock.notifyAll();
        }    	
    }
    /*
    private void cancelDiscovery(){
    	synchronized(lock){
    		discAgent.cancelInquiry(this);
    	}
    }
    
   
    private void printDeviceInfo(RemoteDevice remDevice, DeviceClass deviceClass){
        //Print in the log the devices discovered
    	try{
    		//log.debug("NEW MOBILE PHONE. "+remDevice.getBluetoothAddress()+"|"+remDevice.getFriendlyName(false));
    		//log.debug("Major device class: "+deviceClass.getMajorDeviceClass());
    		//log.debug("Minor device class: "+deviceClass.getMinorDeviceClass());
    	}
    	catch(Exception e){
    		//log.debug("Error retrieving device info");
    	}
    }*/

    public static Vector getAllNearbyDevices(){
    	return vecDevices;
    }
    
    public static Vector getServiceRecord(){
    	return servicesDisc;
    }
    
    public static Vector getGooseDevices(){
    	Vector rt = new Vector();
        for (int i=0; i<servicesDisc.size(); i++){
        	ServiceRecord tmpSR = (ServiceRecord)servicesDisc.elementAt(i);
        	RemoteDevice tmpRD = tmpSR.getHostDevice();
        	rt.addElement(tmpRD);
        }
        return rt;
    }

}
