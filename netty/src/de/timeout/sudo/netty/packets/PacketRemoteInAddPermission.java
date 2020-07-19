package de.timeout.sudo.netty.packets;

import java.io.IOException;
import java.util.UUID;

import org.apache.commons.lang.Validate;
import org.jetbrains.annotations.NotNull;

import de.timeout.sudo.groups.Group;
import de.timeout.sudo.users.Root;
import de.timeout.sudo.users.User;
import io.netty.buffer.ByteBuf;

/**
 * Packet which will be sent if the group or user gets a new permission
 * @author Timeout
 *
 */
public class PacketRemoteInAddPermission extends Packet<PacketRemoteInAddPermission> {

	private BaseType type;
	private UUID user;
	private String group;
	
	private String permission;
	
	/**
	 * Constructor for decoders
	 */
	public PacketRemoteInAddPermission() {
		super(PacketRemoteInAddPermission.class);
	}
	
	/**
	 * Creates a new packet in user type
	 * 
	 * @param user the user who gets the permission. Cannot be null
	 * @param permission the new permission of the user. Cannot be null
	 * @param executor root-user who gave him the permission. Cannot be null
	 * @throws IllegalArgumentException if any argument is null
	 */
	public PacketRemoteInAddPermission(@NotNull User user, @NotNull String permission, @NotNull Root executor) {
		super(PacketRemoteInAddPermission.class);
		
		Validate.notNull(user, "User cannot be null");
		Validate.notEmpty(permission, "Permission can neither be null nor empty");
		Validate.notNull(executor, "Executor cannot be null");
		Validate.isTrue(executor.isRoot(), "Unable to look up ressource. Executor is not root!");
		
		this.type = BaseType.USER;
		this.user = user.getUniqueID();
		this.permission = permission;
	}
	
	/**
	 * Creates a new packet in group-mode
	 * @param group the group which gets the permission. Cannot be null
	 * @param permission the new permission of the group. Cannot be null
	 * @param executor the root-user who gave him the permission. Cannot be null
	 * @throws IllegalArgumentException if any argument is null
	 */
	public PacketRemoteInAddPermission(@NotNull Group group, @NotNull String permission, @NotNull Root executor) {
		super(PacketRemoteInAddPermission.class);
		
		Validate.notNull(group, "Group cannot be null");
		Validate.notEmpty(permission, "Permission can neither be null nor empty");
		Validate.notNull(executor, "Executor cannot be null");
		Validate.isTrue(executor.isRoot(), "Unable to look up ressource. Executor is not root!");
		
		this.type = BaseType.GROUP;
		this.group = group.getName();
		this.permission = permission;
	}
	
	@Override
	public void decode(ByteBuf input) throws IOException {
		// read type
		this.type = BaseType.getTypeByID(input.readInt());
		
		// read user or group depending on type
		if(type == BaseType.USER) {
			this.user = readUUID(input);
		} else this.group = readString(input);
		
		// read permission
		this.permission = readString(input);
	}
	
	@Override
	public void encode(ByteBuf output) throws IOException {
		// Do super call
		super.encode(output);
		
		// write type
		output.writeInt(type.id);
		// write group or user
		writeString(output, type == BaseType.USER ? user.toString() : group);
		// write permission
		writeString(output, permission);
	}

	/**
	 * Enum to differ between Group and User 
	 * @author Timeout
	 *
	 */
	private enum BaseType {
		
		USER(0), GROUP(1);
		
		private int id;
		
		private BaseType(int id) {
			this.id = id;
		}
		
		/**
		 * Gets the BaseType of the id
		 * @param id the id 
		 * @return
		 */
		public static BaseType getTypeByID(int id) {
			return id == 1 ? GROUP : USER;
		}
	}
}
