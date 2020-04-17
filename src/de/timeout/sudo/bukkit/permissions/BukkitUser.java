package de.timeout.sudo.bukkit.permissions;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;

import javax.annotation.Nonnull;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.permissions.PermissibleBase;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.ServerOperator;

import com.google.common.io.Files;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import de.timeout.libs.config.JsonConfig;
import de.timeout.sudo.bukkit.Sudo;
import de.timeout.sudo.groups.BaseGroup;
import de.timeout.sudo.groups.Customizable;
import de.timeout.sudo.groups.Group;
import de.timeout.sudo.groups.User;
import de.timeout.sudo.utils.PermissionTree;
import de.timeout.sudo.utils.Storable;

import net.md_5.bungee.api.ChatColor;

/**
 * Bukkit representation of the User-Interface
 * @author Timeout
 *
 */
public class BukkitUser extends PermissibleBase implements User, Storable, Customizable {
	
	private static final String PERMISSIONS_FIELD = "permissions";
	private static final String GROUPS_FIELD = "groups";
	
	private static final Sudo main = Sudo.getInstance();
	private static final Gson JSON_BUILDER = new GsonBuilder().setPrettyPrinting().create();
		
	protected final PermissionTree permissions = new PermissionTree();
	protected final Set<Group> groups = new HashSet<>();
	
	protected OfflinePlayer operator;
	protected String prefix;
	protected String suffix;
	
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
		// load from config if server runs bukkit mode
		if(!main.bungeecordEnabled()) {
			try {
				load();
			} catch (IOException e) {
				Sudo.log().log(Level.WARNING, "&cUnable to load configuration from ", e);
			}
		}
	}
	
	public BukkitUser(JsonObject data) {
		// load for OfflinePlayer
		this(Bukkit.getOfflinePlayer(UUID.fromString(data.get("uuid").getAsString())));
		// set data
		this.prefix = ChatColor.translateAlternateColorCodes('&', data.get("prefix").getAsString());
		this.suffix = ChatColor.translateAlternateColorCodes('&', data.get("suffix").getAsString());
		
		// load groups
		data.get(GROUPS_FIELD).getAsJsonArray().forEach(groupname -> {
			// get group
			Group group = main.getGroupManager().getGroupByName(groupname.getAsString());
			// join if group yould be found
			if(group != null) join(group);
		});
		
		// load own permissions
		data.get(PERMISSIONS_FIELD).getAsJsonArray().forEach(permission -> addPermission(permission.getAsString()));
	}
	
	@Override
	public boolean isOp() {
		// Return always false. Sudo does not allow OP
		return false;
	}

	@Override
	public void setOp(boolean value) {
		/* DO NOTHING. SUDO DOES NOT ALLOW OP */
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
	public boolean hasPermission(Permission perm) {
		// check if user has permission
		return this.hasPermission(perm.getName());
	}

	@Override
	public boolean hasPermission(String inName) {
		// return true if the user has this permission
		if(!this.permissions.contains(inName)) {
			// run through groups
			for(Group group : groups) if(group.hasPermission(inName)) return true;
			// return false. Player has not this permission
			return false;
		} else return true;
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
		return groups.contains(element);
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
	public boolean isOnline() {
		return operator.isOnline();
	}

	@Override
	public UUID getUniqueID() {
		return operator.getUniqueId();
	}

	@Override
	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	@Override
	public void setSuffix(String suffix) {
		this.suffix = suffix;
	}
	
	@Override
	public JsonObject toJson() {
		// create JsonObject
		JsonObject object = new JsonObject();
		// write uuid
		object.addProperty("uuid", this.operator.getUniqueId().toString());
		
		// write primitives
		JsonObject options = new JsonObject();
		options.addProperty("prefix", this.prefix != null ? this.prefix.replace(ChatColor.COLOR_CHAR, '&') : null);
		options.addProperty("suffix", this.suffix != null ? this.suffix.replace(ChatColor.COLOR_CHAR, '&') : null);
		object.add("options", options);
		
		// write groups
		JsonArray groupsArray = new JsonArray();
		this.groups.forEach(group -> groupsArray.add(new JsonPrimitive(group.getName())));
		object.add(GROUPS_FIELD, groupsArray);
		
		// write permissions
		JsonArray permissionsArray = new JsonArray();
		this.permissions.toSet().forEach(permission -> permissionsArray.add(new JsonPrimitive(permission)));
		options.add(PERMISSIONS_FIELD, permissionsArray);
		
		return object;
	}

	@Override
	public void load() throws IOException {
		// get configuration
		JsonConfig userConfig = new JsonConfig(new File(new File(main.getDataFolder(), "users"), String.format("%s.json", this.operator.getUniqueId().toString())));
		// clear permissions and groups
		permissions.clear();
		groups.forEach(this::kick);
		
		// load options
		prefix = ChatColor.translateAlternateColorCodes('&', userConfig.getString("options.prefix", ""));
		suffix = ChatColor.translateAlternateColorCodes('&', userConfig.getString("options.suffix", ""));
		
		// load groups
		userConfig.getStringList(GROUPS_FIELD).forEach(groupname -> {
			// get group
			Group group = main.getGroupManager().getGroupByName(groupname);
			// add to group if group has been found
			if(group != null) join(group);
		});
		// add him to default group if he has no group
		if(groups.isEmpty()) join(BaseGroup.getDefaultGroup());
		System.out.println(BaseGroup.getDefaultGroup().getName());
		
		// load permissions
		userConfig.getStringList(PERMISSIONS_FIELD).forEach(this::addPermission);
	}

	@Override
	public void save() throws IOException {
		// only save if bungeecord is disabled
		if(!main.bungeecordEnabled()) {
			// get Json
			JsonObject data = toJson();
			// create new File
			File file = new File(new File(main.getDataFolder(), "users"), String.format("%s.json", this.operator.getUniqueId().toString()));
			// create folder if not exists
			if(!file.getParentFile().exists()) file.getParentFile().mkdirs();
			// write data into file
			Files.write(JSON_BUILDER.toJson(data), file, StandardCharsets.UTF_8);
		}
	}

	@Override
	public Collection<Group> getGroups() {
		return new ArrayList<>(groups);
	}

	@Override
	public void recalculatePermissions() {
		/* DO NOTHING. NO RELOAD IS REQUIRED WHILE RUNNING SUDO */
	}
}
