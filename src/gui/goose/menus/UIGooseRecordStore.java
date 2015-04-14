package gui.goose.menus;

import java.io.IOException;
import java.util.Vector;

import goose.contactsManager.Contact;
import gui.goose.resources.SourceFiles;

import javax.microedition.lcdui.Image;
import javax.microedition.rms.InvalidRecordIDException;
import javax.microedition.rms.RecordComparator;
import javax.microedition.rms.RecordEnumeration;
import javax.microedition.rms.RecordFilter;
import javax.microedition.rms.RecordListener;
import javax.microedition.rms.RecordStore;
import javax.microedition.rms.RecordStoreException;
import javax.microedition.rms.RecordStoreNotFoundException;
import javax.microedition.rms.RecordStoreNotOpenException;

import microlog.Logger;

public class UIGooseRecordStore implements RecordListener, RecordFilter{

	private final int VALUE_TOP = 10;
	private  RecordStore recordStore = null;
	
	private RecordStore allRecordStore  = null;
	private String userID = "";
	
	private static Logger log = Logger.getLogger();
	private static int currentMenu;
	
	
	public RecordFilter recordFilter = null; 
	
	
	public UIGooseRecordStore () {
		try {
			openRecordStore(SourceFiles.RECORD_STORE_GOOSE_WALL);
			openRecordStore(SourceFiles.RECORD_STORE_ALL_PROFILE);
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			//log.debug("RecordStore error when open: "+ e.getMessage());
		} catch (RecordStoreException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			//log.debug("RecordStore error when open: "+ e.getMessage());
		}
	}
	
	
	public boolean writeRecordStore(byte[] data){
		
		try {
			RecordEnumeration re=null;
			re = recordStore.enumerateRecords(null, null, false);
			 //re = allRecordStore.enumerateRecords((RecordFilter)null, (RecordComparator)null, false);
	         if (re.numRecords() == 0) {
	        	 //log.debug("UIGoose. New element ID ");
	        	 recordStore.addRecord(data, 0, data.length);
	        	 return false;
	         } 
	         int numProfile = re.nextRecordId();
	         //log.debug("UIGoose. Edit element ID: "+numProfile);
	         recordStore.setRecord(numProfile, data, 0, data.length);
			return true;
		} catch (RecordStoreNotOpenException e) {
			// TODO Auto-generated catch block
			System.out.println("error: "+e.getMessage());
			return false;
		} catch (InvalidRecordIDException e) {
			System.out.println("error: "+e.getMessage());
			return false;
		} catch (RecordStoreException e) {
			System.out.println("error: "+e.getMessage());
			return false;
		}
		
	}
	
	
	public byte[] readRecordStore() {
		//log.debug("UIGoose. Before reading record Store");
		RecordEnumeration re = null;
		try {
			re = recordStore.enumerateRecords(null, null, false);
			if(re.hasNextElement()) {
				//log.debug("we have a next element");
				 Image uiCustomImage = null;
					try {
						uiCustomImage = Image.createImage(SourceFiles.SOURCE_BUDDY);
					} catch (IOException e) {
						// TODO Auto-generated catch block
					}
				 //log.debug("UIGoose. After reading record: "+re.numRecords());
				 int index;
				 int indexEnd;
				 byte[] dataProfiles;
				 String auxString;
				 String name;
				 String surname;
					 
				dataProfiles = re.nextRecord();
				auxString = new String(dataProfiles);
				index = auxString.indexOf("//name:")+"//name:".length();
				indexEnd = auxString.indexOf(";//surname:");
				name = auxString.substring(index, indexEnd).toLowerCase();
				index = auxString.indexOf("//surname:")+"//surname:".length();
				indexEnd = auxString.indexOf(";//phonenumber:");
				surname = auxString.substring(index, indexEnd).toLowerCase();
				UIGooseSelectProfile.list.append(name +" "+surname, uiCustomImage);
				return dataProfiles;
			}
			return null;
		}
		catch (Exception e) {
				// TODO Auto-generated catch block
			System.out.println("exception "+e.getMessage());
			return null;
		}
	}
	
	
	
	public boolean writeAllProfiles(byte[] data, String nameRecordStore, String userID, int menuView) {
		try{
	         //openRecordStore(nameRecordStore);
			//log.debug("Record store writing size data: "+new String(data));
			
			System.out.println("userID: "+userID);
			RecordEnumeration re = null;
		
			this.userID = userID;
			
		
	         re = allRecordStore.enumerateRecords(this, (RecordComparator)null, false);
			 //re = allRecordStore.enumerateRecords((RecordFilter)null, (RecordComparator)null, false);
	         if (re.numRecords() == 0) {
	        	 //log.debug("UIGoose. New element ID ");
	        	 allRecordStore.addRecord(data, 0, data.length);
	        	 return false;
	         } 
	         
	         int numProfile = re.nextRecordId();
	         //log.debug("UIGoose. Edit element ID: "+numProfile);
	         allRecordStore.setRecord(numProfile, data, 0, data.length);
			
	         return true;
	     } catch(RecordStoreException rsExc) {
	         //log.debug("Record store writing ALL error: "+rsExc.getMessage());
	         return false;
	     } catch(SecurityException secExc) {
	    	 //log.debug("Record store security writing ALL error: "+secExc.getMessage());
	    	return false;
	     }
		
	}
	
	public byte[] readAllProfiles(String userID, String nameRecordStore) {
		//TODO: if phonenumber equals difierent user in the same phone
		byte[]data;
		this.userID = userID;
		//log.debug("UserID: "+ this.userID);
		try {
			////log.debug("Inside readAllProfiles function searching ID: "+userID);
	         //openRecordStore(nameRecordStore);
			 RecordEnumeration re = null;
			 if (allRecordStore.getNumRecords() == 0){
				 //log.debug("Inside readAllProfiles num records = 0");
				 this.currentMenu = -1;
				 return null;
			 }
			 else if (UIGooseRecordStore.currentMenu == SourceFiles.SELECT_PROFILE_MENU) {
				 Image uiCustomImage = null;
				 byte[] dataProfiles = null;
					try {
						uiCustomImage = Image.createImage(SourceFiles.SOURCE_BUDDY);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						
					}
				 re = allRecordStore.enumerateRecords((RecordFilter)null, (RecordComparator)null, false);
				 if (re.numRecords() == 0) {
					 //log.debug("Inside readAllProfiles num records = 0");
					 return null;
				 }
				 //log.debug("UIGoose. After reading record: "+re.numRecords());
				 int index;
				 int indexEnd;
				 
				 String auxString;
				 String name;
				 String surname;
				 while (re.hasNextElement()){
					 
					 dataProfiles = re.nextRecord();
					 auxString = new String(dataProfiles);
					 index = auxString.indexOf("//name:")+"//name:".length();
					 indexEnd = auxString.indexOf(";//surname:");
					 name = auxString.substring(index, indexEnd).toLowerCase();
					 index = auxString.indexOf("//surname:")+"//surname:".length();
					 indexEnd = auxString.indexOf(";//phonenumber:");
					 surname = auxString.substring(index, indexEnd).toLowerCase();
					 UIGooseSelectProfile.list.append(name +" "+surname, uiCustomImage);
				 }
				 this.currentMenu = -1;
				 return dataProfiles;
			 }
			
			
	         re = allRecordStore.enumerateRecords(this, (RecordComparator)null, false);
			 //re = allRecordStore.enumerateRecords((RecordFilter)null, (RecordComparator)null, false);
	         if (re.numRecords() == 0) {
	        	 this.currentMenu = -1;
	        	 return null;
	         } 
	         //log.debug("UIGoose. num users found: "+ re.numRecords());
	         data = re.nextRecord();
	         //log.debug("num bytes readed: "+ data.length);
	         //int numBytes= recordStore.getRecord(recordStore.getNextRecordID(), data, 0);
	        ////log.debug("num bytes readed: "+ numBytes);
	         //closeRecordStore(nameRecordStore);
	         return data;
	     } catch(RecordStoreException rsExc) {
	         //log.debug("Record store reading ALL error: "+rsExc.getMessage());
	         return null;
	     } catch(SecurityException secExc) {
	    	 //log.debug("Record store security reading ALL error: "+secExc.getMessage());
	    	return null;
	     }
		
	}
	
	public void setCurrentMenu(int num) {
		currentMenu = num;
	}
	
	public byte[] getSelectedProfile(int numSelected, String paramSearch) {
		try {
			 RecordEnumeration re = null;
			 re = allRecordStore.enumerateRecords(null,(RecordComparator)null, false);
			int index ;
			int indexEnd;
			String auxString;
			byte [] data;
			 while (re.hasNextElement()) {
				 data = re.nextRecord();
				auxString = new String (data);
				index = 0;
				indexEnd = auxString.indexOf(";//phonenumber:");
				//log.debug("element: "+auxString.substring(index, indexEnd));
				if (paramSearch.equalsIgnoreCase(auxString.substring(index, indexEnd))) {
					return data;
				}
			}
			return null;
		} catch (RecordStoreNotOpenException e) {
			// TODO Auto-generated catch block
			//log.debug("Error: "+e.getMessage());
			return null;
		} catch (InvalidRecordIDException e) {
			// TODO Auto-generated catch block
			//log.debug("Error: "+e.getMessage());
			return null;
		} catch (RecordStoreException e) {
			// TODO Auto-generated catch block
			//log.debug("Error: "+e.getMessage());
			return null;
		}
	}
	
	public void deleteProfiles() {
		try {
			 RecordEnumeration re = null;
			 if (allRecordStore.getNumRecords() == 0){
				 return ;
			 }
	         re = allRecordStore.enumerateRecords((RecordFilter)null, (RecordComparator)null, false);
	         while(re.hasNextElement()) {
	        	 allRecordStore.deleteRecord(re.nextRecordId());
	         }
	         //for (int i= 1; i <= allRecordStore.getNumRecords(); i++) {
			//	allRecordStore.deleteRecord(i);
			//}
		} catch (RecordStoreNotOpenException e) {
			// TODO Auto-generated catch block
			//log.debug("UIGoose. Error recordnotop when deleting data: "+e.getMessage());
		} catch (InvalidRecordIDException e) {
			// TODO Auto-generated catch block	
			//log.debug("UIGoose. Error invalidrecExcep when deleting data: "+e.getMessage());
		} catch (RecordStoreException e) {
			// TODO Auto-generated catch block
			//log.debug("UIGoose. Error Recexcep when deleting data: "+e.getMessage());
		}
		
	}
	
     private void openRecordStore(String nameRecordStore) throws RecordStoreException, SecurityException {
         if (nameRecordStore == SourceFiles.RECORD_STORE_GOOSE_WALL) {
        	 recordStore = RecordStore.openRecordStore(nameRecordStore, true);
        	 recordStore.setMode(RecordStore.AUTHMODE_PRIVATE, true);
        	 recordStore.addRecordListener(this);
         }
         else if(nameRecordStore == SourceFiles.RECORD_STORE_ALL_PROFILE) {
        	 allRecordStore = RecordStore.openRecordStore(nameRecordStore, true);
        	 allRecordStore.setMode(RecordStore.AUTHMODE_PRIVATE, true);
        	 allRecordStore.addRecordListener(this);
         }
    	 
     }
     
     public void closeAll() {
    	 closeRecordStore(SourceFiles.RECORD_STORE_ALL_PROFILE);
    	 closeRecordStore(SourceFiles.RECORD_STORE_GOOSE_WALL);
     }
  
     /**
      * Closes record store.
      */
     private void closeRecordStore(String nameRecordStore){
         try {
        	 if (nameRecordStore == SourceFiles.RECORD_STORE_GOOSE_WALL) {
        		 recordStore.closeRecordStore();
        	 }/*
        	 else {
        		 allRecordStore.closeRecordStore();
        	 }*/
         } catch (RecordStoreException ex) {
             // Do nothing.
        	 //log.debug("UIGoose. Error when closing recordStore: "+ex.getMessage());
         }
     }



	public void recordAdded(RecordStore store, int recordId) {
		// TODO Auto-generated method stub
		
	}



	public void recordChanged(RecordStore arg0, int arg1) {
		// TODO Auto-generated method stub
		
	}



	public void recordDeleted(RecordStore rs, int arg1) {
		// TODO Auto-generated method stub
	}


	public boolean matches(byte[] parameter) {
		// TODO Change to btAddress if posible 
		
		String userID = new String(parameter);
		int index;
		int indexEnd;
		index = 0;
		indexEnd = userID.indexOf(";//date:");
		
		String currentuserID = userID.substring(index, indexEnd).toLowerCase();
		if (currentuserID.equalsIgnoreCase(this.userID)){
			//log.debug("UIGoose. recordStore matches: "+userID.substring(index, indexEnd));
			System.out.println("UIGoose. recordStore matches: "+userID.substring(index, indexEnd));
			return true;
		}	
	
		return false;
	}
	
	
}
