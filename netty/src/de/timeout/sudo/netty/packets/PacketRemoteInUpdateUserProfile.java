package de.timeout.sudo.netty.packets;

import java.util.Optional;

import de.timeout.sudo.users.User;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PacketRemoteInUpdateUserProfile extends PacketAbstractUser {

	private static final long serialVersionUID = 6870952115953880491L;
	
	private String prefix;
	private String suffix;
	
	public PacketRemoteInUpdateUserProfile(@NotNull User user, @Nullable String prefix, @Nullable String suffix) {
		super(user);
		
		this.prefix = Optional.ofNullable(prefix).orElse("");
		this.suffix = Optional.ofNullable(suffix).orElse("");
	}

	/**
	 * Returns the new prefix of the user
	 * @return the prefix of the user. Cannot be null
	 */
	@NotNull
	public String getPrefix() {
		return prefix;
	}

	/**
	 * Returns the new suffix of the user
	 * @return the suffix of the user. Cannot be null
	 */
	public String getSuffix() {
		return suffix;
	}
}
