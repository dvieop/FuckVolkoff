package FuckParty.fuckVolkoff.src.main.java.xyz.velocity.modules.util;

import com.golfing8.kore.FactionsKore;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import xyz.velocity.VelocityFeatures;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class OutpostUtil {

    public static boolean isStillOnCap(List<Player> playerList, CapturePoint capturePoint) {

        boolean isOnCap = false;

        for (Player p : playerList) {

            String getPlayerFaction = FactionsKore.getIntegration().getPlayerFactionId(p);

            if (capturePoint.getFactionContesting().equals(getPlayerFaction)) {
                isOnCap = true;
                break;
            }

        }

        return isOnCap;

    }

    public static Set<String> getFactions(List<Player> playerList) {
        Set<String> factions = new HashSet<>();

        for(Player player : playerList.stream().filter(z -> FactionsKore.getIntegration().hasFaction(z)).collect(Collectors.toList())){
            factions.add(FactionsKore.getIntegration().getPlayerFactionId(player));
        }

        return factions;
    }

    public static void broadcastMessage(String message, String faction, String outpost) {
        Bukkit.broadcastMessage(VelocityFeatures.chat(message
                .replace("<faction>", faction)
                .replace("<stronghold>", outpost)
                .replace("<garrison>", outpost)
        ));
    }

}
