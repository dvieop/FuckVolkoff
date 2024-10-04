package FuckParty.fuckVolkoff.src.main.java.xyz.velocity.modules.stronghold.config;

import dev.lyons.configapi.ConfigAPI;
import dev.lyons.configapi.ConfigClass;
import dev.lyons.configapi.EnumFormattingType;
import dev.lyons.configapi.annotations.Config;
import lombok.Getter;
import lombok.Setter;
import xyz.velocity.VelocityFeatures;
import xyz.velocity.modules.safari.config.saves.EquipmentSave;
import xyz.velocity.modules.stronghold.config.saves.*;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Config("stronghold")
public class StrongholdConfig implements ConfigClass {

    public static StrongholdConfig getInstance() {
        return ConfigAPI.getInstance().getConfigObject(StrongholdConfig.class);
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
    public String guiName = "&6&lStronghold";

    @Getter
    @Config
    public int guiSize = 27;

    @Getter
    @Config
    public boolean fillEmptyUiSlots = true;

    @Getter
    @Config
    public String fillMaterial = "STAINED_GLASS_PANE";

    @Getter
    @Config
    public int fillMaterialDamage = 15;

    @Getter
    @Config
    public List<String> effectsDisabledWorlds = new ArrayList<>(Collections.singleton("lms_world"));

    @Getter
    @Config
    public int strongholdsPerFaction = 1;

    @Getter
    @Config
    public List<StrongholdSave> strongholds = new ArrayList<>(Collections.singleton(exampleStronghold()));

    @Override
    public EnumFormattingType getFormatType() {
        return EnumFormattingType.YAML;
    }

    @Override
    public File getFile() {
        return new File(
                VelocityFeatures.getFileUtils().getConfigDir("Stronghold"),
                "stronghold.yml"
        );
    }

    @Override
    public boolean isStatsConfig() {
        return false;
    }

    private StrongholdSave exampleStronghold() {
        String name = "stronghold1";
        int slot = 2;
        String chatName = "&7&lCastle";
        String displayName = "&7&lCastle Stronghold";
        String displayItem = "DIAMOND_SWORD";

        List<String> lore = new ArrayList<>();
        lore.add("Status: <status>");
        lore.add("Percent: <percent>");
        lore.add("Controlled by: <faction_controlling>");
        lore.add("Contested by: <faction_contesting>");
        lore.add("Time controlled: <time_controlled>");

        String corner1 = "100:10:100:world";
        String corner2 = "103:13:103:world";
        String holoLocation = "102:15:102:world";
        List<String> holoLore = Arrays.asList("example hologram line");
        boolean playerstack = true;
        double percentPerPlayer = 0.5;
        List<String> pots = Arrays.asList("STRENGTH:2");
        List<String> cmds = new ArrayList<>();
        cmds.add("15:eco give <leader> 1500000");
        cmds.add("30:eco give <all> 100000");
        List<String> custom = Arrays.asList("SELLBOOST:2.0");
        MobSave mob = new MobSave(false,"&7&lCastle Mob", "ZOMBIE", 100, 15, 20, 3, Arrays.asList("1:1:1:world"), Arrays.asList(new RewardSave("money", "&2$&a500,000", "eco give <player> 500000", 15, false)), Arrays.asList(new EquipmentSave("DIAMOND_HELMET", "", "", Arrays.asList("protection_environmental:1"))), Arrays.asList("SPEED:2"));
        WallRegionSave wallRegionSave = new WallRegionSave(true, 30, "STAINED_CLAY", 600, Arrays.asList(new WallLocations("1:1:1:world", "3:3:3:world")));

        return new StrongholdSave(name, slot, chatName, displayName, displayItem, lore, corner1, corner2, holoLocation, holoLore, playerstack, percentPerPlayer, pots, custom, cmds, Arrays.asList(mob), wallRegionSave);
    }

}