package FuckParty.fuckVolkoff.src.main.java.xyz.velocity.modules.safari.config.saves;

import lombok.Getter;

import java.util.List;

@Getter
public class SafariTierSave {

    String name;
    String displayName;
    int cooldown;
    int priority;
    int maxPerPlayer;
    int amountOfRewards;
    HologramSave hologram;
    List<String> location;
    PriorityRewardSave rewards;
    int mobsToSpawn;
    List<MobSave> mobs;

    public SafariTierSave(String name, String displayName, int cooldown, int priority, HologramSave hologram, int maxPerPlayer, List<String> location, int amountOfRewards, PriorityRewardSave rewards, int mobsToSpawn, List<MobSave> mobs) {
        this.name = name;
        this.displayName = displayName;
        this.cooldown = cooldown;
        this.priority = priority;
        this.hologram = hologram;
        this.maxPerPlayer = maxPerPlayer;
        this.location = location;
        this.amountOfRewards = amountOfRewards;
        this.rewards = rewards;
        this.mobsToSpawn = mobsToSpawn;
        this.mobs = mobs;
    }

}
