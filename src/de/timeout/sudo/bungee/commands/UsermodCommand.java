package de.timeout.sudo.bungee.commands;

import java.util.ArrayList;
import java.util.List;

import de.timeout.sudo.bungee.security.RootRequiredCommand;
import de.timeout.sudo.groups.Group;
import de.timeout.sudo.users.Root;

import net.md_5.bungee.api.CommandSender;

/**
 * Implementation of Linux Command usermod
 * 
 * Syntax: usermod [PARAMETERS] user
 * @author Timeout
 *
 */
public class UsermodCommand extends RootRequiredCommand {
	
	private final List<Group> groups = new ArrayList<>();

	public UsermodCommand() {
		super("usermod");
	}

	@Override
	public void execute(CommandSender sender, Root root, String[] args) {
	}
}
