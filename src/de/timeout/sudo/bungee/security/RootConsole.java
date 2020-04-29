package de.timeout.sudo.bungee.security;

import de.timeout.sudo.security.Root;

/**
 * Implementation for RootConsole
 * @author Timeout
 *
 */
class RootConsole implements Root {
	
	private boolean root;

	@Override
	public boolean enableRoot() {
		// do nothing if root is already enabled
		if(!root) {
			root = true;
			// return success
			return true;
		}
		return false;
	}

	@Override
	public boolean disableRoot() {
		// do nothing if root is already disabled
		if(root) {
			root = false;
			// return success
			return true;
		}
		return false;
	}

	@Override
	public boolean isRoot() {
		return root;
	}

}
