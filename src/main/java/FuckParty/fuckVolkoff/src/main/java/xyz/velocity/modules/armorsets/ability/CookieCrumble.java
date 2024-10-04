package FuckParty.fuckVolkoff.src.main.java.xyz.velocity.modules.armorsets.ability;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import xyz.velocity.VelocityFeatures;
import xyz.velocity.modules.armorsets.AbilityManager;
import xyz.velocity.modules.armorsets.AbilityWrapper;
import xyz.velocity.modules.armorsets.ArmorSets;
import xyz.velocity.modules.armorsets.annotations.Ability;
import xyz.velocity.modules.armorsets.config.AbilitiesConfig;
import xyz.velocity.modules.armorsets.config.saves.AbilitySave;
import xyz.velocity.modules.util.EnchantUtil;

@Ability
public class CookieCrumble extends AbstractAbility {

    public CookieCrumble() {
        AbilitiesConfig config = AbilitiesConfig.getInstance();

        AbilitySave ability = getAbilitySave();

        if (!config.getAbilities().stream().anyMatch(obj -> obj.getName().equals(ability.getName()))) {
            config.getAbilities().add(ability);
        }
    }

    @Override
    public String getName() {
        return "cookiecrumble";
    }

    @Override
    public AbilitySave getAbility() {
        return AbilitiesConfig.getInstance().getAbilities()
                .stream()
                .filter(obj -> obj.getName().equals(this.getName()))
                .findFirst().get();
    }

    @Override
    public <T extends Event> void runTask(T event) {
        EntityDamageByEntityEvent e = (EntityDamageByEntityEvent) event;

        Player attacker = (Player) e.getDamager();
        Player defender = (Player) e.getEntity();

        if (!ArmorSets.equippedSets.containsKey(attacker.getUniqueId())) return;
        if (!ArmorSets.getInstance().hasAbility(attacker.getInventory().getArmorContents())) return;

        String id = ArmorSets.getInstance().getAbilityId(attacker.getInventory().getHelmet());

        if (!id.equals(this.getName())) return;
        if (!AbilityManager.isAbilityActive(attacker)) {
            double chance = EnchantUtil.getRandomDouble();

            AbilitySave ability = getAbility();

            if (chance < ability.getChance()) {
                if (AbilityManager.isOnCooldown(attacker)) return;
                if (!ArmorSets.getInstance().isInWater(attacker)) return;
                if (defender.hasPotionEffect(PotionEffectType.SATURATION)) {
                    defender.removePotionEffect(PotionEffectType.SATURATION);
                }

                defender.setFoodLevel(0);

                new BukkitRunnable() {
                    @Override
                    public void run() {
                        defender.addPotionEffect(EnchantUtil.getEffect("SATURATION:0    "));
                    }

                }.runTaskLater(VelocityFeatures.getInstance(), ability.getDuration() * 20 + 1);

                AbilityWrapper newAbility = new AbilityWrapper(
                        this.getName(),
                        attacker,
                        defender,
                        System.currentTimeMillis() + (ability.getDuration() * 1000L),
                        System.currentTimeMillis() + (ability.getCooldown() * 1000L)
                );

                AbilityManager.abilityCache.put(attacker.getUniqueId(), newAbility);
                AbilityManager.sendAbilityMessages(ability, attacker, defender);
            }
        }
    }

    private AbilitySave getAbilitySave() {
        String name = "cookiecrumble";
        String chatName = "&6&lCookie Crumble";
        String attMsg = "You have affected <player> with <ability>";
        String defMsg = "You have been affected by <ability>";
        double damageMulti = 1.0;
        double damageRed = 1.0;
        double chance = 1.5;
        int duration = 5;
        int cooldown = 60;

        return new AbilitySave(name, chatName, attMsg, defMsg, damageMulti, damageRed, chance, duration, cooldown, "cookiemonster");
    }

}
