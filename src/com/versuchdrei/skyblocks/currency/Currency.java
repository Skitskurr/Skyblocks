package com.versuchdrei.skyblocks.currency;

import org.bukkit.entity.Player;

public interface Currency {
	
	public boolean has(Player player, int amount);
	
	public boolean pay(Player player, int amount);
	
}
