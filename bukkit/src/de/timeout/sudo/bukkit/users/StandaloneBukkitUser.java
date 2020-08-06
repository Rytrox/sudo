package de.timeout.sudo.bukkit.users;

import java.io.File;
import java.io.IOException;

import de.timeout.libs.config.ConfigCreator;
import de.timeout.libs.config.JsonConfig;
import de.timeout.sudo.security.Authorizable;
import de.timeout.sudo.utils.PasswordCryptor;

import org.apache.commons.lang.Validate;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;

/**
 * Standalone-Instance of BukkitUser. Will be used when Bukkit-Server runs a standalone service
 * @author Timeout
 *
 */
final class StandaloneBukkitUser extends BukkitUser implements Authorizable {

	private String encodedPassword;
	private boolean authorized;
	
	public StandaloneBukkitUser(OfflinePlayer opable, ConfigurationSection configuration, String encodedPassword) {
		super(opable, configuration);
		
		Validate.isTrue(!main.bungeecordEnabled(), "Sudo must be a standalone service to create this instance");
		
		this.encodedPassword = encodedPassword;
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
		// get configuration of user
		JsonConfig configuration = new JsonConfig(
				new File(new File(main.getDataFolder(), "users"),
						String.format("%s.json", this.operator.getUniqueId().toString())));
		
		// write data in configuration
		configuration.set("groups", arg1);
	}
}
