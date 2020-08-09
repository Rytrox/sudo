package de.timeout.sudo.container;

import org.apache.commons.lang.Validate;
import org.jetbrains.annotations.NotNull;

public abstract class Container {
	
	protected final String name;
	
	public Container(@NotNull String name) {
		// Validate
		Validate.notEmpty(name, "Holder's name can neither be null nor empty");
		
		this.name = name;
	}
	
	/**
	 * Returns holder's name
	 * @return holder's name
	 */
	@NotNull
	public String getName() {
		return name;
	}


}
