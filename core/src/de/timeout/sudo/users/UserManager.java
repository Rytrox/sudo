package de.timeout.sudo.users;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang.Validate;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class UserManager {

	protected final Map<UUID, User> profiles = new HashMap<>();
	protected final RemoteUser root;
	
	public UserManager(@NotNull RemoteUser root) {
		Validate.notNull(root, "Root cannot be null");
		
		this.root = root;
	}
	
	/**
	 * Returns all loaded user profiles
	 * @author Timeout
	 * 
	 * @return a list containing all loaded user profiles
	 */
	@NotNull
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
	 * Unloads a user in cache.
	 * @author Timeout
	 * 
	 * @throws IllegalStateException if the user is still online
	 * @throws IllegalArgumentException if the user is null
	 * @param user the user you want to remove. Cannot be null
	 */
	public abstract void unloadUser(@NotNull User user);
	
	public RemoteUser getRoot() {
		return root;
	}
}
