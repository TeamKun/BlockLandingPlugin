package net.kunmc.lab.blocklandingplugin;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.block.Block;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;

public class GameManager {

    private final BlockLandingPlugin blockLandingPlugin;

    public GameManager(BlockLandingPlugin _blockLandingPlugin) {
        blockLandingPlugin = _blockLandingPlugin;
    }

    public static List<LandingBlockTask> blockList = new ArrayList<>();

    /**
     * @param player
     */
    public void start(Player player, int startY) {

        for (int i = 0; i < 5; i++) {
            //初期ブロック設定
            Location location = player.getLocation();
            location.setY(location.getY() + startY);
            Block block = location.getBlock();
            blockList.add(new LandingBlockTask(new BlockLandingRunnable(player, block, i, blockLandingPlugin),Material.ACACIA_WOOD));
        }
        blockList.get(0).getBlockLandingRunnable().runTaskTimer(blockLandingPlugin, 0, 20);
    }

}
