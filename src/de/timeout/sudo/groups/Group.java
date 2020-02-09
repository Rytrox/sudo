package de.timeout.sudo.groups;

import java.util.Collection;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Represents a group which handles permissions 
 * @author Timeout
 *
 */
public interface Group {
	
	/**
	 * Returns the super group of this group. <br>
	 * Can be null if the group has no Super-Group
	 * 
	 * @return the super group or null
	 */
	@Nullable
	public Group getSuperGroup();

	/**
	 * Adds a user to this group. <br>
	 * Returns false if the user is null
	 * 
	 * @param user the user
	 * @return true if the remove succeed else false
	 */
	public boolean join(User user);
	
	/**
	 * Removes a user from this group <br>
	 * Returns false if the user is null
	 * 
	 * @param user the user
	 * @return true if the remove succeed else false
	 */
	public boolean kick(User user);
	
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
	 * Checks if the user is a member of the group
	 * 
	 * @param user the user
	 * @throws IllegalArgumentException if the user is null
	 * @return true if the user is a member otherwise false
	 */
	public boolean isMember(@Nonnull User user);
	
	/**
	 * Returns a collection of all permissions of this group
	 * @return a collection containing all permissions of this group
	 */
	@Nonnull
	public Collection<String> getPermissions();
	
	/**
	 * Returns the name of this group
	 * @return the name of this group
	 */
	@Nonnull
	public String getName();
	
	/**
	 * Returns the prefix of this group. Can be null
	 * @return the prefix or null
	 */
	@Nullable
	public String getPrefix();
	
	/**
	 * Returns the suffix of this group. Can be null
	 * @return the suffix or null
	 */
	@Nullable
	public String getSuffix();
	
	/**
	 * Checks if this group is a default group
	 * @return if this group is a default group
	 */
	public boolean isDefault();
}
