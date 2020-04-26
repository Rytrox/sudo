package de.timeout.sudo.security;

import javax.annotation.Nonnull;

import de.timeout.sudo.groups.User;

/**
 * Interface for Sudoers
 * @author Timeout
 *
 */
public interface Sudoer extends Root, User {

	/**
	 * Method to authorize or deauthorize user
	 * @author Timeout
	 * 
	 * @param authorize if the user should be authorized or not
	 * @throws IllegalArgumentException if the password is null or undefined
	 * @return true if the user could be authorized else false
	 */
	public boolean authorize(@Nonnull String password);
	
	/**
	 * deauthorize a current user
	 * @author Timeout
	 *
	 */
	public void deauthorize();
	
	/**
	 * Check if user has authorized himself with his password
	 * @author Timeout
	 * 
	 * @return if the user is authorized
	 */
	public boolean isAuthorized();
	
	/**
	 * Set the password of the current superuser
	 * @author Timeout
	 * 
	 * @param password the new password of the superuser. Cannot be null
	 * @param executor the executor of the password set. Cannot be null
	 * @throws IllegalArgumentException if any argument is null
	 * @return true if the password could be set else otherwise
	 */
	public boolean setPassword(@Nonnull String password, @Nonnull Sudoer executor);
	
}
