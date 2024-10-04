package FuckParty.fuckVolkoff.src.main.java.xyz.velocity.modules.stronghold.config.saves;

import lombok.Getter;

@Getter
public class RewardSave {

    String name;
    String displayName;
    String command;
    double chance;
    boolean broadcast;

    public RewardSave(String name, String displayName, String command, double chance, boolean broadcast) {
        this.name = name;
        this.displayName = displayName;
        this.command = command;
        this.chance = chance;
        this.broadcast = broadcast;
    }

}
