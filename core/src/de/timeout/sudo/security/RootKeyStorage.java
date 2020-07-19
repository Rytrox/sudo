package de.timeout.sudo.security;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.Validate;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import de.timeout.sudo.users.RemoteRoot;
import de.timeout.sudo.users.Root;
import de.timeout.sudo.users.Sudoer;
import de.timeout.sudo.users.User;

/**
 * A Storage for Sudoers and Root-Keys
 * @author Timeout
 *
 */
public abstract class RootKeyStorage {

	protected final Map<String, Root> activeKeys = new HashMap<>();
	
	protected final RemoteRoot console;
	
	/**
	 * Creates a new Storage for local Root-Keys
	 * @param console the user of the console 
	 */
	public RootKeyStorage(@NotNull RemoteRoot console) {
		Validate.notNull(console, "Console cannot be null");

		this.console = console;
		activeKeys.put(console.getRootKey(), console);
	}
	
	/**
	 * Upgrades a user to a super-user and returns the 
	 * 
	 * @param user
	 * @param password
	 * @param executor
	 * @return
	 */
	@NotNull
	public abstract Sudoer upgradeUser(@NotNull User user, @NotNull String password, @NotNull Root executor) throws IOException;
	
	@Nullable
	public Root getRootUser(String key) {
		// return null if no root exists
		return activeKeys.get(key);
	}
	
	@NotNull
	public RemoteRoot getConsoleUser() {
		return console;
	}
	
	/**
	 * Creates an random security-key
	 * @return a random string with 20 characters length
	 */
	protected String createSecurityKey() {
		// Just to be sure that no key already exists
		String securityKey;
		do {
			securityKey = RandomStringUtils.random(20);
		} while(activeKeys.containsKey(securityKey));
		
		return securityKey;
	}
}
