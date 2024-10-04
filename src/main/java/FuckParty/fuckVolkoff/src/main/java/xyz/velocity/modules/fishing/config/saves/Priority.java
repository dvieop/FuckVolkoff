package FuckParty.fuckVolkoff.src.main.java.xyz.velocity.modules.fishing.config.saves;

import lombok.Getter;

@Getter
public class Priority {

    int priority;
    int reelTimeTicks;
    String permission;

    public Priority(int priority, int reelTimeTicks, String permission) {
        this.priority = priority;
        this.reelTimeTicks = reelTimeTicks;
        this.permission = permission;
    }

}
