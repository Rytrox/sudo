package de.timeout.sudo.bungee.permissions;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import javax.annotation.Nullable;

import org.apache.commons.lang.Validate;

import de.timeout.sudo.bungee.Sudo;
import de.timeout.sudo.groups.Group;
import de.timeout.sudo.groups.User;
import de.timeout.sudo.utils.PermissionTree;

import net.md_5.bungee.api.connection.ProxiedPlayer;

public class ProxyUser implements User {
	
	private static final Sudo main = Sudo.getInstance();
	
	private final PermissionTree permissions = new PermissionTree();
	private final List<Group> groups = new ArrayList<>();
	
	private UUID playerID;
	
	public ProxyUser(UUID uuid) {
		this.playerID = uuid;
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
}
