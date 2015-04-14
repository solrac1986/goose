package gui.goose.resources;

import gui.goose.menus.UIGooseManager;

import javax.microedition.lcdui.*;
//
//import com.goose.gui.*;

public class UISplashScreen extends Canvas {

	private final int HEIGHT_MIN = 300;
	private final int WIDTH_MIN = 200;
	
	private String SOURCE_SPLASH = "/mainIcon.png";
	
	
	private int width;
	private int height;
	
	/*
	private TextField textFieldName ;
	private TextField textFieldSurname;
	private TextField textFieldPhone ;
	*/
	
	public UISplashScreen() {
		super();
		
	}

	public void createClass(UIGooseManager manager) {
		
		
		//display = Display.getDisplay(manager);
		
		if (getWidth() < WIDTH_MIN || getHeight() < HEIGHT_MIN) {
			manager.screenSize = false;
		}
		else {
			manager.screenSize = true;
		}
		manager.width = getWidth();
		manager.height = getHeight();
	}
	
	

	
	protected void paint(Graphics g) {
		// TODO Auto-generated method stub
		//Login user before start menu 
		//g.drawString(Integer.toString(UIGooseManager.width), 0, 0, 0);
		Image image = null;
		try {
			image = Image.createImage(SOURCE_SPLASH);
		}catch (Exception e) {
			e.printStackTrace();
		}
		if (image != null) {
			g.drawImage( image, (getWidth() - image.getWidth())/2, 
                    (getHeight() - image.getHeight())/2, 
                    Graphics.TOP|Graphics.LEFT);
		}
		
		g.drawString("Goose", width/2, height/3, Graphics.BOTTOM | Graphics.HCENTER);
		g.setColor(255, 0, 0);
		g.setClip(20, 20, 50, 50);
	}

	protected void keyPressed(int keyCode) {
		
	}
}
