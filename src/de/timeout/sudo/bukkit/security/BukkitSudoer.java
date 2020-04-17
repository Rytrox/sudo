package de.timeout.sudo.bukkit.security;

import javax.annotation.Nonnull;

import org.bukkit.Bukkit;

import de.timeout.sudo.bukkit.permissions.BukkitUser;
import de.timeout.sudo.groups.User;
import de.timeout.sudo.security.Sudoer;
import de.timeout.sudo.utils.PasswordCryptor;

/**
 * Represents a User which can use the Sudo-Command
 * @author Timeout
 *
 */
public class BukkitSudoer extends BukkitUser implements Sudoer {
	
	private boolean authorized;
	private String password;
	
	/**
	 * Sudoer Constructor to upgrade a BukkitUser to BukkitSudoer
	 * @author Timeout
	 *
	 * @param user the user you want to upgrade
	 */
	public BukkitSudoer(@Nonnull BukkitUser user, String password) {
		super(Bukkit.getOfflinePlayer(user.getUniqueID()));
		// write attributes in upgrade
		this.prefix = user.getPrefix();
		this.suffix = user.getSuffix();
		this.groups.addAll(user.getGroups());
		user.getPermissions().forEach(this::addPermission);
		// write password
		this.password = password;
	}
	
	@Override
	public boolean isAuthorized() {
		return authorized;
	}

	@Override
	public boolean authorize(String password) {
		// only perform if user is not authorized
		if(!authorized) authorized = PasswordCryptor.authenticate(password, this.password);
		// return authorized
		return authorized;
	}

	@Override
	public void deauthorize() {
		authorized = false;
	}

	@Override
	public boolean setPassword(String password, Sudoer executor) {
		return false;
	}

	@Override
	public User getUser() {
		return this;
	}
}
