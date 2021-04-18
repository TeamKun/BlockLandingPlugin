package net.kunmc.lab.blocklandingplugin;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scoreboard.Team;

import java.util.*;

/**
 * 1チェスト単位で管理
 */
public class GameManager {

    private Team team;
    private final BlockLandingPlugin blockLandingPlugin;

    public GameManager(BlockLandingPlugin _blockLandingPlugin, Team team) {
        blockLandingPlugin = _blockLandingPlugin;
        this.team = team;
    }

    public static List<LandingBlockTask> blockList = new ArrayList<>();

    public Map<Integer, ItemStack> itemList = new HashMap<>();

    public void start(Player player) {
        blockList = new ArrayList<>();
        Set<OfflinePlayer> teamPlayers = team.getPlayers();
        ConfigData configData = ConfigData.getInstance();
        int count = 0;
        for (ItemStack item : itemList.values()) {
            for (int i = 0; i < item.getAmount(); i++) {
                //初期ブロック設定
                Location location = player.getLocation();
                location.setY(location.getY() + configData.getStartY());
                Block block = location.getBlock();
                blockList.add(new LandingBlockTask(new BlockLandingRunnable(blockLandingPlugin, player, block, count++), item.getType()));
            }
        }

        blockList.get(0).getBlockLandingRunnable().runTaskTimer(blockLandingPlugin, 0, 20);
    }

    public GameManager setItemList(Map<Integer, ItemStack> itemList) {
        this.itemList = itemList;
        return this;
    }

}
