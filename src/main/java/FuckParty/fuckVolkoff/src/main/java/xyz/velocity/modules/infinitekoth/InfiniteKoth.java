package FuckParty.fuckVolkoff.src.main.java.xyz.velocity.modules.infinitekoth;

import org.bukkit.entity.Player;
import xyz.velocity.VelocityFeatures;
import xyz.velocity.commands.CommandAPI;
import xyz.velocity.modules.infinitekoth.commands.KothCommand;
import xyz.velocity.modules.infinitekoth.config.KothConfig;
import xyz.velocity.modules.AbstractModule;
import xyz.velocity.modules.Module;
import xyz.velocity.modules.infinitekoth.util.Hologram;
import xyz.velocity.modules.util.Location;

@Module
public class InfiniteKoth extends AbstractModule {

    public InfiniteKoth() {

    }

    @Override
    public String getName() {
        return "infinite_koth";
    }

    @Override
    public boolean isEnabled() {
        return KothConfig.getInstance().isEnabled();
    }

    @Override
    public void onEnable() {
        new KothCap();
        new Hologram(VelocityFeatures.getInstance(), Location.parseToLocation(KothConfig.getInstance().getHologram().location)).spawnHologram();
        CommandAPI.getInstance().enableCommand(new KothCommand());

        KothConfig.getInstance().setEnabled(true);
    }

    @Override
    public void onDisable() {
        Hologram.getInstance().deleteHologram();
        CommandAPI.getInstance().disableCommand(KothCommand.class);

        try {
            KothCap.getInstance().bukkitTask.cancel();
        } catch (Throwable e) { }

        KothConfig.getInstance().setEnabled(false);
    }

    @Override
    public String placeholderRequest(Player player, String arg) {
        return "";
    }

}
