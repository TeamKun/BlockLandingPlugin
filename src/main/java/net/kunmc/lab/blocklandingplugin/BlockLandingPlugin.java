package net.kunmc.lab.blocklandingplugin;

import net.kunmc.lab.blocklandingplugin.message.GameMessage;
import net.kunmc.lab.blocklandingplugin.team.LandingTeam;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.block.data.Bisected;
import org.bukkit.block.data.type.Door;
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

    private Map<String, LandingTeam> landingTeamList;

    //ゲームコマンド
    private final String CMD = "landing";

    //ゲーム開始コマンド
    private final String GAME_START = "start";

    //ゲーム設定コマンド
    private final String GAME_SET = "set";

    //チーム設定コマンド
    private final String TEAM_SET = "team";

    //コンフィグ変更コマンド
    private final String CONFIG_SET = "config";

    @Override
    public void onEnable() {
        FileConfiguration config = getConfig();
        ConfigData configData = ConfigData.getInstance();
        int startY = Integer.parseInt(config.getString("startY"));
        int taskRepeatTime = Integer.parseInt(config.getString("taskRepeatTime"));

        configData.setStartY(startY);
        configData.setTaskRepeatTime(taskRepeatTime);
    }

    public void configUpdate() {
        FileConfiguration config = getConfig();
        ConfigData configData = ConfigData.getInstance();

        config.set("startY", configData.getStartY());
        config.set("taskRepeatTime", configData.getTaskRepeatTime());
        saveConfig();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {


        if (cmd.getName().equals(CMD)) {
            if (args.length < 1) {
                sender.sendMessage(GameMessage.ERROR_LESS_ARGS);
                return false;
            }

            switch (args[0].toLowerCase()) {
                case GAME_START:
                    return startGame(sender);

                case GAME_SET:
                    return setGame(sender, args);

                //現在存在するチームを読み込む
                case TEAM_SET:
                    return setTeam(sender);

                case CONFIG_SET:
                    setConfig(sender, args);
                    break;
            }
        }

        return true;
    }

    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        ArrayList<String> completes = new ArrayList<>();

        if (!command.getName().equalsIgnoreCase(CMD)) return super.onTabComplete(sender, command, alias, args);

        if (!sender.hasPermission(CMD)) {
            return new ArrayList<>();
        }

        if (args.length == 1) {
            completes.add(GAME_START);
            completes.add(GAME_SET);
            completes.add(TEAM_SET);
        }

        return completes;
    }

    //ゲーム実行
    private boolean startGame(CommandSender sender) {
        ConfigData configData = ConfigData.getInstance();
        GameManager gameManager = new GameManager();
        gameManager.setLandingTeamList(landingTeamList);
        Map<String, LandingTeam> landingTeamList = gameManager.getLandingTeamList();
        //最初の1ターン目を生成しておく
        for (Map.Entry<String, LandingTeam> landingTeam : landingTeamList.entrySet()) {
            landingTeam.getValue().setNextTurn();
        }

        gameManager.runTaskTimer(this, 0, configData.getTaskRepeatTime());
        return true;
    }

    private boolean canStart(CommandSender sender){
        //チームが読み込まれていない
        if(landingTeamList == null || landingTeamList.size() == 0){
            sender.sendMessage(GameMessage.ERROR_CANT_START);
            return false;
        }

        for (ItemStack item : items.values()) {
        for(LandingTeam landingTeam : landingTeamList.values() -> inde)
        if (landingTeamList.) {
            //チームにアイテムが読み込まれていない
            sender.sendMessage(GameMessage.ERROR_NO_TEAM_ITEM);
            return false;
        }

        return true;
    }

    private void setConfig(CommandSender sender, String[] args) {
        if (args.length != 2) {
            return;
        }
        String[] temp = args[1].split("=");
        if (temp.length == 2) {
            if (ConfigData.TASK_REPEAT_TIME_STRING.equals(temp[0])) {
                sender.sendMessage(GameMessage.getConfigSet(ConfigData.TASK_REPEAT_TIME_STRING));
                ConfigData.getInstance().setTaskRepeatTime(Integer.parseInt(temp[1]));

            } else if (ConfigData.START_Y_STRING.equals(temp[0])) {
                sender.sendMessage(GameMessage.getConfigSet(ConfigData.START_Y_STRING));
                ConfigData.getInstance().setStartY(Integer.parseInt(temp[1]));
            }
            configUpdate();
        }
    }

    //コマンド実行者の足元のチェストを読み込み、引数のチームに登録する
    private boolean setGame(CommandSender sender, String[] args) {

        if (!setCmdArgsCheck(sender, args)) {
            return false;
        }

        //プレイヤーの足元のブロックを取得
        Location location = ((Player) sender).getLocation();
        Block footBlock = location.add(0, -0.1, 0).getBlock();
        if (footBlock.getType() != Material.CHEST) {
            sender.sendMessage(GameMessage.ERROR_NOT_CHEST);
            return false;
        }

        //チェストであれば、中身を取得してゲームに設定する
        Chest chest = (Chest) footBlock.getState();
        Inventory inv = chest.getInventory();

        Map<Integer, ItemStack> items = getItems(inv);
        String teamName = args[1];
        this.landingTeamList.get(teamName).setItemList(items);

        int sum = 0;
        for (ItemStack item : items.values()) {
            sum += item.getAmount();
        }
        sender.sendMessage(GameMessage.getLoadingChest(sum));
        return true;
    }

    //現在のチームを読み込む
    private boolean setTeam(CommandSender sender) {
        ScoreboardManager manager = Bukkit.getScoreboardManager();
        Scoreboard board = manager.getMainScoreboard();
        Set<Team> teams = board.getTeams();

        List<String> teamNames = new ArrayList<>();
        Map<String, LandingTeam> landingTeamList = new HashMap<>();

        if (teams.size() == 0) {
            sender.sendMessage(GameMessage.ERROR_CANT_START);
        }
        for (Team targetTeam : teams) {
            LandingTeam landingTeam = new LandingTeam(targetTeam.getEntries().stream().collect(Collectors.toSet()), targetTeam.getName());
            landingTeamList.put(targetTeam.getName(), landingTeam);
            teamNames.add(targetTeam.getName());
        }
        this.landingTeamList = landingTeamList;
        sender.sendMessage(GameMessage.getLoadingTeam(teamNames.stream().collect(Collectors.joining("、"))));
        return true;
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

    private boolean setCmdArgsCheck(CommandSender sender, String[] args) {
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
        if (!this.landingTeamList.containsKey(teamName)) {
            sender.sendMessage(GameMessage.getErrorNoTeamName(teamName));
            return false;
        }
        return true;
    }
}
