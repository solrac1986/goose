


import microlog.Logger;
import microlog.appender.FormAppender;
import microlog.appender.ConsoleAppender;
import goose.unitaryTests.*;
import javax.microedition.midlet.*;
import javax.microedition.lcdui.*;
import goose.mainManager.*;
import goose.forwardingManager.MessageTools;
 
public class GooseMidletTest extends MIDlet implements CommandListener {
    
    public GooseMidletTest() {
    }
    // Display
    private Display display;
    // Main form
    private Form form;
    // For the message
    private StringItem stringItem;
    // For the exit command
    private Command exitCommand;
 
    private Logger log = Logger.getLogger();
    
//    private ContactsManagerUT cmut;
   

    private GooseUnitaryTest gut = null;
    
    
    public void commandAction(Command command, Displayable displayable) {
        if (displayable == form) {
            if (command == exitCommand) {
                exitMIDlet();
            }
        }
    }
        
    public void startApp() {
        // Create form
        stringItem = new StringItem("GooseTest", "BluetoothUT");
        form = new Form(null, new Item[] {stringItem});
        exitCommand = new Command("Exit", Command.EXIT, 1);
        form.addCommand(exitCommand);
        form.setCommandListener(this);
        
//        RecordForm form = new RecordForm();
//        form.setMidlet(this);
//        Display.getDisplay(this).setCurrent(form);
        
        
        // Get display for drawning
        display = Display.getDisplay(this);        
        display.setCurrent(form);
        ConsoleAppender ca = new ConsoleAppender();
        FormAppender fa = new FormAppender();
        ca.setConsole(System.out);
        fa.setLogForm(form);
//        log.addAppender(fa);
        log.addAppender(ca);
        
        //Gose Unitary Test:
        gut = new GooseUnitaryTest();
        gut.startTest();
        

        //SMS Unitary test
//        SMSUT sms = new SMSUT();
//        sms.startSMSUT();
        
//        log.debug("FIRST DEBUG MESSAGE");
        
        //CONTACT MANAGER UT
//        ContactsManagerUT cmut = new ContactsManagerUT();
//        cmut.startTest();
        
        //BLUETOOTH MANAGER UT
//        btut = new BluetoothManagerUT();
//        btut.startUnitaryTest();
        
        //EVENT MANAGER UT
//        EventManagerUT emUT = new EventManagerUT();
//        emUT.startTest();
        
        //FORWARDING MANAGER DATABASE UT
//        ForwardingManagerUT fmUT = new ForwardingManagerUT();
//        fmUT.startTest();

        //HASHTABLE UT
//        HashTableUT htUT = new HashTableUT();
//        htUT.startTest();
        
        //INSERTION ARRAY TEST
//        InsertionArrayTest.test();
       
    }
    
    
    /*
    private void audioTest(){
    	sleep(10000);
    	log.debug("Start debugging");
        byte [] recordedAudio = null;
        AudioRecorder ar = new AudioRecorder();
        ar.startRecording();
        sleep(15000);
        for (int i=0; i<5; i++){
	        Display.getDisplay(this).vibrate(500);
	        sleep(500);
        }
        try
        {
        	recordedAudio = ar.stop();
        	log.debug("Total data recorded: "+recordedAudio.length);
        }
        catch(Exception e){
        	log.debug("Error stoping recording");
        }
        
        try{
        	AudioPlayer ap = new AudioPlayer(recordedAudio);
        	ap.start();
        }
        catch(Exception e){
        	log.debug("Audio Player: Error with audio input");
        }
    }
    */
    private void sleep(long mseconds){
    	try{
    		Thread.sleep(mseconds);
    	}
    	catch(Exception e){
    		log.debug("MIDlet Exception when sending thread to sleep");
    	}
    }
    
    
    // Your MIDlet should not call pauseApp(), only system will call this life-cycle method
    public void pauseApp() {
    }
 
    // Your MIDlet should not call destroyApp(), only system will call this life-cycle method    
    public void destroyApp(boolean unconditional) {
    	gut.stopTest();
    	log.debug("Destroy App");
    }
 
    public void exitMIDlet() {
//    	cmut.finishTest();
//    	btut.finishUnitaryTest();
    	log.debug("ExitMIDlet");
    	gut.stopTest();
    	try{
    		this.sleep(2000);
    	}
    	catch(Exception e){

        	log.debug("Closing MIDLET");
    		log.debug(e.getMessage());
    	}
        display.setCurrent(null);
        notifyDestroyed();
    }
    
}