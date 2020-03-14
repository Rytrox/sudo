package de.timeout.sudo.groups;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Interface for managing Prefix, Suffix and Custom names
 * @author Timeout
 *
 */
public interface Customizable {

	/**
	 * Returns the name of this group
	 * @return the name of this group
	 */
	@Nonnull
	public String getName();
	
	/**
	 * Returns the prefix of this group. Can be null
	 * @return the prefix or null
	 */
	@Nullable
	public String getPrefix();
	
	/**
	 * Returns the suffix of this group. Can be null
	 * @return the suffix or null
	 */
	@Nullable
	public String getSuffix();
}
