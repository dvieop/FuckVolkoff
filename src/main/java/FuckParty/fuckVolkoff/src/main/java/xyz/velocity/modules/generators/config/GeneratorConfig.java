package FuckParty.fuckVolkoff.src.main.java.xyz.velocity.modules.generators.config;

import dev.lyons.configapi.ConfigAPI;
import dev.lyons.configapi.ConfigClass;
import dev.lyons.configapi.EnumFormattingType;
import dev.lyons.configapi.annotations.Config;
import lombok.Getter;
import lombok.Setter;
import xyz.velocity.VelocityFeatures;
import xyz.velocity.modules.generators.config.saves.*;
import xyz.velocity.modules.pets.config.saves.InventoryItemSave;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Config("generator")
public class GeneratorConfig implements ConfigClass {

    public static GeneratorConfig getInstance() {
        return ConfigAPI.getInstance().getConfigObject(GeneratorConfig.class);
    }

    @Getter
    @Setter
    @Config
    public boolean enabled = false;

    @Getter
    @Config
    String tierUpgrade = "You have successfully upgraded generator to tier <tier>";

    @Getter
    @Config
    String storageUpgrade = "You have upgraded generator storage to level <storageLevel>";

    @Getter
    @Config
    String placeMessage = "You have broken your generator <generator>"; // will say generator [Tier V]

    @Getter
    @Config
    String breakMessage = "You have placed a generator <generator>";

    @Getter
    @Config
    String alreadyMax = "This upgrade is already on the max level!";

    @Getter
    @Config
    String cantAfford = "You don't have enough money to upgrade!";

    @Getter
    @Config
    String limitMessage = "You have reached a limit of 5 generators for your faction!";

    @Getter
    @Config
    List<String> previewLore = new ArrayList<>();

    @Getter
    @Config
    int factionLimit = 5;

    @Getter
    @Config
    List<GeneratorSave> generators = Arrays.asList(exampleGenerator());

    @Getter
    @Config
    InventorySave inventory = exampleInv();

    @Override
    public EnumFormattingType getFormatType() {
        return EnumFormattingType.YAML;
    }

    @Override
    public File getFile() {
        return new File(
                VelocityFeatures.getFileUtils().getConfigDir("Generator"),
                "generator.yml"
        );
    }

    @Override
    public boolean isStatsConfig() {
        return false;
    }

    private GeneratorSave exampleGenerator() {
        TierUpgradeSave tierUpgradeSave = new TierUpgradeSave(true, 5, 1000000, 1000, 50);
        StorageUpgradeSave storageUpgradeSave = new StorageUpgradeSave(true, 5, 10000, 50000000);
        List<String> hologram = new ArrayList<>();

        return new GeneratorSave("gen1", tierUpgradeSave, storageUpgradeSave, hologram, exampleItem());
    }

    private GenItemSave exampleItem() {
        String texture = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjg0M2RlM2Q0MzYyMWIzM2Y5YmJiMDk4ODQ5N2JlYTkzMTliMDdhODJmODY4MmQzZThhMmVhZDk1NTI2N2M4YSJ9fX0=";
        String itemName = "&a&lCactus Generator";

        List<String> lore = new ArrayList<>();

        lore.add("Tier: <tier>");
        lore.add("Speed: <speed>");
        lore.add("Money/min: <moneyInterval>");
        lore.add("Storage: <storage>/<capacity>");

        return new GenItemSave(texture, itemName, lore);
    }

    private InventorySave exampleInv() {
        String name = "&8Generator";
        int size = 27;
        InventoryItemSave mainItem = new InventoryItemSave("CACTUS", "&a&lGeneral Information", new ArrayList<>(), 0, 13);
        InventoryItemSave tierItem = new InventoryItemSave("NETHER_STAR", "&d&lTier Upgrade", new ArrayList<>(), 0, 11);
        InventoryItemSave storageItem = new InventoryItemSave("CHEST", "&6&lStorage Upgrade", new ArrayList<>(), 0, 15);
        InventoryItemSave logsItem = new InventoryItemSave("PAPER", "&e&lSell Logs", new ArrayList<>(), 0, 8);
        InventoryItemSave filler = new InventoryItemSave("STAINED_GLASS_PANE", "&7 ", new ArrayList<>(), 7, 0);

        return new InventorySave(name, size, mainItem, tierItem, storageItem, logsItem, filler);
    }

}
