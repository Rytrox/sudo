package de.timeout.sudo.bungee.groups;

import de.timeout.sudo.bungee.Sudo;
import de.timeout.sudo.groups.SudoGroup;

/**
 * Proxy implementation of group sudo
 * @author Timeout
 *
 */
public final class ProxySudoGroup extends SudoGroup {
	
	private static final Sudo main = Sudo.getInstance();

	/**
	 * Creates the Sudo-Group
	 */
	public ProxySudoGroup() {
		super(main.getConfig().getStringList("sudo.permissions"));
	}

	@Override
	public boolean addPermission(String permission) {
		// set console user root
		boolean root = main.getUserManager().getConsoleUser().enableRoot();
		boolean result = this.addPermission(permission, main.getUserManager().getConsoleUser());
		
		// deactivate root if its enabled by this method
		if(root) main.getUserManager().getConsoleUser().disableRoot();
		
		return result;
	}

	@Override
	public boolean removePermission(String permission) {
		// set console user root
		boolean root = main.getUserManager().getConsoleUser().enableRoot();
		boolean result = this.removePermission(permission, main.getUserManager().getConsoleUser());
		
		// deactivate root if its enabled by this method
		if(root) main.getUserManager().getConsoleUser().disableRoot();
		
		return result;
	}

}
