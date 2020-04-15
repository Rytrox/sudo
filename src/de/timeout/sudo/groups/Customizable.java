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
	 * Returns the name of this instance
	 * @return the name of this instance
	 */
	@Nonnull
	public String getName();
	
	/**
	 * Returns the prefix of this instance. Can be null
	 * @return the prefix or null
	 */
	@Nullable
	public String getPrefix();
	
	/**
	 * Sets the prefix of the user
	 * @author Timeout
	 * 
	 * @param prefix the new prefix of the user
	 */
	public void setPrefix(String prefix);
	
	/**
	 * Returns the suffix of this instance. Can be null
	 * @return the suffix or null
	 */
	@Nullable
	public String getSuffix();
	
	/**
	 * Sets the suffix of this instance
	 * @author Timeout
	 *
	 * @param suffix the new suffix of the instance
	 */
	public void setSuffix(String suffix);
}
