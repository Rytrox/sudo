package de.timeout.sudo.security;

/**
 * Interface for managing Sudoer Configuration
 * @author Timeout
 *
 */
public interface SudoerConfigurable {

	/**
	 * reloads the sudoer configuration
	 * @author Timeout
	 *
	 */
	public void reloadSudoerConfig();
	
	/**
	 * Saves the current sudoer config
	 * @author Timeout
	 *
	 */
	public void saveSudoerConfig();
	
}
