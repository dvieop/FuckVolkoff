package FuckParty.fuckVolkoff.src.main.java.xyz.velocity.modules.garrison.config;

import dev.lyons.configapi.ConfigAPI;
import dev.lyons.configapi.ConfigClass;
import dev.lyons.configapi.EnumFormattingType;
import dev.lyons.configapi.annotations.Config;
import lombok.Getter;
import lombok.Setter;
import xyz.velocity.VelocityFeatures;
import xyz.velocity.modules.garrison.EnumBoostMode;
import xyz.velocity.modules.garrison.config.saves.*;
import xyz.velocity.modules.safari.config.saves.PlayerGearSave;
import xyz.velocity.modules.stronghold.config.saves.*;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Config("garrison")
public class GarrisonConfig implements ConfigClass {

    public static GarrisonConfig getInstance() {
        return ConfigAPI.getInstance().getConfigObject(GarrisonConfig.class);
    }

    @Getter
    @Setter
    @Config
    boolean enabled = false;

    @Getter
    @Config
    public String factionEntered = "<faction> is now contesting the <stronghold> stronghold!";

    @Getter
    @Config
    public String factionLeft = "<faction> has stopped contesting the <stronghold> stronghold!";

    @Getter
    @Config
    public String gainedControl = "<faction> has gained control over the <stronghold> stronghold!";

    @Getter
    @Config
    public String lostControl = "<faction> has lost control over the <stronghold> stronghold!";

    @Getter
    @Config
    public String neutralized = "<stronghold> has been neutralized by <faction>!";

    @Getter
    @Config
    public String mobKillMessage = "You have received <reward> while grinding!";

    @Getter
    @Config
    public String broadcastReward = "<player> has received <reward> while grinding <stronghold> stronghold";

    @Getter
    @Config
    public String wallMined = "This block has <amount> durability left";

    @Getter
    @Config
    public boolean announceUpgrade = true;

    @Getter
    @Config
    public String inventoryEmpty = "Your inventory must be empty";

    @Getter
    @Config
    public String tierUpgrade = "<faction> has upgraded their <mode> to tier <tier>";

    @Getter
    @Config
    public String leaveLocation = "0:0:0:world";

    @Getter
    @Config
    public HologramSave hologram = new HologramSave("", "29:109:69:world", Arrays.asList("lore"));

    @Getter
    @Config
    public GarrisonSave garrison = exampleGarrison();

    @Override
    public EnumFormattingType getFormatType() {
        return EnumFormattingType.YAML;
    }

    @Override
    public File getFile() {
        return new File(
                VelocityFeatures.getFileUtils().getConfigDir("Garrison"),
                "garrison.yml"
        );
    }

    @Override
    public boolean isStatsConfig() {
        return false;
    }

    private GarrisonSave exampleGarrison() {
        String name = "garrison";
        String chatName = "&7&lGarrison";

        String corner1 = "100:10:100:world";
        String corner2 = "103:13:103:world";
        boolean playerstack = true;
        double percentPerPlayer = 0.5;
        //WallRegionSave wallRegionSave = new WallRegionSave(true, 30, "STAINED_CLAY", 600, Arrays.asList(new WallLocations("1:1:1:world", "3:3:3:world")));
        GraceSave graceSave = new GraceSave(false, 10, false, "garrison is now on grace", "garrison is no longer on grace", Arrays.asList("garrison is going off grace in <time>"));
        PlayerGearSave playerGearSave = new PlayerGearSave("garrisonKit");
        DeathBanSave deathBanSave = new DeathBanSave(false, 5, "You have been death banned for <duration>");

        return new GarrisonSave(name, chatName, corner1, corner2, playerstack, percentPerPlayer, graceSave, playerGearSave, deathBanSave, getXpSaves(), getBoosts());
    }

    private List<BoostSave> getBoosts() {
        List<BoostSave> list = new ArrayList<>();

        for (EnumBoostMode value : EnumBoostMode.values()) {
            list.add(new BoostSave(false, value.name().toLowerCase(), "&e&l" + value.name().toLowerCase(), 5, 0.1, 2000));
        }

        return list;
    }

    private List<XpSave> getXpSaves() {
        List<XpSave> list = new ArrayList<>();

        list.add(new XpSave("player", 150));
        list.add(new XpSave("mob", 2));
        list.add(new XpSave("fish", 35));
        list.add(new XpSave("envoy", 50));
        list.add(new XpSave("koth", 1500));
        list.add(new XpSave("safari", 50));

        return list;
    }

}
