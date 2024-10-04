package FuckParty.fuckVolkoff.src.main.java.xyz.velocity.modules.mining;

import de.tr7zw.nbtapi.NBTCompound;
import de.tr7zw.nbtapi.NBTItem;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import xyz.velocity.VelocityFeatures;
import xyz.velocity.commands.CommandAPI;
import xyz.velocity.modules.AbstractModule;
import xyz.velocity.modules.Module;
import xyz.velocity.modules.mining.commands.MiningCommand;
import xyz.velocity.modules.mining.config.MiningConfig;
import xyz.velocity.modules.mining.config.saves.TypeSave;
import xyz.velocity.modules.safari.config.saves.DropItemSave;
import xyz.velocity.modules.util.ItemUtil;
import xyz.velocity.modules.util.SkullUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Module
public class Mining extends AbstractModule {

    public Mining() {
        instance = this;
    }

    @Getter
    private static Mining instance;
    public static Object2ObjectMap<Location, OreCache> miningBlocks = new Object2ObjectOpenHashMap<>();

    public void loadBlocks() {
        miningBlocks.clear();

        Location corner1 = xyz.velocity.modules.util.Location.parseToLocation(MiningConfig.getInstance().blockBounds.getPos1());
        Location corner2 = xyz.velocity.modules.util.Location.parseToLocation(MiningConfig.getInstance().blockBounds.getPos2());

        int minX = Math.min(corner1.getBlockX(), corner2.getBlockX());
        int maxX = Math.max(corner1.getBlockX(), corner2.getBlockX());
        int minY = Math.min(corner1.getBlockY(), corner2.getBlockY());
        int maxY = Math.max(corner1.getBlockY(), corner2.getBlockY());
        int minZ = Math.min(corner1.getBlockZ(), corner2.getBlockZ());
        int maxZ = Math.max(corner1.getBlockZ(), corner2.getBlockZ());

        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y < maxY; y++) {
                for (int z = minZ; z <= maxZ; z++) {
                    Block block = corner1.getWorld().getBlockAt(x, y, z);

                    if (!block.getType().name().endsWith("_ORE")) continue;

                    Location location = block.getLocation();
                    String blockType = block.getType().name().replace("_ORE", "");

                    TypeSave type = MiningConfig.getInstance().types
                            .stream()
                            .filter(obj -> obj.getTypeName().equalsIgnoreCase(blockType))
                            .findFirst()
                            .orElse(null);

                    if (type == null) continue;

                    miningBlocks.put(location, new OreCache(0, location, type));
                }
            }
        }
    }

    public ItemStack getDropItem(DropItemSave drop, int amount) {
        ItemStack item;

        if (drop.getMaterial().startsWith("head")) {
            item = createDropItem(drop, SkullUtil.skullItem(drop.getMaterial().replace("head-", "")), amount);
        } else {
            item = createDropItem(drop, amount);
        }

        NBTItem nbtItem = new NBTItem(item);
        NBTCompound nbtCompound = nbtItem.addCompound("velocity_mining_drop");

        nbtCompound.setString("id", drop.getId());
        nbtCompound.setInteger("sellPrice", drop.getSellPrice());

        return nbtItem.getItem();
    }

    private ItemStack createDropItem(DropItemSave drop, int amount) {
        ItemStack item = new ItemStack(Material.getMaterial(drop.getMaterial()), amount, (byte) drop.getData());
        ItemMeta itemMeta = item.getItemMeta();

        itemMeta.setDisplayName(VelocityFeatures.chat(drop.getDisplayName()));

        String lore = VelocityFeatures.chat(String.join("VDIB", drop.getLore()));

        itemMeta.setLore(Arrays.asList(lore.split("VDIB")));

        //itemMeta.spigot().setUnbreakable(true);
        item.setItemMeta(itemMeta);

        if (drop.isGlow()) item = ItemUtil.addGlow(item);

        return item;
    }

    private ItemStack createDropItem(DropItemSave drop, ItemStack item, int amount) {
        item.setAmount(amount);

        ItemMeta itemMeta = item.getItemMeta();

        itemMeta.setDisplayName(VelocityFeatures.chat(drop.getDisplayName()));

        String lore = VelocityFeatures.chat(String.join("VDIB", drop.getLore()));

        itemMeta.setLore(Arrays.asList(lore.split("VDIB")));

        //itemMeta.spigot().setUnbreakable(true);
        item.setItemMeta(itemMeta);

        if (drop.isGlow()) item = ItemUtil.addGlow(item);

        return item;
    }

    public DropItemSave getRandomDrop(TypeSave typeSave) {
        List<DropItemSave> dropSaves = getMiningDrops(typeSave.getDropIds());

        if (dropSaves.isEmpty()) return null;

        double totalChances = 0.0;

        for (DropItemSave drop : dropSaves) {
            totalChances += drop.getDropChance();
        }

        int index = 0;

        for (double r = Math.random() * totalChances; index < dropSaves.size() - 1; ++index) {
            r -= dropSaves.get(index).getDropChance();
            if (r <= 0.0) break;
        }

        return dropSaves.get(index);
    }

    public List<DropItemSave> getMiningDrops(List<String> dropIds) {
        List<DropItemSave> drops = MiningConfig.getInstance().itemDrops;
        List<DropItemSave> newList = new ArrayList<>();

        for (String drop : dropIds) {
            DropItemSave dropItemSave = drops.stream().filter(obj -> obj.getId().equalsIgnoreCase(drop)).findFirst().orElse(null);

            if (dropItemSave == null) continue;

            newList.add(dropItemSave);
        }

        return newList;
    }

    public void resetBlocks() {
        for (OreCache value : miningBlocks.values()) {
            Material toSet = Material.getMaterial(value.getTypeSave().getMaterial());
            Block block = value.getLocation().getBlock();

            block.setType(toSet);
        }
    }

    @Override
    public String getName() {
        return "mining";
    }

    @Override
    public boolean isEnabled() {
        return MiningConfig.getInstance().isEnabled();
    }

    @Override
    public void onEnable() {
        CommandAPI.getInstance().enableCommand(new MiningCommand());
        VelocityFeatures.registerEvent(new MiningListener());

        new MiningInterval();
        loadBlocks();
    }

    @Override
    public void onDisable() {
        CommandAPI.getInstance().disableCommand(MiningCommand.class);
        VelocityFeatures.unregisterEvent(MiningListener.getInstance());
        MiningInterval.getInstance().bukkitTask.cancel();

        miningBlocks.clear();
    }

    @Override
    public String placeholderRequest(Player player, String arg) {
        return "";
    }

}
