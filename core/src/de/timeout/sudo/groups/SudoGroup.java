package de.timeout.sudo.groups;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;

import javax.annotation.Nonnull;

import com.google.gson.JsonObject;

/**
 * Representation of the SudoGroup
 * @author Timeout
 *
 */
public abstract class SudoGroup extends Group {
	
	/**
	 * Creates a new SudoGroup
	 * @author Timeout
	 *
	 * @param permissions
	 */
	public SudoGroup(@Nonnull List<String> permissions) {
		super("sudo", permissions, null, null);
	}
	
	protected String encodeJson() {
		// create Json-File
		
		JsonObject users = new JsonObject();
		
		// add encoded passwords
		permissions.getMembers()
			.forEach(member -> users.addProperty(member.getUniqueID().toString(), member.getEncodedPassword()));
		
		// Encode to Base64 and returns the string
		return Base64.getEncoder().encodeToString(users.toString().getBytes(StandardCharsets.UTF_8));
	}
	
}
