package net.kunmc.lab.blocklandingplugin.team;

import net.kunmc.lab.blocklandingplugin.team.turn.LandingTurn;
import org.bukkit.Bukkit;
import org.bukkit.Material;
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

    public LandingTeam(Set<String> teamPlayerNames, String teamName) {
        this.teamName = teamName;
        this.teamPlayerNames = teamPlayerNames;
        this.playerNamesIterator = teamPlayerNames.iterator();
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

    public int getCount() {
        return this.turnCount;
    }

    public LandingTeam addTurnCount() {
        this.turnCount++;
        return this;
    }

    public boolean hasLandingTurn() {
        return this.itemIterator.hasNext();
    }

    //次のブロックとプレイヤーの設定
    public void setNextTurn() {
        //プレイヤー設定
        //リストの最後までいった場合初期化
        Player currentPlayer;
        if (this.hasLandingTurn()) {
            //オンラインプレイヤーを割り当て
            //memo: 危ないかな…でもチームに一人も有効な人がいない場合以外無限ループしないので、エラールート作るほどのリスクはないような…
            while (true) {
                if (!this.playerNamesIterator.hasNext()) {
                    this.playerNamesIterator = teamPlayerNames.iterator();
                }
                currentPlayer = Bukkit.getPlayer(this.playerNamesIterator.next());
                if (currentPlayer != null) {
                    break;
                }
            }
            this.currentTurn = new LandingTurn(currentPlayer, currentPlayer.getLocation().getBlock(), this.itemIterator.next());
        }
    }

    public LandingTurn getCurrentTurn() {
        return this.currentTurn;
    }
}
