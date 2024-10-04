package FuckParty.fuckVolkoff.src.main.java.xyz.velocity.modules.particles.config;

import dev.lyons.configapi.ConfigAPI;
import dev.lyons.configapi.ConfigClass;
import dev.lyons.configapi.EnumFormattingType;
import dev.lyons.configapi.annotations.Config;
import lombok.Getter;
import lombok.Setter;
import xyz.velocity.VelocityFeatures;

import java.io.File;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static xyz.velocity.modules.util.ConfigUtil.saveThread;

@Config("particles")
public class ParticleConfig implements ConfigClass {

    public static ParticleConfig getInstance() {
        return ConfigAPI.getInstance().getConfigObject(ParticleConfig.class);
    }

    @Getter
    @Setter
    @Config
    public boolean enabled = false;

    @Getter
    @Config
    public String damageHolo = "&e✧ &c<damage> &e✧";

    @Getter
    @Config
    public String toggleMsg = "You have toggled particles: <toggle>";

    @Getter
    @Config
    public final Map<UUID, Boolean> toggles = new HashMap<>();

    @Override
    public EnumFormattingType getFormatType() {
        return EnumFormattingType.YAML;
    }

    @Override
    public File getFile() {
        return new File(
                VelocityFeatures.getFileUtils().getConfigDir("Particles"),
                "particles.yml"
        );
    }

    @Override
    public boolean isStatsConfig() {
        return false;
    }

    public void saveData() {
        saveThread.submit(this::saveConfig);
    }

    public synchronized void saveConfig() {
        synchronized (this.toggles) {
            ConfigAPI.getInstance().saveConfig(this);
        }
    }

}
