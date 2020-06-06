package com.skitskurr.skyblocks.results;

import java.util.Arrays;
import java.util.Random;
import java.util.stream.Collectors;

import org.bukkit.Material;
import org.bukkit.block.Biome;

public class Composter {
	
	private static class Result{
		private final int likelihood;
		private final Material type;
		
		private Result(final int likelihood, final Material type) {
			this.likelihood = likelihood;
			this.type = type;
		}
	}
	
	private final Random random = new Random();
	private final Result[] results;
	
	public Composter(final Biome biome) {
		this.results = Arrays.stream(ComposterResult.values())
				.filter(result -> result.isApplicable(biome))
				.map(result -> new Result(result.getChance(0), result.getResult()))
				.collect(Collectors.toList()).toArray(new Result[0]);
	}
	
	public Material compost() {
		int roll = random.nextInt(10000);
		for(final Result result: this.results) {
			if(roll < result.likelihood) {
				return result.type;
			}
			roll -= result.likelihood;
		}
		
		return Material.BONE_MEAL;
	}

}
