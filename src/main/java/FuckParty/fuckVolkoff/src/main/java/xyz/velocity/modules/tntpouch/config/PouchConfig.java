package FuckParty.fuckVolkoff.src.main.java.xyz.velocity.modules.tntpouch.config;

import dev.lyons.configapi.ConfigAPI;
import dev.lyons.configapi.ConfigClass;
import dev.lyons.configapi.EnumFormattingType;
import dev.lyons.configapi.annotations.Config;
import java.io.File;
import java.util.Arrays;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import xyz.velocity.VelocityFeatures;
import xyz.velocity.modules.tntpouch.config.saves.PouchItemSave;
import xyz.velocity.modules.tntpouch.config.saves.PouchSave;

@Config("tntpouch")
public class PouchConfig implements ConfigClass {

    public static PouchConfig getInstance() {
        return ConfigAPI.getInstance().getConfigObject(PouchConfig.class);
    }

    @Getter
    @Setter
    @Config
    public boolean enabled = false;

    @Getter
    @Config
    String empty = "Can't withdraw from an empty tnt pouch!";

    @Getter
    @Config
    String withdraw = "You have successfully withdrew <amount> from the TNT Pouch!";

    @Getter
    @Config
    List<PouchSave> pouches = Arrays.asList(new PouchSave(1, 20000, new PouchItemSave("ENDER_CHEST", 0, 1, "&c&lPouch")));

    @Override
    public EnumFormattingType getFormatType() {
        return EnumFormattingType.YAML;
    }

    @Override
    public File getFile() {
        return new File(
                VelocityFeatures.getFileUtils().getConfigDir("TNTPouch"),
                "pouches.yml"
        );
    }

    @Override
    public boolean isStatsConfig() {
        return false;
    }

}
