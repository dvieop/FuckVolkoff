package FuckParty.fuckVolkoff.src.main.java.xyz.velocity.modules.customenchants.config.saves;

import lombok.Getter;

@Getter
public class CommandSave {

    double chance;
    String command;

    public CommandSave(double chance, String command) {
        this.chance = chance;
        this.command = command;
    }

}
