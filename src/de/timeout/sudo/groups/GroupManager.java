package de.timeout.sudo.groups;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.commons.lang.Validate;

import com.google.common.graph.GraphBuilder;
import com.google.common.graph.Graphs;
import com.google.common.graph.MutableGraph;

import de.timeout.sudo.groups.exception.CircularInheritanceException;

public abstract class GroupManager {
	
	protected final MutableGraph<UserGroup> groups = GraphBuilder.directed().build();
	protected final SudoGroup sudoGroup;
	
	protected UserGroup defaultGroup;
	
	public GroupManager(@Nonnull List<String> sudopermissions) {
		Validate.notNull(sudopermissions, "Sudopermissions cannot be null");
		sudoGroup = new SudoGroup(sudopermissions);
	}
		
	/**
	 * Returns all loaded groups in a list
	 * @author Timeout
	 * 
	 * @return a list containing all loaded groups
	 */
	@Nonnull
	public List<UserGroup> getGroups() {
		return new ArrayList<>(groups.nodes());
	}
	
	/**
	 * Returns the current default group. Cannot be null
	 * @author Timeout
	 * 
	 * @return the current default group. Cannot be null
	 */
	@Nonnull
	public UserGroup getDefaultGroup() {
		return defaultGroup;
	}
	
	/**
	 * Returns the Sudo group. Cannot be null
	 * @author Timeout
	 * 
	 * @return the sudo group. Cannot be null
	 */
	@Nonnull
	public SudoGroup getSudoGroup() {
		return sudoGroup;
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
	
	/**
	 * Adds a new Group to Graph
	 * @author Timeout
	 * 
	 * @param group the group you want to add. Cannot be null
	 * @return if the group could be added
	 */
	public boolean addGroup(@Nonnull UserGroup group) {
		// Validate
		Validate.notNull(group, "Group cannot be null");
		// adds to graph
		return groups.addNode(group);
	}
}
