package de.timeout.sudo.bukkit.users;

import java.util.UUID;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

import de.timeout.sudo.bukkit.Sudo;
import de.timeout.sudo.users.User;
import de.timeout.sudo.users.UserManager;

public class BukkitUserManager extends UserManager {
	
	protected static final Sudo main = Sudo.getInstance();
	
	public BukkitUserManager() {
		super(new RootConsole());
	}
	
	/**
	 * Get a user from cache if the user is loaded
	 * @author Timeout
	 * 
	 * @param player the player. Cannot be null
	 * @throws IllegalArgumentException if the player is null
	 * @return the user profile of the player. Returns null if the profile is not loaded yet
	 */
	@Nullable
	public User getUser(@Nonnull OfflinePlayer player) {
		// Validate
		Validate.notNull(player);
		return getUser(player.getUniqueId());
	}


	@Override
	public void unloadUser(User user) {
		// Validate
		Validate.notNull(user, "User cannot be null");
		
		// check if user is not online
		if(Bukkit.getPlayer(user.getUniqueID()) == null) {
			// remove from all groups
			user.getPermissionContainer().getMembers().forEach(user::leaveGroup);
						
			// remove from cache
			profiles.remove(user.getUniqueID());
		} else throw new IllegalStateException("Unable to unload a player who is still online!");
	}
	
	/**
	 * Loads a new User for a certain uuid
	 * @param uuid the uuid of the user. Cannot be null
	 * @throws IllegalArgumentException if the uuid is null
	 */
	public void loadUser(@NotNull UUID uuid) {
		// Validate
		Validate.notNull(uuid, "UUID cannot be null");
		
		// only continue if profile is not loaded yet
		if(!profiles.containsKey(uuid)) {
			// load user
			profiles.put(uuid, new BukkitUser(Bukkit.getOfflinePlayer(uuid)));
		}
	}
}
