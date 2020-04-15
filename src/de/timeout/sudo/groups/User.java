package de.timeout.sudo.groups;

import java.util.Collection;
import java.util.UUID;

import javax.annotation.Nonnull;

import com.google.gson.JsonObject;

/**
 * Represents User which can be managed in groups
 * @author Timeout
 *
 */
public interface User extends Comparable<User>, PermissibleBase, Collectable<Group>, Customizable {
	
	/**
	 * Checks if the user is currently online
	 * @return true if the user is online otherwise false
	 */
	public boolean isOnline();
	
	/**
	 * Returns a collection with all groups of the user
	 * @author Timeout
	 * 
	 * @return a collection containing all groups of the user
	 */
	@Nonnull
	public Collection<Group> getGroups();
	
	/**
	 * Returns the unique id of the player
	 * @return the unique id of the player
	 */
	@Nonnull
	public UUID getUniqueID();
	
	/**
	 * Converts the user into a Json-String
	 * @author Timeout
	 * 
	 * @return the user as JsonObject
	 */
	@Nonnull
	public JsonObject toJson();
}
