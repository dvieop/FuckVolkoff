package FuckParty.fuckVolkoff.src.main.java.xyz.velocity.modules.foundry.config.saves;

import com.google.gson.JsonArray;
import java.io.Serializable;

public class FoundrySave implements Serializable {

    public String name;
    public String corner1;
    public String corner2;

    public JsonArray rewards = new JsonArray();

    public FoundrySave(String name, String corner1, String corner2) {
        this.name = name;
        this.corner1 = corner1;
        this.corner2 = corner2;

        this.rewards.add(new Reward("DIAMOND_SWORD", "&9Sword", "give <player> diamond_sword 1", "&6test lore||&9Credits: &9<credits>", 9, 120, 10).reward);
        this.rewards.add(new Reward("DIAMOND_HELMET", "&6Helmet", "give <player> diamond_helmet 1", "&btest lore 2|&9Credits: &9<credits>", 11, 160, 50).reward);
    }


}