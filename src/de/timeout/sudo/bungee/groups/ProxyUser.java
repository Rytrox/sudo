package de.timeout.sudo.bungee.groups;

import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;

import javax.annotation.Nullable;

import org.apache.commons.lang.Validate;

import de.timeout.sudo.bungee.Sudo;
import de.timeout.sudo.groups.Group;
import de.timeout.sudo.groups.User;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class ProxyUser implements User {
	
	private static final Sudo main = Sudo.getInstance();
	
	private final Set<String> permissions = new TreeSet<>();
	private final Set<Group> groups = new TreeSet<>();
	
	private UUID playerID;
	
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
		// add user to group
		group.join(this);
		// return success
		return groups.add(group);
	}

	@Override
	public boolean kick(Group group) {
		// Validate
		if(group != null) {
			// kick from group
			group.kick(this);
			// remove and return
			return groups.remove(group);
		}
		// group is null
		return false;
	}

	@Override
	public boolean addPermission(String permission) {
		return false;
	}

	@Override
	public boolean removePermission(String permission) {
		return false;
	}

	@Override
	public boolean hasPermission(String permission) {
		return false;
	}

	@Override
	public boolean isSudoer() {
		return false;
	}

	@Override
	public Set<String> getPermissions() {
		// create a copy of permissions
		Set<String> copy = new TreeSet<>(permissions);
		// add all group permissions to copy
		groups.forEach(group -> 
			// if group is not this group
			copy.addAll(group.getPermissions())
		);
		// return copy
		return copy;
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
