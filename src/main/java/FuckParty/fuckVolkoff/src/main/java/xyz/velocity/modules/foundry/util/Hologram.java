package FuckParty.fuckVolkoff.src.main.java.xyz.velocity.modules.foundry.util;

import eu.decentsoftware.holograms.api.DHAPI;
import eu.decentsoftware.holograms.api.holograms.HologramLine;
import eu.decentsoftware.holograms.api.utils.items.HologramItem;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import xyz.velocity.VelocityFeatures;
import xyz.velocity.modules.foundry.config.FoundryConfig;

public class Hologram {

    private static Hologram instance;

    public static Hologram getInstance() {
        return instance;
    }

    eu.decentsoftware.holograms.api.holograms.Hologram hologram = null;
    HologramLine line;

    org.bukkit.Location location;
    Plugin plugin;

    public Hologram(Plugin plugin, org.bukkit.Location location) {
        this.plugin = plugin;
        this.location = location;

        instance = this;
    }

    public void spawnHologram() {

        FoundryConfig config = FoundryConfig.getInstance();

        hologram = DHAPI.createHologram(String.valueOf(hashCode()), this.location);

        DHAPI.addHologramLine(hologram, VelocityFeatures.chat(config.getHologramTitle()));

        this.line = DHAPI.addHologramLine(hologram, VelocityFeatures.chat(config.getHologramSub().replace("<time>", "None")));

        HologramLine item = DHAPI.addHologramLine(hologram, new ItemStack(Material.DIAMOND_SWORD));

    }

    public void updateTime(int n) {
        FoundryConfig config = FoundryConfig.getInstance();

        String s;

        if (n == 0) {
            s = "None";
        } else {
            int minutes = (n % 3600) / 60;
            n = n % 60;

            s = minutes + "m " + n + "s";
        }

        this.line.setText(VelocityFeatures.chat(config.getHologramSub().replace("<time>", s)));
    }

    public void addItem(ItemStack itemStack) {
        this.hologram.getPage(0).getLine(2).setItem(HologramItem.fromItemStack(itemStack));
    }

    public void deleteHologram() {
        this.hologram.delete();
    }

}
