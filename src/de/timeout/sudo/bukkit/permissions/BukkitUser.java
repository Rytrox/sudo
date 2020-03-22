package de.timeout.sudo.bukkit.permissions;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissibleBase;
import org.bukkit.permissions.ServerOperator;
import org.bukkit.plugin.messaging.PluginMessageListener;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import de.timeout.sudo.bukkit.Sudo;
import de.timeout.sudo.groups.Group;
import de.timeout.sudo.groups.User;
import de.timeout.sudo.utils.PermissionTree;

public class BukkitUser extends PermissibleBase implements User {
	
	private static final Sudo main = Sudo.getInstance();
	private static final Map<ServerOperator, User> cache = new HashMap<>();
	
	private final PermissionTree permissions = new PermissionTree();
	private final Set<Group> groups = new HashSet<>();
	
	private OfflinePlayer operator;
	private String prefix;
	private String suffix;
	
	/**
	 * Loads the BukkitUser of an ServerOperator
	 * @author Timeout
	 *
	 * @param opable
	 */
	protected BukkitUser(ServerOperator opable) {
		super(opable);
		// add him to default group
		join(BukkitGroup.getDefaultGroup());
		// add to cache
		cache.put(opable, this);
	}
	
	/**
	 * Loads the BukkitUser of the OfflinePlayer
	 * @author Timeout
	 *
	 * @param opable the offlineplayer you want to load
	 */
	public BukkitUser(@Nonnull OfflinePlayer opable) {
		super(opable);
		this.operator = opable;
		// add to cache
		cache.put(opable, this);
	}
	
	public BukkitUser(JsonObject data) {
		// load for OfflinePlayer
		this(Bukkit.getOfflinePlayer(UUID.fromString(data.get("uuid").getAsString())));
	}
	
	public static Future<User> getUserFromPlayer(OfflinePlayer player) {
		// load from cache else from OfflinePlayer
		return CompletableFuture.supplyAsync(() -> 
			// try to load from cache
			Optional.ofNullable(cache.get(player)).orElseGet(() -> {
				// create from user and request from BungeeCord
				BukkitUser user = new BukkitUser(player);
				try {
					// load json from Bungeecord
					JsonObject data = user.new UserBungeeWrapper().get(2, TimeUnit.SECONDS);
					// insert prefix and suffix
					user.setPrefix(data.get("prefix").getAsString());
					user.setSuffix(data.get("suffix").getAsString());
					// set permissions
					data.get("permissions").getAsJsonArray().forEach(permission -> user.addPermission(permission.getAsString()));
					// set groups
					data.get("groups").getAsJsonArray().forEach(groupName -> {
						// get Group by name
						Group group = BukkitGroup.getGroupByName(groupName.getAsString());
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
			})
		);
	}
	
	/**
	 * Returns the User of the operator. Can be null
	 * @author Timeout
	 * 
	 * @param operator the operator you want to get
	 * @return the User of the Player or null
	 */
	@Nullable
	public static User getUserFromOperator(ServerOperator operator) {
		// returns from cache if user is loaded. else load him before
		return Optional.ofNullable(cache.get(operator)).orElse(new BukkitUser(operator));
	}
	
	@Override
	public boolean isOp() {
		return permissions.contains("*");
	}

	@Override
	public void setOp(boolean value) {
		permissions.add("*");
	}

	@Override
	public boolean addPermission(String permission) {
		return permissions.add(permission);
	}

	@Override
	public boolean removePermission(String permission) {
		return permissions.remove(permission);
	}

	@Override
	public Set<String> getPermissions() {
		return permissions.toSet();
	}

	@Override
	public int compareTo(User o) {
		return this.getUniqueID().compareTo(o.getUniqueID());
	}

	@Override
	public boolean join(@Nonnull Group element) {
		// add to set
		if(groups.add(element)) {
			// apply group
			return element.join(this);
		}
		// user is already in group
		return true;
	}

	@Override
	public boolean kick(@Nonnull Group element) {
		// remove from group
		if(element.kick(this)) {
			// apply remove
			return groups.remove(element);
		}
		return false;
	}

	@Override
	public boolean isMember(@Nonnull Group element) {
		return false;
	}

	@Override
	public String getName() {
		return operator.getName();
	}

	@Override
	public String getPrefix() {
		return prefix;
	}

	@Override
	public String getSuffix() {
		return suffix;
	}

	@Override
	public boolean isSudoer() {
		return false;
	}

	@Override
	public boolean isOnline() {
		return operator.isOnline();
	}

	@Override
	public UUID getUniqueID() {
		return operator.getUniqueId();
	}

	@Override
	public JsonObject toJson() {
		return null;
	}

	@Override
	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	@Override
	public void setSuffix(String suffix) {
		this.suffix = suffix;
	}
	
	/**
	 * Task to request and receive Information from Bungeecord
	 * @author Timeout
	 *
	 */
	private class UserBungeeWrapper implements PluginMessageListener, Future<JsonObject> {
				
		private JsonObject data;
		private boolean cancel;
		
		public UserBungeeWrapper() {
			// send request to bungeecord over loadUser
			ByteArrayDataOutput out = ByteStreams.newDataOutput();
			out.writeUTF("loadUser");
			out.writeUTF(operator.getUniqueId().toString());
			Bukkit.getServer().sendPluginMessage(main, "sudo", out.toByteArray());
			// register this BungeeCord message listener
			Bukkit.getMessenger().registerIncomingPluginChannel(main, "sudo", this);
		}
		
		@Override
		public void onPluginMessageReceived(String channel, Player player, byte[] message) {
			// read message
			ByteArrayDataInput in = ByteStreams.newDataInput(message);
			// check if subchannel is loadUser channel and uuid is correct
			if(in.readUTF().equalsIgnoreCase("loadUser") && in.readUTF().equalsIgnoreCase(operator.getUniqueId().toString())) {
				// read data
				data = new JsonParser().parse(in.readUTF()).getAsJsonObject();
				// unregister this listener
				Bukkit.getMessenger().unregisterIncomingPluginChannel(main, "sudo", this);
			}
		}
		
		/**
		 * Returns if the request is done and the data is received
		 * @author Timeout
		 * 
		 * @return if the data is received
		 */
		@Override
		public boolean isDone() {
			return data != null;
		}

		@Override
		public boolean cancel(boolean mayInterruptIfRunning) {
			// only cancel if task is not done
			if(!isDone()) {
				// interrupt here
				cancel = true;
				// unregister Listener
				Bukkit.getMessenger().unregisterIncomingPluginChannel(main, "sudo", this);
			}
			// return cancel
			return cancel;
		}

		@Override
		public boolean isCancelled() {
			return cancel;
		}

		@Override
		public JsonObject get() throws InterruptedException {
			// wait until it is done
			while(!isDone()) {
				// interrupt thread if future is canceled
				if(!cancel) {
					// wait for the next tick
					Thread.sleep(1000 / 20);
				} else {
					// interrupt thread
					Thread.currentThread().interrupt();
					// throw interruptedException
					throw new InterruptedException();
				}
			}
			// return data for success
			return data;
		}

		@Override
		public JsonObject get(long timeout, TimeUnit unit) throws InterruptedException, TimeoutException {
			// get time
			final long time = unit.toMillis(timeout);
			// get current time
			long current = 0;
			// wait until it's done
			while(!isDone()) {
				// only continue if the time is not reached
				if(current < time) {
					// interrupt if it's canceled
					if(!cancel) {
						// wait for next tick
						Thread.sleep(1000 / 20);
					} else {
						Thread.currentThread().interrupt();
						// throw interrupted exception
						throw new InterruptedException();
					}
				} else throw new TimeoutException();
			}
			
			return data;
		}
	}
}
