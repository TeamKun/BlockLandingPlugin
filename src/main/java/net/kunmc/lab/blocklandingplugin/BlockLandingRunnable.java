package net.kunmc.lab.blocklandingplugin;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * 1ブロック落下までを管理
 */
public class BlockLandingRunnable extends BukkitRunnable {

    Player player;
    Block block;
    int index;
    int count = 0;
    Plugin plugin;

    public BlockLandingRunnable(Plugin plugin, Player player, Block block, int index) {
        this.plugin = plugin;
        this.player = player;
        this.block = block;
        this.index = index;
    }

    public void run() {
        //最初の1ターンの場合ブロック生成
        //todo これいらんかも
        if (count == 0) {
            block.setType(GameManager.blockList.get(index).getMaterial());
        }

        //次のブロックの位置決め
        Location location = block.getLocation();
        location.add(0, -0.1, 0);
        location.setX(player.getLocation().getX());
        location.setZ(player.getLocation().getZ());

        //次のブロック生成
        Block nextBlock = location.getBlock();
        nextBlock.setType(block.getType());

        block.setType(Material.AIR);
        block = nextBlock;
        count++;

        //todo 当たり判定
        if (count >= 10) {
            if (index + 1 < GameManager.blockList.size()) {
                GameManager.blockList.get(index + 1).getBlockLandingRunnable().runTaskTimer(plugin, 0, 20);
            }
            cancel();
        }
    }
}
