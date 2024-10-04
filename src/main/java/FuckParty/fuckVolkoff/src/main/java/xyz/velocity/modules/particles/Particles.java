package FuckParty.fuckVolkoff.src.main.java.xyz.velocity.modules.particles;

import lombok.Getter;
import org.bukkit.entity.Player;
import xyz.velocity.VelocityFeatures;
import xyz.velocity.commands.CommandAPI;
import xyz.velocity.modules.particles.commands.ParticleCommand;
import xyz.velocity.modules.particles.config.ParticleConfig;
import xyz.velocity.modules.AbstractModule;
import xyz.velocity.modules.Module;

import java.util.Random;

@Module
public class Particles extends AbstractModule {

    public Particles() {
        instance = this;
    }

    @Getter
    private static Particles instance;

    public int getRandom(int lower, int upper) {
        Random random = new Random();
        return random.nextInt((upper - lower) + 1) + lower;
    }

    public double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        long factor = (long) Math.pow(10, places);
        value = value * factor;
        long tmp = Math.round(value);
        return (double) tmp / factor;
    }

    @Override
    public String getName() {
        return "particles";
    }

    @Override
    public boolean isEnabled() {
        return ParticleConfig.getInstance().isEnabled();
    }

    @Override
    public void onEnable() {
        VelocityFeatures.registerEvent(new ParticleListener());
        CommandAPI.getInstance().enableCommand(new ParticleCommand());

        ParticleConfig.getInstance().setEnabled(true);
    }

    @Override
    public void onDisable() {
        VelocityFeatures.registerEvent(ParticleListener.getInstance());
        CommandAPI.getInstance().enableCommand(ParticleCommand.class);

        ParticleConfig.getInstance().setEnabled(false);
    }

    @Override
    public String placeholderRequest(Player player, String arg) {
        return "";
    }

}
