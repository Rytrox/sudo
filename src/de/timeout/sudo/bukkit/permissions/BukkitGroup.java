package de.timeout.sudo.bukkit.permissions;

import javax.annotation.Nonnull;

import org.bukkit.configuration.ConfigurationSection;

import com.google.gson.JsonObject;

import de.timeout.sudo.groups.UserGroup;

public class BukkitGroup extends UserGroup {
	
	private static final String OPTIONS = "options";
	
	/**
	 * Creates a new BukkitGroup from the JsonObject
	 * @author Timeout
	 *
	 * @param data the json object of the group. Cannot be null
	 */
	public BukkitGroup(@Nonnull JsonObject data) {
		// load data without inheritances
		super(data.get(OPTIONS).getAsJsonObject().get("name").getAsString(),
				data.get(OPTIONS).getAsJsonObject().get("prefix").getAsString(),
				data.get(OPTIONS).getAsJsonObject().get("suffix").getAsString(),
				data.get(OPTIONS).getAsJsonObject().get("default").getAsBoolean());
		// add own permissions in group
		data.get("permissions").getAsJsonArray().forEach(permission -> this.addPermission(permission.getAsString()));
	}
	
	/**
	 * Creates a new BukkitGroup
	 * @author Timeout
	 *
	 * @param name the name of the group. Can neither be null nor empty
	 */
	public BukkitGroup(@Nonnull String name) {
		super(name, null, null, false);
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
		super(section.getName(), section.getString("options.prefix"), section.getString("options.suffix"), section.getBoolean("options.default", false));
		// add own permissions in group
		section.getStringList("permissions").forEach(this::addPermission);
	}
}
