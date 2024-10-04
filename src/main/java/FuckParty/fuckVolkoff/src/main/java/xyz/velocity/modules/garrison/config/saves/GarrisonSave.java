package FuckParty.fuckVolkoff.src.main.java.xyz.velocity.modules.garrison.config.saves;

import lombok.Getter;
import xyz.velocity.modules.safari.config.saves.PlayerGearSave;
import xyz.velocity.modules.stronghold.config.saves.MobSave;
import xyz.velocity.modules.stronghold.config.saves.WallRegionSave;

import java.util.List;

@Getter
public class GarrisonSave {

    String name;
    String chatName;
    String corner1;
    String corner2;
    boolean playerStack;
    double percentPerPlayer;
    GraceSave graceSave;
    PlayerGearSave playerGear;
    DeathBanSave deathBan;
    List<XpSave> xp;
    List<BoostSave> boosts;
    //WallRegionSave wallRegions;

    public GarrisonSave(String name, String chatName, String corner1, String corner2, boolean playerStack, double percentPerPlayer, GraceSave graceSave, PlayerGearSave playerGear, DeathBanSave deathBan, List<XpSave> xp, List<BoostSave> boosts) {
        this.name = name;
        this.chatName = chatName;
        this.corner1 = corner1;
        this.corner2 = corner2;
        this.playerStack = playerStack;
        this.percentPerPlayer = percentPerPlayer;
        this.graceSave = graceSave;
        this.playerGear = playerGear;
        this.deathBan = deathBan;
        this.xp = xp;
        this.boosts = boosts;
        //this.wallRegions = walllRegions;
    }

}
