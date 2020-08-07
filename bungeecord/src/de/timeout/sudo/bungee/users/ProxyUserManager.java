package de.timeout.sudo.bungee.users;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;

import com.google.common.io.Files;

import org.apache.commons.lang.Validate;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import de.timeout.sudo.bungee.Sudo;
import de.timeout.sudo.netty.packets.PacketRemoteInAddUserPermission;
import de.timeout.sudo.netty.packets.PacketRemoteInLoadUser;
import de.timeout.sudo.netty.packets.PacketRemoteInUpdateUserProfile;
import de.timeout.sudo.netty.packets.PacketRemoteInUserJoinsGroup;
import de.timeout.sudo.permissions.UserConfigHandler;
import de.timeout.sudo.users.User;
import de.timeout.sudo.users.UserManager;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.PendingConnection;
import net.md_5.bungee.api.connection.Server;
import net.md_5.bungee.api.event.LoginEvent;
import net.md_5.bungee.api.event.PermissionCheckEvent;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.ServerConnectedEvent;
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
public class ProxyUserManager extends UserManager implements Listener, UserConfigHandler<Configuration> {
	
	private static final Sudo main = Sudo.getInstance();
	
	private static final File userFolder = new File(main.getDataFolder(), "users");
				
	/**
	 * Create a new ProxySudoerManager
	 * @author Timeout
	 * @throws IOException 
	 *
	 */
	public ProxyUserManager() throws IOException {	
		super(new RootConsole());
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
	 * @param player the person you want to get. Can be null
	 * @return the user or null if the player is null
	 */
	@Nullable
	public User getUser(@Nullable CommandSender player) {
		// check if sender is the console when sender is not a player connection. If true return root, else null (not found)
		if(player instanceof PendingConnection) {
			// return player's profile
			return profiles.get(((PendingConnection) player).getUniqueId());
		} else return main.getProxy().getConsole().equals(player) ? getRoot() : null;
	}
	
	@Override
	public Configuration getUserConfiguration(@NotNull UUID uuid) throws IOException {
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
		// do nothing if the login is cancelled
		if(!event.isCancelled()) {
			try {
				// load user from configuration
				ProxyUser user = new ProxyUser(event.getConnection(), getUserConfiguration(event.getConnection().getUniqueId()));
			
				// cache result in profiles
				profiles.put(user.getUniqueID(), user);
			} catch (IOException e) {
				Sudo.log().log(Level.SEVERE, 
						String.format("&cUnable to load configuration of player %s.", event.getConnection().getName()), e);
			}
		}
	}
	
	@EventHandler
	public void onUserSave(PlayerDisconnectEvent event) {
		// removes user from cache
		unloadUser(getUser(event.getPlayer()));
	}
	
	@EventHandler
	public void onSendUserInformations(ServerConnectedEvent event) {
		// get User-Profile of the player
		User user = getUser(event.getPlayer().getUniqueId());
		
		// send user to bukkit server
		sendUserToBukkit(user, event.getServer());
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPermissionCheck(PermissionCheckEvent event) {
		// get Player
		User user = getUser(event.getSender());
		
		// override permissions
		event.setHasPermission(user.hasPermission(event.getPermission()));
	}

	@Override
	public void unloadUser(User user) {
		// Validate
		Validate.notNull(user, "User cannot be null");
		
		// check if user is not null online
		if(main.getProxy().getPlayer(user.getUniqueID()) == null) {
			// remove from all groups
			user.getPermissionContainer().getMembers().forEach(user::leaveGroup);
			
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
	
	/**
	 * Method which sends a user profile to a certain server
	 * @param user the user you want to send. Cannot be null
	 * @param server the server where the user profile will arrive. Cannot be null
	 * @throws IllegalArgumentException if any argument is null
	 */
	public void sendUserToBukkit(@NotNull User user, @NotNull Server server) {	
		// Validate
		Validate.notNull(user, "User cannot be null");
		Validate.notNull(server, "Server cannot be null");
		
		// send profile to bukkit server
		main.getNettyServer().sendPacket(server, new PacketRemoteInLoadUser(user));
		main.getNettyServer().sendPacket(server, 
				new PacketRemoteInUpdateUserProfile(user, user.getPermissionContainer().getPrefix(), user.getPermissionContainer().getSuffix()));
		
		// send permissions to bukkit server
		user.getPermissionContainer().getPermissions().forEach(permission -> 
				main.getNettyServer().sendPacket(server, new PacketRemoteInAddUserPermission(user, permission)));
		
		// send all groups to bukkit server
		user.getPermissionContainer().getMembers().forEach(group ->
				main.getNettyServer().sendPacket(server, new PacketRemoteInUserJoinsGroup(user, group)));
	}
}
