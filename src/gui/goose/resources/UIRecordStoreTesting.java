package gui.goose.resources;

import gui.goose.menus.*;

import java.io.IOException;

import javax.microedition.lcdui.Image;
import javax.microedition.rms.InvalidRecordIDException;
import javax.microedition.rms.RecordComparator;
import javax.microedition.rms.RecordEnumeration;
import javax.microedition.rms.RecordFilter;
import javax.microedition.rms.RecordStore;
import javax.microedition.rms.RecordStoreException;
import javax.microedition.rms.RecordStoreNotOpenException;

import microlog.Logger;

public class UIRecordStoreTesting {

	private static Logger log = Logger.getLogger();
	
	public static String macCurrentUser = "";
	
	private static RecordStore recordStore = null;
	public static final String NAME_TESTING_RECORD_STORE = "TestingRecordStore";
	
	public static final String MAC="mac:";
	public static final String PREVIOUS_RELAYS = "previousrelays:";
	public static final String TIME= "time:";
	public static final String MESSAGE_SENT = "messagesent";
	public static final String TIME_SENT= "timesent:";
	public static final String TYPE= "type:";
	public static final String MEMORY = "memory";
	public static final String ERROR_BT = "errorbt";
	public static final String DEST_BT = "destbt:";
	public static final String OBJECTIVE = "objective:";
	public static final String EXCEPTION = "exception:";
	public static final String SUCCES = "s";
	public static final String FAILED = "f";
	public static final String SIZE_MESSAGE = "sizemessage:";
	public static final String STATUS_MESSAGE = "statusmessage";
	public static final String SIZE_CONTENT = "sizecontent:";
	public static final String SIZE_HEADER = "sizeheader:";
	public static final String RANKING_BEFORE = "rankingbefore:";
	public static final String RANKING_AFTER = "rankingafter:";
	public static final String KNOWN_DEVICE = "knowndevice:";
	public static final String MEMORY_EVENT = "memoryevent:";
	public static final String MEMORY_MESSAGE = "memorymessage";
	public static final String MEMORY_GARBAGE = "garbage";
	public static final String MEMORY_BEFORE = "memorybefore:";
	public static final String MEMORY_AFTER = "memoryafter:";
	public static final String MESSAGE_TYPE = "messagetype:";
	public static final String BCAST = "broadcast";
	public static final String UCAST = "unicast";
	public static final String MCAST = "multicast";
	public static final String FWMGR= "fwmgr";
	public static final String NAME_OUT = "nameout:";
	public static final String NAME_IN = "namein:";
	public static final String BATTERY_LEVEL = "batterylevel:";
	public static final String NEWEVENT= "newevent";
	public static final String FRIEND_SEARCH= "friendsearch";
	public static final String DEVICES_FOUND= "devicesfound:";
	public static final String DESTINATION= "destination:";
	public static final String SOURCE= "source:";
	public static final String SEPARATOR = "//";
	public static final char END_LINE ='\n';
	public static final String LAST_ITEM = "LAST";
	
	
	
	
	public static boolean writeTrace(byte[] data) {
		try{
			String dataString = new String(data);
			//Add new final line to know when finish a event Testing saving
			dataString += UIRecordStoreTesting.END_LINE;
			data = dataString.getBytes();
	        recordStore.addRecord(data, 0, data.length);
	        return true;
	     } catch(RecordStoreException rsExc) {
	         ////log.debug("Record store writing ALL error: "+rsExc.getMessage());
	         return false;
	     } catch(SecurityException secExc) {
	    	 ////log.debug("Record store security writing ALL error: "+secExc.getMessage());
	    	return false;
	     }
		
	}
	
	public static void readTrace(UIGooseManager uiGooseManager) {
		//TODO: if phonenumber equals difierent user in the same phone
		byte[]data;
		//log.debug("UIGoose. Inside readTrace");
		try {
	         //openRecordStore(nameRecordStore);
			////log.debug("Inside readAllProfiles function searching ID: "+userID);
			if (recordStore == null) {
				//log.debug("UIGoose. Not elements to read found in readTrace"); 
				return ;
			}
			 if (recordStore.getNumRecords() == 0){
				 //log.debug("UIGoose. Not elements to read found in readTrace");
				 return ;
			 }
			 //log.debug("UIGoose. Enumerating records saved");
			 RecordEnumeration re = null;
	         re = recordStore.enumerateRecords(null, (RecordComparator)null, false);
	        
	         //log.debug("UIGoose. reading record testing size:"+ re.numRecords());
			 //re = recordStore.enumerateRecords((RecordFilter)null, (RecordComparator)null, false);
	         if (re.numRecords() == 0) {
	        	 return ;
	         } 
	        UIGooseFunction.openFileToWrite(SourceFiles.stringPath, uiGooseManager);
	        String stringAux;
			stringAux = UIRecordStoreTesting.SEPARATOR + UIRecordStoreTesting.MAC+ UIRecordStoreTesting.macCurrentUser;
			stringAux += UIRecordStoreTesting.SEPARATOR + UIRecordStoreTesting.TIME + UIGooseManager.timeStampBegin;
			UIGooseFunction.writeFile(SourceFiles.stringPath, stringAux.getBytes(), uiGooseManager); 
			while(re.hasNextElement()) {
	        	 data = re.nextRecord();
	        	 UIGooseFunction.writeFile(SourceFiles.stringPath, data, uiGooseManager);
	         }
	         UIGooseFunction.closeFileToWrite(uiGooseManager);
	         ////log.debug("num bytes readed: "+ data.length);
	         //int numBytes= recordStore.getRecord(recordStore.getNextRecordID(), data, 0);
	        ////log.debug("num bytes readed: "+ numBytes);
	         //closeRecordStore(nameRecordStore);
	     } catch(RecordStoreException rsExc) {
	         ////log.debug("Record store reading ALL error: "+rsExc.getMessage());
	         return;
	     } catch(SecurityException secExc) {
	    	 ////log.debug("Record store security reading ALL error: "+secExc.getMessage());
	    	return;
	     }
		
	}
	
	public static void openRecordStoreTesting() {
		try{
			recordStore = RecordStore.openRecordStore(UIRecordStoreTesting.NAME_TESTING_RECORD_STORE, true);
       	 	recordStore.setMode(RecordStore.AUTHMODE_PRIVATE, true);
		}catch(RecordStoreException ex) {
			//log.debug("UIGoose. Error when opening recordStore: "+ex.getMessage());
		}
	}
	
	public static void deleteRecordStoreTesting(){
		try {
			RecordEnumeration re = null;
			re = recordStore.enumerateRecords(null, null, false);
			while(re.hasNextElement()) {
				recordStore.deleteRecord(re.nextRecordId());
			}
			RecordStore.deleteRecordStore(UIRecordStoreTesting.NAME_TESTING_RECORD_STORE);
			recordStore.closeRecordStore();
			openRecordStoreTesting();
        } catch (RecordStoreException ex) {
            // Do nothing.
       	 //log.debug("UIGoose. Error when closing recordStore: "+ex.getMessage());
        }
	}
	
	public static void closeRecordStoreTesting() {
		try {
			recordStore.closeRecordStore();
        } catch (RecordStoreException ex) {
            // Do nothing.
       	 //log.debug("UIGoose. Error when closing recordStore: "+ex.getMessage());
        }
	}
	
	
}
