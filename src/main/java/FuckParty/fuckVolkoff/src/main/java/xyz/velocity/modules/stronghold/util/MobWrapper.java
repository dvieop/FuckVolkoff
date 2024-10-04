package FuckParty.fuckVolkoff.src.main.java.xyz.velocity.modules.stronghold.util;

import xyz.velocity.modules.stronghold.config.saves.MobSave;

public class MobWrapper {

    String stronghold;
    MobSave mobSave;
    int level;

    public MobWrapper(String stronghold, MobSave mobSave, int level) {
        this.stronghold = stronghold;
        this.mobSave = mobSave;
        this.level = level;
    }

    public String getStronghold() {
        return stronghold;
    }

    public MobSave getMobSave() {
        return mobSave;
    }

    public int getLevel() {
        return level;
    }

}
