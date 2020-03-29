package de.timeout.sudo.bukkit.permissions;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import javax.annotation.Nonnull;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.permissions.PermissibleBase;
import org.bukkit.permissions.ServerOperator;

import com.google.gson.JsonObject;

import de.timeout.sudo.groups.Group;
import de.timeout.sudo.groups.User;
import de.timeout.sudo.utils.PermissionTree;

public class BukkitUser extends PermissibleBase implements User {
		
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
	}
	
	public BukkitUser(JsonObject data) {
		// load for OfflinePlayer
		this(Bukkit.getOfflinePlayer(UUID.fromString(data.get("uuid").getAsString())));
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
}
