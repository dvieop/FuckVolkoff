package FuckParty.fuckVolkoff.src.main.java.xyz.velocity.modules.customenchants.items;

import org.bukkit.event.Event;

public abstract class AbstractEnchantItem {

    public abstract String getName();
    public abstract <T extends Event> void runTask(T event);

}
