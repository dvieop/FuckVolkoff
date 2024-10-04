package FuckParty.fuckVolkoff.src.main.java.xyz.velocity.modules.infinitekoth.config.saves;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.io.Serializable;

public class RewardSave implements Serializable {

    public JsonArray tiers = new JsonArray();

    public RewardSave(int tierLevel) {

        JsonObject tier = new JsonObject();
        JsonArray rewards = new JsonArray();

        rewards.add(tierRewards("&6&lexample1", "example comand 1"));
        rewards.add(tierRewards("&6&lexample2", "example comand 2"));

        tier.addProperty("tier", tierLevel);
        tier.add("rewards", rewards);

        this.tiers.add(tier);

    }

    public JsonObject tierRewards(String name, String command) {
        JsonObject reward = new JsonObject();

        reward.addProperty("name", name);
        reward.addProperty("command", command);

        return reward;
    }

}
