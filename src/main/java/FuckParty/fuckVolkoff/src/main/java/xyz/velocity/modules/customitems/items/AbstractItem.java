package FuckParty.fuckVolkoff.src.main.java.xyz.velocity.modules.customitems.items;

import org.bukkit.event.Event;

public abstract class AbstractItem {

    public abstract String getName();
    public abstract <T extends Event> void runTask(T event);

}
