package de.timeout.sudo.bukkit.permissions;

import java.lang.reflect.Field;

import org.bukkit.Bukkit;
import org.bukkit.permissions.PermissibleBase;
import org.bukkit.permissions.Permission;


import de.timeout.libs.BukkitReflections;
import de.timeout.libs.Reflections;
import de.timeout.sudo.users.Root;

/**
 * Permissions for Console which is the only root console
 * @author Timeout
 *
 */
class RootConsole extends PermissibleBase implements Root {
	
	private static final Class<?> servercommandsenderClass = BukkitReflections.getCraftBukkitClass("command.ServerCommandSender");
	
	private static final Field permField = Reflections.getField(servercommandsenderClass, "perm"); 
	
	private boolean root = true;
		
	/**
	 * Create a new Console and hooks into Vanilla
	 * @author Timeout
	 *
	 */
	public RootConsole() {
		super(Bukkit.getConsoleSender());
		
		// hook into bukkit vanilla
		hookBukkit();
	}
	
	/**
	 * Hooks into Vanilla
	 * @author Timeout
	 *
	 */
	private void hookBukkit() {
		// insert value in bukkits craftconsole
		Reflections.setField(permField, Bukkit.getConsoleSender(), this);
	}

	@Override
	public boolean hasPermission(Permission perm) {
		return root;
	}

	@Override
	public boolean hasPermission(String inName) {
		return root;
	}

	@Override
	public boolean isOp() {
		return root;
	}

	/**
	 * Enables Root Access of the console
	 * @author Timeout
	 * 
	 * @return true if the root could be enabled. Otherwise false.
	 */
	@Override
	public boolean enableRoot() {
		// do nothing it root is enabled
		if(!root) {
			// change value
			root = true;
			
			// return success
			return true;
		}
		return false;
	}

	/**
	 * Disables Root Access of the console
	 * @author Timeout
	 * 
	 * @return true of the root could be disable false otherwise
	 */
	@Override
	public boolean disableRoot() {
		// do nothing if root is disabled
		if(root) {
			// change value
			root = false;
			
			// return success
			return true;
		}
		return false;
	}

	@Override
	public boolean isRoot() {
		return root;
	}
}
