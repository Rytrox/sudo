package de.timeout.sudo.commands;

/**
 * Represents a parameter in a Linux-Command
 * @author Timeout
 *
 */
@FunctionalInterface
public interface Parameter {

	/**
	 * Executes the parameter subfunction
	 * @author Timeout
	 * 
	 * @param arguments all arguments which are required if there are required
	 */
	public void execute(Object... arguments);
}
