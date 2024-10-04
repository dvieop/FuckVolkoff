package FuckParty.fuckVolkoff.src.main.java.xyz.velocity.modules.pets.config.saves;

import lombok.Getter;

@Getter
public class PetStats {

    String name;
    int level;
    int xp;
    int xpToLevelUp;
    double xpBoost;

    public PetStats(String name, int level, int xp, int xpToLevelUp, double xpBoost) {
        this.name = name;
        this.level = level;
        this.xp = xp;
        this.xpToLevelUp = xpToLevelUp;
        this.xpBoost = xpBoost;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public void setXp(int xp) {
        this.xp = xp;
    }

    public void setXpToLevelUp(int xpToLevelUp) {
        this.xpToLevelUp = xpToLevelUp;
    }

}
