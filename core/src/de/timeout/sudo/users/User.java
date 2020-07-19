package de.timeout.sudo.users;

import java.util.UUID;

import javax.annotation.Nonnull;

import org.jetbrains.annotations.NotNull;

import com.google.gson.JsonObject;

import de.timeout.sudo.groups.Group;
import de.timeout.sudo.utils.Collectable;
import de.timeout.sudo.utils.Customizable;
import de.timeout.sudo.utils.PermissibleBase;

/**
 * Represents User which can be managed in groups
 * @author Timeout
 *
 */
public interface User extends Comparable<User>, PermissibleBase, Collectable<Group>, Customizable {
	
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
	 * Returns the unique id of the player
	 * @return the unique id of the player
	 */
	@Nonnull
	public UUID getUniqueID();
	
	/**
	 * Converts this object into a JSON-Object
	 * @return a JSON-Object of this object
	 */
	@NotNull
	public JsonObject toJson();
}
