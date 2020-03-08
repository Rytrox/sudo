package de.timeout.sudo.bukkit.permissions;

import java.util.Set;

import org.bukkit.permissions.PermissibleBase;
import org.bukkit.permissions.ServerOperator;

import de.timeout.sudo.utils.PermissionTree;

public class BukkitUser extends PermissibleBase implements de.timeout.sudo.groups.PermissibleBase {
	
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

}
