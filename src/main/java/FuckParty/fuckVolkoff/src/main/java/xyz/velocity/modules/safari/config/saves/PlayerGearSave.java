package FuckParty.fuckVolkoff.src.main.java.xyz.velocity.modules.safari.config.saves;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class PlayerGearSave {

    String customEnchantKit;
    List<String> commands = new ArrayList<>();

    public PlayerGearSave(String customEnchantKit) {
        this.customEnchantKit = customEnchantKit;
    }

}
