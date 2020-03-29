package de.timeout.sudo.bukkit.permissions;

import javax.annotation.Nonnull;

import org.bukkit.configuration.ConfigurationSection;

import com.google.gson.JsonObject;

import de.timeout.sudo.groups.BaseGroup;

public class BukkitGroup extends BaseGroup {
	
	/**
	 * Creates a new BukkitGroup from the JsonObject
	 * @author Timeout
	 *
	 * @param data the json object of the group. Cannot be null
	 */
	public BukkitGroup(@Nonnull JsonObject data) {
		// load data without inheritances
		super(data.get("name").getAsString(),
				data.get("prefix").getAsString(),
				data.get("suffix").getAsString(),
				data.get("default").getAsBoolean());
		// add own permissions in group
		data.get("permissions").getAsJsonArray().forEach(permission -> this.addPermission(permission.getAsString()));
	}
	
	/**
	 * Creates a new BukkitGroup from the bukkit groups.yml
	 * @author Timeout
	 *
	 * @param name the name of the group
	 * @param section the section of the group in groups.yml. Cannot be null
	 * @throws IllegalArgumentException if the section is null
	 */
	public BukkitGroup(@Nonnull ConfigurationSection section) {
		// load data without inheritances
		super(section.getName(), section.getString("prefix"), section.getString("suffix"), section.getBoolean("default"));
		// add own permissions in group
		section.getStringList("permissions").forEach(this::addPermission);
	}
}
