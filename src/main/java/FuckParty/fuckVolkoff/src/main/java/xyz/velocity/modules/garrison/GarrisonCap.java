package FuckParty.fuckVolkoff.src.main.java.xyz.velocity.modules.garrison;

import com.golfing8.kore.FactionsKore;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import xyz.velocity.VelocityFeatures;
import xyz.velocity.modules.garrison.config.GarrisonConfig;
import xyz.velocity.modules.garrison.config.saves.GraceSave;
import xyz.velocity.modules.util.CapturePoint;
import xyz.velocity.modules.util.Location;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static xyz.velocity.modules.util.OutpostUtil.*;

public class GarrisonCap {

    public GarrisonCap() {
        startCapturePoints();
        instance = this;
    }

    public BukkitTask bukkitTask;
    private boolean hasAnnounced = false;

    private void startCapturePoints() {
        BukkitRunnable bukkitRunnable = new BukkitRunnable() {

            @Override
            public void run() {
                CapturePoint capturePoint = Garrison.getInstance().capturePoint;

                GraceSave graceSave = GarrisonConfig.getInstance().getGarrison().getGraceSave();

                if (graceSave.isEnabled()) {
                    graceAlert(capturePoint);

                    if (Garrison.getInstance().isOnGrace(capturePoint)) {
                        if (!hasAnnounced && graceSave.isAnnounce()) {
                            Bukkit.broadcastMessage(VelocityFeatures.chat(graceSave.getGraceEnabled()));
                            hasAnnounced = true;
                        }
                    } else {
                        if (hasAnnounced && graceSave.isAnnounce()) {
                            Bukkit.broadcastMessage(VelocityFeatures.chat(graceSave.getGraceDisabled()));
                            hasAnnounced = false;
                        }
                    }
                }

                List<Player> playerList = Location.getPlayersInCuboid(capturePoint.getLocation1(), capturePoint.getLocation2()).stream().filter(z -> !z.isDead() && z.isValid()).collect(Collectors.toList());

                /*if (!capturePoint.isNeutral()) {
                    capturePoint.setTotalCaptureTime(capturePoint.getTotalCaptureTime() + 1);
                }*/

                //Garrison.getInstance().updateData(capturePoint);

                if (!capturePoint.isCapturing()) {

                    if (playerList.isEmpty()) {
                        if (capturePoint.isNeutral()) {
                            capturePoint.updatePercentage(-5);
                        }

                        return;
                    }

                    Set<String> factions = getFactions(playerList);

                    if(factions.size() != 1) return;

                    String faction = (String) factions.toArray()[0];

                    capturePoint.setFactionContesting(faction);

                    String factionName = FactionsKore.getIntegration().getTagFromId(faction);

                    if (!capturePoint.getFactionContesting().equals(capturePoint.getFactionOwning())) {
                        broadcastMessage(GarrisonConfig.getInstance().getFactionEntered(), factionName, capturePoint.getGarrison().getChatName());
                    }

                    capturePoint.setCapturing(true);

                }

                if (capturePoint.getFactionContesting() != null) {

                    boolean isStillCapturing = isStillOnCap(playerList, capturePoint);

                    if (isStillCapturing) {

                        double updatePercentage = 0;

                        if (capturePoint.getGarrison().isPlayerStack()) {
                            for (Player p : FactionsKore.getIntegration().getOnlineMembers(capturePoint.getFactionContesting())) {
                                if (playerList.contains(p)) {
                                    updatePercentage += capturePoint.getGarrison().getPercentPerPlayer();
                                }
                            }
                        } else {
                            updatePercentage += capturePoint.getGarrison().getPercentPerPlayer();
                        }

                        if (capturePoint.isNeutral()) {
                            Set<String> factions = getFactions(playerList);

                            if(factions.size() != 1) return;

                            capturePoint.updatePercentage(updatePercentage);
                        }/* else {
                            if (capturePoint.getFactionOwning().equals(capturePoint.getFactionContesting())) {
                                capturePoint.updatePercentage(updatePercentage);
                            } else {
                                capturePoint.updatePercentage(-updatePercentage);
                            }
                        }*/
                    } else {
                        String factionName = FactionsKore.getIntegration().getTagFromId(capturePoint.getFactionContesting());

                        if (!capturePoint.getFactionContesting().equals(capturePoint.getFactionOwning())) {
                            broadcastMessage(GarrisonConfig.getInstance().getFactionLeft(), factionName, capturePoint.getGarrison().getChatName());
                        }

                        capturePoint.setFactionContesting(null);
                        capturePoint.setCapturing(false);
                    }

                    if (capturePoint.getPercentage() >= 100 && capturePoint.isNeutral()) {
                        String factionName = FactionsKore.getIntegration().getTagFromId(capturePoint.getFactionContesting());

                        broadcastMessage(GarrisonConfig.getInstance().getGainedControl(), factionName, capturePoint.getGarrison().getChatName());

                        capturePoint.setFactionOwning(capturePoint.getFactionContesting());
                        capturePoint.setProtectionTime(System.currentTimeMillis() + ((GarrisonConfig.getInstance().getGarrison().getGraceSave().getMinutes() * 60) * 1000L));
                        capturePoint.setNeutral(false);

                        Garrison.getInstance().updateData(capturePoint);
                        Garrison.getInstance().updateProtectionData(capturePoint);

                        for (Player player : capturePoint.getLocation1().getWorld().getPlayers()) {
                            player.teleport(Location.parseToLocation(GarrisonConfig.getInstance().leaveLocation));
                        }
                        //Garrison.getInstance().updateWalls(capturePoint.getGarrison());
                    }

                    /*else if (capturePoint.getPercentage() <= 0 && !capturePoint.isNeutral()) {
                        String factionName = FactionsKore.getIntegration().getTagFromId(capturePoint.getFactionOwning());

                        broadcastMessage(GarrisonConfig.getInstance().getLostControl(), factionName, capturePoint.getGarrison().getChatName());

                        capturePoint.setFactionOwning(null);
                        capturePoint.setNeutral(true);
                        capturePoint.setTotalCaptureTime(0);

                        Garrison.getInstance().resetBoosts();
                        Garrison.getInstance().updateData(capturePoint);
                    }*/

                }

            }
        };

        bukkitTask = bukkitRunnable.runTaskTimer(VelocityFeatures.getInstance(), 0, 20);
    }

    private void graceAlert(CapturePoint capturePoint) {
        if (!Garrison.getInstance().isOnGrace(capturePoint)) return;

        GraceSave graceSave = GarrisonConfig.getInstance().getGarrison().getGraceSave();
        List<Integer> alertTimes = graceSave.getGraceAlertTimes();

        long protectionTime = capturePoint.getProtectionTime();
        int secondsLeft = (int) ((protectionTime - System.currentTimeMillis()) / 1000L);

        if (alertTimes.contains(secondsLeft)) {
            for (String s : graceSave.getGraceAlertAnnouncement()) {
                Bukkit.broadcastMessage(VelocityFeatures.chat(s
                        .replace("<time>", Garrison.getInstance().formatTime(secondsLeft))
                ));
            }
        };
    }

    @Getter
    private static GarrisonCap instance;


}
