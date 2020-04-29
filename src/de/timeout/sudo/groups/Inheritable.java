package de.timeout.sudo.groups;

import java.util.Collection;

import javax.annotation.Nonnull;

public interface Inheritable<T> {

	/**
	 * Returns a list of extended group of this group. <br>
	 * Is empty of the group has no inheritance
	 * 
	 * @return the super group or null
	 */
	@Nonnull
	public Collection<T> getExtendedGroups();
	
	/**
	 * Extends this group from another (Syntax "this extends other")
	 * @author Timeout
	 * 
	 * @param other the other group
	 * @throws IllegalArgumentException if the other group is null
	 */
	public void extend(@Nonnull T other);
}
