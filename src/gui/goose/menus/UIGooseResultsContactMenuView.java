package gui.goose.menus;

import java.io.IOException;
import java.util.Vector;


import gui.goose.resources.*;
import goose.contactsManager.Contact;
import javax.microedition.lcdui.*;


public class UIGooseResultsContactMenuView extends Form implements UIGooseView{

	private final int VALUE_TOP = 10;
	
	private List vList = null;
	private Display display;
	
	public Vector results = null;
	
	
	public UIGooseResultsContactMenuView (int viewIdentifier, UIGooseManager uiGooseManager, Vector contacts) {
		super(null);
		
		display = uiGooseManager.display;
		vList= new List("Results search", Choice.IMPLICIT);
		results = new Vector();
		switch(viewIdentifier){
		case SourceFiles.CONTACT_SEARCH_RESULTS_MENU:
			results = contacts;
			fillSearchResults(uiGooseManager);
			break;
		case SourceFiles.CONTACT_TOP_RESULTS:
			results =  uiGooseManager.gooseManager.getTopContacts(VALUE_TOP);
			fillResults(uiGooseManager);
			break;
		case SourceFiles.CONTACT_LAST:
//			TODO: Function that gives last used contact
			results  = uiGooseManager.gooseManager.getLastContactsUsed(VALUE_TOP);
			fillResults(uiGooseManager);
			break;
		}
		
		vList.addCommand(SourceFiles.backCommand);
		//vList.addCommand(SourceFiles.logCommand);
		vList.setCommandListener(uiGooseManager);
		
	}
	
	
	public void destroy() {
		// TODO Auto-generated method stub
		vList = null;
		this.deleteAll();
	}

	public void show() {
		// TODO Auto-generated method stub
		display.setCurrent(vList);
	}
	
	public  Contact getSelectedContact(int index) {
		return (Contact)results.elementAt(index);
	}
	
	private void fillSearchResults(UIGooseManager uiGooseManager) {
		
		Image uiCustomImage = null;
		try {
			uiCustomImage = Image.createImage(SourceFiles.SOURCE_BUDDY);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			
		}
		
		String name;
		String surname;
	
		for(int i = 0; i < results.size(); i++) {
			if (results.elementAt(i)==null){
				continue;
			}
			
			name = (((Contact)(results.elementAt(i))).getName());
			surname = (((Contact)(results.elementAt(i))).getSurname());
			
			vList.append(name + " " + surname, uiCustomImage);
		}
		
		
	}
	
	private void fillResults(UIGooseManager uiGooseManager) {
		
		
		//System.out.println("filltopResults");
		
		Image uiCustomImage = null;
		try {
			uiCustomImage = Image.createImage(SourceFiles.SOURCE_BUDDY);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			
		}
		String name;
		String surname;
		String score;
		String stringDisplay ="";
		for(int i = 0; i < results.size(); i++) {
			if (results.elementAt(i)==null){
				continue;
			}
			
			name = (((Contact)(results.elementAt(i))).getName());
			surname = (((Contact)(results.elementAt(i))).getSurname());
			if (uiGooseManager.viewIdentifier == SourceFiles.CONTACT_TOP_MENU) {
				score = Integer.toString((((Contact)(results.elementAt(i))).getScore()));
				stringDisplay = score;
			}
			vList.append(stringDisplay + "     \t"+ name + " " + surname, uiCustomImage);
		}
		
	}
	
	

}
