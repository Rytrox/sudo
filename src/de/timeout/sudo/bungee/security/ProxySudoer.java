package de.timeout.sudo.bungee.security;

import java.io.IOException;

import javax.annotation.Nonnull;

import org.apache.commons.lang.Validate;

import de.timeout.sudo.bungee.permissions.ProxyUser;
import de.timeout.sudo.groups.User;
import de.timeout.sudo.security.Sudoer;
import de.timeout.sudo.utils.PasswordCryptor;

public final class ProxySudoer extends ProxyUser implements Sudoer {
	
	private boolean authorized;
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
	public static ProxySudoer upgradeUserToSudoer(@Nonnull ProxyUser user, @Nonnull String password, @Nonnull Sudoer executor) throws IOException {
		// Validate
		Validate.notNull(user, "ProxyUser cannot be null");
		Validate.notEmpty(password, "Password can neither be null nor empty.");
		Validate.notNull(executor, "Executor cannot be null");
		Validate.isTrue(executor.isAuthorized(), "Executor must be authorize for creating a new Sudoer");
		
		// return the new sudoer
		return new ProxySudoer(user, password);
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
	public User getUser() {
		return this;
	}

	@Override
	public boolean hasPermission(String permission) {
		// check if permission is in Sudo-Group
		return super.hasPermission(permission);
	}
}
