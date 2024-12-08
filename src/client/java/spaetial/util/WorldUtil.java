package spaetial.util;

import net.minecraft.util.math.BlockBox;
import net.minecraft.world.WorldAccess;

public final class WorldUtil {
    private WorldUtil() {}

    /**
     * @return True, if all chunks intersecting the specified box are loaded on the client
     */
    public static boolean isLoaded(WorldAccess world, BlockBox box) {
        int minX = box.getMinX() >> 4;
        int minZ = box.getMinZ() >> 4;
        int maxX = box.getMaxX() >> 4;
        int maxZ = box.getMaxZ() >> 4;
        for (int x = minX; x <= maxX; x++) {
            for (int z = minZ; z <= maxZ; z++) {
                // why world.getChunkManager.isChunkLoaded instead of world.isChunkLoaded? see ClientWorld::isChunkLoaded
                if (!world.getChunkManager().isChunkLoaded(x, z)) return false;
            }
        }
        return true;
    }
}
