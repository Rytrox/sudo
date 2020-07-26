package de.timeout.sudo.users;

import java.util.UUID;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.google.gson.JsonObject;

import de.timeout.sudo.groups.UserGroup;
import de.timeout.sudo.permissions.ChangeablePermissionHolder;
import de.timeout.sudo.permissions.UserContainer;
import de.timeout.sudo.utils.Customizable;
import de.timeout.sudo.utils.Storable;

/**
 * Represents User which can be managed in groups
 * @author Timeout
 *
 */
public interface User extends Comparable<User>, ChangeablePermissionHolder<User>, Customizable, Storable {
		
	/**
	 * Checks if the user is currently online
	 * @return true if the user is online otherwise false
	 */
	public boolean isOnline();
	
	/**
	 * Returns the unique id of the player
	 * @return the unique id of the player
	 */
	@NonNull
	public UUID getUniqueID();
	
	/**
	 * Returns the encoded password of this user
	 * @return the encoded password of this user
	 */
	@Nullable
	public String getEncodedPassword();
	
	/**
	 * tries to authorize the user with the password
	 * @param password the password
	 * @return whether the authorization worked or not
	 */
	public boolean authorize(@Nullable String password);
	
	/**
	 * Checks if the user has entered the password once successfully since he is on the server
	 * @return true if he has entered the correct password. False otherwise
	 */
	public boolean isAuthorized();
	
	/**
	 * Converts this object into a JSON-Object
	 * @return a JSON-Object of this object
	 */
	@NotNull
	public JsonObject toJson();
	
	/**
	 * Joins this object to a group
	 * @param group the group you want this object to join. Cannot be null
	 * @return true if this object is added to the group due to this operation. False if the user was already a member of the group
	 * @throws IllegalArgumentException if the group is null
	 */
	public boolean joinGroup(@NotNull UserGroup group);
	
	/**
	 * Checks if this object is a member of a certain group
	 * @param group the group you want to check. Cannot be null
	 * @return true if this object is a member of this group
	 */
	public boolean isMember(@NotNull UserGroup group);
	
	/**
	 * Remove this object from a group
	 * @param group the group from which this object should exit. Cannot be null
	 * @return true if this object was a member before. False otherwise
	 */
	public boolean leaveGroup(@NotNull UserGroup group);
	
	/**
	 * Checks if this user is a sudoer and can use the /sudo command
	 * @return true if this object is a sudoer. False otherwise
	 */
	public boolean isSudoer();
	
	/**
	 * Checks if this user is currently root
	 * @return true if this user is logged in as root. False otherwise
	 */
	public boolean isRoot();
	
	/**
	 * Override to UserContainer. Users cannot have other instances
	 */
	@Override
	public UserContainer getPermissionContainer();
}
