package de.timeout.sudo.netty.packets;

import java.util.Optional;

import de.timeout.sudo.groups.UserGroup;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Packet which will be sent if a group changes its prefix or suffix
 * @author Timeout
 *
 */
public class PacketRemoteInUpdateGroupProfile extends PacketAbstractGroup {

	private static final long serialVersionUID = -3600987147208084977L;

	private String prefix;
	private String suffix;
	
	public PacketRemoteInUpdateGroupProfile(@NotNull UserGroup group, @Nullable String prefix, @Nullable String suffix) {
		super(group);
		
		this.prefix = Optional.ofNullable(prefix).orElse("");
		this.suffix = Optional.ofNullable(suffix).orElse("");
	}

	/**
	 * Returns the new prefix of the group. Cannot be null
	 * @return the prefix of the group
	 */
	@NotNull
	public String getPrefix() {
		return prefix;
	}

	/**
	 * Returns the new suffix of the group. Cannot be null
	 * @return the suffix of the group
	 */
	@NotNull
	public String getSuffix() {
		return suffix;
	}
	
	
}
