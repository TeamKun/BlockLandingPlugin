package net.kunmc.lab.blocklandingplugin.team;

import net.kunmc.lab.blocklandingplugin.ConfigData;
import net.kunmc.lab.blocklandingplugin.GameManager;
import net.kunmc.lab.blocklandingplugin.message.ErrorMessage;
import net.kunmc.lab.blocklandingplugin.team.turn.LandingTurn;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team;

import java.util.*;
import java.util.stream.Collectors;

public final class LandingTeam {
    //チーム情報要らない？
    private Team team;

    //アイテム
    private List<Material> itemList;
    private Iterator<Material> itemIterator;

    //プレイヤー
    private Set<String> teamPlayers;
    private Iterator<String> playerIterator;

    //現在落下中のブロック、操作プレイヤー情報
    private LandingTurn currentTurn;

    private int turnCount;

    public LandingTeam(Team team) {
        this.team = team;

        this.teamPlayers = team.getPlayers();
        this.playerIterator = teamPlayers.iterator();
    }

    public LandingTeam setItemList(HashMap<Integer, ItemStack> itemList) {
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
        if (this.itemIterator.hasNext()) {
            return true;
        }
        return false;
    }

    //次のブロックとプレイヤーの設定
    public void setNextTurn() {
        //プレイヤー設定
        //リストの最後までいった場合初期化
        Player player;
        if (this.hasLandingTurn()) {
            //オンラインプレイヤーを割り当て
            //memo: 危ないかな…でもチームに一人も有効な人がいない場合以外無限ループしないので、エラールート作るほどのリスクはないような…
            while (true) {
                if (!this.playerIterator.hasNext()) {
                    this.playerIterator = teamPlayers.iterator();
                }
                player = this.playerIterator.next().getPlayer();
                if (player.isOnline()) {
                    break;
                }
            }
            this.currentTurn = new LandingTurn(player, player.getLocation().getBlock(), this.itemIterator.next());
        }
    }

    public LandingTurn getCurrentTurn() {
        return this.currentTurn;
    }
}
