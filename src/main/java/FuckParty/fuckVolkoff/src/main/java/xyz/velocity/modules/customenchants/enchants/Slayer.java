package FuckParty.fuckVolkoff.src.main.java.xyz.velocity.modules.customenchants.enchants;

import com.google.common.util.concurrent.AtomicDouble;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import net.minecraft.server.v1_8_R3.EntityPlayer;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import xyz.velocity.modules.customenchants.EnumEnchantType;
import xyz.velocity.modules.customenchants.config.CustomEnchantConfig;
import xyz.velocity.modules.customenchants.config.saves.EnchantSave;
import xyz.velocity.modules.customenchants.CustomEnchants;
import xyz.velocity.modules.customenchants.EnchantItems;
import xyz.velocity.modules.customenchants.EnchantManager;
import xyz.velocity.modules.customenchants.annotations.Enchant;
import xyz.velocity.modules.customenchants.enchants.util.DeathWrapper;
import xyz.velocity.modules.customenchants.events.CustomDeathEvent;
import xyz.velocity.modules.customenchants.events.EnchantProcEvent;
import xyz.velocity.modules.util.EnchantUtil;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

@Enchant
public class Slayer extends AbstractEnchant {

    private final CustomEnchantConfig config;

    public Slayer() {
        this.config = CustomEnchantConfig.getInstance();

        EnchantSave enchant = new EnchantSave(true, "Slayer", "&cSlayer", Arrays.asList("&715% chance to negate holy white scroll!"), "SWORD", "SLAYER:<level>", 3, 3, 20, 1, false, this.extraInfo());

        if (!config.getEnchantList().stream().anyMatch(obj -> obj.getName().equals(enchant.getName()))) {
            config.getEnchantList().add(enchant);
        }

        EnchantManager.nonVanillaEnchants.put(this.getName(), this);
    }

    @Override
    public boolean isEnabled() {
        return CustomEnchantConfig.getInstance().getEnchantList().stream().filter(obj -> obj.getName().equals(this.getName())).findFirst().get().isEnabled();
    }

    @Override
    public String getName() {
        return "Slayer";
    }

    @Override
    public boolean isVanillaEnchant() {
        return false;
    }

    @Override
    public String getEnchant() {
        return "SLAYER";
    }

    @Override
    public EnumEnchantType getEnchantType() {
        return EnumEnchantType.OTHER;
    }

    @Override
    public <T extends Event> void runTask(T event) {


        if (event instanceof PlayerDeathEvent) {
            deathEvent((PlayerDeathEvent) event);
        }
    }

    private void deathEvent(PlayerDeathEvent e) {
        try {
            if (e.getEntity().getKiller() == null || !(e.getEntity().getKiller() instanceof Player)) return;

            Player killer = e.getEntity().getKiller();
            Player bummed = e.getEntity();

            DeathWrapper deathWrapper = CustomDeathEvent.itemsList.get(bummed.getUniqueId());
            double chance = getEnchantInformation(killer);

            if (chance == 0 || deathWrapper == null) return;

            List<ItemStack> getItems = deathWrapper.getItems();

            for (Iterator<ItemStack> iterator = getItems.iterator(); iterator.hasNext();) {
                iterator.next();
                double newChance = EnchantUtil.getRandomDouble();

                if (newChance < chance) {
                    iterator.remove();
                }
            }

            if (deathWrapper.getItems().size() > 1) {
                EnchantProcEvent procEvent = CustomEnchants.getInstance().callEvent(killer, bummed, this);
                if (procEvent.isCancelled()) return;

                deathWrapper.setItems(getItems);
            }
        } catch (Throwable err) {
            err.printStackTrace();
        }
    }

    private JsonObject extraInfo() {
        JsonObject info = new JsonObject();

        info.addProperty("chancePerLevel", 2);

        info.toString();

        return info;
    }

    private double getEnchantInformation(Player player) {
        EntityPlayer entityPlayer = ((CraftPlayer) player).getHandle();

        AtomicDouble chance = new AtomicDouble(0);

        Map<EnchantSave, Integer> toReturn = CustomEnchants.getInstance().getEnchantsFromPlayer(player);

        toReturn.forEach((enchant, level) -> {
            if (!enchant.getName().equals(this.getName())) return;
            JsonObject obj = new Gson().fromJson(enchant.getExtra().get(0), JsonObject.class);

            chance.set(obj.get("chancePerLevel").getAsDouble() * level);
        });

        return chance.get();
    }

}
