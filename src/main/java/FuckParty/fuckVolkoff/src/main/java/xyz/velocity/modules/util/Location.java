package FuckParty.fuckVolkoff.src.main.java.xyz.velocity.modules.util;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class Location {

    public static org.bukkit.Location parseToLocation(String string) {
        if (string == null) {
            return null;
        }
        String[] data = string.split(":");
        try {
            double x = Double.parseDouble(data[0]);
            double y = Double.parseDouble(data[1]);
            double z = Double.parseDouble(data[2]);
            World world = Bukkit.getServer().getWorld(data[3]);
            return new org.bukkit.Location(world, x, y, z);
        }
        catch (NumberFormatException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public static List<Player> getPlayersInCuboid(org.bukkit.Location locationOne, org.bukkit.Location locationTwo){
        List<Player> toReturn = new ArrayList<>();

        if(!locationOne.getWorld().equals(locationTwo.getWorld())) return toReturn;

        double minX = Math.min(locationOne.getX(), locationTwo.getX());
        double maxX = Math.max(locationOne.getX(), locationTwo.getX());

        double minY = Math.min(locationOne.getY(), locationTwo.getY());
        double maxY = Math.max(locationOne.getY(), locationTwo.getY());

        double minZ = Math.min(locationOne.getZ(), locationTwo.getZ());
        double maxZ = Math.max(locationOne.getZ(), locationTwo.getZ());

        for (Player player : Bukkit.getOnlinePlayers()){
            if (!player.getWorld().equals(locationOne.getWorld())) continue;

            org.bukkit.Location pLoc = player.getLocation();

            double px = pLoc.getX();

            double py = pLoc.getY();

            double pz = pLoc.getZ();

            if(px >= minX && px <= maxX && py >= minY && py <= maxY && pz >= minZ && pz <= maxZ)toReturn.add(player);
        }

        return toReturn;
    }

    public static String parseToString(org.bukkit.Location location) {
        return (int)location.getX() + ":" + (int)location.getY() + ":" + (int)location.getZ() + ":" + location.getWorld().getName();
    }

    public static String locationToFancyString(org.bukkit.Location location, String primary, String secondary){
        return primary + "X: " + secondary + "" + location.getBlockX() + " " + primary + "Y: " + secondary + "" + location.getBlockY() + " " + primary + "Z: " + secondary + "" + location.getBlockZ() + primary + " World: " + secondary + "" + location.getWorld().getName();
    }

}
