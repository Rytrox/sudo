package de.timeout.sudo.groups.exception;

import java.io.IOException;

import de.timeout.sudo.groups.Group;

/**
 * Exception which will be thrown if a circular inheritance between groups is detected
 * @author Timeout
 *
 */
public class CircularInheritanceException extends IOException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2751679361737981022L;

	public CircularInheritanceException(Group error) {
		super("Detected circular inheritance in " + error.getName());
	}

}
