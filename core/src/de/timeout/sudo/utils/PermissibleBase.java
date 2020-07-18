package de.timeout.sudo.utils;

import java.util.Set;

import javax.annotation.Nonnull;

/**
 * Represents an object which handles permissions
 * @author Timeout
 *
 */
public interface PermissibleBase {
	
	/**
	 * Checks if this group has the permission.
	 * 
	 * @param permission the permission to check
	 * @throws IllegalArgumentException if the permission is null
	 * @return the result
	 */
	public boolean hasPermission(@Nonnull String permission);
	
	/**
	 * Returns a sorted set of all permissions of this group.
	 * 
	 * @return a set containing all permissions of this group
	 */
	@Nonnull
	public Set<String> getPermissions();

}
