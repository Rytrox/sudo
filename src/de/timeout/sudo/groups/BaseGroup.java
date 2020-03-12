package de.timeout.sudo.groups;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.commons.lang.Validate;

import com.google.common.graph.GraphBuilder;
import com.google.common.graph.Graphs;
import com.google.common.graph.MutableGraph;

import de.timeout.sudo.utils.PermissionTree;

public class BaseGroup implements Group, Comparable<Group> {
	
	protected static final MutableGraph<Group> inheritances = GraphBuilder.directed().build();
	
	protected final PermissionTree permissions = new PermissionTree();
	protected final Set<User> members = new HashSet<>();
	
	protected String name;
	protected String prefix;
	protected String suffix;
	protected boolean defaultGroup;
	
	/**
	 * Constructor for inheritances
	 */
	protected BaseGroup(String name, String prefix, String suffix, boolean defaultGroup) {
		this.name = name;
		this.prefix = prefix;
		this.suffix = suffix;
		this.defaultGroup = defaultGroup;
		
		inheritances.addNode(this);
	}
	
	/**
	 * Get the Group by its name. Returns null if the group is not loaded
	 * @author Timeout
	 * 
	 * @param name the name of the group
	 * @return the group or null if the group is not loaded
	 */
	@Nullable
	public static Group getGroupByName(String name) {
		// for each group in graph
		for(Group group : inheritances.nodes()) {
			// return group if group is found
			if(group.getName().equalsIgnoreCase(name)) return group;
		}
		// return null for not found
		return null;
	}
	
	@Override
	public boolean isMember(User user) {
		return members.contains(user);
	}

	@Override
	public boolean join(User user) {
		return members.add(user);
	}

	@Override
	public boolean kick(User user) {
		return members.remove(user);
	}

	@Override
	public boolean addPermission(String permission) {
		// if permission is not null or empty
		if(permission != null && !permission.isEmpty()) {
			// add permission to list
			return permissions.add(permission);
		}
		// return false
		return false;
	}

	@Override
	public boolean removePermission(String permission) {
		// if permission is not null
		if(permission != null) {
			// remove permission from collection
			return permissions.remove(permission);
		}
		// return false
		return false;
	}

	@Override
	public boolean hasPermission(String permission) {
		// return true if this group has permission
		if(!permissions.contains(permission)) {
			// search in extended groups
			for(Group extended : getExtendedGroups()) {
				// search for permission, return true if found
				if(extended.hasPermission(permission)) return true;
			}
			// not found. return false
			return false;
		} else return true;
	}

	@Override
	public Set<String> getPermissions() {
		// return list
		return permissions.toSet();
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getPrefix() {
		return prefix;
	}

	@Override
	public String getSuffix() {
		return suffix;
	}

	@Override
	public boolean isDefault() {
		return defaultGroup;
	}

	@Override
	public Collection<Group> getExtendedGroups() {
		return new ArrayList<>(inheritances.successors(this));
	}

	@Override
	public int compareTo(Group o) {
		return this.name.compareTo(o.getName());
	}

	@Override
	public int hashCode() {
		return Objects.hash(defaultGroup, members, name, permissions, prefix, suffix);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		BaseGroup other = (BaseGroup) obj;
		return Objects.equals(name, other.name) && Objects.equals(permissions, other.permissions);
	}

	protected void bindInheritance(@Nonnull Group other) throws CircularInheritanceException {
		// Validate
		Validate.notNull(other, "Other group cannot be null");
		// link in graph
		inheritances.putEdge(this, other);
		// check for circle
		if(Graphs.hasCycle(inheritances)) {
			// remove edge
			inheritances.removeEdge(this, other);
			// throw exception
			throw new CircularInheritanceException(this);
		}
	}
}
