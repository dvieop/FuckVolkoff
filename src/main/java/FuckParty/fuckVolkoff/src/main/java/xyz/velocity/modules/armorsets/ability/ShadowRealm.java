package FuckParty.fuckVolkoff.src.main.java.xyz.velocity.modules.armorsets.ability;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.potion.PotionEffect;
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

import java.util.ArrayList;
import java.util.List;

@Ability
public class ShadowRealm extends AbstractAbility {

    public ShadowRealm() {
        AbilitiesConfig config = AbilitiesConfig.getInstance();

        AbilitySave ability = getAbilitySave();

        if (!config.getAbilities().stream().anyMatch(obj -> obj.getName().equals(ability.getName()))) {
            config.getAbilities().add(ability);
        }
    }

    @Override
    public String getName() {
        return "shadowrealm";
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

                AbilityWrapper newAbility = new AbilityWrapper(
                        this.getName(),
                        attacker,
                        defender,
                        System.currentTimeMillis() + (ability.getDuration() * 1000L),
                        System.currentTimeMillis() + (ability.getCooldown() * 1000L)
                );

                defender.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, ability.getDuration() * 20, 1));

                List<Player> invisPlayers = new ArrayList<>();

                for (Entity nearbyEntity : attacker.getNearbyEntities(64, 64, 64)) {
                    if (!(nearbyEntity instanceof Player)) continue;
                    if (!nearbyEntity.equals(attacker) && !nearbyEntity.equals(defender)) {
                        Player nearbyPlayer = (Player) nearbyEntity;

                        nearbyPlayer.hidePlayer(attacker);
                        nearbyPlayer.hidePlayer(defender);

                        attacker.hidePlayer(nearbyPlayer);
                        defender.hidePlayer(nearbyPlayer);

                        invisPlayers.add(nearbyPlayer);
                    }
                }
                new BukkitRunnable() {

                    @Override
                    public void run() {
                        for (Player invisPlayer : invisPlayers) {
                            invisPlayer.showPlayer(attacker);
                            invisPlayer.showPlayer(defender);

                            attacker.showPlayer(invisPlayer);
                            defender.showPlayer(invisPlayer);
                        }
                    }

                }.runTaskLater(VelocityFeatures.getInstance(), ability.getDuration() * 20L);

                AbilityManager.abilityCache.put(attacker.getUniqueId(), newAbility);
                AbilityManager.sendAbilityMessages(ability, attacker, defender);
            }
        }
    }

    private AbilitySave getAbilitySave() {
        String name = "shadowrealm";
        String chatName = "&5&lShadow Realm";
        String attMsg = "You have affected <player> with <ability>";
        String defMsg = "You have been affected by <ability>";
        double damageMulti = 1.0;
        double damageRed = 1.0;
        double chance = 1.5;
        int duration = 5;
        int cooldown = 60;

        return new AbilitySave(name, chatName, attMsg, defMsg, damageMulti, damageRed, chance, duration, cooldown, "randomset");
    }

}
