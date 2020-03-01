package de.timeout.sudo.groups;

import java.util.Collection;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Represents a group which handles permissions 
 * @author Timeout
 *
 */
public interface Group extends PermissibleBase, Collectable<User> {
	
	/**
	 * Returns a list of extended group of this group. <br>
	 * Is empty of the group has no inheritance
	 * 
	 * @return the super group or null
	 */
	@Nonnull
	public Collection<Group> getExtendedGroups();
	
	/**
	 * Returns a Set of all permissions of the group
	 * @author Timeout
	 * 
	 * @return a set containing all permissions of the group and his inheritances
	 */
	@Nonnull
	public Set<String> getAllPermissions();
	
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
