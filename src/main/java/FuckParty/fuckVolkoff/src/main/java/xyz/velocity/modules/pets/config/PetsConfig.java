package FuckParty.fuckVolkoff.src.main.java.xyz.velocity.modules.pets.config;

import dev.lyons.configapi.ConfigAPI;
import dev.lyons.configapi.ConfigClass;
import dev.lyons.configapi.EnumFormattingType;
import dev.lyons.configapi.annotations.Config;
import lombok.Getter;
import lombok.Setter;
import xyz.velocity.VelocityFeatures;
import xyz.velocity.modules.pets.config.saves.*;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Config("petsConfig")
public class PetsConfig implements ConfigClass {

    public static PetsConfig getInstance() {
        return ConfigAPI.getInstance().getConfigObject(PetsConfig.class);
    }

    @Getter
    @Setter
    @Config
    public boolean enabled = false;

    @Getter
    @Config
    public String alreadyHavePet = "You already have that pet in your inventory!";

    @Getter
    @Config
    public String petAdded = "You have added <pet> into your inventory!";

    @Getter
    @Config
    public String invFull = "Your pet inventory is full!";

    @Getter
    @Config
    public String alreadyEquipped = "You already have this pet equipped!";

    @Getter
    @Config
    public String equippedPet = "You have equipped <pet>!";

    @Getter
    @Config
    public String petLeveled = "Your <pet> has ranked up to level <level>!";

    @Getter
    @Config
    public String unEquip = "You have unequipped <pet>!";

    @Getter
    @Config
    public String playerInvFull = "Your inventory is full!";

    @Getter
    @Config
    public String takeOut = "You took out your <pet> from your pet inventory!";

    @Getter
    @Config
    public String visibilityToggle = "You have <status> pet visibility!";

    @Getter
    @Config
    public List<String> blacklistedWorlds = new ArrayList<>();

    @Getter
    @Config
    public List<PetTierSave> petTiers = Arrays.asList(exampleTier());

    @Getter
    @Config
    public List<PetSave> pets = Arrays.asList(examplePet());

    @Getter
    @Config
    public List<EffectSave> effectList = Arrays.asList(new EffectSave("speed", "Speed: <stat>"));

    @Getter
    @Config
    public List<AttachableItemSave> attachableItems = new ArrayList<>(Collections.singleton(exampleItem()));

    @Getter
    @Config
    public InventorySave inventory = exampleInv();

    @Override
    public EnumFormattingType getFormatType() {
        return EnumFormattingType.YAML;
    }

    @Override
    public File getFile() {
        return new File(
                VelocityFeatures.getFileUtils().getConfigDir("Pets"),
                "pets.yml"
        );
    }

    @Override
    public boolean isStatsConfig() {
        return false;
    }

    private PetTierSave exampleTier() {
        int tier = 1;
        String xpRequirement = "(<level>*100)*((<level>+1)*0.25)";
        int xpAmountOnUse = 500;
        int xpCooldown = 10;

        return new PetTierSave(tier, xpRequirement, new ArrayList<>(Collections.singleton(new XpSave("KILL:PLAYER:50", 10))), 50);
    }

    private PetSave examplePet() {
        String name = "tiger";
        int tier = 1;
        String texture = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZmM0MjYzODc0NDkyMmI1ZmNmNjJjZDliZjI3ZWVhYjkxYjJlNzJkNmM3MGU4NmNjNWFhMzg4Mzk5M2U5ZDg0In19fQ==";
        String type = "COMBAT";
        String displayName = "&e&lTiger Pet";
        List<String> lore = Arrays.asList("item lore");
        List<String> inventoryLore = Arrays.asList("inv lore");
        List<LevelEffectSave> levelEffects = Arrays.asList(new LevelEffectSave(5, Arrays.asList("INCREASE_DAMAGE:2")));

        return new PetSave(name, tier, 20, displayName, texture, type, lore, inventoryLore, levelEffects);
    }

    private InventorySave exampleInv() {
        String name = "&8Pets";
        int size = 27;
        int equippedSlot = 23;
        InventoryItemSave unequip = new InventoryItemSave("REDSTONE_WIRE", "&cunequip", new ArrayList<>(), 0, 24);
        InventoryItemSave takeout = new InventoryItemSave("REDSTONE_WIRE", "&atake out", new ArrayList<>(), 0, 24);
        InventoryItemSave locked = new InventoryItemSave("GLASS", "&c&llocked", new ArrayList<>(), 0, -1);
        InventoryItemSave toggle = new InventoryItemSave("REDSTONE_TORCH_ON", "&cstatus: <status>", new ArrayList<>(), 0, 24);

        return new InventorySave(name, size, equippedSlot, unequip, takeout, locked, toggle, Arrays.asList(new InventoryItemSave("STONE", "&7 ", new ArrayList<>(), 0, 0)));
    }

    private AttachableItemSave exampleItem() {
        String name = "xpshard";
        String displayName = "&b&lXP Shard &8[&3<multiplier&8]";
        String material = "PRISMARINE_SHARD";
        List<String> lore = Arrays.asList("&7Example lore");
        String attachedLore = "&f&lATTACHED: &b&lXP Shard &8[&3<multiplier&8]";
        int data = 0;

        return new AttachableItemSave(name, material, displayName, lore, attachedLore, data);
    }

}
