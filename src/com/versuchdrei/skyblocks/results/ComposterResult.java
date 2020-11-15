package com.versuchdrei.skyblocks.results;

import java.util.Arrays;

import org.bukkit.Material;
import org.bukkit.block.Biome;

public enum ComposterResult {
	DIRT("Dirt", Material.DIRT, 500, 50, defaultBiomes()),
	BROWN_MUSHROOM("Brown Mushroom", Material.BROWN_MUSHROOM, 250, 25, defaultBiomes()),
	RED_MUSHROOM("Red Mushroom", Material.RED_MUSHROOM, 250, 25, defaultBiomes()),
	
	// desert results
	SAND("Sand", Material.SAND, 500, 50, Biome.DESERT),
	
	// nether results
	SOUL_SAND("Soul Sand", Material.SOUL_SAND, 250, 25, netherBiomes()),
	SOUL_SOIL("Soul Soil", Material.SOUL_SOIL, 250, 25, netherBiomes()),
	CRIMSON_FUNGUS("Crimson Fungus", Material.CRIMSON_FUNGUS, 250, 25, netherBiomes()),
	WARPED_FUNGUS("Warped Fungus", Material.WARPED_FUNGUS, 250, 25, netherBiomes());
	
	private final String name;
	private final Material icon;
	private final Material result;
	private final int baseChance;
	private final int chancePerLevel;
	private final Biome[] biomes;
	
	private static Biome[] defaultBiomes() {
		return new Biome[]{Biome.PLAINS, Biome.TAIGA, Biome.SNOWY_TUNDRA, Biome.SWAMP};
	}
	
	private static Biome[] netherBiomes() {
		return new Biome[] {Biome.NETHER_WASTES, Biome.SOUL_SAND_VALLEY, Biome.CRIMSON_FOREST, Biome.WARPED_FOREST, Biome.BASALT_DELTAS};
	}
	
	private ComposterResult(final String name, final Material icon, final Material result, final int baseChance, final int chancePerLevel, final Biome[] biomes) {
		this.name = name;
		this.icon = icon;
		this.result = result;
		this.baseChance = baseChance;
		this.chancePerLevel = chancePerLevel;
		this.biomes = biomes;
	}
	
	private ComposterResult(final String name, final Material type, final int baseChance, final int chancePerLevel, final Biome[] biomes) {
		this.name = name;
		this.icon = type;
		this.result = type;
		this.baseChance = baseChance;
		this.chancePerLevel = chancePerLevel;
		this.biomes = biomes;
	}
	
	private ComposterResult(final String name, final Material type, final int baseChance, final int chancePerLevel, final Biome biome) {
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
