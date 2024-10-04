package FuckParty.fuckVolkoff.src.main.java.xyz.velocity.modules.mining.config;

import dev.lyons.configapi.ConfigAPI;
import dev.lyons.configapi.ConfigClass;
import dev.lyons.configapi.EnumFormattingType;
import dev.lyons.configapi.annotations.Config;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import xyz.velocity.VelocityFeatures;
import xyz.velocity.modules.fishing.config.saves.ItemMultiplierSave;
import xyz.velocity.modules.mining.config.saves.LocationSave;
import xyz.velocity.modules.mining.config.saves.TypeSave;
import xyz.velocity.modules.safari.config.saves.*;

@Config("mining")
public class MiningConfig implements ConfigClass {

    public static MiningConfig getInstance() {
        return ConfigAPI.getInstance().getConfigObject(MiningConfig.class);
    }

    @Getter
    @Setter
    @Config
    public boolean enabled = false;

    @Getter
    @Config
    public String rewardMessage = "You've found <amount>x <reward> from mining a <block> block";

    @Getter
    @Config
    public String broadcastReward = "<player> has dug out <reward> from the mines!";

    @Getter
    @Config
    public int invSize = 36;

    @Getter
    @Config
    public String guiName = "<player> has dug out <reward> from the mines!";

    @Getter
    @Config
    public LocationSave blockBounds = new LocationSave("0:0:0:world", "10:10:10:world");

    @Getter
    @Config
    public List<TypeSave> types = Arrays.asList(new TypeSave("diamond", "&b&lDiamond Ore", "DIAMOND_ORE", 30, Arrays.asList("regular")));

    @Getter
    @Config
    public List<DropItemSave> itemDrops = Arrays.asList(exampleDrop());

    @Override
    public EnumFormattingType getFormatType() {
        return EnumFormattingType.YAML;
    }

    @Override
    public File getFile() {
        return new File(
                VelocityFeatures.getFileUtils().getConfigDir("Mining"),
                "mining.yml"
        );
    }

    @Override
    public boolean isStatsConfig() {
        return false;
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
