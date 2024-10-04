package FuckParty.fuckVolkoff.src.main.java.xyz.velocity.modules.garrison.config.saves;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class GraceSave {

    boolean enabled;
    int minutes;
    boolean announce;
    String graceEnabled;
    String graceDisabled;
    List<String> graceAlertAnnouncement;
    List<Integer> graceAlertTimes = new ArrayList<>();

    public GraceSave(boolean enabled, int minutes, boolean announce, String graceEnabled, String graceDisabled, List<String> graceAlertAnnouncement) {
        this.enabled = enabled;
        this.minutes = minutes;
        this.announce = announce;
        this.graceEnabled = graceEnabled;
        this.graceDisabled = graceDisabled;
        this.graceAlertAnnouncement = graceAlertAnnouncement;
    }

}
