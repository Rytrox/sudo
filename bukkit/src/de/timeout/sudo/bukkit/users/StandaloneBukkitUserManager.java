package de.timeout.sudo.bukkit.users;

import java.io.IOException;
import java.util.UUID;
import java.util.logging.Level;

import de.timeout.libs.config.JsonConfig;
import de.timeout.sudo.bukkit.Sudo;
import de.timeout.sudo.permissions.UserConfigHandler;
import de.timeout.sudo.users.AuthorizableUser;
import de.timeout.sudo.users.User;

import org.apache.commons.lang.Validate;
import org.jetbrains.annotations.NotNull;

/**
 * Implementation of the Standalone-UserManager of Sudo
 * @author Timeout
 *
 */
public final class StandaloneBukkitUserManager extends BukkitUserManager implements UserConfigHandler<JsonConfig> {
	
	
	public StandaloneBukkitUserManager() {
		super();
	}
	
	public void loadUserFromFile(@NotNull UUID uuid) {
		
	}
	
	/**
	 * Unloads a user and saves its data in a file
	 * @author Timeout
	 * 
	 * @param player the player you want to unload
	 * @throws IllegalStateException if the server runs in bungeecord mode
	 * @throws IllegalArgumentException if the player is null or online
	 */
	@Override
	public void unloadUser(User user) {
		// Validate
		Validate.notNull(user, "User cannot be null");
		
		// throws illegal state exception if bukkit mode is disabled
		if(!user.isOnline()) {
			// unload user from profiles
			profiles.remove(user.getUniqueID());
			
			// save in file
			try {
				user.save();
			} catch (IOException e) {
				Sudo.log().log(Level.WARNING, String.format("&cUnable to write data file of player %s", user.getName()), e);
			}
		} else throw new IllegalStateException("Users cannot be saved in files while bungeecord is enabled or player is online.");
	}
	
	public void changePassword(AuthorizableUser user, String password) throws IOException {
		
	}

	@Override
	public JsonConfig getUserConfiguration(UUID uuid) throws IOException {
		return null;
	}
}
