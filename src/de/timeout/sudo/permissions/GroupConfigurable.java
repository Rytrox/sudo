package de.timeout.sudo.permissions;

import javax.annotation.Nullable;

/**
 * Interface for managing groups.yml
 * @author Timeout
 *
 * @param <E> the type of Configuration 
 */
public interface GroupConfigurable<E> {

	/**
	 * reload groups.yml from File
	 * @author Timeout
	 *
	 */
	public void reloadGroupConfig();
	
	/**
	 * get the groups configuration. <br>
	 * Returns null if the configuration is not loaded with {@link GroupConfigurable#reloadGroupConfig()} before
	 * @author Timeout
	 * 
	 * @return the group configuration. Can be null
	 */
	@Nullable
	public E getGroupConfig();
	
	/**
	 * Write Configuration into groups.yml file
	 * @author Timeout
	 *
	 */
	public void saveGroupConfig();
}
