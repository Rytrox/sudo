package de.timeout.sudo.bukkit.messenger;

import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;

/**
 * Messager which receives update informations from Proxy
 * @author Timeout
 *
 */
public class ProxyUpdateMessager implements PluginMessageListener {
	
	@Override
	public void onPluginMessageReceived(String channel, Player player, byte[] message) {
		// ignore all channels but sudo channel
		if(channel.equalsIgnoreCase("sudo")) {
			// read message
			ByteArrayDataInput in = ByteStreams.newDataInput(message);
			// continue for update subchannel
			if("update".equalsIgnoreCase(in.readUTF())) {
				// TODO: Update-Channel erstellen und verlinken...
			}
		}
	}

}
