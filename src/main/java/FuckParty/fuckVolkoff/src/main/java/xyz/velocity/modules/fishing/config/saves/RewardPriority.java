package FuckParty.fuckVolkoff.src.main.java.xyz.velocity.modules.fishing.config.saves;

import lombok.Getter;

import java.io.Serializable;
import java.util.List;

@Getter
public class RewardPriority implements Serializable {

    boolean enabled;
    List<Priority> priority;

    public RewardPriority(boolean enabled, List<Priority> priority) {
        this.enabled = enabled;
        this.priority = priority;
    }

}
