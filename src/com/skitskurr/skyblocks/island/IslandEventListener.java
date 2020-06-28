package com.skitskurr.skyblocks.island;

import java.util.Arrays;
import java.util.Optional;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Levelled;
import org.bukkit.craftbukkit.v1_16_R1.inventory.CraftBlockInventoryHolder;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockFormEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.event.world.PortalCreateEvent;
import org.bukkit.event.world.PortalCreateEvent.CreateReason;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;

import com.skitskurr.datamanager.DataManager;
import com.skitskurr.menumanager.utils.MetadataUtils;
import com.skitskurr.skyblocks.Main;

public class IslandEventListener implements Listener{
	
	private final Main plugin;
	
	public IslandEventListener(final Main plugin) {
		this.plugin = plugin;
	}
	
	@EventHandler(ignoreCancelled = true)
	public void onLoad(final WorldLoadEvent event) {
		final World world = event.getWorld();
		final String name = world.getName();
		final String island = name.substring(name.lastIndexOf('_') + 1);
		if(!DataManager.Groups.isGroup(island, Main.PLUGIN_KEY)) {
			return;
		}
		world.setKeepSpawnInMemory(false);
		world.setMetadata(IslandData.METADATA_KEY_ISLAND_DATA, new FixedMetadataValue(this.plugin, IslandData.getData(island)));
	}
	
	/**
	 * generates ores on water and lava colliding
	 * @param event
	 */
	@EventHandler(ignoreCancelled = true)
	public void onGenerate(final BlockFormEvent event) {
		switch(event.getNewState().getType()) {
		case COBBLESTONE:
			final Block cobblestone = event.getBlock();
			MetadataUtils.getMetadata(this.plugin, cobblestone.getWorld(), IslandData.METADATA_KEY_ISLAND_DATA, IslandData.class)
			.ifPresent(data -> event.getNewState().setType(data.generateCobblestoneResult(cobblestone.getBiome())));
			break;
		case BASALT:
			final Block basalt = event.getBlock();
			MetadataUtils.getMetadata(this.plugin, basalt.getWorld(), IslandData.METADATA_KEY_ISLAND_DATA, IslandData.class)
			.ifPresent(data -> event.getNewState().setType(data.generateBasaltResult(basalt.getBiome())));
			break;
		default:
			break;
		}
	}
	
	/**
	 * generates dirt/mushrooms on composter clearing
	 * @param event
	 */
	@EventHandler(ignoreCancelled = true)
	public void onHopper(final InventoryMoveItemEvent event) {
		final InventoryHolder holder = event.getSource().getHolder();
		
		if(!(holder instanceof CraftBlockInventoryHolder)) {
			return;
		}
		
		final CraftBlockInventoryHolder craftHolder = (CraftBlockInventoryHolder) holder;
		final Block block = craftHolder.getBlock();
		if(!(block.getType() == Material.COMPOSTER)) {
			return;
		}
		
		MetadataUtils.getMetadata(this.plugin, block.getWorld(), IslandData.METADATA_KEY_ISLAND_DATA, IslandData.class)
		.ifPresent(data -> event.setItem(new ItemStack(data.compostBlock(block.getBiome()))));
	}
	
	/**
	 * generates dirt/mushrooms on composter clearing
	 * @param event
	 */
	@EventHandler(ignoreCancelled = true)
	public void onInteractComposter(final PlayerInteractEvent event){
		if(event.getAction() != Action.RIGHT_CLICK_BLOCK) {
			return;
		}
		
		final Block block = event.getClickedBlock();
		if(block.getType() != Material.COMPOSTER) {
			return;
		}
		
		final BlockData data = block.getBlockData();
		if(!(data instanceof Levelled)) {
			return;
		}
		
		final Levelled levelled = (Levelled) data;
		if(levelled.getLevel() != levelled.getMaximumLevel()) {
			return;
		}
		
		final Optional<IslandData> optionalData = MetadataUtils.getMetadata(this.plugin, block.getWorld(), IslandData.METADATA_KEY_ISLAND_DATA, IslandData.class);
		if(!optionalData.isPresent()) {
			return;
		}
		
		event.setCancelled(true);
		levelled.setLevel(0);
		block.setBlockData(levelled);
		block.getWorld().dropItemNaturally(block.getLocation().add(0.5, 1, 0.5), new ItemStack(optionalData.get().compostBlock(block.getBiome())));
	}
	
	@EventHandler(ignoreCancelled = true)
	public void onBreak(final BlockBreakEvent event) {
		final Player player = event.getPlayer();
		
		final Optional<IslandData> optionalData = MetadataUtils.getMetadata(plugin, player.getWorld(), IslandData.METADATA_KEY_ISLAND_DATA, IslandData.class);
		if(!optionalData.isPresent()) {
			if(!player.isOp()) {
				event.setCancelled(true);
			}
			return;
		}
		
		final IslandData data = optionalData.get();
		if(!data.isMember(player)) {
			event.setCancelled(true);
		}
	}
	
	@EventHandler(ignoreCancelled = true)
	public void onPlace(final BlockPlaceEvent event) {
		final Player player = event.getPlayer();
		
		final Optional<IslandData> optionalData = MetadataUtils.getMetadata(plugin, player.getWorld(), IslandData.METADATA_KEY_ISLAND_DATA, IslandData.class);
		if(!optionalData.isPresent()) {
			if(!player.isOp()) {
				event.setCancelled(true);
			}
			return;
		}
		
		final IslandData data = optionalData.get();
		if(!data.isMember(player)) {
			event.setCancelled(true);
		}
	}
	
	@EventHandler(ignoreCancelled = true)
	public void onInteract(final PlayerInteractEvent event) {
		if(!event.hasBlock()) {
			return;
		}
		final Material type = event.getClickedBlock().getType();
		if(!Arrays.stream(IslandManager.interactables).anyMatch(type::equals) && !type.equals(Material.FARMLAND)) {
			return;
		}
		
		final Player player = event.getPlayer();
		if(player.getGameMode() == GameMode.SPECTATOR) {
			return;
		}
		
		final Optional<IslandData> optionalData = MetadataUtils.getMetadata(plugin, player.getWorld(), IslandData.METADATA_KEY_ISLAND_DATA, IslandData.class);
		if(!optionalData.isPresent()) {
			return;
		}
		
		final IslandData data = optionalData.get();
		if(!data.isMember(player) && !data.canGuestUse(type)) {
			event.setCancelled(true);
		}
	}
	
	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
	public void onSpawn(final CreatureSpawnEvent event) {
		final Optional<IslandData> optionalData = MetadataUtils.getMetadata(this.plugin, event.getLocation().getWorld(), IslandData.METADATA_KEY_ISLAND_DATA, IslandData.class);
		if(!optionalData.isPresent()) {
			event.setCancelled(true);
			return;
		}
		
		if(event.getEntityType() != EntityType.ZOMBIE_VILLAGER) {
			return;
		}
		
		event.setCancelled(optionalData.map(data -> !data.getType().isZombieVillagerAllowed()).orElse(true));
	}
	
	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
	public void onPortalCreation(final PortalCreateEvent event) {
		if(event.getReason() != CreateReason.FIRE) {
			return;
		}
		
		event.setCancelled(MetadataUtils.getMetadata(this.plugin, event.getWorld(), IslandData.METADATA_KEY_ISLAND_DATA, IslandData.class)
				.map(data -> !data.getType().isNetherAllowed()).orElse(true));
	}
	
	@EventHandler(ignoreCancelled = true)
	public void onPortalUse(final PlayerPortalEvent event) {
		if(event.getCause() != TeleportCause.NETHER_PORTAL) {
			return;
		}
		
		final Location from = event.getFrom();
		final World worldFrom = from.getWorld();
		final Environment environmentFrom = worldFrom.getEnvironment();
		final String prefixFrom = IslandManager.getPrefix(environmentFrom);
		final String prefixTo = IslandManager.getPrefix(environmentFrom == Environment.NORMAL ? Environment.NETHER : Environment.NORMAL);
		
		final World worldTo = Bukkit.getWorld(worldFrom.getName().replace(prefixFrom, prefixTo));
		
		event.setTo(new Location(worldTo, from.getX(), from.getY(), from.getZ()));
	}

}
