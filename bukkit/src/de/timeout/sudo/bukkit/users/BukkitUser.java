package de.timeout.sudo.bukkit.users;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import javax.annotation.Nonnull;

import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.permissions.PermissibleBase;
import org.bukkit.permissions.Permission;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import de.timeout.sudo.bukkit.Sudo;
import de.timeout.sudo.groups.UserGroup;
import de.timeout.sudo.permissions.UserContainer;
import de.timeout.sudo.users.User;
import de.timeout.sudo.utils.Storable;

import net.md_5.bungee.api.ChatColor;

/**
 * Bukkit representation of the User-Interface
 * @author Timeout
 *
 */
public class BukkitUser extends PermissibleBase implements User, Storable {
	
	private static final String PERMISSIONS_FIELD = "permissions";
	private static final String GROUPS_FIELD = "groups";
	
	protected static final Sudo main = Sudo.getInstance();
	
	protected final UserContainer ownContainer;
	protected UserContainer activeContainer;
	
	protected OfflinePlayer operator;
	
	/**
	 * Loads the BukkitUser of the OfflinePlayer
	 * @author Timeout
	 *
	 * @param opable the offlineplayer you want to load
	 */
	public BukkitUser(@NotNull OfflinePlayer opable) {
		this(opable, null);
	}
	
	public BukkitUser(@NotNull OfflinePlayer opable, @Nullable ConfigurationSection configuration) {
		super(opable);
		
		this.operator = opable;
		
		// try to load configuration
		final List<UserGroup> groups = new ArrayList<>();
		final List<String> permissions = new ArrayList<>();
		String prefix = "";
		String suffix = "";
		
		if(configuration != null) {
			// try to load prefix
			prefix = ChatColor.translateAlternateColorCodes('&', configuration.getString("prefix", ""));
			suffix = ChatColor.translateAlternateColorCodes('&', configuration.getString("suffix", ""));
			
			// add all groups to list
			configuration.getStringList(GROUPS_FIELD)
				.stream()
				.map(main.getGroupManager()::getGroupByName)
				.forEach(group -> {
					// add to list if group is usergroup
					if(group instanceof UserGroup) groups.add((UserGroup) group);
				}
			);
			
			// add all permissions to list
			permissions.addAll(configuration.getStringList(PERMISSIONS_FIELD));
		}
		
		this.ownContainer = new UserContainer(this, permissions, groups, opable.getName(), prefix, suffix);
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

	public boolean addPermission(String permission) {
		return ownContainer.addPermission(permission);
	}

	public boolean removePermission(String permission) {
		return ownContainer.removePermission(permission);
	}

	@Override
	public Collection<String> getPermissions() {
		return ownContainer.getPermissions();
	}
	
	@Override
	public boolean hasPermission(Permission perm) {
		// check if user has permission
		return this.hasPermission(perm.getName());
	}

	@Override
	public boolean hasPermission(String inName) {
		// return true if the user has this permission
		return activeContainer.hasPermission(inName);
	}

	@Override
	public int compareTo(User o) {
		return this.getUniqueID().compareTo(o.getUniqueID());
	}

	public boolean isSudoer() {
		return main.getGroupManager().getSudoGroup().isMember(this);
	}

	@Override
	public boolean isMember(@Nonnull UserGroup element) {
		return activeContainer.isMember(element);
	}

	@Override
	public String getName() {
		return activeContainer.getName();
	}

	@Override
	public String getPrefix() {
		return activeContainer.getPrefix();
	}

	@Override
	public String getSuffix() {
		return activeContainer.getSuffix();
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
		ownContainer.setPrefix(prefix);
	}

	@Override
	public void setSuffix(String suffix) {
		ownContainer.setPrefix(suffix);
	}
	
	@Override
	public JsonObject toJson() {
		// create JsonObject
		JsonObject object = new JsonObject();
		// write uuid
		object.addProperty("uuid", this.operator.getUniqueId().toString());
		
		// write primitives
		JsonObject options = new JsonObject();
		options.addProperty("prefix", this.ownContainer.getPrefix().replace(ChatColor.COLOR_CHAR, '&'));
		options.addProperty("suffix", this.ownContainer.getSuffix().replace(ChatColor.COLOR_CHAR, '&'));
		object.add("options", options);
		
		// write groups
		JsonArray groupsArray = new JsonArray();
		this.ownContainer.getMembers().forEach(group -> groupsArray.add(new JsonPrimitive(group.getName())));
		object.add(GROUPS_FIELD, groupsArray);
		
		// write permissions
		JsonArray permissionsArray = new JsonArray();
		this.ownContainer.getPermissions().forEach(permission -> permissionsArray.add(new JsonPrimitive(permission)));
		options.add(PERMISSIONS_FIELD, permissionsArray);
		
		return object;
	}

	@Override
	public void recalculatePermissions() {
		/* DO NOTHING. NO RELOAD IS REQUIRED WHILE RUNNING SUDO */
	}

	@Override
	public void applyPermissionContainer(User holder) {
		this.activeContainer = holder != null ? holder.getPermissionContainer() : this.getPermissionContainer();
	}

	@Override
	public boolean joinGroup(UserGroup group) {
		return ownContainer.add(group);
	}

	@Override
	public boolean leaveGroup(UserGroup group) {
		return ownContainer.remove(group);
	}

	@Override
	public boolean isRoot() {
		return main.getUserManager().getRoot().getPermissionContainer().equals(activeContainer);
	}

	@Override
	public UserContainer getPermissionContainer() {
		return ownContainer;
	}

	@Override
	public void save() throws IOException {
		/* Send Packet to proxy */
	}
}
