package de.timeout.sudo.bukkit.listener;

import java.lang.reflect.Field;
import java.util.UUID;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.commons.lang.Validate;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import de.timeout.libs.Reflections;
import de.timeout.sudo.bukkit.Sudo;
import de.timeout.sudo.bukkit.permissions.BukkitUser;
import de.timeout.sudo.groups.User;

public class VanillaPermissionOverrider implements Listener {

	private static final Class<?> crafthumanentityClass = Reflections.getCraftBukkitClass("entity.CraftHumanEntity");
	private static final Field permField = Reflections.getField(crafthumanentityClass, "perm");
	
	private static final Sudo main = Sudo.getInstance();
	
	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		// get Player
		Player player = event.getPlayer();
		// try to load profile
		BukkitUser user = (BukkitUser) main.getGroupManager().getUserFromOperator(player);
		// if user could be loaded
		overridePermissionSystem(player, user);
	}
	
	/**
	 * Searches the right profile from cache. <br>
	 * Returns null if the profile is not loaded yet
	 * @author Timeout
	 * 
	 * @param uuid the uuid of the user
	 * @return the profile or null if it's not loaded yet
	 */
	@Nullable
	private User searchProfile(UUID uuid) {
		// search in users
		for(User user : main.getGroupManager().getUsers()) {
			// return true if uuid is equal
			if(user.getUniqueID().compareTo(uuid) == 0) return user;
		}
		// return null for not found
		return null;
	}
	
	/**
	 * Overrides the profile of the player
	 * @author Timeout
	 * 
	 * @param player the player itself
	 * @param user the user of the player
	 * @throws IllegalArgumentException if any argument is null or the profile is not applicable to the player
	 */
	public static void overridePermissionSystem(@Nonnull Player player, @Nonnull BukkitUser user) {
		// validate
		Validate.notNull(player, "Player cannot be null");
		Validate.notNull(user, "User cannot be null");
		if(player.getUniqueId().compareTo(user.getUniqueID()) == 0) {
			// write profile into player
			Reflections.setField(permField, player, user);
		} else throw new IllegalArgumentException("User profile is not for this player");
	}
}
