package com.skitskurr.skyblocks.results;

import java.util.Arrays;

import org.bukkit.Material;
import org.bukkit.block.Biome;

public enum GeneratorResult {
	// "classic" Skyblock results
	COAL("Coal", Material.COAL, Material.COAL_ORE, 1000, 100, defaultBiomes()),
	IRON("Iron", Material.IRON_INGOT, Material.IRON_ORE, 500, 50, defaultBiomes()),
	GOLD("Gold", Material.GOLD_INGOT, Material.GOLD_ORE, 250, 25, defaultBiomes()),
	REDSTONE("Redstone", Material.REDSTONE, Material.REDSTONE_ORE, 100, 10, defaultBiomes()),
	LAPIS_LAZULI("Lapis Lazuli", Material.LAPIS_LAZULI, Material.LAPIS_ORE, 100, 10, defaultBiomes()),
	EMERALD("Emerald", Material.EMERALD, Material.EMERALD_ORE, 50, 5, defaultBiomes()),
	DIAMOND("Diamond", Material.DIAMOND, Material.DIAMOND_ORE, 10, 1, defaultBiomes()),
	
	// not so classic results
	GRANITE("Granite", Material.GRANITE, 100, 10, defaultBiomes()),
	ANDESITE("Andesite", Material.ANDESITE, 100, 10, defaultBiomes()),
	
	// desert results
	TERRACOTTA("Terracotta", Material.TERRACOTTA, 1500, 150, Biome.DESERT),
	DESERT_GOLD("Gold", Material.GOLD_INGOT, Material.GOLD_ORE, 1000, 50, Biome.DESERT),
	DESERT_GRANITE("Granite", Material.GRANITE, 500, 50, Biome.DESERT),
	BONE("Bones", Material.BONE, Material.BONE_BLOCK, 100, 10, Biome.DESERT),
	
	//ocean results
	MAGMA_BLOCK("Magma Block", Material.MAGMA_BLOCK, 250, 25, Biome.DEEP_OCEAN),
	SEA_LANTERN("Sea Lantern", Material.PRISMARINE_CRYSTALS, Material.SEA_LANTERN, 100, 10, Biome.DEEP_OCEAN);
	
	public static Material[] getDefaultResult(final Biome biome) {
		switch(biome) {
		case DESERT:
			return new Material[] {Material.SANDSTONE, Material.SANDSTONE, Material.SANDSTONE, Material.RED_SANDSTONE};
		case OCEAN:
			return new Material[] {Material.TUBE_CORAL_BLOCK, Material.BRAIN_CORAL_BLOCK, Material.BUBBLE_CORAL_BLOCK, Material.FIRE_CORAL_BLOCK, Material.HORN_CORAL_BLOCK};
		case THE_END:
			return new Material[] {Material.END_STONE};
		default:
			return new Material[] {Material.COBBLESTONE};
		}
	}
	
	private static Biome[] defaultBiomes() {
		return new Biome[]{Biome.PLAINS, Biome.TAIGA, Biome.SNOWY_TUNDRA, Biome.SWAMP};
	}

	private final String name;
	private final Material icon;
	private final Material result;
	private final int baseChance;
	private final int chancePerLevel;
	private final Biome[] biomes;
	
	private GeneratorResult(final String name, final Material icon, final Material result, final int baseChance, final int chancePerLevel, final Biome[] biomes){
		this.name = name;
		this.icon = icon;
		this.result = result;
		this.baseChance = baseChance;
		this.chancePerLevel = chancePerLevel;
		this.biomes = biomes;
	}
	
	private GeneratorResult(final String name, final Material type, final int baseChance, final int chancePerLevel, final Biome[] biomes){
		this.name = name;
		this.icon = type;
		this.result = type;
		this.baseChance = baseChance;
		this.chancePerLevel = chancePerLevel;
		this.biomes = biomes;
	}
	
	private GeneratorResult(final String name, final Material icon, final Material result, final int baseChance, final int chancePerLevel, final Biome biome) {
		this.name = name;
		this.icon = icon;
		this.result = result;
		this.baseChance = baseChance;
		this.chancePerLevel = chancePerLevel;
		this.biomes = new Biome[] {biome};
	}
	
	private GeneratorResult(final String name, final Material type, final int baseChance, final int chancePerLevel, final Biome biome) {
		this.name = name;
		this.icon = type;
		this.result = type;
		this.baseChance = baseChance;
		this.chancePerLevel = chancePerLevel;
		this.biomes = new Biome[] {biome};
	}
	
	public String getName() {
		return this.name;
	}
	
	public Material getIcon() {
		return this.icon;
	}
	
	public Material getResult() {
		return this.result;
	}
	
	public int getChance(final int level) {
		return this.baseChance + level * this.chancePerLevel;
	}
	
	public boolean isApplicable(final Biome biome) {
		return Arrays.stream(this.biomes).anyMatch(biome::equals);
	}
}
