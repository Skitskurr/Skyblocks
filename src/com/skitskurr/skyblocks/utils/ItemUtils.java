package com.skitskurr.skyblocks.utils;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ItemUtils {
	
	public static ItemStack newItem(final Material type, final String name) {
		return setName(new ItemStack(type), name);
	}
	
	public static ItemStack newItem(final Material type, final String name, final int amount) {
		return setName(new ItemStack(type, amount), name);
	}
	
	public static ItemStack newItem(final Material type, final String name, final List<String> lore) {
		return setNameAndLore(new ItemStack(type), name, lore);
	}
	
	public static ItemStack newItem(final Material type, final String name, final String... lore) {
		return setNameAndLore(new ItemStack(type), name, lore);
	}
	
	public static ItemStack setName(final ItemStack item, final String name) {
		final ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(name);
		item.setItemMeta(meta);
		return item;
	}

	public static ItemStack setLore(final ItemStack item, final List<String> lore) {
		final ItemMeta meta = item.getItemMeta();
		meta.setLore(lore);
		item.setItemMeta(meta);
		return item;
	}

	public static ItemStack setLore(final ItemStack item, final String lore) {
		return setLore(item, Collections.singletonList(lore));
	}

	public static ItemStack setLore(final ItemStack item, final String... lore) {
		return setLore(item, Arrays.asList(lore));
	}

	public static ItemStack setNameAndLore(final ItemStack item, final String name, final List<String> lore) {
		final ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(name);
		meta.setLore(lore);
		item.setItemMeta(meta);
		return item;
	}

	public static ItemStack setNameAndLore(final ItemStack item, final String name, final String lore) {
		return setNameAndLore(item, name, Collections.singletonList(lore));
	}

	public static ItemStack setNameAndLore(final ItemStack item, final String name, final String... lore) {
		return setNameAndLore(item, name, Arrays.asList(lore));
	}

	public static ItemStack[] splitIntoStacks(final Material type, final int amount) {
		if (amount == 0) {
			return new ItemStack[0];
		}
		final int maxStackSize = type.getMaxStackSize();
		final int max = amount / maxStackSize;
		final int remainder = amount % maxStackSize;
		final ItemStack[] items = new ItemStack[remainder == 0 ? max : max + 1];

		for (int i = 0; i < max; i++) {
			items[i] = new ItemStack(type, maxStackSize);
		}
		if (remainder != 0) {
			items[max] = new ItemStack(type, remainder);
		}

		return items;
	}

}
