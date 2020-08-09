package de.timeout.sudo.bukkit.users;

import java.io.File;
import java.io.IOException;
import java.util.stream.Collectors;

import com.google.common.base.Predicates;

import de.timeout.libs.config.JsonConfig;
import de.timeout.sudo.groups.Group;
import de.timeout.sudo.security.Authorizable;
import de.timeout.sudo.utils.PasswordCryptor;

import net.md_5.bungee.api.ChatColor;

import org.apache.commons.lang.Validate;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Standalone-Instance of BukkitUser. Will be used when Bukkit-Server runs a standalone service
 * @author Timeout
 *
 */
final class StandaloneBukkitUser extends BukkitUser implements Authorizable {

	private String encodedPassword;
	private boolean authorized;
	
	public StandaloneBukkitUser(OfflinePlayer opable, ConfigurationSection configuration, String encodedPassword) {
		this(opable, configuration);
		
		Validate.isTrue(!main.bungeecordEnabled(), "Sudo must be a standalone service to create this instance");
		
		this.encodedPassword = encodedPassword;
	}
	
	public StandaloneBukkitUser(@NotNull OfflinePlayer opable, @Nullable ConfigurationSection configuration) {
		super(opable);
		
		if(configuration != null) {
			// try to load prefix
			setPrefix(ChatColor.translateAlternateColorCodes('&', configuration.getString("prefix", "")));
			setSuffix(ChatColor.translateAlternateColorCodes('&', configuration.getString("suffix", "")));
			
			// add all groups to list
			configuration.getStringList("groups")
				.stream()
				.map(main.getGroupManager()::getGroupByName)
				.filter(Predicates.notNull())
				.forEach(this::joinGroup);
			
			// add all permissions to list
			configuration.getStringList("permissions").forEach(this::addPermission);
			
			this.encodedPassword = configuration.getString("password", "");
		}
	}

	@Override
	public boolean authorize(String password) {
		this.authorized = PasswordCryptor.authenticate(password, encodedPassword);
		
		return authorized;
	}

	@Override
	public String getEncodedPassword() {
		return encodedPassword;
	}

	@Override
	public boolean isAuthorized() {
		return authorized;
	}

	@Override
	public void save() throws IOException {
		// get User file
		File file = new File(new File(main.getDataFolder(), "users"),
				String.format("%s.json", this.operator.getUniqueId().toString()));
		
		// get configuration of user
		JsonConfig configuration = new JsonConfig(file);
		
		// write primitives in configuration
		configuration.set("name", ownContainer.getName());
		configuration.set("prefix", ownContainer.getPrefix().replace(ChatColor.COLOR_CHAR, '&'));
		configuration.set("suffix", ownContainer.getSuffix().replace(ChatColor.COLOR_CHAR, '&'));
		configuration.set("password", encodedPassword);
		
		// write data in configuration
		configuration.set("groups", ownContainer.getMembers()
				.stream()
				.map(Group::getName)
				.collect(Collectors.toList()));
		
		// write permissions in configuration
		configuration.set("permissions", ownContainer.getPermissions());
		
		
		// save config
		configuration.save(file);
	}
}
