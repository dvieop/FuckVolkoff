package FuckParty.fuckVolkoff.src.main.java.xyz.velocity.modules.foundry.config;

import dev.lyons.configapi.ConfigAPI;
import dev.lyons.configapi.ConfigClass;
import dev.lyons.configapi.EnumFormattingType;
import dev.lyons.configapi.annotations.Config;
import lombok.Getter;
import lombok.Setter;
import xyz.velocity.VelocityFeatures;
import xyz.velocity.modules.foundry.config.saves.FoundrySave;

import java.io.File;

@Config
public class FoundryConfig implements ConfigClass {

    public static FoundryConfig getInstance() {
        return ConfigAPI.getInstance().getConfigObject(FoundryConfig.class);
    }

    @Getter
    @Setter
    @Config
    public boolean enabled = false;

    @Getter
    @Config
    String foundryStart = "&bFoundry has started crafting <item>";

    @Getter
    @Config
    String foundryEnd = "&bFoundry has ended";

    @Getter
    @Config
    String foundryActive = "&9&lFoundry is already active";

    @Getter
    @Config
    String playerEnter = "&9&l<player> has entered the foundry";

    @Getter
    @Config
    String playerLeft = "&9&l<player> has left the foundry";

    @Getter
    @Config
    String foundryFail = "Failed to craft <item> due to nobody capturing";

    @Getter
    @Config
    String gainCreditsFrom = "silverfish";

    @Getter
    @Config
    int creditsPerKill = 1;

    @Getter
    @Config
    String notEnoughCredits = "&e&lYou need <credits> for this";

    @Getter
    @Config
    String creditsBalance = "&c<player>'s credits are: &d<credits>";

    @Getter
    @Config
    String creditsSet = "&c<player>'s credits have been set to: &d<credits>";

    @Getter
    @Config
    String creditsGive = "&c<player> has been given &d<credits> &c<credits>";

    @Getter
    @Config
    String hologramLocation = "81:93:266:world";

    @Getter
    @Config
    String hologramTitle = "&9&eFoundry";

    @Getter
    @Config
    String hologramSub = "&7Time left: &f<time>";

    @Getter
    @Config
    public int guiSize = 27;

    @Getter
    @Config
    public boolean fillEmptyUiSlots = true;

    @Getter
    @Config
    public String fillMaterial = "STAINED_GLASS_PANE";

    @Getter
    @Config
    public int fillMaterialDamage = 15;

    @Getter
    @Config
    FoundrySave foundry = new FoundrySave("Foundry", "82:91:265:world", "80:93:267:world");

    @Override
    public EnumFormattingType getFormatType() {
        return EnumFormattingType.YAML;
    }

    @Override
    public File getFile() {
        return new File(
                VelocityFeatures.getFileUtils().getConfigDir("Foundry"),
                "FoundryConfig.yml"
        );
    }

    @Override
    public boolean isStatsConfig() {
        return false;
    }

    public void saveConfig() {
        ConfigAPI.getInstance().saveConfig(this);
    }

}
