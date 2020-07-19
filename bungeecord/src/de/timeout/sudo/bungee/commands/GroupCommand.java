package de.timeout.sudo.bungee.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang.ArrayUtils;

import com.google.common.base.Predicates;

import de.timeout.sudo.bungee.security.RootRequiredCommand;
import de.timeout.sudo.groups.Group;
import de.timeout.sudo.groups.UserGroup;
import de.timeout.sudo.users.Root;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;

/**
 * PermissionsEx Group Command implementation
 * 
 * @author Timeout
 *
 */
public class GroupCommand extends RootRequiredCommand {
	
	private static final String GROUP_NOT_EXISTS = ChatColor.translateAlternateColorCodes('&', "&8[&6Sudo&8] &cDie Gruppe %s existiert nicht!");

	/*
	 * ./group -> List all groups
	 * ./group <group> -> show permissions and settings
	 * ./group <group> prefix <prefix> -> Set prefix
	 * ./group <group> suffix <suffix> -> Set suffix
	 * ./group <group> create <parents> -> Create group with parents
	 * ./group <group> delete -> delete group
	 * ./group <group> parents list -> list all parents of a group
	 * ./group <group> parents set -> set parents of a group
	 * ./group <group> add <permission> -> adds a permission
	 * ./group <group> remove <permission> -> removes a permission
	 * 
	 */
	
	public GroupCommand() {
		super("group");
	}

	@Override
	public Iterable<String> onTabComplete(CommandSender sender, String[] args) {
		return null;
	}

	@Override
	public void execute(CommandSender sender, Root root, String[] args) {
		// send help for empty arguments
		if(args.length > 0) {
			// get group
			String groupname = args[1];
			Group group = main.getGroupManager().getGroupByName(groupname);
				
			// check for arguments
			if(args.length > 1) {
				// argument switch
				switch(args[2].toLowerCase(Locale.ENGLISH)) {
				case "prefix":
						
					break;
				case "suffix":
					break;
				case "create":
					List<Group> parents = new ArrayList<>();
					// check if parents param is used
					if(args.length > 3) {
						// get groups
						parents.addAll(Arrays
							.stream((String[]) ArrayUtils.subarray(args, 2, args.length))
							.map(parentname -> main.getGroupManager().getGroupByName(parentname))
							.filter(Predicates.notNull())
							.collect(Collectors.toList())
						);
					} 
					main.getGroupManager().createGroup(groupname, parents);
					break;
				case "delete":
					// delete group
					if(main.getGroupManager().deleteGroup(group)) {
						// TODO: Erfolgreich schreiben
					}
					break;
				case "parents":
					// list parents
					showGroupInheritances(sender, group);
					break;
				case "add":
					// check if permissions are not null
					if(args.length > 2) {
						// get permissions
						Stream<String> permissions = Arrays.stream((String[]) ArrayUtils.subarray(args, 2, args.length));
						
						// add permissions to group
						if(main.getGroupManager().getSudoGroup().equals(group)) {
								permissions.forEach(permission -> main.getGroupManager().getSudoGroup().addPermission(permission, root));
						} else permissions.forEach(((UserGroup)group)::addPermission);
					}
					break;
				case "remove":
					// check for args length
					if(args.length > 2) {
						// get permissions
						Stream<String> permissions = Arrays.stream((String[]) ArrayUtils.subarray(args, 2, args.length));
						
						// remove permission from group
						if(main.getGroupManager().getSudoGroup().equals(group)) {
							permissions.forEach(permission -> main.getGroupManager().getSudoGroup().removePermission(permission, root));
						} else permissions.forEach(((UserGroup)group)::removePermission);
					}
					break;
				default: sendHelp(sender);
				}
			} else showGroupDetails(sender, group);
		} else listGroups(sender);
	}

	private void sendHelp(CommandSender sender) {
		// TODO: Hilfe schreiben
	}
	
	private void listGroups(CommandSender sender) {
		// TODO: Gruppenliste ausgeben
	}
	
	private void showGroupDetails(CommandSender sender, Group group) {
		// TODO: Group senden
	}
	
	private void showGroupInheritances(CommandSender sender, Group group) {
		
	}
}
