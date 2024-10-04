package FuckParty.fuckVolkoff.src.main.java.xyz.velocity.modules.safari.config.saves;

import lombok.Getter;

import java.util.List;

@Getter
public class HologramSave {

    String title;
    List<String> cooldownLore;
    List<String> ongoingLore;
    List<String> availableLore;
    List<String> rewardAvailableLore;

    public HologramSave(String title, List<String> cooldownLore, List<String> ongoingLore, List<String> availableLore, List<String> rewardAvailableLore) {
        this.title = title;
        this.cooldownLore = cooldownLore;
        this.ongoingLore = ongoingLore;
        this.availableLore = availableLore;
        this.rewardAvailableLore = rewardAvailableLore;
    }

}
