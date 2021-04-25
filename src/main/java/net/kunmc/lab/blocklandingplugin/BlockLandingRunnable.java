package net.kunmc.lab.blocklandingplugin;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.BoundingBox;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

/**
 * 1ブロック落下までを管理
 */
public class BlockLandingRunnable extends BukkitRunnable {

    private Player player;
    private Block block;
    private int index;
    private int count;
    private Plugin plugin;

    public BlockLandingRunnable(Plugin plugin, Player player, Block block, int index) {
        this.plugin = plugin;
        this.player = player;
        this.block = block;
        this.index = index;
        this.count = 0;
    }

    //todo ふってこないアイテムだとcancelまでたどり着かずバグる
    public void run() {

        //次のブロックの位置決め
        Location location = block.getLocation();
        if (count % 2 == 0) {
            location.add(0, -1, 0);
        }
        location.setX(player.getLocation().getX());
        location.setZ(player.getLocation().getZ());

        //次のブロック生成
        Block nextBlock = location.getBlock();
        block.setType(Material.AIR);

        if (nextBlock.getType() != Material.AIR) {
            nextBlock = nextBlock.getLocation().add(0, 1, 0).getBlock();
        }
        nextBlock.setType(GameManager.blockList.get(index).getMaterial());
        block = nextBlock;

        //当り判定確認
        if (isHittingNextBlock(block)) {
            //次がある場合
            if (index + 1 < GameManager.blockList.size()) {
                ConfigData configData = ConfigData.getInstance();
                GameManager.blockList.get(index + 1).getBlockLandingRunnable().runTaskTimer(plugin, 0, configData.getTaskRepeatTime());
            } else {
                player.sendTitle("完成！", "", 10, 80, 10);
            }
            location.getWorld().spawnParticle(
                    Particle.SPELL_MOB,
                    location,
                    10
            );
            cancel();
        }
        count++;
    }

    /**
     * 上下左右にブロックがあるか確認する
     */
    private boolean isHittingNextBlock(Block block) {
        Location location = block.getLocation();
        List<Block> blockList = new ArrayList<Block>();

        blockList.add(location.clone().add(0, -1, 0).getBlock());
        blockList.add(location.clone().add(0, 1, 0).getBlock());
        blockList.add(location.clone().add(-1, 0, 0).getBlock());
        blockList.add(location.clone().add(1, 0, 0).getBlock());
        blockList.add(location.clone().add(0, 0, -1).getBlock());
        blockList.add(location.clone().add(0, 0, 1).getBlock());

        for (Block myBlock : blockList) {
            if (myBlock.getType() != Material.AIR) {
                return true;
            }
        }
        return false;
    }
}
