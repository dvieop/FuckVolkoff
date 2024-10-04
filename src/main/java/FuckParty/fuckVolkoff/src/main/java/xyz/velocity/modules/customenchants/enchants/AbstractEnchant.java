package FuckParty.fuckVolkoff.src.main.java.xyz.velocity.modules.customenchants.enchants;

import org.bukkit.event.Event;
import xyz.velocity.modules.customenchants.EnumEnchantType;

public abstract class AbstractEnchant {

    public abstract boolean isEnabled();
    public abstract String getName();
    public abstract boolean isVanillaEnchant();
    public abstract String getEnchant();
    public abstract EnumEnchantType getEnchantType();
    public abstract <T extends Event> void runTask(T event);

}
