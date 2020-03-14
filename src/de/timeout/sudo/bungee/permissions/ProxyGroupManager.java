package de.timeout.sudo.bungee.permissions;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;

import org.bukkit.entity.Player;

import de.timeout.sudo.bungee.Sudo;
import de.timeout.sudo.groups.User;

import net.md_5.bungee.api.event.PlayerHandshakeEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.event.EventHandler;

public class ProxyGroupManager implements Listener {

	private static final Sudo main = Sudo.getInstance();
	
	private final Map<Player, User> profiles = new HashMap<>();
	
	public ProxyGroupManager() {
		// register Listener
		main.getProxy().getPluginManager().registerListener(main, this);
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
	
	@EventHandler
	public void onGroupLoad(PlayerHandshakeEvent event) {
		// get User 
		User user = new ProxyUser(event.getConnection());
	}
}
