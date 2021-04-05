package net.kunmc.lab.blocklandingplugin;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.entity.Player;

public final class BlockLandingPlugin extends JavaPlugin {

    int startY;

    @Override
    public void onEnable() {
        FileConfiguration config = getConfig();
        startY = Integer.parseInt(config.getString("startY"));
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        if (sender instanceof Player) {
            GameManager gameManager = new GameManager(this);
            gameManager.start((Player) sender, startY);
        }
        return false;
    }
}
