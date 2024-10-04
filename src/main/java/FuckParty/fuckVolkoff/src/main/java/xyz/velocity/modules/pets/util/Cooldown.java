package FuckParty.fuckVolkoff.src.main.java.xyz.velocity.modules.pets.util;

public class Cooldown {

    String type;
    long cooldown = 0l;

    public Cooldown(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public long getCooldown() {
        return cooldown;
    }

    public boolean isOnCooldown() {
        return cooldown > System.currentTimeMillis();
    }

    public void setCooldown(long cooldown) {
        this.cooldown = cooldown;
    }

}
