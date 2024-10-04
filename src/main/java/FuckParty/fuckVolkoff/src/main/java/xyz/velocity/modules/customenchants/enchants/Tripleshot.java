package FuckParty.fuckVolkoff.src.main.java.xyz.velocity.modules.customenchants.enchants;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.util.Vector;
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
public class Tripleshot extends AbstractEnchant {

    private final CustomEnchantConfig config;

    public Tripleshot() {
        this.config = CustomEnchantConfig.getInstance();

        EnchantSave enchant = new EnchantSave(true, "Tripleshot", "&cTripleshot", Arrays.asList("&7Shoow multiple arrows at once!"), "BOW", "TRIPLESHOT:<level>", 3, 3, 20, 1, false, this.extraInfo());

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
        return "Tripleshot";
    }

    @Override
    public boolean isVanillaEnchant() {
        return false;
    }

    @Override
    public String getEnchant() {
        return "TRIPLESHOT";
    }

    @Override
    public EnumEnchantType getEnchantType() {
        return EnumEnchantType.PROJECTILE;
    }

    @Override
    public <T extends Event> void runTask(T event) {
        EntityShootBowEvent e;

        try {
            e = (EntityShootBowEvent) event;
        } catch (Throwable err) {
            return;
        }

        Player player = (Player) e.getEntity();

        double chance = EnchantUtil.getRandomDouble();
        double enchantInformation = getEnchantInformation(player);

        if (chance < enchantInformation) {
            EnchantProcEvent procEvent = CustomEnchants.getInstance().callEvent(player, null, this);

            if (procEvent.isCancelled()) return;

            Arrow original = (Arrow) e.getProjectile();

            spawnArrow(player, rotateYAxis(original.getVelocity(), 0), e);
            spawnArrow(player, rotateYAxis(original.getVelocity(), -30), e);
            spawnArrow(player, rotateYAxis(original.getVelocity(), 30), e);

            procEvent.activationMessage();
        }
    }

    private void spawnArrow(Player player, Vector vector, EntityShootBowEvent e) {
        Arrow arrow = player.getWorld().spawn(player.getEyeLocation(), Arrow.class);

        arrow.setShooter(player);
        arrow.setVelocity(vector);
        arrow.setBounce(false);

        e.setProjectile(arrow);
        EnchantManager.nonVanillaEnchants.get("Homing").runTask(e);
    }

    private Vector rotateYAxis(Vector vector, double angle) {
        if (angle == 0.0D) return vector;

        double newAngle = Math.toRadians(angle);
        double x = vector.getX();
        double z = vector.getZ();
        double cos = Math.cos(newAngle);
        double sin = Math.sin(newAngle);

        vector.setX(x * cos + z * (-sin));
        vector.setZ(x * sin + z * cos);

        return vector;
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
