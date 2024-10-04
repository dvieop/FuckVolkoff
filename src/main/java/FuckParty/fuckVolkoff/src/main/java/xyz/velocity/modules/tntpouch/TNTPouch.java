package FuckParty.fuckVolkoff.src.main.java.xyz.velocity.modules.tntpouch;

import de.tr7zw.nbtapi.NBTCompound;
import de.tr7zw.nbtapi.NBTItem;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import xyz.velocity.VelocityFeatures;
import xyz.velocity.commands.CommandAPI;
import xyz.velocity.modules.AbstractModule;
import xyz.velocity.modules.Module;
import xyz.velocity.modules.tntpouch.commands.PouchCommand;
import xyz.velocity.modules.tntpouch.config.PouchConfig;
import xyz.velocity.modules.tntpouch.config.saves.PouchSave;
import xyz.velocity.modules.util.ItemUtil;

import java.util.Arrays;

@Module
public class TNTPouch extends AbstractModule {

    public TNTPouch() {
        instance = this;
    }

    public void createTNTPouch(Player player, int tier) {
        PouchSave pouch = PouchConfig.getInstance().getPouches()
                .stream()
                .filter(obj -> obj.getTier() == tier)
                .findFirst()
                .orElse(null);

        if (pouch == null) return;

        ItemStack item = new ItemStack(Material.getMaterial(pouch.getItem().getMaterial()), 1);
        ItemMeta itemMeta = item.getItemMeta();

        String lore = VelocityFeatures.chat(String.join("VDIB", pouch.getItem().getLore())
                        .replace("<storage>", String.format("%,d", pouch.getMaxStorage()))
                        .replace("<maxStorage>", String.format("%,d", pouch.getMaxStorage()))
        );

        itemMeta.setDisplayName(VelocityFeatures.chat(pouch.getItem().getDisplayName()));
        itemMeta.setLore(Arrays.asList(lore.split("VDIB")));

        item.setItemMeta(itemMeta);

        NBTItem nbtItem = new NBTItem(item);
        NBTCompound compound = nbtItem.addCompound("velocity_tntpouch_item");

        compound.setInteger("tier", tier);
        compound.setInteger("max", pouch.getMaxStorage());
        compound.setInteger("storage", pouch.getMaxStorage());

        player.getInventory().addItem(nbtItem.getItem());
    }

    public boolean isTNTPouch(ItemStack item) {
        NBTItem nbtItem = new NBTItem(item);

        return nbtItem.getCompound("velocity_tntpouch_item") != null;
    }

    public void withdrawPouch(Player player, int amount) {
        if (ItemUtil.isAirOrNull(player.getItemInHand())) return;

        ItemStack item = player.getItemInHand();

        if (!isTNTPouch(item)) return;

        NBTItem nbtItem = new NBTItem(item);
        NBTCompound compound = nbtItem.getCompound("velocity_tntpouch_item");

        int storage = compound.getInteger("storage");
        int emptySlots = getEmptySlots(player);
        int toWithdraw = amount;
        int maxInventory = emptySlots * 64;

        if (storage <= 0) {
            player.sendMessage(VelocityFeatures.chat(PouchConfig.getInstance().getEmpty()));
            return;
        }
        if (amount > storage) {
            toWithdraw = storage;
        }
        if (toWithdraw > maxInventory) {
            toWithdraw = maxInventory;
        }

        giveTNT(player, emptySlots, toWithdraw);
        updateItemLore(player, item, storage - toWithdraw, compound.getInteger("tier"));
    }

    private void updateItemLore(Player player, ItemStack item, int storage, int tier) {
        PouchSave pouch = PouchConfig.getInstance().getPouches()
                .stream()
                .filter(obj -> obj.getTier() == tier)
                .findFirst()
                .orElse(null);

        if (pouch == null) return;

        ItemMeta itemMeta = item.getItemMeta();

        String lore = VelocityFeatures.chat(String.join("VDIB", pouch.getItem().getLore())
                .replace("<storage>", String.format("%,d", storage))
                .replace("<maxStorage>", String.format("%,d", pouch.getMaxStorage()))
        );

        itemMeta.setLore(Arrays.asList(lore.split("VDIB")));

        item.setItemMeta(itemMeta);

        NBTItem nbtItem = new NBTItem(item);
        NBTCompound compound = nbtItem.getCompound("velocity_tntpouch_item");

        compound.setInteger("storage", storage);

        player.setItemInHand(nbtItem.getItem());
    }

    private void giveTNT(Player player, int emptySlots, int toWithdraw) {
        for (int i = 0; i < emptySlots; i++) {
            int give = toWithdraw;

            if (toWithdraw > 64) give = 64;
            if (toWithdraw <= 0) break;

            player.getInventory().addItem(new ItemStack(Material.TNT, give));
            toWithdraw -= 64;
        }

        player.sendMessage(VelocityFeatures.chat(PouchConfig.getInstance().getWithdraw()
                .replace("<amount>", String.format("%,d", toWithdraw))
        ));
    }

    private int getEmptySlots(Player player) {
        int slots = 0;

        ItemStack[] items = player.getInventory().getContents();

        for (ItemStack item : items) {
            if (item == null)
                slots++;
        }

        return slots;
    }

    public void fillPouch(Player player, int amount) {
        if (ItemUtil.hasNoItemMeta(player.getItemInHand())) return;

        ItemStack item = player.getItemInHand();

        if (!isTNTPouch(item));

        NBTItem nbtItem = new NBTItem(item);
        NBTCompound compound = nbtItem.getCompound("velocity_tntpouch_item");

        compound.setInteger("storage", amount);
        updateItemLore(player, item, amount, compound.getInteger("tier"));
    }

    @Getter
    private static TNTPouch instance;

    @Override
    public String getName() {
        return "tntpouch";
    }

    @Override
    public boolean isEnabled() {
        return PouchConfig.getInstance().isEnabled();
    }

    @Override
    public void onEnable() {
        CommandAPI.getInstance().enableCommand(new PouchCommand());
        VelocityFeatures.registerEvent(new PouchListener());

        PouchConfig.getInstance().setEnabled(true);
    }

    @Override
    public void onDisable() {
        CommandAPI.getInstance().disableCommand(PouchCommand.class);
        VelocityFeatures.registerEvent(new PouchListener());

        PouchConfig.getInstance().setEnabled(false);
    }

    @Override
    public String placeholderRequest(Player player, String arg) {
        return "";
    }

}
