package com.versuchdrei.skyblocks;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.WanderingTrader;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.MerchantRecipe;
import org.bukkit.metadata.FixedMetadataValue;

import com.versuchdrei.skyblocks.island.IslandManager;
import com.versuchdrei.skyblocks.island.IslandType;
import com.versuchdrei.skyblocks.results.WanderingTraderRecipe;
import com.versuchdrei.skyblocks.title.TitleManager;
import com.versuchdrei.skyblocks.utils.MetadataUtils;

import net.md_5.bungee.api.ChatColor;
import net.minecraft.server.v1_16_R3.MinecraftServer;

public class EventListener implements Listener{
	
	private static final String METADATA_KEY_LAST_NOTE_BLOCK_TICK = "lastNoteBlockTick";
	
	private final Main plugin;
	
	public EventListener(final Main plugin) {
		this.plugin = plugin;
	}
	
	@EventHandler
	public void onJoin(final PlayerJoinEvent event) {
		final Player player = event.getPlayer();
		
		TitleManager.loadTitles(this.plugin, player, () -> IslandManager.createIsland(this.plugin, player, IslandType.CLASSIC));
		
		IslandManager.loadDefaultIsland(player);
		IslandManager.enterDefaultOrHub(player);
		
		player.sendMessage("Welcome, use /island to manage your islands.");
	}
	
	@EventHandler
	public void onInteract(final PlayerInteractEvent event) {
		if(event.getAction() != Action.RIGHT_CLICK_BLOCK) {
			return;
		}
		
		if(event.getClickedBlock().getType() != Material.NOTE_BLOCK) {
			return;
		}
		
		final Player player = event.getPlayer();
		final int lastTick = MetadataUtils.getMetadata(this.plugin, player, EventListener.METADATA_KEY_LAST_NOTE_BLOCK_TICK, Integer.class).orElse(0);
		final int currentTick = MinecraftServer.currentTick;
		
		if(lastTick + 15 > currentTick) {
			player.kickPlayer("Spamming causes lag!");
		} else {
			player.setMetadata(EventListener.METADATA_KEY_LAST_NOTE_BLOCK_TICK, new FixedMetadataValue(this.plugin, currentTick));
		}
	}
	
	@EventHandler
	public void onSpawn(final CreatureSpawnEvent event) {
		if(!(event.getEntity().getType() == EntityType.WANDERING_TRADER)) {
			return;
		}

		final Random random = new Random();
		final WanderingTrader trader = (WanderingTrader) event.getEntity();
		final List<MerchantRecipe> trades = new ArrayList<>();
		
		final Map<Integer, List<WanderingTraderRecipe>> recipes = Arrays.stream(WanderingTraderRecipe.values())
				.collect(Collectors.groupingBy(recipe -> recipe.getTier(), Collectors.toList()));
		
		final List<WanderingTraderRecipe> tier1 = recipes.get(1);
		Collections.shuffle(tier1);
		for(int i = 0; i < 4; i++) {
			trades.add(tier1.get(i).toRecipe());
		}
		
		final List<WanderingTraderRecipe> tier2 = recipes.get(2);
		trades.add(tier2.get(random.nextInt(tier2.size())).toRecipe());
		
		if(random.nextInt(10) == 0) {
			final List<WanderingTraderRecipe> tier3 = recipes.get(3);
			trades.add(tier3.get(random.nextInt(tier3.size())).toRecipe());
		}
			
		trader.setRecipes(trades);
		
		for(final Player player: trader.getWorld().getPlayers()) {
			player.sendMessage(ChatColor.YELLOW + "A Wandering Trader is visiting your island!");
		}
	}
	
	@EventHandler
	public void onExplode(final EntityExplodeEvent event) {
		if(event.getEntityType() != EntityType.PRIMED_TNT) {
			return;
		}
		
		event.setCancelled(true);
		
		final Location location = event.getEntity().getLocation();
		
		final Material locType = location.getBlock().getType();
		if(locType == Material.WATER || locType == Material.LAVA) {
			return;
		}
		
		final World world = location.getWorld();
		final int blockX = location.getBlockX();
		final int blockY = location.getBlockY();
		final int blockZ = location.getBlockZ();
		
		int x = blockX - 1;
		int y = blockY - 1;
		int z = blockZ - 1;
		
		while(z < blockZ + 2) {
			if(y >= 0 && y < world.getMaxHeight()) {
				final Block block = world.getBlockAt(x, y, z);
				final Material type = block.getType();
				if(type.isBlock() && type.getBlastResistance() <= 10) {
					block.breakNaturally();
				}
			}
			
			// nested loops are expensive, so we do this stuff instead
			if(++x == blockX + 2) {
				x = blockX - 1;
				if(++y == blockY + 2) {
					y = blockY - 1;
					z++;
				}
			}
		}
	}

}
