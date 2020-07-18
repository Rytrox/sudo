package de.timeout.sudo.utils;

import java.util.Collection;

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
	
	/**
	 * Returns a collection with all groups of the user
	 * @author Timeout
	 * 
	 * @return a collection containing all groups of the user
	 */
	@Nonnull
	public Collection<T> getMembers();
	
}
