package de.timeout.sudo.bungee.security;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.UUID;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.commons.lang.Validate;

import de.timeout.libs.Reflections;
import de.timeout.sudo.bungee.permissions.ProxyUser;
import de.timeout.sudo.users.Root;
import de.timeout.sudo.users.Sudoer;
import de.timeout.sudo.utils.PasswordCryptor;

import net.md_5.bungee.config.Configuration;

public final class ProxySudoer extends ProxyUser implements Sudoer {
	
	private static final Field sudoersConfig = Reflections.getField(ProxyUserManager.class, "decodedSudoers");
		
	private boolean authorized;
	private boolean root;
	private String password;

	/**
	 * Upgrades a ProxyUser to a ProxySudoer
	 * @author Timeout
	 *
	 * @param user the user you want to upgrade. Cannot be null
	 * @param password the password of the user. Can neither be null nor empty
	 * @throws IOException if there was an unexpected IO-Exception
	 */
	private ProxySudoer(ProxyUser user, @Nonnull String password) throws IOException {
		super(user.getUniqueID());
		// Validate
		Validate.notEmpty(password, "Password can neither be null nor empty.");
		this.password = password;
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
	@Nonnull
	public static ProxySudoer upgradeUserToSudoer(@Nonnull ProxyUser user, @Nonnull String password, @Nonnull Root executor) throws IOException {
		// Validate
		Validate.notNull(user, "ProxyUser cannot be null");
		Validate.notEmpty(password, "Password can neither be null nor empty.");
		Validate.notNull(executor, "Executor cannot be null");
		Validate.isTrue(executor.isRoot(), "Executor must be root for creating a new Sudoer");
		
		// create a new ProxySudoer
		ProxySudoer sudoer = new ProxySudoer(user, password);
		// add sudoer to sudo group
		main.getGroupManager().getSudoGroup().join(sudoer, executor);
		
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
	public static ProxySudoer loadSudoerFromFile(@Nonnull ProxyUser user, @Nonnull Root executor) throws IOException {
		// Validate
		Validate.notNull(user, "ProxyUser cannot be null");
		Validate.notNull(executor, "Executor cannot be null");
		Validate.isTrue(executor.isRoot(), "Executor must be root for loading a Sudoer");
		// get Configuration
		Configuration config = getSudoerConfiguration(user.getUniqueID());
		// if it is a sudoer
		if(config != null) {
			// create sudoer
			ProxySudoer sudoer = new ProxySudoer(user, config.getString("password"));
			// add sudoer to sudo group
			main.getGroupManager().getSudoGroup().join(sudoer, executor);
			
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
}
