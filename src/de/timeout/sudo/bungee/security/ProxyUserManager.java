package de.timeout.sudo.bungee.security;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;

import javax.annotation.Nonnull;

import org.apache.commons.lang.Validate;

import com.google.common.io.Files;

import de.timeout.libs.Reflections;
import de.timeout.sudo.bungee.Sudo;
import de.timeout.sudo.bungee.permissions.ProxyUser;
import de.timeout.sudo.netty.packets.PacketRemoteInLoadUser;
import de.timeout.sudo.users.Root;
import de.timeout.sudo.users.Sudoer;
import de.timeout.sudo.users.User;
import de.timeout.sudo.users.UserManager;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.LoginEvent;
import net.md_5.bungee.api.event.PermissionCheckEvent;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.JsonConfiguration;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;

/**
 * Manager class for handling sudoers
 * @author Timeout
 *
 */
public class ProxyUserManager extends UserManager<Configuration> implements Listener {
	
	private static final Sudo main = Sudo.getInstance();
	
	private static final Field passwordField = Reflections.getField(ProxySudoer.class, "password");
	private static final File userFolder = new File(main.getDataFolder(), "users");
	
	private Configuration decodedSudoers;
	
	/**
	 * Create a new ProxySudoerManager
	 * @author Timeout
	 *
	 */
	public ProxyUserManager() {
		super(new RootConsole());
		
		reloadSudoerConfig();
	}
	
	@Override
	public void reloadSudoerConfig() {
		// load configuration
		try {
			decodedSudoers = ConfigurationProvider.getProvider(JsonConfiguration.class).load(new String(
						Base64.getDecoder().decode(
								String.join("", Files.readLines(new File(main.getDataFolder(), "sudoers.out"),
										StandardCharsets.UTF_8)))));
		} catch (IOException e) {
			Sudo.log().log(Level.WARNING, "&cUnable to read sudoers.out", e);
		}
	}

	@Override
	public void saveSudoerConfig() {
		// save all new users in sudoers config
		main.getGroupManager().getSudoGroup().getMembers().forEach(sudoer -> 
			// create a new Configuration
			decodedSudoers.set(String.format("%s.password", sudoer.getUniqueID().toString()), Reflections.getValue(passwordField, sudoer))
		);
		
		// define new file
		File file = new File(main.getDataFolder(), "sudoers.out");
		
		try {
			// creates parents dir
			Files.createParentDirs(file);
			// create file
			Files.touch(file);
			// write data into file
			ConfigurationProvider.getProvider(JsonConfiguration.class).save(decodedSudoers, file);
			// encode data
			Files.write(Base64.getEncoder().encodeToString(Files.toByteArray(file)), file, StandardCharsets.UTF_8);
		} catch (IOException e) {
			Sudo.log().log(Level.WARNING, "&cUnable to write sudoers.out");
		}
	}

	/**
	 * Returns a list with all online users
	 * @author Timeout
	 * 
	 * @return a list containing a
	 */
	public List<User> getAllLoggedUsers() {
		// create new list
		List<User> onlineUsers = new ArrayList<>();
		// run through onlineplayers
		main.getProxy().getPlayers().forEach(player -> onlineUsers.add(profiles.get(player.getUniqueId())));
		// return list
		return onlineUsers;
	}
	
	/**
	 * Returns the User of the Player if the player is loaded. <br>
	 * Return null if the player is null
	 * @author Timeout
	 * 
	 * @param player the player you want to get. Can be null
	 * @return the user or null if the player is null
	 */
	public User getUser(ProxiedPlayer player) {
		return player != null ? profiles.get(player.getUniqueId()) : null;
	}
	
	@Override
	public void upgradeUser(Sudoer superUser, Root executor) {
		// only continue if the executor is authrized
		Validate.isTrue(executor.isRoot(), "For promoting a user to a sudoer the executor must be root");
		
		// replace user with superuser
		profiles.replace(superUser.getUniqueID(), superUser);
	}
	
	@Override
	public Configuration getUserConfiguration(@Nonnull UUID uuid) throws IOException {
		// Validate
		Validate.notNull(uuid, "UUID cannot be null");
		// get user-file
		File file = new File(userFolder, String.format("%s.json", uuid.toString()));
		// create parent directories
		Files.createParentDirs(file);
		// read file if file exists and is not empty
		if(file.exists() && file.length() > 0L) {
			return ConfigurationProvider.getProvider(JsonConfiguration.class).load(file);
		} else return null;
	}
	
	/**
	 * loads the user while player joins on the proxy
	 * @author Timeout
	 * 
	 * @param event the playerhandshakeevent
	 */
	@EventHandler
	public void onUserLoad(LoginEvent event) {
		// load permissions if the user is connected
		if(!event.isCancelled()) {
			try {
				// get User 
				User user = new ProxyUser(event.getConnection());
				// add user to cache
				profiles.put(event.getConnection().getUniqueId(), user);
				// send user to all subserver
				main.getProxy().getScheduler().runAsync(main, () -> 
					main.getNettyServer().broadcastPacket(new PacketRemoteInLoadUser(user)));
			} catch (IOException e) {
				Sudo.log().log(Level.WARNING, 
						String.format("&cUnable to access %s.json", event.getConnection().getUniqueId().toString()), e);
			}
		}
	}
	
	@EventHandler
	public void onUserSave(PlayerDisconnectEvent event) {
		// removes user from cache
		unloadUser(getUser(event.getPlayer()));

	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPermissionCheck(PermissionCheckEvent event) {
		// get ProxiedPlayer
		CommandSender sender = event.getSender();
		// if sender is a player
		if(sender instanceof ProxiedPlayer) {
			// get Player
			ProxiedPlayer player = (ProxiedPlayer) sender;
			// override permissions
			event.setHasPermission(getUser(player.getUniqueId()).hasPermission(event.getPermission()));
		}
	}

	@Override
	public void unloadUser(User user) {
		// Validate
		Validate.notNull(user, "User cannot be null");
		
		// check if user is not null online
		if(main.getProxy().getPlayer(user.getUniqueID()) == null) {
			// remove from all groups
			user.getMembers().forEach(group -> group.kick(user));
			
			// remove from sudogroup if user is a sudoer
			if(user instanceof Sudoer) main.getGroupManager().getSudoGroup().kick((Sudoer) user, console);
			
			// remove from cache
			profiles.remove(user.getUniqueID());
			
			// save to file
			try {
				((ProxyUser) user).save();
			} catch (IOException e) {
				Sudo.log().log(Level.WARNING, "&cUnable to save user.", e);
			}
		} else throw new IllegalStateException("Unable to unload a player who is still online!");
	}
}
