package FuckParty.fuckVolkoff.src.main.java.xyz.velocity.modules.foundry.config.saves;

import com.google.gson.JsonObject;

public class Reward {

    public JsonObject reward;

    public Reward(String displayItem, String itemName, String command, String lore, int slot, int time, int credits) {

        JsonObject rewardInfo = new JsonObject();

        rewardInfo.addProperty("displayItem", displayItem);
        rewardInfo.addProperty("itemName", itemName);
        rewardInfo.addProperty("lore", lore);
        rewardInfo.addProperty("slot", slot);
        rewardInfo.addProperty("command", command);
        rewardInfo.addProperty("time", time);
        rewardInfo.addProperty("credits", credits);

        rewardInfo.toString();

        this.reward = rewardInfo;
    }

}
