package FuckParty.fuckVolkoff.src.main.java.xyz.velocity.modules.util;

import org.bukkit.Color;

public class ColorUtil {

    public static Color getColor(String color) {
        String c = color.toUpperCase();

        switch (c) {
            case "BLUE":
                return Color.BLUE;
            case "RED":
                return Color.RED;
            case "YELLOW":
                return Color.YELLOW;
            case "AQUA":
                return Color.AQUA;
            case "GREEN":
                return Color.GREEN;
            case "LIME":
                return Color.LIME;
            case "ORANGE":
                return Color.ORANGE;
            case "BLACK":
                return Color.BLACK;
            default:
                return Color.WHITE;
        }
    }

}
