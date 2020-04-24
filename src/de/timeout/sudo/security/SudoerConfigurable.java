package de.timeout.sudo.security;

import javax.annotation.Nonnull;

import de.timeout.sudo.groups.User;

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
	
	/**
	 * Adds a new user to the sudoers and creates a new sudoer object
	 * @author Timeout
	 * 
	 * @param user the user you want to upgrade
	 * @param password the password of the new sudo user. Cannot be null
	 * @param executor the executor of the method. cannot be null
	 * @return the new created sudoer. Cannot be null
	 */
	@Nonnull
	public Sudoer addSudoer(@Nonnull User user, @Nonnull String password, @Nonnull Sudoer executor);
	
	/**
	 * Removes a sudoer from being sudo
	 * @author Timeout
	 * 
	 * @param sudoer the sudoer you want to remove
	 * @return if the sudoer could be removed
	 */
	public boolean removeSudoer(@Nonnull Sudoer sudoer, @Nonnull Sudoer executor);
	
}
