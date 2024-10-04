package FuckParty.fuckVolkoff.src.main.java.xyz.velocity.modules.infinitekoth.config.saves;

import java.io.Serializable;
import java.util.List;

public class HologramSave implements Serializable {

    public String title;
    public List<String> subtitles;
    public String location;

    public HologramSave(String title, List<String> subtitles, String location) {
        this.title = title;
        this.subtitles = subtitles;
        this.location = location;
    }

}
