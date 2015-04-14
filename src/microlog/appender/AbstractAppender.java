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

import microlog.Appender;
import microlog.Formatter;
import microlog.Level;
import microlog.format.SimpleFormatter;
import microlog.util.PropertiesGetter;

/**
 * This is the abstract super class of all the appenders.
 * 
 * @author Johan Karlsson (johan.karlsson@jayway.se)
 * @since 0.1
 */
public abstract class AbstractAppender implements Appender {

	/**
	 * This is the default formatter used by all subclasses. The subclass can
	 * change this as suitable.
	 */
	protected Formatter formatter = new SimpleFormatter();

	/**
	 * The logOpen shows whether the log is open or not. The implementing
	 * subclass is responsible for setting the correct value.
	 */
	protected boolean logOpen;

	/**
	 * Set the <code>Formatter</code> object that is used for formatting the
	 * output.
	 * 
	 * @see net.sf.microlog.Appender#setFormatter(net.sf.microlog.Formatter)
	 * @throws IllegalArgumentException
	 *             if the <code>formatter</code> is <code>null</code>.
	 */
	public final void setFormatter(Formatter formatter)
			throws IllegalArgumentException {
		if (formatter == null) {
			throw new IllegalArgumentException(
					"The formatter must not be null.");
		}

		this.formatter = formatter;

	}

	/**
	 * Get the <code>Formatter</code> object that is used for formatting the
	 * output.
	 * 
	 * @see net.sf.microlog.Appender#getFormatter()
	 */
	public final Formatter getFormatter() {
		return formatter;
	}

	/**
	 * Check if the log is open.
	 * 
	 * @return <code>true</code> if the log is open, otherwise
	 *         <code>false</code> is returned.
	 */
	public boolean isLogOpen() {
		return logOpen;
	}

	/**
	 * Do the logging.
	 * @param level
	 *            the level at which the logging shall be done.
	 * @param message
	 *            the message to log.
	 * @param t
	 *            the exception to log.
	 */
	public abstract void doLog(String name, long time, Level level,
			Object message, Throwable t);

	/**
	 * Clear the log.
	 * 
	 * @see net.sf.microlog.Appender#clear()
	 */
	public abstract void clear();

	/**
	 * Close the log.
	 * 
	 * @see net.sf.microlog.Appender#close()
	 */
	public abstract void close() throws IOException;

	/**
	 * Open the log.
	 * 
	 * @see net.sf.microlog.Appender#open()
	 */
	public abstract void open() throws IOException;

	/**
	 * Configure the appender.
	 * 
	 * @see net.sf.microlog.Appender#configure(net.sf.microlog.util.PropertiesGetter)
	 */
	public void configure(PropertiesGetter properties) {
		// Ignore the properties
	}

}
