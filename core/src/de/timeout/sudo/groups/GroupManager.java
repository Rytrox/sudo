package de.timeout.sudo.groups;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.Validate;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.google.common.graph.GraphBuilder;
import com.google.common.graph.Graphs;
import com.google.common.graph.MutableGraph;

import de.timeout.sudo.groups.exception.CircularInheritanceException;

public abstract class GroupManager {
	
	protected final MutableGraph<UserGroup> groups = GraphBuilder.directed().build();
	protected final SudoGroup sudoGroup;
	
	protected UserGroup defaultGroup;
	
	public GroupManager(@NotNull SudoGroup sudogroup) {
		Validate.notNull(sudogroup, "SudoGroup cannot be null");
		
		this.sudoGroup = sudogroup;
	}
		
	/**
	 * Returns all loaded groups in a list
	 * @author Timeout
	 * 
	 * @return a list containing all loaded groups
	 */
	@NotNull
	public List<UserGroup> getGroups() {
		return new ArrayList<>(groups.nodes());
	}
	
	/**
	 * Returns the current default group. Cannot be null
	 * @author Timeout
	 * 
	 * @return the current default group. Cannot be null
	 */
	@NotNull
	public UserGroup getDefaultGroup() {
		return defaultGroup;
	}
	
	/**
	 * Returns the Sudo group. Cannot be null
	 * @author Timeout
	 * 
	 * @return the sudo group. Cannot be null
	 */
	@NotNull
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
	public abstract boolean deleteGroup(@NotNull UserGroup group); 
	
	/**
	 * Saves a usergroup in the groups.yml
	 * @author Timeout
	 * 
	 * @param group the group you want to save. Cannot be null
	 * @throws IllegalArgumentException if the group is null
	 */
	public abstract void saveToConfig(@NotNull UserGroup group);
	
	/**
	 * Creates a new group and sends it to all remotes.
	 * @author Timeout
	 * 
	 * @param name the name of the new group
	 * @param parents the parents of the new group
	 * @return the group or null if the group could not be created 
	 */
	public abstract @Nullable UserGroup createGroup(String name, List<Group> parents);
	
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
	protected void bindInheritance(@NotNull UserGroup group, @NotNull UserGroup extend) throws CircularInheritanceException {
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
