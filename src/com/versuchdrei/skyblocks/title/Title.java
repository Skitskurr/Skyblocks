package com.versuchdrei.skyblocks.title;

import org.bukkit.ChatColor;
import org.bukkit.Material;

public enum Title {
	PLAYER(ChatColor.GRAY, "Player", Material.PLAYER_HEAD),
	ALPHA(ChatColor.YELLOW, "Alpha", Material.GOLD_INGOT),
	DEBUGGER(ChatColor.DARK_GRAY, "Debugger", Material.OBSERVER),
	WEAVER(ChatColor.DARK_RED, "Weaver", Material.END_CRYSTAL);
	
	private final ChatColor color;
	private final String name;
	private final Material icon;
	
	private Title(final ChatColor color, final String name, final Material icon) {
		this.color = color;
		this.name = name;
		this.icon = icon;
	}
	
	public String getName() {
		return this.color + this.name;
	}
	
	public Material getIcon() {
		return this.icon;
	}
}
