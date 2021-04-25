package net.kunmc.lab.blocklandingplugin;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scoreboard.Team;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * チーム単位でゲームを管理
 */
public class GameManager {

    private final BlockLandingPlugin blockLandingPlugin;
    private final Team team;
    //タスクから参照されるためstatic
    public static List<LandingBlockTask> blockList = new ArrayList<>();
    private Map<Integer, ItemStack> itemList = new HashMap<>();

    public GameManager(BlockLandingPlugin _blockLandingPlugin, Team team) {
        blockLandingPlugin = _blockLandingPlugin;
        this.team = team;
    }


    //ゲームを開始する
    public void start() {
        blockList = new ArrayList<>();
        List<OfflinePlayer> teamPlayers = team.getPlayers().stream().filter(OfflinePlayer::isOnline).collect(Collectors.toList());
        ConfigData configData = ConfigData.getInstance();
        int count = 0;
        Player player;

        Iterator<OfflinePlayer> iterator = teamPlayers.iterator();
        for (ItemStack item : itemList.values()) {
            for (int i = 0; i < item.getAmount(); i++) {
                //リストの最後までいった場合初期化
                if (!iterator.hasNext()) {
                    iterator = teamPlayers.iterator();
                }
                player = iterator.next().getPlayer();
                //初期ブロック設定
                assert player != null;
                Location location = player.getLocation();
                location.setY(location.getY() + configData.getStartY());
                Block block = location.getBlock();
                blockList.add(new LandingBlockTask(new BlockLandingRunnable(blockLandingPlugin, player, block, count++), item.getType()));
            }
        }

        blockList.get(0).getBlockLandingRunnable().runTaskTimer(blockLandingPlugin, 0, configData.getTaskRepeatTime());
    }

    public GameManager setItemList(Map<Integer, ItemStack> itemList) {
        this.itemList = itemList;
        return this;
    }

}
