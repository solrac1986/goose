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
package microlog.appender;

import java.io.IOException;
import java.util.Vector;

import microlog.Level;
import microlog.util.PropertiesGetter;

/**
 * An appender that writes the log entries to a memory buffer. It can be 
 * configured to hold a maximum number of entries, which by default is set to
 * 20 and it can run in two different modes: either it stops logging when the
 * buffer limit is reached or the oldest log entry is overwritten. By default
 * it overwrites old log entries.
 * 
 * The appender supports two different configuration options:
 * <table border="1">
 * 	<tr>
 * 	 <th>Configuration key</th>
 *   <th>Values</th>
 *   <th>Default value</th>
 *  </tr>
 *  <tr>
 *   <td>microlog.appender.MemoryBufferAppender.cyclicBuffer</td>
 *   <td>true or false</td>
 *   <td>true</td>
 *  </tr>
 *  <tr>
 *   <td>microlog.appender.MemoryBufferAppender.maxLogEntries</td>
 *   <td>Max number of entries</td>
 *   <td>20</td>
 *  </tr>  
 * </table> 
 * 
 * @author Henrik Larne (henriklarne@users.sourceforge.net)
 * @since 1.1.0
 */
public class MemoryBufferAppender extends AbstractAppender {

	/**
	 * The property to set to control the appender behavior, cyclic or stop when
	 * the buffer is full
	 */
	public static final String MEMORY_BUFFER_CYCLIC_BUFFER_STRING = "microlog.appender.MemoryBufferAppender.cyclicBuffer";
	/**
	 * The property to set to configure the maximum number of log entries
	 */
	public static final String MEMORY_BUFFER_MAX_ENTRIES_STRING = "microlog.appender.MemoryBufferAppender.maxLogEntries";
	/**
	 * The default maximum number of log entries
	 */
	private static final int DEFAULT_MAX_NBR_OF_ENTRIES = 20;
	/**
	 * The buffer that holds all the log entries as Strings
	 */
	private Vector buffer;
	/**
	 * The maximum number of log entries of the appender
	 */
	private int maxNbrOfEntries;
	/**
	 * Is the buffer cyclic or does it stop logging when it gets full
	 */
	private boolean cyclicBuffer;

	/**
	 * Create a <code>MemoryBufferAppender</code> with the default settings
	 */
	public MemoryBufferAppender() {
		this(DEFAULT_MAX_NBR_OF_ENTRIES, true);
	}

	/**
	 * Create a customized <code>MemoryBufferAppender</code>
	 * 
	 * @param maxNbrOfEntries
	 *            the maximum number of entries to hold in the buffer
	 * @param cyclicBuffer
	 *            should the buffer be cyclic or stop logging when it gets full
	 */
	public MemoryBufferAppender(int maxNbrOfEntries, boolean cyclicBuffer) {
		if (maxNbrOfEntries <= 0)	{
			maxNbrOfEntries = DEFAULT_MAX_NBR_OF_ENTRIES;
		}
		this.maxNbrOfEntries = maxNbrOfEntries;
		this.cyclicBuffer = cyclicBuffer;
		buffer = new Vector();
	}

	/**
	 * Get the maximum number of entries that this appender is configured for
	 * 
	 * @return the maximu number of entries the appender can hold
	 */
	public int getMaxNbrOfEntries()
	{
		return maxNbrOfEntries;
	}

	/**
	 * Is the buffer cyclic or fixed
	 * 
	 * @return true, if the buffer is cyclic, false otherwise
	 */
	public boolean isCyclicBuffer()
	{
		return cyclicBuffer;
	}

	/**
	 * Retrieve the log buffer. Note that this is the actual buffer being used
	 * for logging, thus do not alter it.
	 * 
	 * @return the log buffer containing the log entries as Strings
	 */
	public Vector getLogBuffer() {
		return buffer;
	}

	/**
	 * Do the logging.
	 * 
	 * @param level
	 *            the level at which the logging shall be done.
	 * @param message
	 *            the message to log.
	 * @param t
	 *            the exception to log.
	 */
	public void doLog(String name, long time, Level level, Object message, Throwable t) {
		if (logOpen && formatter != null) {
			// Check if the buffer is full
			if (buffer.size() >= maxNbrOfEntries) {
				// The buffer is full, should we replace the oldest log entry or 
				// disregard the log entry 
				if (cyclicBuffer) {
					buffer.removeElementAt(0);
					buffer.addElement(formatter.format(name, time, level, message, t));
				}
			}
			else {
				// The buffer is not full, simply add the log entry
				buffer.addElement(formatter.format(name, time, level, message, t));
			}
		} else if (formatter == null) {
			System.err.println("Please set a formatter.");
		}
	}

	/**
	 * Clear the buffer of all log entries
	 * 
	 * @see net.sf.microlog.appender.AbstractAppender#clear()
	 */
	public void clear() {
		if (buffer != null) {
			buffer.removeAllElements();
		}
	}

	/**
	 * @see net.sf.microlog.appender.AbstractAppender#close()
	 */
	public void close() throws IOException {
		logOpen = false;
	}

	/**
	 * @see net.sf.microlog.appender.AbstractAppender#open()
	 */
	public void open() throws IOException {
		logOpen = true;
	}

	/**
	 * Get the current size of the log
	 * 
	 * @return the size of the log
	 */
	public long getLogSize() {
		return buffer.size();
	}
	
	/**
	 * Configure the MemoryBufferAppender from a PropertiesGetter, usually done
	 * with a properties file.
	 * <p>
	 * The type of buffer (cyclic or fixed) can be set with the parameter 
	 * <code>microlog.appender.MemoryBufferAppender.cyclicBuffer</code>
	 * </p>
	 * <p>
	 * The maximum number of log entries in this appender can be set with the 
	 * parameter <code>microlog.appender.MemoryBufferAppender.maxLogEntries</code>
	 * </p>
	 * 
	 * @param properties
	 *            Properties to configure with
	 */
	public void configure(PropertiesGetter properties) {
		// Check cyclic buffer configuration
		String cyclicBufferString = properties.getString(MEMORY_BUFFER_CYCLIC_BUFFER_STRING);
		if (cyclicBufferString != null) {
            this.cyclicBuffer = "true".equals(cyclicBufferString.toLowerCase());
		}
		// Check buffer size configuration
		String maxNbrOfEntriesString = properties.getString(MEMORY_BUFFER_MAX_ENTRIES_STRING);
		if (maxNbrOfEntriesString != null) {
			int maxNbrOfEntries = Integer.parseInt(maxNbrOfEntriesString);
			if (maxNbrOfEntries > 0) {
				this.maxNbrOfEntries = maxNbrOfEntries;
			}
		}
	}
}
