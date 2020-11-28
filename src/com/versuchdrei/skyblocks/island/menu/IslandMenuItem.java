package com.versuchdrei.skyblocks.island.menu;

import java.util.Optional;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;

import com.versuchdrei.datamanager.DataManager;
import com.versuchdrei.menumanager.implementations.MenuItem;
import com.versuchdrei.menumanager.implementations.MenuItemClickEvent;
import com.versuchdrei.skyblocks.Main;
import com.versuchdrei.skyblocks.island.IslandData;
import com.versuchdrei.skyblocks.island.IslandManager;
import com.versuchdrei.skyblocks.utils.ItemUtils;
import com.versuchdrei.skyblocks.utils.MetadataUtils;

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
		final ItemStack item = new ItemStack(this.data.getType().getIcon());
		final boolean isDefault = isDefault(player);
		if(isDefault) {
			ItemUtils.enchant(item);
		}
		ItemUtils.setNameAndLore(item, this.data.getName(), "§7left click to enter", "§7right click to configure", isDefault ? "§7this is your default island" : "§7shift-click to set as default");
		return item;
	}
	
	@Override
	public void onClick(final MenuItemClickEvent event) {
		switch(event.getType()) {
		case LEFT:
			IslandManager.enterIsland(event.getPlayer(), this.data);
			break;
		case SHIFT_LEFT:
			final Player player = event.getPlayer();
			DataManager.Players.set(player, Main.PLUGIN_KEY, IslandManager.DATA_KEY_DEFAULT_ISLAND, this.data.getId());
			Main.getCurrent().ifPresent(plugin -> player.setMetadata(IslandManager.METADATA_KEY_DEFAULT_ISLAND, new FixedMetadataValue(plugin, this.data.getId())));
			event.setRedraw(true);
			break;
		case RIGHT:
		case SHIFT_RIGHT:
			this.menu.open(event.getPlayer());
			break;
		default:
			break;
		}
	}
	
	private boolean isDefault(final Player player) {
		final Optional<Main> optionalPlugin = Main.getCurrent();
		if(optionalPlugin.isPresent()) {
			return this.data.getId().equals(MetadataUtils.getMetadata(optionalPlugin.get(), player, IslandManager.METADATA_KEY_DEFAULT_ISLAND, String.class).orElse(""));
		}
		return false;
	}

}
