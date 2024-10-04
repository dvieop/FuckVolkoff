package FuckParty.fuckVolkoff.src.main.java.xyz.velocity.modules.armorsets.config;

import dev.lyons.configapi.ConfigAPI;
import dev.lyons.configapi.ConfigClass;
import dev.lyons.configapi.EnumFormattingType;
import dev.lyons.configapi.annotations.Config;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;
import lombok.Getter;
import lombok.Setter;
import xyz.velocity.VelocityFeatures;
import xyz.velocity.modules.armorsets.config.saves.ArmorSave;
import xyz.velocity.modules.armorsets.config.saves.SetItemSave;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Config("armorsets")
public class ArmorConfig implements ConfigClass {

    public static ArmorConfig getInstance() {
        return ConfigAPI.getInstance().getConfigObject(ArmorConfig.class);
    }

    @Getter
    @Setter
    @Config
    public boolean enabled = false;

    @Getter
    @Config
    public String equippedMessage = "You have equipped <set>";

    @Getter
    @Config
    public String unequipMessage = "You have unequipped <set>";

    @Getter
    @Setter
    @Config
    public List<ArmorSave> sets = new ArrayList<>(Collections.singleton(exampleSet()));

    @Override
    public EnumFormattingType getFormatType() {
        return EnumFormattingType.YAML;
    }

    @Override
    public File getFile() {
        return new File(
                VelocityFeatures.getFileUtils().getConfigDir("ArmorSets"),
                "armor-sets.yml"
        );
    }

    @Override
    public boolean isStatsConfig() {
        return false;
    }

    private ArmorSave exampleSet() {
        String name = "example";
        String chatName = "&6example";
        List<String> lore = Arrays.asList("lore");
        List<String> vanillaEffects = Arrays.asList("SPEED:3");
        List<String> customEffects = Arrays.asList("HEALTH:4");

        String displayName = "&6Helmet";
        String material = "DIAMOND_HELMET";
        List<String> enchants = Arrays.asList("damage_all:5");
        List<SetItemSave> items = Arrays.asList(new SetItemSave(displayName, material, "", enchants));

        return new ArmorSave(name, chatName, new ArrayList<>(), lore, vanillaEffects, customEffects, items, 20);
    }

}
