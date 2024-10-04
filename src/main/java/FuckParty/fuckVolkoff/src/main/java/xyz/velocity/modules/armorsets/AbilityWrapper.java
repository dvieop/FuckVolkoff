package FuckParty.fuckVolkoff.src.main.java.xyz.velocity.modules.armorsets;

import org.bukkit.entity.Player;

import java.util.UUID;

public class AbilityWrapper {

    String abilityName;
    Player attacker;
    Player defender;
    long duration;
    long cooldown;

    public AbilityWrapper(String abilityName, Player attacker, Player defender, long duration, long cooldown) {
        this.abilityName = abilityName;
        this.attacker = attacker;
        this.defender = defender;
        this.duration = duration;
        this.cooldown = cooldown;
    }

    public String getAbilityName() {
        return abilityName;
    }

    public Player getAttacker() {
        return attacker;
    }

    public Player getDefender() {
        return defender;
    }

    public long getDuration() {
        return duration;
    }

    public long getCooldown() {
        return cooldown;
    }

    public boolean cooldownExpired() {
        return System.currentTimeMillis() >= cooldown;
    }

    public boolean isOnCooldown(Player player) {
        UUID uuid = player.getUniqueId();

        boolean isOnCD = false;

        if (AbilityManager.abilityCache.containsKey(uuid)) {
            AbilityWrapper ability = AbilityManager.abilityCache.get(uuid);

            if (ability.cooldownExpired()) {
                AbilityManager.abilityCache.remove(uuid);
            } else {
                isOnCD = true;
            }
        }

        return isOnCD;
    }

    public boolean hasDurationExpired(Player player) {
        return System.currentTimeMillis() >= AbilityManager.abilityCache.get(player.getUniqueId()).getDuration();
    }
}
