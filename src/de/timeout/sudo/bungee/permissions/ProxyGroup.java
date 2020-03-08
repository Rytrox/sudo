package de.timeout.sudo.bungee.permissions;

import java.util.logging.Level;

import javax.annotation.Nonnull;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import de.timeout.sudo.bungee.Sudo;
import de.timeout.sudo.groups.BaseGroup;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.config.Configuration;

public class ProxyGroup extends BaseGroup {
	
	private static final Sudo main = Sudo.getInstance();

	public ProxyGroup(String name, Configuration groupConfiguration) {
		super(name, groupConfiguration.getString("options.prefix"),
				groupConfiguration.getString("options.suffix"),
				groupConfiguration.getBoolean("options.defaultGroup"));
		// load inheritances
		groupConfiguration.getStringList("extends").forEach(extendedGroupName -> {
			// only continue if same name cannot be in extended group
			if(!main.getGroupConfig().getStringList(String.format("%s.extends", extendedGroupName)).contains(name)) {
				// load supergroup
				ProxyGroup superGroup = (ProxyGroup) main.getGroupManager().getGroup(extendedGroupName);
				// only continue if group could be loaded
				if(superGroup != null) {
					// add to supergroup
					this.inheritance.add(superGroup);
					// add permissions to supergroup
					this.allPermissions.addAll(superGroup.allPermissions);
				}
			} else Sudo.log().log(Level.WARNING, "&cCannot instanciate Configuration. There are an extendrotation");
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
	public JsonObject toJsonObject() {
		// create new JsonObject
		JsonObject object = new JsonObject();
		// write name and options in object
		object.addProperty("name", name);
		object.addProperty("defaultGroup", defaultGroup);
		object.addProperty("prefix", prefix.replace(ChatColor.COLOR_CHAR, '&'));
		object.addProperty("suffix", prefix.replace(ChatColor.COLOR_CHAR, '&'));
		// create JsonArray for extended groups
		JsonArray extend = new JsonArray();
		inheritance.forEach(group -> extend.add(new JsonPrimitive(group.getName())));
		// create JsonArray for permissions
		JsonArray permissions = new JsonArray();
		this.allPermissions.toSet().forEach(permission -> permissions.add(new JsonPrimitive(permission)));
		// write arrays in object
		object.add("extends", extend);
		object.add("permissions", permissions);
		// return object
		return object;
	}
}
