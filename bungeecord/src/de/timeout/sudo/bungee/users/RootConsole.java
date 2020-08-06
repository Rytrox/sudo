package de.timeout.sudo.bungee.users;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;

import org.apache.commons.lang.Validate;

import com.google.gson.JsonObject;

import de.timeout.sudo.bungee.Sudo;
import de.timeout.sudo.groups.Group;
import de.timeout.sudo.permissions.UserContainer;
import de.timeout.sudo.users.RemoteUser;
import de.timeout.sudo.users.User;
import de.timeout.sudo.utils.PasswordCryptor;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

/**
 * Implementation for RootConsole
 * @author Timeout
 *
 */
public class RootConsole implements RemoteUser {
	
	private static final Sudo main = Sudo.getInstance();
		
	private final Set<Group> groups = new HashSet<>();
	private final UUID proxyID;
	private final UserContainer permissions;
	private final String ip;
	private final int port;
	
	private UserContainer activePermissions;
	
	private String prefix = ChatColor.translateAlternateColorCodes('&', "&8[&4Console&8] ");
	private String suffix = "";
	
	private boolean authorized;
	
	public RootConsole() throws IOException {		
		// Load proxy configuration
		Configuration config = ConfigurationProvider.getProvider(YamlConfiguration.class).load(new File(main.getDataFolder().getParentFile(), "config.yml"));
			
		// get uuid
		this.proxyID = UUID.fromString(config.getString("uuid"));
		
		// load permissions
		this.permissions = new UserContainer(this, Arrays.asList("*"), new ArrayList<>(),
				"root", ChatColor.translateAlternateColorCodes('&', "&8[&4Console&8] "), "");
		this.activePermissions = permissions;
		
		// get listener
		Object listeners = config.getList("listeners").get(0);
		
		String ip = "";
		int port = 0;
			
		if(listeners instanceof Configuration) {
			// get host
			String[] host = ((Configuration) listeners).getString("host").split(":");
			
			ip = host[0];
			port = Integer.valueOf(host[1]);
		} else Sudo.log().log(Level.WARNING, "&cUnable to read listener of the config");
		
		this.ip = ip;
		this.port = port;
	}

	@Override
	public String getServerIP() {
		return Optional.ofNullable(ip).orElse("127.0.0.1");
	}

	@Override
	public int getServerPort() {
		return port != 0 ? port : 25577;
	}

	/**
	 * RootConsole is always online!
	 */
	@Override
	public boolean isOnline() {
		return true;
	}

	@Override
	public UUID getUniqueID() {
		return proxyID;
	}

	@Override
	public String getEncodedPassword() {
		return null;
	}

	@Override
	public boolean authorize(String password) {
		// try password		
		authorized = PasswordCryptor.authenticate(password, getEncodedPassword());
		
		return authorized;
	}

	@Override
	public boolean isAuthorized() {
		return authorized;
	}

	@Override
	public JsonObject toJson() {
		return null;
	}

	@Override
	public int compareTo(User o) {
		return this.equals(o) ? 0 : 1;
	}

	@Override
	public void applyPermissionContainer(User holder) {
		this.activePermissions = holder != null ? holder.getPermissionContainer() : permissions;
	}

	@Override
	public String getName() {
		return "root";
	}

	@Override
	public boolean hasPermission(String permission) {
		return activePermissions.hasPermission(permission);
	}

	@Override
	public Collection<String> getPermissions() {
		return permissions.getPermissions();
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
	public boolean joinGroup(Group element) {
		// Validate
		Validate.notNull(element, "Group cannot be null");
		
		// add group		
		element.add(this);
		return groups.add(element);
	}

	@Override
	public boolean leaveGroup(Group element) {
		// Validate
		Validate.notNull(element, "Group cannot be null");
		
		element.remove(this);
		return groups.remove(element);
	}

	@Override
	public String getPrefix() {
		return prefix;
	}

	@Override
	public void setPrefix(String prefix) {
		this.prefix = Optional.ofNullable(prefix).orElse("");
	}

	@Override
	public String getSuffix() {
		return suffix;
	}

	@Override
	public void setSuffix(String suffix) {
		this.suffix = Optional.ofNullable(suffix).orElse("");
	}

	@Override
	public void save() throws IOException {
		// save console here!
	}

	@SuppressWarnings("unchecked")
	@Override
	public UserContainer getPermissionContainer() {
		return new UserContainer(permissions);
	}

	@Override
	public boolean isMember(Group group) {
		return groups.contains(group);
	}

	@Override
	public boolean isSudoer() {
		return true;
	}

	@Override
	public boolean isRoot() {
		return true;
	}

}
