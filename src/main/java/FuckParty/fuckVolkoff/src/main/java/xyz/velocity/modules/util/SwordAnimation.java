package FuckParty.fuckVolkoff.src.main.java.xyz.velocity.modules.util;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.Vector;

public class SwordAnimation {

    private Player player;
    public ArmorStand sword;
    public BukkitRunnable runnable;

    public SwordAnimation(Player player) {
        this.player = player;
    }

    public void start(JavaPlugin plugin) {
        spawnStand();

        runnable = new BukkitRunnable(){

            public void run() {
                sword.setVelocity(new Vector(1, 0, 0));

                EulerAngle a = new EulerAngle(1.4192805702648004, -1.619980118438698, 0.0);

                Location loc = player.getLocation();

                loc.setYaw(1.4192805702648004f);
                loc.setPitch(-1.619980118438698f);
                loc.add(0.87, 2.3, 0);

                sword.setRightArmPose(a);
                sword.teleport(loc);
            }

        };

        runnable.runTaskTimer(plugin, 0L, 1L);
    }

    private void spawnStand() {
        sword = player.getWorld().spawn(player.getLocation().add(0, 2.3, 0), ArmorStand.class);

        sword.setVisible(false);
        sword.setItemInHand(new ItemStack(Material.DIAMOND_SWORD));
        sword.setRightArmPose(new EulerAngle(-200, 0, 0));
        sword.setCustomName("diamondSwordAnimation");
        sword.setCanPickupItems(false);
    }
}
