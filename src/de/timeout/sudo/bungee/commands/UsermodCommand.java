package de.timeout.sudo.bungee.commands;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.commons.lang.ArrayUtils;

import com.google.common.base.Predicates;

import de.timeout.sudo.bungee.Sudo;
import de.timeout.sudo.bungee.permissions.ProxyUser;
import de.timeout.sudo.bungee.security.RootRequiredCommand;
import de.timeout.sudo.groups.Group;
import de.timeout.sudo.groups.UserGroup;
import de.timeout.sudo.users.Root;
import de.timeout.sudo.users.Sudoer;
import de.timeout.sudo.users.User;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;

/**
 * Implementation of Command usermod
 * 
 * Syntax: usermod 
 * @author Timeout
 *
 */
public class UsermodCommand extends RootRequiredCommand {
	
	private static final String G_SHORT = ChatColor.translateAlternateColorCodes('&', "&8[&6Sudo&8] &cDer Parameter G braucht mindestens eine Gruppe");
	private static final String P_NO_ARGUMENT = ChatColor.translateAlternateColorCodes('&', "&8[&6Sudo&8] &cDer Parameter p braucht ein Argument");
	private static final String P_EMPTY_PASSWORD = ChatColor.translateAlternateColorCodes('&', "&8[&6Sudo&8] &cDas Passwort darf nicht leer sein!");
	private static final String P_NO_SUDOER = ChatColor.translateAlternateColorCodes('&', "&8[&6Sudo&8] &cDer Spieler %s ist nicht in der sudoers Datei und hat somit kein Passwort!");
	private static final String NO_SUCH_PARAM = ChatColor.translateAlternateColorCodes('&', "&8[&6Sudo&8] &cDas Argument %c konnte nicht gefunden werden");

	public UsermodCommand() {
		super("usermod");
	}

	@Override
	public void execute(CommandSender sender, Root root, String[] args) {
		// check if length is correct
		if(args.length > 2) {
			// get user
			User user = getUser(args[args.length - 1]);
			// boolean if groups are keeped
			boolean keepGroups = false;
			// String for password
			String newPassword = null;
			// list for new groups
			List<Group> groups = new ArrayList<>();
			
			// split in params (ignore user field at the end)
			Map<Character, List<String>> paramsMap = solveParams((String[]) ArrayUtils.subarray(args, 0, args.length - 1));
			
			// run through params
			for(Entry<Character, List<String>> entry : paramsMap.entrySet()) {
				// decide param
				switch(entry.getKey()) {
				case 'a':
					keepGroups = true;
					break;
				case 'G':
				case 'g':
					// check if args is minimum 1 long
					if(!entry.getValue().isEmpty()) {
						// convert groups
						groups.addAll(entry.getValue()
								.stream()
								.map(groupname -> main.getGroupManager().getGroupByName(groupname))
								.filter(Predicates.notNull())
								.collect(Collectors.toList())
						);
					} else {
						// send error to sender
						sender.sendMessage(new TextComponent(G_SHORT));
						return;
					}
					break;
				case 'p':
					// check if password is given
					if(!entry.getValue().isEmpty()) {
						// get password
						newPassword = entry.getValue().get(0);
					} else sender.sendMessage(new TextComponent(P_NO_ARGUMENT));
					break;
				default:
					sender.sendMessage(new TextComponent(String.format(NO_SUCH_PARAM, entry.getKey())));
				}
			}
			
			// apply promotion to sudoer
			if(groups.remove(main.getGroupManager().getSudoGroup()) && newPassword != null) applySudoGroup(user, newPassword, root);
			// apply group changes
			applyUserGroups(groups, user, root, keepGroups);
		}
	}
	
	private boolean applySudoGroup(User user, String password, Root executor) {
		try {
			// add to sudoers
			main.getUserManager().upgradeUser(user, password, executor);
			return true;
		} catch (IOException e) {
			Sudo.log().log(Level.WARNING, String.format("&cUnable to create sudoer %s", user.getName()), e);
			return false;
		}
	}
	
	private void applyUserGroups(List<Group> groups, User user, Root executor, boolean keepGroups) {	
		if(!keepGroups) {
			// leave all usergroups if groups are not kept
			user.getMembers().forEach(group -> group.kick(user));
			
			// kick from sudo group if sudo group will not be set
			if(user instanceof Sudoer && !groups.contains(main.getGroupManager().getSudoGroup())) 
				main.getGroupManager().getSudoGroup().kick((Sudoer) user, executor);
		}
		
		// run through groups
		groups.forEach(group -> 
			// join to group
			((ProxyUser) user).join((UserGroup) group)
		);
		
	}
	
	/**
	 * Returns the user of the player if the player is online
	 * @author Timeout
	 * 
	 * @param name the name of the player
	 * @return the profile of the player. Cannot be null
	 */
	@Nullable
	private User getUser(@Nonnull String name) {
		return main.getUserManager().getUser(main.getProxy().getPlayer(name));
	}

	@Override
	public Iterable<String> onTabComplete(CommandSender sender, String[] args) {
		return null;
	}
}
