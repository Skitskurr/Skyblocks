package com.versuchdrei.skyblocks.island;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.World.Environment;
import org.bukkit.block.Biome;
import org.bukkit.inventory.ItemStack;

import com.versuchdrei.skyblocks.utils.ItemUtils;

public enum IslandType {
	CLASSIC("Classic Island", Material.GRASS_BLOCK, "has a generator", 16, false, false, Biome.PLAINS, "classic", Environment.NORMAL),
	FROZEN("Frozen Island", Material.SNOW_BLOCK, "it's cold", 32, false, false, Biome.SNOWY_TUNDRA, "frozen", Environment.NORMAL),
	DROUGHT("Drought Island", Material.SAND, "desert generator results", 32, false, false, Biome.DESERT, "drought", Environment.NORMAL),
	FOUR_SEASONS("Four Seasons", Material.MYCELIUM, "special generator results", 96, false, false, new Biome[] {Biome.DESERT, Biome.OCEAN, Biome.SNOWY_TAIGA, Biome.JUNGLE}, "fourSeasons", Environment.NORMAL),
	NETHER_GATE("Nether Gate Island", Material.NETHERRACK, "allows Nether Portals", 96, false, true, Biome.NETHER_WASTES, "netherGate", new Environment[] {Environment.NORMAL, Environment.NETHER}),
	BASALT_GATE("Basalt Gate Island", Material.BASALT, "allows Nether Portals", 96, false, true, Biome.BASALT_DELTAS, "basaltGate", new Environment[] {Environment.NORMAL, Environment.NETHER}),
	SOUL_GATE("Soul Gate Island", Material.SOUL_SAND, "allows Nether Portals", 96, false, true, Biome.SOUL_SAND_VALLEY, "soulGate", new Environment[] {Environment.NORMAL, Environment.NETHER}),
	//POPULATED("Populated Island", Material.COBBLESTONE, "spawns Zombie Villagers", 192, true, false, Biome.TAIGA, "populated", Environment.NORMAL),
	SWAMP_HUT("Swamp Hut Island", Material.PODZOL, "contains a Witch Hut", 192, false, false, Biome.SWAMP, "swampHut", Environment.NORMAL),
	FORTRESS_GATE("Fortress Gate Island", Material.NETHER_BRICKS, "Nether Gate to a Fortress", 192, false, true, Biome.NETHER_WASTES, "fortressGate", new Environment[] {Environment.NORMAL, Environment.NETHER}),
	OCEAN_MONUMENT("Ocean Monument Island", Material.SEA_LANTERN, "contains an Ocean Monument", 192, false, false, Biome.DEEP_OCEAN, "oceanMonument", Environment.NORMAL);
	//END_PORTAL("End Portal", Material.END_STONE, false, false, Biome.THE_END, new String[] {"world_endPortal", "end_endPortal"});

	private final String name;
	private final Material icon;
	private final String specialty;
	private final int price;
	private final boolean allowZombieVillager;
	private final boolean allowNether;
	private final Biome[] biomes;
	private final String fileSuffix;
	private final Environment[] environments;
	
	private IslandType(final String name, final Material icon, final String specialty, final int price, final boolean allowZombieVillager, final boolean allowNether, final Biome biome, final String fileSuffix, final Environment[] environments) {
		this.name = name;
		this.icon = icon;
		this.specialty = specialty;
		this.price = price;
		this.allowZombieVillager = allowZombieVillager;
		this.allowNether = allowNether;
		this.biomes = new Biome[] {biome};
		this.fileSuffix = fileSuffix;
		this.environments = environments;
	}
	
	private IslandType(final String name, final Material icon, final String specialty, final int price, final boolean allowZombieVillager, final boolean allowNether, final Biome[] biomes, final String fileSuffix, final Environment environment) {
		this.name = name;
		this.icon = icon;
		this.specialty = specialty;
		this.price = price;
		this.allowZombieVillager = allowZombieVillager;
		this.allowNether = allowNether;
		this.biomes = biomes;
		this.fileSuffix = fileSuffix;
		this.environments = new Environment[] {environment};
	}
	
	private IslandType(final String name, final Material icon, final String specialty, final int price, final boolean allowZombieVillager, final boolean allowNether, final Biome biome, final String fileSuffix, final Environment environment) {
		this.name = name;
		this.icon = icon;
		this.specialty = specialty;
		this.price = price;
		this.allowZombieVillager = allowZombieVillager;
		this.allowNether = allowNether;
		this.biomes = new Biome[] {biome};
		this.fileSuffix = fileSuffix;
		this.environments = new Environment[] {environment};
	}
	
	public String getName() {
		return this.name;
	}
	
	public Material getIcon() {
		return this.icon;
	}
	
	public int getPrice() {
		return this.price;
	}
	
	public boolean isZombieVillagerAllowed() {
		return this.allowZombieVillager;
	}
	
	public boolean isNetherAllowed() {
		return this.allowNether;
	}
	
	public String getFileSuffix() {
		return this.fileSuffix;
	}
	
	public Environment[] getEnvironments() {
		return this.environments;
	}
	
	public ItemStack toShopItem() {
		final List<String> lore = new ArrayList<>();
		addBiomeLore(lore);
		lore.add(specialtyLore());
		lore.add(priceLore());
		
		return ItemUtils.newItem(this.icon, this.name, lore);
	}
	
	private void addBiomeLore(final List<String> lore) {
		if(this.biomes.length == 1) {
			lore.add("§7noteworthy biome: §8" + biomeText(this.biomes[0]));
			return;
		} else {
			lore.add("§7noteworthy biomes: §8" + biomeText(this.biomes[0]) + "§7,");
		}
		
		final String biomeNames[] = new String[this.biomes.length - 1];
		for(int i = 1; i < this.biomes.length; i++) {
			final String[] split = this.biomes[i].toString().split("_");
			for(int j = 0; j < split.length; j++) {
				split[j] = split[j].substring(0, 1) + split[j].substring(1).toLowerCase();
			}
			biomeNames[i - 1] = biomeText(this.biomes[i]);
		}
		
		lore.add(String.join("§7, ", biomeNames));
	}
	
	private String specialtyLore() {
		return "§7specialty: " + this.specialty;
	}
	
	private String priceLore() {
		return "§7price: §8" + this.price + " §7Emeralds";
	}
	
	private static String biomeText(final Biome biome) {
		final String[] split = biome.toString().split("_");
		for(int i = 0; i < split.length; i++) {
			split[i] = split[i].substring(0, 1) + split[i].substring(1).toLowerCase();
		}
		return "§8" + String.join(" ", split);
	}
}
