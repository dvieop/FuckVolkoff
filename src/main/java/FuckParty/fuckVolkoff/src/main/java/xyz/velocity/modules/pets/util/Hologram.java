package FuckParty.fuckVolkoff.src.main.java.xyz.velocity.modules.pets.util;

import eu.decentsoftware.holograms.api.DHAPI;
import eu.decentsoftware.holograms.api.holograms.HologramLine;
import eu.decentsoftware.holograms.api.utils.items.HologramItem;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.inventory.ItemStack;

public class Hologram {

    private static Hologram instance;

    public static Hologram getInstance() {
        return instance;
    }

    public eu.decentsoftware.holograms.api.holograms.Hologram hologram = null;

    public HologramLine line;
    public HologramLine itemLine;
    public World world;

    Location location;

    public Hologram(Location location) {
        this.location = location;
        this.world = location.getWorld();

        instance = this;
        spawnHologram();
    }

    public void spawnHologram() {
        hologram = DHAPI.createHologram(String.valueOf(hashCode()), this.location);
    }

    public void updateName(String name) {
        if (line == null)
            line = DHAPI.insertHologramLine(hologram, 0, 0, name);

        line.setText(name);
    }

    public void updateItem(ItemStack item) {
        if (itemLine == null)
            itemLine = DHAPI.insertHologramLine(hologram, 0, 1, item);

        itemLine.setItem(HologramItem.fromItemStack(item));
    }

    public void updateLocation(Location location) {
        DHAPI.moveHologram(hologram, location);
    }

    public void deleteHologram() {
        this.hologram.delete();
    }

    public void recreateHologram(Location location, String name) {
        this.deleteHologram();
        this.itemLine = null;
        this.line = null;

        hologram = DHAPI.createHologram(String.valueOf(hashCode()), location);

        this.updateName(name);
    }

}
