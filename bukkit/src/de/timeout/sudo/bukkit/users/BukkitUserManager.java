package de.timeout.sudo.bukkit.users;

import java.io.IOException;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import de.timeout.sudo.bukkit.Sudo;
import de.timeout.sudo.users.User;
import de.timeout.sudo.users.UserManager;

public class BukkitUserManager extends UserManager {
	
	protected static final Sudo main = Sudo.getInstance();
	
	public BukkitUserManager() {
		super(new RootConsole());
	}
	
	/**
	 * Get a user from cache if the user is loaded
	 * @author Timeout
	 * 
	 * @param player the player. Cannot be null
	 * @throws IllegalArgumentException if the player is null
	 * @return the user profile of the player. Returns null if the profile is not loaded yet
	 */
	@Nullable
	public User getUser(@Nonnull OfflinePlayer player) {
		// Validate
		Validate.notNull(player);
		return getUser(player.getUniqueId());
	}
	
	@Override
	public void changePassword(User user, String password) throws IOException {
//		Validate.notNull(user, "User cannot be null");
//		Validate.notEmpty(password, "Password can neither be null nor empty");
//		
//		// create Sudoer
//		Sudoer sudoer;
//		
//		if(!main.bungeecordEnabled()) {
//			sudoer = BukkitSudoer.upgradeUserToSudoer((BukkitUser) user, password, executor);
//			
//			// write data in sudoers
//			decodedSudoer.set(user.getUniqueID().toString(), PasswordCryptor.encode(password));
//		} else sudoer = BukkitSudoer.upgradeUserToSudoer((BukkitUser) user, executor);
//		
//		// update profile
//		this.profiles.replace(user.getUniqueID(), sudoer);
//		
//		// return sudoer
//		return sudoer;
	}
	



	@Override
	public void unloadUser(User user) {
		// Validate
		Validate.notNull(user, "User cannot be null");
		
		// check if user is not online
		if(Bukkit.getPlayer(user.getUniqueID()) == null) {
			// remove from all groups
			user.getPermissionContainer().getMembers().forEach(user::leaveGroup);
			
			// remove from sudogroup if user is a sudoer
			main.getGroupManager().getSudoGroup().removeMember(user);
			
			// remove from cache
			profiles.remove(user.getUniqueID());
		} else throw new IllegalStateException("Unable to unload a player who is still online!");
	}
}
