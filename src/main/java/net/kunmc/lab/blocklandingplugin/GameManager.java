package net.kunmc.lab.blocklandingplugin;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 1チェスト単位で管理
 */
public class GameManager {

    private final BlockLandingPlugin blockLandingPlugin;

    public GameManager(BlockLandingPlugin _blockLandingPlugin) {
        blockLandingPlugin = _blockLandingPlugin;
    }

    public static List<LandingBlockTask> blockList = new ArrayList<>();

    /**
     * @param player
     */
    public void start(Player player, HashMap<Integer, ItemStack> items) {
        ConfigData configData = ConfigData.getInstance();
        int count = 0;
        for (ItemStack item : items.values()) {
            for(int i = 0 ; i < item.getAmount() ; i ++){
                //初期ブロック設定
                Location location = player.getLocation();
                location.setY(location.getY() + configData.getStartY());
                Block block = location.getBlock();
                blockList.add(new LandingBlockTask(new BlockLandingRunnable(blockLandingPlugin,player, block, count++),item.getType()));
            }
        }
        blockList.get(0).getBlockLandingRunnable().runTaskTimer(blockLandingPlugin, 0, 20);
    }

}
