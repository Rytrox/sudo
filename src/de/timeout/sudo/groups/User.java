package de.timeout.sudo.groups;

import java.util.Collection;

import javax.annotation.Nonnull;

/**
 * Represents User which can be managed in groups
 * @author Timeout
 *
 */
public interface User {

	/**
	 * Returns the highest group of this object
	 * @return the highest group
	 */
	@Nonnull
	public Group getGroup();
	
	/**
	 * Join a group. <br>
	 * 
	 * @param group the group
	 * @throws IllegalArgumentException if the group is null
	 * @return if the user is joined successfully
	 */
	public boolean join(@Nonnull Group group);
	
	/**
	 * Kick user from Group
	 * @return if the user is kicked successfully
	 */
	public boolean kick();
	
	/**
	 * Add permission to the user
	 * @param permission the new permission
	 * @return if it succeed
	 */
	public boolean addPermission(String permission);
	
	/**
	 * Removes permission from the user
	 * @param permission the permission to remove
	 * @return if it succeed
	 */
	public boolean removePermission(String permission);
	
	/**
	 * Returns a collection with all permissions of the user including his group permission
	 * @return a set of all permissions
	 */
	public Collection<String> getPermissions();
	
	/**
	 * Check if user has permission
	 * @param permission the permission
	 * @return check if user has permission
	 */
	public boolean hasPermission(String permission);
	
	/**
	 * Checks if the user is sudoer and can use sudo
	 * @return if the user is sudoer
	 */
	public boolean isSudoer();
	
	/**
	 * Checks if the user is currently online
	 * @return true if the user is online otherwise false
	 */
	public boolean isOnline();
}
