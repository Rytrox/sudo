package de.timeout.sudo.utils;

import java.util.Set;

import org.jetbrains.annotations.NotNull;

import de.timeout.sudo.users.Root;

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
	public boolean hasPermission(@NotNull String permission);
	
	/**
	 * Returns a sorted set of all permissions of this group.
	 * 
	 * @return a set containing all permissions of this group
	 */
	@NotNull
	public Set<String> getPermissions();

	/**
	 * Adds a permission by the executor
	 * @param permission the permission you want to add. Cannot be null
	 * @param executor the executor of the modification. Cannot be null
	 * @return a boolean if the permission table was modified by this operation
	 * @throws IllegalArgumentException if any argument is null
	 */
	public boolean addPermission(@NotNull String permission, @NotNull Root executor);
	
	/**
	 * Adds a permission by the server console. It is not recommended to use this method.
	 * 
	 * Please use {@link PermissibleBase#addPermission(String, Root)} instead.
	 * @param permission the permission you want to add
	 * @return a boolean which shows if the permission table was modified by this operation
	 * 
	 * @throws IllegalArgumentException if the permission is null
	 */
	public boolean addPermission(@NotNull String permission);
	
	/**
	 * Removes a permission by the executor
	 * @param permission the permission you want to remove. Cannot be null
	 * @param executor the executor of this operation. Cannot be null
	 * @return a boolean which shows if the permission table was modified due to this operation
	 * @throws IllegalArgumentException if any argument is null or the executor is not a root
	 */
	public boolean removePermission(@NotNull String permission, @NotNull Root executor);
	
	/**
	 * Removes a permission by the server-console
	 * @param permission the permission you want to remove. Cannot be null
	 * @return a boolean which shows if the permission table was modified due to this operation
	 * @throws IllegalArgumentException if any argument is null
	 */
	public boolean removePermission(@NotNull String permission);
}
