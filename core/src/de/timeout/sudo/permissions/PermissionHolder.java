package de.timeout.sudo.permissions;

import java.util.Collection;

import org.jetbrains.annotations.NotNull;

/**
 * Represents an object which handles permissions
 * @author Timeout
 *
 */
public interface PermissionHolder extends ContainerHolder {
	
	/**
	 * Checks if this group has the permission.
	 * 
	 * @param permission the permission to check
	 * @throws IllegalArgumentException if the permission is null
	 * @return the result
	 */
	public boolean hasPermission(@NotNull String permission);
	
	/**
	 * Returns a sorted set of all permissions of this group.
	 * 
	 * @return a set containing all permissions of this group
	 */
	@NotNull
	public Collection<String> getPermissions();
	
	/**
	 * Adds a permission to this group and returns a result if the container was modified due to this operation
	 * 
	 * @param permission the permission you want to add
	 * @return a boolean which shows if the permission table was modified by this operation
	 * 
	 * @throws IllegalArgumentException if the permission is null
	 */
	public boolean addPermission(@NotNull String permission);
	
	/**
	 * Removes a permission by the server-console
	 * @param permission the permission you want to remove. Cannot be null
	 * @return a boolean which shows if the permission table was modified due to this operation
	 * @throws IllegalArgumentException if any argument is null
	 */
	public boolean removePermission(@NotNull String permission);
}
