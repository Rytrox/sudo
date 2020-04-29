package de.timeout.sudo.groups;

import javax.annotation.Nonnull;

/**
 * Interface to collect data in a datatype
 * @author Timeout
 *
 * @param <T> the type of the data 
 */
public interface Collectable<T> {
		
	/**
	 * Checks if the user is a member of the group
	 * 
	 * @param user the user
	 * @throws IllegalArgumentException if the user is null
	 * @return true if the user is a member otherwise false
	 */
	public boolean isMember(@Nonnull T element);
	
}
