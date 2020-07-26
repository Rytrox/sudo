package de.timeout.sudo.bungee.users;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
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
import de.timeout.sudo.users.User;
import de.timeout.sudo.groups.UserGroup;
import de.timeout.sudo.utils.PasswordCryptor;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.connection.PendingConnection;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.config.Configuration;

public class ProxyUser implements User {
		
	private static final String NAME_FIELD = "name";
	private static final String PREFIX_FIELD = "prefix";
	private static final String SUFFIX_FIELD = "suffix";
	private static final String GROUPS_FIELD = "groups";
	private static final String PERMISSIONS_FIELD = "permissions";
	
	protected static final Sudo main = Sudo.getInstance();
	
	protected final UserContainer permissions;
	
	protected UUID playerID;
	protected UserContainer activePermissions;
	
	protected String encodedPassword;
	private boolean authorized; 
	
	/**
	 * Loads a ProxyUser from the internal File
	 * @author Timeout
	 *
	 * @param connection the connection
	 * @throws IOException if the file cannot be read
	 */
	public ProxyUser(@NotNull PendingConnection connection, @Nullable Configuration configuration) throws IOException {
		// set attributes to default
		this.playerID = connection.getUniqueId();
		
		Set<String> permissions = new HashSet<>();
		String prefix = null;
		String suffix = null;
		
		if(configuration != null) {
			// load from config
			prefix = ChatColor.translateAlternateColorCodes('&', configuration.getString(PREFIX_FIELD, ""));
			suffix = ChatColor.translateAlternateColorCodes('&', configuration.getString(SUFFIX_FIELD, ""));
			
			// load permissions
			permissions.addAll(configuration.getStringList(PERMISSIONS_FIELD));
			
			// load groups
		}
		this.permissions = new UserContainer(this, permissions, new ArrayList<>(), connection.getName(), prefix, suffix);
		this.activePermissions = this.permissions;
	}
			
	@Override
	public int compareTo(User o) {
		return playerID.compareTo(o.getUniqueID());
	}
	
	@Override
	public boolean addPermission(String permission) {
		return permissions.addPermission(permission);
	}

	@Override
	public boolean removePermission(String permission) {
		return permissions.removePermission(permission);
	}

	@Override
	public boolean hasPermission(String permission) {	
		// permission was not found in groups. Search in personal permissions
		return activePermissions.hasPermission(permission);
	}

	@Override
	public Collection<String> getPermissions() {
		return permissions.getPermissions();
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
		object.addProperty(NAME_FIELD, this.permissions.getName());
		object.addProperty(PREFIX_FIELD, this.permissions.getPrefix());
		object.addProperty(SUFFIX_FIELD, this.permissions.getSuffix());
		
		// Create JsonArray for groups
		JsonArray groupsArray = new JsonArray();
		// add all elements in groups
		this.permissions.getMembers().forEach(group -> groupsArray.add(new JsonPrimitive(group.getName())));
		
		// Create JsonArray for permissions
		JsonArray permissionsArray = new JsonArray();
		// add all own permissions in permissions array
		this.permissions.getPermissions().forEach(permission -> permissionsArray.add(new JsonPrimitive(permission)));
		
		// write both arrays in object
		object.add(GROUPS_FIELD, groupsArray);
		object.add(PERMISSIONS_FIELD, permissionsArray);
		// return object
		return object;
	}

	@Override
	public String getName() {
		return activePermissions.getName();
	}

	@Override
	public String getPrefix() {
		return activePermissions.getPrefix();
	}

	@Override
	public String getSuffix() {
		return activePermissions.getSuffix();
	}

	@Override
	public void setPrefix(String prefix) {
		this.permissions.setPrefix(prefix);
	}

	@Override
	public void setSuffix(String suffix) {
		this.permissions.setSuffix(suffix);
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
		this.activePermissions = holder != null ? holder.getPermissionContainer() : permissions;
	}

	@Override
	public UserContainer getPermissionContainer() {
		return new UserContainer(permissions);
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
		return activePermissions.getOwner().equals(main.getUserManager().getRoot());
	}

	@Override
	public boolean joinGroup(UserGroup group) {
		return permissions.add(group);
	}

	@Override
	public boolean leaveGroup(UserGroup group) {
		return permissions.remove(group);
	}

	@Override
	public boolean isMember(UserGroup group) {
		return permissions.isMember(group);
	}
}
