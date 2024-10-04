package FuckParty.fuckVolkoff.src.main.java.xyz.velocity.modules.customenchants.config.saves;

import lombok.Getter;
import org.checkerframework.checker.units.qual.A;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Getter
public class KitSave implements Serializable {

    String name;
    //int maxEnchants;
    //boolean randomLevels;
    List<Item> items = new ArrayList<>();
    List<CommandSave> commands = new ArrayList<>();

    public KitSave(String name, List<Item> items) {
        this.name = name;
        //this.maxEnchants = maxEnchants;
        //this.randomLevels = randomLevels;
        this.items = items;
        this.commands.add(new CommandSave(20, "pets give <player> lion 1"));
    }

    /*public int getMaxEnchants() {
        return maxEnchants;
    }

    public boolean isRandomLevels() {
        return randomLevels;
    }*/

}
