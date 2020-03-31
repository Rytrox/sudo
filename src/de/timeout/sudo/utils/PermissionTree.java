package de.timeout.sudo.utils;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Datastructure which manages permissions of a Permissible.
 * @author Timeout
 *
 */
public class PermissionTree {
	
	private final Map<String, PermissionTree> children = new HashMap<>();
	
	private boolean asterisk;
	
	public PermissionTree() {
		/* EMPTY. IT IS NOT NECESSARY */
	}
	
	/**
	 * Creates a clone of a permission tree
	 * @author Timeout
	 *
	 * @param clone the clone of this tree
	 */
	public PermissionTree(PermissionTree clone) {
		asterisk = clone.asterisk;
		clone.children.entrySet().forEach(subtree -> 
			this.children.put(subtree.getKey(), new PermissionTree(subtree.getValue()))
		);
	}
	
	/**
	 * Runs through subtrees and collect all permissions into a HashSet.
	 * 
	 * @author Timeout
	 * 
	 * @param path the path of the subtree
	 * @param current the current subtree
	 * @return
	 */
	@Nonnull
	private static Set<String> runthroughTree(String path, PermissionTree current) {
		// Create Set
		Set<String> set = new HashSet<>();
		// check if current is an asterisk
		if(current.isAsterisk()) {
			// end here
			set.add(path + ".*");
		} else {
			// run through children
			current.children.entrySet().forEach(entry -> 
				// add all permissions from subtree to this set
				set.addAll(runthroughTree(path + "." + entry.getKey(), entry.getValue()))
			);
		}
		// return set
		return set;
	}
	
	/**
	 * Adds all elements of other permissiontree into
	 * @author Timeout
	 * 
	 * @param instance
	 * @param other
	 */
	private static void addAll(PermissionTree instance, PermissionTree other) {
		// set instance to asterisk if other is asterisk
		if(!other.isAsterisk()) {
			// run through other
			other.children.entrySet().forEach(entry -> {
				// define next subTree
				PermissionTree subtree = instance.children.get(entry.getKey());
				// add full subtree if instance does not contain subtree
				if(subtree != null) {
					// call for next level if instance's children is not an asterisk node
					if(!subtree.isAsterisk()) addAll(instance.children.get(entry.getKey()), entry.getValue());
				} else instance.children.put(entry.getKey(), entry.getValue());
			});
		} else instance.setAsterisk();
	}
	
	/**
	 * Clears the node so this node is the last node.
	 * @author Timeout
	 *
	 */
	private void setAsterisk() {
		this.children.clear();
		asterisk = true;
	}
	
	/**
	 * Checks if the current node is an asterisk-node
	 * @author Timeout
	 * 
	 * @return if the current node is an asterisk-node
	 */
	public boolean isAsterisk() {
		return asterisk;
	}
	
	/**
	 * Adds a permission to the tree.
	 * Returns false if the permission is null
	 * @author Timeout
	 * 
	 * @param permission the permission you want to add
	 * @return true if the permission is successfully added
	 */
	public boolean add(String permission) {
		// Validate. A Permission cannot be null and no permission can be added if the current tree is an forced asterisk
		if(!asterisk && permission != null && !permission.isEmpty()) {
			// Split permissions into Subpermisions (Add asterisk at end)
			String[] subpermissions = (permission.endsWith("*") ? permission : permission + ".*").toLowerCase(Locale.ENGLISH).split("\\.");
			// define actual parent
			PermissionTree parent = this;
			int currentPosition = 0;
			// stop when parent is asterisk or last one is reached. (last one is definitely a asterisk)
			while(!parent.isAsterisk() && currentPosition < subpermissions.length -1) {
				// get Sub-Permission
				String subpermission = subpermissions[currentPosition];
				// increase current position
				currentPosition++;
				// create tree section if it not exists
				if(!parent.children.containsKey(subpermission)) {
					// create tree section
					PermissionTree child = new PermissionTree();
					// link section in parents children
					parent.children.put(subpermission, child);
					// update parent
					parent = child;
				} else parent = parent.children.get(subpermission);
			}
			// set last one to asterisk
			parent.setAsterisk();
			// return true for success
			return true;
		} else return false;
	}
	
	public boolean remove(String permission) {
		// return false when client has asterisk permission and not asterisk will be removed
		if(!asterisk && !"*".equals(permission)) {
			// Split permissions into subpermission
			String[] subpermissions = (permission.endsWith(".*") ? permission.substring(0, permission.length() - 2) : permission)
					.toLowerCase(Locale.ENGLISH).split(".");
			// define parent
			PermissionTree parent = this;
			// run trough array
			for (int i = 0; i < subpermissions.length; i++) {
				String subpermission = subpermissions[i];
				// return false if permission is not in tree
				if(parent.children.containsKey(subpermission)) {
					// check if subpermission is last one
					if(i == subpermissions.length -1) {
						// delete tree in children
						children.remove(subpermission);
						// return true for success
						return true;
					} else {
						// redefine parent
						parent = parent.children.get(subpermission);
						// break if parent is asterisk
						if(parent.isAsterisk()) return false;
					}
				} else return false;
			}
			// permission cannot be found return false
			return false;
		} else return false;
	}
	
	public boolean contains(String permission) {
		// return true if client has asterisk permission
		if(!asterisk) {
			// validate (return false for null)
			if(permission != null && !permission.isEmpty()) {
				// split permission into Array
				String[] subpermissions = permission.split(".");
				// define current subtree
				PermissionTree current = this;
				// run through subpermissions
				for (int i = 0; i < subpermissions.length; i++) {
					String subpermission = subpermissions[i];
					// redefine current
					current = current.children.get(subpermission);
					// return false if current cannot be found (is null)
					if(current == null) return false;
				}
				// found!
				return true;
			} else return false;
		} else return true;
	}
	
	/**
	 * Collects all Permissions into a HashSet and returns it.
	 * Cannot be null
	 * @author Timeout
	 * 
	 * @return a set with all permissions inside the tree
	 */
	@Nonnull
	public Set<String> toSet() {
		// create Set
		Set<String> set = new HashSet<>();
		// check if root is not an asterisk
		if(!asterisk) {
			// run through subtree
			children.entrySet().forEach(entry -> 
				// add to set
				set.addAll(runthroughTree(entry.getKey(), entry.getValue()))
			);
		} else set.add("*");
		// return set
		return set;
	}
	
	/**
	 * Adds all elements from other tree into this tree. Does nothing if the tree is null
	 * @author Timeout
	 * 
	 * @param tree the other tree
	 */
	public void addAll(@Nullable PermissionTree tree) {
		// check if tree is not null
		if(tree != null) {
			// add all subtrees
			addAll(this, tree);
		}
	}
}
