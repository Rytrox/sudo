package de.timeout.sudo.bukkit.security;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.logging.Level;

import org.apache.commons.lang.Validate;

import com.google.common.io.Files;

import de.timeout.libs.config.JsonConfig;
import de.timeout.sudo.bukkit.Sudo;
import de.timeout.sudo.bukkit.permissions.BukkitUser;
import de.timeout.sudo.groups.User;
import de.timeout.sudo.security.Sudoer;
import de.timeout.sudo.security.SudoerConfigurable;
import de.timeout.sudo.utils.PasswordCryptor;

/**
 * Manager class for handling sudoers
 * @author Timeout
 *
 */
public class BukkitSudoerManager implements SudoerConfigurable {
	
	private static final Sudo main = Sudo.getInstance();
		
	private JsonConfig decryptedSudoer;
	
	/**
	 * Creates a new Manager
	 * @author Timeout
	 *
	 */
	public BukkitSudoerManager() {
		// load configuration
		reloadSudoerConfig();
	}

	@Override
	public void reloadSudoerConfig() {
		// load file
		File file = new File(main.getDataFolder(), "sudoers.out");
		
		try {
			// read sudoer file
			decryptedSudoer = new JsonConfig(new String(Base64.getDecoder().decode(String.join("", Files.readLines(file, StandardCharsets.UTF_8)))));
		} catch (IOException e) {
			Sudo.log().log(Level.WARNING, "&cUnable to read sudoers file", e);
		}
	}

	@Override
	public void saveSudoerConfig() {
		// save json
		String data = decryptedSudoer.saveToString();
		// get file
		File sudoers = new File(main.getDataFolder(), "sudoers.out");
		try {
			// create file if not exists
			Files.touch(sudoers);
			// encode data and write into sudoer file
			Files.write(Base64.getEncoder().encodeToString(data.getBytes()), sudoers, StandardCharsets.UTF_8);
		} catch (IOException e) {
			Sudo.log().log(Level.WARNING, "&cUnable to write data in sudoer file", e);
		}
	}

	@Override
	public Sudoer addSudoer(User user, String password, Sudoer executor) {
		// Validate
		Validate.notNull(user, "User cannot be null");
		Validate.notEmpty(password, "Password cannot be empty or null");
		Validate.notNull(executor, "Executor cannot be null");
		Validate.isTrue(executor.isAuthorized(), "Executor must be authorized!");
		
		// throw error if the user is not a Bukkit-User
		if(user instanceof BukkitUser) {
			// encode password
			password = PasswordCryptor.encode(password);
			// upgrade User
			Sudoer superUser = BukkitSudoer.upgradeUserToSudoer((BukkitUser) user, password, executor);
			// set password in sudoer configuration
			decryptedSudoer.set(user.getUniqueID().toString(), password);
			// upgrade in groupmanager
			main.getGroupManager().upgradeUser(superUser, executor);
			// return success
			return superUser;
		} else throw new IllegalArgumentException("user is no instance of BukkitUser");
	}

	@Override
	public boolean removeSudoer(Sudoer sudoer, Sudoer executor) {
		// do nothing if the executor is not authorized
		if(!executor.isAuthorized()) {
			// remove from sudoer
			decryptedSudoer.set(sudoer.getUser().getUniqueID().toString(), null);
		}
		return false;
	}

}
