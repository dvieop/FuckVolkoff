package FuckParty.fuckVolkoff.src.main.java.xyz.velocity.modules.killtracker.config;

import lombok.Getter;

@Getter
public class StatsSave {

    int kills;
    int deaths;

    public StatsSave(int kills, int deaths) {
        this.kills = kills;
        this.deaths = deaths;
    }

    public void addKill() {
        this.kills += 1;
    }

    public void addDeath() {
        this.deaths += 1;
    }

}
