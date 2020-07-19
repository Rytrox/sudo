package de.timeout.sudo.bungee.permissions;

import java.io.File;
import java.io.IOException;
import java.util.Optional;
import java.util.logging.Level;

import org.apache.commons.lang.Validate;
import org.jetbrains.annotations.NotNull;

import de.timeout.sudo.bungee.Sudo;
import de.timeout.sudo.users.RemoteRoot;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

/**
 * Implementation for RootConsole
 * @author Timeout
 *
 */
public class RootConsole implements RemoteRoot {
	
	private static final Sudo main = Sudo.getInstance();
	
	private boolean root;
	
	private final String securityKey;
	
	private String ip;
	private int port;
	
	public RootConsole(@NotNull String securityKey) {
		Validate.notEmpty(securityKey, "Security-Key can neither be null nor empty");
		Validate.isTrue(securityKey.length() == 20, "Security-Key length must be exact 20 characters long");
		
		this.securityKey = securityKey;
		
		// Load proxy configuration
		try {
			Configuration config = ConfigurationProvider.getProvider(YamlConfiguration.class).load(new File(main.getDataFolder().getParentFile(), "config.yml"));
			
			// get listener
			Object listeners = config.getList("listeners").get(0);
			
			if(listeners instanceof Configuration) {
				// get host
				String[] host = ((Configuration) listeners).getString("host").split(":");
				
				this.ip = host[0];
				this.port = Integer.valueOf(host[1]);
			} else throw new IllegalArgumentException("Unable to read listener of the config");
		} catch (IOException e) {
			Sudo.log().log(Level.WARNING, "Unable to read proxy config.yml", e);
		}
	}

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

	@Override
	public String getRootKey() {
		return securityKey;
	}

	@Override
	public String getServerIP() {
		return Optional.ofNullable(ip).orElse("127.0.0.1");
	}

	@Override
	public int getPort() {
		return port != 0 ? port : 25577;
	}

}
