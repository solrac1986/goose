package goose.ui;

import java.io.*;
import javax.microedition.midlet.*;
import javax.microedition.lcdui.*;
import javax.microedition.media.*;
import javax.microedition.media.control.*;

public class RecordForm extends Form implements CommandListener{
	
	  private StringItem messageItem;
	    private StringItem errorItem;
	    private final Command recordCommand, playCommand, stopCommand, stopPlayingCommand;
	    private Player p;
	    private byte[] recordedSoundArray = null;
	    private ByteArrayOutputStream output;
	    private RecordControl rc;
	    private MIDlet m;
	    
	    
	    
	    public RecordForm(){  
	        super("Record Audio");   
	        messageItem = new StringItem("Record", "Click record to start recording.");
	        this.append(messageItem);
	        errorItem = new StringItem("", "");
	        this.append(errorItem);        
	        recordCommand = new Command("Record", Command.SCREEN, 1);
	        this.addCommand(recordCommand);
	        stopCommand = new Command("Stop Recording", Command.SCREEN, 2);
	        this.addCommand(stopCommand);
	        playCommand = new Command("Play", Command.SCREEN, 3);
	        this.addCommand(playCommand);       
	        stopPlayingCommand = new Command("Stop Playing", Command.SCREEN, 4);
	        this.addCommand(stopPlayingCommand);
//	        exitCommand = new Command("Exit", Command.SCREEN, 5);
//	        this.addCommand(exitCommand);
	        StringBuffer inhalt = new StringBuffer();
	        this.setCommandListener(this);
	    }
	    
	    public void setMidlet(MIDlet m){
	    	this.m = m;
	    }
	    
	    public void commandAction(Command comm, Displayable disp){
	        if(comm==recordCommand){
	            try{                
	            	Display.getDisplay(m).vibrate(500);
	                p = Manager.createPlayer("capture://audio?encoding=pcm");
	                p.realize();                
	                rc = (RecordControl)p.getControl("RecordControl");
	                output = new ByteArrayOutputStream();
	                rc.setRecordStream(output);                
	                rc.startRecord();
	                p.start();
	                messageItem.setText("recording...");
	            } catch (IOException ioe) {
	                errorItem.setLabel("Error");
	                errorItem.setText(ioe.toString());
	            } catch (MediaException me) {
	                errorItem.setLabel("Error");
	                errorItem.setText(me.toString());
	            } catch (Exception ie) {
	                errorItem.setLabel("Error");
	                errorItem.setText(ie.toString());
	            }
	        } else if(comm == playCommand) {
	            try {

	            	Display.getDisplay(m).vibrate(500);
	                ByteArrayInputStream recordedInputStream = new ByteArrayInputStream
	                      (recordedSoundArray);
	                p = Manager.createPlayer(recordedInputStream,"audio/basic");
	                p.prefetch();
	                p.start();
	            }  catch (IOException ioe) {
	                errorItem.setLabel("Error");
	                errorItem.setText(ioe.toString());
	            } catch (MediaException me) {
	                errorItem.setLabel("Error");
	                errorItem.setText(me.toString());
	            }
	        }
	        else if(comm == stopCommand){
	        	try{
	            	Display.getDisplay(m).vibrate(500);
	                Thread.sleep(5000);
	                messageItem.setText("done!");
	                rc.commit();               
	                recordedSoundArray = output.toByteArray();                
	                p.close();
	        	}
	        	catch (Exception e){
	        		errorItem.setLabel("error stoping");
	        		errorItem.setText(e.toString());
	        	}
	        }
	        else if(comm == stopPlayingCommand){
	        	try{
	            	Display.getDisplay(m).vibrate(500);
	                messageItem.setText("Stop playing!");
	                p.stop();
	                p.close();
	        	}
	        	catch (Exception e){
	        		errorItem.setLabel("error stoping playback");
	        		errorItem.setText(e.toString());
	        	}
	        }
	    }
	}
