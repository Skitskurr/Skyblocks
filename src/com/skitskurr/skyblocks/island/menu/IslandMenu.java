package com.skitskurr.skyblocks.island.menu;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import com.skitskurr.datamanager.DataManager;
import com.skitskurr.menumanager.ClickEvent;
import com.skitskurr.menumanager.Menu;
import com.skitskurr.menumanager.implementations.FixedScrollableMenu;
import com.skitskurr.menumanager.implementations.MenuItem;
import com.skitskurr.menumanager.implementations.ScrollableMenu;
import com.skitskurr.skyblocks.Main;
import com.skitskurr.skyblocks.island.IslandData;
import com.skitskurr.skyblocks.island.IslandType;
import com.skitskurr.skyblocks.utils.ItemUtils;

public class IslandMenu extends ScrollableMenu{
	
	private final Menu islandShop = new FixedScrollableMenu("Island Shop",
			Arrays.stream(IslandType.values()).map(type -> new IslandShopMenuItem(type)).collect(Collectors.toList()));

	public IslandMenu() {
		super("Islands");
	}

	@Override
	protected List<? extends MenuItem> getItems(final Player player) {
		return DataManager.Groups.getGroups(player, Main.PLUGIN_KEY).orElse(new ArrayList<>())
				.stream().map(id -> new IslandMenuItem(IslandData.getData(id))).collect(Collectors.toList());
	}
	
	@Override
	protected Inventory getInventory(final Player player) {
		final Inventory inventory = super.getInventory(player);
		
		inventory.setItem(7, ItemUtils.newItem(Material.EMERALD, "Island Shop", "§7buy new islands here"));
		
		return inventory;
	}
	
	@Override
	protected void onClick(final ClickEvent event) {
		if(event.getSlot() == 7) {
			islandShop.open(event.getPlayer());
		} else {
			super.onClick(event);
		}
	}

}
