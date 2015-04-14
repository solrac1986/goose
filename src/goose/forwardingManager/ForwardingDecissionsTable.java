package goose.forwardingManager;

import java.util.Hashtable;
import java.util.Vector;
import javax.bluetooth.ServiceRecord;

/**
*
* @author Narseo
*/
public class ForwardingDecissionsTable {

	//User_ServiceRecord; Vector(String)->Message_GUID_toForward
	private Hashtable entries;
//	private Logger log = Logger.getLogger();
	
	public ForwardingDecissionsTable(){
		
		entries = new Hashtable();
	}
	
	public synchronized boolean exists (ServiceRecord sr, String msgGUID){
		if(entries.containsKey(sr)){
			Vector v = (Vector)entries.get(sr);
			if (v!=null){
				if (v.contains(msgGUID.toLowerCase())){
//					//log.debug("EXISTS: Object "+msgGUID+" already stored in forwarding decissions table for "+sr.getHostDevice().getBluetoothAddress());
					return true;
				}
			}		
		}
//		//log.debug("EXISTS: Object "+msgGUID+" IS NOT stored in forwarding decissions table for "+sr.getHostDevice().getBluetoothAddress());
		
		return false;
	}
	
	public synchronized void put (ServiceRecord sr, String msgGUID){
		if (exists(sr, msgGUID)){
//			//log.debug("PUT. Object "+msgGUID+" already stored in forwarding decissions table for "+sr.getHostDevice().getBluetoothAddress());
			return;
		}
		Vector v = null;
		if(entries.containsKey(sr)){
			v = (Vector)entries.get(sr);
			if (v!=null){
				if(v.contains(msgGUID.toLowerCase())){
//					//log.debug("PUT: Object "+msgGUID+" already included in forwarding decissions table for "+sr.getHostDevice().getBluetoothAddress());				
					return;
				}
				else{
//					//log.debug("PUT: Object "+msgGUID+" will be updated for "+sr.getHostDevice().getBluetoothAddress());
					
					v.addElement(msgGUID.toLowerCase());
				}
			}
			entries.remove(v);
		}
		else{
			//Complete new object
//			//log.debug("Object "+msgGUID+" is not stored in forwarding decissions table for "+sr.getHostDevice().getBluetoothAddress());
			
			v = new Vector();
			v.addElement(msgGUID.toLowerCase());
//			//log.debug("New object added and updated");
		}
		try{
			entries.put(sr, v);
//			//log.debug("PUT: Entry updated");
			
		}
		catch(Exception e){
//			//log.debug("PUT. Error updating entry: "+e.getMessage());
		}
	}
		
	public synchronized Vector getMessages (ServiceRecord sr){
		if(entries.containsKey(sr)){
			Vector v = (Vector)entries.get(sr);
			if (v!=null){
//				//log.debug("ForwardingDecissionsTable.getMessages() Number of messages to send found for "+sr.getHostDevice().getBluetoothAddress()+": "+v.size());
				return v;
			}
		}
//		//log.debug("No entry found for "+sr.getHostDevice().getBluetoothAddress());
		return new Vector();
	}
}