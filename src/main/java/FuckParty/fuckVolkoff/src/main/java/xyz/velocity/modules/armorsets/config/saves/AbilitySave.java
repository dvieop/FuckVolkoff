package FuckParty.fuckVolkoff.src.main.java.xyz.velocity.modules.armorsets.config.saves;

import lombok.Getter;

@Getter
public class AbilitySave {

    String name;
    String chatName;
    String attackerMessage;
    String defenderMessage;
    double damageMulti;
    double damageReduction;
    double chance;
    int duration;
    int cooldown;
    String setBind;

    public AbilitySave(String name, String chatName, String attackerMessage, String defenderMessage, double damageMulti, double damageReduction, double chance, int duration, int cooldown, String setBind) {
        this.name = name;
        this.chatName = chatName;
        this.attackerMessage = attackerMessage;
        this.defenderMessage = defenderMessage;
        this.damageMulti = damageMulti;
        this.damageReduction = damageReduction;
        this.chance = chance;
        this.duration = duration;
        this.cooldown = cooldown;
        this.setBind = setBind;
    }

}
