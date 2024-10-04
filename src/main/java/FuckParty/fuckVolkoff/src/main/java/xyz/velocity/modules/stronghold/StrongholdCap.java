package FuckParty.fuckVolkoff.src.main.java.xyz.velocity.modules.stronghold;

import com.golfing8.kore.FactionsKore;
import com.massivecraft.factions.*;
import com.massivecraft.factions.zcore.fupgrades.UpgradeType;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import xyz.velocity.VelocityFeatures;
import xyz.velocity.modules.stronghold.config.StrongholdConfig;
import xyz.velocity.modules.util.CapturePoint;
import xyz.velocity.modules.util.Location;
import xyz.velocity.modules.util.OutpostUtil;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class StrongholdCap {

    public StrongholdCap() {
        startCapturePoints();
        instance = this;
    }

    public BukkitTask bukkitTask;

    private void startCapturePoints() {
        BukkitRunnable bukkitRunnable = new BukkitRunnable() {

            @Override
            public void run() {

                for (CapturePoint capturePoint : Stronghold.getInstance().capturePoints) {

                    List<Player> playerList = Location.getPlayersInCuboid(capturePoint.getLocation1(), capturePoint.getLocation2()).stream().filter(z -> !z.isDead() && z.isValid()).collect(Collectors.toList());

                    if (!capturePoint.isNeutral()) {
                        capturePoint.setTotalCaptureTime(capturePoint.getTotalCaptureTime() + 1);
                        capturePoint.setStrongholdWallRegenTime(capturePoint.getWallRegenTime() - 1);
                        capturePoint.setEntityClear(capturePoint.getEntityClear() + 1);
                    }

                    //capturePoint.getHologram().updateHologram();
                    Stronghold.getInstance().updateData(capturePoint);

                    if (!capturePoint.isCapturing()) {

                        if (playerList.isEmpty()) {
                            if (capturePoint.isNeutral()) {
                                capturePoint.updatePercentage(-5);
                            }

                            continue;
                        }

                        Set<String> factions = OutpostUtil.getFactions(playerList);

                        if(factions.size() != 1) continue;

                        String faction = (String) factions.toArray()[0];

                        capturePoint.setFactionContesting(faction);

                        String factionName = FactionsKore.getIntegration().getTagFromId(faction);

                        if (!capturePoint.getFactionContesting().equals(capturePoint.getFactionOwning())) {
                            OutpostUtil.broadcastMessage(StrongholdConfig.getInstance().getFactionEntered(), factionName, capturePoint.getStronghold().getChatName());
                        }

                        capturePoint.setCapturing(true);

                    }

                    if (capturePoint.getFactionContesting() != null) {

                        boolean isStillCapturing = OutpostUtil.isStillOnCap(playerList, capturePoint);

                        if (isStillCapturing) {

                            double updatePercentage = 0;

                            Faction fac = Factions.getInstance().getFactionById(capturePoint.getFactionContesting());

                            int level = fac.getUpgrade(UpgradeType.OUTPOST);

                            if (capturePoint.getStronghold().isPlayerStack()) {
                                for (Player p : FactionsKore.getIntegration().getOnlineMembers(capturePoint.getFactionContesting())) {
                                    if (playerList.contains(p)) {
                                        updatePercentage += capturePoint.getStronghold().getPercentPerPlayer() + (level * 0.1);
                                    }
                                }
                            } else {
                                updatePercentage += capturePoint.getStronghold().getPercentPerPlayer() + (level * 0.1);
                            }

                            if (capturePoint.isNeutral()) {
                                Set<String> factions = OutpostUtil.getFactions(playerList);

                                if(factions.size() != 1) continue;

                                String faction = (String) factions.toArray()[0];

                                if (Stronghold.getInstance().getStrongholdAmount(faction) >= StrongholdConfig.getInstance().strongholdsPerFaction) continue;

                                capturePoint.updatePercentage(updatePercentage);
                            } else {
                                if (capturePoint.getFactionOwning().equals(capturePoint.getFactionContesting())) {
                                    capturePoint.updatePercentage(updatePercentage);
                                } else {
                                    capturePoint.updatePercentage(-updatePercentage);
                                }
                            }
                        } else {
                            String factionName = FactionsKore.getIntegration().getTagFromId(capturePoint.getFactionContesting());

                            if (!capturePoint.getFactionContesting().equals(capturePoint.getFactionOwning())) {
                                OutpostUtil.broadcastMessage(StrongholdConfig.getInstance().getFactionLeft(), factionName, capturePoint.getStronghold().getChatName());
                            }

                            capturePoint.setFactionContesting(null);
                            capturePoint.setCapturing(false);
                        }

                        if (capturePoint.getPercentage() >= 100 && capturePoint.isNeutral()) {
                            String factionName = FactionsKore.getIntegration().getTagFromId(capturePoint.getFactionContesting());

                            OutpostUtil.broadcastMessage(StrongholdConfig.getInstance().getGainedControl(), factionName, capturePoint.getStronghold().getChatName());

                            capturePoint.setFactionOwning(capturePoint.getFactionContesting());
                            capturePoint.setNeutral(false);

                            Stronghold.getInstance().updateData(capturePoint);
                            Stronghold.getInstance().updateWalls(capturePoint.getStronghold());
                            Stronghold.getInstance().giveEffects(capturePoint);
                        }

                        else if (capturePoint.getPercentage() <= 0 && !capturePoint.isNeutral()) {
                            String factionName = FactionsKore.getIntegration().getTagFromId(capturePoint.getFactionOwning());

                            OutpostUtil.broadcastMessage(StrongholdConfig.getInstance().getLostControl(), factionName, capturePoint.getStronghold().getChatName());
                            Stronghold.getInstance().removeEffects(capturePoint);

                            capturePoint.setFactionOwning(null);
                            capturePoint.setNeutral(true);
                            capturePoint.setTotalCaptureTime(0);

                            Stronghold.getInstance().updateData(capturePoint);
                        }

                    }

                }

            }
        };

        bukkitTask = bukkitRunnable.runTaskTimer(VelocityFeatures.getInstance(), 0, 20);
    }

    @Getter
    private static StrongholdCap instance;

}
