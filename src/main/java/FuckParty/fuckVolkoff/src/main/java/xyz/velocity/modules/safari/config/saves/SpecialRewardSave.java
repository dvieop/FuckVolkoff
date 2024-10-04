package FuckParty.fuckVolkoff.src.main.java.xyz.velocity.modules.safari.config.saves;

import lombok.Getter;

@Getter
public class SpecialRewardSave {

    String name;
    String displayName;
    double chance;
    String command;
    boolean broadcast;

    public SpecialRewardSave(String name, String displayName, double chance, String command, boolean broadcast) {
        this.name = name;
        this.displayName = displayName;
        this.chance = chance;
        this.command = command;
        this.broadcast = broadcast;
    }

}
