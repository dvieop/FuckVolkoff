package FuckParty.fuckVolkoff.src.main.java.xyz.velocity.modules.armorsets.ability;

import org.bukkit.event.Event;
import xyz.velocity.modules.armorsets.config.saves.AbilitySave;

public abstract class AbstractAbility {

    public abstract String getName();
    public abstract AbilitySave getAbility();
    public abstract <T extends Event> void runTask(T event);

}
