package de.timeout.sudo.groups;

import java.util.UUID;

import javax.annotation.Nonnull;

/**
 * Represents User which can be managed in groups
 * @author Timeout
 *
 */
public interface User extends Comparable<User>, PermissibleBase {

	/**
	 * Returns the highest group of this object
	 * @return the highest group
	 */
	@Nonnull
	public Group getGroup();
	
	/**
	 * Join a group. <br>
	 * 
	 * @param group the group
	 * @throws IllegalArgumentException if the group is null
	 * @return if the user is joined successfully
	 */
	public boolean join(@Nonnull Group group);
	
	/**
	 * Kick user from Group
	 * @return if the user is kicked successfully
	 */
	public boolean kick();
	
	/**
	 * Checks if the user is sudoer and can use sudo
	 * @return if the user is sudoer
	 */
	public boolean isSudoer();
	
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
}
