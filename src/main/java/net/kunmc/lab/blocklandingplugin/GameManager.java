package net.kunmc.lab.blocklandingplugin;
import net.kunmc.lab.blocklandingplugin.team.LandingTeam;
import net.kunmc.lab.blocklandingplugin.team.turn.LandingTurn;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scoreboard.Team;
import java.util.*;
import java.util.stream.Collectors;

/**
 * チーム単位でゲームを管理
 */
public class GameManager {
    private List<LandingTeam> landingTeamList;

    public void start(){
        //各チームに対して順番に処理を行う
        for(LandingTeam landingTeam : this.landingTeamList){
            //ブロック・プレイヤー読み込み
            LandingTurn landingTurn = landingTeam.getLandingTurn();
            Block block = landingTurn.getBlock();
            Player player = landingTurn.getPlayer();

            //ブロック初回生成
            if(block == null){

            }

            //次のブロックの位置決め
            Location location = block.getLocation();
            if (landingTeam.getCount() % 2 == 0) {
                location.add(0, -1, 0);
            }
            location.setX(player.getLocation().getX());
            location.setZ(player.getLocation().getZ());

            //次のブロック生成
            Block nextBlock = location.getBlock();
            block.setType(Material.AIR);

            //移動位置にブロックがあった場合一つ上に移動
            if (nextBlock.getType() != Material.AIR) {
                nextBlock = nextBlock.getLocation().add(0, 1, 0).getBlock();
            }
            nextBlock.setType(block.getType());
            block = nextBlock;

            //当り判定
            if(isHittingNextBlock(block)){
                //次の準備

            }

            landingTeam.addCount();
        }
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
/*
    private final BlockLandingPlugin blockLandingPlugin;
    private final Team team;
    private List<LandingBlockTask> landingBlockTask = new ArrayList<>();
    private Map<Integer, ItemStack> itemList = new HashMap<>();

    public GameManager(BlockLandingPlugin _blockLandingPlugin, Team team) {
        blockLandingPlugin = _blockLandingPlugin;
        this.team = team;
    }

    //ゲームを開始する
    public void start() {
        landingBlockTask = new ArrayList<>();
        List<OfflinePlayer> teamPlayers = team.getPlayers().stream().filter(OfflinePlayer::isOnline).collect(Collectors.toList());

        int index = 0;
        Player player;

        Iterator<OfflinePlayer> iterator = teamPlayers.iterator();
        for (ItemStack item : itemList.values()) {
            for (int i = 0; i < item.getAmount(); i++) {
                //リストの最後までいった場合初期化
                if (!iterator.hasNext()) {
                    iterator = teamPlayers.iterator();
                }
                player = iterator.next().getPlayer();
                assert player != null;
                landingBlockTask.add(new LandingBlockTask(new BlockLandingRunnable(blockLandingPlugin, player, index++), item.getType()));
            }
        }

        landingBlockTask.get(0).getBlockLandingRunnable().runTaskTimer(blockLandingPlugin, 0, configData.getTaskRepeatTime());
    }

    public GameManager setItemList(Map<Integer, ItemStack> itemList) {
        this.itemList = itemList;
        return this;
    }
*/
}
