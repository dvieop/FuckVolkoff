package FuckParty.fuckVolkoff.src.main.java.xyz.velocity.modules.safari;

import eu.decentsoftware.holograms.api.DHAPI;
import eu.decentsoftware.holograms.api.holograms.HologramLine;
import lombok.Getter;
import org.bukkit.plugin.Plugin;
import xyz.velocity.VelocityFeatures;
import xyz.velocity.modules.safari.config.saves.HologramSave;

import java.util.LinkedList;
import java.util.List;

public class Hologram {

    @Getter
    private static Hologram instance;

    public eu.decentsoftware.holograms.api.holograms.Hologram hologram = null;

    public LinkedList<HologramLine> lines = new LinkedList<>();

    org.bukkit.Location location;
    Plugin plugin;
    SafariCache safariCache;

    public Hologram(Plugin plugin, org.bukkit.Location location, SafariCache safariCache) {
        this.plugin = plugin;
        this.location = location;
        this.safariCache = safariCache;

        instance = this;
        spawnHologram();
    }

    public void spawnHologram() {

        HologramSave hologramSave = safariCache.safariTierSave.getHologram();

        hologram = DHAPI.createHologram(String.valueOf(hashCode()), this.location);

        lines.add(DHAPI.addHologramLine(hologram, VelocityFeatures.chat(hologramSave.getTitle())));

        for (String s : hologramSave.getAvailableLore()) {
            lines.add(DHAPI.addHologramLine(hologram, VelocityFeatures.chat(s)));
        }

    }

    public void updateHologram(List<String> list) {
        for (int i = 1; i < lines.size(); i++) {
            HologramLine line = lines.get(i);

            line.setText(VelocityFeatures.chat(
                    list.get(i - 1)
                        .replace("<cooldown>", safariCache.cooldownLeft())
                        .replace("<player>", safariCache.starter == null ? "None" : safariCache.starter.getName())
            ));
        }
    }

    public void updateHologram(String type) {
        switch (type) {
            case "available":
                updateHologram(safariCache.safariTierSave.getHologram().getAvailableLore());
                break;
            case "cooldown":
                updateHologram(safariCache.safariTierSave.getHologram().getCooldownLore());
                break;
            case "reward":
                updateHologram(safariCache.safariTierSave.getHologram().getRewardAvailableLore());
                break;
            case "ongoing":
                updateHologram(safariCache.safariTierSave.getHologram().getOngoingLore());
                break;
        }
    }

    public void deleteHologram() {
        this.hologram.delete();
    }

}
