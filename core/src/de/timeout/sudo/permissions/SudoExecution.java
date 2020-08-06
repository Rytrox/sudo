package de.timeout.sudo.permissions;

import de.timeout.sudo.users.AuthorizableUser;

import org.apache.commons.lang.Validate;
import org.jetbrains.annotations.NotNull;

/**
 * Class which will be instantiate when a player uses /sudo <command> and is not authorized
 * 
 * @author Timeout
 *
 */
public abstract class SudoExecution<P> {

	protected final int maxAttempts;
	
	protected AuthorizableUser user;
	protected String command;
	protected int attempts;
	
	public SudoExecution(@NotNull AuthorizableUser user, @NotNull String command, int maxAttempts) {
		// Validate
		Validate.notNull(user, "User cannot be null");
		Validate.notEmpty(command, "Command can neither be null nor empty");
		
		this.user = user;
		this.command = command;
		this.maxAttempts = maxAttempts;
	}

	/**
	 * Returns the user of this execution
	 * @return the user
	 */
	public AuthorizableUser getUser() {
		return user;
	}

	/**
	 * returns the executed command
	 * @return the command
	 */
	public String getCommand() {
		return command;
	}

	/**
	 * returns an integer how often the executor entered a password
	 * @return the attempts
	 */
	public int getAttempts() {
		return attempts;
	}
	
	/**
	 * Returns the player of the user
	 * @return the player object of the user. cannot be null
	 */
	@NotNull
	public abstract P getPlayer();
	
	/**
	 * Tries am authorization of the user with an entered password 
	 * @param password the password of the attempt
	 * @return true if the user could be authorized. False if the user takes to many attempts or the password is wrong
	 */
	public boolean authorize(String password) {
		// increase attempts if attempts should be counted. Return false if clause is true
		if(maxAttempts > 0 && ++attempts > maxAttempts) {
			return false;
		}
		
		// try to authorize
		return this.user.authorize(password);
	}
	
	/**
	 * Checks if the execution is interrupted due to too many password attempts
	 * @return true if the user took too many attempts. False otherwise
	 */
	public boolean isMaxReached() {
		return attempts > maxAttempts;
	}
	
	/**
	 * Checks if the player is authorized
	 * @return true if the player is authorized. False otherwise
	 */
	public boolean isAuthorized() {
		return user.isAuthorized();
	}
}
