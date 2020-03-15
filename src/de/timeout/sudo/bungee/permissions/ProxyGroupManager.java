package de.timeout.sudo.bungee.permissions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;

import de.timeout.sudo.bungee.Sudo;
import de.timeout.sudo.groups.User;

import net.md_5.bungee.api.event.PlayerHandshakeEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.event.EventHandler;

public class ProxyGroupManager implements Listener {

	private static final Sudo main = Sudo.getInstance();
	
	private final Map<UUID, User> profiles = new HashMap<>();
	
	public ProxyGroupManager() {
		// register Listener
		main.getProxy().getPluginManager().registerListener(main, this);
		// load groups.yml
		main.getGroupConfig().getKeys().forEach(this::loadGroup);
		// log data
		Sudo.log().log(Level.FINE, "&6groups.yml &asuccessfully loaded&7.");
	}
	
	/**
	 * Returns a list with all online users
	 * @author Timeout
	 * 
	 * @return a list containing a
	 */
	public List<User> getAllLoggedUsers() {
		// create new list
		List<User> onlineUsers = new ArrayList<>();
		// run through onlineplayers
		main.getProxy().getPlayers().forEach(player -> onlineUsers.add(profiles.get(player.getUniqueId())));
		// return list
		return onlineUsers;
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
