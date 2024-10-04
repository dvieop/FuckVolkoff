package FuckParty.fuckVolkoff.src.main.java.xyz.velocity.modules.customenchants.enchants;

import com.google.common.util.concurrent.AtomicDouble;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import net.minecraft.server.v1_8_R3.EntityPlayer;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.scheduler.BukkitRunnable;
import xyz.velocity.VelocityFeatures;
import xyz.velocity.modules.customenchants.CustomEnchants;
import xyz.velocity.modules.customenchants.EnchantManager;
import xyz.velocity.modules.customenchants.EnumEnchantType;
import xyz.velocity.modules.customenchants.annotations.Enchant;
import xyz.velocity.modules.customenchants.config.CustomEnchantConfig;
import xyz.velocity.modules.customenchants.config.saves.EnchantSave;
import xyz.velocity.modules.customenchants.events.EnchantProcEvent;
import xyz.velocity.modules.util.EnchantUtil;
import xyz.velocity.modules.util.Pair;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Enchant
public class Permafrost extends AbstractEnchant {

    private final CustomEnchantConfig config;

    public Permafrost() {
        this.config = CustomEnchantConfig.getInstance();

        EnchantSave enchant = new EnchantSave(true, "Permafrost", "&aPermafrost", Arrays.asList("&7Block up your opponent in an ice cage!"), "BOW", "PERMAFROST:<level>", 2, 5, 50, 1, false, this.extraInfo());

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
        return "Permafrost";
    }

    @Override
    public boolean isVanillaEnchant() {
        return false;
    }

    @Override
    public String getEnchant() {
        return "PERMAFROST";
    }

    @Override
    public EnumEnchantType getEnchantType() {
        return EnumEnchantType.PROJECTILE;
    }

    @Override
    public <T extends Event> void runTask(T event) {
        EntityDamageByEntityEvent e;

        try {
            e = (EntityDamageByEntityEvent) event;
        } catch (Throwable err) {
            return;
        }

        Player violator = (Player) (((Arrow) e.getDamager()).getShooter());
        Player violated = (Player) e.getEntity();

        double chance = EnchantUtil.getRandomDouble();
        Pair<Integer, Double> enchantInformation = getEnchantInformation(violator);

        if (chance < enchantInformation.second) {
            EnchantProcEvent procEvent = CustomEnchants.getInstance().callEvent(violator, violated, this);
            if (procEvent.isCancelled()) return;

            spawnIceCage(violated, enchantInformation.first);

            procEvent.activationMessage();
        }

    }

    public void spawnIceCage(Player player, int duration) {
        List<Block> blockList = new ArrayList<>();
        Location loc = player.getLocation();

        Block floor = player.getWorld().getBlockAt(loc.clone().add(0, -1, 0));
        Block roof = player.getWorld().getBlockAt(loc.clone().add(0, 2, 0));
        Block left = player.getWorld().getBlockAt(loc.clone().add(1, 1, 0));
        Block right = player.getWorld().getBlockAt(loc.clone().add(-1, 1, 0));
        Block front = player.getWorld().getBlockAt(loc.clone().add(0, 1, 1));
        Block back = player.getWorld().getBlockAt(loc.clone().add(0, 1, -1));

        blockList.add(floor);
        blockList.add(roof);
        blockList.add(left);
        blockList.add(right);
        blockList.add(front);
        blockList.add(back);

        for (Block block : blockList) {
            if (block.getType() == Material.AIR) {
                block.setType(Material.ICE);
            }
        }

        BukkitRunnable bukkitRunnable = new BukkitRunnable() {

            @Override
            public void run() {
                for (Block block : blockList) {
                    if (block.getType() == Material.ICE) {
                        block.setType(Material.AIR);
                    }
                }
            }

        };

        bukkitRunnable.runTaskLater(VelocityFeatures.getInstance(), duration * 20);
    }

    private JsonObject extraInfo() {
        JsonObject info = new JsonObject();

        info.addProperty("durationPerLevel", 10);
        info.addProperty("chancePerLevel", 5);

        info.toString();

        return info;
    }

    private Pair<Integer, Double> getEnchantInformation(Player player) {
        EntityPlayer entityPlayer = ((CraftPlayer) player).getHandle();

        AtomicInteger duration = new AtomicInteger(0);
        AtomicDouble chance = new AtomicDouble(0);

        Object2ObjectMap<EnchantSave, Integer> toReturn = CustomEnchants.getInstance().getEnchantsFromPlayer(player);

        toReturn.forEach((enchant, level) -> {
            if (!enchant.getName().equals(this.getName())) return;
            JsonObject obj = new Gson().fromJson(enchant.getExtra().get(0), JsonObject.class);

            duration.set(obj.get("durationPerLevel").getAsInt() * level);
            chance.set(obj.get("chancePerLevel").getAsDouble() * level);
        });

        return new Pair<>(duration.get(), chance.get());
    }

}
