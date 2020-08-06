package de.timeout.sudo.permissions;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import de.timeout.sudo.utils.Collectable;

import org.apache.commons.lang.Validate;
import org.jetbrains.annotations.NotNull;

public class CollectableContainer<E> extends Container implements Collectable<E> {

	protected final Set<E> members = new HashSet<>();
	
	public CollectableContainer(@NotNull String name, @NotNull Collection<E> members) {
		super(name);
		
		Validate.notNull(members, "Members cannot be null");
		this.members.addAll(members);
	}
	
	@Override
	public boolean isMember(E element) {
		return members.contains(element);
	}

	@Override
	public Collection<E> getMembers() {
		return new ArrayList<>(members);
	}

	@Override
	public boolean add(E element) {
		Validate.notNull(element, "User cannot be null");
		
		return members.add(element);
	}

	@Override
	public boolean remove(E element) {
		return members.remove(element);
	}
}
