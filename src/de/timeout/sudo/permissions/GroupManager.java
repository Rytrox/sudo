package de.timeout.sudo.permissions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.commons.lang.Validate;

import com.google.common.graph.GraphBuilder;
import com.google.common.graph.Graphs;
import com.google.common.graph.MutableGraph;

import de.timeout.sudo.groups.Group;
import de.timeout.sudo.groups.UserGroup;
import de.timeout.sudo.groups.exception.CircularInheritanceException;
import de.timeout.sudo.users.Root;
import de.timeout.sudo.users.Sudoer;
import de.timeout.sudo.users.User;

public abstract class GroupManager<T> {
	
	protected static UserGroup defaultGroup;

	protected final MutableGraph<Group> groups = GraphBuilder.directed().build();
	protected final Map<T, User> profiles = new HashMap<>();
	
	/**
	 * Returns all loaded groups in a list
	 * @author Timeout
	 * 
	 * @return a list containing all loaded groups
	 */
	@Nonnull
	public List<Group> getGroups() {
		return new ArrayList<>(groups.nodes());
	}
	
	/**
	 * Returns all loaded user profiles
	 * @author Timeout
	 * 
	 * @return a list containing all loaded user profiles
	 */
	@Nonnull
	public List<User> getUsers() {
		return new ArrayList<>(profiles.values());
	}
	
	/**
	 * Returns the user of a certain key. <br>
	 * Can be null if the user is not loaded yet
	 * @author Timeout
	 * 
	 * @param key the key you want to load.
	 * @return the user or null if the user is not loaded yet or the key is null
	 */
	@Nullable
	public User getUser(T key) {
		return profiles.get(key);
	}
	
	/**
	 * Get the Group by its name. Returns null if the group is not loaded
	 * @author Timeout
	 * 
	 * @param name the name of the group
	 * @return the group or null if the group is not loaded
	 */
	@Nullable
	public Group getGroupByName(String name) {
		// for each group in graph
		for(Group group : groups.nodes()) {
			// return group if group is found
			if(group.getName().equalsIgnoreCase(name)) return group;
		}
		// return null for not found
		return null;
	}
	
	/**
	 * loads a Group by its name from the default configuration file
	 * @author Timeout
	 * 
	 * @param name the name of the group
	 */
	protected abstract UserGroup loadGroup(String name);
	
	/**
	 * Upgrades a normal User to super-user
	 * @author Timeout
	 * 
	 * @param superUser the new superuser
	 * @param executor the executor of the command
	 */
	public abstract void upgradeUser(@Nonnull Sudoer superUser, @Nonnull Root executor);
	
	/**
	 * Binds the group extension to the extended group
	 * @author Timeout
	 * 
	 * @param group the group
	 * @param extend the extended group like (group extends extend)
	 * @throws CircularInheritanceException if the group has a circular dependency
	 * @throws IllegalArgumentException if any group is null
	 */
	protected void bindInheritance(@Nonnull UserGroup group, @Nonnull UserGroup extend) throws CircularInheritanceException {
		// Validate
		Validate.notNull(group, "Group cannot be null");
		Validate.notNull(extend, "Extends group cannot be null");
		// link in graph
		groups.putEdge(group, extend);
		// check for circle
		if(Graphs.hasCycle(groups)) {
			// remove edge
			groups.removeEdge(group, extend);
			// throw exception
			throw new CircularInheritanceException(group);
		} else {
			// add extension to group
			group.extend(extend);
		}
	}
}
