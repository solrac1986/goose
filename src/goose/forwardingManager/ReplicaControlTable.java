package goose.forwardingManager;

import java.util.Hashtable;
import java.util.Vector;
import java.util.Date;
//import microlog.Logger;
import goose.exceptions.GooseException;

public class ReplicaControlTable {

	//UUID; Vector(String)->BluetoothMAC_ADDRESS
	private Hashtable entries;
	//UUID; Vector(long)->TimeStamp
	private Hashtable entriesInfo;
//	private Logger log = Logger.getLogger();
	
	public ReplicaControlTable(){
		
		entries = new Hashtable();
		entriesInfo = new Hashtable();
	}
	
	public synchronized boolean exists (String UUID, String bluetoothAddress){
		
		if(entries.containsKey(UUID.toLowerCase())){
			Vector v = (Vector)entries.get(UUID.toLowerCase());
			if (v!=null){
				if (v.contains(bluetoothAddress.toLowerCase())){
//					//log.debug("RCT: Object "+UUID.toLowerCase()+" already exists in RCT");
					return true;					
				}
			}		
		}
//		//log.debug("RCT: Object "+UUID.toLowerCase()+" does not exists in RCT");
		return false;
	}
	
	public synchronized void put (String UUID, String bluetoothAddress){
		Vector v = null;
		if(entries.containsKey(UUID.toLowerCase())){
			v = (Vector)entries.get(UUID.toLowerCase());
			if (v!=null){
				if(v.contains(bluetoothAddress.toLowerCase())){

//					//log.debug("RCT.put: Object "+UUID.toLowerCase()+" already exists in RCT");
					
					return;
				}
				else{
//					//log.debug("RCT.put: Object "+UUID.toLowerCase()+" does not exists in RCT. ADDED");
					v.addElement(bluetoothAddress.toLowerCase());
				}
			}
			entries.remove(v);
		}
		else{
			//Complete new object
			v = new Vector();
			v.addElement(bluetoothAddress.toLowerCase());
			Date now = new Date();
			entriesInfo.put(UUID.toLowerCase(), new Long(now.getTime()));
//			//log.debug("RCT, Adding element: "+UUID);
		}
		entries.put(UUID.toLowerCase(), v);
	}
	
	public synchronized void put (String UUID, Vector arrayBluetoothAddress){
		for (int i=0; i<arrayBluetoothAddress.size(); i++){
			String tmp = (String)arrayBluetoothAddress.elementAt(i);
			put(UUID, tmp);
		}
	}
	
	public synchronized long getTimeIn (String UUID) throws GooseException{
		if(entriesInfo.containsKey(UUID.toLowerCase())){
			Long value = (Long)entriesInfo.get(UUID.toLowerCase());
			return value.longValue();
		}
		throw new GooseException ("Not entry found in RCT. "+UUID.toLowerCase());
	}
	
	public synchronized Vector getRecipient (String UUID){
		if (entries.containsKey(UUID.toLowerCase())){
			return (Vector)entries.get(UUID.toLowerCase());
		}
		else{
			return new Vector();
		}
	}
	
	public synchronized void update(String UUID, String listOfRelays){
		Vector v = MessageTools.readRecipientList(listOfRelays);
		for (int i=0;  i<v.size(); i++){
			String tmpBt = (String)v.elementAt(i);
			put(UUID.toLowerCase(), tmpBt.toLowerCase());
//			//log.debug("RCT. "+UUID.toLowerCase()+" entry updated");
		}
	}
}
