package FuckParty.fuckVolkoff.src.main.java.xyz.velocity.modules.mobarena.config.saves;

import lombok.Getter;

import java.util.List;

@Getter
public class PhaseSave {

    int phase;
    int mobsPerPlayer;
    int maxRound;
    double rewardChanceMultiplier;
    List<MonsterSave> monsters;

    public PhaseSave(int phase, int mobsPerPlayer, int maxRound, double rewardChanceMultiplier, List<MonsterSave> monsters) {
        this.phase = phase;
        this.mobsPerPlayer = mobsPerPlayer;
        this.maxRound = maxRound;
        this.rewardChanceMultiplier = rewardChanceMultiplier;
        this.monsters = monsters;
    }

}
