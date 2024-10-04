package FuckParty.fuckVolkoff.src.main.java.xyz.velocity.modules.pets;

import com.golfing8.kore.FactionsKore;
import eu.decentsoftware.holograms.api.DHAPI;
import eu.decentsoftware.holograms.api.holograms.HologramLine;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.UUID;

public class PetsRunnable {

    public BukkitRunnable runnable;

    @Getter
    public static PetsRunnable instance;

    public PetsRunnable() {
        instance = this;
    }

    public void start(JavaPlugin plugin) {

        runnable = new BukkitRunnable() {

            public void run() {
                for (Object2ObjectMap.Entry<UUID, PetWrapper> map : Pets.equippedPets.object2ObjectEntrySet()) {
                    Player player = Bukkit.getPlayer(map.getKey());

                    if (player.getGameMode().equals(GameMode.SPECTATOR)) continue;
                    if (FactionsKore.isPlayerVanishedOrInStaff(player)) {
                        Pets.getInstance().unequipPet(player);
                        continue;
                    }

                    Location newLoc = player.getLocation().add(0, 1.75, 0).add(getRightBackHeadDirection(player, -10).multiply(1.25D));
                    newLoc.setY(player.getLocation().getY() + 0.3);

                    try {
                        HologramLine line = DHAPI.getHologram(player.getName()).getPage(0).getLine(1);

                        line.setFacing(player.getLocation().getYaw());

                        DHAPI.moveHologram(player.getName(), newLoc.add(getRightBackHeadDirection(player, 0.15).multiply(0.6D)).add(0, 0.6, 0));
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }
                }
            }

        };

        runnable.runTaskTimer(plugin, 3, 3);
    }


    public static Vector getRightHeadDirection(Player player) {
        Vector direction = player.getLocation().getDirection().normalize();
        return new Vector(-direction.getZ(), 0.0, direction.getX()).normalize();
    }

    public static Vector getRightBackHeadDirection(Player player, double multiply) {
        Vector direction = player.getLocation().getDirection().normalize();
        Vector right = new Vector(-direction.getZ(), 0.0, direction.getX()).normalize();
        Vector back = direction.multiply(multiply);

        return right.add(back).normalize();
    }

}
