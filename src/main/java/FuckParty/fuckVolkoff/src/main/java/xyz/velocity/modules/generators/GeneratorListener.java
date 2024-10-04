package FuckParty.fuckVolkoff.src.main.java.xyz.velocity.modules.generators;

import com.earth2me.essentials.Essentials;
import com.golfing8.kore.FactionsKore;
import de.tr7zw.nbtapi.NBTCompound;
import de.tr7zw.nbtapi.NBTItem;
import lombok.Getter;
import net.minecraft.server.v1_8_R3.TileEntityMobSpawner;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import xyz.velocity.VelocityFeatures;
import xyz.velocity.modules.generators.config.GeneratorConfig;
import xyz.velocity.modules.generators.config.StorageConfig;
import xyz.velocity.modules.generators.config.saves.GenDataSave;
import xyz.velocity.modules.generators.config.saves.GeneratorSave;
import xyz.velocity.modules.generators.config.saves.InventorySave;
import xyz.velocity.modules.util.ItemUtil;
import xyz.velocity.modules.util.Location;

import java.util.UUID;

public class GeneratorListener implements Listener {

    @EventHandler
    public void generatorPlace(BlockPlaceEvent e) {
        if (e.isCancelled()) return;
        if (!(e.getBlockPlaced().getType() == Material.SKULL)) return;

        ItemStack generator = e.getItemInHand();

        if (generator == null) return;

        NBTItem nbtItem = new NBTItem(generator);
        NBTCompound nbtCompound = nbtItem.getCompound("velocity_generator_item");

        if (nbtCompound == null) return;

        org.bukkit.Location placedLocation = e.getBlockPlaced().getLocation();

        String factionID = FactionsKore.getIntegration().getFactionsIdAt(placedLocation);
        String factionTag = FactionsKore.getIntegration().getTagFromId(factionID);

        if (factionTag.contains("Wilderness")) {
            e.setCancelled(true);
            return;
        }
        if (StorageConfig.getInstance().hasFaction(factionID) && StorageConfig.getInstance().generators.get(factionID).size() > GeneratorConfig.getInstance().getFactionLimit()) {
            e.getPlayer().sendMessage(VelocityFeatures.chat(GeneratorConfig.getInstance().getLimitMessage()));
            e.setCancelled(true);
            return;
        }

        String type = nbtCompound.getString("type");

        int tier = nbtCompound.getInteger("tier");
        int capacity = nbtCompound.getInteger("capacity");
        int storage = nbtCompound.getInteger("storage");

        GenDataSave genDataSave = new GenDataSave(type, Location.parseToString(placedLocation), tier, capacity, storage);
        GenCache genCache = new GenCache(genDataSave);

        Hologram hologram = new Hologram(VelocityFeatures.getInstance(), e.getBlockPlaced().getLocation().add(0.5, 4, 0.5), genCache);

        genCache.setHologram(hologram);

        Generator.placedGenerators.add(genCache);
        StorageConfig.getInstance().addToData(factionID, genDataSave);

        e.getPlayer().sendMessage(VelocityFeatures.chat(GeneratorConfig.getInstance().getPlaceMessage()));
    }

    @EventHandler
    public void generatorBreak(BlockBreakEvent e) {
        if (e.isCancelled()) return;
        if (!(e.getBlock().getType() == Material.SKULL)) return;
        if (!Generator.getInstance().isGeneratorBlock(e.getBlock().getLocation())) return;

        org.bukkit.Location blockLocation = e.getBlock().getLocation();

        GenDataSave genDataSave = Generator.getInstance().getGenerator(blockLocation);

        if (genDataSave == null) return;

        Generator.getInstance().generatorBreak(genDataSave, blockLocation);

        e.getBlock().setType(Material.AIR);
        e.setCancelled(true);
        e.getPlayer().sendMessage(VelocityFeatures.chat(GeneratorConfig.getInstance().getBreakMessage()));
    }

    @EventHandler
    public void onGeneratorClick(PlayerInteractEvent e) {
        Player player = e.getPlayer();
        Block block = e.getClickedBlock();

        if (!(e.getAction().equals(Action.RIGHT_CLICK_BLOCK))) return;
        if (!block.getType().equals(Material.SKULL)) return;

        org.bukkit.Location blockLocation = block.getLocation();

        if (!Generator.getInstance().isGeneratorBlock(blockLocation)) return;

        String playerFactionId = FactionsKore.getIntegration().getPlayerFactionId(player);
        String generatorFactionId = FactionsKore.getIntegration().getFactionsIdAt(blockLocation);

        if (!playerFactionId.equalsIgnoreCase(generatorFactionId)) return;

        Generator.getInstance().addInvContents(player, blockLocation);
        TileEntityMobSpawner mobSpawner = (TileEntityMobSpawner) e.getClickedBlock();
    }

    @EventHandler
    public void upgradeEvents(InventoryClickEvent e) {
        Inventory inv = e.getInventory();
        Player p = (Player) e.getWhoClicked();
        UUID id = p.getUniqueId();

        GeneratorConfig config = GeneratorConfig.getInstance();
        InventorySave inventorySave = GeneratorConfig.getInstance().getInventory();

        if (!inv.getName().equals(VelocityFeatures.chat(inventorySave.getGuiName()))) return;

        e.setCancelled(true);

        if (ItemUtil.isAirOrNull(e.getCurrentItem())) return;

        ItemStack mainItem = e.getClickedInventory().getItem(inventorySave.getMainItem().getSlot());

        NBTItem nbtItem = new NBTItem(mainItem);
        NBTCompound nbtCompound = nbtItem.getCompound("velocity_generator_item");

        if (nbtCompound == null) return;

        String type = nbtCompound.getString("type");
        String location = nbtCompound.getString("location");
        int tier = nbtCompound.getInteger("tier");
        int capacity = nbtCompound.getInteger("capacity");

        GeneratorSave generatorSave = GeneratorConfig.getInstance().getGenerators()
                .stream()
                .filter(obj -> obj.getId().equalsIgnoreCase(type))
                .findFirst()
                .orElse(null);

        if (generatorSave == null) return;
        if (e.getSlot() == inventorySave.getTierItem().getSlot()) {
            Generator.getInstance().handleTierUpgrade(p, e.getInventory(), generatorSave, tier, location);
        }
        if (e.getSlot() == inventorySave.getStorageItem().getSlot()) {
            Generator.getInstance().handleStorageUpgrade(p, e.getInventory(), generatorSave, capacity, location);
        }
        if (e.getSlot() == inventorySave.getMainItem().getSlot()) {
            Generator.getInstance().handleSelling(p, e.getInventory(), generatorSave, capacity, location);
        }
    }

    @EventHandler
    public void generatorExplode(BlockExplodeEvent e) {
        if (e.isCancelled()) return;
        if (!(e.getBlock().getType() == Material.SKULL)) return;
        if (!Generator.getInstance().isGeneratorBlock(e.getBlock().getLocation())) return;

        org.bukkit.Location blockLocation = e.getBlock().getLocation();

        GenDataSave genDataSave = Generator.getInstance().getGenerator(blockLocation);

        if (genDataSave == null) return;

        Generator.getInstance().generatorBreak(genDataSave, blockLocation);
    }

    public GeneratorListener() {
        instance = this;
    }

    @Getter
    private static GeneratorListener instance;

}
