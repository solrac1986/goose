package gui.goose.resources;


import java.io.IOException;

import gui.goose.menus.UIGooseManager;
import javax.microedition.lcdui.*;




public class UICustomItem extends CustomItem implements Runnable {
	
	
	//private Form form;
	//private  UIGooseManager uiGooseManager;
	//private Graphics graphics;
	private  Image image;
	private int color;
	private int width;
	private int height;
	private String title;
	private Font font;

	private int numCustomItem = 0;

	 private boolean
	    repaint,
	    traversed;

	
	private UIGooseManager uiGooseManager = null;
	
	public UICustomItem (String label, int width, int height, UIGooseManager uiGooseManager) {
		super(null);
		
		this.uiGooseManager = uiGooseManager;
		this.setLayout(Item.BUTTON);
		if ("Delete messages".length() >= width) {
			font = SourceFiles.fontSmall;
		}
		else{
			font = Font.getFont(Font.FACE_SYSTEM, Font.STYLE_BOLD, Font.SIZE_LARGE);
		}
		
		title = label;
		
		color = 0x00000000;
		this.width = width;
		
		this.height = height;
		
		
		
	}
	
	public  void setHeight(int value) {
		this.height = value;
	}
	
	public void setText(String text) {
		this.title = text;
	}
	
	public String getText() {
		return this.title;
	}
	
	public void setBackground (int color) {
		//0x00RRGGBB
		this.color = color;
	}
	
	public void setRepaint()
	{
		repaint();
	}
	
	/**
     * Called by the user to implement an image in the Item
     * 
    */
	
	public void setImage(String source) {
		try {
			this.image = Image.createImage(source);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	public void setImage(String source, int num) {
		try {
			this.image = Image.createImage(source);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		numCustomItem = num;
	}
	
	
	
    public void paint(Graphics g, int width, int height) {
       //
    	//g.setClip(0, 0, this.getMinimumWidth(), this.getMinimumHeight());
    	g.setFont(font);
       
        if ((title.length())*font.getSize() >= width) {
        	font = SourceFiles.fontSmall;
        	g.setFont(font);
        	
        	int index = 0;
        	int line  = 0;
        	String aux;
        	while (index + width/font.getSize()  < title.length()) {
        		
        		aux = title.substring(index, index+width/font.getSize());
       
            	g.drawString(aux, (width-20), 
                		   line, 
                		   Graphics.RIGHT | Graphics.TOP);
        		index += width/font.getSize();
        		line += font.getHeight();
        	}
        	if (index == 0) {
        		g.drawString(title, (width-20), 
             		   line, 
             		   Graphics.RIGHT | Graphics.TOP);
        	}
        	
       }
        else {
        	g.drawString(title, (width-20), 
           		   (0), 
           		   Graphics.RIGHT | Graphics.TOP);
        }
        
    	
    	
        g.setColor(color);
        
        //g.setColor(255, 0, 0);
        g.setClip(g.getClipX(), g.getClipY(), g.getClipWidth(), g.getClipHeight());
        //g.setClip(g.getClipX(), g.getClipY(), image.getWidth(), image.getHeight());
        //g.setClip(0, 0, 0, 0);
        
       if (image != null) {
    	  g.drawImage(image, 0, 0, 0);     
       }
       //g.setClip(0, 0, 0, 0);
       
    }
    /*
    protected void traverseOut(){
    	repaint();
    }
    */
    
    protected boolean traverse(int dir, int viewportWidth, int viewportHeight, int[] visRect_inout)
    {
    	/*
    	switch(dir) {
    	case Canvas.DOWN:
    		UIGooseManager.focus++;
    		break;
    	case Canvas.RIGHT:
    		UIGooseManager.focus++;
    		break;
    	case Canvas.UP:
    		UIGooseManager.focus--;
    		break;
    	case Canvas.LEFT:
    		UIGooseManager.focus--;
    		break;
    	}
    	
    	if (this.numCustomItem == UIGooseManager.focus){
    		}
    		
    	*/
    	repaint = !traversed;
      	traversed = true;
      	repaint();
    
    	
      	//repaint();
    	return false;
      
	

    }
    /*
    public static void repaintScreen(Vector vector) {
    	for (int i = 0; i < vector.size(); i++) {
    		((UICustomItem)vector.elementAt(i)).repaint();
    	}
    }*/
    /*
    protected void traverseOut()
    {
      for(int i=0, size=this.size(); i<size; i++)
        ((CustomItem)items.elementAt(i)).repaint();
    } 
	*/
    
    /*
     * Function that returns true if it found the image, other case return false 
     * and paint only the title
     */
    
  
    /**
     * From CustomItem.
     * Called by the system to retrieve minimum width required for this control.
     */
    protected int getMinContentWidth() {
        //return 150;
    	// Check screen size
    	return width;
    }
 
    /**
     * From CustomItem.
     * Called by the system to retrieve minimum height required for this control.
     */
    protected int getMinContentHeight() {
        //return 100;
    	return image.getHeight();
    }
 
    /**
     * From CustomItem.
     * Called by the system to retrieve preferred width for this control.
     */
    protected int getPrefContentWidth(int arg0) {
       
    	return width;
    }
 
    /**
     * From CustomItem.
     * Called by the system to retrieve preferred height for this control.
     */
    protected int getPrefContentHeight(int arg0) {
        //return 100;
    	if (height == 0) {
    		return height / 5;
    	}
    	return height / 7 ;
    	
    }
    
    /**
     * From CustomItem.
     * Called by the system to redraw canvas.
     * @param graphics used for drawing operations.
     */
    protected void keyPressed(int keyCode) { //throws GooseUIException{
    	
    	//try {
    	repaint();
	    	uiGooseManager.actionKey(keyCode, this);
	    	
	   // }catch(Exception e){
	   // 	System.out.println("Error in gooseManager actionKey" + e.getMessage());
	    //}
	    	
	    
    }

	public void run() {
		// TODO Auto-generated method stub
		repaint();
		Display display = Display.getDisplay(uiGooseManager);
		display.callSerially(this);
		
		
	}
	
	
	
	
	
}


