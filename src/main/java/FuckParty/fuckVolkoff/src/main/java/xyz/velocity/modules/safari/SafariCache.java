package FuckParty.fuckVolkoff.src.main.java.xyz.velocity.modules.safari;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import xyz.velocity.VelocityFeatures;
import xyz.velocity.modules.safari.config.saves.SafariTierSave;

public class SafariCache {

    SafariTierSave safariTierSave;
    Player starter;
    Location location;
    Hologram hologram;
    long cooldown;
    boolean isActive;
    boolean rewardAvailable;
    ObjectArrayList<LivingEntity> mobEntities = new ObjectArrayList<>();
    int resetTimer = 60;

    public SafariCache(SafariTierSave safariTierSave, Location location) {
        this.safariTierSave = safariTierSave;
        this.location = location;

        this.starter = null;
        this.cooldown = 0L;
        this.isActive = false;
        this.rewardAvailable = false;

        Location hologramLocation = new Location(location.getWorld(), location.getX() + 0.5, location.getY() + 2, location.getZ() + 0.5);

        this.hologram = new Hologram(VelocityFeatures.getInstance(), hologramLocation, this);
    }

    public SafariTierSave getSafariTierSave() {
        return safariTierSave;
    }

    public Player getStarter() {
        return starter;
    }

    public Location getLocation() {
        return location;
    }

    public Hologram getHologram() {
        return hologram;
    }

    public long getCooldown() {
        return cooldown;
    }

    public boolean isActive() {
        return isActive;
    }

    public boolean isRewardAvailable() {
        return rewardAvailable;
    }

    public void setStarter(Player starter) {
        this.starter = starter;
    }

    public void setCooldown(long cooldown) {
        this.cooldown = cooldown;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public void setRewardAvailable(boolean rewardAvailable) {
        this.rewardAvailable = rewardAvailable;
    }

    public boolean cooldownExpired() {
        return System.currentTimeMillis() >= cooldown;
    }

    public String cooldownLeft() {
        return "" + (cooldown - System.currentTimeMillis()) / 1000L;
    }

    public int getResetTimer() {
        return resetTimer;
    }

    public void setResetTimer(int resetTimer) {
        this.resetTimer = resetTimer;
    }

    public boolean tickReset() {
        this.resetTimer--;

        return this.resetTimer == 0;
    }

}
