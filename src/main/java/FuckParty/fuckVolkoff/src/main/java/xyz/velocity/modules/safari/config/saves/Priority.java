package FuckParty.fuckVolkoff.src.main.java.xyz.velocity.modules.safari.config.saves;

import lombok.Getter;

@Getter
public class Priority {

    int priority;
    String permission;
    boolean mobsRequirement;
    int mobsToKill;
    boolean levelRequirement;
    int level;

    public Priority(int priority, String permission, boolean mobsRequirement, int mobsToKill, boolean levelRequirement, int level) {
        this.priority = priority;
        this.permission = permission;
        this.mobsRequirement = mobsRequirement;
        this.mobsToKill = mobsToKill;
    }

}
