package de.timeout.sudo.bukkit.security;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.logging.Level;

import javax.annotation.Nonnull;

import com.google.common.io.Files;

import de.timeout.libs.config.JsonConfig;
import de.timeout.sudo.bukkit.Sudo;
import de.timeout.sudo.security.SudoGroup;
import de.timeout.sudo.security.SudoerConfigurable;

public class BukkitSudoerManager implements SudoerConfigurable {

	private static final Sudo main = Sudo.getInstance();
	private final SudoGroup sudo;

	private JsonConfig decodedSudoer;
	
	public BukkitSudoerManager() {
		reloadSudoerConfig();
		// load sudo group (load permissions from file in Bukkit-Mode
		sudo = new SudoGroup(main.bungeecordEnabled() ? new ArrayList<>() : main.getConfig().getStringList("sudo.permissions"));
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
		// do nothing if bungeecord mode is enabled
		if(!main.bungeecordEnabled()) {
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
	}
	
	/**
	 * Returns the sudo group. Cannot be null
	 * @author Timeout
	 * 
	 * @return the sudo group. Cannot be null
	 */
	@Nonnull
	public SudoGroup getSudoGroup() {
		return sudo;
	}
}
