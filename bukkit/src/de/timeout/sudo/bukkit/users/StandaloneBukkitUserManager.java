package de.timeout.sudo.bukkit.users;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.UUID;
import java.util.logging.Level;

import com.google.common.io.Files;

import de.timeout.libs.config.JsonConfig;
import de.timeout.sudo.bukkit.Sudo;
import de.timeout.sudo.permissions.UserConfigHandler;
import de.timeout.sudo.security.SudoerConfigurable;
import de.timeout.sudo.users.User;

import org.apache.commons.lang.Validate;
import org.jetbrains.annotations.NotNull;

/**
 * Implementation of the Standalone-UserManager of Sudo
 * @author Timeout
 *
 */
public final class StandaloneBukkitUserManager extends BukkitUserManager implements SudoerConfigurable, UserConfigHandler<JsonConfig> {
	
	private JsonConfig decodedSudoer;
	
	public StandaloneBukkitUserManager() {
		super();
		
		reloadSudoerConfig();
	}
	
	public void loadUserFromFile(@NotNull UUID uuid) {
		
	}
	
	/**
	 * Unloads a user and saves its data in a file
	 * @author Timeout
	 * 
	 * @param player the player you want to unload
	 * @throws IllegalStateException if the server runs in bungeecord mode
	 * @throws IllegalArgumentException if the player is null or online
	 */
	@Override
	public void unloadUser(User user) {
		// Validate
		Validate.notNull(user, "User cannot be null");
		
		// throws illegal state exception if bukkit mode is disabled
		if(!user.isOnline()) {
			// unload user from profiles
			profiles.remove(user.getUniqueID());
			
			// save in file
			try {
				user.save();
			} catch (IOException e) {
				Sudo.log().log(Level.WARNING, String.format("&cUnable to write data file of player %s", user.getName()), e);
			}
		} else throw new IllegalStateException("Users cannot be saved in files while bungeecord is enabled or player is online.");
	}

	@Override
	public void reloadSudoerConfig() {
		// do nothing if bungeecord mode is enabled
		if(!main.bungeecordEnabled()) {
			// load sudoer fine
			try {
				decodedSudoer = new JsonConfig(new String(
						Base64.getDecoder().decode(
								String.join("", Files.readLines(new File(main.getDataFolder(), "sudoers.out"), StandardCharsets.UTF_8))
						)
				));
			} catch (IOException e) {
				Sudo.log().log(Level.WARNING, "&cUnable to read sudoers.out", e);
			}
		}
	}

	@Override
	public void saveSudoerConfig() {
		// get data
		String data = decodedSudoer.saveToString();
		// create File
		File file = new File(main.getDataFolder(), "sudoers.out");
			
		try {
			// create Files
			Files.createParentDirs(file);
			Files.touch(file);
				
			// encode and write data into file
			Files.write(Base64.getEncoder().encodeToString(data.getBytes(StandardCharsets.UTF_8)), file, StandardCharsets.UTF_8);
		} catch (IOException e) {
			Sudo.log().log(Level.WARNING, "&cUnable to save sudoers.out", e);
		}
	}

	@Override
	public JsonConfig getUserConfiguration(UUID uuid) throws IOException {
		return null;
	}
}
