package goose.mainManager;

import java.util.Stack;
import goose.exceptions.*;

public class EventStack{

	private Stack eventStack;
	
	public EventStack(){
		eventStack = new Stack();
	}

	
	public synchronized void storeNewEvent(IncomingMessageEvent event){
		if(eventStack == null){
			eventStack = new Stack();
		}
		eventStack.push(event);
	}
	
	public synchronized IncomingMessageEvent getLastEvent() throws GooseException{
		if(eventStack != null){
			IncomingMessageEvent event = (IncomingMessageEvent) eventStack.pop();
			return event;
		}
		else{
			throw new GooseException("No more events available on the stack");
		}
	}
	
	public synchronized boolean isModified(){
		if(eventStack.empty()){
			return false;
		}
		return true;
	}
}
