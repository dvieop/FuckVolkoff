package FuckParty.fuckVolkoff.src.main.java.xyz.velocity.modules.pets.config.saves;

import lombok.Getter;

import java.util.List;

@Getter
public class PetTierSave {

    int tier;
    String xpRequirement;
    int levelLimit;
    List<XpSave> xpGains;

    public PetTierSave(int tier, String xpRequirement, List<XpSave> xpGains, int levelLimit) {
        this.tier = tier;
        this.xpRequirement = xpRequirement;
        this.xpGains = xpGains;
        this.levelLimit = levelLimit;
    }

}
