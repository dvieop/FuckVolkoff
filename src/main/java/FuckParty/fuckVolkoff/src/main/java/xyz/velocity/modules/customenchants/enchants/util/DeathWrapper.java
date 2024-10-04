package FuckParty.fuckVolkoff.src.main.java.xyz.velocity.modules.customenchants.enchants.util;

import org.bukkit.inventory.ItemStack;

import java.util.List;

public class DeathWrapper {

    List<ItemStack> items;
    long timeout;

    public DeathWrapper(List<ItemStack> items, long timeout) {
        this.items = items;
        this.timeout = timeout;
    }

    public List<ItemStack> getItems() {
        return items;
    }

    public long getTimeout() {
        return timeout;
    }

    public void setItems(List<ItemStack> items) {
        this.items = items;
    }

}
