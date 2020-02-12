package de.timeout.sudo.bungee.groups;

import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;

import org.apache.commons.lang.Validate;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import de.timeout.sudo.groups.User;
import de.timeout.sudo.utils.PasswordCryptor;

public final class ProxySudoGroup {

	private final Map<UUID, String> passwords = new TreeMap<>();
	
	public ProxySudoGroup(JsonArray sudoers) {
		// for each user
		sudoers.forEach(user -> {
			// get Json object
			JsonObject userObj = user.getAsJsonObject();
			// add to map
			passwords.put(UUID.fromString(userObj.get("uuid").getAsString()), userObj.get("password").getAsString());
		});
	}

	/**
	 * Adds a user to sudoers
	 * 
	 * @param user the user to add
	 * @param password the password of the user
	 * @return if it succeed
	 */
	public boolean join(User user, String password) {
		// validate
		Validate.notNull(user, "User cannot be null");
		Validate.notNull(password, "Password cannot be null");
		// if user is not a sudoer
		if(!passwords.containsKey(user.getUniqueID())) {
			// encode password
			String passwordEncoded = PasswordCryptor.encode(password);
			// set element in passwords
			passwords.put(user.getUniqueID(), passwordEncoded);
			// return true
			return true;
		}
		// return false. User is already member
		return false;
	}

	
	public boolean kick(User user) {
		// Validate
		Validate.notNull(user, "User cannot be null");
		// remove from sudoers
		return passwords.remove(user.getUniqueID()) != null;
	}

	public boolean isMember(User user) {
		return passwords.containsKey(user.getUniqueID());
	}

}
