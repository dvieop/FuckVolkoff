package FuckParty.fuckVolkoff.src.main.java.xyz.velocity.modules.mobarena.config.saves;

import lombok.Getter;
import xyz.velocity.modules.safari.config.saves.SpecialRewardSave;

import java.util.List;

@Getter
public class MobArenaSave {

    String world;
    int maxPlayers;
    int maxMobs;
    int maxRounds;
    List<PhaseSave> phases;
    List<SpecialRewardSave> rewards;

    public MobArenaSave(String world, int maxPlayers, int maxMobs, int maxRounds, List<PhaseSave> phases, List<SpecialRewardSave> rewards) {
        this.world = world;
        this.maxPlayers = maxPlayers;
        this.maxMobs = maxMobs;
        this.maxRounds = maxRounds;
        this.phases = phases;
        this.rewards = rewards;
    }

}
