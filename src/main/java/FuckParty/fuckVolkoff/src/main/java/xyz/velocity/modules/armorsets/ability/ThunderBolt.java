package FuckParty.fuckVolkoff.src.main.java.xyz.velocity.modules.armorsets.ability;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import xyz.velocity.modules.armorsets.AbilityManager;
import xyz.velocity.modules.armorsets.AbilityWrapper;
import xyz.velocity.modules.armorsets.ArmorSets;
import xyz.velocity.modules.armorsets.annotations.Ability;
import xyz.velocity.modules.armorsets.config.AbilitiesConfig;
import xyz.velocity.modules.armorsets.config.saves.AbilitySave;
import xyz.velocity.modules.customenchants.CustomEnchants;
import xyz.velocity.modules.util.EnchantUtil;

@Ability
public class ThunderBolt extends AbstractAbility {

    public ThunderBolt() {
        AbilitiesConfig config = AbilitiesConfig.getInstance();

        AbilitySave ability = getAbilitySave();

        if (!config.getAbilities().stream().anyMatch(obj -> obj.getName().equals(ability.getName()))) {
            config.getAbilities().add(ability);
        }
    }

    @Override
    public String getName() {
        return "thunderbolt";
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

                int players = 0;

                for (Entity nearbyEntity : attacker.getNearbyEntities(10, 10, 10)) {
                    if (players >= 5) break;
                    if (!(nearbyEntity instanceof Player)) continue;

                    Player nearbyPlayer = (Player) nearbyEntity;

                    if (!CustomEnchants.getInstance().canDamage(attacker, nearbyPlayer)) continue;
                    if (CustomEnchants.getInstance().isAlly(attacker, nearbyPlayer)) continue;

                    Location loc = nearbyPlayer.getLocation();

                    nearbyPlayer.getWorld().spawnEntity(loc, EntityType.LIGHTNING);
                    nearbyPlayer.getWorld().strikeLightningEffect(loc);

                    nearbyPlayer.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, ability.getDuration() * 20, 1));
                    nearbyPlayer.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, ability.getDuration() * 20, 0));
                    nearbyPlayer.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, ability.getDuration() * 20, 0));

                    if (!nearbyPlayer.equals(defender)) AbilityManager.sendAbilityMessages(ability, nearbyPlayer);

                    players += 1;
                }

                AbilityManager.sendAbilityMessages(ability, attacker, defender);
                AbilityManager.abilityCache.put(attacker.getUniqueId(), newAbility);
            }
        }
    }

    private AbilitySave getAbilitySave() {
        String name = "thunderbolt";
        String chatName = "&f&lThunder Bolt";
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
