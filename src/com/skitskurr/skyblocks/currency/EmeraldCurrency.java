package com.skitskurr.skyblocks.currency;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class EmeraldCurrency implements Currency{
	
	private static class PaymentSlot{
		
		private final int slot;
		private final int amount;
		
		private PaymentSlot(final int slot, final int amount) {
			this.slot = slot;
			this.amount = amount;
		}
		
		private void pay(final Inventory inventory) {
			final ItemStack item = inventory.getItem(this.slot);
			item.setAmount(item.getAmount() - this.amount);
			inventory.setItem(this.slot, item);
		}
	}

	@Override
	public boolean has(final Player player, final int amount) {
		final Inventory inventory = player.getInventory();
		int remaining = amount;
		for(final ItemStack item: inventory.getContents()) {
			if(item != null && item.getType() == Material.EMERALD) {
				remaining -= item.getAmount();
				if(remaining <= 0) {
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public boolean pay(final Player player, final int amount) {
		final Inventory inventory = player.getInventory();
		int remaining = amount;
		final List<PaymentSlot> payments = new ArrayList<>();
		for(int i = 0; i < 36; i++) {
			final ItemStack item = inventory.getItem(i);
			if(item != null && item.getType() == Material.EMERALD) {
				final int itemAount = item.getAmount();
				if(remaining > itemAount) {
					remaining -= itemAount;
					payments.add(new PaymentSlot(i, itemAount));
				} else {
					payments.add(new PaymentSlot(i, remaining));
					for(final PaymentSlot payment: payments) {
						payment.pay(inventory);
					}
					return true;
				}
			}
		}
		return false;
	}

}
