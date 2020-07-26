package de.timeout.sudo.bungee.groups;

import java.io.IOException;

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
	public void save() throws IOException {
		// Save sudoers here!
	}

}
