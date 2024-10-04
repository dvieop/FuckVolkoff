package FuckParty.fuckVolkoff.src.main.java.xyz.velocity.modules.masks.config;

import dev.lyons.configapi.ConfigAPI;
import dev.lyons.configapi.ConfigClass;
import dev.lyons.configapi.EnumFormattingType;
import dev.lyons.configapi.annotations.Config;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import lombok.Getter;
import lombok.Setter;
import xyz.velocity.VelocityFeatures;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Config("masks")
public class MaskConfig implements ConfigClass {

    public static MaskConfig getInstance() {
        return ConfigAPI.getInstance().getConfigObject(MaskConfig.class);
    }

    @Getter
    @Setter
    @Config
    public boolean enabled = false;

    @Getter
    @Config
    public String attachedLore = "ATTACHED: <mask>";

    @Getter
    @Config
    public String cantApply = "Cant apply <mask> to <item>";

    @Getter
    @Config
    public String alreadyApplied = "You already have <mask> applied to this item";

    @Getter
    @Config
    public String successfullyApplied = "You have applied <mask> to <item>";

    @Getter
    @Config
    public List<MaskSave> masks = new ArrayList<>(Collections.singleton(addExampleMask()));

    @Override
    public EnumFormattingType getFormatType() {
        return EnumFormattingType.YAML;
    }

    @Override
    public File getFile() {
        return new File(
                VelocityFeatures.getFileUtils().getConfigDir("Masks"),
                "mask-config.yml"
        );
    }

    @Override
    public boolean isStatsConfig() {
        return false;
    }

    public void saveConfig() {
        ConfigAPI.getInstance().saveConfig(this);
    }

    private MaskSave addExampleMask() {
        String name = "golem";
        String displayName = "&e&lGolem Mask";
        List<String> lore = Arrays.asList("example lore");
        String material = "SKULL";
        String texture = "";
        List<String> vanillaEffects = Arrays.asList("SPEED:2");
        List<String> customEffects = Arrays.asList("DAMAGE:5");
        MultiMaskSave multiMaskSave = new MultiMaskSave(false, 2, "ATTACHED: <mask>");

        return new MaskSave(1, name, displayName, lore, material, texture, vanillaEffects, customEffects, multiMaskSave, 20);
    }

}
