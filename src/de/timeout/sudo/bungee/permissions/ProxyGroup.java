package de.timeout.sudo.bungee.permissions;

import java.util.Optional;
import java.util.logging.Level;

import javax.annotation.Nonnull;

import com.google.common.graph.Graphs;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import de.timeout.sudo.bungee.Sudo;
import de.timeout.sudo.groups.BaseGroup;
import de.timeout.sudo.groups.exception.CircularInheritanceException;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.config.Configuration;

public class ProxyGroup extends BaseGroup {
	
	private static final Sudo main = Sudo.getInstance();

	public ProxyGroup(String name, Configuration groupConfiguration) {
		super(name, groupConfiguration.getString("options.prefix"),
				groupConfiguration.getString("options.suffix"),
				groupConfiguration.getBoolean("options.default"));
		// load inheritances
		groupConfiguration.getStringList("extends").forEach(extendedGroupName -> {
			// load supergroup
			ProxyGroup superGroup = (ProxyGroup) Optional.ofNullable(BaseGroup.getGroupByName(extendedGroupName))
							.orElse(new ProxyGroup(extendedGroupName, main.getGroupConfig().getSection(extendedGroupName)));
			// only continue if group could be loaded
			if(superGroup != null) {
				// bind inheritance
				try {
					bindInheritance(superGroup);
				} catch (CircularInheritanceException e) {
					Sudo.log().log(Level.SEVERE, String.format("&cInvalid group configuration for Group %s", name), e);
				}
			}
		});
		// load permissions
		groupConfiguration.getStringList("permissions").forEach(this::addPermission);
	}
	
	/**
	 * Converts the current ProxyGroup into a JsonObject and returns it
	 * @author Timeout
	 * 
	 * @return the group converted in a JsonObject
	 */
	@Nonnull
	public JsonObject toJson() {
		// create new JsonObject
		JsonObject object = new JsonObject();
		// write name and options in object
		object.addProperty("name", name);
		object.addProperty("default", isDefault);
		object.addProperty("prefix", prefix.replace(ChatColor.COLOR_CHAR, '&'));
		object.addProperty("suffix", prefix.replace(ChatColor.COLOR_CHAR, '&'));
		// create JsonArray for extended groups
		JsonArray extend = new JsonArray();
		Graphs.reachableNodes(inheritances, this).forEach(group -> extend.add(new JsonPrimitive(group.getName())));
		// create JsonArray for permissions
		JsonArray permissions = new JsonArray();
		this.permissions.toSet().forEach(permission -> permissions.add(new JsonPrimitive(permission)));
		// write arrays in object
		object.add("extends", extend);
		object.add("permissions", permissions);
		// return object
		return object;
	}
}
