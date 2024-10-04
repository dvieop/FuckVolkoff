package FuckParty.fuckVolkoff.src.main.java.xyz.velocity.modules.armorsets.config;

import dev.lyons.configapi.ConfigAPI;
import dev.lyons.configapi.ConfigClass;
import dev.lyons.configapi.EnumFormattingType;
import dev.lyons.configapi.annotations.Config;
import lombok.Getter;
import xyz.velocity.VelocityFeatures;
import xyz.velocity.modules.armorsets.config.saves.ArmorsetToggleSave;
import xyz.velocity.modules.armorsets.config.saves.EffectsToggleSave;
import xyz.velocity.modules.armorsets.config.saves.ItemSave;
import xyz.velocity.modules.armorsets.config.saves.NBTSave;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Config("weaponsConfig")
public class SpecialItemsConfig implements ConfigClass {

    public static SpecialItemsConfig getInstance() {
        return ConfigAPI.getInstance().getConfigObject(SpecialItemsConfig.class);
    }

    @Getter
    @Config
    public List<ItemSave> items = new ArrayList<>(Collections.singleton(exampleWeapon()));

    @Override
    public EnumFormattingType getFormatType() {
        return EnumFormattingType.YAML;
    }

    @Override
    public File getFile() {
        return new File(
                VelocityFeatures.getFileUtils().getConfigDir("ArmorSets"),
                "special-items.yml"
        );
    }

    @Override
    public boolean isStatsConfig() {
        return false;
    }

    private ItemSave exampleWeapon() {
        String name = "example";
        String displayName = "&6example";
        List<String> lore = Arrays.asList("lore");

        String material = "DIAMOND_HOE";
        List<String> enchants = Arrays.asList("damage_all:5");

        EffectsToggleSave effects = new EffectsToggleSave(false, new ArrayList<>());
        ArmorsetToggleSave armor = new ArmorsetToggleSave(false, "zeus");
        NBTSave nbt = new NBTSave(false, "", "", "");

        return new ItemSave(name, displayName, lore, enchants, material, effects, armor, nbt);
    }
}
