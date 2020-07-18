package de.timeout.sudo.permissions;

import java.io.IOException;
import java.util.UUID;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Interface for handling user configurations
 * @author Timeout
 *
 * @param <T> the class of the configuration
 */
@FunctionalInterface
public interface UserConfigHandler<T> {

	/**
	 * Returns the configuration of a user.
	 * 
	 * @author Timeout
	 * 
	 * @param uuid the uuid of the user
	 * @return the configuration of the user. Returns null if no such user exists
	 * @throws IllegalArgumentException if the uuid is null
	 * @throws IOException if the file can not be accessed
	 */
	@Nullable
	public T getUserConfiguration(@Nonnull UUID uuid) throws IOException;
}
