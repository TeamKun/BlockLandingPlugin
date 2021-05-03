package net.kunmc.lab.blocklandingplugin;

import net.kunmc.lab.blocklandingplugin.team.LandingTeam;
import net.kunmc.lab.blocklandingplugin.team.turn.LandingTurn;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;


public class GameManager extends BukkitRunnable {

    private static Map<String, LandingTeam> landingTeamList;

    public void setLandingTeamList(Map<String, LandingTeam> landingTeamList) {
        this.landingTeamList = landingTeamList;
    }

    public Map<String, LandingTeam> getLandingTeamList() {
        return this.landingTeamList;
    }

    public void run() {
        //各チームに対して順番に処理を行う
        for (Map.Entry<String, LandingTeam> landingTeam : landingTeamList.entrySet()) {
            //ブロック・プレイヤー読み込み
            LandingTurn currentTurn = landingTeam.getValue().getCurrentTurn();
            Block block = currentTurn.getBlock();
            Player player = currentTurn.getPlayer();
            Material material = currentTurn.getMaterial();

            //次のブロックの位置決め
            Location location = block.getLocation();
            if (landingTeam.getValue().getCount() % 2 == 0) {
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
            nextBlock.setType(material);
            block = nextBlock;

            //当り判定
            if (isHittingNextBlock(block)) {
                location.getWorld().spawnParticle(
                        Particle.SPELL_MOB,
                        location,
                        10
                );
                //次の準備
                landingTeam.getValue().setNextTurn();
            }

            landingTeam.getValue().addTurnCount();
            player.sendTitle("完成！", "", 10, 80, 10);
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
}