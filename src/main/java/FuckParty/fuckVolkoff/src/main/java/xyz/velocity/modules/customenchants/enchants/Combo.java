package FuckParty.fuckVolkoff.src.main.java.xyz.velocity.modules.customenchants.enchants;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.server.v1_8_R3.EntityPlayer;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import xyz.velocity.modules.customenchants.EnumEnchantType;
import xyz.velocity.modules.customenchants.config.CustomEnchantConfig;
import xyz.velocity.modules.customenchants.config.saves.EnchantSave;
import xyz.velocity.modules.customenchants.CustomEnchants;
import xyz.velocity.modules.customenchants.EnchantManager;
import xyz.velocity.modules.customenchants.annotations.Enchant;
import xyz.velocity.modules.customenchants.events.EnchantProcEvent;
import xyz.velocity.modules.util.Pair;

import java.util.Arrays;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

@Enchant
public class Combo extends AbstractEnchant {

    public static Object2ObjectOpenHashMap<UUID, Integer> comboList = new Object2ObjectOpenHashMap<>();
    private final CustomEnchantConfig config;

    public Combo() {
        this.config = CustomEnchantConfig.getInstance();

        EnchantSave enchant = new EnchantSave(true, "Combo", "&cCombo", Arrays.asList("&7Do more damage as long as you combo"), "SWORD", "COMBO:<level>", 3, 3, 20, 1, false, this.extraInfo());

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
        return "Combo";
    }

    @Override
    public boolean isVanillaEnchant() {
        return false;
    }

    @Override
    public String getEnchant() {
        return "COMBO";
    }

    @Override
    public EnumEnchantType getEnchantType() {
        return EnumEnchantType.PVP;
    }

    @Override
    public <T extends Event> void runTask(T event) {
        EntityDamageByEntityEvent e = (EntityDamageByEntityEvent) event;

        Player damager = (Player) e.getDamager();
        Player violated = (Player) e.getEntity();

        EnchantProcEvent procEvent = CustomEnchants.getInstance().callEvent(damager, violated, this);
        if (procEvent.isCancelled()) return;

        procEvent.activationMessage();

        checkAndPut(damager);
        checkAndPut(violated);

        resetCombo(violated);

        UUID id = damager.getUniqueId();
        int combo = comboList.get(id);
        comboList.put(id, combo + 1);

        Pair<Integer, Integer> enchantInfo = getEnchantInformation(damager);

        if (enchantInfo == null) return;

        int multiply = enchantInfo.first * combo;

        if (multiply >= enchantInfo.second) {
            multiply = enchantInfo.second;
        }

        e.setDamage(e.getDamage() + multiply);
        //violated.damage(e.getFinalDamage() + multiply);
    }

    private JsonObject extraInfo() {
        JsonObject info = new JsonObject();

        info.addProperty("damageIncreasePerHitPerLevel", 1);
        info.addProperty("maxIncreasePerLevel", 3);

        info.toString();

        return info;
    }

    private void checkAndPut(Player player) {

        if (!comboList.containsKey(player.getUniqueId())) {
            comboList.put(player.getUniqueId(), 0);
        }

    }

    private void resetCombo(Player player) {
        comboList.put(player.getUniqueId(), 0);
    }

    private Pair<Integer, Integer> getEnchantInformation(Player player) {
        EntityPlayer entityPlayer = ((CraftPlayer) player).getHandle();

        AtomicInteger damageIncrease = new AtomicInteger(0);
        AtomicInteger maxDamage = new AtomicInteger(0);

        Object2ObjectMap<EnchantSave, Integer> toReturn = CustomEnchants.getInstance().getEnchantsFromPlayer(player);

        toReturn.forEach((enchant, level) -> {
            if (!enchant.getName().equals(this.getName())) return;
            JsonObject obj = new Gson().fromJson(enchant.getExtra().get(0), JsonObject.class);

            damageIncrease.set(obj.get("damageIncreasePerHitPerLevel").getAsInt() * (level / 2));
            maxDamage.set(obj.get("maxIncreasePerLevel").getAsInt() * level);
        });

        return new Pair<>(damageIncrease.get(), maxDamage.get());
    }

}
