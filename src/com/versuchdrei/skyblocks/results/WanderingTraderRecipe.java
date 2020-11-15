package com.versuchdrei.skyblocks.results;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MerchantRecipe;

public enum WanderingTraderRecipe {

	// tier 1: saplings
	OAK_SAPLING(Material.OAK_SAPLING, 1, 3, 1, 4),
	BIRCH_SAPLING(Material.BIRCH_SAPLING, 1, 3, 1, 4),
	SPRUCE_SAPLING(Material.SPRUCE_SAPLING, 1, 3, 1, 4),
	JUNGLE_SAPLING(Material.JUNGLE_SAPLING, 1, 3, 1, 4),
	ACACIA_SAPLING(Material.ACACIA_SAPLING, 1, 3, 1, 4),
	DARK_OAK_SAPLING(Material.DARK_OAK_SAPLING, 1, 3, 1, 4),
	
	// tier 1: plants
	WHEAT_SEEDS(Material.WHEAT_SEEDS, 1, 5, 1, 5),
	POTATO(Material.POTATO, 1, 4, 2, 3),
	CARROT(Material.CARROT, 1, 4, 2, 3),
	BEETROOT_SEEDS(Material.BEETROOT_SEEDS, 1, 4, 2, 3),
	SWEET_BERRIES(Material.SWEET_BERRIES, 1, 4, 2, 3),
	MELON_SEEDS(Material.MELON_SEEDS, 1, 2, 3, 2),
	PUMPKIN_SEEDS(Material.PUMPKIN_SEEDS, 1, 2, 3, 2),
	CACTUS(Material.CACTUS, 1, 1, 2, 2),
	
	// tier 1: other
	DIRT(Material.DIRT, 1, 2, 1, 5),
	SAND(Material.SAND, 1, 2, 1, 5),
	MAGMA_BLOCK(Material.MAGMA_BLOCK, 1, 2, 1, 4),
	
	// tier 2
	BAMBOO(Material.BAMBOO, 2, 2, 4, 2),
	KELP(Material.KELP, 2, 2, 4, 2),
	SUGAR_CANE(Material.SUGAR_CANE, 2, 2, 4, 2),
	OBISIDIAN(Material.OBSIDIAN, 2, 1, 3, 4),
	NETHER_WART(Material.NETHER_WART, 2, 1, 5, 1),
	SOUL_SAND(Material.SOUL_SAND, 2, 2, 5, 5),
	SPONGE(Material.SPONGE, 2, 1, 3, 3),
	EGG(Material.EGG, 2, 4, 1, 3),
	NAME_TAG(Material.NAME_TAG, 2, 1, 2, 3),
	
	// tier 3
	TURTLE_EGG(Material.TURTLE_EGG, 3, 1, 10, 2),
	SHULKER_BOX(Material.SHULKER_BOX, 3, 1, 16, 1),
	HEAR_OF_THE_SEA(Material.HEART_OF_THE_SEA, 3, 1, 32, 1),
	TOTEM_OF_UNDYING(Material.TOTEM_OF_UNDYING, 3, 1, 12, 2),
	ELYTRA(Material.ELYTRA, 3, 1, 64, 1);
	
	private final Material type;
	private final int tier;
	private final int amount;
	private final int price;
	private final int stock;
	
	private WanderingTraderRecipe(final Material type, final int tier, final int amount, final int price, final int stock) {
		this.type = type;
		this.tier = tier;
		this.amount = amount;
		this.price = price;
		this.stock = stock;
	}
	
	public int getTier() {
		return this.tier;
	}
	
	public MerchantRecipe toRecipe() {
		final MerchantRecipe recipe = new MerchantRecipe(new ItemStack(this.type, this.amount), this.stock);
		if(this.price > 64) {
			recipe.addIngredient(new ItemStack(Material.EMERALD_BLOCK, this.price / 9));
			recipe.addIngredient(new ItemStack(Material.EMERALD, this.price % 9));
		} else {
			recipe.addIngredient(new ItemStack(Material.EMERALD, this.price));
		}
		
		return recipe;
	}

}
