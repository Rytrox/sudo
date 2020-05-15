package de.timeout.sudo.users;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.commons.lang.Validate;

import de.timeout.sudo.permissions.UserConfigHandler;
import de.timeout.sudo.security.SudoerConfigurable;

public abstract class UserManager<C> implements SudoerConfigurable, UserConfigHandler<C> {

	protected final Map<UUID, User> profiles = new HashMap<>();
	protected final Root console;
	
	protected C decodedSudoer;
	
	public UserManager(@Nonnull Root console) {
		// Validate
		Validate.notNull(console, "Console cannot be null");
		this.console = console;
	}
	
	/**
	 * Returns all loaded user profiles
	 * @author Timeout
	 * 
	 * @return a list containing all loaded user profiles
	 */
	@Nonnull
	public List<User> getUsers() {
		return new ArrayList<>(profiles.values());
	}
	
	/**
	 * Returns the user of a certain key. <br>
	 * Can be null if the user is not loaded yet
	 * @author Timeout
	 * 
	 * @param key the key you want to load.
	 * @return the user or null if the user is not loaded yet or the key is null
	 */
	@Nullable
	public User getUser(UUID key) {
		return profiles.get(key);
	}
	
	/**
	 * Upgrades a normal User to super-user
	 * @author Timeout
	 * 
	 * @param superUser the new superuser
	 * @param executor the executor of the command
	 */
	public abstract void upgradeUser(@Nonnull Sudoer superUser, @Nonnull Root executor);
	
	/**
	 * Unloads a user in cache.
	 * @author Timeout
	 * 
	 * @throws IllegalStateException if the user is still online
	 * @throws IllegalArgumentException if the user is null
	 * @param user the user you want to remove. Cannot be null
	 */
	public abstract void unloadUser(@Nonnull User user);
}
