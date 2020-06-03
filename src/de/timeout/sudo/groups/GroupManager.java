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
		// return null if name is null or empty
		if(name != null && name.trim().isEmpty()) {
			return groups.nodes()
					.stream()
					.filter(group -> group.getName().equalsIgnoreCase(name))
					.findAny()
					.orElse(null);
		}
		
		// return null for not found
		return null;
	}
	
	/**
	 * Deletes a group from the system
	 * @author Timeout
	 * 
	 * @param group the group you want to delete
	 * @return whether the group could be deleted or not
	 */
	public abstract boolean deleteGroup(@Nonnull Group group); 
	
	/**
	 * Saves a usergroup in the groups.yml
	 * @author Timeout
	 * 
	 * @param group the group you want to save. Cannot be null
	 * @throws IllegalArgumentException if the group is null
	 */
	public abstract void saveToConfig(@Nonnull UserGroup group);
	
	/**
	 * Creates a new group 
	 * @author Timeout
	 * 
	 * @param name the name of the new group
	 * @param parents the parents of the new group
	 * @return the group or null if the group could not be created 
	 */
	@Nullable
	public abstract UserGroup createGroup(String name, List<Group> parents);
	
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
}
