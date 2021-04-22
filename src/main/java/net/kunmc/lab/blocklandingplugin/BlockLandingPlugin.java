package net.kunmc.lab.blocklandingplugin;

import net.kunmc.lab.blocklandingplugin.message.ErrorMessage;
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

    //teamname,gameManager
    private Map<String, GameManager> gameManagerList = new HashMap<>();

    @Override
    public void onEnable() {
        FileConfiguration config = getConfig();
        ConfigData configData = ConfigData.getInstance();
        int startY = Integer.parseInt(config.getString("startY"));
        configData.setStartY(startY);
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
        final String GAME_SET = "ready";

        //チーム設定コマンド
        final String TEAM_SET = "team";

        try {
            if(cmd.getName().equals(CMD)){
                if(args.length < 1){
                    //todo エラーメッセージを適切な対象に送る
                    Bukkit.getServer().broadcastMessage(ErrorMessage.LESS_ARGS);
                    return false;
                }

                switch (args[0].toLowerCase()) {
                    case GAME_START:
                        return gameStart(sender);

                    case GAME_SET:
                        return gameSet(sender, args);

                    //現在存在するチームを元にゲームを作成する
                    case TEAM_SET:
                        return teamSet();
                }
            }
        } catch (Exception e) {
            Bukkit.getServer().broadcastMessage(e.getMessage());
        }

        return false;
    }

    //ゲーム実行
    private boolean gameStart(CommandSender sender) {
        if (gameManagerList.size() == 0) {
            Bukkit.getServer().broadcastMessage(ErrorMessage.CANT_START);
        }
        gameManagerList.forEach((k, v) -> v.start());
        return false;
    }

    //コマンド実行者の足元のチェストを読み込み、引数のチームに登録する
    private boolean gameSet(CommandSender sender, String[] args) {
        //実行者がプレイヤーでないと足元のチェストが拾えないため
        if (!(sender instanceof Player)) {
            //todo エラーメッセージを適切な対象に送る
            Bukkit.getServer().broadcastMessage(ErrorMessage.CMD_SENDER_ERROR);
            return false;
        }

        //チームが必要
        if (args.length != 2) {
            //todo エラーメッセージを適切な対象に送る
            Bukkit.getServer().broadcastMessage(ErrorMessage.NO_TEAM_CMD);
            return false;
        }

        String teamName = args[1];
        //指定チームが存在しない場合
        if (!gameManagerList.containsKey(teamName)) {
            //todo エラーメッセージを適切な対象に送る
            Bukkit.getServer().broadcastMessage(ErrorMessage.NO_TEAM_NAME.replace("TEAM_NAME", teamName));
            return false;
        }

        //プレイヤーの足元のブロックを取得
        Location location = ((Player) sender).getLocation();
        location.add(0, -0.1, 0);
        Block footBlock = location.getBlock();
        if (footBlock.getType() != Material.CHEST) {
            //todo エラーメッセージを適切な対象に送る
            Bukkit.getServer().broadcastMessage(ErrorMessage.NOT_CHEST);
            return false;
        }
        //チェストであれば、中身を取得してゲームに設定する
        Chest chest = (Chest) footBlock.getState();
        Inventory inv = chest.getInventory();

        //todo もう一度Mapに登録する必要がある？
        GameManager teamGameManager = gameManagerList.get(teamName);
        HashMap<Integer, ItemStack> items = getItems(inv);
        teamGameManager.setItemList(getItems(inv));

        int sum = 0;
        for (ItemStack item : items.values()) {
            sum += item.getAmount();
        }
        Bukkit.getServer().broadcastMessage("チェストの中身を読み込みました（" + sum + "個）");
        return false;
    }

    //現在のチームを読み込む
    private boolean teamSet() {
        ScoreboardManager manager = Bukkit.getScoreboardManager();
        Scoreboard board = manager.getMainScoreboard();
        Set<Team> teams = board.getTeams();
        List<String> teamNames = new ArrayList<>();

        for (Team targetTeam : teams) {
            GameManager newGameManager = new GameManager(this, targetTeam);
            gameManagerList.put(targetTeam.getName(), newGameManager);
            teamNames.add(targetTeam.getName());
        }

        Bukkit.getServer().broadcastMessage("チームを読み込みました（" + teamNames.stream().collect(Collectors.joining("、")) + "）");
        return false;
    }

    /**
     * インベントリからアイテムを重複しない形で取得
     */
    private HashMap<Integer, ItemStack> getItems(Inventory inv) {
        HashMap<Integer, ItemStack> items = new HashMap<>();
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
