package com.versuchdrei.skyblocks;

import java.util.Optional;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.java.JavaPlugin;

import com.versuchdrei.datamanager.DataManager;
import com.versuchdrei.menumanager.implementations.SlimeMap;
import com.versuchdrei.menumanager.implementations.Trade;
import com.versuchdrei.skyblocks.island.IslandData;
import com.versuchdrei.skyblocks.island.IslandEventListener;
import com.versuchdrei.skyblocks.island.IslandManager;
import com.versuchdrei.skyblocks.island.menu.IslandMenu;
import com.versuchdrei.skyblocks.utils.MetadataUtils;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ClickEvent.Action;
import net.md_5.bungee.api.chat.ComponentBuilder;


public class Main extends JavaPlugin{
	
	public static final String PLUGIN_KEY = "versuchdrei_skyblocks";
	
	public static final String METADATA_KEY_TP_REQUEST = "tp_request";
	public static final String METADATA_KEY_TRADE_REQUEST = "trade_request";
	
	private static final IslandMenu ISLAND_MENU = new IslandMenu();
	private static final SlimeMap SLIME_MAP = new SlimeMap();
	
	private static Main current;
	
	/**
	 * returns the current instance of the main plugin
	 * @return the instance of the main plugin
	 */
	public static Optional<Main> getCurrent(){
		if(current == null) {
			return Optional.empty();
		}
		return Optional.of(current);
	}
	
	@Override
	public void onEnable() {
		Main.current = this;
		Bukkit.getPluginManager().registerEvents(new EventListener(this), this);
		Bukkit.getPluginManager().registerEvents(new IslandEventListener(this), this);
	}
	
	@Override
	public void onDisable() {
		
	}
	
	@Override
	public boolean onCommand(final CommandSender sender, final Command command, final String label,
			final String[] args) {
		
		if(command.getLabel().equals("hub")) {
			if(!(sender instanceof Player)) {
				return true;
			}
			
			IslandManager.enterHub((Player) sender);
			return true;
		}
		
		if(command.getLabel().equals("island")) {
			if(!(sender instanceof Player)) {
				return true;
			}
			
			final Player player = (Player) sender;
			
			if(args.length == 0) {
				Main.ISLAND_MENU.open(player);
				return true;
			}
			
			if(!DataManager.Groups.isGroup(args[0], Main.PLUGIN_KEY)) {
				player.sendMessage(ChatColor.RED + args[0] + " is not an island.");
				return true;
			}
			
			final IslandData data = IslandData.getData(args[0]);
			
			if(args.length == 2) {
				if(args[1].equals("accept")) {
					data.acceptInvite(player);
				}
			}
			
			return true;
		}
		
		if(command.getLabel().equals("default")) {
			if(!(sender instanceof Player)) {
				return true;
			}
			
			IslandManager.enterDefaultOrHub((Player) sender);
			return true;
		}
		
		if(command.getLabel().equals("setspawn")) {
			if(!(sender instanceof Player)) {
				return true;
			}
			
			IslandManager.setSpawn(this, (Player) sender);
			return true;
		}
		
		if(command.getLabel().equals("join")) {
			if(!(sender instanceof Player)) {
				return true;
			}
			if(args.length != 1) {
				sender.sendMessage("Invalid number of arguments.");
				return true;
			}
			final Player target = Bukkit.getPlayer(args[0]);
			if(target == null) {
				sender.sendMessage(args[0] + " could not be found.");
				return true;
			}
			final Optional<IslandData> optionalData = MetadataUtils.getMetadata(this, target.getWorld(), IslandData.METADATA_KEY_ISLAND_DATA, IslandData.class);
			if(!optionalData.isPresent()) {
				sender.sendMessage(args[0] + " is not on an island.");
				return true;
			}
			final IslandData data = optionalData.get();
			final Player actor = (Player) sender;
			if(!data.isMember(actor)) {
				sender.sendMessage("You cannot enter that island.");
				return true;
			}
			IslandManager.enterIsland(actor, data);
			return true;
		}
		
		if(command.getLabel().equals("tpa")) {
			if(!(sender instanceof Player)) {
				return true;
			}
			if(args.length != 1) {
				sender.sendMessage("Invalid number of arguments.");
				return true;
			}
			final Player player = Bukkit.getPlayer(args[0]);
			if(player == null) {
				sender.sendMessage(args[0] + " could not be found.");
				return true;
			}
			((Player) sender).setMetadata(Main.METADATA_KEY_TP_REQUEST, new FixedMetadataValue(this, player.getUniqueId()));
			player.spigot().sendMessage(new ComponentBuilder(sender.getName() + " requested to teleport to you. Click ").color(ChatColor.GRAY)
					.append(">>here<<").event(new ClickEvent(Action.RUN_COMMAND, "/tpaccept " + sender.getName())).color(ChatColor.GOLD)
					.append(" to accept.").color(ChatColor.GRAY).create());
			return true;
		}
		
		if(command.getLabel().equals("tpaccept")) {
			if(!(sender instanceof Player)) {
				return true;
			}
			if(args.length != 1) {
				sender.sendMessage("Invalid number of arguments.");
				return true;
			}
			final Player target = Bukkit.getPlayer(args[0]);
			if(target == null) {
				sender.sendMessage(args[0] + " could not be found.");
				return true;
			}
			final Player actor = (Player) sender;
			final Optional<UUID> optionalUuid = MetadataUtils.getMetadata(this, target, Main.METADATA_KEY_TP_REQUEST, UUID.class);
			if(!optionalUuid.isPresent() || !optionalUuid.get().equals(actor.getUniqueId())){
				sender.sendMessage("That player did not request a teleport.");
				return true;
			}
			
			target.removeMetadata(Main.METADATA_KEY_TP_REQUEST, this);
			target.teleport(actor.getLocation());
			return true;
		}
		
		if(command.getLabel().equals("trade")) {
			if(!(sender instanceof Player)) {
				return true;
			}
			if(args.length != 1) {
				sender.sendMessage("Invalid number of arguments.");
				return true;
			}
			final Player player = Bukkit.getPlayer(args[0]);
			if(player == null) {
				sender.sendMessage(args[0] + " could not be found.");
				return true;
			}
			((Player) sender).setMetadata(Main.METADATA_KEY_TRADE_REQUEST, new FixedMetadataValue(this, player.getUniqueId()));
			player.spigot().sendMessage(new ComponentBuilder(sender.getName() + " requested a trade with you. Click ").color(ChatColor.GRAY)
					.append(">>here<<").event(new ClickEvent(Action.RUN_COMMAND, "/tradeaccept " + sender.getName())).color(ChatColor.GOLD)
					.append(" to accept.").color(ChatColor.GRAY).create());
			return true;
		}
		
		if(command.getLabel().equals("tradeaccept")) {
			if(!(sender instanceof Player)) {
				return true;
			}
			if(args.length != 1) {
				sender.sendMessage("Invalid number of arguments.");
				return true;
			}
			final Player target = Bukkit.getPlayer(args[0]);
			if(target == null) {
				sender.sendMessage(args[0] + " could not be found.");
				return true;
			}
			final Player actor = (Player) sender;
			final Optional<UUID> optionalUuid = MetadataUtils.getMetadata(this, target, Main.METADATA_KEY_TRADE_REQUEST, UUID.class);
			if(!optionalUuid.isPresent() || !optionalUuid.get().equals(actor.getUniqueId())){
				sender.sendMessage("That player did not request a trade.");
				return true;
			}
			
			target.removeMetadata(Main.METADATA_KEY_TRADE_REQUEST, this);
			Trade.open(actor, target);
			return true;
		}
		
		if(command.getLabel().equals("slimemap")) {
			if(!(sender instanceof Player)) {
				return true;
			}
			
			Main.SLIME_MAP.open((Player) sender);
			return true;
		}
		
		return false;
		
	}
}
