package com.skitskurr.skyblocks.island.menu;

import java.util.Optional;

import org.bukkit.entity.Player;

import com.skitskurr.menumanager.implementations.FixedMenuItem;
import com.skitskurr.menumanager.implementations.LambdaYesNoMenu;
import com.skitskurr.menumanager.implementations.MenuItemClickEvent;
import com.skitskurr.skyblocks.Main;
import com.skitskurr.skyblocks.currency.Currency;
import com.skitskurr.skyblocks.currency.EmeraldCurrency;
import com.skitskurr.skyblocks.island.IslandManager;
import com.skitskurr.skyblocks.island.IslandType;

import net.md_5.bungee.api.ChatColor;

public class IslandShopMenuItem extends FixedMenuItem{
	
	private static final Currency CURRENCY = new EmeraldCurrency();
	
	private final IslandType type;

	public IslandShopMenuItem(final IslandType type) {
		super(type.toShopItem());
		
		this.type = type;
	}
	
	@Override
	public void onClick(final MenuItemClickEvent event) {
		final Player player = event.getPlayer();
		if(IslandShopMenuItem.CURRENCY.has(player, this.type.getPrice())) {
			new LambdaYesNoMenu("buy " + this.type.getName() + "?", this.type.toShopItem(), this::purchase).open(event.getPlayer());
		} else {
			player.sendMessage(ChatColor.RED + "You don't have sufficient emeralds to buy this island.");
		}
		
	}
	
	private void purchase(final Player player) {
		if(IslandShopMenuItem.CURRENCY.pay(player, this.type.getPrice())) {
			final Optional<Main> optionalPlugin = Main.getCurrent();
			if(!optionalPlugin.isPresent()) {
				player.sendMessage("There was an error during your purchase.");
				return;
			}
			IslandManager.createIsland(optionalPlugin.get(), player, this.type);
			player.sendMessage("Purchase successful.");
			player.getOpenInventory().close();
		} else {
			player.sendMessage(ChatColor.RED + "You don't have sufficient emeralds to buy this island.");
		}
	}

}
