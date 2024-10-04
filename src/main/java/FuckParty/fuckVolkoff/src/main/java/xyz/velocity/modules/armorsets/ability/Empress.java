package FuckParty.fuckVolkoff.src.main.java.xyz.velocity.modules.armorsets.ability;

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
import xyz.velocity.modules.util.EnchantUtil;

@Ability
public class Empress extends AbstractAbility {

    public Empress() {
        AbilitiesConfig config = AbilitiesConfig.getInstance();

        AbilitySave ability = getAbilitySave();

        if (!config.getAbilities().stream().anyMatch(obj -> obj.getName().equals(ability.getName()))) {
            config.getAbilities().add(ability);
        }
    }

    @Override
    public String getName() {
        return "empress";
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

        if (!ArmorSets.equippedSets.containsKey(defender.getUniqueId())) return;
        if (!ArmorSets.getInstance().hasAbility(defender.getInventory().getArmorContents())) return;

        String id = ArmorSets.getInstance().getAbilityId(defender.getInventory().getHelmet());

        if (!id.equals(this.getName())) return;
        if (!AbilityManager.isAbilityActive(defender)) {
            double chance = EnchantUtil.getRandomDouble();

            AbilitySave ability = getAbility();

            if (chance < ability.getChance()) {
                if (AbilityManager.isOnCooldown(defender)) return;

                AbilityWrapper newAbility = new AbilityWrapper(
                        this.getName(),
                        attacker,
                        defender,
                        System.currentTimeMillis() + (ability.getDuration() * 1000L),
                        System.currentTimeMillis() + (ability.getCooldown() * 1000L)
                );

                defender.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, ability.getDuration() * 20, 0));

                AbilityManager.abilityCache.put(defender.getUniqueId(), newAbility);
                AbilityManager.sendAbilityMessages(ability, defender);
            }
        } else {
            e.setCancelled(true);
        }
    }

    private AbilitySave getAbilitySave() {
        String name = "empress";
        String chatName = "&d&lEmpress";
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
