package de.timeout.sudo.bungee.netty.security;

import java.io.IOException;

import org.apache.commons.lang.RandomStringUtils;

import de.timeout.sudo.bungee.permissions.ProxySudoer;
import de.timeout.sudo.bungee.permissions.ProxyUser;
import de.timeout.sudo.bungee.permissions.RootConsole;
import de.timeout.sudo.security.RootKeyStorage;
import de.timeout.sudo.users.Root;
import de.timeout.sudo.users.Sudoer;
import de.timeout.sudo.users.User;

public class ProxyRootKeyStorage extends RootKeyStorage {
	
	public ProxyRootKeyStorage() {
		super(createConsoleUser());
	}

	private static RootConsole createConsoleUser() {
		// create root console
		String securityKey = RandomStringUtils.random(20);
		
		// create console and put into storage
		return new RootConsole(securityKey);
	}

	@Override
	public Sudoer upgradeUser(User user, String password, Root executor) throws IOException{	
		// create user
		Sudoer sudoer = ProxySudoer.upgradeUserToSudoer((ProxyUser) user, password, executor, createSecurityKey());
			
		// add security-key
		this.activeKeys.put(sudoer.getRootKey(), sudoer);
		
		// return sudoer
		return sudoer;
	}
}