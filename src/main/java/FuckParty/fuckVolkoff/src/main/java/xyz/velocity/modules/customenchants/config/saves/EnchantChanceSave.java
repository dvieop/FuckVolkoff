package FuckParty.fuckVolkoff.src.main.java.xyz.velocity.modules.customenchants.config.saves;

import lombok.Getter;

import java.util.List;

@Getter
public class EnchantChanceSave {

    boolean enabled;
    boolean breakOnFail;
    List<EnchantItemSave> items;

    public EnchantChanceSave(boolean enabled, boolean breakOnFail, List<EnchantItemSave> items) {
        this.enabled = enabled;
        this.items = items;
    }

}
