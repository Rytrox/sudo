package de.timeout.sudo.groups;

import java.util.Set;

import javax.annotation.Nonnull;

/**
 * Represents an object which handles permissions
 * @author Timeout
 *
 */
public interface PermissibleBase {

	/**
	 * Adds a permission to this group. <br>
	 * Returns false if the permission is null
	 * 
	 * @param permission the permission to add
	 * @return if it succeed
	 */
	public boolean addPermission(String permission);
	
	/**
	 * Remove a permission from this group. <br>
	 * Returns false if the permission is null
	 * 
	 * @param permission the permission to remove
	 * @return if it succeed
	 */
	public boolean removePermission(String permission);
	
	/**
	 * Checks if this group has the permission.
	 * 
	 * @param permission the permission to check
	 * @throws IllegalArgumentException if the permission is null
	 * @return the result
	 */
	public boolean hasPermission(@Nonnull String permission);
	
	/**
	 * Returns a sorted set of all permissions of this group
	 * @return a set containing all permissions of this group
	 */
	@Nonnull
	public Set<String> getPermissions();

}
