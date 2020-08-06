package de.timeout.sudo.bungee.users;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import de.timeout.sudo.bungee.Sudo;
import de.timeout.sudo.permissions.UserContainer;
import de.timeout.sudo.users.AuthorizableUser;
import de.timeout.sudo.users.User;
import de.timeout.sudo.groups.Group;
import de.timeout.sudo.utils.PasswordCryptor;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.connection.PendingConnection;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.config.Configuration;

public class ProxyUser implements AuthorizableUser {
		
	private static final String NAME_FIELD = "name";
	private static final String PREFIX_FIELD = "prefix";
	private static final String SUFFIX_FIELD = "suffix";
	private static final String GROUPS_FIELD = "groups";
	private static final String PERMISSIONS_FIELD = "permissions";
	
	protected static final Sudo main = Sudo.getInstance();
	
	protected final UserContainer container;
	
	protected UUID playerID;
	protected UserContainer activeContainer;
	
	protected String encodedPassword;
	private boolean authorized; 
	
	/**
	 * Loads a ProxyUser from the internal File
	 * @author Timeout
	 *
	 * @param connection the connection
	 * @throws IOException if the file cannot be read
	 */
	public ProxyUser(@NotNull PendingConnection connection, @Nullable Configuration configuration, @Nullable String encodedPassword) throws IOException {
		// set attributes to default
		this.playerID = connection.getUniqueId();
		
		Set<String> permissions = new HashSet<>();
		Set<Group> groups = new HashSet<>();
		
		String prefix = null;
		String suffix = null;
		
		if(configuration != null) {
			// load from config
			prefix = ChatColor.translateAlternateColorCodes('&', configuration.getString(PREFIX_FIELD, ""));
			suffix = ChatColor.translateAlternateColorCodes('&', configuration.getString(SUFFIX_FIELD, ""));
			
			// load permissions
			permissions.addAll(configuration.getStringList(PERMISSIONS_FIELD));
			
			// load groups
			configuration.getStringList(GROUPS_FIELD).forEach(groupname -> {
				// get Group
				Group group = main.getGroupManager().getGroupByName(groupname);
				
				// join group
				group.add(this);
				groups.add(group);
			});
		}
		this.container = new UserContainer(this, permissions, groups, connection.getName(), prefix, suffix);
		this.activeContainer = this.container;
		this.encodedPassword = encodedPassword;
	}
			
	@Override
	public int compareTo(User o) {
		return playerID.compareTo(o.getUniqueID());
	}
	
	@Override
	public boolean addPermission(String permission) {
		return container.addPermission(permission);
	}

	@Override
	public boolean removePermission(String permission) {
		return container.removePermission(permission);
	}

	@Override
	public boolean hasPermission(String permission) {	
		// permission was not found in groups. Search in personal permissions
		return activeContainer.hasPermission(permission);
	}

	@Override
	public Collection<String> getPermissions() {
		return container.getPermissions();
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
		object.addProperty(NAME_FIELD, this.container.getName());
		object.addProperty(PREFIX_FIELD, this.container.getPrefix());
		object.addProperty(SUFFIX_FIELD, this.container.getSuffix());
		
		// Create JsonArray for groups
		JsonArray groupsArray = new JsonArray();
		// add all elements in groups
		this.container.getMembers().forEach(group -> groupsArray.add(new JsonPrimitive(group.getName())));
		
		// Create JsonArray for permissions
		JsonArray permissionsArray = new JsonArray();
		// add all own permissions in permissions array
		this.container.getPermissions().forEach(permission -> permissionsArray.add(new JsonPrimitive(permission)));
		
		// write both arrays in object
		object.add(GROUPS_FIELD, groupsArray);
		object.add(PERMISSIONS_FIELD, permissionsArray);
		// return object
		return object;
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
	public void setPrefix(String prefix) {
		this.container.setPrefix(prefix);
	}

	@Override
	public void setSuffix(String suffix) {
		this.container.setSuffix(suffix);
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
	public void applyPermissionContainer(User holder) {
		// check if holder is null
		this.activeContainer = holder != null ? holder.getPermissionContainer() : container;
	}

	@SuppressWarnings("unchecked")
	@Override
	public UserContainer getPermissionContainer() {
		return new UserContainer(container);
	}

	@Override
	public String getEncodedPassword() {
		return encodedPassword;
	}

	@Override
	public boolean authorize(String password) {
		authorized = PasswordCryptor.authenticate(password, encodedPassword);
		return authorized;
	}

	@Override
	public boolean isAuthorized() {
		return authorized;
	}

	@Override
	public boolean isSudoer() {
		return main.getGroupManager().getSudoGroup().isMember(this);
	}

	@Override
	public boolean isRoot() {
		return activeContainer.getOwner().equals(main.getUserManager().getRoot());
	}

	@Override
	public boolean joinGroup(Group group) {
		return container.add(group);
	}

	@Override
	public boolean leaveGroup(Group group) {
		return container.remove(group);
	}

	@Override
	public boolean isMember(Group group) {
		return container.isMember(group);
	}
}
