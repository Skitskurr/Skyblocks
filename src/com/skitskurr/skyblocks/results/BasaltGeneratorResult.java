package com.skitskurr.skyblocks.results;

import java.util.Arrays;

import org.bukkit.Material;
import org.bukkit.block.Biome;

public enum BasaltGeneratorResult {
	GOLD("Gold", Material.GOLD_INGOT, Material.NETHER_GOLD_ORE, 500, 50, defaultBiomes()),
	QUARTZ("Quartz", Material.QUARTZ, Material.NETHER_QUARTZ_ORE, 250, 25, defaultBiomes()),
	MAGMA_BLOCK("Magma Block", Material.MAGMA_BLOCK, 250, 25, defaultBiomes()),
	GLOWSTONE("Glowstone", Material.GLOWSTONE, 100, 10, defaultBiomes()),
	ANCIENT_DEBRIS("Netherite Scrap", Material.NETHERITE_SCRAP, Material.ANCIENT_DEBRIS, 10, 1, defaultBiomes());
	
	public static Material[] getDefaultResult(final Biome biome) {
		switch(biome) {
		case NETHER_WASTES:
			return new Material[] {Material.BASALT, Material.NETHERRACK};
		case BASALT_DELTAS:
			return new Material[] {Material.BASALT, Material.NETHERRACK, Material.BLACKSTONE};
		default:
			return new Material[] {Material.BASALT};
		}
	}
	
	private static Biome[] defaultBiomes() {
		return new Biome[] {Biome.NETHER_WASTES, Biome.SOUL_SAND_VALLEY, Biome.CRIMSON_FOREST, Biome.WARPED_FOREST, Biome.BASALT_DELTAS};
	}
	
	private final String name;
	private final Material icon;
	private final Material result;
	private final int baseChance;
	private final int chancePerLevel;
	private final Biome[] biomes;
	
	private BasaltGeneratorResult(final String name, final Material icon, final Material result, final int baseChance, final int chancePerLevel, final Biome[] biomes) {
		this.name = name;
		this.icon = icon;
		this.result = result;
		this.baseChance = baseChance;
		this.chancePerLevel = chancePerLevel;
		this.biomes = biomes;
	}
	
	private BasaltGeneratorResult(final String name, final Material type, final int baseChance, final int chancePerLevel, final Biome[] biomes) {
		this.name = name;
		this.icon = type;
		this.result = type;
		this.baseChance = baseChance;
		this.chancePerLevel = chancePerLevel;
		this.biomes = biomes;
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
