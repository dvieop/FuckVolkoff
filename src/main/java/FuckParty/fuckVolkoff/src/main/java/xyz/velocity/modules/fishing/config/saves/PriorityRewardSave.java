package FuckParty.fuckVolkoff.src.main.java.xyz.velocity.modules.fishing.config.saves;

import lombok.Getter;

import java.util.List;

@Getter
public class PriorityRewardSave {

    int priority;
    double specialRewardChance;
    List<String> fishesId;
    List<SpecialRewardSave> specialRewards;

    public PriorityRewardSave(int priority, double specialRewardChance, List<String> fishesId, List<SpecialRewardSave> specialRewards) {
        this.priority = priority;
        this.specialRewardChance = specialRewardChance;
        this.fishesId = fishesId;
        this.specialRewards = specialRewards;
    }

}
