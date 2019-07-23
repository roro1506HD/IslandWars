package fr.roro.islandwars.game;

/**
 * This file is a part of IslandWars project.
 *
 * @author roro1506_HD
 */
public enum Mode {

    NORMAL("Normal"),
    ULTRA("Ultra"),
    INFINITY("Infini"),
    EXPLOSIVE("Explosif");

    String displayName;

    Mode(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return this.displayName;
    }
}
