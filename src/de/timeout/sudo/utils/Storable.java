package de.timeout.sudo.utils;

import java.io.IOException;

/**
 * Interface for saving and loading from data like files
 * @author Timeout
 *
 */
public interface Storable {

	/**
	 * Load the object from data
	 * @author Timeout
	 *
	 * @throws IOException if there was an unexpected IO-Error while reading data
	 */
	public void load() throws IOException;
	
	/**
	 * Save the object in data
	 * @author Timeout
	 *
	 * @throws IOException if there was an unexpected IO-Error while writing data
	 */
	public void save() throws IOException;
	
}
