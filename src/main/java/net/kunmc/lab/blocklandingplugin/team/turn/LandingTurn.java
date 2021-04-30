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

    public Player getPlayer() {
        return this.player;
    }

    public LandingTurn setPlayer(Player player) {
        this.player = player;
        return this;
    }

    public Block getBlock() {
        return this.block;
    }

    public LandingTurn setBlock(Block block) {
        this.block = block;
        return this;
    }

    public Material getMaterial(){
        return this.material;
    }

    public LandingTurn setMaterial(Material material){
        this.material = material;
        return this;
    }
}
