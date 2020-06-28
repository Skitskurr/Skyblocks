package com.skitskurr.skyblocks.island;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Biome;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.skitskurr.datamanager.DataManager;
import com.skitskurr.skyblocks.Main;
import com.skitskurr.skyblocks.results.BasaltGenerator;
import com.skitskurr.skyblocks.results.CobblestoneGenerator;
import com.skitskurr.skyblocks.results.Composter;
import com.skitskurr.skyblocks.utils.ItemUtils;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ClickEvent.Action;
import net.md_5.bungee.api.chat.ComponentBuilder;

public class IslandData {
	
	public static final String METADATA_KEY_ISLAND_DATA = "islanddata";
	
	// group data keys
	static final String DATA_KEY_TYPE = "type";
	static final String DATA_KEY_NAME = "name";
	static final String DATA_KEY_CONSTRUCTED = "constructed";
	static final String DATA_KEY_GUEST_TYPES = "guesttypes";
	static final String DATA_KEY_OWNER = "owner";
	
	// player data keys
	static final String DATA_KEY_RANK = "rank";
	
	private static final int MAX_RANK = 1;
	
	private static final Map<String, IslandData> datas = new HashMap<>();
	
	public static IslandData getData(final String id) {
		if(!datas.containsKey(id)) {
			datas.put(id, new IslandData(id));
		}
		return datas.get(id);
	}
	
	private final String id;
	private final IslandType type;
	private String name;
	private final UUID ownerID;
	private final List<Material> guestTypes;
	
	private final List<UUID> memberIDs;
	private final Map<UUID, Integer> ranks = new HashMap<>();
	
	private final Map<Biome, CobblestoneGenerator> cobblestoneGenerators = new HashMap<>();
	private final Map<Biome, BasaltGenerator> basaltGenerators = new HashMap<>();
	private final Map<Biome, Composter> composters = new HashMap<>();
	private final Set<UUID> invited = new HashSet<>();
	
	private IslandData(final String id) {
		this.id = id;
		this.type = IslandType.valueOf(DataManager.Groups.getString(id, Main.PLUGIN_KEY, IslandData.DATA_KEY_TYPE, IslandType.CLASSIC.toString()));
		this.name = DataManager.Groups.getString(id, Main.PLUGIN_KEY, IslandData.DATA_KEY_NAME, "ERROR");
		this.ownerID = UUID.fromString(DataManager.Groups.getString(id, Main.PLUGIN_KEY, IslandData.DATA_KEY_OWNER, ""));
		this.guestTypes = DataManager.Groups.getList(id, Main.PLUGIN_KEY, IslandData.DATA_KEY_GUEST_TYPES, new ArrayList<>())
				.stream().map(type -> Material.valueOf(type)).collect(Collectors.toList());
		this.memberIDs = DataManager.Groups.getMemberIDs(id, Main.PLUGIN_KEY).orElse(new ArrayList<>());
	}
	
	public void setName(final String name) {
		this.name = name;
		DataManager.Groups.set(this.id, Main.PLUGIN_KEY, IslandData.DATA_KEY_NAME, name);
	}
	
	public String getId() {
		return this.id;
	}
	
	public IslandType getType() {
		return this.type;
	}
	
	public String getName() {
		return this.name;
	}
	
	public ItemStack getIcon() {
		return ItemUtils.newItem(this.type.getIcon(), this.name);
	}
	
	public Material generateCobblestoneResult(final Biome biome) {
		if(!cobblestoneGenerators.containsKey(biome)) {
			cobblestoneGenerators.put(biome, new CobblestoneGenerator(biome));
		}
		
		return cobblestoneGenerators.get(biome).generate();
	}
	
	public Material generateBasaltResult(final Biome biome) {
		if(!basaltGenerators.containsKey(biome)) {
			basaltGenerators.put(biome, new BasaltGenerator(biome));
		}
		
		return basaltGenerators.get(biome).generate();
	}
	
	public Material compostBlock(final Biome biome) {
		if(!composters.containsKey(biome)) {
			composters.put(biome, new Composter(biome));
		}
		
		return composters.get(biome).compost();
	}
	
	public void toggleGuestType(final Material type) {
		if(this.guestTypes.contains(type)) {
			this.guestTypes.remove(type);
		} else {
			this.guestTypes.add(type);
		}
		DataManager.Groups.set(this.id, Main.PLUGIN_KEY, IslandData.DATA_KEY_GUEST_TYPES, this.guestTypes
				.stream().map(type2 -> type2.toString()).collect(Collectors.toList()));
	}
	
	public boolean canGuestUse(final Material type) {
		return this.guestTypes.contains(type);
	}
	
	public boolean isOwner(final UUID uuid) {
		return this.ownerID.equals(uuid);
	}
	
	public boolean isOwner(final OfflinePlayer player) {
		return isOwner(player.getUniqueId());
	}
	
	public void addMember(final Player player) {
		final UUID uuid = player.getUniqueId();
		if(this.memberIDs.contains(uuid)) {
			return;
		}
		
		DataManager.Groups.addMember(player, this.id, Main.PLUGIN_KEY);
		this.memberIDs.add(uuid);
	}
	
	public void removeMember(final Player player) {
		final UUID uuid = player.getUniqueId();
		if(!this.memberIDs.contains(uuid)) {
			return;
		}
		
		if(this.ownerID.equals(uuid)) {
			return;
		}
		
		DataManager.Groups.removeMember(player, this.id, Main.PLUGIN_KEY);
		this.memberIDs.remove(uuid);
	}
	
	public void removeMember(final UUID uuid) {
		if(!this.memberIDs.contains(uuid)) {
			return;
		}
		
		if(this.ownerID.equals(uuid)) {
			return;
		}
		
		DataManager.Groups.removeMember(Bukkit.getOfflinePlayer(uuid), this.id, Main.PLUGIN_KEY);
		this.memberIDs.remove(uuid);
	}
	
	public boolean isMember(final UUID uuid) {
		return this.memberIDs.contains(uuid);
	}
	
	public boolean isMember(final Player player) {
		return isMember(player.getUniqueId());
	}
	
	public List<UUID> getMemnberIDs() {
		return this.memberIDs;
	}
	
	public void inviteMember(final Player player) {
		final UUID uuid = player.getUniqueId();
		if(this.memberIDs.contains(uuid)) {
			return;
		}
		
		invited.add(uuid);
		
		player.spigot().sendMessage(new ComponentBuilder("You have been invited to ").color(ChatColor.GRAY)
				.append(this.name).color(ChatColor.DARK_GRAY)
				.append(". Click ").color(ChatColor.GRAY)
				.append(">>here<<").event(new ClickEvent(Action.RUN_COMMAND, "/island " + this.id + " accept")).color(ChatColor.GOLD)
				.append(" to accept.").color(ChatColor.GRAY).create());
	}
	
	public void acceptInvite(final Player player) {
		final UUID uuid = player.getUniqueId();
		if(!this.invited.contains(uuid)) {
			return;
		}
		
		addMember(player);
		invited.remove(uuid);
	}
	
	public void promote(final UUID uuid) {
		setRank(uuid, Math.min(IslandData.MAX_RANK, getRank(uuid) + 1));
	}
	
	public void demote(final UUID uuid) {
		setRank(uuid, Math.max(0, getRank(uuid) - 1));
	}
	
	public boolean hasRank(final UUID uuid, final int rank) {
		if(this.ownerID.equals(uuid)) {
			return true;
		}
		
		return getRank(uuid) >= rank;
	}
	
	public boolean hasRank(final OfflinePlayer player, final int rank) {
		return hasRank(player.getUniqueId(), rank);
	}
	
	public int getRank(final UUID uuid) {
		if(this.ownerID.equals(uuid)) {
			return IslandData.MAX_RANK + 1;
		}
		
		if(!this.ranks.containsKey(uuid)) {
			this.ranks.put(uuid, DataManager.Groups.getInt(Bukkit.getOfflinePlayer(uuid), this.id, Main.PLUGIN_KEY, IslandData.DATA_KEY_RANK, 0));
		}
		
		return this.ranks.get(uuid);
	}
	
	public int getRank(final OfflinePlayer player) {
		return getRank(player.getUniqueId());
	}
	
	private void setRank(final UUID uuid, final int rank) {
		this.ranks.put(uuid, rank);
		DataManager.Groups.set(this.id, Main.PLUGIN_KEY, IslandData.DATA_KEY_RANK, rank);
	}

}
