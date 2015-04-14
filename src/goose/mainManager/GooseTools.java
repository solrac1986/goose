package goose.mainManager;

import java.util.Date;
import java.util.Random;

public class GooseTools {

	public static long getTime(){
		Date now = new Date();
		return now.getTime();
	}
	
	public static long getRandomLong(){
		Random random = new Random();
		return random.nextLong();
	}
	
	public static int getRandomInt(){
		Random random = new Random();
		return random.nextInt();
	}
	
	public static long getPositiveRandomLong(){
		Random random = new Random();
		long next = random.nextLong();
    	if(next <0){
    		return next*(-1);    		
    	}
    	else{
    		return next;
    	}
	}
	
	public static int getPositiveRandomInt(){
		Random random = new Random();
		int next = random.nextInt();
    	if(next <0){
    		return next*(-1);    		
    	}
    	else{
    		return next;
    	}
	}
}
