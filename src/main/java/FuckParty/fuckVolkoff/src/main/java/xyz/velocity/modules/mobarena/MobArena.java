package FuckParty.fuckVolkoff.src.main.java.xyz.velocity.modules.mobarena;

import lombok.Getter;
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
import xyz.velocity.modules.mobarena.commands.MobArenaCommand;
import xyz.velocity.modules.mobarena.config.MobArenaConfig;
import xyz.velocity.modules.mobarena.config.saves.InventorySave;
import xyz.velocity.modules.pets.config.saves.InventoryItemSave;
import xyz.velocity.modules.util.ItemUtil;

import java.util.Arrays;

@Module
public class MobArena extends AbstractModule {

    public MobArena() {
        instance = this;
    }

    @Getter
    private static MobArena instance;

    public void addInvContents(Player player) {
        InventorySave inventorySave = MobArenaConfig.getInstance().getInventory();
        Inventory inv = Bukkit.createInventory(null, inventorySave.getSize(), VelocityFeatures.chat(inventorySave.getGuiName()));

        for (InventoryItemSave classItem : inventorySave.getClassItems()) {
            setSpecialSlot(inv, classItem);
        }

        addFillerContent(inv, inventorySave);

        player.openInventory(inv);
    }

    public void setSpecialSlot(Inventory inv, InventoryItemSave iis) {
        int slot = iis.getSlot();

        ItemStack item = new ItemStack(Material.getMaterial(iis.getMaterial()), 1, (byte) iis.getData());
        ItemMeta meta = item.getItemMeta();

        String lore = VelocityFeatures.chat(String.join("VDIB", iis.getLore()));

        meta.setDisplayName(VelocityFeatures.chat(iis.getDisplayName()));
        meta.setLore(Arrays.asList(lore.split("VDIB")));

        item.setItemMeta(meta);

        inv.setItem(slot, item);
    }

    public void addFillerContent(Inventory inventory, InventorySave inventorySave) {
        for (int i = 0; i < inventory.getSize(); i++) {
            if (!ItemUtil.hasNoItemMeta(inventory.getItem(i))) continue;

            ItemStack itemStack = new ItemStack(Material.getMaterial(inventorySave.getFiller().getMaterial()), 1, (byte) inventorySave.getFiller().getData());
            ItemMeta meta = itemStack.getItemMeta();

            meta.setDisplayName(VelocityFeatures.chat("&7 "));
            itemStack.setItemMeta(meta);

            inventory.setItem(i, itemStack);
        }
    }

    @Override
    public String getName() {
        return "mobarena";
    }

    @Override
    public boolean isEnabled() {
        return MobArenaConfig.getInstance().isEnabled();
    }

    @Override
    public void onEnable() {
        //CommandAPI.getInstance().enableCommand(new MobArenaCommand());
        MobArenaConfig.getInstance().setEnabled(true);
    }

    @Override
    public void onDisable() {

        MobArenaConfig.getInstance().setEnabled(false);
    }

    @Override
    public String placeholderRequest(Player player, String arg) {
        return "";
    }

}
