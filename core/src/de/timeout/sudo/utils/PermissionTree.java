package de.timeout.sudo.utils;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.commons.lang.Validate;

public class PermissionTree {

	private final Node root;
	
	/**
	 * Creates a new empty PermissionTree
	 * @author Timeout
	 *
	 */
	public PermissionTree() {
		root = new Node();
	}
	
	/**
	 * Constructor to create a copy of the permissiontree
	 * @author Timeout
	 *
	 * @param other the original permissiontree
	 */
	public PermissionTree(@Nonnull PermissionTree other) {
		// Validate
		Validate.notNull(other, "Original tree cannot be null");
		// create copy of the root
		root = Node.createCopy(null, other.root);
	}
	
	/**
	 * Static method to get all permissions of the trr
	 * @author Timeout
	 * 
	 * @param current the current node
	 * @param path the path of the permission
	 * @return a set containing all permissions 
	 */
	@Nonnull
	private static Set<String> toSet(Node current, String path) {
		// create Set
		Set<String> set = new HashSet<>();
		// if current has asterisk
		if(!current.asterisk) {
			// perform for all children
			current.children.values().forEach(child -> 
				set.addAll(
						toSet(child, 
						(path != null && !path.isEmpty() ? String.format("%s.%s", path, child.subpermission) : child.subpermission)))
			);
		} else {
			// add permission (add asterisk if this is the first
			set.add((path != null && !path.isEmpty() ? path + ".*" : "*"));
		}
		// return set
		return set;
	}
	
	private static void addAll(@Nonnull Node current, @Nonnull Collection<Node> insertions) {
		// Validate
		Validate.notNull(current, "Current node cannot be null");
		Validate.notNull(insertions, "Insertions cannot be null");
		// continue if current is not an asterisk
		if(current.asterisk) {
			// run through insertions
			insertions.forEach(insertNode -> {
				// get next node
				Node children = current.children.get(insertNode.subpermission);
				// add subtree if not exists
				if(children == null) {
					// apply to next
					addAll(children, insertions);
				} else current.children.put(insertNode.subpermission, insertNode);
			});
		}
	}
	
	public void addAll(PermissionTree other) {
		// do nothing if the other tree is null
		if(other != null) {
			// add all to other tree
			addAll(root, other.root.children.values());
		}
	}
	
	/**
	 * adds a permission to the tree and returns a boolean if the permission could be added
	 * @author Timeout
	 * 
	 * @param permission the permission 
	 * @return true if the player now has the permission. else false
	 */
	public boolean add(String permission) {
		// validate (do nothing if permission is invalid)
		if(permission != null && !permission.isEmpty()) {
			// define current node
			Node current = root;
			// split permission into subpermissions
			String[] subpermissions = (permission.endsWith(".*") ? permission : permission + ".*").toLowerCase(Locale.ENGLISH).split("\\.");
			// run through permissions
			for(int i = 0; i < subpermissions.length -1; i++) {
				// break if the current is an asterisk
				if(!current.asterisk && !subpermissions[i].equals("*")) {
					// finalize current to have write-access
					final Node currentConst = current;
					// get subpermission
					String subpermission = subpermissions[i];
					// get current (link if not exists)
					Node child = Optional.ofNullable(current.children.get(subpermission)).orElseGet(() -> {
						// create new Node
						Node node = new Node(currentConst, subpermission);
						// link it
						currentConst.children.put(subpermission, node);
						// return node
						return node;
					});
					// relink current and continue
					current = child;
				} else break;
			}
			// set last element to asterisk
			current.asterisk = true;
			return true;
		} 
		return false;
	}
	
	public boolean remove(String permission) {
		// validate
		if(permission != null && !permission.isEmpty()) {
			// define current node
			Node current = root;
			// split permission into subpermissions
			String[] subpermissions = (permission.endsWith(".*") ? permission.substring(0, permission.length() -2) : permission).toLowerCase(Locale.ENGLISH).split("\\.");
			// run through subpermissions
			for(int i = 0; i < subpermissions.length -1; i++) {
				// return false if current has asterisk and is not null
				if(current != null && !current.asterisk) {
					// redefine current
					current = current.children.get(subpermissions[i]);
				} else return false;
			}
			// try to get last one
			boolean result = current.children.remove(subpermissions[subpermissions.length -1]) != null;
			// remove unnecessary nodes if the result is true
			if(result) removeUnnecessaryNodes(current);
			// return result
			return result;
		}
		// return false for error
		return false;
	}
	
	/**
	 * Removes all unnnecessary nodes from a current node in direction to its root
	 * @author Timeout
	 * 
	 * @param current the node where this method starts
	 */
	private void removeUnnecessaryNodes(Node current) {
		// walk in tree backwards
		Node parent;
		while((parent = current.parent) != null) {
			// remove current from parent
			parent.children.remove(current.subpermission);
			// break loop if parents children are not empty
			if(parent.children.isEmpty()) {
				// set parent as current to continue
				current = parent;
			} else break;
		}
	}
	
	/**
	 * Checks if the current permission is in this tree
	 * @author Timeout
	 * 
	 * @param permission the permission you want to check
	 * @return false if the permission is null or not inside the tree else true
	 */
	public boolean contains(String permission) {
		// validate (return false if permission is invalid)
		if(permission != null && !permission.isEmpty()) {
			// define pointer for current node
			Node current = root;
			// split into subpermissions and erase ".*"
			String[] subpermissions = (permission.endsWith(".*") ? permission.substring(0, permission.length() -2) : permission).toLowerCase(Locale.ENGLISH).split("\\.");
			// run through subpermissions
			for(int i = 0; i < subpermissions.length; i++) {
				// return false if current does not exists
				if(current != null) {
					// return true if current is asterisk
					if(!current.asterisk) {
						// redefine current
						current = current.children.get(subpermissions[i]);
					} else return true;
				} else return false;
			}
			// return true if the last node is asterisk
			return current.asterisk;
		} else return false;
	}
	
	/**
	 * Clears the current permissiontree
	 * @author Timeout
	 *
	 */
	public void clear() {
		root.children.clear();
	}
	
	/**
	 * Converts the permissiontree to a set with all permissions
	 * @author Timeout
	 * 
	 * @return the permissiontree with all permissions
	 */
	@Nonnull
	public Set<String> toSet() {
		return toSet(root, "");
	}
	
	/**
	 * Structure for Nodes
	 * @author Timeout
	 *
	 */
	private static class Node {
		
		private final Map<String, Node> children = new HashMap<>();
		
		private boolean asterisk;
		private String subpermission;
		private Node parent;
		
		public Node() {
			parent = null;
			subpermission = null;
		}
		
		/**
		 * Creates a copy of the node
		 * @author Timeout
		 *
		 * @param other the copy of the node
		 * @throws IllegalArgumentException if the argument is null
		 */
		private Node(Node other) {
			// Validate
			Validate.notNull(other, "Other Node cannot be null");
			this.asterisk = other.asterisk;
			this.subpermission = other.subpermission;
			this.parent = other.parent;
			
			// copy all childrens
			other.children.entrySet().forEach(entry -> 
				// link copy here
				this.children.put(entry.getKey(), createCopy(this, entry.getValue()))
			);
		}
		
		public Node(Node parent, String subpermission) {
			this.parent = parent;
			this.subpermission = subpermission;
		}
		
		/**
		 * Copies a Node with its parent
		 * @author Timeout
		 * 
		 * @param parent the parent copy of the node
		 * @param current the node you want to copy
		 * @throws IllegalArgumentException if the current node is null
		 * @return the copy of the node
		 */
		public static Node createCopy(@Nullable Node parent, @Nonnull Node current) {
			// Validate
			Validate.notNull(current, "Current node cannot be null");
			// create Node
			Node copy = new Node(current);
			// reset parent
			copy.parent = parent;
			
			// copy all children
			current.children.entrySet().forEach(entry -> copy.children.put(entry.getKey(), createCopy(copy, entry.getValue())));
			// return node
			return copy;
		}
	}
}
