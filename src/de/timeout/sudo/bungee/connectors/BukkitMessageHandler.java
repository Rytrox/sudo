package de.timeout.sudo.bungee.connectors;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.UUID;
import java.util.logging.Level;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import de.timeout.sudo.bungee.Sudo;
import de.timeout.sudo.groups.BaseGroup;

import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.Connection;
import net.md_5.bungee.api.connection.Server;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;
import net.md_5.bungee.event.EventHandler;

public class BukkitMessageHandler implements Listener {
	
	private static final Sudo main = Sudo.getInstance();
	
	private UUID uuid;
	
	public BukkitMessageHandler() {
		// register listener
		main.getProxy().getPluginManager().registerListener(main, this);
		// get uuid of proxy
		try {
			uuid = UUID.fromString(
					ConfigurationProvider.getProvider(YamlConfiguration.class)
						.load(new File(main.getDataFolder().getParentFile(), "")).getString("stats"));
		} catch (IOException e) {
			Sudo.log().log(Level.WARNING, "&cUnable to load uuid from BungeeCord config.yml.", e);
		}
	}
	
	public void sendPluginMessage(ServerInfo server, byte[] message) {
		server.sendData("sudo", message);
	}

	@EventHandler
	public void onServerLogin(PluginMessageEvent event) {
		// get Sender
		Connection sender = event.getSender();
		// check if channel is sudo channel
		if(event.getTag().equalsIgnoreCase("sudo")) {
			// get Message input
			DataInputStream in = new DataInputStream(new ByteArrayInputStream(event.getData()));
			// get subchannel
			try {
				String subchannel = in.readUTF();
				// check if subchannel is login channel
				if("login".equalsIgnoreCase(subchannel) && sender instanceof Server) {
					ServerInfo server = ((Server) sender).getInfo();
					// create holder for data input
					JsonObject data = new JsonObject();
					// send error if uuid is not equal
					if(uuid.compareTo(UUID.fromString(in.readUTF())) == 0) {
						// log result
						Sudo.log().log(Level.INFO, "&2Bungeecord &7received a Login-Message with a &acorrent uuid&7. &eSending data...");
						// write result in data
						data.addProperty("code", 200);
						data.addProperty("message", "OK");
						
						// create array for groups
						JsonArray groups = new JsonArray();
						// write each group in array
						BaseGroup.getGroups().forEach(group -> groups.add(group.toJson()));
						// add array to data
						data.add("groups", groups);
						
						// create array for users
						JsonArray users = new JsonArray();
						// write each user in array
						main.getGroupManager().getAllLoggedUsers().forEach(user -> users.add(user.toJson()));
						// add array to data
						data.add("users", users);
					} else {
						// log false uuid
						Sudo.log().log(Level.INFO, String.format("&2BungeeCord &7received a Login-Message with an &cincorrect uuid &7from &5%s&7."
								+ " &cPlease check your settings if you believe this is an error."
								+ " IP will be blocked."
								+ " Unblock it with /sudo unblock ip", server.getSocketAddress().toString()));
						
						// write result in json data
						data.addProperty("code", 412);
						data.addProperty("message", "Incorrect Proxy-UniqueID");
					}
					
					// send json data back to server
					ByteArrayOutputStream stream = new ByteArrayOutputStream();
					DataOutputStream out = new DataOutputStream(stream);
					out.writeUTF("login");
					out.writeUTF(data.toString());
					sendPluginMessage(server, stream.toByteArray());
				}
			} catch (IOException e) {
				Sudo.log().log(Level.WARNING, "&cUnable to read PluginMessageChannel", e);
			}
		}
	}
}
