package FuckParty.fuckVolkoff.src.main.java.xyz.velocity.modules.killtracker;

import lombok.Getter;
import org.bukkit.entity.Player;
import xyz.velocity.VelocityFeatures;
import xyz.velocity.commands.CommandAPI;
import xyz.velocity.modules.killtracker.commands.KilltrackerCommand;
import xyz.velocity.modules.killtracker.config.KilltrackerConfig;
import xyz.velocity.modules.AbstractModule;
import xyz.velocity.modules.Module;

@Module
public class Killtracker extends AbstractModule {

    public Killtracker() {
        instance = this;
    }

    @Getter
    private static Killtracker instance;

    @Override
    public String getName() {
        return "killtracker";
    }

    @Override
    public boolean isEnabled() {
        return KilltrackerConfig.getInstance().isEnabled();
    }

    @Override
    public void onEnable() {
        VelocityFeatures.registerEvent(new KillListener());
        CommandAPI.getInstance().enableCommand(new KilltrackerCommand());

        KilltrackerConfig.getInstance().setEnabled(true);
    }

    @Override
    public void onDisable() {
        VelocityFeatures.unregisterEvent(KillListener.getInstance());
        CommandAPI.getInstance().disableCommand(KilltrackerCommand.class);

        KilltrackerConfig.getInstance().setEnabled(false);
    }

    @Override
    public String placeholderRequest(Player player, String arg) {
        return "";
    }

}