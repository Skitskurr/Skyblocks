package com.skitskurr.skyblocks.island.menu;

import java.util.Arrays;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import com.skitskurr.menumanager.ClickEvent;
import com.skitskurr.menumanager.Menu;
import com.skitskurr.menumanager.TextEvent;
import com.skitskurr.menumanager.implementations.FixedScrollableMenu;
import com.skitskurr.menumanager.implementations.LambdaYesNoMenu;
import com.skitskurr.skyblocks.island.IslandData;
import com.skitskurr.skyblocks.island.IslandManager;
import com.skitskurr.skyblocks.utils.ItemUtils;

public class IslandSubMenu extends Menu{
	
	private static final String TEXT_REQUEST_KEY_RENAME = "rename";
	private static final String TEXT_REQUEST_KEY_INVITE = "invite";
	
	private final IslandData data;
	private final Menu privacyMenu;
	private final Menu memberMenu;
	
	public IslandSubMenu(final IslandData data) {
		this.data = data;
		this.privacyMenu = new FixedScrollableMenu("Privacy Settings", Arrays.stream(IslandManager.interactables)
				.map(type -> new IslandPrivacyMenuItem(data, type)).collect(Collectors.toList()));
		this.memberMenu = new IslandMemberMenu(data);
	}

	@Override
	protected Inventory getInventory(final Player player) {
		final Inventory inventory = Bukkit.createInventory(null, 54, this.data.getName());
		
		inventory.setItem(10, ItemUtils.newItem(Material.GRASS_BLOCK, "enter"));
		final String[] memberLore = this.data.hasRank(player, 1) ? new String[] {"§7manage members", "§7shift-click to invite player"} : new String[] {"§7manage members"};
		inventory.setItem(12, ItemUtils.newItem(Material.PLAYER_HEAD, "§fmembers", memberLore));
		inventory.setItem(16, ItemUtils.newItem(Material.IRON_DOOR, "leave island"));
		
		if(this.data.isOwner(player)) {
			inventory.setItem(28, ItemUtils.newItem(Material.NAME_TAG, "rename", "§7click to rename the island"));
			inventory.setItem(30, ItemUtils.newItem(Material.CHEST, "privacy settings", "§7define what guests can interact with"));
			inventory.setItem(34, ItemUtils.newItem(Material.BARRIER, "delete", "§7deletes the island", "§7WARNING: this cannot be undone"));
		}
		
		return inventory;
	}
	
	@Override
	protected void onClick(final ClickEvent event) {
		final Player player = event.getPlayer();
		switch(event.getSlot()) {
		case 10:
			IslandManager.enterIsland(player, this.data);
			break;
		case 12:
			switch(event.getClickType()) {
			case LEFT:
			case RIGHT:
				this.memberMenu.open(player);
				break;
			case SHIFT_LEFT:
			case SHIFT_RIGHT:
				if(this.data.hasRank(player, 1)) {
					super.requestText(player, IslandSubMenu.TEXT_REQUEST_KEY_INVITE, "type the name in the chat");
				}
				break;
			default:
				break;
			}
			break;
		case 16:
			this.data.removeMember(player);
			player.getOpenInventory().close();
			break;
		case 28:
			if(this.data.isOwner(player)) {
				super.requestText(player, IslandSubMenu.TEXT_REQUEST_KEY_RENAME, "type the name in the chat");
			}
			break;
		case 30:
			if(this.data.isOwner(player)) {
				this.privacyMenu.open(player);
			}
			break;
		case 34:
			if(this.data.isOwner(player)) {
				new LambdaYesNoMenu("delete " + this.data.getName() + "?", this.data.getType().toShopItem(), this::delete).open(player);
			}
			break;
		default:
			break;
		}
	}
	
	@Override
	protected void onTextReceive(final TextEvent event) {
		switch(event.getKey()) {
		case IslandSubMenu.TEXT_REQUEST_KEY_RENAME:
			this.data.setName(event.getText());
			break;
		case IslandSubMenu.TEXT_REQUEST_KEY_INVITE:
			final Player player = Bukkit.getPlayer(event.getText());
			if(player == null) {
				event.getPlayer().sendMessage(event.getText() + " cannot be found.");
			} else {
				this.data.inviteMember(player);
				event.getPlayer().sendMessage(event.getText() +  " was invited to your island.");
			}
			break;
		default:
			break;
		}
	}
	
	private void delete(final Player player) {
		IslandManager.deleteIsland(data);
		player.getOpenInventory().close();
		
		player.sendMessage(this.data.getName() + " has been deleted.");
	}

}
