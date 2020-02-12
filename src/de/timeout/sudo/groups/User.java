package de.timeout.sudo.groups;

import java.util.UUID;

import javax.annotation.Nonnull;

/**
 * Represents User which can be managed in groups
 * @author Timeout
 *
 */
public interface User extends Comparable<User>, PermissibleBase, Collectable<Group> {

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
