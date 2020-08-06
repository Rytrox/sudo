package de.timeout.sudo.permissions;

import org.jetbrains.annotations.NotNull;

/**
 * Interface for objects which holds container
 * @author Timeout
 *
 */
public interface ContainerHolder {
	
	/**
	 * Returns the name of this instance
	 * @return the name of this instance
	 */
	@NotNull
	public String getName();

	/**
	 * Returns the owned permission container
	 * @return this object's own permission container. Cannot be null
	 */
	@NotNull
	public <T extends Container> T getPermissionContainer();
}
