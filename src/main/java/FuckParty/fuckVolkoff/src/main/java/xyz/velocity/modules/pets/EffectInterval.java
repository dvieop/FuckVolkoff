package FuckParty.fuckVolkoff.src.main.java.xyz.velocity.modules.pets;

import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import xyz.velocity.VelocityFeatures;
import xyz.velocity.modules.pets.config.saves.LevelEffectSave;

import java.util.UUID;

public class EffectInterval {

    public EffectInterval() {
        instance = this;
        startEffectsInterval(15);
    }

    public BukkitTask bukkitTask;

    private void startEffectsInterval(int interval) {
        BukkitRunnable bukkitRunnable = new BukkitRunnable() {
            @Override
            public void run() {
                for (Object2ObjectMap.Entry<UUID, PetWrapper> entry : Pets.equippedPets.object2ObjectEntrySet()) {
                   Player p = Bukkit.getPlayer(entry.getKey());
                   PetWrapper pW = entry.getValue();

                   LevelEffectSave effects =  Pets.getInstance().getEffectsByLevel(pW.customPet.getPetSave().getLevelEffects(), pW.petStats);

                    for (String effect : effects.getEffects()) {
                        if (effect.startsWith("potion")) {
                            Pets.getInstance().givePotionEffect(p, effect.replace("potion:", "").toUpperCase());
                        }

                        if (effect.startsWith("watereffects")) {
                            Pets.getInstance().givePotionEffect(p, effect.replace("watereffects:", "").toUpperCase());
                        }

                        if (effect.startsWith("lavaeffects")) {
                            Pets.getInstance().givePotionEffect(p, effect.replace("lavaeffects:", "").toUpperCase());
                        }
                    }
                }
            }
        };

        bukkitTask = bukkitRunnable.runTaskTimer(VelocityFeatures.getInstance(), 0, interval * 20);
    }

    private static EffectInterval instance;

    public static EffectInterval getInstance() {
        return instance;
    }

}
