package de.timeout.sudo.bungee.permissions;

import java.util.logging.Level;


import de.timeout.sudo.bungee.Sudo;

import net.md_5.bungee.config.Configuration;

public class ProxyGroupManager  {

	private static final Sudo main = Sudo.getInstance();
	
	public ProxyGroupManager() {
		// load groups.yml
		main.getGroupConfig().getKeys().forEach(this::loadGroup);
		// log data
		Sudo.log().log(Level.FINE, "&6groups.yml &asuccessfully loaded&7.");
	}
	
	private void loadGroup(String name) {	
		// create new group or get null if the group cannot be found
		Configuration section = main.getGroupConfig().getSection(name);
		// load group if section is found
		if(section != null) new ProxyGroup(name, section);
	}
}
