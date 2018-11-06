package me.yourname.amazing.path;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.util.BlockIterator;

public class LocationUtil {

	/*
	 * @return Location with the values of the generated string
	 */
	public static Location getLocationWithDirection(String locationString) {
		String[] array = locationString.split(", ");
		Location loc = new Location(Bukkit.getWorld(array[0]), Double.parseDouble(array[1]),
				Double.parseDouble(array[2]), Double.parseDouble(array[3]));
		loc.setPitch(Float.parseFloat(array[4]));
		loc.setYaw(Float.parseFloat(array[5]));
		return loc;
	}

	/*
	 * @return Location with the values of the generated string
	 */
	public static Location getLocation(String locationString) {
		String[] array = locationString.split(", ");
		return new Location(Bukkit.getWorld(array[0]), Double.parseDouble(array[1]), Double.parseDouble(array[2]),
				Double.parseDouble(array[3]));
	}

	/*
	 * @return generated string with the values of the given location
	 */
	public static String getStringWithDirection(Location location) {
		return location.getWorld().getName() + ", " + location.getBlockX() + ", " + location.getBlockY() + ", "
				+ location.getBlockZ() + ", " + location.getPitch() + ", " + location.getYaw();
	}

	/*
	 * @return generated string with the values of the given location
	 */
	public static String getString(Location location) {
		return location.getWorld().getName() + ", " + location.getBlockX() + ", " + location.getBlockY() + ", "
				+ location.getBlockZ();
	}

	public static boolean isUnderBlock(Location location) {
		for(int i =0; i<75;i++) {
			if(!new Location(location.getWorld(), location.getX(), location.getY()+i, location.getZ()).getBlock().getType().equals(Material.AIR)) {
				return true;
			}
		}
		return false;
	}
	
	/*
	 * @return boolean either the location is inside the to corner location or not
	 */
	public static boolean isInside(Location location, Location cornerA, Location cornerB) {
		double maxX = Math.max(cornerA.getX(), cornerB.getX());
		double maxY = Math.max(cornerA.getY(), cornerB.getY());
		double maxZ = Math.max(cornerA.getZ(), cornerB.getZ());
		double minX = Math.min(cornerA.getX(), cornerB.getX());
		double minY = Math.min(cornerA.getY(), cornerB.getY());
		double minZ = Math.min(cornerA.getZ(), cornerB.getZ());
		double x = location.getX();
		double y = location.getY();
		double z = location.getZ();
		if (maxX > x && x > minX) {
			if (maxY > y && y > minY) {
				if (maxZ > z && z > minZ) {
					return true;
				}
			}
		}
		return false;
	}
	
	
	/* @param player the used player
	 * @param range max range the block is away from the player
	 * @return Block the player is looking at
	 * */
    public static final Block getTargetBlock(Player player, int range) {
        BlockIterator iter = new BlockIterator(player, range);
        Block lastBlock = iter.next();
        while (iter.hasNext()) {
            lastBlock = iter.next();
            if (lastBlock.getType() == Material.AIR) {
                continue;
            }
            break;
        }
        return lastBlock;
    }
    
}
