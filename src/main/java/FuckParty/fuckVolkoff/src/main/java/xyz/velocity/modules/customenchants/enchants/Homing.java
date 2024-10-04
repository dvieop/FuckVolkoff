package FuckParty.fuckVolkoff.src.main.java.xyz.velocity.modules.customenchants.enchants;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import org.bukkit.Effect;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import xyz.velocity.VelocityFeatures;
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
public class Homing extends AbstractEnchant {

    private final CustomEnchantConfig config;

    public Homing() {
        this.config = CustomEnchantConfig.getInstance();

        EnchantSave enchant = new EnchantSave(true, "Homing", "&cHoming", Arrays.asList("&7Arrows will look for a nearby players and follow them!"), "BOW", "HOMING:<level>", 3, 3, 20, 1, false, this.extraInfo());

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
        return "Homing";
    }

    @Override
    public boolean isVanillaEnchant() {
        return false;
    }

    @Override
    public String getEnchant() {
        return "HOMING";
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

        // Stole this shit off some kid gg

        double minAngle = 6.283185307179586D;
        Entity minEntity = null;

        double chance = EnchantUtil.getRandomDouble();
        double enchantInformation = getEnchantInformation(player);

        if (chance < enchantInformation) {
            EnchantProcEvent procEvent = CustomEnchants.getInstance().callEvent(player, null, this);
            if (procEvent.isCancelled()) return;

            for (Entity entity : player.getNearbyEntities(32.0D, 32.0D, 32.0D)) {
                if ((player.hasLineOfSight(entity)) && ((entity instanceof LivingEntity)) && (!entity.isDead())) {
                    if (entity instanceof Player && CustomEnchants.getInstance().isAlly(player, (Player) entity)) continue;
                    if (!CustomEnchants.getInstance().canDamage(player, entity)) continue;

                    Vector toTarget = entity.getLocation().toVector().clone().subtract(player.getLocation().toVector());

                    double angle = e.getProjectile().getVelocity().angle(toTarget);

                    if (angle < minAngle) {
                        minAngle = angle;
                        minEntity = entity;
                    }
                }
            }

            if (minEntity != null) {
                new HomingTask((Arrow) e.getProjectile(), (LivingEntity) minEntity);

                procEvent.activationMessage();
            }
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

class HomingTask extends BukkitRunnable {

    private static final double MaxRotationAngle = 0.12D;
    private static final double TargetSpeed = 1.4D;

    Arrow arrow;
    LivingEntity target;

    public HomingTask(Arrow arrow, LivingEntity target) {
        this.arrow = arrow;
        this.target = target;

        runTaskTimer(VelocityFeatures.getInstance(), 1L, 1L);
    }

    public void run() {

        double speed = this.arrow.getVelocity().length();

        if ((this.arrow.isOnGround()) || (this.arrow.isDead()) || (this.target.isDead())) {
            cancel();
            return;
        }

        Vector toTarget = this.target.getLocation().clone().add(new Vector(0.0D, 0.5D, 0.0D)).subtract(this.arrow.getLocation()).toVector();
        Vector dirVelocity = this.arrow.getVelocity().clone().normalize();
        Vector dirToTarget = toTarget.clone().normalize();

        double angle = dirVelocity.angle(dirToTarget);
        double newSpeed = 0.9D * speed + 0.14D;

        if (((this.target instanceof Player)) && (this.arrow.getLocation().distance(this.target.getLocation()) < 8.0D)) {
            Player player = (Player) this.target;

            if (player.isBlocking()) {
                newSpeed = speed * 0.6D;
            }
        }

        Vector newVelocity;

        if (angle < 0.12D) {
            newVelocity = dirVelocity.clone().multiply(newSpeed);
        }
        else {
            Vector newDir = dirVelocity.clone().multiply((angle - 0.12D) / angle).add(dirToTarget.clone().multiply(0.12D / angle));
            newDir.normalize();
            newVelocity = newDir.clone().multiply(newSpeed);
        }

        this.arrow.setVelocity(newVelocity.add(new Vector(0.0D, 0.03D, 0.0D)));
        //this.arrow.getWorld().playEffect(this.arrow.getLocation(), Effect.SMOKE, 0);
    }
}
