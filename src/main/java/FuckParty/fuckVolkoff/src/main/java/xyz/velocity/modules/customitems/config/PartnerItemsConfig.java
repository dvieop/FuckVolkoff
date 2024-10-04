package FuckParty.fuckVolkoff.src.main.java.xyz.velocity.modules.customitems.config;

import dev.lyons.configapi.ConfigAPI;
import dev.lyons.configapi.ConfigClass;
import dev.lyons.configapi.EnumFormattingType;
import dev.lyons.configapi.annotations.Config;
import lombok.Getter;
import lombok.Setter;
import xyz.velocity.VelocityFeatures;
import xyz.velocity.modules.customitems.config.saves.ItemSave;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Config(value = "itemsConfig")
public class PartnerItemsConfig implements ConfigClass {

    public static PartnerItemsConfig getInstance() {
        return ConfigAPI.getInstance().getConfigObject(PartnerItemsConfig.class);
    }

    @Getter
    @Setter
    @Config
    public boolean enabled = false;

    @Getter
    @Config
    public String usedAbilityMsg = "You have used <ability> it is now on cooldown for <cooldown>";

    @Getter
    @Config
    public String onCooldownMsg = "You are on cooldown for <ability> for another <cooldown> seconds";

    @Getter
    @Config
    public String effectPlayer = "You have affected <player> with <ability>, you can't use this for another <cooldown> seconds";

    @Getter
    @Config
    public String gotAffected = "You have been affected by <player> with <ability> for <duration>";

    @Getter
    @Config
    public String cantPlaceDueToBone = "You cannot place blocks for another <duration>";

    @Getter
    @Config
    public String gotSwitchered = "You have been switchered by <player> using <ability>";

    @Getter
    @Config
    public List<ItemSave> items = new ArrayList<>();

    @Override
    public EnumFormattingType getFormatType() {
        return EnumFormattingType.YAML;
    }

    @Override
    public File getFile() {
        return new File(
                VelocityFeatures.getFileUtils().getConfigDir("CustomItems"),
                "partner-items.yml"
        );
    }

    @Override
    public boolean isStatsConfig() {
        return false;
    }

    public void saveConfig() {
        ConfigAPI.getInstance().saveConfig(this);
    }

}
