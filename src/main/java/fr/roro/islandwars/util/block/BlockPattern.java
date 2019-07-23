package fr.roro.islandwars.util.block;

import org.bukkit.Material;

public class BlockPattern {

    private byte     data;
    private Material material;

    public BlockPattern(Material material) {
        this(material, 0);
    }

    public BlockPattern(Material material, int data) {
        this.material = material;
        this.data = (byte) data;
    }

    public Material getMaterial() {
        return material;
    }

    public byte getData() {
        return data;
    }

}
