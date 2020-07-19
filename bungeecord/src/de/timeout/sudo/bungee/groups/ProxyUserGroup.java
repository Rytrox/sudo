package de.timeout.sudo.bungee.groups;

import de.timeout.sudo.bungee.Sudo;
import de.timeout.sudo.groups.UserGroup;

import net.md_5.bungee.config.Configuration;

public class ProxyUserGroup extends UserGroup {
	
	private static final Sudo main = Sudo.getInstance();
		
	public ProxyUserGroup(String name, Configuration groupConfiguration) {
		super(name, groupConfiguration.getString("options.prefix"),
				groupConfiguration.getString("options.suffix"),
				groupConfiguration.getBoolean("options.default"));
		// load permissions
		groupConfiguration.getStringList("permissions").forEach(this::addPermission);
	}
	
	/**
	 * Creates a new ProxyGroup
	 * @author Timeout
	 *
	 * @param name the name of the group
	 * @throws IllegalArgumentException if the name equals sudo
	 */
	public ProxyUserGroup(String name) {
		super(name, "", "", false);
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
