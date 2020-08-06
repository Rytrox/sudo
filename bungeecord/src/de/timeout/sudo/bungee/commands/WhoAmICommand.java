package de.timeout.sudo.bungee.commands;

import de.timeout.sudo.bungee.Sudo;
import de.timeout.sudo.users.User;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.Command;

/**
 * Command implementation of /whoami
 * @author Timeout
 *
 */
public class WhoAmICommand extends Command {

	private static Sudo main = Sudo.getInstance();
	
	private static final String WHOAMI_PATTERN = ChatColor.translateAlternateColorCodes('&', "&8[&6Sudo&8] &7Momentan eingeloggt als &5%s&7.");
	
	public WhoAmICommand() {
		super("whoami");
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		// get User of sender
		User user = main.getUserManager().getUser(sender);
		
		// send profile to player
		sender.sendMessage(new TextComponent(String.format(WHOAMI_PATTERN, user.getName())));
	}

}
