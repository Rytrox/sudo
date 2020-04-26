package de.timeout.sudo.security;

public interface Root {

	/**
	 * Enables root for sudoer
	 * @author Timeout
	 * 
	 * @return if root could be enabled
	 */
	public boolean enableRoot();
	
	/**
	 * Disabled root for sudoer
	 * @author Timeout
	 * 
	 * @return if root could be disabled
	 */
	public boolean disableRoot();
	
	/**
	 * Check if root is enabled
	 * @author Timeout
	 * 
	 * @return true if root is enabled otherwise false
	 */
	public boolean isRoot();
}
