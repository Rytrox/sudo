package de.timeout.sudo.bungee.groups;

import org.apache.commons.lang.Validate;

import com.google.gson.JsonArray;

public class GroupManager {

	private static final ProxySudoGroup sudo = new ProxySudoGroup(loadSudoers());
	
	public GroupManager() {
		
	}
	
	/**
	 * Check if 
	 * @param user
	 * @return
	 */
	public boolean isSudoer(ProxyUser user) {
		// validate
		Validate.notNull(user, "User cannot be null");
		return sudo.isMember(user);
	}
	
	private static JsonArray loadSudoers() {
		return null;
	}
}
