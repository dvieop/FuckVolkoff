package FuckParty.fuckVolkoff.src.main.java.xyz.velocity.modules.waterpearl;

import org.bukkit.entity.Player;
import xyz.velocity.VelocityFeatures;
import xyz.velocity.modules.waterpearl.config.WaterPearlConfig;
import xyz.velocity.modules.AbstractModule;
import xyz.velocity.modules.Module;

@Module
public class WaterPearl extends AbstractModule {

    @Override
    public String getName() {
        return "water_pearls";
    }

    @Override
    public boolean isEnabled() {
        return WaterPearlConfig.getInstance().isEnabled();
    }

    @Override
    public void onEnable() {
        VelocityFeatures.registerEvent(new PearlListener());

        WaterPearlConfig.getInstance().setEnabled(true);
    }

    @Override
    public void onDisable() {
        VelocityFeatures.unregisterEvent(PearlListener.getInstance());

        WaterPearlConfig.getInstance().setEnabled(false);
    }

    @Override
    public String placeholderRequest(Player player, String arg) {
        return "";
    }

}
