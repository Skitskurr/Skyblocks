package com.skitskurr.skyblocks.island;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.WorldCreator;
import org.bukkit.craftbukkit.libs.org.apache.commons.io.FileUtils;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.skitskurr.datamanager.DataManager;
import com.skitskurr.menumanager.utils.MetadataUtils;
import com.skitskurr.skyblocks.Main;

public class IslandManager {
	
	public static final Material[] interactables = new Material[] {Material.CHEST, Material.TRAPPED_CHEST, Material.BARREL, Material.SHULKER_BOX,
			Material.CAMPFIRE, Material.FURNACE, Material.BLAST_FURNACE, Material.BREWING_STAND, Material.DROPPER, Material.DISPENSER, Material.HOPPER,
			Material.CRAFTING_TABLE, Material.GRINDSTONE, Material.STONECUTTER, Material.ANVIL, Material.ENCHANTING_TABLE, Material.CARTOGRAPHY_TABLE,
			Material.OAK_DOOR, Material.BIRCH_DOOR, Material.SPRUCE_DOOR, Material.JUNGLE_DOOR, Material.ACACIA_DOOR, Material.DARK_OAK_DOOR,
			Material.OAK_FENCE_GATE, Material.BIRCH_FENCE_GATE, Material.SPRUCE_FENCE_GATE, Material.JUNGLE_FENCE_GATE, Material.ACACIA_FENCE_GATE, Material.DARK_OAK_FENCE_GATE,
			Material.OAK_TRAPDOOR, Material.BIRCH_TRAPDOOR, Material.SPRUCE_TRAPDOOR, Material.JUNGLE_TRAPDOOR, Material.ACACIA_TRAPDOOR, Material.DARK_OAK_TRAPDOOR,
			Material.LEVER, Material.REPEATER, Material.COMPARATOR,
			Material.OAK_BUTTON, Material.BIRCH_BUTTON, Material.SPRUCE_BUTTON, Material.JUNGLE_BUTTON, Material.ACACIA_BUTTON, Material.DARK_OAK_BUTTON, Material.STONE_BUTTON,
			Material.OAK_PRESSURE_PLATE, Material.BIRCH_PRESSURE_PLATE, Material.SPRUCE_PRESSURE_PLATE, Material.JUNGLE_PRESSURE_PLATE, Material.ACACIA_PRESSURE_PLATE,
			Material.DARK_OAK_PRESSURE_PLATE, Material.STONE_PRESSURE_PLATE, Material.LIGHT_WEIGHTED_PRESSURE_PLATE, Material.HEAVY_WEIGHTED_PRESSURE_PLATE};
	
	private static final String WORLD_DIR = "worlds/";
	private static final String ISLAND_DIR = "islands/";
	
	private static final String WORLD_HUB_NAME = "world";
	
	public static boolean createIsland(final Main plugin, final Player player, final IslandType type) {
		final String id = "" + new Date().getTime();
		
		if(!DataManager.Groups.addGroup(id, Main.PLUGIN_KEY)) {
			player.sendMessage(ChatColor.RED + "There was an unexpected error while registering your island.");
			return false;
		}

		DataManager.Groups.addMember(player, id, Main.PLUGIN_KEY);
		DataManager.Groups.set(id, Main.PLUGIN_KEY, IslandData.DATA_KEY_TYPE, type.toString());
		DataManager.Groups.set(id, Main.PLUGIN_KEY, IslandData.DATA_KEY_NAME, type.getName());
		DataManager.Groups.set(id, Main.PLUGIN_KEY, IslandData.DATA_KEY_OWNER, player.getUniqueId().toString());
		
		new BukkitRunnable() {
			@Override
			public void run() {
				final String suffix = type.getFileSuffix();
				for(final Environment environment: type.getEnvironments()) {
					final String prefix = getPrefix(environment);
					final File source = new File(IslandManager.ISLAND_DIR + prefix + suffix);
					final File destination = new File(IslandManager.WORLD_DIR + prefix + id);
					try {
						FileUtils.copyDirectory(source,  destination);
					} catch(final IOException ex) {
						player.sendMessage(ChatColor.RED + "There was an unexpected error while creating your island.");
					}
				}
				DataManager.Groups.set(id, Main.PLUGIN_KEY, IslandData.DATA_KEY_CONSTRUCTED, true);
			}
		}.runTaskAsynchronously(plugin);
		
		return true;
	}
	
	public static void deleteIsland(final IslandData data) {
		final String id = data.getId();
		final List<String> worldIds = Arrays.stream(data.getType().getEnvironments())
				.map(environment -> IslandManager.WORLD_DIR + getPrefix(environment) + id).collect(Collectors.toList());
		
		DataManager.Groups.deleteGroup(id, Main.PLUGIN_KEY);
		
		if(Bukkit.getWorld(worldIds.get(0)) != null) {
			for(final String worldId: worldIds) {
				final World world = Bukkit.getWorld(worldId);
				for(final Player player: world.getPlayers()) {
					enterHub(player);
				}
				Bukkit.unloadWorld(world, false);
			}
		}
		
		for(final String worldId: worldIds) {
			try {
				FileUtils.deleteDirectory(new File(worldId));
			} catch(final Exception ex) {
				ex.printStackTrace();
			}
		}
	}
	
	public static void enterIsland(final Player player, final IslandData data) {
		final String id = data.getId();
		if(!DataManager.Groups.getBoolean(id, Main.PLUGIN_KEY, IslandData.DATA_KEY_CONSTRUCTED, false)) {
			player.sendMessage(ChatColor.RED + "That island is still in construction.");
			return;
		}
		
		World world = Bukkit.getWorld(IslandManager.WORLD_DIR + getPrefix(Environment.NORMAL) + id);
		if(world == null) {
			for(final Environment environment: data.getType().getEnvironments()) {
				final String dir = IslandManager.WORLD_DIR + getPrefix(environment) + id;
				final File file = new File(dir);
				if(!file.exists()) {
					player.sendMessage(ChatColor.RED + "There was an unexpected error while loading your island.");
					return;
				}
				if(environment == Environment.NORMAL) {
					world = new WorldCreator(dir).createWorld();
				} else {
					new WorldCreator(dir).environment(environment).createWorld();
				}
			}
		}
		
		player.teleport(world.getSpawnLocation());
	}
	
	public static void enterHub(final Player player) {
		final World hub = Bukkit.getWorld(IslandManager.WORLD_HUB_NAME);
		if(hub != null) {
			player.teleport(hub.getSpawnLocation());
		}
	}
	
	public static void setSpawn(final Main plugin, final Player player) {
		final World world = player.getWorld();
		final Optional<IslandData> optionalData = MetadataUtils.getMetadata(plugin, world, IslandData.METADATA_KEY_ISLAND_DATA, IslandData.class);
		if(!optionalData.isPresent()) {
			player.sendMessage("You are not on an island.");
			return;
		}
		
		final IslandData data = optionalData.get();
		if(!data.hasRank(player, 1)) {
			player.sendMessage("You don't have permission to move this islands spawn.");
			return;
		}
		
		final Location location = player.getLocation();
		world.setSpawnLocation(location);
		player.sendMessage("Spawn location set to X: " + location.getBlockX() + ", Y: " + Math.ceil(location.getY()) + ", Z: " + location.getBlockZ() + ".");
	}
	
	static String getPrefix(final Environment environment) {
		switch(environment) {
		case NORMAL:
			return "world_";
		case NETHER:
			return "nether_";
		case THE_END:
			return "end_";
		default:
			return null;	
		}
	}

}
