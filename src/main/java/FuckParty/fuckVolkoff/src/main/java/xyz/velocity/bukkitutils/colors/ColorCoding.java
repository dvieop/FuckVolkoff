package FuckParty.fuckVolkoff.src.main.java.xyz.velocity.bukkitutils.colors;

import org.bukkit.ChatColor;

public class ColorCoding {

	public static String colorCode(String string) {
		if (!string.contains("&")) return string;
		return string.replaceAll("&0", codes[0]).replaceAll("&1", codes[1]).replaceAll("&2", codes[2]).replaceAll("&3", codes[3]).replaceAll("&4", codes[4]).replaceAll("&5", codes[5]).replaceAll("&6", codes[6]).replaceAll("&7", codes[7]).replaceAll("&8", codes[8]).replaceAll("&9", codes[9]).replaceAll("&a", codes[10]).replaceAll("&b", codes[11]).replaceAll("&c", codes[12]).replaceAll("&d", codes[13]).replaceAll("&e", codes[14]).replaceAll("&f", codes[15]).replaceAll("&l", codes[16]).replaceAll("&o", codes[17]).replaceAll("&k", codes[18]).replaceAll("&r", codes[19]).replaceAll("&n", codes[20]).replaceAll("&m", codes[21]);
	}

	private static final String[] codes = new String[]{ChatColor.BLACK.toString(), ChatColor.DARK_BLUE.toString(), ChatColor.DARK_GREEN.toString(), ChatColor.DARK_AQUA.toString(), ChatColor.DARK_RED.toString(), ChatColor.DARK_PURPLE.toString(), ChatColor.GOLD.toString(), ChatColor.GRAY.toString(), ChatColor.DARK_GRAY.toString(), ChatColor.BLUE.toString(), ChatColor.GREEN.toString(), ChatColor.AQUA.toString(), ChatColor.RED.toString(), ChatColor.LIGHT_PURPLE.toString(), ChatColor.YELLOW.toString(), ChatColor.WHITE.toString(), ChatColor.BOLD.toString(), ChatColor.ITALIC.toString(), ChatColor.MAGIC.toString(), ChatColor.RESET.toString(), ChatColor.UNDERLINE.toString(), ChatColor.STRIKETHROUGH.toString()};


}
