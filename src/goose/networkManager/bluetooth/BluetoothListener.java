/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package goose.networkManager.bluetooth;

import javax.microedition.io.StreamConnection;
import javax.microedition.io.StreamConnectionNotifier;
import javax.microedition.io.Connector;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import microlog.Logger;
import goose.forwardingManager.ForwardingManager;
import goose.mainManager.GooseManager;
import goose.mainManager.IncomingMessageEvent;
import javax.bluetooth.BluetoothConnectionException;
import javax.bluetooth.LocalDevice;
import javax.bluetooth.ServiceRecord;


/**
 *
 * @author Narseo
 */
public class BluetoothListener extends Thread{

    private boolean quit = false;
    private StreamConnectionNotifier scn = null;
    private StreamConnection sc = null;
    private GooseManager gm;
    
    //Not all the JSR-82 capable devices support OBEX (object exchange) so it is necessary
    //to transmit the data using Bluetooth Serial Port Protocol (btspp)
    private String connProtocol = "btspp";
    //we set target to localhost to start a server
    private String connTarget = "localhost";

    
    private Logger log = Logger.getLogger();
    

    private DataInputStream in;
    private DataOutputStream out;
    private String UUID = null;

    public BluetoothListener(GooseManager gm, String UUID){
        this.gm = gm;
        this.UUID=UUID;
    }

    public void startListener (){
        try{
            //Create server. Both the uuid and the serviceName are used to describe the type of service that is suported
        	String url = connProtocol+"://"+connTarget +":" +UUID+";" +"name=" + GooseManager.firstname;
        	
//        	//log.debug("Bluetooth Listener started: "+url);

            //Maybe the stream connection notifier must be created in the constructor. It 
        	//allow us to advertise the service to other nearby devices.
            scn = (StreamConnectionNotifier)Connector.open(url);
                        
            start();
        }
        catch(Exception e){
//        	//log.debug("ERROR initializing BT Listener Thread");
        }
    }


    public void run(){
        //Check what to do and where to take action in case the connection is not closed
        while(!quit){
            //This is a blocking method until a new connection is received
        	//The SPP can accept multiple connections from different clients by calling acceptAndOpen repeteadly
            try{
            	//Indicate that the service is ready to accept a client connection
            	sc = (StreamConnection) scn.acceptAndOpen();
                
                in = sc.openDataInputStream();
                out = sc.openDataOutputStream();
                                
                byte []buffer = null;
                int messageLength = 0;          
                
                //Read the number of bytesToRead to initialize the buffer
                try{
                	messageLength = in.readInt();
//                	//log.debug("Full message size: "+messageLength+" bytes");
                    buffer = new byte[messageLength];
                    out.write(0);
                    out.close();
                }
                catch(EOFException eof){
                	//log.debug("EOF in First read. "+eof.getMessage());
                	cleanup();
                	return;
                }
                catch(IOException ioe){
                	//log.debug("Error in first read. "+ioe.getMessage());
                	cleanup();
                	return;
                }
                /*
                 * Perform communication
                 */
                int bytes = 0;
                int offset = 0;
                while (true) {
                	//It reads up to buffer.length-offset bytes! If less, it will read less
                     bytes = in.read(buffer, offset, buffer.length-offset);                     
//                     //log.debug("New chunk received of "+bytes+"In total: "+offset+"/"+ buffer.length);                  	                   	 
                     offset += bytes;
                     
                     if (bytes == -1 || offset >= buffer.length) {                         
                         IncomingMessageEvent tmp = new IncomingMessageEvent(IncomingMessageEvent.BT_MSG, buffer);
                         ForwardingManager tmpFwRef = (ForwardingManager)gm.getManager(GooseManager.FORWARDING_MANAGER);
                         tmpFwRef.storeNewEvent(tmp);
                    	 cleanup();
//                    	 //log.debug("New message received over Bluetooth. Tx completed and connection closed");
                         break;                     
                     }
                }            
            }
            catch (BluetoothConnectionException bce){
//            	//log.debug("Error when enabling new input connections: "+bce.getMessage());
//            	We cannot return. Otherwise we will kill the server. It will be initialised again when a new incoming conexion will be received
            	cleanup();            	
            }
            catch (Exception e){
//            	//log.debug("Error with a BT income connection "+e.getMessage());
            	//We cannot return. Otherwise we will kill the server. It will be initialised again when a new incoming conexion will be received
            	cleanup();
            }
        }
    }
    
    public String getConnectionURL(){

    	try{
	    	ServiceRecord rb= LocalDevice.getLocalDevice().getRecord(scn);
	//    	 gets senders connection URL
	    	return rb.getConnectionURL(ServiceRecord.NOAUTHENTICATE_NOENCRYPT,false).toString();     	
	    }
    	catch(Exception e){
    		//log.debug("error getting connection url");
    		return "";
    	}
    }
    
    public void cleanup(){
    	//log.debug("Entering cleanup");
    	try{
	    	if (in!=null){
	    		in.close();
	    	}
    	}
    	catch(IOException e){
    		//log.debug("Closing dataInputStream exception_"+e.getMessage());
    	}
    	try{
	    	if (out!=null){
	    		out.close();
	    	}
    	}
    	catch(IOException e){
    		//log.debug("Closing dataOutputStream exception_"+e.getMessage());
    	}
    	
    	try{
	    	if (sc!=null){
	    		sc.close();
	    	}
    	}
    	catch(IOException e){
    		//log.debug("Closing StreamConnection exception_"+e.getMessage());
    	}
    	in = null;
    	out = null;
    	sc = null;
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

    public void stopListener(){
        quit = true;
        closeBT();                                                         
    }

    private void closeBT(){
        try{                                                                              
        	sc.close();
            scn.close();   
            sc=null;
            scn = null;
        }
        catch(Exception e){
        	//log.debug("Error closing BT Connections");
        }
    }
}
