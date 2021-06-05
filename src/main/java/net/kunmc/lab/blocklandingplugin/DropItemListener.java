package net.kunmc.lab.blocklandingplugin;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ItemSpawnEvent;

public class DropItemListener implements Listener {

    @EventHandler
    public void onDropItem(ItemSpawnEvent event) {
        if (BlockLandingPlugin.isGaming()) {
            event.setCancelled(true);
        }
    }
}
