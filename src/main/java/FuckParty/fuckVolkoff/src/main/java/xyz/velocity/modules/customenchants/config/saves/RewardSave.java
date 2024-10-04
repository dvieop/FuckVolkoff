package FuckParty.fuckVolkoff.src.main.java.xyz.velocity.modules.customenchants.config.saves;

import lombok.Getter;

@Getter
public class RewardSave {

    String command;
    int chance;

    public RewardSave(String command, int chance) {
        this.command = command;
        this.chance = chance;
    }

}
