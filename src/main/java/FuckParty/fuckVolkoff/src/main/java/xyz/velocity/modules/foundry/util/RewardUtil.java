package FuckParty.fuckVolkoff.src.main.java.xyz.velocity.modules.foundry.util;

import org.bukkit.inventory.ItemStack;

public class RewardUtil {

    public ItemStack item;
    public String command;
    public int capTime;
    public int credits;

    public RewardUtil(ItemStack item, int capTime, String command, int credits) {
        this.item = item;
        this.command = command;
        this.capTime = capTime;
        this.credits = credits;
    }

}
