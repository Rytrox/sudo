package de.timeout.sudo.security;

import org.jetbrains.annotations.Nullable;

/**
 * Interface for objects which manages passwords and needs authorization
 * @author Timeout
 *
 */
public interface Authorizable {

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
}
