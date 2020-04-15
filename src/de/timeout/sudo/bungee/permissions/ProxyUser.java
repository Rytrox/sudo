package de.timeout.sudo.bungee.permissions;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.commons.lang.Validate;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import de.timeout.sudo.bungee.Sudo;
import de.timeout.sudo.groups.BaseGroup;
import de.timeout.sudo.groups.Group;
import de.timeout.sudo.groups.User;
import de.timeout.sudo.utils.PermissionTree;
import de.timeout.sudo.utils.Storable;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.connection.PendingConnection;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.config.Configuration;

public class ProxyUser implements User, Storable {
		
	private static final String NAME_FIELD = "name";
	private static final String PREFIX_FIELD = "prefix";
	private static final String SUFFIX_FIELD = "suffix";
	private static final String GROUPS_FIELD = "groups";
	private static final String PERMISSIONS_FIELD = "permissions";
	
	private static final Sudo main = Sudo.getInstance();
	
	private final PermissionTree permissions = new PermissionTree();
	private final List<Group> groups = new ArrayList<>();
	
	private UUID playerID;
	private String name;
	private String prefix;
	private String suffix;
	
	/**
	 * Loads a ProxyUser from the internal File
	 * @author Timeout
	 *
	 * @param connection the connection
	 * @throws IOException if the file cannot be read
	 */
	public ProxyUser(@Nonnull PendingConnection connection) throws IOException {
		this(connection.getUniqueId());
		// set attributes to default
		this.name = connection.getName();
	}
	
	/**
	 * Loads a ProxyUser from its uuid
	 * @author Timeout
	 *
	 * @param uuid the uuid of the user
	 * @throws IOException if the file cannot be accessed
	 */
	public ProxyUser(UUID uuid) throws IOException {
		this.playerID = uuid;
		// load data
		Configuration section = main.getGroupManager().getUserConfiguration(uuid);
		// if configuration could be loaded
		if(section != null) {
			// load values from file
			this.name = section.getString(NAME_FIELD);
			this.prefix = section.getString(PREFIX_FIELD);
			this.suffix = section.getString(SUFFIX_FIELD);
			
			// load groups
			section.getStringList(GROUPS_FIELD).forEach(group -> this.groups.add(main.getGroupManager().getGroupByName(group)));
			// load own permissions
			section.getStringList(PERMISSIONS_FIELD).forEach(this.permissions::add);
		} else {
			// add default group to groups
			groups.add(BaseGroup.getDefaultGroup());
		}
	}
		
	@Override
	public boolean isMember(Group element) {
		return groups.contains(element);
	}
	
	@Override
	public int compareTo(User o) {
		return playerID.compareTo(o.getUniqueID());
	}

	@Override
	public boolean join(Group group) {
		// check if group is not null
		Validate.notNull(group, "Group cannot be null");
		// remove from old group
		
		// add user to group
		group.join(this);
		// add group to list
		groups.add(group);
		// return success
		return true;
	}
	
	@Override
	public boolean kick(Group element) {
		return false;
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
	public boolean hasPermission(String permission) {
		// check for own permission
		if(!permissions.contains(permission)) {
			// search in group
			for(Group group : groups) if(group.hasPermission(permission)) return true;
			// return false for not found
			return false;
		} else return true;
	}

	@Override
	public Set<String> getPermissions() {
		return permissions.toSet();
	}

	@Override
	public boolean isOnline() {
		return main.getProxy().getPlayer(playerID) != null;
	}

	/**
	 * Returns the online player. <br>
	 * Returns null if the player is not online
	 * 
	 * @return the onlineplayer or null
	 */
	@Nullable
	public ProxiedPlayer getOnlinePlayer() {
		return main.getProxy().getPlayer(playerID);
	}

	@Override
	public UUID getUniqueID() {
		return playerID;
	}

	@Override
	public JsonObject toJson() {
		// create JsonObject
		JsonObject object = new JsonObject();
		// write attributes in object
		object.addProperty("uuid", this.playerID.toString());
		object.addProperty(NAME_FIELD, this.name);
		object.addProperty(PREFIX_FIELD, this.prefix != null ? this.prefix : "");
		object.addProperty(SUFFIX_FIELD, this.suffix != null ? this.suffix : "");
		
		// Create JsonArray for groups
		JsonArray groupsArray = new JsonArray();
		// add all elements in groups
		this.groups.forEach(group -> groupsArray.add(new JsonPrimitive(group.getName())));
		
		// Create JsonArray for permissions
		JsonArray permissionsArray = new JsonArray();
		// add all own permissions in permissions array
		this.permissions.toSet().forEach(permission -> permissionsArray.add(new JsonPrimitive(permission)));
		
		// write both arrays in object
		object.add(GROUPS_FIELD, groupsArray);
		object.add(PERMISSIONS_FIELD, permissionsArray);
		// return object
		return object;
	}

	@Override
	public String getName() {
		return name;
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
	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	@Override
	public void setSuffix(String suffix) {
		this.suffix = suffix;
	}

	@Override
	public void load() throws IOException {
		// get configuration
		Configuration config = main.getGroupManager().getUserConfiguration(playerID);
		// load prefix suffix from confoguration
		this.prefix = ChatColor.translateAlternateColorCodes('&', config.getString(PREFIX_FIELD));
		this.suffix = ChatColor.translateAlternateColorCodes('&', config.getString(SUFFIX_FIELD));
		this.playerID = UUID.fromString(config.getString("uuid"));
		this.name = config.getString(NAME_FIELD);
		// load group from configuration
		config.getStringList(GROUPS_FIELD).forEach(groupname -> {
			// get group
			Group group = main.getGroupManager().getGroupByName(groupname);
			// only join if group can be found
			if(group != null) join(group);
		});
		// load permissions from configuration
		config.getStringList(PERMISSIONS_FIELD).forEach(this::addPermission);
	}

	@Override
	public void save() throws IOException {
		// get Path of file
		Path path = new File(new File(main.getDataFolder(), "users"), String.format("%s.json", playerID.toString())).toPath();
		// create file if not exists
		if(Files.notExists(path)) Files.createFile(path);
		// write data in file
		Files.write(path, new GsonBuilder().setPrettyPrinting().create().toJson(toJson()).getBytes(StandardCharsets.UTF_8));
	}

	@Override
	public Collection<Group> getGroups() {
		return new ArrayList<>(groups);
	}
}
