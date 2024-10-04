package FuckParty.fuckVolkoff.src.main.java.xyz.velocity.modules.stronghold.config.saves;

import lombok.Getter;

import java.io.Serializable;
import java.util.List;

@Getter
public class StrongholdSave implements Serializable {

    String name;
    int slot;
    String chatName;
    String displayName;
    String displayItem;
    List<String> lore;
    String corner1;
    String corner2;
    String hologram;
    List<String> hologramLore;
    boolean playerStack;
    double percentPerPlayer;
    List<String> potionEffects;
    List<String> customEffects;
    List<String> commands;
    List<MobSave> mob;
    WallRegionSave wallRegions;

    public StrongholdSave(String name, int slot, String chatName, String displayName, String displayItem, List<String> lore, String corner1, String corner2, String hologram, List<String> hologramLore, boolean playerStack, double percentPerPlayer, List<String> potionEffects, List<String> customEffects, List<String> commandsInterval, List<MobSave> mob, WallRegionSave walllRegions) {
        this.name = name;
        this.slot = slot;
        this.chatName = chatName;
        this.displayName = displayName;
        this.displayItem = displayItem;
        this.lore = lore;
        this.corner1 = corner1;
        this.corner2 = corner2;
        this.hologram = hologram;
        this.hologramLore = hologramLore;
        this.playerStack = playerStack;
        this.percentPerPlayer = percentPerPlayer;
        this.potionEffects = potionEffects;
        this.customEffects = customEffects;
        this.commands = commandsInterval;
        this.mob = mob;
        this.wallRegions = walllRegions;
    }

}
