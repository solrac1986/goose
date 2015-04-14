package goose.networkManager;

import goose.exceptions.*;
import java.util.Vector;

public interface IConnectivity {
	/*
	 * Starts the listener necessary to receive and handle incoming connections
	 */
	public boolean startListener () throws GooseNetworkException;
	
	/*
	 * Stops the listener necessary to handle incoming connections
	 */
	public void stopListener();
	
	/*
	 * Pauses the listener necessary to handle incoming connections
	 */
	public void pauseListener();
	
	/*
	 * Restarts the listener necessary to handle incoming connections
	 */
	public void restartListener();
	
	/*
	 * Discovers nearby devices
	 */
	public Vector discoverDevices() throws GooseNetworkException;
	
	/*
	 * Returns the local address for this connectivity. Bluetooth MAC or phone number
	 */
//	public String getLocalAddress() throws GooseNetworkException;
	
	
	/*
	 * Sends the message after being serialized to a particular destination.
	 */
//	public void send(String messageSerialized, String destination);

}
