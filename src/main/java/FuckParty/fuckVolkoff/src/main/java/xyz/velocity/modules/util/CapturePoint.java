package FuckParty.fuckVolkoff.src.main.java.xyz.velocity.modules.util;

import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import lombok.Getter;
import org.bukkit.Location;
import xyz.velocity.modules.garrison.Garrison;
import xyz.velocity.modules.garrison.config.saves.GarrisonSave;
import xyz.velocity.modules.stronghold.config.saves.StrongholdSave;
import xyz.velocity.modules.stronghold.Stronghold;

public class CapturePoint {

    @Getter
    Location location1;

    @Getter
    Location location2;

    @Getter
    String name;

    @Getter
    String displayName;

    @Getter
    private int totalCaptureTime = 0;

    @Getter
    private int wallRegenTime;

    @Getter
    private int entityClear = 0;

    @Getter
    private double percentage = 0;

    @Getter
    private String factionOwning = null;

    @Getter
    private String factionContesting = null;

    @Getter
    private StrongholdSave stronghold;

    @Getter
    private GarrisonSave garrison;

    @Getter
    private Object2ObjectMap<String, Integer> commands = new Object2ObjectOpenHashMap<>();

    @Getter
    private long protectionTime = 0;

    private boolean isCapturing = false;
    private boolean isNeutral = true;
    private Object2ObjectMap<String, Integer> memCommands = new Object2ObjectOpenHashMap<>();

    public CapturePoint(StrongholdSave stronghold) {
        location1 = xyz.velocity.modules.util.Location.parseToLocation(stronghold.getCorner1());
        location2 = xyz.velocity.modules.util.Location.parseToLocation(stronghold.getCorner2());

        name = stronghold.getName();
        displayName = stronghold.getDisplayName();
        wallRegenTime = stronghold.getWallRegions().getRegenInterval();

        this.stronghold = stronghold;
    }

    public CapturePoint(GarrisonSave garrison) {
        location1 = xyz.velocity.modules.util.Location.parseToLocation(garrison.getCorner1());
        location2 = xyz.velocity.modules.util.Location.parseToLocation(garrison.getCorner2());

        name = garrison.getName();
        //wallRegenTime = garrison.getWallRegions().getRegenInterval();

        this.garrison = garrison;
    }

    public boolean isCapturing() {
        return isCapturing;
    }

    public boolean isNeutral() {
        return isNeutral;
    }

    public void updatePercentage(double toAdd) {
        this.percentage += toAdd;
        if (this.percentage > 100) this.percentage = 100;
        if (this.percentage < 0) this.percentage = 0;
    }

    public void setFactionContesting(String factionContesting) {
        this.factionContesting = factionContesting;
    }

    public void setFactionOwning(String factionOwning) {
        this.factionOwning = factionOwning;
    }

    public void setTotalCaptureTime(int totalCaptureTime) {
        this.totalCaptureTime = totalCaptureTime;
    }

    public void setStrongholdWallRegenTime(int wallRegenTime) {
        if (!this.stronghold.getWallRegions().isEnabled()) return;
        this.wallRegenTime = wallRegenTime;

        if (this.wallRegenTime <= 0) {
            this.wallRegenTime = stronghold.getWallRegions().getRegenInterval();
            Stronghold.getInstance().updateWalls(stronghold);
        }
    }

    public void setGarrisonWallRegenTime(int wallRegenTime) {
        /*if (!this.garrison.getWallRegions().isEnabled()) return;
        this.wallRegenTime = wallRegenTime;

        if (this.wallRegenTime <= 0) {
            this.wallRegenTime = garrison.getWallRegions().getRegenInterval();
            //Garrison.getInstance().updateWalls(garrison);
        }*/
    }

    public void setEntityClear(int entityClear) {
        this.entityClear = entityClear;

        if (this.entityClear >= 300) {
            this.entityClear = 0;
            Stronghold.getInstance().clearEntities(this);
        }
    }

    public void setNeutral(boolean neutral) {
        isNeutral = neutral;
    }

    public void setCapturing(boolean capturing) {
        isCapturing = capturing;
    }

    public void setPercentage(double percentage) {
        this.percentage = percentage;
    }

    public void addCommand(String command, Integer delay) {
        this.commands.put(command, delay);
        this.memCommands.put(command, delay);
    }

    public void resetCommand(String command) {
        this.commands.replace(command, memCommands.get(command));
    }

    public void setProtectionTime(long protectionTime) {
        this.protectionTime = protectionTime;
    }

}
