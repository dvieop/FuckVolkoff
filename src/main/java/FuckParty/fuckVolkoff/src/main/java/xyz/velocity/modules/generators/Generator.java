package FuckParty.fuckVolkoff.src.main.java.xyz.velocity.modules.generators;

import com.earth2me.essentials.Essentials;
import com.golfing8.kore.FactionsKore;
import com.google.common.collect.Lists;
import de.tr7zw.nbtapi.NBTCompound;
import de.tr7zw.nbtapi.NBTItem;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectList;
import lombok.Getter;
import net.ess3.api.MaxMoneyException;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import xyz.velocity.VelocityFeatures;
import xyz.velocity.commands.CommandAPI;
import xyz.velocity.modules.AbstractModule;
import xyz.velocity.modules.Module;
import xyz.velocity.modules.generators.commands.GeneratorCommand;
import xyz.velocity.modules.generators.config.GeneratorConfig;
import xyz.velocity.modules.generators.config.StorageConfig;
import xyz.velocity.modules.generators.config.saves.*;
import xyz.velocity.modules.pets.config.saves.InventoryItemSave;
import xyz.velocity.modules.util.ItemUtil;
import xyz.velocity.modules.util.SkullUtil;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Module
public class Generator extends AbstractModule {

    public Generator() {
        instance = this;
    }

    public static ObjectList<GenCache> placedGenerators = new ObjectArrayList<>();
    
    public void loadGenerators() {
        placedGenerators.clear();

        for (Object2ObjectMap.Entry<String, ArrayList<GenDataSave>> map : StorageConfig.getInstance().generators.object2ObjectEntrySet()) {
            String factionId = map.getKey();
            List<GenDataSave> gens = map.getValue();

            for (GenDataSave gen : gens) {
                Location location = xyz.velocity.modules.util.Location.parseToLocation(gen.getLocation());

                GenCache genCache = new GenCache(gen);
                Hologram hologram = new Hologram(VelocityFeatures.getInstance(), location.add(0.5, 4, 0.5), genCache);

                genCache.setHologram(hologram);

                placedGenerators.add(genCache);
            }
        }
    }

    public void unloadGenerators() {
        for (GenCache placedGenerator : placedGenerators) {
            placedGenerator.hologram.deleteHologram();
        }

        placedGenerators.clear();
    }

    public ItemStack buildItem(String id, int tier) {

        GeneratorSave generatorSave = GeneratorConfig.getInstance().getGenerators().stream().filter(obj -> obj.getId().equalsIgnoreCase(id)).findFirst().orElse(null);

        if (generatorSave == null) return null;

        GenItemSave genItemSave = generatorSave.getItem();

        ItemStack item = SkullUtil.skullItem(genItemSave.getSkullTexture());
        ItemMeta meta = item.getItemMeta();

        String lore = VelocityFeatures.chat(String.join("VDIB", genItemSave.getLore()));

        int speed = tier * generatorSave.getTierUpgrade().getSpeedIncrement();
        int moneyInterval = speed * generatorSave.getTierUpgrade().getBaseMoney();

        lore = lore
                .replace("<tier>", tier + "")
                .replace("<speed>", speed + "")
                .replace("<moneyInterval>", moneyInterval + "")
                .replace("<storage>", formatNumber(0))
                .replace("<capacity>", formatNumber(generatorSave.getStorageUpgrade().getStorageUpgradeIncrement()));

        meta.setDisplayName(VelocityFeatures.chat(genItemSave.getName()));
        meta.setLore(Arrays.asList(lore.split("VDIB")));

        item.setItemMeta(meta);

        NBTItem nbtItem = new NBTItem(item);

        NBTCompound nbtCompound = nbtItem.addCompound("velocity_generator_item");

        nbtCompound.setString("type", generatorSave.getId());
        nbtCompound.setInteger("tier", tier);
        nbtCompound.setInteger("speed", speed);
        nbtCompound.setInteger("moneyInterval", moneyInterval);
        nbtCompound.setInteger("storage", 0);
        nbtCompound.setInteger("capacity", generatorSave.getStorageUpgrade().getStorageUpgradeIncrement());

        return nbtItem.getItem();

    }

    public ItemStack updateItem(NBTItem nbtItem, NBTCompound nbtCompound, GenDataSave genDataSave) {
        String type = nbtCompound.getString("type");

        int speed = nbtCompound.getInteger("speed");
        int moneyInterval = nbtCompound.getInteger("moneyInterval");

        GeneratorSave generatorSave = GeneratorConfig.getInstance().getGenerators()
                .stream()
                .filter(obj -> obj.getId().equalsIgnoreCase(type))
                .findFirst()
                .orElse(null);

        if (generatorSave == null) return null;

        ItemStack item = nbtItem.getItem();
        ItemMeta meta = item.getItemMeta();

        String lore = VelocityFeatures.chat(String.join("VDIB", generatorSave.getItem().getLore()));

        lore = lore
                .replace("<tier>", genDataSave.getTier() + "")
                .replace("<speed>", speed + "")
                .replace("<moneyInterval>", moneyInterval + "")
                .replace("<storage>", formatNumber(genDataSave.getStorage()))
                .replace("<capacity>", formatNumber(genDataSave.getCapacity()));

        meta.setDisplayName(VelocityFeatures.chat(generatorSave.getItem().getName()));
        meta.setLore(Arrays.asList(lore.split("VDIB")));

        item.setItemMeta(meta);

        return item;
    }

    public boolean isGeneratorBlock(Location location) {
        String factionId = FactionsKore.getIntegration().getFactionsIdAt(location);
        String checkLocation = xyz.velocity.modules.util.Location.parseToString(location);

        if (!StorageConfig.getInstance().hasFaction(factionId)) return false;

        List<GenDataSave> list = StorageConfig.getInstance().generators.get(factionId);

        for (GenDataSave genDataSave : list) {
            if (checkLocation.equals(genDataSave.getLocation())) return true;
        }

        return false;
    }

    public GenDataSave getGenerator(Location location) {
        String factionId = FactionsKore.getIntegration().getFactionsIdAt(location);
        String checkLocation = xyz.velocity.modules.util.Location.parseToString(location);

        return StorageConfig.getInstance().generators.get(factionId)
                .stream()
                .filter(obj -> obj.getLocation().equalsIgnoreCase(checkLocation))
                .findFirst()
                .orElse(null);
    }

    public void removeGeneratorFromCache(GenDataSave genDataSave) {
        for (ObjectIterator<GenCache> iterator = placedGenerators.iterator(); iterator.hasNext();) {
            GenCache genCache = iterator.next();

            if (genCache.getGenDataSave().equals(genDataSave)) {
                genCache.hologram.deleteHologram();
                iterator.remove();
                break;
            }
        }
    }

    public String formatNumber(double number) {
        return String.format("%.2fM", number/ 1000000.0);
    }

    public String tierBar(int tier, int maxTier) {
        StringBuilder finalStr = new StringBuilder();

        for (int i = 0; i < tier; i++) {
            finalStr.append("&a●");
        }

        int remaining = maxTier - tier;

        for (int i = 0; i < remaining; i++) {
            finalStr.append("&c●");
        }

        return "&8[" + finalStr + "&8]";
    }

    public void addInvContents(Inventory inventory, InventorySave inventorySave) {
        for (int i = 0; i < inventory.getSize(); i++) {
            if (!ItemUtil.hasNoItemMeta(inventory.getItem(i))) continue;

            ItemStack itemStack = new ItemStack(Material.getMaterial(inventorySave.getFiller().getMaterial()), 1, (byte) inventorySave.getFiller().getData());
            ItemMeta meta = itemStack.getItemMeta();

            meta.setDisplayName(VelocityFeatures.chat("&7 "));
            itemStack.setItemMeta(meta);

            inventory.setItem(i, itemStack);
        }
    }

    public void setSpecialSlot(GenDataSave gen, Inventory inv, InventoryItemSave iis, int slot, boolean main) {
        ItemStack item = new ItemStack(Material.getMaterial(iis.getMaterial()), 1, (byte) iis.getData());

        ItemMeta meta = item.getItemMeta();

        GeneratorSave generatorSave = GeneratorConfig.getInstance().getGenerators()
                .stream()
                .filter(obj -> obj.getId().equalsIgnoreCase(gen.getGenType()))
                .findFirst()
                .orElse(null);

        if (generatorSave == null) return;

        int tier = gen.getTier();
        int maxTier = generatorSave.getTierUpgrade().getMaxTier();
        int maxCapacity = generatorSave.getStorageUpgrade().getMaxLevel() * generatorSave.getStorageUpgrade().getStorageUpgradeIncrement();

        int nextTier = gen.getTier() + 1;
        int speed = gen.getTier() * generatorSave.getTierUpgrade().getSpeedIncrement();
        int nextSpeed = (gen.getTier() + 1) * generatorSave.getTierUpgrade().getSpeedIncrement();
        int moneyInterval = speed * generatorSave.getTierUpgrade().getBaseMoney();
        int nextMoneyInterval = nextSpeed * generatorSave.getTierUpgrade().getBaseMoney();
        int tierCost = gen.getTier() * generatorSave.getTierUpgrade().getUpgradeCostIncrement();
        int capacity = gen.getCapacity();

        String lore = VelocityFeatures.chat(String.join("VDIB", iis.getLore()));

        lore = lore
                .replace("<tier>", gen.getTier() + "")
                .replace("<nextTier>", nextTier + "")
                .replace("<speed>", speed + "")
                .replace("<nextSpeed>", nextSpeed + "")
                .replace("<moneyInterval>", String.format("%,d", moneyInterval))
                .replace("<nextMoneyInterval>", String.format("%,d", nextMoneyInterval))
                .replace("<tierCost>", String.format("%,d", tierCost))
                .replace("<capacity>", formatNumber(capacity))
                .replace("<nextCapacity>", formatNumber(capacity + generatorSave.getStorageUpgrade().getStorageUpgradeIncrement()))
                .replace("<storage>", formatNumber(gen.getStorage()))
                .replace("<storageCost>", String.format("%,d", generatorSave.getStorageUpgrade().getUpgradeCostIncrement() * (getStorageTier(capacity, maxCapacity) + 1)))
                .replace("<tierMaxBoolean>", formatUpgrades(tier, maxTier, "&7 "))
                .replace("<storageMaxBoolean>", formatUpgrades(capacity, maxCapacity, "&7 "));

        meta.setDisplayName(VelocityFeatures.chat(iis.getDisplayName()));
        meta.setLore(Arrays.asList(lore.split("VDIB")));

        item.setItemMeta(meta);

        if (main) {
            item = addNbtData(item, gen, generatorSave);
        }

        inv.setItem(slot, item);
    }

    private void setLogsSlot(GenDataSave genDataSave, Inventory inv, InventoryItemSave iis, int slot) {
        ItemStack item = new ItemStack(Material.getMaterial(iis.getMaterial()), 1, (byte) iis.getData());

        ItemMeta meta = item.getItemMeta();

        List<String> sellerList = new ArrayList<>(iis.getLore());

        int logSlot = 0;

        for (int i = 0; i < sellerList.size(); i++) {
            String str = sellerList.get(i);

            if (!str.contains("<seller>")) continue;
            if (genDataSave.getLogs().size() > logSlot) {
                String[] split = Lists.reverse(genDataSave.getLogs()).get(logSlot).split(":");

                sellerList.set(i, str
                        .replace("<seller>", split[0])
                        .replace("<amount>", split[1]));

                logSlot++;
            } else {
                sellerList.set(i, "");
            }
        }

        String lore = VelocityFeatures.chat(String.join("VDIB", sellerList));

        meta.setDisplayName(VelocityFeatures.chat(iis.getDisplayName()));
        meta.setLore(Arrays.asList(lore.split("VDIB")));

        item.setItemMeta(meta);

        inv.setItem(slot, item);
    }

    public ItemStack addNbtData(ItemStack item, GenDataSave genDataSave, GeneratorSave generatorSave) {
        int speed = genDataSave.getTier() * generatorSave.getTierUpgrade().getSpeedIncrement();
        int moneyInterval = speed * generatorSave.getTierUpgrade().getBaseMoney();

        NBTItem nbtItem = new NBTItem(item);
        NBTCompound nbtCompound = nbtItem.addCompound("velocity_generator_item");

        nbtCompound.setString("type", genDataSave.getGenType());
        nbtCompound.setString("location", genDataSave.getLocation());
        nbtCompound.setInteger("tier", genDataSave.getTier());
        nbtCompound.setInteger("speed", speed);
        nbtCompound.setInteger("moneyInterval", moneyInterval);
        nbtCompound.setInteger("storage", genDataSave.getStorage());
        nbtCompound.setInteger("capacity", genDataSave.getCapacity());

        return nbtItem.getItem();
    }

    public void handleTierUpgrade(Player p, Inventory inv, GeneratorSave generatorSave, int tier, String location) {
        GeneratorConfig config = GeneratorConfig.getInstance();

        int tierCost = tier * generatorSave.getTierUpgrade().getUpgradeCostIncrement();

        if (tier >= generatorSave.getTierUpgrade().getMaxTier()) {
            p.sendMessage(VelocityFeatures.chat(config.getAlreadyMax()));
            return;
        }
        if (!canAfford(p, tierCost)) return;

        GenCache genCache = placedGenerators.stream().filter(obj -> obj.getGenDataSave().getLocation().equalsIgnoreCase(location)).findFirst().orElse(null);

        if (genCache == null) return;

        genCache.getGenDataSave().setTier(tier + 1);
        genCache.getHologram().updateHologram(generatorSave.getHologram());

        p.sendMessage(VelocityFeatures.chat(config.getTierUpgrade()
                .replace("<tier>", (tier + 1) + "")
        ));

        Essentials.getPlugin(Essentials.class).getUser(p).takeMoney(BigDecimal.valueOf(tierCost));
        reloadInventory(p, inv, xyz.velocity.modules.util.Location.parseToLocation(location));
    }

    public void handleStorageUpgrade(Player p, Inventory inv, GeneratorSave generatorSave, int capacity, String location) {
        GeneratorConfig config = GeneratorConfig.getInstance();

        int maxCapacity = generatorSave.getStorageUpgrade().getMaxLevel() * generatorSave.getStorageUpgrade().getStorageUpgradeIncrement();
        int storageTier = getStorageTier(capacity, maxCapacity) + 1;
        int storageCost = generatorSave.getStorageUpgrade().getUpgradeCostIncrement() * storageTier;

        if (capacity >= maxCapacity) {
            p.sendMessage(VelocityFeatures.chat(config.getAlreadyMax()));
            return;
        }
        if (!canAfford(p, storageCost)) return;

        GenCache genCache = placedGenerators.stream().filter(obj -> obj.getGenDataSave().getLocation().equalsIgnoreCase(location)).findFirst().orElse(null);

        if (genCache == null) return;

        int newCapacity = capacity + generatorSave.getStorageUpgrade().getStorageUpgradeIncrement();

        genCache.getGenDataSave().setCapacity(newCapacity);
        genCache.getHologram().updateHologram(generatorSave.getHologram());

        p.sendMessage(VelocityFeatures.chat(config.getStorageUpgrade()
                .replace("<storageLevel>", formatNumber(newCapacity))
        ));

        Essentials.getPlugin(Essentials.class).getUser(p).takeMoney(BigDecimal.valueOf(storageCost));
        reloadInventory(p, inv, xyz.velocity.modules.util.Location.parseToLocation(location));
    }

    public void handleSelling(Player p, Inventory inv, GeneratorSave generatorSave, int capacity, String location) {
        GeneratorConfig config = GeneratorConfig.getInstance();

        GenCache genCache = placedGenerators.stream().filter(obj -> obj.getGenDataSave().getLocation().equalsIgnoreCase(location)).findFirst().orElse(null);

        if (genCache == null) return;

        int money = genCache.getGenDataSave().getStorage();

        if (!(money > 0)) return;

        genCache.getGenDataSave().setStorage(0);
        genCache.getGenDataSave().getLogs().add(p.getName() + ":" + formatNumber(money));
        genCache.getHologram().updateHologram(generatorSave.getHologram());

        if (genCache.getGenDataSave().getLogs().size() > 10) {
            genCache.getGenDataSave().getLogs().remove(0);
        }

        try {
            Essentials.getPlugin(Essentials.class).getUser(p).giveMoney(BigDecimal.valueOf(money));
        } catch (MaxMoneyException err) {
            return;
        }

        reloadInventory(p, inv, xyz.velocity.modules.util.Location.parseToLocation(location));
    }

    public void generatorBreak(GenDataSave genDataSave, Location blockLocation) {
        String factionId = FactionsKore.getIntegration().getFactionsIdAt(blockLocation);
        ItemStack genItem = Generator.getInstance().buildItem(genDataSave.getGenType(), genDataSave.getTier());

        NBTItem nbtItem = new NBTItem(genItem);
        NBTCompound nbtCompound = nbtItem.getCompound("velocity_generator_item");

        nbtCompound.setInteger("storage", genDataSave.getStorage());
        nbtCompound.setInteger("capacity", genDataSave.getCapacity());

        blockLocation.getWorld().dropItem(blockLocation, Generator.getInstance().updateItem(nbtItem, nbtCompound, genDataSave));

        removeGeneratorFromCache(genDataSave);
        StorageConfig.getInstance().removeGenerator(factionId, genDataSave);
    }

    public void reloadInventory(Player player, Inventory inv, Location location) {
        inv.clear();

        addInvContents(player, location);
    }

    private boolean canAfford(Player p, int cost) {
        if (!Essentials.getPlugin(Essentials.class).getUser(p).canAfford(BigDecimal.valueOf(cost))) {
            p.sendMessage(VelocityFeatures.chat(GeneratorConfig.getInstance().getCantAfford()));
            return false;
        }

        return true;
    }

    public void addInvContents(Player player, Location location) {
        GenDataSave gen = Generator.getInstance().getGenerator(location);

        InventorySave inventorySave = GeneratorConfig.getInstance().getInventory();
        Inventory inv = Bukkit.createInventory(null, inventorySave.getSize(), VelocityFeatures.chat(inventorySave.getGuiName()));

        setSpecialSlot(gen, inv, inventorySave.getMainItem(), inventorySave.getMainItem().getSlot(), true);
        setSpecialSlot(gen, inv, inventorySave.getStorageItem(), inventorySave.getStorageItem().getSlot(), false);
        setSpecialSlot(gen, inv, inventorySave.getTierItem(), inventorySave.getTierItem().getSlot(), false);
        setLogsSlot(gen, inv, inventorySave.getLogsItem(), inventorySave.getLogsItem().getSlot());
        addInvContents(inv, inventorySave);

        player.openInventory(inv);
    }

    private int getStorageTier(int capacity, int maxCapacity) {
        int calc = maxCapacity / capacity;
        return calc - (calc - 1);
    }

    private String formatUpgrades(int tier, int maxTier, String str) {
        if (tier >= maxTier) return VelocityFeatures.chat("&7VDIB&c&lMAX LEVELVDIB&7");
        return VelocityFeatures.chat(str);
    }

    @Getter
    private static Generator instance;

    @Override
    public String getName() {
        return "generator";
    }

    @Override
    public boolean isEnabled() {
        return GeneratorConfig.getInstance().isEnabled();
    }

    @Override
    public void onEnable() {
        VelocityFeatures.registerEvent(new GeneratorListener());
        CommandAPI.getInstance().enableCommand(new GeneratorCommand());

        loadGenerators();
        new GeneratorInterval();

        GeneratorConfig.getInstance().setEnabled(true);
    }

    @Override
    public void onDisable() {
        VelocityFeatures.unregisterEvent(GeneratorListener.getInstance());
        CommandAPI.getInstance().disableCommand(GeneratorCommand.class);
        GeneratorInterval.getInstance().bukkitTask.cancel();

        unloadGenerators();

        GeneratorConfig.getInstance().setEnabled(false);
    }

    @Override
    public String placeholderRequest(Player player, String arg) {
        return "";
    }

}
