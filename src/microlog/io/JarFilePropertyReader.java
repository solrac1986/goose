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
package microlog.io;

import java.util.Hashtable;
import microlog.MicrologConstants;

/**
 * The <code>JarFilePropertyReader</code> class is used for reading a file
 * from the encapsulating JAR file.
 * 
 * @author Johan Karlsson (johan.karlsson@jayway.se)
 */
public class JarFilePropertyReader {

	private String fileName;

	private static final int DEFAULT_BUFFER_SIZE = 256;
	private final byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
	private final StringBuffer stringBuffer = new StringBuffer(
			2 * DEFAULT_BUFFER_SIZE);

	/**
	 * Create a <code>JarFilePropertyReader</code> with the default filename.
	 */
	public JarFilePropertyReader() {
		fileName = MicrologConstants.DEFAULT_PROPERTY_FILE;
	}

	/**
	 * Create a <code>JarFilePropertyReader</code> with the specified
	 * filename.
	 * 
	 * @param fileName
	 *            the filename to use.
	 */
	public JarFilePropertyReader(String fileName) {
		this.fileName = fileName;
	}

	/**
	 * Get the file name that is used for reading properties.
	 * 
	 * @return the fileName used for reading the properties.
	 */
	public String getFileName() {
		return fileName;
	}

	/**
	 * Set the file that is used for reading the properties.
	 * 
	 * @param fileName
	 *            the file name used for reading the properties.
	 */
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	/**
	 * Insert the values taken from a property source into the Hashtable. Any
	 * previous values with the same key should be overridden/overwritten.
	 * 
	 * @param properties
	 *            the Hashtable in which the properties are stored
	 */
	public void insertProperties(Hashtable properties) {
		// get an InputStream to the property file; is
		String configString = readPropertyfile();

		// parse the string and put keys/values into the properties hashtable
		if (configString != null && configString.length() > 0l) {
			parseConfigString(properties, configString);
		}
	}

	/**
	 * Read the property file and put into the a String.
	 * 
	 * @return a <code>String</code> that contains the content of the file.
	 */
	private String readPropertyfile() {
		
        /*
		InputStream inputStream = null;

		if (fileName != null && fileName.length() > 0) {
            inputStream = PropertyFile.class.getClass().getResourceAsStream(fileName);

            
		} else {
			System.out.println("No file name specified.");
		}

		String configString = null;

		if (inputStream != null) {
			System.out.println("Using property file " + fileName);
			// get a string with the contents of the file; configString
			try {
				int readBytes = inputStream.read(buffer);
				while (readBytes > 0) {
					String string = new String(buffer, 0, readBytes, "UTF-8");
					stringBuffer.append(string);
					readBytes = inputStream.read(buffer);
				}

				if (stringBuffer.length() > 0) {
					configString = stringBuffer.toString();
					// configString is used below
				}
			} catch (IOException e) {
				System.err.println("Failed to read property file " + e);
			} finally {
				try {
					inputStream.close();
				} catch (IOException e) {
					System.err
							.println("Failed to close the property file " + e);
				}
			}

		} else {
			System.out.println("Warning! Property file not found: " + fileName);
		}
		return configString;
         * */
        return null;
	}

	/**
	 * Parse the configuration string that was read from the file.
	 * 
	 * @param properties
	 *            the properties <code>Hashtable</code> to put the properties
	 *            into.
	 * @param configString
	 *            the configuration string to parse.
	 */
	private void parseConfigString(Hashtable properties, String configString) {
		int separatorIndex = configString.indexOf('=');
		int currentIndex = 0;
		int newLineIndex = 0;

		while (separatorIndex > -1) {
			String propertyKey = configString.substring(currentIndex,
					separatorIndex++);
			int newLineIndex1 = configString.indexOf("\r\n", separatorIndex);
			int newLineIndex2 = configString.indexOf("\n", separatorIndex);
			if (newLineIndex1 == -1) {
				newLineIndex1 = newLineIndex2;
			}
			if (newLineIndex2 == -1) {
				newLineIndex2 = newLineIndex1;
			}
			newLineIndex = newLineIndex1 < newLineIndex2 ? newLineIndex1
					: newLineIndex2;
			if (newLineIndex != -1) {
				String propertyValue = configString.substring(separatorIndex,
						newLineIndex);
				separatorIndex = newLineIndex + 2;
				currentIndex = separatorIndex;
				// Put the propertyKey and the propertyValue
				// into the properies hashtable
				properties.put(propertyKey, propertyValue);
			}

			separatorIndex = configString.indexOf("=", separatorIndex);
		}
	}
}
