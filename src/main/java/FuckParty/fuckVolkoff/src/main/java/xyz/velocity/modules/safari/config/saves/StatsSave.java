package FuckParty.fuckVolkoff.src.main.java.xyz.velocity.modules.safari.config.saves;

import lombok.Getter;

@Getter
public class StatsSave {

    int level;
    int xp;
    int xpToLevelUp;
    int mobsKilled;
    int mobCounter;
    int chatWins;
    double luckBoost;

    public StatsSave(int level, int xp, int xpToLevelUp, int mobsKilled, int mobCounter, int chatWins, double luckBoost) {
        this.level = level;
        this.xp = xp;
        this.xpToLevelUp = xpToLevelUp;
        this.mobsKilled = mobsKilled;
        this.mobCounter = mobCounter;
        this.chatWins = chatWins;
        this.luckBoost = luckBoost;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public void setXpToLevelUp(int xpToLevelUp) {
        this.xpToLevelUp = xpToLevelUp;
    }

    public void setXp(int xp) {
        this.xp = xp;
    }

    public void setMobsKilled(int mobsKilled) {
        this.mobsKilled = mobsKilled;
    }

    public void setMobCounter(int mobCounter) {
        this.mobCounter = mobCounter;
    }

    public void setChatWins(int chatWins) {
        this.chatWins = chatWins;
    }

    public void setLuckBoost(double luckBoost) {
        this.luckBoost = luckBoost;
    }

}
