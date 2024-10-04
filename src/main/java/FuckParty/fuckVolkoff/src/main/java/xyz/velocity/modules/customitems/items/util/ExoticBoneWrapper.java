package FuckParty.fuckVolkoff.src.main.java.xyz.velocity.modules.customitems.items.util;

import org.bukkit.entity.Player;

public class ExoticBoneWrapper {

    Player playerHit;
    int hits;
    long sinceLastHit;

    public ExoticBoneWrapper(Player player, int hits, long time) {
        this.playerHit = player;
        this.hits = hits;
        this.sinceLastHit = time;
    }

    public Player getPlayerHit() {
        return playerHit;
    }

    public int getHits() {
        return hits;
    }

    public long getSinceLastHit() {
        return sinceLastHit;
    }

    public void setPlayerHit(Player player) {
        this.playerHit = player;
    }

    public void setHits(int hits) {
        this.hits = hits;
    }

    public void setSinceLastHit(long sinceLastHit) {
        this.sinceLastHit = sinceLastHit;
    }

}
