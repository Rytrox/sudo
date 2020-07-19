package de.timeout.sudo.utils;

import java.util.Collection;

import org.jetbrains.annotations.NotNull;

import de.timeout.sudo.users.Root;

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
	public boolean isMember(@NotNull T element);
	
	/**
	 * Returns a collection with all groups of the user
	 * @author Timeout
	 * 
	 * @return a collection containing all groups of the user
	 */
	@NotNull
	public Collection<T> getMembers();
	
	/**
	 * Adds a data object to this collectable object
	 * @param element the element you want to add. Cannot be null
	 * @param executor the executor of this operation. Cannot be null
	 * @return if the object is added and was not added before
	 * @throws IllegalArgumentException if any argument is null
	 */
	public boolean add(@NotNull T element, @NotNull Root executor);
	
	/**
	 * Removes a data object from this collectable object
	 * @param element the element you want to remove. Cannot be null
	 * @param executor the executor of this operation. Cannot be null
	 * @return if the object is removed and was containing this object before
	 * @throws IllegalArgumentException if any argument is null
	 */
	public boolean remove(@NotNull T element, @NotNull Root executor);
}
