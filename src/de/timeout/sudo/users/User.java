package de.timeout.sudo.users;

import java.util.Collection;
import java.util.UUID;

import javax.annotation.Nonnull;

import com.google.gson.JsonObject;

import de.timeout.sudo.groups.Group;
import de.timeout.sudo.utils.Collectable;
import de.timeout.sudo.utils.PermissibleBase;

/**
 * Represents User which can be managed in groups
 * @author Timeout
 *
 */
public interface User extends Comparable<User>, PermissibleBase, Collectable<Group> {
	
	/**
	 * Returns the name of this instance
	 * @return the name of this instance
	 */
	@Nonnull
	public String getName();
	
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
