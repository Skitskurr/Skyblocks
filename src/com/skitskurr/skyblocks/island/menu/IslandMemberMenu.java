package com.skitskurr.skyblocks.island.menu;

import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.entity.Player;

import com.skitskurr.menumanager.implementations.MenuItem;
import com.skitskurr.menumanager.implementations.ScrollableMenu;
import com.skitskurr.skyblocks.island.IslandData;

public class IslandMemberMenu extends ScrollableMenu{
	
	private final IslandData data;

	public IslandMemberMenu(final IslandData data) {
		super("members");
		
		this.data = data;
	}

	@Override
	protected List<? extends MenuItem> getItems(final Player player) {
		return this.data.getMemnberIDs().stream().map(uuid -> new IslandMemberMenuItem(this.data, uuid)).collect(Collectors.toList());
	}

}
