package com.sandbox.game;

import java.util.Arrays;

public class MapGen {
	
	private static final int GRASS = 0;
	private static final int WATER = 1;
	
	public static int[][] generateMap(double waterChance, int iteration, int x, int y) {
		
		final int sizeX = x;
		final int sizeY = y;
		
		// Map is in format X, Y.
		int[][] map = new int[sizeX][sizeY];
		Arrays.fill(map, 0);
	
		
		
		return map;
		
	}
	
}
