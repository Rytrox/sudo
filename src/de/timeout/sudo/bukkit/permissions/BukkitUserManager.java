package de.timeout.sudo.bukkit.permissions;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Optional;
import java.util.UUID;
import java.util.logging.Level;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import com.google.common.io.Files;
import com.google.gson.JsonObject;

import de.timeout.libs.config.JsonConfig;
import de.timeout.sudo.bukkit.Sudo;
import de.timeout.sudo.bukkit.listener.VanillaPermissionOverrider;
import de.timeout.sudo.bukkit.security.BukkitSudoer;
import de.timeout.sudo.users.Root;
import de.timeout.sudo.users.Sudoer;
import de.timeout.sudo.users.User;
import de.timeout.sudo.users.UserManager;

public class BukkitUserManager extends UserManager<JsonConfig> {
	
	private static final Sudo main = Sudo.getInstance();
	
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
	
	/**
	 * Converts the users array from Bungeecord into BukkitUsers
	 * @author Timeout
	 * 
	 * @param data the users array from bungeecord message
	 */
	public void loadUserFromBungeecord(@Nonnull JsonObject data) {
		// validate
		Validate.notNull(data, "Json-Data cannot be null");
		// create user
		BukkitUser user = new BukkitUser(data);
		// get OfflinePlayer
		OfflinePlayer op = Bukkit.getOfflinePlayer(user.getUniqueID());
		// cache user
		profiles.put(op.getUniqueId(), user);
		// overrides profile if user is already online
		if(op.isOnline()) VanillaPermissionOverrider.overridePermissionSystem(op.getPlayer(), user);
	}
	
	/**
	 * Loads a new user from its configuration-file.
	 * Throws an exception if the server runs in bungeecord-mode
	 * 
	 * @author Timeout
	 * 
	 * @param uuid the uuid of the user you want to load
	 * @throws IllegalArgumentException if the uuid is null
	 * @throws IllegalStateException if the server runs in bungeecord-mode
	 */
	public void loadUserFromFile(@Nonnull UUID uuid) {
		// Validate
		Validate.notNull(uuid, "UUID cannot be null");
		// throw illegal state exception if bukkit mode is disabled
		if(!main.bungeecordEnabled()) {
			// get OfflinePlayer
			OfflinePlayer player = Bukkit.getServer().getOfflinePlayer(uuid);
			// get User and load from file
			User user = Optional.ofNullable(profiles.get(player.getUniqueId())).orElse(new BukkitUser(player));
			// apply user to online player
			Player p = player.getPlayer();
			if(p != null) VanillaPermissionOverrider.overridePermissionSystem(p, (BukkitUser) user);
			// cache in database
			profiles.putIfAbsent(player.getUniqueId(), user);
		} else throw new IllegalStateException("Users cannot be loaded from files while bungeecord is enabled!");
	}
	
	/**
	 * Unloads a user and saves its data in a file
	 * @author Timeout
	 * 
	 * @param player the player you want to unload
	 * @throws IllegalStateException if the server runs in bungeecord mode
	 * @throws IllegalArgumentException if the player is null or online
	 */
	public void unloadUserToFile(@Nonnull OfflinePlayer player) {
		// Validate
		Validate.notNull(player, "Player cannot be null");
		// throws illegal state exception if bukkit mode is disabled
		if(!main.bungeecordEnabled() && !player.isOnline()) {
			// unload user from profiles
			BukkitUser user = (BukkitUser) profiles.remove(player.getUniqueId());
			// save in file
			try {
				user.save();
			} catch (IOException e) {
				Sudo.log().log(Level.WARNING, String.format("&cUnable to write data file of player %s", player.getPlayer()), e);
			}
		} else throw new IllegalStateException("Users cannot be saved in files while bungeecord is enabled or player is online.");
	}
	
	@Override
	public void upgradeUser(Sudoer superUser, Root executor) {
		Validate.notNull(superUser, "Superuser cannot be null");
		Validate.notNull(executor, "Executor cannot be null");
		// do nothing if executor is not authorized or superuser is not a bukkitsudoer
		if(executor.isRoot()) {
			// update profile
			this.profiles.replace(superUser.getUniqueID(), superUser);
		}
	}
	
	@Override
	public void reloadSudoerConfig() {
		// do nothing if bungeecord mode is enabled
		if(!main.bungeecordEnabled()) {
			// load sudoer fine
			try {
				decodedSudoer = new JsonConfig(new String(
						Base64.getDecoder().decode(
								String.join("", Files.readLines(new File(main.getDataFolder(), "sudoers.out"), StandardCharsets.UTF_8))
						)
				));
			} catch (IOException e) {
				Sudo.log().log(Level.WARNING, "&cUnable to read sudoers.out", e);
			}
		}
	}

	@Override
	public void saveSudoerConfig() {
		// do nothing if bungeecord mode is enabled
		if(!main.bungeecordEnabled()) {
			// get data
			String data = decodedSudoer.saveToString();
			// create File
			File file = new File(main.getDataFolder(), "sudoers.out");
			
			try {
				// create Files
				Files.createParentDirs(file);
				Files.touch(file);
				
				// encode and write data into file
				Files.write(Base64.getEncoder().encodeToString(data.getBytes(StandardCharsets.UTF_8)), file, StandardCharsets.UTF_8);
			} catch (IOException e) {
				Sudo.log().log(Level.WARNING, "&cUnable to save sudoers.out", e);
			}
		}
	}
	
	/**
	 * Loads a SuperUser from the sudoers file and upgrades it in group system if the sudoer could be loaded
	 * @author Timeout
	 * 
	 * @param user the user you want to load
	 * @throws IllegalArgumentException if the user is null or the server is running BungeeCord-Mode
	 * @return the sudoer of the user or null if the user is null
	 */
	@Nullable
	public Sudoer loadSudoerFromConfiguration(@Nonnull User user) {
		Validate.notNull(user, "User cannot be null");
		Validate.isTrue(!main.bungeecordEnabled(), "File-Support is only enabled while server is running in Bukkit-Mode");
		Validate.isTrue(user instanceof BukkitUser, "User is not an instance of BukkitUser");
		
		// do nothing if the user is already a superuser
		if(!(user instanceof Sudoer)) {
			// load Sudoer
			Sudoer sudoer = BukkitSudoer.loadSudoerFromConfiguration((BukkitUser) user, console);
			// update in group manager if sudoer could be loaded
			if(sudoer != null) upgradeUser(sudoer, console);
			// return sudoer
			return sudoer;
		} else return (Sudoer) user;
	}
	
	/**
	 * Upgrades a User to a Sudoer
	 * @author Timeout
	 * 
	 * @param user the user you want to upgrade. Cannot be null
	 * @param password the password of the user. Cannot be null nor empty
	 * @param executor the executor of the command. Cannot be null
	 * @throws IllegalArgumentException if any argument is null, the password is empty or {@link Root#isRoot()} returns false
	 * @return the Sudoer of the user. Cannot be null
	 */
	@Nonnull
	public Sudoer upgradeUserToSudoer(@Nonnull User user, @Nonnull String password, @Nonnull Root executor) {
		// Validate
		Validate.notNull(user, "User cannot be null");
		Validate.notNull(executor, "Executor cannot be null");
		Validate.notEmpty(password, "Password can neither be null nor empty");
		Validate.isTrue(executor.isRoot(), "Unable to acquire the sudo frontend lock. Are you root?");
		Validate.isTrue(user instanceof BukkitUser, "User must be an instance of BukkitUser");
		
		// create Sudoer
		Sudoer sudoer = BukkitSudoer.upgradeUserToSudoer((BukkitUser) user, password, executor);
		// upgrade in group manager
		upgradeUser(sudoer, executor);
		// return user
		return sudoer;
	}

	@Override
	public JsonConfig getUserConfiguration(UUID uuid) throws IOException {
		return null;
	}

	@Override
	public void unloadUser(User user) {
		// Validate
		Validate.notNull(user, "User cannot be null");
		
		// check if user is not online
		if(Bukkit.getPlayer(user.getUniqueID()) == null) {
			// remove from all groups
			user.getMembers().forEach(group -> group.kick(user));
			
			// remove from sudogroup if user is a sudoer
			if(user instanceof Sudoer) main.getGroupManager().getSudoGroup().kick((Sudoer) user, console);
			
			// remove from cache
			profiles.remove(user.getUniqueID());
		} else throw new IllegalStateException("Unable to unload a player who is still online!");
	}
}
