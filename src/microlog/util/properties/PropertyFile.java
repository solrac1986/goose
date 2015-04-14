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
package microlog.util.properties;

import java.util.Hashtable;

import microlog.io.JarFilePropertyReader;

/**
 * A class to handle the properties in a property file (a textfile) that is
 * included in the JAR file.
 * 
 * @author Johan Karlsson (johan.karlsson@jayway.se)
 * @author Darius Katz
 */
public class PropertyFile implements PropertySource {

	private JarFilePropertyReader fileReader;

	/**
	 * Creates a new instance of PropertyFile
	 */
	public PropertyFile(String fileName) {
		fileReader = new JarFilePropertyReader();
		fileReader.setFileName(fileName);
	}

	/**
	 * Get the file name that is used for reading properties.
	 * 
	 * @return the fileName used for reading the properties.
	 */
	public String getFileName() {
		return fileReader.getFileName();
	}

	/**
	 * Set the file that is used for reading the properties.
	 * 
	 * @param fileName
	 *            the file name used for reading the properties.
	 */
	public void setFileName(String fileName) {
		fileReader.setFileName(fileName);
	}

	/**
	 * Insert the values taken from a property source into the Hashtable. Any
	 * previous values with the same key should be overridden/overwritten.
	 * 
	 * @param properties
	 *            the Hashtable in which the properties are stored
	 */
	public void insertProperties(Hashtable properties) {
		fileReader.insertProperties(properties);
	}
}
