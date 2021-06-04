package net.kunmc.lab.blocklandingplugin;

import net.kunmc.lab.blocklandingplugin.team.LandingTeam;
import net.kunmc.lab.blocklandingplugin.team.turn.LandingTurn;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.data.Bisected;
import org.bukkit.block.data.type.Door;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;


public class GameManager extends BukkitRunnable {

    private Map<String, LandingTeam> landingTeamList;

    private int POTION_EFFECT_LEVEL = 4;

    public void setLandingTeamList(Map<String, LandingTeam> landingTeamList) {
        this.landingTeamList = landingTeamList;
    }

    public Map<String, LandingTeam> getLandingTeamList() {
        return this.landingTeamList;
    }

    public void run() {
        boolean isGaming = false;
        boolean isSneaking;
        ConfigData configData = ConfigData.getInstance();

        //各チームに対して順番に処理を行う
        teamLavel:
        for (Map.Entry<String, LandingTeam> landingTeam : landingTeamList.entrySet()) {

            if (!landingTeam.getValue().hasNextTurn()) {
                continue;
            }

            //ブロック・プレイヤー読み込み
            LandingTurn currentTurn = landingTeam.getValue().getCurrentTurn();
            Block block = currentTurn.getBlock();
            Player player = currentTurn.getPlayer();
            player.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, configData.getTaskRepeatTime(), POTION_EFFECT_LEVEL));
            player.sendActionBar(Component.text("あなたのターンです！ブロック落下中！"));
            if (landingTeam.getValue().isFirst()) {
                landingTeam.getValue().setIsFirst(false);
                player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_PLACE, 1, 1);
            }
            Material material = currentTurn.getMaterial();

            do {
                //次のブロックの位置決め
                Location nextLocation = block.getLocation();
                if (landingTeam.getValue().getTurnCount() % 2 == 0) {
                    nextLocation.add(0, -1, 0);
                }
                nextLocation.setX(player.getLocation().getX());
                nextLocation.setZ(player.getLocation().getZ());

                //次のブロック生成
                Block nextBlock = nextLocation.getBlock();
                block.setType(Material.AIR);

                //移動位置にブロックがあった場合一つ上に移動
                if (nextBlock.getType() != Material.AIR) {
                    nextBlock = nextBlock.getLocation().add(0, 1, 0).getBlock();
                }

                //ブロック具現化
                nextBlock.setType(material, false);
                currentTurn.setBlock(nextBlock);
                block = nextBlock;

                isSneaking = player.isSneaking();
                //当り判定
                //どこかに引っかかっていれば次のターンへ
                if (isHittingNextBlock(block, isSneaking)) {
                    nextLocation.getWorld().spawnParticle(
                            Particle.SPELL_MOB,
                            nextLocation,
                            10
                    );
                    isSneaking = false;
                    //次の準備
                    landingTeam.getValue().setNextTurn();
                    if (!landingTeam.getValue().hasNextTurn()) {
                        if (isDoor(material)) {
                            setTopDoor(nextLocation, material);
                        }
                        landingTeam.getValue().sendTitleToTeamMember("完成！");
                        continue teamLavel;
                    }
                }
                if (isDoor(material)) {
                    setTopDoor(nextLocation, material);
                }
                isGaming = true;
                landingTeam.getValue().addTurnCount();
            } while (isSneaking);
        }

        if (!isGaming) {
            cancel();
            landingTeamList.forEach((teamName, team) -> team.reset());
        }
    }

    private void setTopDoor(Location nextLocation, Material material) {
        Location topDoorLocation = nextLocation.clone();
        Block topDoorBlock = topDoorLocation.add(0, 1, 0).getBlock();
        Door doorData = ((Door) material.createBlockData());
        doorData.setHalf(Bisected.Half.TOP);
        topDoorBlock.setBlockData(doorData);
    }

    /**
     * 上下左右にブロックがあるか確認する
     * スニーク中は下まで落下する
     */
    private boolean isHittingNextBlock(Block block, boolean isSneaking) {
        Location location = block.getLocation();
        List<Block> blockList = new ArrayList<Block>();

        if (!isSneaking) {
            blockList.add(location.clone().add(0, 1, 0).getBlock());
            blockList.add(location.clone().add(-1, 0, 0).getBlock());
            blockList.add(location.clone().add(1, 0, 0).getBlock());
            blockList.add(location.clone().add(0, 0, -1).getBlock());
            blockList.add(location.clone().add(0, 0, 1).getBlock());
        }
        blockList.add(location.clone().add(0, -1, 0).getBlock());

        for (Block myBlock : blockList) {
            if (myBlock.getType() != Material.AIR) {
                return true;
            }
        }
        return false;
    }

    /**
     * ドアブロックか判定
     */
    private boolean isDoor(Material material) {
        if (material == Material.BIRCH_DOOR ||
                material == Material.ACACIA_DOOR ||
                material == Material.DARK_OAK_DOOR ||
                material == Material.CRIMSON_DOOR ||
                material == Material.IRON_DOOR ||
                material == Material.JUNGLE_DOOR ||
                material == Material.OAK_DOOR ||
                material == Material.WARPED_DOOR ||
                material == Material.SPRUCE_DOOR) {
            return true;
        }
        return false;
    }
}