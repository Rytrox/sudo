package de.timeout.sudo.bukkit.connectors;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.Locale;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;

import com.google.common.io.ByteArrayDataOutput;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import de.timeout.sudo.bukkit.Sudo;

public class ProxyMessageHandler implements PluginMessageListener {
	
	private static final Sudo main = Sudo.getInstance();

	@Override
	public void onPluginMessageReceived(String channel, Player player, byte[] message) {
		// create Data Input
		DataInputStream in = new DataInputStream(new ByteArrayInputStream(message));
		try {
			// read subchannel
			String subchannel = in.readUTF();
			// read channels
			switch(subchannel.toLowerCase(Locale.ENGLISH)) {
			case "initialize":
				// read Json-Data
				JsonObject obj = new JsonParser().parse(in.readUTF()).getAsJsonObject();
				JsonElement error = obj.get("error");
				// result cannot be an error
				if(error == null) {
					// run through groups
					
				} else {
					// error handling
					Sudo.log().log(Level.SEVERE, error.getAsJsonObject().get("message").getAsString());
				}
			}
		} catch (IOException e) {
			main.error("Unable to receive information from proxy", e);
		}
	}

	/**
	 * Sends a plugin-message to proxy
	 * @param output the byte array data output if the infomation
	 */
	public void sendPluginMessage(ByteArrayDataOutput output) {
		Bukkit.getServer().sendPluginMessage(main, "sudo", output.toByteArray());
	}
}
