package de.timeout.sudo.bukkit.security;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.logging.Level;

import com.google.common.io.Files;

import de.timeout.libs.config.JsonConfig;
import de.timeout.sudo.bukkit.Sudo;
import de.timeout.sudo.bukkit.permissions.BukkitUser;
import de.timeout.sudo.groups.User;
import de.timeout.sudo.security.Sudoer;
import de.timeout.sudo.security.SudoerConfigurable;

public class BukkitSudoerManager implements SudoerConfigurable {
	
	private static final Sudo main = Sudo.getInstance();
	
	private JsonConfig decryptedSudoer;
	
	public BukkitSudoerManager() {
		// load configuration
		reloadSudoerConfig();
	}

	@Override
	public void reloadSudoerConfig() {
		// load file
		File file = new File(main.getDataFolder(), "sudoers.out");
		
		try {
			// create file if file does not exists
			if(!file.exists()) file.createNewFile();
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
			if(!sudoers.exists()) sudoers.createNewFile();
			// encode data and write into sudoer file
			Files.write(Base64.getEncoder().encodeToString(data.getBytes()), sudoers, StandardCharsets.UTF_8);
		} catch (IOException e) {
			Sudo.log().log(Level.WARNING, "&cUnable to write data in sudoer file", e);
		}
	}

	@Override
	public Sudoer addSudoer(User user, Sudoer executor) {
		// check if executor is authorized
		if(executor.isAuthorized()) {
			// upgrade User
			Sudoer superUser = new BukkitSudoer((BukkitUser) user);
			// upgrade in manager
		}
		return null;
	}

	@Override
	public void removeSudoer(Sudoer sudoer, Sudoer executor) {
	}

}
