package de.timeout.sudo.permissions;

import javax.annotation.Nullable;

/**
 * Interface for access to users.yml
 * @author Timeout
 *
 * @param <E> the type of the Configuration-Class
 */
public interface UserConfigurable<E> {

	/**
	 * Reloads users.yml into Configuration
	 * @author Timeout
	 *
	 */
	public void reloadUserConfig();
	
	/**
	 * returns the user configuration if {@link UserConfigurable#reloadUserConfig()} invoked before. <br>
	 * Returns null if the configuration is not loaded
	 * @author Timeout
	 * 
	 * @return the user configuration or null
	 */
	@Nullable
	public E getUserConfig();
	
	/**
	 * saves the user configuration into users.yml file
	 * @author Timeout
	 *
	 */
	public void saveUserConfig();
}
