package FuckParty.fuckVolkoff.src.main.java.xyz.velocity.modules.mining;

import com.earth2me.essentials.Essentials;
import de.tr7zw.nbtapi.NBTCompound;
import de.tr7zw.nbtapi.NBTItem;
import lombok.Getter;
import net.ess3.api.MaxMoneyException;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;
import xyz.velocity.VelocityFeatures;
import xyz.velocity.modules.fishing.config.saves.ItemMultiplierSave;
import xyz.velocity.modules.mining.config.MiningConfig;
import xyz.velocity.modules.mining.config.saves.TypeSave;
import xyz.velocity.modules.safari.config.saves.DropItemSave;
import xyz.velocity.modules.util.EnchantUtil;
import xyz.velocity.modules.util.ItemUtil;

import java.math.BigDecimal;

public class MiningListener implements Listener {

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void blockBreak(BlockBreakEvent e) {
        Player player = e.getPlayer();
        Block block = e.getBlock();
        Location location = block.getLocation();

        if (!Mining.miningBlocks.containsKey(location)) return;

        OreCache oreCache = Mining.miningBlocks.get(location);
        TypeSave type = oreCache.typeSave;

        if (oreCache.isOnCooldown()) return;

        DropItemSave drop = Mining.getInstance().getRandomDrop(type);
        ItemMultiplierSave multiplier = drop.getMultiplier();

        int amount = drop.getAmountToGive();

        if (multiplier.isEnabled() && EnchantUtil.getRandomDouble() < multiplier.getChance()) {
            amount += (int) EnchantUtil.getRandomDouble(1, multiplier.getAmount());
        }

        ItemStack item = Mining.getInstance().getDropItem(drop, amount);

        player.getInventory().addItem(item);
        player.sendMessage(VelocityFeatures.chat(MiningConfig.getInstance().rewardMessage
                .replace("<amount>", amount + "")
                .replace("<reward>", drop.getDisplayName())
                .replace("<block>", type.getChatName())
        ));

        e.setCancelled(true);

        oreCache.setCooldown(System.currentTimeMillis() + (type.getCooldown() * 1000L));
        block.setType(Material.BEDROCK);
    }

    @EventHandler
    public void invClose(InventoryCloseEvent event) {
        String guiName = event.getInventory().getName();
        String configGuiName = VelocityFeatures.chat(MiningConfig.getInstance().getGuiName());

        if (!configGuiName.equalsIgnoreCase(guiName)) return;
        int money = 0;

        for (ItemStack content : event.getInventory().getContents()) {
            if (ItemUtil.hasNoItemMeta(content)) continue;

            NBTItem item = new NBTItem(content);
            NBTCompound compound = item.getCompound("velocity_mining_drop");

            money += compound.getInteger("sellPrice") * content.getAmount();
        }

        if (!(money > 0)) return;

        try {
            Essentials.getPlugin(Essentials.class).getUser(event.getPlayer()).giveMoney(BigDecimal.valueOf(money));
        } catch (MaxMoneyException err) {
            return;
        }
    }

    @EventHandler
    public void putItems(InventoryClickEvent event) {
        String guiName = event.getInventory().getName();
        String configGuiName = VelocityFeatures.chat(MiningConfig.getInstance().guiName);

        if (!guiName.equalsIgnoreCase(configGuiName)) return;

        NBTItem item = new NBTItem(event.getCurrentItem());
        NBTCompound compound = item.getCompound("velocity_mining_drop");

        if (compound == null) {
            event.setCancelled(true);
        }
    }

    @Getter
    private static MiningListener instance;

    public MiningListener() {
        instance = this;
    }

}
