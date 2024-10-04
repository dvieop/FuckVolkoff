package FuckParty.fuckVolkoff.src.main.java.xyz.velocity.modules;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;

public class Placeholder extends PlaceholderExpansion {

    @Override
    public String getIdentifier() {
        return "velocity";
    }

    @Override
    public String getAuthor() {
        return "volkoff0";
    }

    @Override
    public String getVersion() {
        return "v1.2";
    }

    @Override
    public boolean canRegister() {
        return true;
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public String onPlaceholderRequest(Player player, String arg) {
        if(player == null || player.getUniqueId() == null) return "";

        if (ModuleManager.placeholders.containsKey(arg)) {
            return ModuleManager.placeholders.get(arg).placeholderRequest(player, arg);
        } else {
            return "";
        }
    }

}
