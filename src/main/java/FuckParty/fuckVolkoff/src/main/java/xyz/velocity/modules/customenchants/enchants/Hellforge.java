package FuckParty.fuckVolkoff.src.main.java.xyz.velocity.modules.customenchants.enchants;

import com.google.common.util.concurrent.AtomicDouble;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import xyz.velocity.modules.customenchants.EnumEnchantType;
import xyz.velocity.modules.customenchants.config.CustomEnchantConfig;
import xyz.velocity.modules.customenchants.config.saves.EnchantSave;
import xyz.velocity.modules.customenchants.CustomEnchants;
import xyz.velocity.modules.customenchants.EnchantManager;
import xyz.velocity.modules.customenchants.annotations.Enchant;
import xyz.velocity.modules.customenchants.events.EnchantProcEvent;
import xyz.velocity.modules.util.EnchantUtil;
import xyz.velocity.modules.util.Pair;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;

@Enchant
public class Hellforge extends AbstractEnchant {

    private final CustomEnchantConfig config;

    public Hellforge() {
        this.config = CustomEnchantConfig.getInstance();

        EnchantSave enchant = new EnchantSave(true, "Hellforge", "&cHellforge", Arrays.asList("&7Chance to regain some armor durability upon getting hit!"), "HELMET", "HELLFORGE:<level>", 3, 3, 50, 1, false, this.extraInfo());

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
        return "Hellforge";
    }

    @Override
    public boolean isVanillaEnchant() {
        return false;
    }

    @Override
    public String getEnchant() {
        return "HELLFORGE";
    }

    @Override
    public EnumEnchantType getEnchantType() {
        return EnumEnchantType.PVP;
    }

    @Override
    public <T extends Event> void runTask(T event) {
        EntityDamageByEntityEvent e = (EntityDamageByEntityEvent) event;

        Player violated = (Player) e.getEntity();

        double chance = EnchantUtil.getRandomDouble();

        Pair<Double, Integer> getInformation = getEnchantInformation(violated);

        if (getInformation == null) return;
        if (chance < getInformation.first) {
            EnchantProcEvent procEvent = CustomEnchants.getInstance().callEvent(violated, null, this);
            if (procEvent.isCancelled()) return;

            procEvent.activationMessage();

            ItemStack[] armor = violated.getEquipment().getArmorContents();

            for (ItemStack item : armor) {
                if (item.getType() == Material.SKULL_ITEM) continue;

                Integer toAdd = item.getDurability() - getInformation.second.shortValue();
                short maxDurability = item.getType().getMaxDurability();

                item.setDurability(toAdd.shortValue() > maxDurability ? maxDurability : toAdd.shortValue());
            }

        }

    }

    private JsonObject extraInfo() {
        JsonObject info = new JsonObject();

        info.addProperty("chancePerLevel", 7);
        info.addProperty("durabilityPerLevel", 5);

        info.toString();

        return info;
    }

    private Pair<Double, Integer> getEnchantInformation(Player player) {

        AtomicDouble chance = new AtomicDouble();
        AtomicInteger durability = new AtomicInteger();

        Object2ObjectMap<EnchantSave, Integer> getEnchants = CustomEnchants.getInstance().getEnchantsFromPlayer(player);

        getEnchants.forEach((enchant, level) -> {
            if (!enchant.getName().equals(this.getName())) return;

            JsonObject obj = new Gson().fromJson(enchant.getExtra().get(0), JsonObject.class);

            chance.set(obj.get("chancePerLevel").getAsDouble() * level);
            durability.set(obj.get("durabilityPerLevel").getAsInt() * level);
        });

        return new Pair<>(chance.get(), durability.get());
    }

}
