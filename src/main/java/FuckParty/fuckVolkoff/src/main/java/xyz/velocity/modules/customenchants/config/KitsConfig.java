package FuckParty.fuckVolkoff.src.main.java.xyz.velocity.modules.customenchants.config;

import dev.lyons.configapi.ConfigAPI;
import dev.lyons.configapi.ConfigClass;
import dev.lyons.configapi.EnumFormattingType;
import dev.lyons.configapi.annotations.Config;
import lombok.Getter;
import xyz.velocity.VelocityFeatures;
import xyz.velocity.modules.customenchants.config.saves.Item;
import xyz.velocity.modules.customenchants.config.saves.KitEnchant;
import xyz.velocity.modules.customenchants.config.saves.KitSave;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Config("Kits")
public class KitsConfig implements ConfigClass {

    public static KitsConfig getInstance() {
        return ConfigAPI.getInstance().getConfigObject(KitsConfig.class);
    }

    @Getter
    @Config
    public List<KitSave> kits = new ArrayList<>(Collections.singleton(exampleKit()));

    @Override
    public EnumFormattingType getFormatType() {
        return EnumFormattingType.YAML;
    }

    @Override
    public File getFile() {
        return new File(
                VelocityFeatures.getFileUtils().getConfigDir("CustomEnchants"),
                "kits.yml"
        );
    }

    @Override
    public boolean isStatsConfig() {
        return false;
    }

    private KitSave exampleKit() {
        List<Item> items = new ArrayList<>();
        List<String> enchants = new ArrayList<>();

        enchants.add("protection_environmental:5");

        KitEnchant kitEnchant = new KitEnchant("Deathbringer", false, 2, 100);

        items.add(new Item("&6Example Helmet", Arrays.asList("example lore"), "DIAMOND_HELMET", enchants, Arrays.asList(kitEnchant)));
        return new KitSave("warrior", items);
    }

}
