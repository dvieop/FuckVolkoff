package FuckParty.fuckVolkoff.src.main.java.xyz.velocity.modules.garrison.config.saves;

import lombok.Getter;

@Getter
public class DeathBanSave {

    boolean enabled;
    int duration;
    String warpMessage;

    public DeathBanSave(boolean enabled, int duration, String warpMessage) {
        this.enabled = enabled;
        this.duration = duration;
        this.warpMessage = warpMessage;
    }

}
