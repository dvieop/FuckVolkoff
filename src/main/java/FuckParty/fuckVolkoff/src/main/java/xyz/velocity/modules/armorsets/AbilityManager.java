package FuckParty.fuckVolkoff.src.main.java.xyz.velocity.modules.armorsets;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import org.bukkit.entity.Player;
import org.reflections.Reflections;
import xyz.velocity.VelocityFeatures;
import xyz.velocity.modules.armorsets.ability.AbstractAbility;
import xyz.velocity.modules.armorsets.config.AbilitiesConfig;
import xyz.velocity.modules.armorsets.config.saves.AbilitySave;

import java.util.*;

public class AbilityManager {

    public static final Object2ObjectOpenHashMap<String, AbstractAbility> abilityList = new Object2ObjectOpenHashMap<>();
    public static Object2ObjectOpenHashMap<UUID, AbilityWrapper> abilityCache = new Object2ObjectOpenHashMap<>();
    private Set<Class<? extends AbstractAbility>> classes;

    public AbilityManager() {

        Reflections reflection = new Reflections();
        this.classes = new LinkedHashSet<>();

        for (Class<?> aClass : reflection.getTypesAnnotatedWith(xyz.velocity.modules.armorsets.annotations.Ability.class)) {
            try {
                this.classes.add((Class<? extends AbstractAbility>) aClass);
            } catch (ClassCastException ignored) {

            } catch (Throwable err) {
                err.printStackTrace();
            }
        }

        loadAbilities();

    }

    private void loadAbilities() {
        abilityList.clear();

        this.classes.forEach(aClass -> {

            try {
                AbstractAbility ability = aClass.newInstance();

                this.abilityList.put(ability.getName(), ability);
            } catch (Throwable e) {
                e.printStackTrace();
            }

        });

        AbilitiesConfig.getInstance().saveConfig();

    }

    public static void sendAbilityMessages(AbilitySave abilitySave, Player damager, Player violated) {
        damager.sendMessage(VelocityFeatures.chat(abilitySave.getAttackerMessage()
                .replace("<player>", violated.getName())
                .replace("<ability>", abilitySave.getChatName())
        ));

        violated.sendMessage(VelocityFeatures.chat(abilitySave.getDefenderMessage()
                .replace("<ability>", abilitySave.getChatName())
        ));
    }

    public static void sendAbilityMessages(AbilitySave abilitySave, Player defender) {
        defender.sendMessage(VelocityFeatures.chat(abilitySave.getDefenderMessage()
                .replace("<ability>", abilitySave.getChatName())
        ));
    }

    public static boolean checkArmorSet(Player player, String armorset) {
        return ArmorSets.equippedSets.containsKey(player.getUniqueId()) && ArmorSets.equippedSets.get(player.getUniqueId()).equalsIgnoreCase(armorset);
    }

    public static boolean isAbilityActive(Player player) {
        return abilityCache.containsKey(player.getUniqueId()) && !abilityCache.get(player.getUniqueId()).hasDurationExpired(player);
    }

    public static boolean isOnCooldown(Player player) {
        return abilityCache.containsKey(player.getUniqueId()) && abilityCache.get(player.getUniqueId()).isOnCooldown(player);
    }

}
