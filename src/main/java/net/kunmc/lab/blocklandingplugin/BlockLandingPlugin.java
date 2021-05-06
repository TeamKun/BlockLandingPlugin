package net.kunmc.lab.blocklandingplugin;

import net.kunmc.lab.blocklandingplugin.message.GameMessage;
import net.kunmc.lab.blocklandingplugin.team.LandingTeam;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team;

import java.util.*;
import java.util.stream.Collectors;

public final class BlockLandingPlugin extends JavaPlugin {

    GameManager gameManager;

    @Override
    public void onEnable() {
        FileConfiguration config = getConfig();
        ConfigData configData = ConfigData.getInstance();
        int startY = Integer.parseInt(config.getString("startY"));
        configData.setStartY(startY);

        this.gameManager = new GameManager();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    //todo resetコマンド作る？
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {

        //ゲームコマンド
        final String CMD = "landing";

        //ゲーム開始コマンド
        final String GAME_START = "start";

        //ゲーム設定コマンド
        final String GAME_SET = "set";

        //チーム設定コマンド
        final String TEAM_SET = "team";

            if (cmd.getName().equals(CMD)) {
                if (args.length < 1) {
                    sender.sendMessage(GameMessage.ERROR_LESS_ARGS);
                    return false;
                }

                switch (args[0].toLowerCase()) {
                    case GAME_START:
                        return gameStart(sender);

                    case GAME_SET:
                        return gameSet(sender, args);

                    //現在存在するチームを読み込む
                    case TEAM_SET:
                        return teamSet(sender);
                }
            }


        return false;
    }

    //ゲーム実行
    private boolean gameStart(CommandSender sender) {
        ConfigData configData = ConfigData.getInstance();

        Map<String, LandingTeam> landingTeamList = this.gameManager.getLandingTeamList();
        //最初の1ターン目を生成しておく
        for (Map.Entry<String, LandingTeam> landingTeam : landingTeamList.entrySet()) {
            landingTeam.getValue().setNextTurn();
        }

        this.gameManager.runTaskTimer(this, 0, configData.getTaskRepeatTime());
        return false;
    }

    //コマンド実行者の足元のチェストを読み込み、引数のチームに登録する
    private boolean gameSet(CommandSender sender, String[] args) {
        //実行者がプレイヤーでないと足元のチェストが拾えないため
        if (!(sender instanceof Player)) {
            sender.sendMessage(GameMessage.ERROR_CMD_SENDER_ERROR);
            return false;
        }

        //チームが必要
        if (args.length != 2) {
            sender.sendMessage(GameMessage.ERROR_NO_TEAM_CMD);
            return false;
        }

        String teamName = args[1];
        //指定チームが存在しない場合
        if (!gameManager.getLandingTeamList().containsKey(teamName)) {
            sender.sendMessage(GameMessage.getErrorNoTeamName(teamName));
            return false;
        }

        //プレイヤーの足元のブロックを取得
        Location location = ((Player) sender).getLocation();
        location.add(0, -0.1, 0);
        Block footBlock = location.getBlock();
        if (footBlock.getType() != Material.CHEST) {
            sender.sendMessage(GameMessage.ERROR_NOT_CHEST);
            return false;
        }
        //チェストであれば、中身を取得してゲームに設定する
        Chest chest = (Chest) footBlock.getState();
        Inventory inv = chest.getInventory();

        Map<Integer, ItemStack> items = getItems(inv);
        Map<String, LandingTeam> landingTeamList = gameManager.getLandingTeamList();
        landingTeamList.get(teamName).setItemList(getItems(inv));

        int sum = 0;
        for (ItemStack item : items.values()) {
            sum += item.getAmount();
        }
        sender.sendMessage(GameMessage.getLoadingChest(sum));
        return false;
    }

    //現在のチームを読み込む
    private boolean teamSet(CommandSender sender) {
        ScoreboardManager manager = Bukkit.getScoreboardManager();
        Scoreboard board = manager.getMainScoreboard();
        Set<Team> teams = board.getTeams();

        List<String> teamNames = new ArrayList<>();
        Map<String, LandingTeam> landingTeamList = new HashMap<>();

        for (Team targetTeam : teams) {
            LandingTeam landingTeam = new LandingTeam(targetTeam.getEntries().stream().collect(Collectors.toSet()), targetTeam.getName());
            landingTeamList.put(targetTeam.getName(), landingTeam);
            teamNames.add(targetTeam.getName());
        }
        gameManager.setLandingTeamList(landingTeamList);
        sender.sendMessage(GameMessage.getLoadingTeam(teamNames.stream().collect(Collectors.joining("、"))));
        return false;
    }

    /**
     * インベントリからアイテムを取得
     */
    private Map<Integer, ItemStack> getItems(Inventory inv) {
        Map<Integer, ItemStack> items = new HashMap<>();
        for (int i = 0, size = inv.getSize(); i < size; ++i) {
            // アイテム取得
            ItemStack targetItem = inv.getItem(i);
            if (targetItem == null) {
                continue;
            }
            items.put(i, targetItem);
        }
        return items;
    }
}
