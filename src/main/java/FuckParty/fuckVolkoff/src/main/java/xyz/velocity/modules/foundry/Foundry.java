package FuckParty.fuckVolkoff.src.main.java.xyz.velocity.modules.foundry;

import org.bukkit.entity.Player;
import xyz.velocity.VelocityFeatures;
import xyz.velocity.commands.CommandAPI;
import xyz.velocity.modules.foundry.commands.FoundryCommand;
import xyz.velocity.modules.foundry.config.FoundryConfig;
import xyz.velocity.modules.AbstractModule;
import xyz.velocity.modules.Module;
import xyz.velocity.modules.foundry.listeners.FoundryListener;
import xyz.velocity.modules.foundry.util.Hologram;
import xyz.velocity.modules.util.Location;

@Module
public class Foundry extends AbstractModule {

    public Foundry() {

    }

    @Override
    public String getName() {
        return "foundry";
    }

    @Override
    public boolean isEnabled() {
        return FoundryConfig.getInstance().isEnabled();
    }

    @Override
    public void onEnable() {
        VelocityFeatures.registerEvent(new FoundryListener());
        CommandAPI.getInstance().enableCommand(new FoundryCommand());

        FoundryConfig.getInstance().setEnabled(true);

        new Hologram(VelocityFeatures.getInstance(), Location.parseToLocation(FoundryConfig.getInstance().getHologramLocation())).spawnHologram();
    }

    @Override
    public void onDisable() {
        VelocityFeatures.unregisterEvent(FoundryListener.getInstance());
        CommandAPI.getInstance().disableCommand(FoundryCommand.class);

        FoundryConfig.getInstance().setEnabled(false);

        FoundryCap.isActive = false;

        try {
            FoundryCap.getInstance().bukkitTask.cancel();
        } catch (Throwable e) { }

        Hologram.getInstance().deleteHologram();
    }

    @Override
    public String placeholderRequest(Player player, String arg) {
        return "";
    }

}
