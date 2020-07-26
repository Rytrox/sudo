package de.timeout.sudo.users;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.commons.lang.Validate;
import org.jetbrains.annotations.NotNull;

import de.timeout.sudo.permissions.UserConfigHandler;
import de.timeout.sudo.security.SudoerConfigurable;

public abstract class UserManager<C> implements SudoerConfigurable, UserConfigHandler<C> {

	protected final Map<UUID, User> profiles = new HashMap<>();
	protected final RemoteUser root;
	
	protected C decodedSudoer;
	
	public UserManager(@NotNull RemoteUser root) {
		Validate.notNull(root, "Root cannot be null");
		
		this.root = root;
		reloadSudoerConfig();
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
	 * @throws IOException if an unexpected IO-Error appears
	 */
	public abstract void upgradeUser(@Nonnull User user, @Nonnull String password, @Nonnull User executor) throws IOException;
	
	/**
	 * Unloads a user in cache.
	 * @author Timeout
	 * 
	 * @throws IllegalStateException if the user is still online
	 * @throws IllegalArgumentException if the user is null
	 * @param user the user you want to remove. Cannot be null
	 */
	public abstract void unloadUser(@Nonnull User user);
	
	public RemoteUser getRoot() {
		return root;
	}
}
