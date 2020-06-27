package com.skitskurr.skyblocks;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.WanderingTrader;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.MerchantRecipe;
import org.bukkit.metadata.FixedMetadataValue;

import com.skitskurr.skyblocks.island.IslandManager;
import com.skitskurr.skyblocks.island.IslandType;
import com.skitskurr.skyblocks.results.WanderingTraderRecipe;
import com.skitskurr.skyblocks.title.TitleManager;
import com.skitskurr.skyblocks.utils.MetadataUtils;

import net.minecraft.server.v1_16_R1.MinecraftServer;

public class EventListener implements Listener{
	
	private static final String METADATA_KEY_LAST_NOTE_BLOCK_TICK = "lastNoteBlockTick";
	
	private final Main plugin;
	
	public EventListener(final Main plugin) {
		this.plugin = plugin;
	}
	
	@EventHandler
	public void onJoin(final PlayerJoinEvent event) {
		final Player player = event.getPlayer();
		player.teleport(Bukkit.getWorld("world").getSpawnLocation());
		
		TitleManager.loadTitles(this.plugin, player, () -> IslandManager.createIsland(this.plugin, player, IslandType.CLASSIC));
		
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
	}

}
