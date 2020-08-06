package de.timeout.sudo.bukkit.groups;

import java.io.IOException;
import java.util.ArrayList;

import javax.annotation.Nonnull;

import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

import de.timeout.sudo.groups.UserGroup;

/**
 * Implementation of UserGroups on Bukkit-Side
 * @author Timeout
 *
 */
public class BukkitGroup extends UserGroup {
	
	/**
	 * Creates a new BukkitGroup
	 * @author Timeout
	 *
	 * @param name the name of the group. Can neither be null nor empty
	 */
	public BukkitGroup(@Nonnull String name) {
		super(name, null, null, false, new ArrayList<>());
	}
	
	/**
	 * Creates a new BukkitGroup from the bukkit groups.yml
	 * @author Timeout
	 *
	 * @param name the name of the group
	 * @param section the section of the group in groups.yml. Cannot be null
	 * @throws IllegalArgumentException if the section is null
	 */
	public BukkitGroup(@NotNull ConfigurationSection section) {
		// load data without inheritances
		super(section.getName(),
				section.getString("options.prefix", ""),
				section.getString("options.suffix", ""),
				section.getBoolean("options.default", false),
				section.getStringList("permissions")
		);
		
		// add own permissions in group
		section.getStringList("permissions").forEach(this::addPermission);
	}

	/**
	 * Only save data if server is standalone
	 */
	@Override
	public void save() throws IOException {

	}
}
