package FuckParty.fuckVolkoff.src.main.java.xyz.velocity.modules.customenchants.enchants;

import com.google.common.util.concurrent.AtomicDouble;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerExpChangeEvent;
import xyz.velocity.modules.customenchants.EnumEnchantType;
import xyz.velocity.modules.customenchants.config.CustomEnchantConfig;
import xyz.velocity.modules.customenchants.config.saves.EnchantSave;
import xyz.velocity.modules.customenchants.CustomEnchants;
import xyz.velocity.modules.customenchants.EnchantManager;
import xyz.velocity.modules.customenchants.annotations.Enchant;
import xyz.velocity.modules.util.EnchantUtil;

import java.util.Arrays;

@Enchant
public class Inquisitive extends AbstractEnchant {

    private final CustomEnchantConfig config;

    public Inquisitive() {
        this.config = CustomEnchantConfig.getInstance();

        EnchantSave enchant = new EnchantSave(true, "Inquisitive", "&cInquisitive", Arrays.asList("&7Gain more xp!"), "SWORD", "INQUISITIVE:<level>", 3, 3, 50, 1, false, this.extraInfo());

        if (!config.getEnchantList().stream().anyMatch(obj -> obj.getName().equals(enchant.getName()))) {
            config.getEnchantList().add(enchant);
        }

        EnchantManager.nonVanillaEnchants.put(this.getName(), this);
    }

    @Override
    public boolean isEnabled() {
        return CustomEnchantConfig.getInstance().getEnchantList().stream().filter(obj -> obj.getName().equals(this.getName())).findFirst().get().isEnabled();
    }

    @Override
    public String getName() {
        return "Inquisitive";
    }

    @Override
    public boolean isVanillaEnchant() {
        return false;
    }

    @Override
    public String getEnchant() {
        return null;
    }

    @Override
    public EnumEnchantType getEnchantType() {
        return EnumEnchantType.GRINDING;
    }

    @Override
    public <T extends Event> void runTask(T event) {
        PlayerExpChangeEvent e;

        try {
            e = (PlayerExpChangeEvent) event;
        } catch (Throwable err) {
            return;
        }

        Player p = e.getPlayer();
        double chance = EnchantUtil.getRandomDouble();
        double getInfo = getEnchantInformation(p);

        if (chance < getInfo) {
            e.setAmount(e.getAmount() * 2);
        }
    }

    private JsonObject extraInfo() {
        JsonObject info = new JsonObject();

        info.addProperty("chancePerLevel", 7);

        info.toString();

        return info;
    }

    private double getEnchantInformation(Player player) {

        AtomicDouble chance = new AtomicDouble(0);

        Object2ObjectMap<EnchantSave, Integer> getEnchants = CustomEnchants.getInstance().getEnchantsFromPlayer(player);

        getEnchants.forEach((enchant, level) -> {
            if (!enchant.getName().equals(this.getName())) return;

            JsonObject obj = new Gson().fromJson(enchant.getExtra().get(0), JsonObject.class);

            chance.set(obj.get("chancePerLevel").getAsDouble() * level);
        });

        return chance.get();
    }

}
