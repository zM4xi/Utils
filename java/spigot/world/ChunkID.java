package me.yourname.amazing.path;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;

public class ChunkID {

	private String chunkIDString;
	
	/*
	 * @param converts the chunk values in writable format
	 * */
	public ChunkID(Chunk chunk) {
		this.chunkIDString = chunk.getX() + "|" + chunk.getZ() + " -" + chunk.getWorld().getName();
	}
	
	/*
	 * @return local converted string
	 * */
	public String getChunkID() {
		return chunkIDString;
	}
	
	/*
	 * @return ChunkID from a converted string
	 * */
	public static ChunkID fromString(String chunkstring) {
		String st = chunkstring.split(" -")[0];
		int x = Integer.parseInt(st.split(" | ")[0]);
		int z = Integer.parseInt(st.split(" | ")[1]);
		return new ChunkID(Bukkit.getWorld(chunkstring.split(" -")[1]).getChunkAt(x, z));
	}
	
	
}
