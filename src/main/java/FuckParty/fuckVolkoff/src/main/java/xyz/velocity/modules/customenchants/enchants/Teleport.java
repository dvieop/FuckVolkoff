package FuckParty.fuckVolkoff.src.main.java.xyz.velocity.modules.customenchants.enchants;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.ProjectileHitEvent;
import xyz.velocity.modules.customenchants.CustomEnchants;
import xyz.velocity.modules.customenchants.EnchantManager;
import xyz.velocity.modules.customenchants.EnumEnchantType;
import xyz.velocity.modules.customenchants.annotations.Enchant;
import xyz.velocity.modules.customenchants.config.CustomEnchantConfig;
import xyz.velocity.modules.customenchants.config.saves.EnchantSave;
import xyz.velocity.modules.customenchants.events.EnchantProcEvent;
import xyz.velocity.modules.util.EnchantUtil;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;

@Enchant
public class Teleport extends AbstractEnchant {

    private final CustomEnchantConfig config;

    public Teleport() {
        this.config = CustomEnchantConfig.getInstance();

        EnchantSave enchant = new EnchantSave(true, "Teleport", "&cTeleport", Arrays.asList("&7Teleport to your ally by hitting them with an arrow!"), "BOW", "TELEPORT:<level>", 3, 3, 20, 1, false, this.extraInfo());

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
        return "Teleport";
    }

    @Override
    public boolean isVanillaEnchant() {
        return false;
    }

    @Override
    public String getEnchant() {
        return "TELEPORT";
    }

    @Override
    public EnumEnchantType getEnchantType() {
        return EnumEnchantType.PROJECTILE;
    }

    @Override
    public <T extends Event> void runTask(T event) {
        ProjectileHitEvent e;

        try {
            e = (ProjectileHitEvent) event;
        } catch (Throwable err) {
            return;
        }

        Player player = (Player) e.getEntity().getShooter();
        Player violated = null;

        try {
            Entity entity = e.getEntity().getNearbyEntities(1, 1, 1).get(0);

            if (!(entity instanceof Player)) return;

            violated = (Player) entity;
        } catch (Throwable err) {
            return;
        }

        double chance = EnchantUtil.getRandomDouble();
        double enchantInformation = getEnchantInformation(player);

        if (chance < enchantInformation) {
            EnchantProcEvent procEvent = CustomEnchants.getInstance().callEvent(player, violated, this);

            if (procEvent.isCancelled()) return;
            if (!CustomEnchants.getInstance().isAlly(player, violated)) return;

            player.teleport(violated.getLocation());
            procEvent.activationMessage();
        }
    }

    private JsonObject extraInfo() {
        JsonObject info = new JsonObject();

        info.addProperty("chancePerLevel", 2);

        info.toString();

        return info;
    }

    private int getEnchantInformation(Player player) {
        AtomicInteger chance = new AtomicInteger(0);

        Object2ObjectMap<EnchantSave, Integer> getEnchants = CustomEnchants.getInstance().getEnchantsFromPlayer(player);

        getEnchants.forEach((enchant, level) -> {
            if (!enchant.getName().equals(this.getName())) return;

            JsonObject obj = new Gson().fromJson(enchant.getExtra().get(0), JsonObject.class);

            chance.set(obj.get("chancePerLevel").getAsInt() * level);
        });

        return chance.get();
    }

}
