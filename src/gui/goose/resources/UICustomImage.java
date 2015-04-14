package gui.goose.resources;

import javax.microedition.lcdui.*;

public class UICustomImage {

	private ImageItem imageItem;
	
	public UICustomImage(String label, String source) {
		imageItem = new ImageItem("", null, Item.PLAIN, "");
		Image image = null;
		try {
			 image = Image.createImage(source);
		}catch (Exception e) {
			e.printStackTrace();
		}
		if (image != null) {
			imageItem = new ImageItem(label, image, Item.PLAIN, "");
		}
		
	}
	
	public ImageItem getImageItem() {
		return imageItem;
	}
}
