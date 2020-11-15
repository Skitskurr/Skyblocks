package com.versuchdrei.skyblocks.island.menu;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.versuchdrei.menumanager.implementations.MenuItem;
import com.versuchdrei.menumanager.implementations.MenuItemClickEvent;
import com.versuchdrei.skyblocks.island.IslandData;
import com.versuchdrei.skyblocks.island.IslandManager;
import com.versuchdrei.skyblocks.utils.ItemUtils;

public class IslandMenuItem extends MenuItem{
	
	private final IslandData data;
	private final IslandSubMenu menu;
	
	public IslandMenuItem(final IslandData data) {
		this.data = data;
		this.menu = new IslandSubMenu(data);
	}

	@Override
	protected boolean filter(final String filter) {
		return this.data.getName().toLowerCase().contains(filter.toLowerCase());
	}

	@Override
	protected ItemStack getItem(final Player player) {
		return ItemUtils.newItem(this.data.getType().getIcon(), this.data.getName(), "§7left click to enter", "§7right click to configure");
	}
	
	@Override
	public void onClick(final MenuItemClickEvent event) {
		switch(event.getType()) {
		case LEFT:
		case SHIFT_LEFT:
			IslandManager.enterIsland(event.getPlayer(), this.data);
			break;
		case RIGHT:
		case SHIFT_RIGHT:
			this.menu.open(event.getPlayer());
			break;
		default:
			break;
		}
	}

}
