package net.kunmc.lab.blocklandingplugin.team.turn;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class LandingTurn {

    private Player player;

    //ブロック
    private Block block;

    //ブロック素材
    private Material material;

    public LandingTurn(Player player, Block block, Material material) {
        this.player = player;
        this.block = block;
        this.material = material;
    }

    public Player getPlayer() {
        return this.player;
    }

    public Block getBlock() {
        return this.block;
    }

    public LandingTurn setBlock(Block block){
        this.block = block;
        return this;
    }

    public Material getMaterial() {
        return this.material;
    }

}
