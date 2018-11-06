/*
 * MIT License
 *
 * Copyright (c) 2018 Maximilian Oswald
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package location;

import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.block.Block;

import java.util.LinkedList;

public class Region {

    @Getter
    private int id;

    @Getter
    private Location positionMax, positionMin;

    public Region(int id, Location pos1, Location pos2) {
        this.id = id;
        if(pos1.getWorld() != pos2.getWorld()) {
            System.err.println("ERROR: both region position have to be in the same world");
        }

        int maxX = Math.max(pos1.getBlockX(), pos2.getBlockX());
        int maxY = Math.max(pos1.getBlockZ(), pos2.getBlockZ());
        int maxZ = Math.max(pos1.getBlockZ(), pos2.getBlockZ());

        int minX = Math.min(pos1.getBlockX(), pos2.getBlockX());
        int minY = Math.min(pos1.getBlockZ(), pos2.getBlockZ());
        int minZ = Math.min(pos1.getBlockZ(), pos2.getBlockZ());

        this.positionMax = new Location(pos1.getWorld(), maxX, maxY, maxZ);
        this.positionMin = new Location(pos1.getWorld(), minX, minY, minZ);
    }

    public boolean insideRegion(Location location) {
        if(location.getBlockX() <= positionMax.getBlockX() && location.getBlockX() >= positionMin.getBlockX()) {
            if(location.getBlockY() <= positionMax.getBlockY() && location.getBlockY() >= positionMin.getBlockY()) {
                if(location.getBlockZ() <= positionMax.getBlockZ() && location.getBlockZ() >= positionMin.getBlockZ()) {
                    return true;
                }
            }
        }
        return false;
    }

    public LinkedList<Block> getBlocks() {
        LinkedList<Block> blocks = new LinkedList<>();
        for(int x = positionMin.getBlockX(); x <= positionMax.getBlockX(); x++) {
            for(int y = positionMin.getBlockX(); y <= positionMax.getBlockX(); y++) {
                for(int z = positionMin.getBlockX(); z <= positionMax.getBlockX(); z++) {
                    blocks.add(new Location(positionMin.getWorld(), x, y, z).getBlock());
                }
            }
        }
        return blocks;
    }

}
