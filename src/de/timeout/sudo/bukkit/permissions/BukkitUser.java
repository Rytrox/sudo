package de.timeout.sudo.bukkit.permissions;

import java.util.Set;
import java.util.UUID;

import org.bukkit.permissions.PermissibleBase;
import org.bukkit.permissions.ServerOperator;

import com.google.gson.JsonObject;

import de.timeout.sudo.groups.Group;
import de.timeout.sudo.groups.User;
import de.timeout.sudo.utils.PermissionTree;

public class BukkitUser extends PermissibleBase implements User {
	
	private final PermissionTree permissions = new PermissionTree();

	public BukkitUser(ServerOperator opable) {
		super(opable);
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
		return 0;
	}

	@Override
	public boolean join(Group element) {
		return false;
	}

	@Override
	public boolean kick(Group element) {
		return false;
	}

	@Override
	public boolean isMember(Group element) {
		return false;
	}

	@Override
	public String getName() {
		return null;
	}

	@Override
	public String getPrefix() {
		return null;
	}

	@Override
	public String getSuffix() {
		return null;
	}

	@Override
	public boolean isSudoer() {
		return false;
	}

	@Override
	public boolean isOnline() {
		return false;
	}

	@Override
	public UUID getUniqueID() {
		return null;
	}

	@Override
	public JsonObject toJson() {
		return null;
	}

}
