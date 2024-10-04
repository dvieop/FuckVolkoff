package FuckParty.fuckVolkoff.src.main.java.xyz.velocity.modules.garrison;

import com.golfing8.kore.FactionsKore;
import eu.decentsoftware.holograms.api.DHAPI;
import eu.decentsoftware.holograms.api.holograms.HologramLine;
import eu.decentsoftware.holograms.api.holograms.HologramPage;
import eu.decentsoftware.holograms.api.holograms.enums.HologramLineType;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import xyz.velocity.VelocityFeatures;
import xyz.velocity.modules.garrison.config.GarrisonConfig;
import xyz.velocity.modules.pets.Pets;
import xyz.velocity.modules.safari.config.StatsConfig;
import xyz.velocity.modules.safari.config.saves.StatsSave;
import xyz.velocity.modules.util.SkullUtil;

import java.util.Objects;

public class Hologram {

    eu.decentsoftware.holograms.api.holograms.Hologram hologram;
    public BukkitRunnable runnable;
    private int line = 0;
    private double startY = 0;

    public Hologram(Location location) {
        this.hologram = DHAPI.createHologram("Garrison", location);
        this.startY = location.getY();

        spawnLoreLines();
        spawnSkull();
        animation();
    }

    private void spawnLoreLines() {
        BoostCache boostCache = Garrison.getInstance().getCurrentBoost();

        if (boostCache == null) return;

        for (String s : GarrisonConfig.getInstance().hologram.getLore()) {
            HologramLine hologramLine = DHAPI.addHologramLine(hologram, VelocityFeatures.chat(s
                    .replace("<tier>", boostCache.tier + "")
                    .replace("<status>", boostStatus(boostCache.getBoost().isEnabled()))
                    .replace("<mode>", Garrison.getInstance().mode.name())
                    .replace("<multiplier>", Garrison.getInstance().roundAvoid(boostCache.multiplier, 2) + "")
                    .replace("<tierBar>", Garrison.getInstance().tierBar(boostCache))
                    .replace("<progressBar>", Garrison.getInstance().progressBar(boostCache))
            ));

            hologramLine.setOffsetY(0.5);

            line++;
        }
    }

    private String boostStatus(boolean status) {
        if (status) return "&aEnabled";
        return "&cDisabled";
    }

    private void spawnSkull() {
        String texture = GarrisonConfig.getInstance().hologram.getHeadTexture();

        HologramLine skull = DHAPI.addHologramLine(hologram, SkullUtil.skullItem(texture));

        skull.setType(HologramLineType.SMALLHEAD);
    }

    public void updateLines() {
        BoostCache boostCache = Garrison.getInstance().getCurrentBoost();

        if (boostCache == null) return;

        line = 0;

        for (String s : GarrisonConfig.getInstance().hologram.getLore()) {
            DHAPI.setHologramLine(hologram, line, VelocityFeatures.chat(s
                    .replace("<tier>", boostCache.tier + "")
                    .replace("<mode>", Garrison.getInstance().mode.name())
                    .replace("<status>", boostStatus(boostCache.getBoost().isEnabled()))
                    .replace("<multiplier>", Garrison.getInstance().roundAvoid(boostCache.multiplier, 2) + "")
                    .replace("<tierBar>", Garrison.getInstance().tierBar(boostCache))
                    .replace("<progressBar>", Garrison.getInstance().progressBar(boostCache))
            ));

            Objects.requireNonNull(DHAPI.getHologram(this.hologram.getName())).getPage(0).getLine(line).setOffsetY(0.5);

            line++;
        }
    }

    private void animation() {

        int maxTime = 100;
        final double[] tick = {0};
        runnable = new BukkitRunnable() {

            public void run() {
                if (hologram.getLocation().getY() + 2.5 > startY) hologram.getLocation().setY(startY);
                
                double swingY = Math.sin(tick[0]) * 0.02D;

                tick[0] += 2 * Math.PI / maxTime;
                if(tick[0]  > maxTime) tick[0] = 0;

                DHAPI.moveHologram(hologram.getName(), hologram.getLocation().add(0, swingY, 0));
            }

        };

        runnable.runTaskTimer(VelocityFeatures.getInstance(), 0l, 1l);
    }

    public void deleteHologram() {
        this.runnable.cancel();
        this.hologram.delete();
    }

}
