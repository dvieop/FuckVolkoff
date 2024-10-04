package FuckParty.fuckVolkoff.src.main.java.xyz.velocity.modules.fishing.config.saves;

import lombok.Getter;

import java.io.Serializable;

@Getter
public class SpecialRewardSave implements Serializable {

    String name;
    String chatName;
    String command;
    double chance;
    boolean broadcast;

    public SpecialRewardSave(String name, String chatName, String command, double chance, boolean broadcast) {
        this.name = name;
        this.chatName = chatName;
        this.command = command;
        this.chance = chance;
        this.broadcast = broadcast;
    }

}
