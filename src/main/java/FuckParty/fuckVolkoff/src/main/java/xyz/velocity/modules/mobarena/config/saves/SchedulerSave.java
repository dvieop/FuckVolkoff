package FuckParty.fuckVolkoff.src.main.java.xyz.velocity.modules.mobarena.config.saves;

import lombok.Getter;

import java.util.List;

@Getter
public class SchedulerSave {

    String timezone;
    List<String> schedules;

    public SchedulerSave(String timezone, List<String> schedules) {
        this.timezone = timezone;
        this.schedules = schedules;
    }

}
