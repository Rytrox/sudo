package de.timeout.sudo.bungee.users;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.apache.commons.lang.Validate;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import de.timeout.sudo.bungee.Sudo;
import de.timeout.sudo.container.UserContainer;
import de.timeout.sudo.users.AuthorizableUser;
import de.timeout.sudo.users.User;
import de.timeout.sudo.groups.Group;
import de.timeout.sudo.groups.UserGroup;
import de.timeout.sudo.utils.PasswordCryptor;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.connection.PendingConnection;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.JsonConfiguration;

public class ProxyUser implements AuthorizableUser {
	
	protected static final Sudo main = Sudo.getInstance();
	
	private static final ConfigurationProvider JSON_PROVIDER = ConfigurationProvider.getProvider(JsonConfiguration.class);
		
	private static final String NAME_FIELD = "name";
	private static final String PASSWORD_FIELD = "password";
	private static final String PREFIX_FIELD = "prefix";
	private static final String SUFFIX_FIELD = "suffix";
	private static final String GROUPS_FIELD = "groups";
	private static final String PERMISSIONS_FIELD = "permissions";
	
	
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
	public ProxyUser(@NotNull PendingConnection connection, @Nullable Configuration configuration) throws IOException {
		// Validate
		Validate.notNull(connection, "Connection cannot be null");
		
		// set attributes to default
		this.playerID = connection.getUniqueId();
		this.encodedPassword = configuration != null ? configuration.getString(PASSWORD_FIELD, "") : "";
		
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
				groups.add(group);
			});
			// add default group if groups is empty
			if(groups.isEmpty()) groups.add(main.getGroupManager().getDefaultGroup());
		}
		
		this.container = new UserContainer(this, permissions, groups, connection.getName(), prefix, suffix);
		this.activeContainer = this.container;
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
		// get file
		File file = new File(new File(main.getDataFolder(), "users"), String.format("%s.json", playerID.toString()));
		
		// create config on path
		Configuration config = JSON_PROVIDER.load(file);
		
		// write primitives into users
		config.set(NAME_FIELD, container.getName());
		config.set(PREFIX_FIELD, container.getPrefix().replace(ChatColor.COLOR_CHAR, '&'));
		config.set(SUFFIX_FIELD, container.getSuffix().replace(ChatColor.COLOR_CHAR, '&'));
		config.set(PASSWORD_FIELD, encodedPassword);
		
		// write groups into users
		config.set(GROUPS_FIELD, container.getMembers()
				.stream()
				.map(Group::getName)
				.collect(Collectors.toList())
		);
		
		// write permissions into user
		config.set(PERMISSIONS_FIELD, container.getPermissions());
		
		// save config
		JSON_PROVIDER.save(config, file);
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
		return container.isMember(main.getGroupManager().getSudoGroup());
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
		// copy of current groups without sudo group
		Collection<Group> groups = container.getMembers()
				.stream()
				.filter(g -> (g instanceof UserGroup))
				.collect(Collectors.toList());
		
		// if method tries to remove last group of the collection
		if(group instanceof UserGroup && groups.remove(group) && groups.isEmpty()) {
			// add default group if group is not the default group
			if(((UserGroup) group).isDefault()) {
				// default group as last element cannot be removed
				return false;
			} else {
				joinGroup(main.getGroupManager().getDefaultGroup());
			}
		}
		
		// everything is fine. Remove
		return container.remove(group);
	}

	@Override
	public boolean isMember(Group group) {
		return container.isMember(group);
	}

	@Override
	public Collection<Group> getGroups() {
		return activeContainer.getMembers();
	}
}
