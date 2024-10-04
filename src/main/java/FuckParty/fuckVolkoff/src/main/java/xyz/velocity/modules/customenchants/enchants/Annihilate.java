package FuckParty.fuckVolkoff.src.main.java.xyz.velocity.modules.customenchants.enchants;

import com.google.common.collect.Maps;
import com.google.common.util.concurrent.AtomicDouble;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import net.minecraft.server.v1_8_R3.EntityPlayer;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import xyz.velocity.modules.customenchants.EnumEnchantType;
import xyz.velocity.modules.customenchants.config.CustomEnchantConfig;
import xyz.velocity.modules.customenchants.config.saves.EnchantSave;
import xyz.velocity.modules.customenchants.CustomEnchants;
import xyz.velocity.modules.customenchants.EnchantManager;
import xyz.velocity.modules.customenchants.annotations.Enchant;
import org.bukkit.event.Event;
import xyz.velocity.modules.customenchants.events.EnchantProcEvent;
import xyz.velocity.modules.util.EnchantUtil;
import xyz.velocity.modules.util.Pair;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;

@Enchant
public class Annihilate extends AbstractEnchant {

    private final CustomEnchantConfig config;

    public Annihilate() {
        this.config = CustomEnchantConfig.getInstance();

        EnchantSave enchant = new EnchantSave(true, "Annihilate", "&cAnnihilate", Arrays.asList("&7Deal more damage to your foes armor!"), "SWORD", "ANNIHILATE:<level>", 3, 5, 50, 1, false, this.extraInfo());

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
        return "Annihilate";
    }

    @Override
    public boolean isVanillaEnchant() {
        return false;
    }

    @Override
    public String getEnchant() {
        return "ANNIHILATE";
    }

    @Override
    public EnumEnchantType getEnchantType() {
        return EnumEnchantType.PVP;
    }

    @Override
    public <T extends Event> void runTask(T event) {
        EntityDamageByEntityEvent e = (EntityDamageByEntityEvent) event;

        Player violator = (Player) e.getDamager();
        Player violated = (Player) e.getEntity();

        Pair<Double, Integer> enchantInfo = getEnchantInformation(violator);

        double chance = EnchantUtil.getRandomDouble();

        if (chance < enchantInfo.first) {
            EnchantProcEvent procEvent = CustomEnchants.getInstance().callEvent(violator, violated, this);
            if (procEvent.isCancelled()) return;

            procEvent.activationMessage();

            ItemStack[] armor = violated.getInventory().getArmorContents();

            for (ItemStack item : armor) {
                if (item.getType() == Material.SKULL_ITEM) continue;
                if (!item.getType().name().startsWith("DIAMOND")) continue;

                Integer newDurability = item.getDurability() + enchantInfo.second;
                item.setDurability(newDurability.shortValue());
            }
        }

    }

    private JsonObject extraInfo() {
        JsonObject info = new JsonObject();

        info.addProperty("damagePerLevel", 2);
        info.addProperty("chancePerLevel", 4);

        info.toString();

        return info;
    }

    private Pair<Double, Integer> getEnchantInformation(Player player) {
        EntityPlayer entityPlayer = ((CraftPlayer) player).getHandle();

        AtomicDouble chance = new AtomicDouble(0);
        AtomicInteger damage = new AtomicInteger(0);

        Object2ObjectMap<EnchantSave, Integer> toReturn = CustomEnchants.getInstance().getEnchantsFromPlayer(player);

        toReturn.forEach((enchant, level) -> {
            if (!enchant.getName().equals(this.getName())) return;
            JsonObject obj = new Gson().fromJson(enchant.getExtra().get(0), JsonObject.class);

            chance.set(obj.get("chancePerLevel").getAsDouble() * level);
            damage.set(obj.get("damagePerLevel").getAsInt() * level);
        });

        return new Pair<>(chance.get(), damage.get());
    }

}
