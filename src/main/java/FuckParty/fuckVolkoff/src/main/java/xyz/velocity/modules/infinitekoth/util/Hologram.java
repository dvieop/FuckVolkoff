package FuckParty.fuckVolkoff.src.main.java.xyz.velocity.modules.infinitekoth.util;

import eu.decentsoftware.holograms.api.DHAPI;
import eu.decentsoftware.holograms.api.holograms.HologramLine;
import org.bukkit.plugin.Plugin;
import xyz.velocity.VelocityFeatures;
import xyz.velocity.modules.infinitekoth.config.KothConfig;
import xyz.velocity.modules.util.Pair;

import java.util.*;

public class Hologram {

  private static Hologram instance;

  public static Hologram getInstance() {
    return instance;
  }

  public eu.decentsoftware.holograms.api.holograms.Hologram hologram = null;
  public HashMap<String, Pair<String, HologramLine>> lines = new HashMap<>();

  org.bukkit.Location location;
  Plugin plugin;

  public Hologram(Plugin plugin, org.bukkit.Location location) {
    this.plugin = plugin;
    this.location = location;

    instance = this;
  }

  public void spawnHologram() {

    KothConfig config = KothConfig.getInstance();

    hologram = DHAPI.createHologram(String.valueOf(hashCode()), this.location);

    lines.put("title", new Pair<>("title", DHAPI.addHologramLine(hologram, VelocityFeatures.chat(config.getHologram().title))));
    DHAPI.addHologramLine(hologram, "");

    config.getHologram().subtitles.forEach(subtitle -> {
      addToHashMap(DHAPI.addHologramLine(hologram, subtitle));
    });

  }

  private void addToHashMap(HologramLine l) {

    String s = l.getText();

    if (s.contains("<player>")) {
      lines.put("player", new Pair<>(s, l));
    } else if (s.contains("<time_left>")) {
      lines.put("left", new Pair<>(s, l));
    } else if (s.contains("<total_time>")) {
      lines.put("total", new Pair<>(s, l));
    } else if (s.contains("<tier>")) {
      lines.put("tier", new Pair<>(s, l));
    }

    l.setText(VelocityFeatures.chat(
            s.replace("<player>", "None")
                .replace("<time_left>", formatTime(KothConfig.getInstance().getRewardInterval()))
                .replace("<tier>", "1")
                .replace("<total_time>", "0:00")
        )
    );

  }

  public void updateHologram(int secondsLeft, int totalSeconds, int tier, String playerName) {

    String s = formatTime(secondsLeft);

    if (this.lines.containsKey("left")) {
      this.lines.get("left").second.setText(
          VelocityFeatures.chat(this.lines.get("left").first.replace("<time_left>", s)));
    }

    if (this.lines.containsKey("total")) {
      this.lines.get("total").second.setText(VelocityFeatures.chat(
          this.lines.get("total").first.replace("<total_time>", formatTime(totalSeconds))));
    }

    if (this.lines.containsKey("player")) {
      this.lines.get("player").second.setText(
          VelocityFeatures.chat(this.lines.get("player").first.replace("<player>", playerName)));
    }

    if (this.lines.containsKey("tier")) {
      this.lines.get("tier").second.setText(
          VelocityFeatures.chat(this.lines.get("tier").first.replace("<tier>", tier + "")));
    }

  }

  private String formatTime(int n) {
    String time = "";

    int hours = n / 3600;
    int minutes = (n % 3600) / 60;
    n = n % 60;

    time += hours > 0 ? hours + "h " : "";

    return time += minutes + "m " + n + "s";
  }

  public void resetTimer() {

    if (this.lines.containsKey("left")) {
      this.lines.get("left").second.setText(VelocityFeatures.chat(
          this.lines.get("left").first.replace("<time_left>",
              formatTime(KothConfig.getInstance().getRewardInterval()))));
    }

    if (this.lines.containsKey("total")) {
      this.lines.get("total").second.setText(VelocityFeatures.chat(
          this.lines.get("total").first.replace("<total_time>", formatTime(0))));
    }

    if (this.lines.containsKey("player")) {
      this.lines.get("player").second.setText(
          VelocityFeatures.chat(this.lines.get("player").first.replace("<player>", "None")));
    }

    if (this.lines.containsKey("tier")) {
      this.lines.get("tier").second.setText(
          VelocityFeatures.chat(this.lines.get("tier").first.replace("<tier>", "1")));
    }

  }

  public void deleteHologram() {
    this.hologram.delete();
  }

}
