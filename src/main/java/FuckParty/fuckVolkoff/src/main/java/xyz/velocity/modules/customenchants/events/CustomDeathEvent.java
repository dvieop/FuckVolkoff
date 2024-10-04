package FuckParty.fuckVolkoff.src.main.java.xyz.velocity.modules.customenchants.events;

import de.tr7zw.nbtapi.NBTCompound;
import de.tr7zw.nbtapi.NBTItem;
import it.unimi.dsi.fastutil.objects.*;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.inventory.ItemStack;
import xyz.velocity.modules.armorsets.ArmorSets;
import xyz.velocity.modules.customenchants.enchants.util.DeathWrapper;
import xyz.velocity.modules.masks.Masks;
import xyz.velocity.modules.pets.Pets;
import xyz.velocity.modules.util.EnchantUtil;

import java.util.List;
import java.util.UUID;

public class CustomDeathEvent extends PlayerEvent implements Cancellable {

    public static Object2ObjectOpenHashMap<UUID, DeathWrapper> itemsList = new Object2ObjectOpenHashMap<>();

    private Player killer;
    private PlayerDeathEvent deathEvent;
    private static final HandlerList handlers = new HandlerList();
    private boolean cancel = false;

    public CustomDeathEvent(Player activator, PlayerDeathEvent deathEvent) {
        super(activator);
        this.killer = deathEvent.getEntity().getKiller();
        this.deathEvent = deathEvent;
    }

    public void loadNegatedItems() {
        ObjectList<ItemStack> protectedItems = getProtectedItems(deathEvent.getDrops());
        ObjectList<Integer> negateChances = getNegationChances(killer);

        for (ObjectIterator<ItemStack> itemIterator = protectedItems.iterator(); itemIterator.hasNext(); ) {
            itemIterator.next();

            for (Integer negateChance : negateChances) {
                double chance = EnchantUtil.getRandomDouble();

                if (chance <= negateChance) {
                    itemIterator.remove();
                    break;
                }
            }
        }

        if (!protectedItems.isEmpty()) itemsList.put(deathEvent.getEntity().getUniqueId(), new DeathWrapper(protectedItems, System.currentTimeMillis() + 5000));
    }

    public ObjectArrayList<ItemStack> getProtectedItems(List<ItemStack> inventory) {
        ObjectArrayList<ItemStack> items = new ObjectArrayList<>();

        for (ItemStack item : inventory) {
            if (item == null) continue;
            if (isApplicable(item)) {
                NBTItem nbtItem = new NBTItem(item);
                NBTCompound compound = nbtItem.getCompound("velocity_enchantItem_holyWhiteScroll");

                if (compound == null) continue;
                if (compound.getBoolean("deathProtection")) items.add(item);
            }
        }

        return items;
    }

    private ObjectList<Integer> getNegationChances(Player killer) {
        ObjectList<Integer> list = new ObjectArrayList<>();

        if (ArmorSets.getInstance().isEnabled()) {
            list.add(ArmorSets.getInstance().getArmorNegateChance(killer));
            list.add(ArmorSets.getInstance().getWeaponNegateChance(killer));
        }

        if (Masks.getInstance().isEnabled()) {
            list.add(Masks.getInstance().getMaskNegateChance(killer));
        }

        if (Pets.getInstance().isEnabled()) {
            list.add(Pets.getInstance().getPetNegationChance(killer));
        }

        return list;
    }

    private boolean isApplicable(ItemStack itemStack) {
        String name = itemStack.getType().name();

        if (name.endsWith("_HELMET")) return true;
        if (name.endsWith("_CHESTPLATE")) return true;
        if (name.endsWith("_LEGGINGS")) return true;
        if (name.endsWith("_BOOTS")) return true;
        if (name.endsWith("_SWORD")) return true;
        if (name.endsWith("_AXE")) return true;
        if (name.endsWith("_PICKAXE")) return true;
        if (name.endsWith("_HOE")) return true;

        return false;
    }

    @Override
    public boolean isCancelled() {
        return this.cancel;
    }

    @Override
    public void setCancelled(boolean b) {
        this.cancel = b;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

}
