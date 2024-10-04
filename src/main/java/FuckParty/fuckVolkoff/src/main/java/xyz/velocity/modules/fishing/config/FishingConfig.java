package FuckParty.fuckVolkoff.src.main.java.xyz.velocity.modules.fishing.config;

import dev.lyons.configapi.ConfigAPI;
import dev.lyons.configapi.ConfigClass;
import dev.lyons.configapi.EnumFormattingType;
import dev.lyons.configapi.annotations.Config;
import lombok.Getter;
import lombok.Setter;
import xyz.velocity.VelocityFeatures;
import xyz.velocity.modules.fishing.config.saves.*;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Config("FishingConfig")
public class FishingConfig implements ConfigClass {

    public static FishingConfig getInstance() {
        return ConfigAPI.getInstance().getConfigObject(FishingConfig.class);
    }

    @Getter
    @Setter
    @Config
    public boolean enabled = false;

    @Getter
    @Config
    public String fishReelMessage = "You have reeled in <amount>x <fish>!";

    @Getter
    @Config
    public String rewardMessage = "You have caught <reward> while fishing!";

    @Getter
    @Config
    public String chatWinMessage = "<player> has caught <reward> while fishing";

    @Getter
    @Config
    public int reelTimeTicks = 100;

    @Getter
    @Config
    public FishingInventorySave inventory = new FishingInventorySave("Fishing Merchant", 36);

    @Getter
    @Config
    public RodItemSave fishingRod = exampleRod();

    @Getter
    @Config
    public RewardPriority rewardsPriority = examplePriority();

    @Getter
    @Config
    public List<FishItemSave> fishList = Arrays.asList(exampleFish());

    @Getter
    @Config
    public List<PriorityRewardSave> rewards = new ArrayList<>(Collections.singleton(exampleReward()));

    @Override
    public EnumFormattingType getFormatType() {
        return EnumFormattingType.YAML;
    }

    @Override
    public File getFile() {
        return new File(
                VelocityFeatures.getFileUtils().getConfigDir("Fishing"),
                "config.yml"
        );
    }

    @Override
    public boolean isStatsConfig() {
        return false;
    }

    private RodItemSave exampleRod() {
        String name = "fishing_rod";
        String displayName = "&6&lFishermans Rod";
        String material = "FISHING_ROD";
        List<String> lore = Arrays.asList("example lore");
        List<String> worlds = Arrays.asList("Overworld");

        return new RodItemSave(name, displayName, material, lore, worlds);
    }

    private PriorityRewardSave exampleReward() {
        String name = "chest";
        String chatName = "&6&lChest of Fortune";
        String command = "give <player> chest";
        double chance = 0.5;
        boolean broadcast = false;

        SpecialRewardSave rewardSave = new SpecialRewardSave(name, chatName, command, chance, broadcast);

        return new PriorityRewardSave(0, 10, Arrays.asList("regular"), Arrays.asList(rewardSave));
    }

    private FishItemSave exampleFish() {
        String id = "regular";
        int amount = 1;
        int sellPrice = 1000;
        ItemMultiplierSave itemMultiplierSave = new ItemMultiplierSave(true, 10, 3);
        String material = "RAW_FISH";
        int data = 0;
        String displayName = "&6&lEpic Fish";

        return new FishItemSave(id, 10, amount, sellPrice, itemMultiplierSave, material, data, true, displayName, new ArrayList<>());
    }

    private RewardPriority examplePriority() {
        int priority = 0;
        String permission = "fishing.priority.0";

        return new RewardPriority(true, Arrays.asList(new Priority(priority, 100, permission)));
    }

}
