package de.timeout.sudo.bungee.security;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.logging.Level;

import javax.annotation.Nonnull;

import com.google.common.io.Files;

import de.timeout.libs.Reflections;
import de.timeout.sudo.bungee.Sudo;
import de.timeout.sudo.security.SudoGroup;
import de.timeout.sudo.security.SudoerConfigurable;

import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.JsonConfiguration;

/**
 * Manager class for handling sudoers
 * @author Timeout
 *
 */
public class ProxySudoerManager implements SudoerConfigurable {
	
	private static final Field passwordField = Reflections.getField(ProxySudoer.class, "password");

	private static final Sudo main = Sudo.getInstance();
	private final SudoGroup sudo;
	
	private Configuration decodedSudoers;
	
	/**
	 * Create a new ProxySudoerManager
	 * @author Timeout
	 *
	 */
	public ProxySudoerManager() {
		reloadSudoerConfig();
		// load sudo group
		sudo = new SudoGroup(main.getConfig().getStringList("sudo.permissions"));
	}
	
	@Override
	public void reloadSudoerConfig() {
		// load configuration
		try {
			decodedSudoers = ConfigurationProvider.getProvider(JsonConfiguration.class).load(new String(
						Base64.getDecoder().decode(
								String.join("", Files.readLines(new File(main.getDataFolder(), "sudoers.out"),
										StandardCharsets.UTF_8)))));
		} catch (IOException e) {
			Sudo.log().log(Level.WARNING, "&cUnable to read sudoers.out", e);
		}
	}

	@Override
	public void saveSudoerConfig() {
		// save all new users in sudoers config
		sudo.getSudoers().forEach(sudoer -> {
			// create a new Configuration
			decodedSudoers.set(String.format("%s.password", sudoer.getUniqueID().toString()), Reflections.getValue(passwordField, sudoer));
		});
		
		// define new file
		File file = new File(main.getDataFolder(), "sudoers.out");
		
		try {
			// creates parents dir
			Files.createParentDirs(file);
			// create file
			Files.touch(file);
			// write data into file
			ConfigurationProvider.getProvider(JsonConfiguration.class).save(decodedSudoers, file);
			// encode data
			Files.write(Base64.getEncoder().encodeToString(Files.toByteArray(file)), file, StandardCharsets.UTF_8);
		} catch (IOException e) {
			Sudo.log().log(Level.WARNING, "&cUnable to write sudoers.out");
		}
	}
		
	/**
	 * Returns the Sudo group. Cannot be null
	 * @author Timeout
	 * 
	 * @return the sudo group. Cannot be null
	 */
	@Nonnull
	public SudoGroup getSudoGroup() {
		return sudo;
	}
}
