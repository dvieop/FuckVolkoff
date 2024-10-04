package FuckParty.fuckVolkoff.src.main.java.xyz.velocity.modules;

import org.bukkit.entity.Player;

public abstract class AbstractModule {
    public abstract String getName();
    public abstract boolean isEnabled();
    public abstract void onEnable();
    public abstract void onDisable();
    public abstract String placeholderRequest(Player player, String arg);
}
