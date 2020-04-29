package de.timeout.sudo.bukkit.security;

import java.lang.reflect.Field;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.permissions.Permission;

import de.timeout.libs.Reflections;
import de.timeout.libs.config.JsonConfig;
import de.timeout.sudo.bukkit.Sudo;
import de.timeout.sudo.bukkit.permissions.BukkitUser;
import de.timeout.sudo.security.Root;
import de.timeout.sudo.security.Sudoer;
import de.timeout.sudo.utils.PasswordCryptor;

/**
 * Represents a User which can use the Sudo-Command
 * @author Timeout
 *
 */
public final class BukkitSudoer extends BukkitUser implements Sudoer {
	
	private static final Sudo main = Sudo.getInstance();
	
	private static final Field decodedsudoersField = Reflections.getField(BukkitSudoerManager.class, "decodedSudoers");
	
	private boolean authorized;
	private boolean root;
	private String password;
	
	/**
	 * Sudoer Constructor to upgrade a BukkitUser to BukkitSudoer
	 * @author Timeout
	 *
	 * @param user the user you want to upgrade
	 */
	private BukkitSudoer(@Nonnull BukkitUser user, String password) {
		super(Bukkit.getOfflinePlayer(user.getUniqueID()));
		// write attributes in upgrade
		this.prefix = user.getPrefix();
		this.suffix = user.getSuffix();
		this.groups.addAll(user.getGroups());
		user.getPermissions().forEach(this::addPermission);
		// write password
		this.password = password;
	}
	
	/**
	 * Creates a new Sudoer of an already existing Bukkit-User
	 * @author Timeout
	 * 
	 * @param user the user you want to upgrade. Cannot be null
	 * @param password the password of the user. Cannot be null or empty
	 * @param executor the authorized executor
	 * @throws IllegalArgumentException if any argument is null or the executor is not authorized
	 * @return the new BukkitSudoer
	 */
	public static BukkitSudoer upgradeUserToSudoer(@Nonnull BukkitUser user, @Nonnull String password, @Nonnull Root executor) {
		// Validate
		Validate.notNull(user, "BukkitUser cannot be null");
		Validate.notNull(password, "Sudoer cannot be null");
		Validate.notNull(executor, "Executor cannot be null");
		Validate.isTrue(executor.isRoot(), "Executor must be root to create a new sudoer");
		
		// create new BukkitSudoer
		BukkitSudoer sudoer = new BukkitSudoer(user, password);
		// add to sudo group
		main.getSudoerManager().getSudoGroup().join(sudoer);
		
		// return sudoer
		return sudoer;
	}
	
	/**
	 * Loads a Sudoer from the configuration. Only works if the server runs in Bukkit-Mode
	 * @author Timeout
	 * 
	 * @param user the user you want to load
	 * @return the superuser of the user
	 */
	@Nullable
	public static BukkitSudoer loadSudoerFromConfiguration(@Nonnull BukkitUser user) {
		// Validate
		Validate.notNull(user, "User cannot be null");
		Validate.isTrue(!main.bungeecordEnabled(), "File-Support is disabled while using BungeeCord-Mode!");
		
		// check if user is not already a sudoer
		if(!(user instanceof BukkitSudoer)) {
			// get Configuration from user
			JsonConfig sudoers = (JsonConfig) Reflections.getValue(decodedsudoersField, main.getSudoerManager());
			// get ConfigurationSection
			ConfigurationSection section = sudoers.getConfigurationSection(user.getUniqueID().toString());
			
			// return null if the section is found
			if(section != null) {
				// create Sudoer
				BukkitSudoer sudoer = new BukkitSudoer(user, section.getString("password"));
				// add to sudo group
				main.getSudoerManager().getSudoGroup().join(sudoer);
			}
			return null;
		} else return (BukkitSudoer) user;
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
		// Validate
		Validate.notNull(executor, "Executor cannot be null");
		Validate.notEmpty(password, "Password cannot be null or empty");
		// do nothing if the executor is not null
		if(executor.isAuthorized()) {
			// renew password
			this.password = PasswordCryptor.encode(password);
			// return success
			return true;
		}
		return false;
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
	public boolean isOp() {
		return root && isAuthorized();
	}

	@Override
	public boolean hasPermission(Permission perm) {
		return this.hasPermission(perm.getName());
	}

	@Override
	public boolean hasPermission(String inName) {
		// return true if user is authorized
		return isOp() || super.hasPermission(inName);
	}
}
