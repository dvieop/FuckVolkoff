package FuckParty.fuckVolkoff.src.main.java.xyz.velocity.modules.customenchants;

import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectLinkedOpenHashSet;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import org.bukkit.entity.Player;
import org.reflections.Reflections;
import xyz.velocity.modules.armorsets.AbilityManager;
import xyz.velocity.modules.armorsets.AbilityWrapper;
import xyz.velocity.modules.customenchants.config.CustomEnchantConfig;
import xyz.velocity.modules.customenchants.annotations.Enchant;
import xyz.velocity.modules.customenchants.config.saves.EnchantSave;
import xyz.velocity.modules.customenchants.enchants.AbstractEnchant;

import java.util.UUID;

public class EnchantManager {

    public static final Object2ObjectMap<String, AbstractEnchant> enchantList = new Object2ObjectOpenHashMap<>();
    public static final Object2ObjectMap<String, AbstractEnchant> nonVanillaEnchants = new Object2ObjectOpenHashMap<>();
    public static Object2ObjectOpenHashMap<UUID, Object2ObjectOpenHashMap<String, Long>> cooldownCache = new Object2ObjectOpenHashMap<>();
    private ObjectSet<Class<? extends AbstractEnchant>> classes;

    public EnchantManager() {

        Reflections reflection = new Reflections();
        this.classes = new ObjectLinkedOpenHashSet<>();

        for (Class<?> aClass : reflection.getTypesAnnotatedWith(Enchant.class)) {
            try {
                this.classes.add((Class<? extends AbstractEnchant>) aClass);
            } catch (ClassCastException ignored) {

            } catch (Throwable err) {
                err.printStackTrace();
            }
        }

        loadEnchants();

    }

    private void loadEnchants() {

        this.classes.forEach(aClass -> {

            try {
                AbstractEnchant enchant = aClass.newInstance();

                this.enchantList.put(enchant.getName(), enchant);
            } catch (Throwable e) {
                e.printStackTrace();
            }

        });

        CustomEnchantConfig.getInstance().saveConfig();

    }

    public static boolean isOnCooldown(Player player, String enchantName) {
        return cooldownCache.containsKey(player.getUniqueId()) && checkCooldown(player, enchantName);
    }

    private static boolean checkCooldown(Player player, String enchantName) {
        UUID uuid = player.getUniqueId();

        Object2ObjectOpenHashMap<String, Long> enchantInfo = cooldownCache.get(uuid);

        if (!enchantInfo.containsKey(enchantName)) return false;
        if (System.currentTimeMillis() >= enchantInfo.get(enchantName)) {
            cooldownCache.get(uuid).remove(enchantName);

            if (cooldownCache.get(uuid).isEmpty()) cooldownCache.remove(uuid);
            return false;
        }

        return true;
    }

    public static void addCooldown(Player player, EnchantSave enchantSave) {
        UUID id = player.getUniqueId();

        if (!cooldownCache.containsKey(id)) cooldownCache.put(id, new Object2ObjectOpenHashMap<>());
        cooldownCache.get(id).put(enchantSave.getName(), System.currentTimeMillis() + (enchantSave.getCooldown() * 1000L));
    }

}
