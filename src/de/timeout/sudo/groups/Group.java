package de.timeout.sudo.groups;

import java.util.Collection;

import javax.annotation.Nonnull;

import com.google.gson.JsonObject;

/**
 * Represents a group which handles permissions 
 * @author Timeout
 *
 */
public interface Group extends PermissibleBase, Collectable<User> {
	
	/**
	 * Returns the name of this instance
	 * @return the name of this instance
	 */
	@Nonnull
	public String getName();
	
	/**
	 * Returns a list of extended group of this group. <br>
	 * Is empty of the group has no inheritance
	 * 
	 * @return the super group or null
	 */
	@Nonnull
	public Collection<Group> getExtendedGroups();
	
	/**
	 * Extends this group from another (Syntax "this extends other")
	 * @author Timeout
	 * 
	 * @param other the other group
	 * @throws IllegalArgumentException if the other group is null
	 */
	public void extend(@Nonnull Group other);
	
	/**
	 * Checks if this group is a default group
	 * @return if this group is a default group
	 */
	public boolean isDefault();
	
	/**
	 * Compiles the group into Json-Objects
	 * @author Timeout
	 * 
	 * @return the group as JsonObject
	 */
	@Nonnull
	public JsonObject toJson();
}
