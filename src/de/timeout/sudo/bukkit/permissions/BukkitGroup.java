package de.timeout.sudo.bukkit.permissions;

import javax.annotation.Nonnull;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import de.timeout.sudo.groups.BaseGroup;
import de.timeout.sudo.groups.Group;
import de.timeout.sudo.groups.exception.CircularInheritanceException;

public class BukkitGroup extends BaseGroup {
	
	/**
	 * Creates a new BukkitGroup from the JsonObject
	 * @author Timeout
	 *
	 * @param data the json object of the group. Cannot be null
	 * @throws CircularInheritanceException if the group has a circular dependency with other groups
	 */
	public BukkitGroup(@Nonnull JsonObject data) throws CircularInheritanceException {
		// load data without inheritances
		super(data.get("name").getAsString(),
				data.get("prefix").getAsString(),
				data.get("suffix").getAsString(),
				data.get("default").getAsBoolean());
		// add own permissions in group
		data.get("permissions").getAsJsonArray().forEach(permission -> this.addPermission(permission.getAsString()));
		// try to add inheritances
		for(JsonElement extend : data.get("extends").getAsJsonArray()) {
			// try to get group
			Group other = BaseGroup.getGroupByName(extend.getAsString());
			// try to bind inheritance
			this.bindInheritance(other);
		}
	}
}
