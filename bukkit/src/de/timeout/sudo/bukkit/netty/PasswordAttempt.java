package de.timeout.sudo.bukkit.netty;

import javax.annotation.Nonnull;

import org.apache.commons.lang.Validate;

import de.timeout.sudo.bukkit.Sudo;

public class PasswordAttempt {

	private static final Sudo main = Sudo.getInstance();
	
	private final int maxAttempts = main.getConfig().getInt("sudo.maxAttempts");
	
	private String command;
	private int counts;
	
	public PasswordAttempt(@Nonnull String command) {
		Validate.notEmpty(command, "Command cannot be empty");
		this.command = command;
	}
	
	/**
	 * increases the amount of wrong attempts 
	 * @author Timeout
	 *
	 */
	public void addWrongAttempt() {
		counts++;
	}
	
	/**
	 * Returns if the maxed attempts is reached
	 * @author Timeout
	 * 
	 * @return if the maxed attempts is reached
	 */
	public boolean maxReached() {
		return counts >= maxAttempts;
	}
	
	/**
	 * Returns the command
	 * @author Timeout
	 * 
	 * @return the command of the execution
	 */
	@Nonnull
	public String getCommand() {
		return command;
	}
}
