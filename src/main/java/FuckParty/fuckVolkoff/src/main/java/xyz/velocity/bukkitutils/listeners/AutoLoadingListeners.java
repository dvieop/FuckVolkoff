package FuckParty.fuckVolkoff.src.main.java.xyz.velocity.bukkitutils.listeners;

import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.reflections.Reflections;

public final class AutoLoadingListeners {

	public static void loadListeners(final JavaPlugin instance) {
		try {
			final Reflections reflections = new Reflections();
			for (Class<?> aClass : reflections.getTypesAnnotatedWith(AutoListener.class)) {
				if (Listener.class.isAssignableFrom(aClass)) {
					instance.getServer().getPluginManager().registerEvents((Listener) aClass.newInstance(), instance);
				}
			}
		} catch (final Throwable err) {
			throw new RuntimeException(err);
		}
	}


}
