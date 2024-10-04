package FuckParty.fuckVolkoff.src.main.java.xyz.velocity;

import de.slikey.effectlib.EffectManager;
import dev.lyons.configapi.ConfigAPI;
import lombok.Getter;
import lombok.SneakyThrows;
import org.apache.commons.io.IOUtils;
import org.bukkit.ChatColor;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import xyz.velocity.bukkitutils.colors.ColorCoding;
import xyz.velocity.bukkitutils.files.BukkitFileUtils;
import xyz.velocity.bukkitutils.listeners.AutoLoadingListeners;
import xyz.velocity.commands.CommandAPI;
import xyz.velocity.commands.main.VelocityFeaturesCommand;
import xyz.velocity.commands.util.AutoLoadingCommands;
import xyz.velocity.modules.ModuleManager;
import xyz.velocity.modules.Placeholder;
import xyz.velocity.modules.armorsets.ArmorSets;
import xyz.velocity.modules.masks.Masks;
import xyz.velocity.modules.mining.Mining;
import xyz.velocity.modules.pets.Pets;
import xyz.velocity.modules.safari.config.RewardConfig;
import xyz.velocity.modules.stronghold.Stronghold;
import xyz.velocity.modules.util.ConfigUtil;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.*;
import java.nio.charset.StandardCharsets;

public class VelocityFeatures extends JavaPlugin {

    @Getter
    private static BukkitFileUtils fileUtils = null;

    @Getter
    private static VelocityFeatures instance = null;

    public VelocityFeatures() {
        fileUtils = new BukkitFileUtils(this);
        instance = this;
    }

    public EffectManager effectManager;

    public void loadAllListeners(){
        AutoLoadingListeners.loadListeners(this);
    }

    public void loadAllCommands(){
        AutoLoadingCommands.loadCommands();
    }

    @SneakyThrows
    @Override
    public void onEnable() {

        /*URL check = new URL("http://checkip.amazonaws.com/");

        BufferedReader br = new BufferedReader(new InputStreamReader(check.openStream()));

        String site = "https://velocitydev.xyz/pluginauth/index.php?ip=" + br.readLine();

        URL url = new URL(site);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        String received = IOUtils.toString(connection.getInputStream(), StandardCharsets.UTF_8);

        if (received.equals("Success")) {*/
            ConfigUtil.loadAllConfigs();
            ConfigUtil.saveConfigTask();

            CommandAPI.getInstance().enableCommand(new VelocityFeaturesCommand());

            new ModuleManager();

            Placeholder placeholder = new Placeholder();
            placeholder.register();

            effectManager = new EffectManager(this);
        //}

    }

    @Override
    public void onDisable() {
        //RewardConfig.getInstance().getRewardCache().clear();

        ConfigUtil.saveAll();

        Masks.getInstance().forceUnequipMasks();
        ArmorSets.getInstance().forceUnequipSets();
        Pets.getInstance().forceUnequipPets();
        Stronghold.getInstance().clearMobs();
        Mining.getInstance().resetBlocks();

        effectManager.dispose();
    }

    public static String chat(String message) {
        return ColorCoding.colorCode(message);
    }

    public static void registerEvent(Listener listener) {
        instance.getServer().getPluginManager().registerEvents(listener, instance);
    }

    public static void unregisterEvent(Listener listener) {
        HandlerList.unregisterAll(listener);
    }

}
