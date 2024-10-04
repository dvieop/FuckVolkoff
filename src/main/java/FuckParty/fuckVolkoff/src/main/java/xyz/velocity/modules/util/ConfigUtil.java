package FuckParty.fuckVolkoff.src.main.java.xyz.velocity.modules.util;

import dev.lyons.configapi.ConfigAPI;
import dev.lyons.configapi.ConfigClass;
import dev.lyons.configapi.EnumFormattingType;
import dev.lyons.configapi.annotations.Config;
import org.bukkit.scheduler.BukkitRunnable;
import org.reflections.Reflections;
import xyz.velocity.VelocityFeatures;
import xyz.velocity.modules.foundry.config.CreditsConfig;
import xyz.velocity.modules.safari.config.RewardConfig;
import xyz.velocity.modules.safari.config.StatsConfig;
import xyz.velocity.modules.stronghold.config.DataConfig;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ConfigUtil {

    public static final ExecutorService saveThread = Executors.newSingleThreadExecutor();

    public static void loadAllConfigs() {
        try {
            final Reflections reflections = new Reflections();
            for (final Class<?> aClass : reflections.getTypesAnnotatedWith(Config.class)) {
                if (ConfigClass.class.isAssignableFrom(aClass)){
                    final ConfigClass config = (ConfigClass) aClass.newInstance();
                    ConfigAPI.getInstance().register(config);
                }
            }
        } catch (final Throwable err) {
            throw new RuntimeException(err);
        }

        ConfigAPI.getInstance().loadAll();
        ConfigAPI.getInstance().saveAll();
    }

    public static void reloadAllConfigs() {
        try {
            final ConfigAPI configAPI = ConfigAPI.getInstance();
            final Field field = configAPI.getClass().getDeclaredField("configMap");
            field.setAccessible(true);
            final Map<Object, ConfigClass> configMap = (Map<Object, ConfigClass>) field.get(configAPI);
            for (final ConfigClass config : configMap.values()) {
                config.getGson().fromJson(EnumFormattingType.JSON.formatConfig(config.getConfigData()), config.getClass());
            }
        } catch (final Throwable err) {
            throw new RuntimeException(err);
        }
    }

    public static <T extends ConfigClass> void loadSpecificConfig(T klass) {
        ConfigAPI.getInstance().loadSpecificConfig(klass, false, false);
    }

    public static void saveConfigTask() {
        new BukkitRunnable() {
            @Override
            public void run() {
                long start = System.currentTimeMillis();
                ConfigAPI.getInstance().saveAllStatsConfigs();
                VelocityFeatures.getInstance().getLogger()
                        .info("Saved all stats configs in " + (System.currentTimeMillis() - start) + "ms");
            }
        }.runTaskTimer(VelocityFeatures.getInstance(), 0, 20 * 60 * 15);

    }

    public static void saveAll() {
        ConfigAPI.getInstance().saveAll();
    }

}
