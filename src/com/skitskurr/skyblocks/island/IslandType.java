package com.skitskurr.skyblocks.island;

import org.bukkit.Material;
import org.bukkit.World.Environment;
import org.bukkit.block.Biome;
import org.bukkit.inventory.ItemStack;

import com.skitskurr.skyblocks.utils.ItemUtils;

public enum IslandType {
	CLASSIC("Classic Island", Material.GRASS_BLOCK, "has a generator", 1, false, false, Biome.PLAINS, "classic", Environment.NORMAL),
	FROZEN("Frozen Island", Material.SNOW_BLOCK, "it's cold", 1, false, false, Biome.SNOWY_TUNDRA, "frozen", Environment.NORMAL),
	DROUGHT("Drought Island", Material.SAND, "desert generator results", 1, false, false, Biome.DESERT, "drought", Environment.NORMAL),
	NETHER_GATE("Nether Gate Island", Material.NETHERRACK, "allows Nether Portals", 1, false, true, Biome.NETHER, "netherGate", new Environment[] {Environment.NORMAL, Environment.NETHER}),
	POPULATED("Populated Island", Material.COBBLESTONE, "spawns Zombie Villagers", 1, true, false, Biome.TAIGA, "populated", Environment.NORMAL),
	SWAMP_HUT("Swamp Hut Island", Material.PODZOL, "contains a Witch Hut", 1, false, false, Biome.SWAMP, "swampHut", Environment.NORMAL),
	FORTRESS_GATE("Fortress Gate Island", Material.NETHER_BRICKS, "Nether Gate to a Fortress", 1, false, true, Biome.NETHER, "fortressGate", new Environment[] {Environment.NORMAL, Environment.NETHER});
	//FOUR_SEASONS("Four Seasons", Material.MYCELIUM, false, new Biome[]{Biome.OCEAN, Biome.SNOWY_TUNDRA, Biome.DESERT, Biome.SWAMP}, new String[]{"world_fourSeasons"}),
	//END_PORTAL("End Portal", Material.END_STONE, false, false, Biome.THE_END, new String[] {"world_endPortal", "end_endPortal"});

	private final String name;
	private final Material icon;
	private final String specialty;
	private final int price;
	private final boolean allowZombieVillager;
	private final boolean allowNether;
	private final Biome biome;
	private final String fileSuffix;
	private final Environment[] environments;
	
	private IslandType(final String name, final Material icon, final String specialty, final int price, final boolean allowZombieVillager, final boolean allowNether, final Biome biome, final String fileSuffix, final Environment[] environments) {
		this.name = name;
		this.icon = icon;
		this.specialty = specialty;
		this.price = price;
		this.allowZombieVillager = allowZombieVillager;
		this.allowNether = allowNether;
		this.biome = biome;
		this.fileSuffix = fileSuffix;
		this.environments = environments;
	}
	
	private IslandType(final String name, final Material icon, final String specialty, final int price, final boolean allowZombieVillager, final boolean allowNether, final Biome biome, final String fileSuffix, final Environment environment) {
		this.name = name;
		this.icon = icon;
		this.specialty = specialty;
		this.price = price;
		this.allowZombieVillager = allowZombieVillager;
		this.allowNether = allowNether;
		this.biome = biome;
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
		return ItemUtils.newItem(this.icon, this.name, biomeLore(), specialtyLore(), priceLore());
	}
	
	private String biomeLore() {
		final String[] split = this.biome.toString().split("_");
		for(int i = 0; i < split.length; i++) {
			split[i] = split[i].substring(0, 1) + split[i].substring(1).toLowerCase();
		}
		return "§7noteworthy biome: §8" + String.join(" ", split);
	}
	
	private String specialtyLore() {
		return "§7specialty: " + this.specialty;
	}
	
	private String priceLore() {
		return "§7price: §8" + this.price + " §7Emeralds";
	}
}
