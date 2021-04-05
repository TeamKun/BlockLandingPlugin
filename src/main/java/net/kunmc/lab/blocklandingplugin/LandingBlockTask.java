package net.kunmc.lab.blocklandingplugin;

import org.bukkit.Material;

public class LandingBlockTask {

    private BlockLandingRunnable blockLandingRunnable;

    private Material material;

    /**
     * @param blockLandingRunnable
     * @param material
     */
    public LandingBlockTask(BlockLandingRunnable blockLandingRunnable, Material material) {
        this.blockLandingRunnable = blockLandingRunnable;
        this.material = material;
    }

    public BlockLandingRunnable getBlockLandingRunnable() {
        return blockLandingRunnable;
    }

    public Material getMaterial(){
        return material;
    }
}
