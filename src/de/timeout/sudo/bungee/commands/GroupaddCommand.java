package de.timeout.sudo.bungee.commands;

import de.timeout.sudo.bukkit.security.RootRequiredCommand;
import de.timeout.sudo.bungee.Sudo;
import de.timeout.sudo.bungee.permissions.ProxyGroup;
import de.timeout.sudo.users.Root;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;

/**
 * Implementation of Linux-Command groupadd, addgroup
 * 
 * Syntax: addgroup NAME
 * Aliases: groupadd
 * @author Timeout
 *
 */
public class GroupaddCommand extends RootRequiredCommand {
	
	private static final Sudo main = Sudo.getInstance();
	private static final String SUCCESS = ChatColor.translateAlternateColorCodes('&', "&8[&6Sudo&8] &7Group &2%s &acreated");
	private static final String FAILED = ChatColor.translateAlternateColorCodes('&', "&8[&6Sudo&8] &cGroup %s could not be created. A group with that name already exists");
	
	public GroupaddCommand() {
		super("groupadd", null, "addgroup");
	}

	@Override
	public void execute(CommandSender sender, Root root, String[] args) {
		// get name of the group
		if(args.length > 0) {
			// get name
			String name = args[0];
			// create new group
			sender.sendMessage(new TextComponent(
					!"sudo".equalsIgnoreCase(name) && main.getGroupManager().addGroup(new ProxyGroup(name)) ? 
					SUCCESS : FAILED));
		}
	}
	
}
