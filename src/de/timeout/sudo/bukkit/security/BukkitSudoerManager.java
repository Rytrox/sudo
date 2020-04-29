package de.timeout.sudo.bukkit.security;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.logging.Level;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.commons.lang.Validate;

import com.google.common.io.Files;

import de.timeout.libs.config.JsonConfig;
import de.timeout.sudo.bukkit.Sudo;
import de.timeout.sudo.bukkit.permissions.BukkitUser;
import de.timeout.sudo.groups.SudoGroup;
import de.timeout.sudo.groups.User;
import de.timeout.sudo.security.Root;
import de.timeout.sudo.security.Sudoer;
import de.timeout.sudo.security.SudoerConfigurable;

public class BukkitSudoerManager implements SudoerConfigurable {

	private static final Sudo main = Sudo.getInstance();
	private final SudoGroup sudo;
	private final Root console;

	private JsonConfig decodedSudoer;
	
	public BukkitSudoerManager() {
		reloadSudoerConfig();
		// load sudo group (load permissions from file in Bukkit-Mode
		sudo = new SudoGroup(main.bungeecordEnabled() ? new ArrayList<>() : main.getConfig().getStringList("sudo.permissions"));
		
		// load console
		console = new RootConsole();
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
	 * Loads a SuperUser from the sudoers file and upgrades it in group system if the sudoer could be loaded
	 * @author Timeout
	 * 
	 * @param user the user you want to load
	 * @throws IllegalArgumentException if the user is null or the server is running BungeeCord-Mode
	 * @return the sudoer of the user or null if the user is null
	 */
	@Nullable
	public Sudoer loadSudoerFromConfiguration(@Nonnull User user) {
		Validate.notNull(user, "User cannot be null");
		Validate.isTrue(!main.bungeecordEnabled(), "File-Support is only enabled while server is running in Bukkit-Mode");
		Validate.isTrue(user instanceof BukkitUser, "User is not an instance of BukkitUser");
		
		// do nothing if the user is already a superuser
		if(!(user instanceof Sudoer)) {
			// load Sudoer
			Sudoer sudoer = BukkitSudoer.loadSudoerFromConfiguration((BukkitUser) user);
			// update in group manager if sudoer could be loaded
			if(sudoer != null) main.getGroupManager().upgradeUser(sudoer, console);
			// return sudoer
			return sudoer;
		} else return (Sudoer) user;
	}
	
	/**
	 * Upgrades a User to a Sudoer
	 * @author Timeout
	 * 
	 * @param user the user you want to upgrade. Cannot be null
	 * @param password the password of the user. Cannot be null nor empty
	 * @param executor the executor of the command. Cannot be null
	 * @throws IllegalArgumentException if any argument is null, the password is empty or {@link Root#isRoot()} returns false
	 * @return the Sudoer of the user. Cannot be null
	 */
	@Nonnull
	public Sudoer upgradeUserToSudoer(@Nonnull User user, @Nonnull String password, @Nonnull Root executor) {
		// Validate
		Validate.notNull(user, "User cannot be null");
		Validate.notNull(executor, "Executor cannot be null");
		Validate.notEmpty(password, "Password can neither be null nor empty");
		Validate.isTrue(executor.isRoot(), "Unable to acquire the sudo frontend lock. Are you root?");
		Validate.isTrue(user instanceof BukkitUser, "User must be an instance of BukkitUser");
		
		// create Sudoer
		Sudoer sudoer = BukkitSudoer.upgradeUserToSudoer((BukkitUser) user, password, executor);
		// upgrade in group manager
		main.getGroupManager().upgradeUser(sudoer, executor);
		// return user
		return sudoer;
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
