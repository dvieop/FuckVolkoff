package FuckParty.fuckVolkoff.src.main.java.xyz.velocity.bukkitutils.files;

import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public final class BukkitFileUtils {
	private final JavaPlugin instance;

	public BukkitFileUtils(JavaPlugin instance) {
		this.instance = instance;
	}

	public File getDataDir() {
		final File file = instance.getDataFolder();
		if (!file.exists() || !file.isDirectory()) file.mkdirs();
		return file;
	}

	public File getConfigDir(String subDir) {
		if (!subDir.endsWith("/")) {
			subDir += "/";
		}
		final File file = new File(getDataDir(), subDir);
		if (!file.exists() || !file.isDirectory()) file.mkdirs();
		return file;
	}
}
