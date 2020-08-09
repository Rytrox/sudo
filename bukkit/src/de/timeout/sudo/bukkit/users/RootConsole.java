package de.timeout.sudo.bukkit.users;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.UUID;

import com.google.gson.JsonObject;

import org.bukkit.Bukkit;
import org.bukkit.permissions.PermissibleBase;
import org.bukkit.permissions.Permission;


import de.timeout.libs.BukkitReflections;
import de.timeout.libs.Reflections;
import de.timeout.sudo.bukkit.Sudo;
import de.timeout.sudo.container.UserContainer;
import de.timeout.sudo.groups.Group;
import de.timeout.sudo.users.RemoteUser;
import de.timeout.sudo.users.User;

/**
 * Permissions for Console which is the only root console
 * @author Timeout
 *
 */
class RootConsole extends PermissibleBase implements RemoteUser {
	
	private static final Sudo main = Sudo.getInstance();
	
	private static final Class<?> servercommandsenderClass = BukkitReflections.getCraftBukkitClass("command.ServerCommandSender");
	
	private static final Field permField = Reflections.getField(servercommandsenderClass, "perm"); 
	
	private final UserContainer ownContainer;
	private UserContainer activeContainer;
	
	/**
	 * Create a new Console and hooks into Vanilla
	 * @author Timeout
	 *
	 */
	public RootConsole() {
		super(Bukkit.getConsoleSender());
		
		// create own container
		ownContainer = new UserContainer(this, Arrays.asList("*"), new ArrayList<>(), "root", "&4Console", "");
		activeContainer = ownContainer;
		
		// insert value in bukkits craftconsole
		Reflections.setValue(permField, Bukkit.getConsoleSender(), this);		
		
		// add to sudogroup
		joinGroup(main.getGroupManager().getSudoGroup());
	}

	@Override
	public boolean hasPermission(Permission perm) {
		return this.hasPermission(perm.getName());
	}

	@Override
	public boolean hasPermission(String inName) {
		return activeContainer.hasPermission(inName);
	}

	@Override
	public boolean isOp() {
		return activeContainer.equals(ownContainer);
	}

	@Override
	public boolean isRoot() {
		return activeContainer.equals(ownContainer);
	}

	@Override
	public boolean isOnline() {
		return true;
	}

	@Override
	public UUID getUniqueID() {
		return null;
	}

	@Override
	public String getEncodedPassword() {
		return null;
	}

	@Override
	public boolean authorize(String password) {
		return true;
	}

	@Override
	public boolean isAuthorized() {
		return true;
	}

	@Override
	public JsonObject toJson() {
		return null;
	}

	@Override
	public boolean joinGroup(Group group) {
		return ownContainer.add(group);
	}

	@Override
	public boolean isMember(Group group) {
		return activeContainer.isMember(group);
	}

	@Override
	public boolean leaveGroup(Group group) {
		return ownContainer.remove(group);
	}

	@Override
	public boolean isSudoer() {
		return true;
	}

	@SuppressWarnings("unchecked")
	@Override
	public UserContainer getPermissionContainer() {
		return ownContainer;
	}

	@Override
	public int compareTo(User o) {
		return 0;
	}

	@Override
	public void applyPermissionContainer(User holder) {
		this.activeContainer = holder != null ? holder.getPermissionContainer() : ownContainer;
	}

	@Override
	public String getName() {
		return activeContainer.getName();
	}

	@Override
	public Collection<String> getPermissions() {
		return activeContainer.getPermissions();
	}

	@Override
	public boolean addPermission(String permission) {
		return ownContainer.addPermission(permission);
	}

	@Override
	public boolean removePermission(String permission) {
		return ownContainer.removePermission(permission);
	}

	@Override
	public String getPrefix() {
		return activeContainer.getPrefix();
	}

	@Override
	public void setPrefix(String prefix) {
		ownContainer.setPrefix(prefix);
	}

	@Override
	public String getSuffix() {
		return activeContainer.getSuffix();
	}

	@Override
	public void setSuffix(String suffix) {
		ownContainer.setSuffix(suffix);
	}

	@Override
	public void save() throws IOException {
		/* EMPTY. A CONSOLE CANNOT BE SAVED */
	}

	@Override
	public String getServerIP() {
		return Bukkit.getServer().getIp();
	}

	@Override
	public int getServerPort() {
		return Bukkit.getServer().getPort();
	}

	@Override
	public Collection<Group> getGroups() {
		return activeContainer.getMembers();
	}
}
