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
package microlog;

import java.io.IOException;

import microlog.util.PropertiesGetter;

/**
 * The interface that all Appender classes must implement.
 * 
 * @author Johan Karlsson (johan.karlsson@jayway.se)
 */
public interface Appender {

	/**
	 * Size returned if log size cannot be determined.
	 */
	int SIZE_UNDEFINED = -1;

	/**
	 * Do the logging.
	 * 
	 * @param name
	 *            the name of the logger.
	 * @param time
	 *            the time since the first logging has done (in milliseconds).
	 * @param level
	 *            the logging level
	 * @param message
	 *            the message to log.
	 * @param t
	 *            the exception to log.
	 */
	void doLog(String name, long time, Level level, Object message, Throwable t);

	/**
	 * Clear the log.
	 */
	void clear();

	/**
	 * Close the log. The consequence is that the logging is disabled until the
	 * log is opened. The logging could be enabled by calling
	 * <code>open()</code>.
	 * 
	 * @throws IOException
	 *             if the close failed.
	 */
	void close() throws IOException;

	/**
	 * Open the log. The consequence is that the logging is enabled.
	 * 
	 * @throws IOException
	 *             if the open failed.
	 * 
	 */
	void open() throws IOException;

	/**
	 * Check if the log is open.
	 * 
	 * @return true if the log is open, false otherwise.
	 */
	boolean isLogOpen();

	/**
	 * Get the size of the log. This may not be applicable to all types of
	 * appenders.
	 * 
	 * @return the size of the log.
	 */
	long getLogSize();

	/**
	 * Set the formatter to use.
	 * 
	 * @param formatter
	 *            The formatter to set.
	 */
	void setFormatter(Formatter formatter);

	/**
	 * Get the formatter that is in use.
	 * 
	 * @return Returns the formatter.
	 */
	Formatter getFormatter();

	/**
	 * Configure the appender.
	 * 
	 * @param properties
	 *            Properties to configure with
	 */
	void configure(PropertiesGetter properties);

}