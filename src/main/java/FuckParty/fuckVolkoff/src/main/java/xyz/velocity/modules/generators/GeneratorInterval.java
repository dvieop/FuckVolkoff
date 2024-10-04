package FuckParty.fuckVolkoff.src.main.java.xyz.velocity.modules.generators;

import it.unimi.dsi.fastutil.objects.ObjectIterator;
import lombok.Getter;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import xyz.velocity.VelocityFeatures;
import xyz.velocity.modules.generators.config.saves.GenDataSave;
import xyz.velocity.modules.generators.config.saves.GeneratorSave;

public class GeneratorInterval {

    public GeneratorInterval() {
        startInterval();
        instance = this;
    }

    public BukkitTask bukkitTask;

    private void startInterval() {
        BukkitRunnable bukkitRunnable = new BukkitRunnable() {

            @Override
            public void run() {
                for (ObjectIterator<GenCache> iterator = Generator.placedGenerators.iterator(); iterator.hasNext();) {
                    GenCache genCache = iterator.next();
                    GeneratorSave generatorSave = genCache.generatorSave;
                    GenDataSave genDataSave = genCache.genDataSave;

                    int storage = genDataSave.getStorage();
                    int speed = genDataSave.getTier() * generatorSave.getTierUpgrade().getSpeedIncrement();
                    int newStorage = storage + (speed * generatorSave.getTierUpgrade().getBaseMoney());

                    if (newStorage >= genDataSave.getCapacity()) {
                        newStorage = genDataSave.getCapacity();
                    }

                    genCache.getGenDataSave().setStorage(newStorage);
                    genCache.getHologram().updateHologram(generatorSave.getHologram());
                }
            }
        };

        bukkitTask = bukkitRunnable.runTaskTimer(VelocityFeatures.getInstance(), 0, 20 * 60);
    }

    @Getter
    private static GeneratorInterval instance;

}
