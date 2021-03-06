package net.kunmc.lab.blocklandingplugin.team;

import net.kunmc.lab.blocklandingplugin.ConfigData;
import net.kunmc.lab.blocklandingplugin.team.turn.LandingTurn;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scoreboard.Team;

import java.util.*;
import java.util.stream.Collectors;

public final class LandingTeam {

    private String teamName;

    //アイテム
    private List<Material> itemList;
    private Iterator<Material> itemIterator;

    //プレイヤー
    private Set<String> teamPlayerNames;
    private Iterator<String> playerNamesIterator;

    //現在落下中のブロック、操作プレイヤー情報
    private LandingTurn currentTurn;

    private int turnCount;
    private boolean isFirst;

    public LandingTeam(Set<String> teamPlayerNames, String teamName) {
        this.teamName = teamName;
        this.teamPlayerNames = teamPlayerNames;
        this.playerNamesIterator = teamPlayerNames.iterator();
    }

    public void reset() {
        currentTurn = null;
        itemIterator = itemList.iterator();
        playerNamesIterator = teamPlayerNames.iterator();
    }

    public String getTeamName() {
        return teamName;
    }

    public boolean hasTeamOnlineMember() {
        for (String playerName : teamPlayerNames) {
            Player currentPlayer = Bukkit.getPlayer(playerName);
            if (currentPlayer != null) {
                return true;
            }
        }
        return false;
    }

    public boolean isFirst() {
        return isFirst;
    }

    public void setIsFirst(boolean isFirst) {
        this.isFirst = isFirst;
    }

    public LandingTeam setItemList(Map<Integer, ItemStack> itemList) {
        this.itemList = new ArrayList<>();
        for (Map.Entry<Integer, ItemStack> item : itemList.entrySet()) {
            for (int i = 0; i < item.getValue().getAmount(); i++) {
                this.itemList.add(item.getValue().getType());
            }
        }
        this.itemIterator = this.itemList.iterator();
        return this;
    }

    public int getTurnCount() {
        return this.turnCount;
    }

    public LandingTeam addTurnCount() {
        this.turnCount++;
        return this;
    }

    public boolean hasNextTurn() {
        return this.currentTurn != null;
    }

    public boolean hasItem() {
        if (this.itemIterator == null) {
            return false;
        }

        return this.itemIterator.hasNext();
    }

    //次のブロックとプレイヤーの設定
    //次のターンがない場合null
    public void setNextTurn() {
        this.currentTurn = null;
        this.isFirst = true;
        //プレイヤー設定
        //リストの最後までいった場合初期化
        Player currentPlayer;
        if (this.itemIterator.hasNext()) {
            //オンラインプレイヤーを割り当て
            while (true) {
                if (!this.playerNamesIterator.hasNext()) {
                    this.playerNamesIterator = teamPlayerNames.iterator();
                }
                currentPlayer = Bukkit.getPlayer(this.playerNamesIterator.next());
                if (currentPlayer != null) {
                    break;
                }
            }
            Block block = currentPlayer.getLocation().getBlock();
            Location location = block.getLocation();
            location.setY(ConfigData.getInstance().getStartY());
            this.currentTurn = new LandingTurn(currentPlayer, location.getBlock(), this.itemIterator.next());
        }
    }

    public LandingTurn getCurrentTurn() {
        return this.currentTurn;
    }

    public void sendTitleToTeamMember(String message) {
        Iterator<String> targetPlayerNamesIterator = teamPlayerNames.iterator();
        Player targetPlayer;
        while (targetPlayerNamesIterator.hasNext()) {
            targetPlayer = Bukkit.getPlayer(targetPlayerNamesIterator.next());
            if (targetPlayer != null) {
                targetPlayer.sendTitle(message, "", 10, 80, 10);
            }
        }
    }
}
