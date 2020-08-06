package de.timeout.sudo.bungee.users;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;

import org.apache.commons.lang.Validate;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.google.common.io.Files;

import de.timeout.sudo.bungee.Sudo;
import de.timeout.sudo.permissions.UserConfigHandler;
import de.timeout.sudo.users.User;
import de.timeout.sudo.users.UserManager;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.PendingConnection;
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
//		// load permissions if the user is connected
//		if(!event.isCancelled()) {
//			try {
//				// load user from file
//				ProxyUser user = new ProxyUser(event.getConnection(),
//						getUserConfiguration(event.getConnection().getUniqueId()),
//						decodedSudoer.getString(event.getConnection().getUniqueId().toString()));
//				
//				// cache userprofile
//				profiles.put(event.getConnection().getUniqueId(), user);
//				
//				// check if user is a sudoer
//				if(decodedSudoer.contains(user.getUniqueID().toString())) {
//					// add to sudoer
//					main.getGroupManager().getSudoGroup().addMember(user);
//				}
//			} catch (IOException e) {
//				Sudo.log().log(Level.SEVERE, 
//						String.format("&cUnable to load Configuration %s.json of player %s",
//								event.getConnection().getUniqueId(),
//								event.getConnection().getName()
//						),
//				e);
//			}
//		}
	}
	
	@EventHandler
	public void onUserSave(PlayerDisconnectEvent event) {
		// removes user from cache
		unloadUser(getUser(event.getPlayer()));

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

	public void changePassword(User user, String password) throws IOException {
		
	}
}
