package com.skitskurr.skyblocks.title;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;

import com.skitskurr.datamanager.DataManager;
import com.skitskurr.skyblocks.Main;

public class TitleManager {
	
	private static final String DATA_KEY_TITLES = "titles";
	
	private static final String METADATA_KEY_TITLES = "titles";
	
	public static void loadTitles(final Main plugin, final Player player, final Runnable onFirstTime) {
		final Optional<List<String>> optionalRankStrings = DataManager.Players.getList(player, Main.PLUGIN_KEY, TitleManager.DATA_KEY_TITLES);
		
		if(optionalRankStrings.isPresent()) {
			player.setMetadata(TitleManager.METADATA_KEY_TITLES, new FixedMetadataValue(plugin, optionalRankStrings.get().stream().map(rankString -> Title.valueOf(rankString)).collect(Collectors.toList())));
		} else {
			final List<Title> titles = new ArrayList<>();
			titles.add(Title.PLAYER);
			titles.add(Title.ALPHA);
			player.setMetadata(TitleManager.METADATA_KEY_TITLES, new FixedMetadataValue(plugin, titles));
			saveRanks(plugin, player, titles);
			onFirstTime.run();
		}
	}
	
	private static void saveRanks(final Main plugin, final Player player, final List<Title> titles) {
		DataManager.Players.set(player, Main.PLUGIN_KEY, TitleManager.DATA_KEY_TITLES, titles.stream().map(rank -> rank.toString()).collect(Collectors.toList()));
	}

}
