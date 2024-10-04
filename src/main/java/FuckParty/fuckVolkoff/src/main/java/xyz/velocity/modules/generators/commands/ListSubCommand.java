package FuckParty.fuckVolkoff.src.main.java.xyz.velocity.modules.generators.commands;

import com.golfing8.kore.FactionsKore;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import xyz.velocity.VelocityFeatures;
import xyz.velocity.commands.ChildCommand;
import xyz.velocity.modules.generators.Generator;
import xyz.velocity.modules.generators.config.GeneratorConfig;
import xyz.velocity.modules.generators.config.StorageConfig;
import xyz.velocity.modules.generators.config.saves.GenDataSave;
import xyz.velocity.modules.util.Location;

import java.util.Arrays;
import java.util.List;

public class ListSubCommand extends ChildCommand {

    @Override
    protected boolean noChildrenExecute(CommandSender commandSender, String string, String[] args) {
        Player player = (Player) commandSender;

        if (!FactionsKore.getIntegration().hasFaction(player)) return false;

        String factionId = FactionsKore.getIntegration().getPlayerFactionId(player);

        if (!StorageConfig.getInstance().hasFaction(factionId)) return false;

        Inventory inv = Bukkit.createInventory(null, 9, VelocityFeatures.chat("&8Generator List"));

        int slot = 0;

        for (GenDataSave gen : StorageConfig.getInstance().generators.get(factionId)) {
            ItemStack item = new ItemStack(Material.CACTUS, 1, (byte) 0);

            ItemMeta meta = item.getItemMeta();

            int capacity = gen.getCapacity();

            String lore = VelocityFeatures.chat(String.join("VDIB", GeneratorConfig.getInstance().getPreviewLore()));

            lore = lore
                    .replace("<tier>", gen.getTier() + "")
                    .replace("<storage>", Generator.getInstance().formatNumber(gen.getStorage()))
                    .replace("<capacity>", Generator.getInstance().formatNumber(capacity))
                    .replace("<location>", VelocityFeatures.chat(Location.locationToFancyString(Location.parseToLocation(gen.getLocation()), "&a", "&f")));

            meta.setDisplayName(VelocityFeatures.chat("&a&lGenerator #" + (slot + 1)));
            meta.setLore(Arrays.asList(lore.split("VDIB")));

            item.setItemMeta(meta);

            inv.setItem(slot, item);

            slot++;
        }

        player.openInventory(inv);
        return false;
    }

    @Override
    public String getName() {
        return "list";
    }

    @Override
    public List<String> getAliases() {
        return Arrays.asList("list");
    }

}
