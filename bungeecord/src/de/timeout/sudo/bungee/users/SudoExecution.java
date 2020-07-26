package de.timeout.sudo.bungee.users;

import de.timeout.sudo.bungee.Sudo;
import de.timeout.sudo.users.User;

import net.md_5.bungee.api.connection.ProxiedPlayer;

import org.apache.commons.lang.Validate;
import org.jetbrains.annotations.NotNull;

/**
 * Class which will be instantiate when a player uses /sudo <command> and is not authorized
 * 
 * @author Timeout
 *
 */
public class SudoExecution {

	private static final Sudo main = Sudo.getInstance();
	private static final int MAX_ATTEMPTS = main.getConfig().getInt("sudo.maxAttempts");
	
	private User user;
	private String command;
	private int attempts;
	
	public SudoExecution(@NotNull User user, @NotNull String command) {
		// Validate
		Validate.notNull(user, "User cannot be null");
		Validate.notEmpty(command, "Command can neither be null nor empty");
		
		this.user = user;
		this.command = command;
	}

	/**
	 * Returns the user of this execution
	 * @return the user
	 */
	public User getUser() {
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
	public ProxiedPlayer getPlayer() {
		return main.getProxy().getPlayer(user.getUniqueID());
	}
	
	/**
	 * Tries am authorization of the user with an entered password 
	 * @param password the password of the attempt
	 * @return true if the user could be authorized. False if the user takes to many attempts or the password is wrong
	 */
	public boolean authorize(String password) {
		// increase attempts if attempts should be counted. Return false if clause is true
		if(MAX_ATTEMPTS > 0 && ++attempts > MAX_ATTEMPTS) {
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
		return attempts > MAX_ATTEMPTS;
	}
	
	/**
	 * Checks if the player is authorized
	 * @return true if the player is authorized. False otherwise
	 */
	public boolean isAuthorized() {
		return user.isAuthorized();
	}
}
