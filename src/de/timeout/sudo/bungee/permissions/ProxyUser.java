package de.timeout.sudo.bungee.permissions;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import javax.annotation.Nullable;

import org.apache.commons.lang.Validate;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import de.timeout.sudo.bungee.Sudo;
import de.timeout.sudo.groups.BaseGroup;
import de.timeout.sudo.groups.Group;
import de.timeout.sudo.groups.User;
import de.timeout.sudo.utils.PermissionTree;

import net.md_5.bungee.api.connection.PendingConnection;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.config.Configuration;

public class ProxyUser implements User {
	
	private static final Sudo main = Sudo.getInstance();
	
	private final PermissionTree permissions = new PermissionTree();
	private final List<Group> groups = new ArrayList<>();
	
	private UUID playerID;
	private String name;
	private String prefix;
	private String suffix;
	
	public ProxyUser(PendingConnection connection) {
		this.playerID = connection.getUniqueId();
		// load data
		Configuration section = main.getUserConfig().getSection(playerID.toString());
		// if configuration could be loaded
		if(section != null) {
			// load values from file
			this.name = section.getString("name", connection.getName());
			this.prefix = section.getString("prefix");
			this.suffix = section.getString("suffix");
			
			// load groups
			section.getStringList("groups").forEach(group -> this.groups.add(BaseGroup.getGroupByName(group)));
			// load own permissions
			section.getStringList("permissions").forEach(this.permissions::add);
		} else {
			// add default group to groups
			groups.add(BaseGroup.getDefaultGroup());
			// set attributes to default
			this.name = connection.getName();
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
	public boolean isSudoer() {
		return false;
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
		object.addProperty("name", this.name);
		object.addProperty("prefix", this.prefix);
		object.addProperty("suffix", this.suffix);
		
		// Create JsonArray for groups
		JsonArray groupsArray = new JsonArray();
		// add all elements in groups
		this.groups.forEach(group -> groupsArray.add(new JsonPrimitive(group.getName())));
		
		// Create JsonArray for permissions
		JsonArray permissionsArray = new JsonArray();
		// add all own permissions in permissions array
		this.permissions.toSet().forEach(permission -> permissionsArray.add(new JsonPrimitive(permission)));
		
		// write both arrays in object
		object.add("groups", groupsArray);
		object.add("permissions", permissionsArray);
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
}
