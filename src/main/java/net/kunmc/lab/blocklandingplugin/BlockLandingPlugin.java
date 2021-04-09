package net.kunmc.lab.blocklandingplugin;

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

import java.util.HashMap;

public final class BlockLandingPlugin extends JavaPlugin {

    //todo GameManagerに持たせる、GameManegerはここで複数管理する
    private HashMap<Integer, ItemStack> temp;

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

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {

        //ゲーム開始コマンド
        final String GAME_START = "lstart";

        //ゲーム設定コマンド
        final String GAME_SET = "lready";

        switch (cmd.getName().toLowerCase()) {
            case GAME_START:
                //todo 将来的にはsetコマンドで開始プレイヤーを設定するのでこの判定は不要
                if (sender instanceof Player) {
                    GameManager gameManager = new GameManager(this);
                    gameManager.start((Player) sender, temp);
                }
                break;
            case GAME_SET:
                if (!(sender instanceof Player)) {
                    //todo エラーメッセージ　プレイヤーではありません的な
                    return false;
                }
                //プレイヤーの足元のブロックを取得
                Location location = ((Player) sender).getLocation();
                location.add(0,-0.1,0);
                Block footBlock = location.getBlock();
                if (footBlock.getType() != Material.CHEST) {
                    //todo エラーメッセージ　足元がチェストではありません的な
                    return false;
                }
                //チェストであれば、中身を取得してゲームに設定する
                Chest chest = (Chest) footBlock.getState();
                Inventory inv = chest.getInventory();

                temp = getItems(inv);
                int sum = 0;
                for(ItemStack item : temp.values()) {
                    sum += item.getAmount();
                }
                GameManager gameManager = new GameManager(this);

                Bukkit.getServer().broadcastMessage("チェストの中身を読み込みました（" + sum + "個）");
                break;
        }
        return false;
    }

    /**
     * インベントリからアイテムを重複しない形で取得
     */
    private HashMap<Integer, ItemStack> getItems(Inventory inv){
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
