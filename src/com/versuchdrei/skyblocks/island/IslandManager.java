package com.versuchdrei.skyblocks.island;

import java.io.File;
import java.util.ArrayList;
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
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;

import com.versuchdrei.datamanager.DataManager;
import com.versuchdrei.skyblocks.Main;
import com.versuchdrei.skyblocks.exceptions.WorldFileNotFoundException;
import com.versuchdrei.skyblocks.utils.JarUtils;
import com.versuchdrei.skyblocks.utils.MetadataUtils;

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
	
	public static final String WORLD_HUB_NAME = "world";
	
	public static final String DATA_KEY_DEFAULT_ISLAND = "defaultisland";
	
	public static final String METADATA_KEY_DEFAULT_ISLAND = "defaultisland";
	
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
					if(!JarUtils.copyFolder(IslandManager.ISLAND_DIR + prefix + suffix, IslandManager.WORLD_DIR + prefix + id)) {
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
			try {
				world = loadWorlds(data).get(0);
			}catch(final WorldFileNotFoundException ex) {
				player.sendMessage(ChatColor.RED + "ERROR: Your world seems to be missing files.");
				return;
			}
		}
		
		player.teleport(world.getSpawnLocation());
	}
	
	private static List<World> loadWorlds(final IslandData data) throws WorldFileNotFoundException{
		final Environment[] environments = data.getType().getEnvironments();
		final List<World> worlds = new ArrayList<World>(environments.length);
		final String id = data.getId();
		
		for(final Environment environment: environments) {
			final String dir = IslandManager.WORLD_DIR + getPrefix(environment) + id;
			final File file = new File(dir);
			if(!file.exists()) {
				for(final World world: worlds) {
					Bukkit.unloadWorld(world, false);
				}
				throw new WorldFileNotFoundException(dir);
			}
			worlds.add(new WorldCreator(dir).environment(environment).createWorld());
		}
		
		return worlds;
	}
	
	public static void loadDefaultIsland(final Player player) {
		Main.getCurrent().ifPresent(plugin -> player.setMetadata(IslandManager.METADATA_KEY_DEFAULT_ISLAND,
				new FixedMetadataValue(plugin, DataManager.Players.getString(player,Main.PLUGIN_KEY, IslandManager.DATA_KEY_DEFAULT_ISLAND, ""))));
	}
	
	public static void enterDefaultOrHub(final Player player) {
		final Optional<Main> optionalPlugin = Main.getCurrent();
		if(optionalPlugin.isPresent()) {
			final Optional<String> optionalDefaultIsland = MetadataUtils.getMetadata(optionalPlugin.get(), player, IslandManager.METADATA_KEY_DEFAULT_ISLAND, String.class);
			if(optionalDefaultIsland.isPresent()) {
				final String defaultIsland = optionalDefaultIsland.get();
				if(DataManager.Groups.getGroups(player, Main.PLUGIN_KEY).orElse(new ArrayList<>(0)).contains(defaultIsland)){
					IslandManager.enterIsland(player, IslandData.getData(defaultIsland));
				} else {
					IslandManager.enterHub(player);
				}
			} else {
				enterHub(player);
			}
		} else {
			enterHub(player);
		}
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
