package FuckParty.fuckVolkoff.src.main.java.xyz.velocity.modules.customenchants.config;

import com.google.gson.JsonObject;
import dev.lyons.configapi.ConfigAPI;
import dev.lyons.configapi.ConfigClass;
import dev.lyons.configapi.EnumFormattingType;
import dev.lyons.configapi.annotations.Config;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import lombok.Getter;
import lombok.Setter;
import xyz.velocity.VelocityFeatures;
import xyz.velocity.modules.customenchants.config.saves.*;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Config("CustomEnchants")
public class CustomEnchantConfig implements ConfigClass {

    public static CustomEnchantConfig getInstance() {
        return ConfigAPI.getInstance().getConfigObject(CustomEnchantConfig.class);
    }

    @Getter
    @Setter
    @Config
    public boolean enabled = false;

    @Getter
    @Config
    public String enchantAppliedMsg = "You have successfully applied: <enchant>";

    @Getter
    @Config
    public String enchantFailedMsg = "<enchant> has failed to apply";

    @Getter
    @Config
    public String enchantFailedBrokeMsg = "<enchant> has failed and corrupted your item causing it to vanish";

    @Getter
    @Config
    public String defenseProcMsg = "<enchant> has been activated!";

    @Getter
    @Config
    public String attackProcMsg = "<enchant> has been activated on <player>!";

    @Getter
    @Config
    public String enchantSizeLayout = "&9[&b<enchantSize>&9]";

    @Getter
    @Config
    public String cannotApply = "You cannot apply that enchant on this item! Required: <applicable>";

    @Getter
    @Config
    public String notEnoughXP = "You require <required> XP for this! Current: <player_xp>";

    @Getter
    @Config
    public String blackScrollSuccess = "Your scroll has removed the <enchant> enchant";

    @Getter
    @Config
    public String blackScrollFail = "Your scroll has failed";

    @Getter
    @Config
    public String blackScrollFailBreak = "Your scroll has failed and caused your armor to break";

    @Getter
    @Config
    public int previewGuiSlots = 9;

    @Getter
    @Config
    public String previewGuiName = "&cCustom Enchants Preview";

    @Getter
    @Config
    public String previewTierLores = "&7Click to preview list of the <tier_name>&7 enchantments";

    @Getter
    @Config
    public int guiSlots = 9;

    @Getter
    @Config
    public String guiName = "&6&lCustom Enchants";

    @Getter
    @Config
    public List<MainUISave> mainUI = new ArrayList<>(Collections.singleton(new MainUISave("shop", "&cPurchase enchants", "ENCHANTED_BOOK", 3, "Example Lore")));

    @Getter
    @Config
    public int shopGuiSlots = 9;

    @Getter
    @Config
    public String shopGuiName = "&6&lPurchase Custom Enchants";

    @Getter
    @Config
    public int defaultMaxEnchants = 3;

    @Getter
    @Config
    public int maxEnchantsWithUpgrade = 5;

    @Getter
    @Config
    public EnchantChanceSave chance = new EnchantChanceSave(true, true, new ArrayList<>(Collections.singleton(new EnchantItemSave(true, "white_scroll", "&f&lWhite Scroll", Arrays.asList("Put this item on your armor to protect it"), Arrays.asList("** PROTECTED **"), "PAPER", 0, false, new JsonObject()))));

    @Getter
    @Config
    public List<EnchantTierSave> tiersList = new ArrayList<>(Collections.singleton(new EnchantTierSave("ENCHANTED_BOOK", 0, "Tier I", "|Some random lore", 1, 3000, true)));

    @Getter
    @Config
    public List<EnchantSave> enchantList = new ArrayList<>();

    @Override
    public EnumFormattingType getFormatType() {
        return EnumFormattingType.YAML;
    }

    @Override
    public File getFile() {
        return new File(
                VelocityFeatures.getFileUtils().getConfigDir("CustomEnchants"),
                "custom-enchants.yml"
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
