package de.timeout.sudo.bukkit.permissions;

import java.util.logging.Level;

import javax.annotation.Nonnull;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import de.timeout.sudo.bukkit.Sudo;

public class BukkitGroupManager implements PluginMessageListener {
	
	private static final Sudo main = Sudo.getInstance();
	private static final JsonParser parser = new JsonParser();
	
	private boolean bukkit;
	
	public BukkitGroupManager(boolean bukkit) {
		this.bukkit = bukkit;
		// register PluginMessageChannel if bungeecord is enabled
		if(!bukkit) {
			// info server for using Bungeecord
			Sudo.log().log(Level.INFO, "&2Bungeecord &7in &6spigot.yml &aenabled&7. Requesting data from &2Bungeecord&7...");
			Bukkit.getMessenger().registerIncomingPluginChannel(main, "sudo", this);
			Bukkit.getMessenger().registerOutgoingPluginChannel(main, "sudo");
			
			// send login request to Bungeecord
			ByteArrayDataOutput out = ByteStreams.newDataOutput();
			out.writeUTF("login");
			out.writeUTF(main.getConfig().getString("bungeecord.uuid"));
			main.getServer().sendPluginMessage(main, "sudo", out.toByteArray());
		} else loadGroupsFromFile();
		
	}
	
	private void loadGroupsFromBungeecord(@Nonnull JsonObject data) {
		
	}
	
	private void loadUsersFromBungeecord(@Nonnull JsonArray data) {
		
	}
	
	private void loadGroupsFromFile() {
		// get data from file
		
	}
	


	@Override
	public void onPluginMessageReceived(String channel, Player player, byte[] message) {
		// if channel is Sudo-Channel
		if("sudo".equalsIgnoreCase(channel)) {
			// get Input
			ByteArrayDataInput input = ByteStreams.newDataInput(message);
			String subchannel = input.readUTF();
			// check if channel is login channel
			if("login".equalsIgnoreCase(subchannel)) {
				// log result to server
				Sudo.log().log(Level.INFO, "&7Data &areceived&7. Fetching into groups...");
				// load groups from BungeeCord.
				loadGroupsFromBungeecord(parser.parse(input.readUTF()).getAsJsonObject());
				// load users from BungeeCord
				loadUsersFromBungeecord(parser.parse(input.readUTF()).getAsJsonArray());
			}
		}
	}
}
