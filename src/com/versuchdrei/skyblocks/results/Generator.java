package com.versuchdrei.skyblocks.results;

import java.util.Random;

import org.bukkit.Material;

public abstract class Generator {
	
	protected static class Result{
		private final int likelihood;
		private final Material type;
		
		protected Result(final int likelihood, final Material type) {
			this.likelihood = likelihood;
			this.type = type;
		}
	}
	
	private final Random random = new Random();
	private final Material[] defaults;
	private final Result[] results;
	
	public Generator(final Material[] defaults, final Result[] results) {
		this.defaults = defaults;
		this.results = results;
	}
	
	public Material generate() {
		int roll = random.nextInt(10000);
		for(final Result result: this.results) {
			if(roll < result.likelihood) {
				return result.type;
			}
			roll -= result.likelihood;
		}
		
		return defaults[random.nextInt(defaults.length)];
	}

}
