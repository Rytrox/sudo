package de.timeout.sudo.bungee.permissions;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.UUID;

import org.apache.commons.lang.Validate;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import de.timeout.libs.Reflections;
import de.timeout.sudo.users.Root;
import de.timeout.sudo.users.Sudoer;
import de.timeout.sudo.utils.PasswordCryptor;

import net.md_5.bungee.config.Configuration;

public final class ProxySudoer extends ProxyUser implements Sudoer {
	
	private static final Field sudoersConfig = Reflections.getField(ProxyUserManager.class, "decodedSudoers");
		
	private boolean authorized;
	private boolean root;
	private String password;
	
	private final String securityKey;

	/**
	 * Upgrades a ProxyUser to a ProxySudoer
	 * @author Timeout
	 *
	 * @param user the user you want to upgrade. Cannot be null
	 * @param password the password of the user. Can neither be null nor empty
	 * @throws IOException if there was an unexpected IO-Exception
	 */
	private ProxySudoer(@NotNull ProxyUser user, @NotNull String password, @NotNull String securityKey) throws IOException {
		super(user.getUniqueID());
		
		// Validate
		Validate.notEmpty(password, "Password can neither be null nor empty.");
		Validate.notEmpty(securityKey, "SecurityKey can neither be null nor empty");
		Validate.isTrue(securityKey.length() == 20, "SecurityKey must be exact 20 characters long");
		
		this.password = password;
		this.securityKey = securityKey;
	}
	
	/**
	 * Upgrades a ProxyUser to a ProxySudoer
	 * @author Timeout
	 * 
	 * @param user the user you want to upgrade
	 * @param password the password of the user. Can neither be null nor empty
	 * @param executor the executor of the command. Cannot be null
	 * @return the upgraded Sudoer
	 * @throws IllegalArgumentException if any argument is null, the password is empty or the executor is not authorized
	 * @throws IOException if there was an unexpected error while upgrading the ProxyUser
	 */
	@NotNull
	public static ProxySudoer upgradeUserToSudoer(@NotNull ProxyUser user, @NotNull String password, @NotNull Root executor, @NotNull String securityKey) throws IOException {
		// Validate
		Validate.notNull(executor, "Executor cannot be null");
		Validate.isTrue(executor.isRoot(), "Executor must be root for creating a new Sudoer");
		
		// create a new ProxySudoer
		ProxySudoer sudoer = new ProxySudoer(user, password, securityKey);
		// add sudoer to sudo group
		main.getGroupManager().getSudoGroup().add(sudoer, executor);
		
		// return the new sudoer
		return sudoer;
	}
	
	/**
	 * Loads a sudoer from the sudoer config
	 * @author Timeout
	 * 
	 * @param user the user you want to load
	 * @return the sudoer if it contains in
	 * @throws IOException
	 */
	@Nullable
	public static ProxySudoer loadSudoerFromFile(@NotNull ProxyUser user, @NotNull Root executor, @NotNull String securityKey) throws IOException {
		// Validate
		Validate.notNull(user, "ProxyUser cannot be null");
		Validate.notNull(executor, "Executor cannot be null");
		Validate.isTrue(executor.isRoot(), "Executor must be root for loading a Sudoer");
		// get Configuration
		Configuration config = getSudoerConfiguration(user.getUniqueID());
		// if it is a sudoer
		if(config != null) {
			// create sudoer
			ProxySudoer sudoer = new ProxySudoer(user, config.getString("password"), securityKey);
			// add sudoer to sudo group
			main.getGroupManager().getSudoGroup().add(sudoer, executor);
			
			// return sudoer
			return sudoer;
		} else return null;
	}
	
	/**
	 * Returns the correct section of the sudoer
	 * @author Timeout
	 * 
	 * @param uuid the uuid of the user. Cannot be null
	 * @return the correct section or null if the section does not exists
	 */
	@Nullable
	private static Configuration getSudoerConfiguration(UUID uuid) {
		// get Sudoers
		Configuration sudoers = (Configuration) Reflections.getValue(sudoersConfig, main.getUserManager());
		// return configuration if exists, otherwise null
		return sudoers.contains(uuid.toString()) ? sudoers.getSection(uuid.toString()) : null;
	}

	@Override
	public boolean authorize(String password) {
		// ignore authorization before
		authorized = PasswordCryptor.authenticate(password, this.password);
		// return authorized
		return authorized;
	}

	@Override
	public void deauthorize() {
		authorized = false;
	}

	@Override
	public boolean isAuthorized() {
		return authorized;
	}

	@Override
	public boolean setPassword(String password, Sudoer executor) {
		// do nothing if executor is not authorized
		if(executor.isAuthorized()) {
			// renew password
			this.password = password;
			// return success
			return true;
		}
		return false;
	}

	@Override
	public boolean hasPermission(String permission) {
		// check if permission is in Sudo-Group
		return !main.getGroupManager().getSudoGroup().hasPermission(permission) ? super.hasPermission(permission) : isAuthorized();
	}

	@Override
	public boolean enableRoot() {
		// return false if root is already enabled
		if(!root) {
			// change value
			root = true;
			// return success
			return true;
		} else return false;
	}

	@Override
	public boolean disableRoot() {
		// return false if root is already disabled
		if(root) {
			// change value
			root = false;
			// return success
			return true;
		} else return false;
	}

	@Override
	public boolean isRoot() {
		return root && isAuthorized();
	}

	@Override
	public String getRootKey() {
		return securityKey;
	}
}
