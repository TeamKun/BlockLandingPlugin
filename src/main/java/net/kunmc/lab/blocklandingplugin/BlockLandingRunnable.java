package net.kunmc.lab.blocklandingplugin;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

public class BlockLandingRunnable extends BukkitRunnable {

    Player player = null;
    Block block = null;
    int index;
    int count = 0;
    Plugin plugin;

    /**
     * @param player
     * @param block
     * @param index
     * @param plugin
     */
    public BlockLandingRunnable(Player player, Block block, int index, Plugin plugin
    ) {
        this.player = player;
        this.block = block;
        this.index = index;
        this.plugin = plugin;
    }

    public void run() {
        //最初の1ターンの場合ブロック生成
        if (count == 0) {
            block.setType(GameManager.blockList.get(index).getMaterial());
        }

        //次のブロックの位置決め
        Location location = block.getLocation();
        location.setY(location.getY() - 1);
        location.setX(player.getLocation().getX());
        location.setZ(player.getLocation().getZ());

        //次のブロック生成
        Block nextBlock = location.getBlock();
        nextBlock.setType(block.getType());

        block.setType(Material.AIR);
        block = nextBlock;
        count++;
        if (count >= 10) {
            GameManager.blockList.get(index + 1).getBlockLandingRunnable().runTaskTimer(plugin, 0, 20);
            cancel();
        }
    }
}
