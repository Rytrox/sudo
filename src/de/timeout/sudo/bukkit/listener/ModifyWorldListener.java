package de.timeout.sudo.bukkit.listener;

import java.util.Locale;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

import de.timeout.sudo.bukkit.Sudo;

import net.md_5.bungee.api.ChatColor;


public class ModifyWorldListener implements Listener {

	@EventHandler
	public void onBlockPlace(BlockPlaceEvent event) {
		// if event is not cancelled
		if(!event.isCancelled() && event.canBuild()) {
			// get Player
			Player p = event.getPlayer();
			// get permission
			String permission = String.format("minecraft.modifyworld.%s", event.getBlock().getType().name().toLowerCase(Locale.ENGLISH));
			// continue if player has permission
			if(!p.hasPermission(permission)) {
				// block action
				event.setBuild(false);
				// send no permission to player
				p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&8[&6Sudo&8] &cYou don't have enough permissions to change the world. Lacking " + permission));
			}
		}
	}
	
	@EventHandler
	public void onBlockBreak(BlockBreakEvent event) {
		// if event is not cancelled
		if(!event.isCancelled()) {
			// get Player
			Player p = event.getPlayer();
			Sudo.getInstance().getGroupManager().getUserFromOperator(p).getGroups().forEach(group -> System.out.println(group.toJson()));
			// get permission
			String permission = String.format("minecraft.modifyworld.%s", event.getBlock().getType().name().toLowerCase(Locale.ENGLISH));
			// continue if player has permission
			if(!p.hasPermission(permission)) {
				// block action
				event.setCancelled(true);
				// send no permission to player
				p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&8[&6Sudo&8] &cYou don't have enough permissions to change the world. Lacking " + permission));
			}
		}
	}
}
