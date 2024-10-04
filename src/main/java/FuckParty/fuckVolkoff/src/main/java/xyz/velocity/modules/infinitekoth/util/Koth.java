package FuckParty.fuckVolkoff.src.main.java.xyz.velocity.modules.infinitekoth.util;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.entity.Player;

@Getter
@Setter
public class Koth {

    private Location corner1;
    private Location corner2;

    private String name;
    private int seconds;

    private int totalCaptureTime = 0;
    private int reminderInterval = 0;
    private int currentTier = 1;

    private Player playerCapping = null;

    public Koth(String name, int seconds, Location corner1, Location corner2) {
        this.name = name;
        this.seconds = seconds;
        this.corner1 = corner1;
        this.corner2 = corner2;
    }

    public boolean tickSeconds() {
        this.seconds--;
        this.totalCaptureTime++;
        this.reminderInterval++;

        return this.seconds <= 0;
    }

    public boolean isCapping() {
        return this.playerCapping != null;
    }


    public void resetTime(int setTime) {
        this.totalCaptureTime = 0;
        this.reminderInterval = 0;
        this.seconds = setTime;
        this.currentTier = 1;

        Hologram.getInstance().resetTimer();
    }
}
