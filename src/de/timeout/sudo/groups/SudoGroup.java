package de.timeout.sudo.groups;

import java.util.List;
import java.util.Set;

import javax.annotation.Nonnull;

import org.apache.commons.lang.Validate;

import com.google.gson.JsonObject;

import de.timeout.sudo.users.Root;
import de.timeout.sudo.users.Sudoer;

/**
 * Representation of the SudoGroup
 * @author Timeout
 *
 */
public final class SudoGroup extends Group {
	
	/**
	 * Creates a new SudoGroup
	 * @author Timeout
	 *
	 * @param permissions
	 */
	public SudoGroup(@Nonnull List<String> permissions) {
		super("sudo");
		// add all permissions
		permissions.forEach(this.permissions::add);
	}
	
	/**
	 * Adds a permission to this group. <br>
	 * Returns false if the permission is null
	 * 
	 * @param permission the permission to add
	 * @param executor the executor of the method
	 * @return if it succeed
	 */
	public boolean addPermission(String permission, Root executor) {
		// executes only if the executor is authorized
		if(executor.isRoot()) {
			// add permission if permission is not null
			return permission != null && !permission.isEmpty() && permissions.add(permission);
		}
		return false;
	}

	/**
	 * Remove a permission from this group. <br>
	 * Returns false if the permission is null
	 * 
	 * @param permission the permission to remove
	 * @param executor the executor of the method
	 * @return if it succeed
	 */
	public boolean removePermission(String permission, Root executor) {
		// executes only if the executor is authorized
		if(executor.isRoot()) {
			// remove permission if permission is not null
			return permission != null && permissions.remove(permission);
		}
		return false;
	}

	@Override
	public boolean hasPermission(String permission) {
		return permission != null && !permission.isEmpty() && permissions.contains(permission);
	}

	@Override
	public Set<String> getPermissions() {
		return permissions.toSet();
	}

	@Override
	public JsonObject toJson() {
		return null;
	}

	/**
	 * Adds a user to this group. <br>
	 * Returns false if the user is null
	 * 
	 * @param user the user
	 * @param executor the executor of the method
	 * @return true if the remove succeed else false
	 */
	public boolean join(Sudoer element, Root executor) {
		// Validate
		Validate.notNull(executor, "Executor cannot be null");
		// only continue if the executor is 
		if(element != null && executor.isRoot()) {
			// add to members
			return members.add(element);
		}
		return false;
	}

	/**
	 * Removes a user from this group <br>
	 * Returns false if the user is null
	 * 
	 * @param user the user
	 * @param executor the executor of the method
	 * @return true if the remove succeed else false
	 */
	public boolean kick(Sudoer element, Root executor) {
		// Validate
		Validate.notNull(executor, "Executor cannot be null");
		// only continue if executor is root
		if(element != null && executor.isRoot()) {
			// add to members
			return members.remove(element);
		}
		return false;
	}

}
