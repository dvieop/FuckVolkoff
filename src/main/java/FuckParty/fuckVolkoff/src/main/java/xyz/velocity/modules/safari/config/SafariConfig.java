package FuckParty.fuckVolkoff.src.main.java.xyz.velocity.modules.safari.config;

import dev.lyons.configapi.ConfigAPI;
import dev.lyons.configapi.ConfigClass;
import dev.lyons.configapi.EnumFormattingType;
import dev.lyons.configapi.annotations.Config;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import xyz.velocity.VelocityFeatures;
import xyz.velocity.modules.fishing.config.saves.ItemMultiplierSave;
import xyz.velocity.modules.safari.config.saves.*;

@Config("safari")
public class SafariConfig implements ConfigClass {

    public static SafariConfig getInstance() {
        return ConfigAPI.getInstance().getConfigObject(SafariConfig.class);
    }

    @Getter
    @Setter
    @Config
    public boolean enabled = false;

    @Getter
    @Config
    public List<String> whitelistedWorlds = new ArrayList<>(Collections.singleton("world"));

    @Getter
    @Config
    public String safariActive = "already active";

    @Getter
    @Config
    public String safariCooldown = "safari is currently on cooldown for <cooldown>";

    @Getter
    @Config
    public String inventoryEmpty = "You must empty your inventory first";

    @Getter
    @Config
    public String rewardMessage = "You have gained <reward>";

    @Getter
    @Config
    public String chatWinMessage = "<player> have gained <reward>";

    @Getter
    @Config
    public String itemDropMessage = "You've found <amount>x <item> inside a monster egg!";

    @Getter
    @Config
    public String limitReached = "You can't activate any more treasure boxes!";

    @Getter
    @Config
    public String noPermission = "You don t have permission to access that treasure box!";

    @Getter
    @Config
    public String noRewards = "You have no rewards to claim";

    @Getter
    @Config
    public String inventoryFull = "Your inventory is full, you have <remaining> rewards left to claim";

    @Getter
    @Config
    public String leaveLocation = "1:1:1:sand";

    @Getter
    @Config
    public String cantLeave = "You need to be in a safari world to leave";

    @Getter
    @Config
    public String claimError = "You need to leave the safari world to claim rewards";

    @Getter
    @Config
    public String safariLeave = "You have <rewards> available! Claim them with /safari claim";

    @Getter
    @Config
    public String xpRequirement = "(<level>*100)*((<level>+1)*0.2)";

    @Getter
    @Config
    public String levelUp = "You have leveled up to level <level>!";

    @Getter
    @Config
    public int invSize = 26;

    @Getter
    @Config
    public String guiName = "&6&lSafari Merchant";

    @Getter
    @Config
    public PlayerGearSave playerGear = new PlayerGearSave("safari");

    @Getter
    @Config
    public MobSwordSave mobSword = new MobSwordSave("DIAMOND_SWORD", "example",
            Collections.singletonList("&7lore"),
            Collections.singletonList(""), false, 500, 0.1, 8);

    @Getter
    @Config
    public RewardPriority rewardsPriority = examplePriority();

    @Getter
    @Config
    public List<DropItemSave> itemDrops = Arrays.asList(exampleDrop());

    @Getter
    @Config
    public List<SafariTierSave> safariTiers = new ArrayList<>(Collections.singleton(exampleSafari()));

    @Override
    public EnumFormattingType getFormatType() {
        return EnumFormattingType.YAML;
    }

    @Override
    public File getFile() {
        return new File(
                VelocityFeatures.getFileUtils().getConfigDir("Safari"),
                "safari.yml"
        );
    }

    @Override
    public boolean isStatsConfig() {
        return false;
    }

    public SafariTierSave exampleSafari() {
        String name = "tier1";
        String displayName = "&6&lSafari &7(Tier I)";
        int cooldown = 60;
        HologramSave hologram = new HologramSave("&6&lSafari &7(Tier I)",
            Collections.singletonList("cooldown: <cooldown>"),
            Collections.singletonList("currently active"),
            Collections.singletonList("available"),
            Collections.singletonList("REWARD AVAILABLE"));
        List<String> location = Collections.singletonList("2:2:2:world");
        SpecialRewardSave specialReward = new SpecialRewardSave("money", "&e&lMoney", 5, "eco give <player> 1", false);
        PriorityRewardSave prs = new PriorityRewardSave(10, Arrays.asList("regular"), Arrays.asList(specialReward));
        List<MobSave> mobs = Collections.singletonList(
            new MobSave("SKELETON", "&6&lBob the Skeleton", 20, 100, 50, 25,
                Collections.singletonList(new EquipmentSave("DIAMOND_HELMET", "", "",
                    Collections.singletonList("protection_environmental:1"))),
                Collections.singletonList("SPEED:2")));

        return new SafariTierSave(name, displayName, cooldown, 0, hologram, 3, location, 3, prs, 4, mobs);
    }

    private RewardPriority examplePriority() {
        int priority = 0;
        String permission = "safari.priority.0";

        return new RewardPriority(true,
            Collections.singletonList(new Priority(priority, permission, false, 0, false, 0)));
    }

    private DropItemSave exampleDrop() {
        String id = "regular";
        ItemMultiplierSave itemMultiplierSave = new ItemMultiplierSave(true, 10, 3);
        String material = "RAW_FISH";
        int data = 0;
        String displayName = "&6&lEpic Fish";

        return new DropItemSave(id, 10, 1, 1000, itemMultiplierSave, material, data, true, displayName, new ArrayList<>());
    }

}
