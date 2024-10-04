package FuckParty.fuckVolkoff.src.main.java.xyz.velocity.modules.generators;

import lombok.Getter;
import xyz.velocity.modules.generators.config.GeneratorConfig;
import xyz.velocity.modules.generators.config.saves.GenDataSave;
import xyz.velocity.modules.generators.config.saves.GeneratorSave;

public class GenCache {

    @Getter
    GenDataSave genDataSave;

    @Getter
    Hologram hologram = null;

    @Getter
    GeneratorSave generatorSave;

    public GenCache(GenDataSave genDataSave) {
        this.genDataSave = genDataSave;
        this.generatorSave = GeneratorConfig.getInstance().getGenerators()
                .stream()
                .filter(obj -> obj.getId().equalsIgnoreCase(genDataSave.getGenType()))
                .findFirst()
                .orElse(null);
    }

    public void setHologram(Hologram hologram) {
        this.hologram = hologram;
    }
}
