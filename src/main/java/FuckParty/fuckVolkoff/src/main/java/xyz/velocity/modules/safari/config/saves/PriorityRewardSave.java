package FuckParty.fuckVolkoff.src.main.java.xyz.velocity.modules.safari.config.saves;

import lombok.Getter;

import java.util.List;

@Getter
public class PriorityRewardSave {

    double specialRewardChance;
    List<String> dropsId;
    List<SpecialRewardSave> specialRewards;

    public PriorityRewardSave(double specialRewardChance, List<String> dropsId, List<SpecialRewardSave> specialRewards) {
        this.specialRewardChance = specialRewardChance;
        this.dropsId = dropsId;
        this.specialRewards = specialRewards;
    }

}
