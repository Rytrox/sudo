package de.timeout.sudo.groups;

import java.util.Collection;

import javax.annotation.Nonnull;

import com.google.gson.JsonObject;

/**
 * Represents a group which handles permissions 
 * @author Timeout
 *
 */
public interface Group extends PermissibleBase, Collectable<User>, Customizable {
	
	/**
	 * Returns a list of extended group of this group. <br>
	 * Is empty of the group has no inheritance
	 * 
	 * @return the super group or null
	 */
	@Nonnull
	public Collection<Group> getExtendedGroups();
	
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
