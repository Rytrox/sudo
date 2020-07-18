package de.timeout.sudo.bungee.permissions;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.stream.Collectors;

import org.apache.commons.lang.Validate;

import de.timeout.sudo.bungee.Sudo;
import de.timeout.sudo.groups.Group;
import de.timeout.sudo.groups.GroupManager;
import de.timeout.sudo.groups.UserGroup;
import de.timeout.sudo.groups.exception.CircularInheritanceException;
import de.timeout.sudo.netty.packets.PacketRemoteInDeleteGroup;
import de.timeout.sudo.netty.packets.PacketRemoteInGroupInheritance;
import de.timeout.sudo.netty.packets.PacketRemoteInInitializeGroup;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.config.Configuration;

public class ProxyGroupManager extends GroupManager {

	private static final Sudo main = Sudo.getInstance();
		
	public ProxyGroupManager() {
		super(main.getConfig().getStringList("sudo.permissions"));
		// load groups.yml
		main.getGroupConfig().getKeys().forEach(this::loadGroup);
		// log data
		Sudo.log().log(Level.INFO, "&6groups.yml &asuccessfully loaded&7.");
	}
	
	@Override
	public Group getGroupByName(String name) {
		return !"sudo".equalsIgnoreCase(name) ? super.getGroupByName(name) : getSudoGroup();
	}

	@Override
	protected UserGroup loadGroup(String name) {
		// ban group name sudo
		if("sudo".equalsIgnoreCase(name)) {
			// log
			Sudo.log().log(Level.INFO, "&8[&6Sudo&8] &cSudo group cannot be overwritten.");
			return null;
		}
		
		// create new group or get null if the group cannot be found
		Configuration section = main.getGroupConfig().getSection(name);
		// check if group is already loaded
		Group group = getGroupByName(name);
		// load group if section is found and group is not loaded yet
		if((group == null || group instanceof UserGroup) && section != null) {
			group = new ProxyGroup(name, section);
			// add edge to graph
			groups.addNode((UserGroup) group);
			// load inheritances
			for(String extendedGroupName : section.getStringList("extends")) {
				// load supergroup
				Group superGroup = Optional.ofNullable(getGroupByName(extendedGroupName))
								.orElse(loadGroup(extendedGroupName));
				// only continue if group could be loaded
				if(superGroup != null && group instanceof UserGroup) {
					// bind inheritance
					try {
						bindInheritance((UserGroup) group, (UserGroup) superGroup);
					} catch (CircularInheritanceException e) {
						// log error
						Sudo.log().log(Level.SEVERE, String.format("&cInvalid group configuration for Group %s", name), e);
					}
				}
			}
			
			// return group
			return (UserGroup) group;
		}
		return null;
	}

	@Override
	public boolean deleteGroup(Group group) {
		// check if group is a usergroup
		if(group instanceof UserGroup) {
			// remove all player from group
			group.getMembers().forEach(((UserGroup) group)::kick);
			
			// delete group in graph
			groups.removeNode(group);
			
			// remove from config and save config
			main.getGroupConfig().set(group.getName(), null);
			main.saveGroupConfig();
			
			// send delete packet to all subservers
			main.getNettyServer().broadcastPacket(new PacketRemoteInDeleteGroup(group.getName()));
			
			return true;
		}
		
		return false;
	}

	@Override
	public UserGroup createGroup(String name, List<Group> parents) {
		// Validate
		Validate.notEmpty(name, "Groupname cannot be null");
		
		// Return null if a group with a similar name exists
		if(getGroupByName(name) == null) {
			// create new Group
			UserGroup group = new ProxyGroup(name);
			groups.addNode(group);
			
			// send packets to all subservers
			main.getNettyServer().broadcastPacket(new PacketRemoteInInitializeGroup(group));
			
			// add parents to group
			if(parents != null) parents.forEach(parent -> {
				// check if group is a user group
				if(parent instanceof UserGroup) {
					// bind inheritance
					try {
						bindInheritance(group, (UserGroup) parent);
						main.getNettyServer().broadcastPacket(new PacketRemoteInGroupInheritance(group, parent.getName()));
					} catch (CircularInheritanceException e) {
						Sudo.log().log(Level.WARNING, String.format("&cUnable to bind parent %s to new group %s", parent.getName(), name));
					}
				}
			});
			
			// save to config
			saveToConfig(group);
			
			return group;
		}
		
		return null;
	}

	@Override
	public void saveToConfig(UserGroup group) {
		// Validate
		Validate.notNull(group, "Group cannot be null");
		
		// create section if section does not exists
		Configuration section = main.getGroupConfig().getSection(group.getName());
			
		// write option fields
		section.set("options.default", group.isDefault());
		section.set("options.prefix", Optional.ofNullable(group.getPrefix()).orElse("").replace(ChatColor.COLOR_CHAR, '&'));
		section.set("options.suffix", Optional.ofNullable(group.getSuffix()).orElse("").replace(ChatColor.COLOR_CHAR, '&'));
			
		// write permissions
		section.set("permissions", new ArrayList<>(group.getPermissions()));
			
		// write inheritances
		section.set("extends", group.getExtendedGroups()
				.stream()
				.map(UserGroup::getName)
				.collect(Collectors.toList())
		);
		
		// save config
		main.saveGroupConfig();
	}
}
