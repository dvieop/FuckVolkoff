package FuckParty.fuckVolkoff.src.main.java.xyz.velocity.modules.foundry.util;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public class Koth {

    private Location corner1;
    private Location corner2;

    private String name;
    private int seconds;

    private Player playerCapping = null;

    public Koth(String name, int seconds, Location corner1, Location corner2) {
        this.name = name;
        this.seconds = seconds;
        this.corner1 = corner1;
        this.corner2 = corner2;
    }

    public boolean tickSeconds() {
        this.seconds--;

        return this.seconds == 0;
    }

    public boolean isCapping() {
        return this.playerCapping != null;
    }

    public Location getCorner1() {
        return this.corner1;
    }

    public Location getCorner2() {
        return this.corner2;
    }

    public Player getPlayerCapping() {
        return playerCapping;
    }

    public void setPlayerCapping(Player player) {
        this.playerCapping = player;
    }

    public int getSecondsLeft() {
        return this.seconds;
    }

}
