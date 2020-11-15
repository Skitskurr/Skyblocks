package com.versuchdrei.skyblocks.results;

import java.util.Arrays;
import java.util.stream.Collectors;

import org.bukkit.block.Biome;

public class CobblestoneGenerator extends Generator{
	
	public CobblestoneGenerator(final Biome biome) {
		super(CobblestoneGeneratorResult.getDefaultResult(biome), getResults(biome));
	}
	
	private static Result[] getResults(final Biome biome) {
		return Arrays.stream(CobblestoneGeneratorResult.values())
		.filter(result -> result.isApplicable(biome))
		.map(result -> new Result(result.getChance(0), result.getResult()))
		.collect(Collectors.toList()).toArray(new Result[0]);
	}

}
