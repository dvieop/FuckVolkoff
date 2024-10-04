package FuckParty.fuckVolkoff.src.main.java.xyz.velocity.modules.safari.config.saves;

import lombok.Getter;

import java.util.List;

@Getter
public class RewardPriority {

    boolean enabled;
    List<Priority> priority;

    public RewardPriority(boolean enabled, List<Priority> priority) {
        this.enabled = enabled;
        this.priority = priority;
    }

}