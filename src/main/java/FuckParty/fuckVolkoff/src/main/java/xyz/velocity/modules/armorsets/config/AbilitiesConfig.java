package FuckParty.fuckVolkoff.src.main.java.xyz.velocity.modules.armorsets.config;

import dev.lyons.configapi.ConfigAPI;
import dev.lyons.configapi.ConfigClass;
import dev.lyons.configapi.EnumFormattingType;
import dev.lyons.configapi.annotations.Config;
import lombok.Getter;
import xyz.velocity.VelocityFeatures;
import xyz.velocity.modules.armorsets.config.saves.AbilitySave;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Config("abilitiesConfig")
public class AbilitiesConfig implements ConfigClass {

    public static AbilitiesConfig getInstance() {
        return ConfigAPI.getInstance().getConfigObject(AbilitiesConfig.class);
    }

    @Getter
    @Config
    public List<AbilitySave> abilities = new ArrayList<>(Collections.singleton(exampleAbility()));

    @Override
    public EnumFormattingType getFormatType() {
        return EnumFormattingType.YAML;
    }

    @Override
    public File getFile() {
        return new File(
                VelocityFeatures.getFileUtils().getConfigDir("ArmorSets"),
                "abilities.yml"
        );
    }

    @Override
    public boolean isStatsConfig() {
        return false;
    }

    public void saveConfig() {
        ConfigAPI.getInstance().saveConfig(this);
    }

    private AbilitySave exampleAbility() {
        String name = "example";
        String chatName = "&6example";
        String attMsg = "You have affected <player> with <ability>";
        String defMsg = "You have been affected by <ability>";
        double damageMulti = 1.0;
        double damageRed = 1.0;
        double chance = 1.5;
        int duration = 5;
        int cooldown = 60;

        return new AbilitySave(name, chatName, attMsg, defMsg, damageMulti, damageRed, chance, duration, cooldown, "exampleset");
    }
}
