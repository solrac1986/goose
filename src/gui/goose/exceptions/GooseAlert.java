package gui.goose.exceptions;

import javax.microedition.lcdui.*;


public class GooseAlert  {

	private static final String label = "INFO";
	private static final Image image = null;
	private static Alert alert;
	public static Display display;
	public static AlertType altertType;
	
	public static Alert createAlert (String string) {
		alert = new Alert (label, string, image, AlertType.INFO); 
		alert.setTimeout(Alert.FOREVER);
		return alert;
	}

	public static Alert createAlert (String string, AlertType alertType) {
		alert = new Alert (label, string, image, AlertType.INFO); 
		alert.setType(alertType);
		alert.setTimeout(Alert.FOREVER);
		return alert;
	}
	
}
