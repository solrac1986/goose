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
package microlog.spot;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

import javax.microedition.midlet.MIDlet;
import javax.microedition.midlet.MIDletStateChangeException;
import javax.microedition.rms.RecordComparator;
import javax.microedition.rms.RecordEnumeration;
import javax.microedition.rms.RecordStore;
import javax.microedition.rms.RecordStoreException;
import javax.microedition.rms.RecordStoreNotFoundException;
import javax.microedition.rms.RecordStoreNotOpenException;

import microlog.appender.RecordStoreAppender;
import microlog.rms.AscendingComparator;
import microlog.util.Properties;

/**
 * A MIDlet that is used for viewing a log created with the RecordStoreAppender.
 * 
 * @author Johan Karlsson (johan.karlsson@jayway.se)
 * @author M Kranz
 */
public class RecordStoreLogExtractor extends MIDlet {

	private final RecordComparator ascComparator = new AscendingComparator();
	private RecordComparator currentComparator = ascComparator;

	private String recordStoreName;

	private RecordStore logRecordStore;

	/**
	 * Start the MIDlet.
	 * 
	 * @throws MIDletStateChangeException
	 *             if the MIDlet fails to change the state.
	 */
	protected void startApp() throws MIDletStateChangeException {

		Properties properties = new Properties(this);

		// Set the record store name from Properties
		String midletName = properties.getString("MIDlet-Name");
		if (midletName != null && midletName.length() > 0
				&& midletName.length() < 30) {
			recordStoreName = midletName + "-ml";
		}

		String recordStoreNameProperty = properties
				.getString(RecordStoreAppender.RECORD_STORE_NAME_STRING);
		if (recordStoreNameProperty != null
				&& recordStoreNameProperty.length() > 0
				&& recordStoreNameProperty.length() < 32) {
			recordStoreName = recordStoreNameProperty;
		}

		if (recordStoreName == null) {
			recordStoreName = RecordStoreAppender.RECORD_STORE_DEFAULT_NAME;
		}

		loadLog();
	}

	/**
	 * Pause the MIDlet.
	 */
	protected void pauseApp() {
	}

	/**
	 * Destroy the MIDlet.
	 * 
	 * @param unconditional
	 *            If true when this method is called, the MIDlet must cleanup
	 *            and release all resources. If false the MIDlet may throw
	 *            <code>MIDletStateChangeException</code> to indicate it does
	 *            not want to be destroyed at this time.
	 * @throws MIDletStateChangeException
	 *             if the MIDlet fails to change the state.
	 */
	protected void destroyApp(boolean unconditional)
			throws MIDletStateChangeException {
	}

	/**
	 * Close the log.
	 */
	private void closeLog() {
		try {
			if (logRecordStore != null) {
				logRecordStore.closeRecordStore();
			}
		} catch (RecordStoreNotOpenException e) {
			System.err.println("RecordStore was not open" + e);
		} catch (RecordStoreException e) {
			System.err.println("Failed to close log " + e);
		}
	}

	/**
	 * Execute the command.
	 * 
	 * @see net.sf.microlog.ui.RecordStoreLogViewer.AbstractCommand#execute()
	 */
	public void loadLog() {
		try {
			logRecordStore = RecordStore.openRecordStore(recordStoreName, true);

			RecordEnumeration recordEnum = logRecordStore.enumerateRecords(
					null, currentComparator, false);
			while (recordEnum.hasNextElement()) {
				byte[] data = recordEnum.nextRecord();

				try {
					ByteArrayInputStream bais = new ByteArrayInputStream(data);
					DataInputStream is = new DataInputStream(bais);

					is.readLong();
					String logString = is.readUTF();

					System.out.println(logString + "\n");

					is.close();
					bais.close();
				} catch (IOException e) {
					System.err.println("Failed to load log " + e);
				}
			}
			recordEnum.destroy();
		} catch (RecordStoreNotFoundException e) {
			System.out.println("Could not find log data in  " + recordStoreName
					+ ' ' + e);
		} catch (RecordStoreException e) {
			System.out.println("Could not open log data. " + e);
		} finally {

			closeLog();
		}

	}
}
