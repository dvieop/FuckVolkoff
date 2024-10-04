package FuckParty.fuckVolkoff.src.main.java.xyz.velocity.modules.stronghold;

import com.golfing8.kore.FactionsKore;
import dev.lyons.configapi.ConfigAPI;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import lombok.Getter;
import net.minecraft.server.v1_8_R3.EntityMonster;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import xyz.velocity.VelocityFeatures;
import xyz.velocity.commands.CommandAPI;
import xyz.velocity.modules.stronghold.commands.StrongholdCommand;
import xyz.velocity.modules.stronghold.config.DataConfig;
import xyz.velocity.modules.stronghold.config.StrongholdConfig;
import xyz.velocity.modules.stronghold.config.saves.DataSave;
import xyz.velocity.modules.stronghold.config.saves.MobSave;
import xyz.velocity.modules.stronghold.config.saves.StrongholdSave;
import xyz.velocity.modules.stronghold.config.saves.WallLocations;
import xyz.velocity.modules.AbstractModule;
import xyz.velocity.modules.Module;
import xyz.velocity.modules.ModuleManager;
import xyz.velocity.modules.stronghold.listeners.StrongholdListener;
import xyz.velocity.modules.util.CapturePoint;
import xyz.velocity.modules.stronghold.util.MobWrapper;
import xyz.velocity.modules.util.EnchantUtil;
import xyz.velocity.modules.util.Pair;

import java.util.*;
import java.util.stream.Collectors;

@Module
public class Stronghold extends AbstractModule {

    @Getter
    private static Stronghold instance;
    public ObjectArrayList<CapturePoint> capturePoints = new ObjectArrayList<>();
    public Object2ObjectMap<StrongholdSave, List<xyz.velocity.modules.util.Block>> blockLocations = new Object2ObjectOpenHashMap<>();
    public ObjectArrayList<MobSpawnInterval> mobSpawnIntervals = new ObjectArrayList<>();
    public ObjectArrayList<EffectsInterval> effectsInterval = new ObjectArrayList<>();
    public Object2ObjectMap<UUID, MobWrapper> spawnedMobs = new Object2ObjectOpenHashMap<>();

    public Stronghold() {
        instance = this;
    }

    public void loadData() {
        capturePoints.clear();
        blockLocations.clear();

        if (!mobSpawnIntervals.isEmpty()) {
            for (MobSpawnInterval mobSpawnInterval : mobSpawnIntervals) {
                try {
                    mobSpawnInterval.bukkitTask.cancel();
                } catch (Throwable e) { }
            }
        }

        if (!effectsInterval.isEmpty()) {
            for (EffectsInterval effectsInterval : effectsInterval) {
                try {
                    effectsInterval.bukkitTask.cancel();
                } catch (Throwable e) { }
            }
        }

        mobSpawnIntervals.clear();
        effectsInterval.clear();

        StrongholdConfig config = StrongholdConfig.getInstance();

        for (StrongholdSave strongholdSave : config.getStrongholds()) {

            CapturePoint capturePoint = new CapturePoint(strongholdSave);

            for (String command : strongholdSave.getCommands()) {
                String[] split = command.split(":");

                capturePoint.addCommand(split[1], Integer.parseInt(split[0]));
            }

            DataSave dataSave = DataConfig.getInstance().getStrongholds().get(strongholdSave.getName());

            if (dataSave != null) {
                capturePoint.setFactionOwning(dataSave.getFaction());
                capturePoint.setPercentage(dataSave.getPercentage());
                capturePoint.setNeutral(dataSave.isNeutral());
                capturePoint.setTotalCaptureTime(dataSave.getCaptureTime());
            }

            //capturePoint.setHologram(new Hologram(VelocityFeatures.getInstance(), xyz.velocity.modules.util.Location.parseToLocation(strongholdSave.getHologram()), capturePoint));

            capturePoints.add(capturePoint);
            updateWalls(strongholdSave);
            addPlaceholders(capturePoint);

            strongholdSave.getMob().forEach(mobSave -> {
                mobSpawnIntervals.add(new MobSpawnInterval(mobSave, capturePoint));
            });

            effectsInterval.add(new EffectsInterval(capturePoint));
        }
    }

    public void unregisterAll() {
        try {
            StrongholdCap.getInstance().bukkitTask.cancel();
        } catch (Throwable e) { }

        try {
            CommandsInterval.getInstance().bukkitTask.cancel();
        } catch (Throwable e) { }

        try {
            for (MobSpawnInterval mobSpawnInterval : mobSpawnIntervals) {
                mobSpawnInterval.bukkitTask.cancel();
            }
        } catch (Throwable e) { }

        try {
            for (EffectsInterval effectsInterval : effectsInterval) {
                effectsInterval.bukkitTask.cancel();
            }
        } catch (Throwable e) { }

        /*for (CapturePoint capturePoint : capturePoints) {
            capturePoint.getHologram().deleteHologram();
        }*/

        VelocityFeatures.unregisterEvent(StrongholdListener.getInstance());
        CommandAPI.getInstance().disableCommand(StrongholdCommand.class);
        StrongholdConfig.getInstance().setEnabled(false);
    }

    public void updateData(CapturePoint capturePoint) {
        DataConfig dataConfig = DataConfig.getInstance();

        if (!dataConfig.getStrongholds().containsKey(capturePoint.getStronghold().getName())) return;

        DataSave dataSave = dataConfig.getStrongholds().get(capturePoint.getStronghold().getName());

        dataSave.setFaction(capturePoint.getFactionOwning());
        dataSave.setPercentage(capturePoint.getPercentage());
        dataSave.setNeutral(capturePoint.isNeutral());
        dataSave.setCaptureTime(capturePoint.getTotalCaptureTime());

        ConfigAPI.getInstance().saveConfig(dataConfig);
    }

    public void updateWalls(StrongholdSave stronghold) {
        if (!stronghold.getWallRegions().isEnabled()) return;

        Material material = Material.getMaterial(stronghold.getWallRegions().getMaterial());

        for (WallLocations region : stronghold.getWallRegions().getRegions()) {

            Location corner1 = xyz.velocity.modules.util.Location.parseToLocation(region.getLocation1());
            Location corner2 = xyz.velocity.modules.util.Location.parseToLocation(region.getLocation2());

            int minX = Math.min(corner1.getBlockX(), corner2.getBlockX());
            int maxX = Math.max(corner1.getBlockX(), corner2.getBlockX());
            int minY = Math.min(corner1.getBlockY(), corner2.getBlockY());
            int maxY = Math.max(corner1.getBlockY(), corner2.getBlockY());
            int minZ = Math.min(corner1.getBlockZ(), corner2.getBlockZ());
            int maxZ = Math.max(corner1.getBlockZ(), corner2.getBlockZ());

            for(int x = minX; x <= maxX; x++) {
                for(int y = minY; y < maxY; y++) {
                    for(int z = minZ; z <= maxZ; z++) {
                        if((x == minX || x == maxX) || (z == minZ || z == maxZ)) {
                            Block b = corner1.getWorld().getBlockAt(x, y, z);
                            if (b.getType() == Material.AIR) {
                                b.setType(material);
                            }

                            if (!blockLocations.containsKey(stronghold)) {
                                blockLocations.put(stronghold, new ArrayList<>());
                            }

                            blockLocations.get(stronghold).add(new xyz.velocity.modules.util.Block(b.getLocation(), stronghold.getWallRegions().getUses()));
                        }
                    }
                }
            }
        }

    }

    public Location getRandomSpawnLocation(MobSave mobSave) {
        List<String> locations = mobSave.getSpawnLocations();

        double totalChances = 0.0;

        for (String location : locations) {
            totalChances += 20;
        }

        int index = 0;

        for (double r = Math.random() * totalChances; index < locations.size() - 1; ++index) {
            r -= 20;
            if (r <= 0.0) break;
        }

        String location = locations.get(index);

        if (location != null) {
            return xyz.velocity.modules.util.Location.parseToLocation(location);
        }

        return null;
    }

    public double roundAvoid(double value, int places) {
        double scale = Math.pow(10, places);
        return Math.round(value * scale) / scale;
    }

    public void giveEffects(CapturePoint capturePoint) {
        List<PotionEffect> pots = getPotionEffects(capturePoint.getStronghold().getPotionEffects());

        for (Player player : FactionsKore.getIntegration().getOnlineMembers(capturePoint.getFactionOwning())) {
            for (PotionEffect pot : pots) {
                if (player.hasPotionEffect(pot.getType())) {
                    PotionEffect potion = player.getActivePotionEffects().stream().filter(obj -> obj.getType().equals(pot.getType())).findFirst().orElse(null);

                    if (potion != null && potion.getAmplifier() == pot.getAmplifier()) {
                        player.removePotionEffect(pot.getType());
                    }
                }

                player.addPotionEffect(new PotionEffect(pot.getType(), 400, pot.getAmplifier()));
            }
        }
    }

    public void giveEffects(Player player) {
        String getFaction = FactionsKore.getIntegration().getPlayerFactionId(player);

        for (CapturePoint capturePoint : capturePoints) {
            if (!getFaction.equals(capturePoint.getFactionOwning())) continue;

            List<PotionEffect> pots = getPotionEffects(capturePoint.getStronghold().getPotionEffects());

            for (PotionEffect pot : pots) {
                player.addPotionEffect(new PotionEffect(pot.getType(), 400, pot.getAmplifier()));
            }
        }
    }

    public void removeEffects(CapturePoint capturePoint) {
        List<PotionEffect> pots = getPotionEffects(capturePoint.getStronghold().getPotionEffects());

        for (Player player : FactionsKore.getIntegration().getOnlineMembers(capturePoint.getFactionOwning())) {
            for (PotionEffect pot : pots) {
                if (player.hasPotionEffect(pot.getType())) {
                    player.removePotionEffect(pot.getType());
                }
            }
        }
    }

    public void removeEffects(Player player) {
        String getFaction = FactionsKore.getIntegration().getPlayerFactionId(player);

        for (CapturePoint capturePoint : capturePoints) {
            if (!getFaction.equals(capturePoint.getFactionOwning())) continue;

            List<PotionEffect> pots = getPotionEffects(capturePoint.getStronghold().getPotionEffects());

            for (PotionEffect pot : pots) {
                if (player.hasPotionEffect(pot.getType())) {
                    player.removePotionEffect(pot.getType());
                }
            }
        }
    }

    private List<PotionEffect> getPotionEffects(List<String> potionList) {
        List<PotionEffect> pots = new ArrayList<>();

        for (String s : potionList) {
            pots.add(EnchantUtil.getEffect(s));
        }

        return pots;
    }

    private boolean isFactionValid(String factionID) {
        try {
            if (FactionsKore.getIntegration().getTagFromId(factionID) == "" || FactionsKore.getIntegration().getTagFromId(factionID) == null) {
                return false;
            }
        } catch (Throwable e) { return false; }
        return true;
    }

    private void addPlaceholders(CapturePoint capturePoint) {
        ModuleManager.placeholders.put(capturePoint.getStronghold().getName() + "_ownedBy", this);
        ModuleManager.placeholders.put(capturePoint.getStronghold().getName() + "_percent", this);
        ModuleManager.placeholders.put(capturePoint.getStronghold().getName() + "_wallsregen", this);
        ModuleManager.placeholders.put(capturePoint.getStronghold().getName() + "_heldfor", this);
        ModuleManager.placeholders.put(capturePoint.getStronghold().getName() + "_contestedBy", this);
        ModuleManager.placeholders.put(capturePoint.getStronghold().getName() + "_status", this);
    }

    private String formatTime(int n) {
        int h = n / 3600;
        int m = (n % 3600) / 60;
        int s = n % 3600 % 60;

        String hDisplay = h > 0 ? h + (h == 1 ? "h " : "h ") : "";
        String mDisplay = m > 0 ? m + (m == 1 ? "m " : "m ") : "";
        String sDisplay = s > 0 ? s + (s == 1 ? "s" : "s") : "";

        return hDisplay + mDisplay + sDisplay;
    }

    public void clearEntities(CapturePoint capturePoint) {
        capturePoint.getLocation1().getWorld().getEntities().forEach(entity -> {
            try {
                for (MobSpawnInterval msi : mobSpawnIntervals) {
                    if (entity.getCustomName().equals(VelocityFeatures.chat(msi.mobSave.getDisplayName()))) {
                        entity.remove();
                        msi.entities.clear();
                    }
                }
            } catch (Throwable e) { }
        });

        spawnedMobs.clear();
    }

    public int getStrongholdAmount(String faction) {
        return DataConfig.getInstance().getStrongholds().values().stream().filter(obj -> obj.getFaction() != null && obj.getFaction().equals(faction)).collect(Collectors.toList()).size();
    }

    public double getSellMultiplier(Player player) {
        String faction = FactionsKore.getIntegration().getPlayerFactionId(player);

        CapturePoint cp = capturePoints.stream().filter(obj -> obj.getFactionOwning() != null && obj.getFactionOwning().equals(faction)).findFirst().orElse(null);

        if (cp == null) return 1.0;
        if (!isFactionValid(cp.getFactionOwning())) return 1.0;

        double multiplier = 1.0;

        for (String customEffect : cp.getStronghold().getCustomEffects()) {
            String[] split = customEffect.split(":");

            if (!split[0].equalsIgnoreCase("sellboost")) continue;

            multiplier = Double.parseDouble(split[1]);
        }

        return multiplier;
    }

    public static boolean isStrongholdBlock(Block blockMined) {
        Pair<String, xyz.velocity.modules.util.Block> blockData = getStrongholdBlockData(blockMined);

        for (Object2ObjectMap.Entry<StrongholdSave, List<xyz.velocity.modules.util.Block>> list : Stronghold.getInstance().blockLocations.object2ObjectEntrySet()) {
            for (xyz.velocity.modules.util.Block block1 : list.getValue()) {
                if (getInstance().isSameCoord(block1.getLocation(), blockMined.getLocation())) {
                    return true;
                }
            }
        }

        return false;
    }

    public static Pair<String, xyz.velocity.modules.util.Block> getStrongholdBlockData(Block blockMined) {
        String stronghold = null;
        xyz.velocity.modules.util.Block block = null;

        for (Object2ObjectMap.Entry<StrongholdSave, List<xyz.velocity.modules.util.Block>> list : Stronghold.getInstance().blockLocations.object2ObjectEntrySet()) {
            stronghold = list.getKey().getName();
            for (xyz.velocity.modules.util.Block block1 : list.getValue()) {
                if (getInstance().isSameCoord(block1.getLocation(), blockMined.getLocation())) {
                    block = block1;
                }
            }
        }

        return new Pair<>(stronghold, block);
    }

    private String getStatus(CapturePoint capturePoint) {
        if (capturePoint.getFactionContesting() != null && !capturePoint.getFactionContesting().equals(capturePoint.getFactionOwning())) {
            return "&c&lContested";
        }

        if (capturePoint.isNeutral()) return "&7&lNeutral";
        if (!capturePoint.isNeutral()) return "&a&lControlled";

        return "";
    }

    private boolean isSameCoord(Location block1, Location block2) {
        if (block1.getX() == block2.getX() && block1.getY() == block2.getY() && block1.getZ() == block2.getZ()) return true;
        return false;
    }

    public void clearMobs() {
        for (CapturePoint capturePoint : capturePoints) {
            for (Entity entity : capturePoint.getLocation1().getWorld().getEntities()) {
                if (entity instanceof EntityMonster) {
                    entity.remove();
                }
            }
        }
    }

    @Override
    public String getName() {
        return "stronghold";
    }

    @Override
    public boolean isEnabled() {
        return StrongholdConfig.getInstance().isEnabled();
    }

    @Override
    public void onEnable() {
        loadData();
        new StrongholdCap();
        new CommandsInterval();

        VelocityFeatures.registerEvent(new StrongholdListener());
        CommandAPI.getInstance().enableCommand(new StrongholdCommand());

        DataConfig.getInstance().addToList();
        StrongholdConfig.getInstance().setEnabled(true);
    }

    @Override
    public void onDisable() {
        unregisterAll();
    }

    @Override
    public String placeholderRequest(Player player, String arg) {
        String[] args = arg.split("_");

        if (args.length == 1) return "";

        CapturePoint capturePoint = capturePoints.stream().filter(obj -> obj.getStronghold().getName().equals(args[0])).findFirst().orElse(null);

        if (capturePoint == null) return "";

        switch (args[1]) {
            case "ownedBy":
                return capturePoint.getFactionOwning() == null ? "None" : FactionsKore.getIntegration().getTagFromId(capturePoint.getFactionOwning());
            case "contestedBy":
                return capturePoint.getFactionContesting() == null ? "None" : FactionsKore.getIntegration().getTagFromId(capturePoint.getFactionContesting());
            case "percent":
                return roundAvoid(capturePoint.getPercentage(), 1) + "";
            case "wallsregen":
                return formatTime(capturePoint.getWallRegenTime());
            case "heldfor":
                return formatTime(capturePoint.getTotalCaptureTime());
            case "status":
                return getStatus(capturePoint);
        }

        return "";
    }

}
