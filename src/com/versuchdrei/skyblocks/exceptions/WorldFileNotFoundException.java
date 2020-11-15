package com.versuchdrei.skyblocks.exceptions;

public class WorldFileNotFoundException extends Exception{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private final String worldPath;
	
	public WorldFileNotFoundException(final String worldPath) {
		this.worldPath = worldPath;
	}
	
	public String getWorldPath() {
		return worldPath;
	}

}
