package de.timeout.sudo.bukkit.permissions;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.permissions.ServerOperator;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import de.timeout.sudo.bukkit.Sudo;
import de.timeout.sudo.bukkit.messenger.PluginMessageFuture;
import de.timeout.sudo.bukkit.messenger.ProxyUpdateMessager;
import de.timeout.sudo.groups.Group;
import de.timeout.sudo.groups.User;
import de.timeout.sudo.groups.exception.CircularInheritanceException;
import de.timeout.sudo.permissions.GroupManager;

public class BukkitGroupManager extends GroupManager<ServerOperator> {
	
	private static final Sudo main = Sudo.getInstance();
	private static final JsonParser parser = new JsonParser();
			
	public BukkitGroupManager(boolean bukkit) {
		// register PluginMessageChannel if bungeecord is enabled
		if(!bukkit) {
			// info server for using Bungeecord
			Sudo.log().log(Level.INFO, "&2Bungeecord &7in &6spigot.yml &aenabled&7. Requesting data from &2Bungeecord&7...");
			Bukkit.getMessenger().registerOutgoingPluginChannel(main, "sudo");

			// create update messager
			Bukkit.getMessenger().registerIncomingPluginChannel(main, "sudo", new ProxyUpdateMessager());
			
			// send Login Request to BungeeCord
			sendLoginRequest();
		} else loadGroupsFromFile();
	}
	
	/**
	 * Sends a Login-Request to BungeeCord and compiles the received data
	 * @author Timeout
	 *
	 */
	private void sendLoginRequest() {
		// send login request to Bungeecord
		final ByteArrayDataOutput out = ByteStreams.newDataOutput();
		out.writeUTF("login");
		out.writeUTF(main.getConfig().getString("bungeecord.uuid"));
		
		// Create new Async task
		Bukkit.getScheduler().runTaskAsynchronously(main, () -> {
			// send future to bungeecord
			try {
				JsonObject result = new LoginMessageFuture(out).get(5, TimeUnit.SECONDS);
				// check if code was a success
				if(result.get("code").getAsInt() == 200) {
					// log result
					Sudo.log().log(Level.INFO, "&aReceived OK from BungeeCord. Compiling groups...");
					// load groups from BungeeCord.
					loadGroupsFromBungeecord(result.get("groups").getAsJsonArray());
					// load users from BungeeCord
					loadUsersFromBungeecord(result.get("users").getAsJsonArray());
					// log done
					Sudo.log().log(Level.INFO, "&aDone!");
				} else {
					// send error log to console
					Sudo.log().log(Level.WARNING, result.get("message").getAsString());
				}
			} catch (InterruptedException e) {
				Sudo.log().log(Level.SEVERE, "&4Unable to wait for BungeeCord Login-Result. Thread interrupted", e);
				Thread.currentThread().interrupt();
			} catch (TimeoutException e) {
				Sudo.log().log(Level.WARNING, "&cUnable to wait longer for PluginMessage. Connection timed out...", e);
			}
		});
	}
	
	/**
	 * Returns the User of the operator. Can be null
	 * @author Timeout
	 * 
	 * @param operator the operator you want to get
	 * @return the User of the Player or null
	 */
	@Nullable
	public User getUserFromOperator(ServerOperator operator) {
		// returns from cache if user is loaded. else load him before
		return Optional.ofNullable(profiles.get(operator)).orElse(new BukkitUser(operator));
	}
	
	/**
	 * Sends a user load request and returns a Future of the request
	 * @author Timeout
	 * 
	 * @param player the offlineplayer you want to get. Cannot be null
	 * @return
	 */
	public Future<User> getUserFromPlayer(OfflinePlayer player) {
		// load from cache else from OfflinePlayer
		return CompletableFuture.supplyAsync(() -> {
			// check if player is not null
			if(player != null) {
				// try to load from cache
				return Optional.ofNullable(profiles.get(player)).orElseGet(() -> {
					// create from user and request from BungeeCord
					BukkitUser user = new BukkitUser(player);
					try {
						// create byte request 
						ByteArrayDataOutput out = ByteStreams.newDataOutput();
						out.writeUTF("user");
						out.writeUTF("load");
						out.writeUTF(player.getUniqueId().toString());
						
						// load json from Bungeecord
						JsonObject data = new PluginMessageFuture(out) {
	
							@Override
							public void onPluginMessageReceived(String channel, Player receiver, byte[] message) {
								// read message
								ByteArrayDataInput in = ByteStreams.newDataInput(message);
								// check if subchannel is loadUser channel and uuid is correct
								if(in.readUTF().equalsIgnoreCase("user") &&
										in.readUTF().equalsIgnoreCase("load") &&
										in.readUTF().equalsIgnoreCase(player.getUniqueId().toString())) {
									// read data
									data = new JsonParser().parse(in.readUTF()).getAsJsonObject();
									// unregister this listener
									Bukkit.getMessenger().unregisterIncomingPluginChannel(main, "sudo", this);
								}
							}
							
						}.get(5, TimeUnit.SECONDS);
						// insert prefix and suffix
						user.setPrefix(data.get("prefix").getAsString());
						user.setSuffix(data.get("suffix").getAsString());
						// set permissions
						data.get("permissions").getAsJsonArray().forEach(permission -> user.addPermission(permission.getAsString()));
						// set groups
						data.get("groups").getAsJsonArray().forEach(groupName -> {
							// get Group by name
							Group group = main.getGroupManager().getGroupByName(groupName.getAsString());
							// if group could be found
							if(group != null) {
								// remove him from default group
								user.kick(BukkitGroup.getDefaultGroup());
								// add him to this group
								user.join(group);
							}
						});
					} catch (InterruptedException e) {
						Sudo.log().log(Level.SEVERE, "&4Fatal error while receiving data from Bungeecord. Thread interrupted", e);
						// reinterrupt task
						Thread.currentThread().interrupt();
					} catch (TimeoutException e) {
						Sudo.log().log(Level.WARNING, "&cUnable to receive data from BungeeCord in time... Connection timed out", e);
					}
					
					// return user
					return user;
				});
			} else return null;
		});
	}
	
	/**
	 * Converts the Groups-Array from BungeeCord into BukkitGroups
	 * @author Timeout
	 * 
	 * @param data the groups array from bungeecords message
	 */
	private void loadGroupsFromBungeecord(@Nonnull JsonArray data) {
		// run through data to initialize formal groups without inheritances
		data.forEach(groupData -> {
			// get JsonObject of data
			JsonObject groupJson = groupData.getAsJsonObject();
			// create group
			try {
				Group group = new BukkitGroup(groupData.getAsJsonObject());
				// try to add inheritances
				for(JsonElement extend : groupJson.get("extends").getAsJsonArray()) {
					// try to get group
					Group other = getGroupByName(extend.getAsString());
					// try to bind inheritance
					this.bindInheritance(group, other);
				}
			} catch (CircularInheritanceException e) {
				Sudo.log().log(Level.WARNING, "&cUnable to apply data from Bungeecord. Circular dependency in group detected.", e);
			}
		});
	}
	
	/**
	 * Converts the users array from Bungeecord into BukkitUsers
	 * @author Timeout
	 * 
	 * @param data the users array from bungeecord message
	 */
	private void loadUsersFromBungeecord(@Nonnull JsonArray data) {
		// run through data to create users 
		data.forEach(userJson -> {
			// create user
			BukkitUser user = new BukkitUser(userJson.getAsJsonObject());
			// cache user
			profiles.put(Bukkit.getServer().getOfflinePlayer(user.getUniqueID()), user);
		});
	}
	
	private void loadGroupsFromFile() {
		// log load from bukkit files
		Sudo.log().log(Level.INFO, "&9Bukkit-Mode &aenabled. Load groups from ");
		// load all groups
		main.getGroupConfig().getKeys(false).forEach(this::loadGroup);
	}
	
	@Override
	protected Group loadGroup(String name) {
		// get ConfigurationSection
		ConfigurationSection section = main.getGroupConfig().getConfigurationSection(name);
		// check if group is not loaded
		Group group = getGroupByName(name);
		// check if section is valid and group is not already loaded
		if(group == null && section != null) {
			// load Group
			group = new BukkitGroup(section);
			// add edge in graph
			groups.addNode(group);
			// load inheritances
			for(String extendName : section.getStringList("extends")) {
				// try to load group
				Group extend = Optional.ofNullable(getGroupByName(extendName)).orElse(loadGroup(extendName));
				// only continue if group could be loaded
				if(extend != null) {
					// bind inheritance
					try {
						bindInheritance(group, extend);
					} catch (CircularInheritanceException e) {
						Sudo.log().log(Level.WARNING, String.format("&cUnable to load Group %s. Circular Inheritance detected", name), e);
					}
				}
			}
		}
		// return group
		return group;
	}
	
	private class LoginMessageFuture extends PluginMessageFuture {

		public LoginMessageFuture(ByteArrayDataOutput out) {
			super(out);
		}

		@Override
		public void onPluginMessageReceived(String channel, Player player, byte[] message) {
			// if channel is Sudo-Channel
			if("sudo".equalsIgnoreCase(channel)) {
				// get Input
				ByteArrayDataInput input = ByteStreams.newDataInput(message);
				String subchannel = input.readUTF();
				// check if channel is login channel
				if("login".equalsIgnoreCase(subchannel)) {
					// log result to server
					Sudo.log().log(Level.INFO, "&7Data &areceived&7");
					// get element of data
					this.data = parser.parse(input.readUTF()).getAsJsonObject();
				}
			}
		}
	}
}
