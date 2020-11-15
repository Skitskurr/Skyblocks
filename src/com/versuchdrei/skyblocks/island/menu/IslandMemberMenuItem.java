package com.versuchdrei.skyblocks.island.menu;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import com.versuchdrei.menumanager.implementations.LambdaYesNoMenu;
import com.versuchdrei.menumanager.implementations.MenuItem;
import com.versuchdrei.menumanager.implementations.MenuItemClickEvent;
import com.versuchdrei.skyblocks.island.IslandData;
import com.versuchdrei.skyblocks.utils.ItemUtils;

public class IslandMemberMenuItem extends MenuItem{
	
	private final IslandData data;
	private final UUID uuid;
	private final String name;
	
	public IslandMemberMenuItem(final IslandData data, final UUID uuid) {
		this.data = data;
		this.uuid = uuid;
		this.name = Bukkit.getOfflinePlayer(uuid).getName();
	}

	@Override
	protected boolean filter(final String filter) {
		return this.name.toLowerCase().contains(filter.toLowerCase());
	}

	@Override
	protected ItemStack getItem(final Player player) {
		if(!this.data.isMember(this.uuid)) {
			return ItemUtils.newItem(Material.WITHER_SKELETON_SKULL, "§m" + this.name);
		}
		
		final ItemStack item = new ItemStack(Material.PLAYER_HEAD);
		final SkullMeta meta = (SkullMeta) item.getItemMeta();
		meta.setDisplayName(this.name);
		meta.setOwningPlayer(Bukkit.getOfflinePlayer(this.uuid));
		final List<String> lore = new ArrayList<>();
		final int rank = this.data.getRank(this.uuid);
		switch(rank) {
		case 1:
			lore.add("§8officer");
			break;
		case 2:
			lore.add("§8owner");
			break;
		default:
			lore.add("§8member");
			break;
		}
		if(this.data.isOwner(player)) {
			lore.add("§7left click to promote");
			lore.add("§7shift-left to demote");
		}
		if(this.data.getRank(player) > rank) {
			lore.add("§7right click to kick");
		}
		item.setItemMeta(meta);
		return item;
	}
	
	@Override
	protected void onClick(final MenuItemClickEvent event) {
		switch(event.getType()) {
		case LEFT:
			if(this.data.isOwner(event.getPlayer())) {
				this.data.promote(this.uuid);
				event.setRedraw(true);
			}
			break;
		case SHIFT_LEFT:
			if(this.data.isOwner(event.getPlayer())) {
				this.data.demote(this.uuid);
				event.setRedraw(true);
			}
			break;
		case RIGHT:
		case SHIFT_RIGHT:
			final Player player = event.getPlayer();
			if(this.data.getRank(player) > this.data.getRank(this.uuid)) {
				final ItemStack item = new ItemStack(Material.PLAYER_HEAD);
				final SkullMeta meta = (SkullMeta) item.getItemMeta();
				meta.setDisplayName(this.name);
				meta.setOwningPlayer(Bukkit.getOfflinePlayer(this.uuid));
				item.setItemMeta(meta);
				
				new LambdaYesNoMenu("Kick " + this.name + "?", item, actor -> this.data.removeMember(uuid)).open(player);
			}
			break;
		default:
			break;
		}
	}

}
