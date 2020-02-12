package de.timeout.sudo.bungee.connectors;

import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.plugin.Listener;

public class BukkitMessageHandler implements Listener {
	
	public void sendPluginMessage(ServerInfo server, byte[] message) {
		server.sendData("sudo", message);
	}

}
