package com.versuchdrei.skyblocks.island.menu;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.versuchdrei.menumanager.implementations.MenuItem;
import com.versuchdrei.menumanager.implementations.MenuItemClickEvent;
import com.versuchdrei.skyblocks.island.IslandData;
import com.versuchdrei.skyblocks.utils.ItemUtils;

public class IslandPrivacyMenuItem extends MenuItem{
	
	private final IslandData data;
	private final Material type;
	private final String filterText;
	
	public IslandPrivacyMenuItem(final IslandData data, final Material type) {
		this.data = data;
		this.type = type;
		this.filterText = ItemUtils.getItemName(this.type);
	}

	@Override
	protected boolean filter(final String filter) {
		return this.filterText.contains(filter);
	}

	@Override
	protected ItemStack getItem(final Player arg0) {
		if(this.data.canGuestUse(this.type)) {
			return ItemUtils.setLore(ItemUtils.enchant(new ItemStack(this.type)), "§7interactable", "§7click to lock for guests");
		} else {
			return ItemUtils.setLore(new ItemStack(this.type), "§7not interactable", "§7click to unlock for guests");
		}
	}
	
	@Override
	protected void onClick(final MenuItemClickEvent event) {
		this.data.toggleGuestType(this.type);
		event.setRedraw(true);
	}

}
