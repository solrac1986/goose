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
import java.io.InterruptedIOException;

import javax.wireless.messaging.MessageConnection;
import javax.wireless.messaging.TextMessage;

import microlog.util.PropertiesGetter;

/**
 * Log messages into a buffer and send it as an SMS (TextMessage) when
 * triggered.
 * 
 * The sending could be triggered manually or when a message is logged at a
 * certain level, typically when a ERROR or FATAL message has been logged. It is
 * possible to set the trigger level by changing the property
 * <code>triggerLevel</code>.
 * 
 * The size of the buffer is set by the property <code>bufferSize</code>.
 * 
 * Note: this requires an implementation of JSR-120 on the target platform.
 * 
 * @author Johan Karlsson (johan.karlsson@jayway.se)
 * @since 0.6
 */
public class SMSBufferAppender extends AbstractMessageAppender {

	public final static String MESSAGE_RECEIVER_PROPERTY = "microlog.appender.SMSBufferAppender.messageReceiver";

	private String messageReceiver;

	/**
	 * The default constructor for <code>SMSBufferAppender</code>.
	 */
	public SMSBufferAppender() {
		lineSeparator = new String(new char[] { GSM_7_BIT_LF });
	}

	/**
	 * @see net.sf.microlog.appender.AbstractMessageAppender#open()
	 */
	public synchronized void open() throws IOException {
		String connectionString = "sms://" + messageReceiver;
		openConnection(connectionString);
		logOpen = true;
	}

	/**
	 * Send the current log.
	 * 
	 */
	synchronized void sendLog(String messageContent) {
		if (messageReceiver != null) {
			TextMessage message = (TextMessage) messageConnection
					.newMessage(MessageConnection.TEXT_MESSAGE);

			message.setPayloadText(messageContent);

			try {
				messageConnection.send(message);
			} catch (InterruptedIOException e) {
				System.err.println("Interrupted while sendinf the log " + e);
			} catch (IOException e) {
				System.err.println("Failed to send the log " + e);
			}

		} else {
			System.err.println("A message receiver is not set.");
		}
	}

	/**
	 * Get the message receiver. This should be a valid telephone number.
	 * 
	 * @return the messageReceiver
	 */
	public synchronized String getMessageReceiver() {
		return messageReceiver;
	}

	/**
	 * Set the message receiver. This should be a valid telephone number.
	 * 
	 * @param messageReceiver
	 *            the messageReceiver to set.
	 * @throws IllegalArgumentException
	 *             if the <code>messageReceiver</code> is null.
	 */
	public synchronized void setMessageReceiver(String messageReceiver)
			throws IllegalArgumentException {
		if (messageReceiver == null) {
			throw new IllegalArgumentException(
					"The messageReceiver must not be null");
		}

		this.messageReceiver = messageReceiver;
	}

	/**
	 * Configure the <code>SMSBufferAppender</code> with the supplied
	 * properties object.
	 */
	public synchronized void configure(PropertiesGetter properties) {
		if (properties != null) {
			String newMessageReceiver = properties
					.getString(SMSBufferAppender.MESSAGE_RECEIVER_PROPERTY);
			if (newMessageReceiver != null) {
				this.messageReceiver = newMessageReceiver;
			}
		}
	}

}
