package FuckParty.fuckVolkoff.src.main.java.xyz.velocity.modules.generators;

import eu.decentsoftware.holograms.api.DHAPI;
import eu.decentsoftware.holograms.api.holograms.HologramLine;
import lombok.Getter;
import org.bukkit.plugin.Plugin;
import xyz.velocity.VelocityFeatures;

import java.util.LinkedList;
import java.util.List;

public class Hologram {

    @Getter
    private static Hologram instance;

    public eu.decentsoftware.holograms.api.holograms.Hologram hologram = null;

    public LinkedList<HologramLine> lines = new LinkedList<>();

    org.bukkit.Location location;
    Plugin plugin;
    GenCache genCache;

    public Hologram(Plugin plugin, org.bukkit.Location location, GenCache genCache) {
        this.plugin = plugin;
        this.location = location;
        this.genCache = genCache;

        instance = this;
        spawnHologram();
    }

    public void spawnHologram() {

        hologram = DHAPI.createHologram(String.valueOf(hashCode()), this.location);

        lines.add(DHAPI.addHologramLine(hologram, VelocityFeatures.chat(genCache.getGeneratorSave().getItem().getName())));

        for (String s : genCache.getGeneratorSave().getHologram()) {
            lines.add(DHAPI.addHologramLine(hologram, VelocityFeatures.chat(s)));
        }

        updateHologram(genCache.getGeneratorSave().getHologram());
    }

    public void updateHologram(List<String> list) {
        for (int i = 1; i < lines.size(); i++) {
            HologramLine line = lines.get(i);

            int tier = genCache.genDataSave.getTier();
            int maxTier = genCache.getGeneratorSave().getTierUpgrade().getMaxTier();
            int speed = tier * genCache.generatorSave.getTierUpgrade().getSpeedIncrement();
            int moneyInterval = speed * genCache.generatorSave.getTierUpgrade().getBaseMoney();
            int storage = genCache.genDataSave.getStorage();
            int capacity = genCache.genDataSave.getCapacity();

            line.setText(VelocityFeatures.chat(
                    list.get(i - 1)
                            .replace("<tier>", Generator.getInstance().tierBar(tier, maxTier))
                            .replace("<speed>", speed + "")
                            .replace("<moneyInterval>", moneyInterval + "")
                            .replace("<storage>", Generator.getInstance().formatNumber(storage))
                            .replace("<capacity>", Generator.getInstance().formatNumber(capacity))
            ));
        }
    }

    public void deleteHologram() {
        this.hologram.delete();
    }

}
