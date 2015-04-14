/*
 * Copyright 2008 The Microlog project @sourceforge.net
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package microlog.bluetooth;

import java.io.DataOutputStream;
import java.io.IOException;

import javax.bluetooth.BluetoothStateException;
import javax.bluetooth.DiscoveryAgent;
import javax.bluetooth.LocalDevice;
import javax.bluetooth.ServiceRecord;
import javax.bluetooth.UUID;
import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;

import microlog.Appender;
import microlog.Level;
import microlog.MicrologConstants;
import microlog.appender.AbstractAppender;
import microlog.util.PropertiesGetter;

/**
 * The <code>BluetoothSerialAppender</code> log using a Bluetooth serial
 * connection (btspp). It can be used in two different modes: try to locate the
 * Bluetooth logger server through regular Bluetooth lookup or by specifying
 * the exact url of the server. The latter version is useful for devices that
 * fails to lookup the server, for instance SonyEricsson P990i. If configured
 * using a property file the server url can be set using the property 
 * <code>microlog.appender.BluetoothSerialAppender.serverUrl</code>
 * 
 * @author Johan Karlsson (johan.karlsson@jayway.se)
 * @since 0.6
 */
public class BluetoothSerialAppender extends AbstractAppender {

	public static final String SERVER_URL_STRING = "microlog.appender.BluetoothSerialAppender.serverUrl";
	
	private String serverUrl;
	private StreamConnection connection;
	private DataOutputStream dataOutputStream;

	/**
	 * Default constructor, used when the device manages to locate the logging
	 * server on its own. The recommended way of using it.
	 */
	 public BluetoothSerialAppender() {
	}
	
	/**
	 * Constructor that lets you specify the Bluetooth logger server instead of
	 * using the lookup service. This is useful if you have a device that fails
	 * to locate the Bluetooth logger server. Experienced with SonyEricsson 
	 * P990i.
	 *
	 * @param serverUrl The server to connect to
	 */
	public BluetoothSerialAppender(String serverUrl) {
		this.serverUrl = serverUrl;
	}
	
	/**
	 * Clear the log. This has not affect for this appender.
	 */
	public void clear() {
		// Do nothing
	}

	/**
	 * Close the log.
	 */
	public synchronized void close() throws IOException {

		if (dataOutputStream != null) {
			try {
				dataOutputStream
						.writeUTF(MicrologConstants.STOP_LOGGING_COMMAND_STRING);
				dataOutputStream.flush();
				dataOutputStream.close();
			} catch (IOException e) {
				System.err
						.println("Failed to terminate the dataOutputStream in a controlled way."
								+ e);
			}
		}

		if (connection != null) {
			try {
				connection.close();
			} catch (IOException e) {
				System.err.println("Failed to close the log " + e);
			}
		}

		logOpen = false;
	}

	public synchronized void doLog(String name, long time, Level level,
			Object message, Throwable t) {
		if (logOpen && formatter != null) {
			try {
				dataOutputStream.writeUTF(formatter.format("", time,
						level, message, t));
				dataOutputStream.flush();
			} catch (IOException e) {
				System.err.println("Unable to log to the output stream. " + e);
			}
		}
	}

	/**
	 * Open the log, i.e. open the Bluetooth connection to the log server.
	 */
	public synchronized void open() throws IOException {

		try {
			String connectionString = null;
			if (serverUrl == null) {
				// Try to find a logger server using Bluetooth discovery
				// Retrieve the connection string to connect to
				// the server
				LocalDevice local = LocalDevice.getLocalDevice();
				DiscoveryAgent agent = local.getDiscoveryAgent();

				connectionString = agent.selectService(new UUID(
					MicrologConstants.DEFAULT_BT_UUID_STRING, false),
					ServiceRecord.NOAUTHENTICATE_NOENCRYPT, false);
			}
			else {
				connectionString = serverUrl;
			}

			if (connectionString != null) {
				try {
					connection = (StreamConnection) Connector
							.open(connectionString);
					dataOutputStream = connection.openDataOutputStream();

					logOpen = true;
				} catch (IOException e) {
					System.err
							.println("Failed to connect to the Bluetooth log server with connection string "
									+ connectionString + ' ' + e);
				}
			} else {
				System.err.println("Did not find any Microlog service.");
			}
		} catch (BluetoothStateException e) {
			System.err
					.println("Failed to connect to the Bluetooth log server. "
							+ e);
		}
	}

	/**
	 * Get the size of the. Always returns <code>Appender.SIZE_UNDEFINED</code>.
	 */
	public long getLogSize() {
		return Appender.SIZE_UNDEFINED;
	}
	
	/**
	 * Configure the BluetoothSerialAppender.
	 * <p>
	 * If the device for some reason fails to locate a bluetooth logger server
	 * the connection url can set hard using the property
	 * <code>microlog.appender.BluetoothSerialAppender.serverUrl</code>.
	 * 
	 * @param properties
	 *            Properties to configure with
	 */
	public synchronized void configure(PropertiesGetter properties) {

		// Set the record store name from Properties
		String serverUrlString = properties.getString(SERVER_URL_STRING);
		if (serverUrlString != null && serverUrlString.length() > 0) {
			serverUrl = serverUrlString;
		}
	}
}
