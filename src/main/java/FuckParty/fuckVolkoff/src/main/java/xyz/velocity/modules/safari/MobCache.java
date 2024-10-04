package FuckParty.fuckVolkoff.src.main.java.xyz.velocity.modules.safari;

import lombok.Getter;
import org.bukkit.entity.Player;
import xyz.velocity.modules.safari.config.saves.MobSave;

@Getter
public class MobCache {

    SafariCache safari;
    Player summoner;
    String original;
    MobSave mobSave;
    int level;
    int damage;

    public MobCache(SafariCache cache, Player summoner, String original, MobSave mobSave, int level, int damage) {
        this.safari = cache;
        this.summoner = summoner;
        this.original = original;
        this.mobSave = mobSave;
        this.level = level;
        this.damage = damage;
    }

}
