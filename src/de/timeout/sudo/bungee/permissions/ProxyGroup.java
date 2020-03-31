package de.timeout.sudo.bungee.permissions;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import de.timeout.sudo.groups.BaseGroup;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.config.Configuration;

public class ProxyGroup extends BaseGroup {
		
	public ProxyGroup(String name, Configuration groupConfiguration) {
		super(name, groupConfiguration.getString("options.prefix"),
				groupConfiguration.getString("options.suffix"),
				groupConfiguration.getBoolean("options.default"));
		// load permissions
		groupConfiguration.getStringList("permissions").forEach(this::addPermission);
	}
	
	/**
	 * Converts the current ProxyGroup into a JsonObject and returns it
	 * @author Timeout
	 * 
	 * @return the group converted in a JsonObject
	 */
	@Override
	public JsonObject toJson() {
		// create new JsonObject
		JsonObject object = new JsonObject();
		// create options section
		JsonObject options = new JsonObject();
		// write name and options in object
		options.addProperty("name", name);
		options.addProperty("default", isDefault);
		options.addProperty("prefix", prefix.replace(ChatColor.COLOR_CHAR, '&'));
		options.addProperty("suffix", prefix.replace(ChatColor.COLOR_CHAR, '&'));
		// write options into object
		object.add("options", options);
		// create JsonArray for extended groups
		JsonArray extend = new JsonArray();
		groups.forEach(group -> extend.add(new JsonPrimitive(group.getName())));
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
