package de.timeout.sudo.bungee.security;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.logging.Level;

import com.google.common.io.Files;

import de.timeout.libs.config.JsonConfig;
import de.timeout.sudo.bungee.Sudo;
import de.timeout.sudo.groups.User;
import de.timeout.sudo.security.Sudoer;
import de.timeout.sudo.security.SudoerConfigurable;

/**
 * Manager class for handling sudoers
 * @author Timeout
 *
 */
public class ProxySudoerManager implements SudoerConfigurable {

	private static final Sudo main = Sudo.getInstance();
		
	private JsonConfig decodedSudoers;
	
	public ProxySudoerManager() {
		reloadSudoerConfig();
	}
	
	@Override
	public void reloadSudoerConfig() {
		try {
			decodedSudoers = new JsonConfig(new String(
					Base64.getDecoder().decode(
							String.join("", Files.readLines(new File(main.getDataFolder(), "sudoers.out"), StandardCharsets.UTF_8)))));
		} catch (IOException e) {
			Sudo.log().log(Level.WARNING, "&cUnable to read sudoers.out", e);
		}
	}

	@Override
	public void saveSudoerConfig() {
		// define new file
		File file = new File(main.getDataFolder(), "sudoers.out");
		
		try {
			// creates parents dir
			Files.createParentDirs(file);
			// create file
			Files.touch(file);
			// write data into file
			Files.write(Base64.getEncoder().encodeToString(decodedSudoers.saveToString().getBytes()), file, StandardCharsets.UTF_8);
		} catch (IOException e) {
			Sudo.log().log(Level.WARNING, "&cUnable to write sudoers.out");
		}
	}

	@Override
	public Sudoer addSudoer(User user, String password, Sudoer executor) {
		// do nothing if the executor is not authotired
		if(executor.isAuthorized()) {
			
		}
		return null;
	}

	@Override
	public boolean removeSudoer(Sudoer sudoer, Sudoer executor) {
		return false;
	}

}
