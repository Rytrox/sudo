package de.timeout.sudo.bungee.groups;

import java.io.IOException;
import java.util.ArrayList;

import de.timeout.sudo.groups.UserGroup;

import net.md_5.bungee.config.Configuration;

public class ProxyUserGroup extends UserGroup {
			
	public ProxyUserGroup(String name, Configuration groupConfiguration) {
		super(name, groupConfiguration.getString("options.prefix"),
				groupConfiguration.getString("options.suffix"),
				groupConfiguration.getBoolean("options.default"),
				groupConfiguration.getStringList("permissions"));
	}
	
	/**
	 * Creates a new ProxyGroup
	 * @author Timeout
	 *
	 * @param name the name of the group
	 * @throws IllegalArgumentException if the name equals sudo
	 */
	public ProxyUserGroup(String name) {
		super(name, "", "", false, new ArrayList<>());
	}

	@Override
	public boolean addPermission(String permission) {
		return this.addPermission(permission);
	}

	@Override
	public boolean removePermission(String permission) {
		// set console user root
		return this.removePermission(permission);
	}

	@Override
	public void save() throws IOException {
		// TODO Auto-generated method stub
		
	}
}
