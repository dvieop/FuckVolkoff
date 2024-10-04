package FuckParty.fuckVolkoff.src.main.java.xyz.velocity.modules.customenchants;

import org.bukkit.entity.Player;

public class Exp {

    public static int getTotalExperience(final Player player) {

        int exp = Math.round(getExpAtLevel(player.getLevel()) * player.getExp());
        int currentLevel = player.getLevel();

        while (currentLevel > 0) {
            currentLevel--;
            exp += getExpAtLevel(currentLevel);
        }

        if (exp < 0) {
            exp = Integer.MAX_VALUE;
        }

        return exp;

    }

    public static int getExpAtLevel(final int level) {

        if (level <= 15) {
            return (2 * level) + 7;
        }

        if ((level >= 16) && (level <= 30)) {
            return (5 * level) - 38;
        }

        return (9 * level) - 158;

    }

    public static void setTotalExperience(final Player player, final int exp) {

        if (exp < 0) {
            throw new IllegalArgumentException("Experience is negative!");
        }

        player.setExp(0);
        player.setLevel(0);
        player.setTotalExperience(0);

        int amount = exp;

        while (amount > 0) {
            final int expToLevel = getExpAtLevel(player.getLevel());
            amount -= expToLevel;

            if (amount >= 0) {
                player.giveExp(expToLevel);
            } else {
                amount += expToLevel;
                player.giveExp(amount);
                amount = 0;
            }
        }
    }

}
