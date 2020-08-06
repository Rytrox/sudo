package de.timeout.sudo.bukkit.groups;

import java.io.IOException;

import de.timeout.sudo.bukkit.Sudo;
import de.timeout.sudo.groups.SudoGroup;

public class BukkitSudoGroup extends SudoGroup {
	
	private static final Sudo main = Sudo.getInstance();

	public BukkitSudoGroup() {
		super(main.getConfig().getStringList("sudo.permissions"));
	}

	@Override
	public void save() throws IOException {
		
	}
	

}
