package de.timeout.sudo.bungee.commands;

import java.util.Locale;

import de.timeout.sudo.bungee.Sudo;
import de.timeout.sudo.groups.Group;
import de.timeout.sudo.groups.UserGroup;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;

/**
 * PermissionsEx Group Command implementation
 * 
 * @author Timeout
 *
 */
public class GroupCommand extends Command implements TabExecutor {
	
	private static final String GROUP_NOT_EXISTS = ChatColor.translateAlternateColorCodes('&', "&8[&6Sudo&8] &cDie Gruppe %s existiert nicht!");
	private static final String GROUP_DELETED = ChatColor.translateAlternateColorCodes('&', "&8[&6Sudo&8] &7Die &5Gruppe &d%s &7wurde &aerfolgreich &cgelöscht&7.");
	private static final String GROUP_CREATED = ChatColor.translateAlternateColorCodes('&', "&8[&6Sudo&8] &7Die &5Gruppe &d%s &7wurde &aerfolgreich erstellt&7.");
	private static final String GROUP_PREFIX_SET = ChatColor.translateAlternateColorCodes('&', "&8[&6Sudo&8] &7Gruppenprefix on &5Gruppe &d%s &agesetzt&7!");
	private static final String GROUP_SUFFIX_SET = ChatColor.translateAlternateColorCodes('&', "&8[&6Sudo&8] &7Gruppensuffix on &5Gruppe &d%s &agesetzt&7!");
	private static final String GROUP_PERMISSIONS_ADD = ChatColor.translateAlternateColorCodes('&', "&8[&6Sudo&8] &1Berechtigung &9%s &aerfolgreich &7der &5Gruppe &d%s &ahinzugefügt&7.");
	private static final String GROUP_PERMISSIONS_OWN = ChatColor.translateAlternateColorCodes('&', "&8[&6Sudo&8] &7Die &5Gruppe &d%s &7besitzt die &1Berechtigung &9%s &7bereits.");
	private static final String GROUP_PERMISSIONS_REMOVE = ChatColor.translateAlternateColorCodes('&', "&8[&6Sudo&8] &1Berechtigung &9%s &aerfolgreich &7von &5Gruppe &d%s &aentfernt&7.");
	private static final String GROUP_PERMISSIONS_MISS = ChatColor.translateAlternateColorCodes('&', "&8[&6Sudo&8] &7Die &5Gruppe &d%s &7besitzt die &1Berechtigung &9%s &cnicht&7.");
	
	private static final String NO_PERMISSION = ChatColor.translateAlternateColorCodes('&', "&8[&6Sudo&8] &cDu hast nicht die benötigte Berechtigung, diesen Befehl auszuführen. Fehlende Berechtigung: %s");
	
	private static final String ERROR_SUDO_EXTENSION = ChatColor.translateAlternateColorCodes('&', "&8[&6Sudo&8] &cDieser Befehl ist nur für benutzerdefinierte Gruppen nutzbar!");
	private static final String ERROR_GROUP_NAME_EMPTY = ChatColor.translateAlternateColorCodes('&', "&8[&6Sudo&8] &cDer Name der Gruppe darf nicht leer sein!");
	private static final String ERROR_GROUP_EXISTS = ChatColor.translateAlternateColorCodes('&', "&8[&6Sudo&8] &cDie Gruppe %s konnte nicht erstellt werden, da eine andere Gruppe mit demselben Namen bereits existiert!");
	private static final String ERROR_GROUP_PERMISSIONS_INVALID = ChatColor.translateAlternateColorCodes('&', "&8[&6Sudo&8] &cDie eingegebene Berechtigung ist ungültig.");
	
	private static final Sudo main = Sudo.getInstance();
	
	/*
	 * ./group -> List all groups
	 * ./group <group> -> show permissions and settings
	 * ./group <group> prefix <prefix> -> Set prefix
	 * ./group <group> suffix <suffix> -> Set suffix
	 * ./group <group> create <parents> -> Create group with parents
	 * ./group <group> delete -> delete group
	 * ./group <group> parents -> list all parents of a group
	 * ./group <group> parents add -> add parents to a group
	 * ./group <group> parents remove -> removes parents from a group
	 * ./group <group> parents set -> sets parents to a group
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
	public void execute(CommandSender sender, String[] args) {
		// send help for empty arguments
		if(args.length > 0) {
			// get group
			Group group = main.getGroupManager().getGroupByName(args[0]);
			
			// send details of group if no arguments follows
			if(args.length > 1) {
				// create group if command 'create' is used
				if(!args[1].equalsIgnoreCase("create")) {
					// check if group could be found
					if(group != null) {
						// check group
						if(group instanceof UserGroup) {
							switch(args[1].toLowerCase(Locale.ENGLISH)) {
							case "prefix":
								// send help if prefix is empty
								if(args.length > 2) {
									updatePrefix(sender, (UserGroup) group, ChatColor.translateAlternateColorCodes('&', args[2]));
								} else sendHelp(sender);
								
								break;
							case "suffix":
								// send help if suffix is empty
								if(args.length > 2) {
									updateSuffix(sender, (UserGroup) group, ChatColor.translateAlternateColorCodes('&', args[2]));
								} else sendHelp(sender);
								
								break;
							case "delete":
								deleteGroup(sender, (UserGroup) group);
								
								break;
							case "parents":
								
								break;
							case "add":
								if(args.length > 2) {
									// add permission
									addPermission(sender, (UserGroup) group, args[2]);
								} else sendHelp(sender);
								
								break;
							case "remove":
								if(args.length > 2) {
									// remove permission
									removePermission(sender, (UserGroup) group, args[2]);
								} else sendHelp(sender);
								
								break;
							default:
								sendHelp(sender);
							}
						} else sender.sendMessage(new TextComponent(ERROR_SUDO_EXTENSION));
					} else sender.sendMessage(new TextComponent(String.format(GROUP_NOT_EXISTS, args[0])));
				} else createGroup(sender, args[0]);
			} else showGroupDetails(sender, group);
		} else listGroups(sender);
	}
	
	/**
	 * Sets the prefix of a group
	 * @param sender the sender of the command
	 * @param group the group you want to set
	 * @param prefix the prefix you want to set
	 */
	private void updatePrefix(CommandSender sender, UserGroup group, String prefix) {
		// create permission
		String permission = String.format("sudo.groups.%s.prefix", group.getName());
		
		// send error to sender if sender does not match permission
		if(sender.hasPermission(permission)) {
			// set prefix
			group.setPrefix(prefix);
			
			// send message to user
			sender.sendMessage(new TextComponent(String.format(GROUP_PREFIX_SET, group.getName())));
				
			// TODO: send update packet to all remotes
		} else sender.sendMessage(new TextComponent(String.format(NO_PERMISSION, permission)));
	}
	
	private void updateSuffix(CommandSender sender, UserGroup group, String suffix) {
		// create permission
		String permission = String.format("sudo.groups.%s.suffix", group.getName());
		
		// send error if sender does not match permission
		if(sender.hasPermission(permission)) {
			// set suffix
			group.setSuffix(suffix);
				
			// send message to user
			sender.sendMessage(new TextComponent(String.format(GROUP_SUFFIX_SET, group.getName())));
				
			// TODO: send update packet to all remotes
		} else sender.sendMessage(new TextComponent(String.format(NO_PERMISSION, permission)));
	}
	
	private void createGroup(CommandSender sender, String name) {
		// check permission
		if(sender.hasPermission("sudo.groups.create")) {
			if(name != null && !name.isEmpty()) {			
				// check if creation succeed
				if(main.getGroupManager().createGroup(name, null) != null) {
					// send success
					sender.sendMessage(new TextComponent(String.format(GROUP_CREATED, name)));
				} else sender.sendMessage(new TextComponent(String.format(ERROR_GROUP_EXISTS, name)));
			} else sender.sendMessage(new TextComponent(ERROR_GROUP_NAME_EMPTY));
		} else sender.sendMessage(new TextComponent(String.format(NO_PERMISSION, "sudo.groups.create")));
	}
	
	private void deleteGroup(CommandSender sender, UserGroup group) {
		// build permission
		String permission = String.format("sudo.groups.%s.delete", group.getName());
			
		// check permission
		if(sender.hasPermission(permission)) {
			// delete group
			main.getGroupManager().deleteGroup((UserGroup) group);
				
			// send success
			sender.sendMessage(new TextComponent(String.format(GROUP_DELETED, group.getName())));
		} else sender.sendMessage(new TextComponent(String.format(NO_PERMISSION, permission)));
	}
	
	private void addPermission(CommandSender sender, UserGroup group, String permission) {
		// build add permission
		String addPermission = String.format("sudo.groups.%s.permissions.add", group.getName());
		
		// send error if permissions does not match
		if(sender.hasPermission(addPermission)) {
			// continue if permission is valid
			if(permission != null && !permission.isEmpty()) {
				// add permission
				if(group.addPermission(permission)) {
					// send modification
					sender.sendMessage(new TextComponent(String.format(GROUP_PERMISSIONS_ADD, permission, group.getName())));
				} else sender.sendMessage(new TextComponent(String.format(GROUP_PERMISSIONS_OWN, group.getName(), permission)));
			} else sender.sendMessage(new TextComponent(ERROR_GROUP_PERMISSIONS_INVALID));
		} else sender.sendMessage(new TextComponent(String.format(NO_PERMISSION, addPermission)));
	}
	
	private void removePermission(CommandSender sender, UserGroup group, String permission) {
		// build add permission
		String removePermission = String.format("sudo.groups.%s.permissions.remove", group.getName());
		
		// send error if permissions does not match
		if(sender.hasPermission(removePermission)) {
			// continue if permission is valid
			if(permission != null && !permission.isEmpty()) {
				// add permission
				if(group.removePermission(permission)) {
					// send modification
					sender.sendMessage(new TextComponent(String.format(GROUP_PERMISSIONS_REMOVE, permission, group.getName())));
				} else sender.sendMessage(new TextComponent(String.format(GROUP_PERMISSIONS_MISS, group.getName(), permission)));
			} else sender.sendMessage(new TextComponent(ERROR_GROUP_PERMISSIONS_INVALID));
		} else sender.sendMessage(new TextComponent(String.format(NO_PERMISSION, removePermission)));

	}

	private void sendHelp(CommandSender sender) {
		// TODO: Hilfe schreiben
	}
	
	private void listGroups(CommandSender sender) {
		
	}
	
	private void showGroupDetails(CommandSender sender, Group group) {
		// TODO: Group senden
	}
	
	private void showGroupInheritances(CommandSender sender, Group group) {
		
	}
}
