/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package goose.networkManager.bluetooth;

import gui.goose.resources.UIRecordStoreTesting;

import javax.bluetooth.LocalDevice;
import javax.microedition.io.StreamConnection;
import javax.microedition.io.Connector;
import java.io.DataOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.Date;

import microlog.Logger;
/**
 *
 * @author Narseo
 */
public class BluetoothSender {

	private static Logger log = Logger.getLogger();
	
    public static void send(LocalDevice ld, byte [] gooseMessage, String connectionURL){
    	DataInputStream in = null;
    	DataOutputStream out = null;
    	StreamConnection sc = null;
    	
    	String objective = UIRecordStoreTesting.SUCCES;
		String exception = "";
		int beginIndex =0;
        int endIndex = beginIndex;
        beginIndex = connectionURL.indexOf("btspp://") + "btspp://".length();
        endIndex = "btspp://".length() + 12;
		String destBT = connectionURL.substring(beginIndex, endIndex);
		String dataString;
		byte []data;
    	
        try{
            if(connectionURL!=null){
                try{
                    sc = (StreamConnection) Connector.open(connectionURL);
                    if (sc == null){
//                    	//log.debug("BT Connection wasn't stablished");
                    }
                    
                    out = sc.openDataOutputStream();
                    in = sc.openDataInputStream();
                }
                catch(IOException ioe){
                	objective = UIRecordStoreTesting.FAILED;
                	exception = ioe.getMessage();
                	//log.debug("Error opening connection: "+ioe.getMessage()+"Proceed to close the interface");
                	try{
            	    	if (in!=null){
            	    		in.close();
            	    		in=null;
            	    	}
            	    	if (out!=null){
            	    		out.close();
            	    		out=null;
            	    	}
            	    	if (sc!=null){
            	    		sc.close();
            	    		sc=null;
            	    	}
                	}
                	catch(IOException e){
                		objective = UIRecordStoreTesting.FAILED;
                    	exception = e.getMessage();
                		//log.debug("Error closing the connection: "+e.getMessage());
                	}
                }
                try{
                	out.writeInt(gooseMessage.length);
                	out.flush();
//                	//log.debug("First server ack received");
                	in.read();
            		try{
            			//Wait for having the database updated
            			Thread.sleep(50);
            		}
            		catch(Exception e){
//            			//log.debug("error sleeping thread");
            		}
                }
                catch(IOException ioe){
                	objective = UIRecordStoreTesting.FAILED;
                	exception = ioe.getMessage();
                	//log.debug("Error tx-ing the lenght of the message: "+ioe.getMessage());
//                	//log.debug("Call to cleanup!!!");
                	try{
            	    	if (in!=null){
            	    		in.close();
            	    		in=null;
            	    	}
                	}
                	catch(IOException e){
                		objective = UIRecordStoreTesting.FAILED;
                    	exception = e.getMessage();
                		//log.debug("Closing dataInputStream exception_"+e.getMessage());
                	}
                	try{
            	    	if (out!=null){
            	    		out.close();
            	    		out=null;
            	    	}
                	}
                	catch(IOException e){
                		objective = UIRecordStoreTesting.FAILED;
                    	exception = e.getMessage();
                		//log.debug("Closing dataOutputStream exception_"+e.getMessage());
                	}
                	try{
            	    	if (sc!=null){
            	    		sc.close();
            	    		sc=null;
            	    	}
                	}
                	catch(IOException e){
                		objective = UIRecordStoreTesting.FAILED;
                    	exception = e.getMessage();
                		//log.debug("Closing dataOutputStream exception_"+e.getMessage());
                	}
                	return;
                }
                try{
                	int bytesSent = 0;
                	int bytesToSend = 0;
                	int length = gooseMessage.length;
                	
                	while(true){
                    	
                		if((length - bytesSent)>Bluetooth.MTU){
                			bytesToSend = Bluetooth.MTU;
                		}
                		else{
                			bytesToSend = length-bytesSent;
                		}
//                		//log.debug("New chunk of "+bytesToSend+"bytes was sent. "+bytesSent+"/"+length);
                		
                		out.write(gooseMessage, bytesSent, bytesToSend);
                		                		
	                	out.flush();	                	
	                	bytesSent+=bytesToSend;
	                	if(bytesSent>=length){
	                		//log.debug("Message sent");
	                		Thread.sleep(200);
	                		break;
	                	}	                	
                	}
                }
                catch(IOException ioe){
                	objective = UIRecordStoreTesting.FAILED;
                	exception = ioe.getMessage();
                	//log.debug("Error tx-ing the message "+ioe.getMessage());
                	if (in == null){
                		//log.debug("input data stream null");
                	}
                	if (out == null){
                		//log.debug("output data stream null");
                	}
                	if (sc == null){
                		//log.debug("StreamConnection null");
                	}
//                	//log.debug("Call to cleanup!!!");
                	try{
            	    	if (in!=null){
            	    		in.close();
            	    		in=null;
            	    	}
                	}
                	catch(IOException e){
                		objective = UIRecordStoreTesting.FAILED;
                    	exception = e.getMessage();
                		//log.debug("Closing dataInputStream exception_"+e.getMessage());
                	}
                	try{
            	    	if (out!=null){
            	    		out.close();
            	    		out=null;
            	    	}
                	}
                	catch(IOException e){
                		objective = UIRecordStoreTesting.FAILED;
                    	exception = e.getMessage();
                		//log.debug("Closing dataOutputStream exception_"+e.getMessage());
                	}
                	try{
            	    	if (sc!=null){
            	    		sc.close();
            	    		sc=null;
            	    	}
                	}
                	catch(IOException e){
                		objective = UIRecordStoreTesting.FAILED;
                    	exception = e.getMessage();
                		//log.debug("Closing dataOutputStream exception_"+e.getMessage());
                	}
                	return;
                }                
            }
            else{
            	//log.debug("Unable to connect with the server");
//            	//log.debug("Call to cleanup!!!");
            	try{
        	    	if (in!=null){
        	    		in.close();
        	    		in=null;
        	    	}
            	}
            	catch(IOException e){
            		objective = UIRecordStoreTesting.FAILED;
                	exception = e.getMessage();
            		//log.debug("Closing dataInputStream exception_"+e.getMessage());
            	}
            	try{
        	    	if (out!=null){
        	    		out.close();
        	    		out=null;
        	    	}
            	}
            	catch(IOException e){
            		objective = UIRecordStoreTesting.FAILED;
                	exception = e.getMessage();
            		//log.debug("Closing dataOutputStream exception_"+e.getMessage());
            	}
            	try{
        	    	if (sc!=null){
        	    		sc.close();
        	    		sc=null;
        	    	}
            	}
            	catch(IOException e){
            		objective = UIRecordStoreTesting.FAILED;
                	exception = e.getMessage();
            		//log.debug("Closing dataOutputStream exception_"+e.getMessage());
            	}
            }
        }
        catch(Exception e){
        	//Testing only
        	objective = UIRecordStoreTesting.FAILED;
        	exception = e.getMessage();
        	
        	//log.debug("Error tx-ing the message "+e.getMessage());
        	if (in == null){
        		//log.debug("input data stream null");
        	}
        	if (out == null){
        		//log.debug("output data stream null");
        	}
        	if (sc == null){
        		//log.debug("StreamConnection null");
        	}
        	//log.debug("Exception sending data: "+e.getMessage());
           
            	//log.debug("Call to cleanup!!!");
            	try{
        	    	if (in!=null){
        	    		in.close();
        	    		in=null;
        	    	}
            	}
            	catch(IOException e1){
            		//log.debug("Closing dataInputStream exception_"+e1.getMessage());
            	}
            	try{
        	    	if (out!=null){
        	    		out.close();
        	    		out=null;
        	    	}
            	}
            	catch(IOException e2){
            		//log.debug("Closing dataOutputStream exception_"+e2.getMessage());
            	}
            	try{
        	    	if (sc!=null){
        	    		sc.close();
        	    		sc=null;
        	    	}
            	}
            	catch(IOException e2){
            		//log.debug("Closing dataOutputStream exception_"+e2.getMessage());
            	}
        }
        Date date = new Date();
        
        dataString = UIRecordStoreTesting.SEPARATOR;
    	dataString += UIRecordStoreTesting.TYPE + UIRecordStoreTesting.ERROR_BT;
    	dataString += UIRecordStoreTesting.SEPARATOR;
    	dataString += UIRecordStoreTesting.TIME + date.getTime();
    	dataString += UIRecordStoreTesting.SEPARATOR;
		dataString += UIRecordStoreTesting.DEST_BT + destBT;
		dataString += UIRecordStoreTesting.SEPARATOR ;
		dataString += UIRecordStoreTesting.OBJECTIVE + objective;
		dataString += UIRecordStoreTesting.SEPARATOR;
		dataString += UIRecordStoreTesting.EXCEPTION + exception;
		data = dataString.getBytes();
		UIRecordStoreTesting.writeTrace(data);
        
//    	//log.debug("Call to cleanup!!!");
    	try{
	    	if (in!=null){
	    		in.close();
	    		in=null;
	    	}
    	}
    	catch(IOException e){
    		//log.debug("Closing dataInputStream exception_"+e.getMessage());
    	}
    	try{
	    	if (out!=null){
	    		out.close();
	    		out=null;
	    	}
    	}
    	catch(IOException e){
    		//log.debug("Closing dataOutputStream exception_"+e.getMessage());
    	}
    	try{
	    	if (sc!=null){
	    		sc.close();
	    		sc=null;
	    	}
    	}
    	catch(IOException e2){
    		//log.debug("Closing dataOutputStream exception_"+e2.getMessage());
    	}
    }
}
