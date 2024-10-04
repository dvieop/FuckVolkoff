package FuckParty.fuckVolkoff.src.main.java.xyz.velocity.modules.garrison;

import com.golfing8.kore.FactionsKore;
import dev.lyons.configapi.ConfigAPI;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import xyz.velocity.VelocityFeatures;
import xyz.velocity.commands.CommandAPI;
import xyz.velocity.modules.AbstractModule;
import xyz.velocity.modules.Module;
import xyz.velocity.modules.ModuleManager;
import xyz.velocity.modules.customenchants.CustomEnchants;
import xyz.velocity.modules.garrison.commands.GarrisonCommand;
import xyz.velocity.modules.garrison.config.DataConfig;
import xyz.velocity.modules.garrison.config.GarrisonConfig;
import xyz.velocity.modules.garrison.config.saves.*;
import xyz.velocity.modules.safari.config.saves.PlayerGearSave;
import xyz.velocity.modules.util.Block;
import xyz.velocity.modules.util.CapturePoint;
import xyz.velocity.modules.util.Pair;

import java.util.*;
import java.util.stream.Collectors;

@Module
public class Garrison extends AbstractModule {

    @Getter
    private static Garrison instance;
    //public List<CapturePoint> capturePoints = new ArrayList<>();
    public CapturePoint capturePoint;
    public Object2ObjectMap<GarrisonSave, List<Block>> blockLocations = new Object2ObjectOpenHashMap<>();
    public static Object2ObjectOpenHashMap<UUID, Long> deathBanMap = new Object2ObjectOpenHashMap<>();
    public List<BoostCache> boostCaches = new ArrayList<>();
    public EnumBoostMode mode = EnumBoostMode.EXP;
    public Hologram hologram;

    public Garrison() {
        instance = this;
    }

    public void loadData() {
        //capturePoints.clear();
        blockLocations.clear();
        boostCaches.clear();

        GarrisonConfig config = GarrisonConfig.getInstance();
        GarrisonSave garrisonSave = config.getGarrison();

        CapturePoint capturePoint = new CapturePoint(garrisonSave);

        DataSave dataSave = DataConfig.getInstance().getGarrison().get(garrisonSave.getName());

        if (dataSave != null) {
            capturePoint.setFactionOwning(dataSave.getFaction());
            capturePoint.setPercentage(dataSave.getPercentage());
            capturePoint.setNeutral(dataSave.isNeutral());
            capturePoint.setTotalCaptureTime(dataSave.getCaptureTime());
            capturePoint.setProtectionTime(dataSave.getProtectionTime());

            this.mode = dataSave.getMode();
        }

        //capturePoints.add(capturePoint);
        for (BoostSave boost : garrisonSave.getBoosts()) {
            if (dataSave == null) {
                boostCaches.add(new BoostCache(boost, 1, boost.getMultiplierPerTier(), 0, boost.getXpIncrement()));
                continue;
            }

            BoostDataSave bds = dataSave.getBoosts().stream().filter(obj -> Objects.equals(obj.getName(), boost.getName())).findFirst().orElse(null);

            if (bds == null) continue;

            boostCaches.add(new BoostCache(boost, bds.getTier(), bds.getMultiplier(), bds.getXp(), bds.getTier() * boost.getXpIncrement()));
        }

        this.capturePoint = capturePoint;

        //updateWalls(garrisonSave);
        addPlaceholders(capturePoint);
        updateData(capturePoint);
    }

    public void updateData(CapturePoint capturePoint) {
        DataConfig dataConfig = DataConfig.getInstance();

        if (!dataConfig.getGarrison().containsKey(capturePoint.getGarrison().getName())) return;

        DataSave dataSave = dataConfig.getGarrison().get(capturePoint.getGarrison().getName());

        dataSave.setFaction(capturePoint.getFactionOwning());
        dataSave.setPercentage(capturePoint.getPercentage());
        dataSave.setNeutral(capturePoint.isNeutral());
        dataSave.setCaptureTime(capturePoint.getTotalCaptureTime());
        dataSave.setMode(this.mode);

        List<BoostDataSave> boostSave = new ArrayList<>();

        for (BoostCache boostCache : boostCaches) {
            boostSave.add(new BoostDataSave(boostCache.getBoost().getName(), boostCache.getTier(), boostCache.getMultiplier() - 1, boostCache.getXp(), boostCache.getXpTillUpgrade()));
        }

        dataSave.setBoosts(boostSave);

        ConfigAPI.getInstance().saveConfig(dataConfig);
    }

    public void updateProtectionData(CapturePoint capturePoint) {
        DataConfig dataConfig = DataConfig.getInstance();
        DataSave dataSave = dataConfig.getGarrison().get(capturePoint.getGarrison().getName());

        dataSave.setProtectionTime(System.currentTimeMillis() + ((GarrisonConfig.getInstance().getGarrison().getGraceSave().getMinutes() * 60) * 1000L));

        ConfigAPI.getInstance().saveConfig(dataConfig);
    }

    public double getDamageReduction(Player player) {
        BoostCache boostCache = Garrison.getInstance().getCurrentBoost();
        String faction = FactionsKore.getIntegration().getPlayerFactionId(player);

        if (!Garrison.getInstance().isEligible(faction, EnumBoostMode.REDUCE)) return 0;

        return boostCache.getMultiplier() - 1.0;
    }

    /*public void updateWalls(GarrisonSave garrison) {
        if (!garrison.getWallRegions().isEnabled()) return;

        Material material = Material.getMaterial(garrison.getWallRegions().getMaterial());

        for (WallLocations region : garrison.getWallRegions().getRegions()) {

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
                            org.bukkit.block.Block b = corner1.getWorld().getBlockAt(x, y, z);
                            if (b.getType() == Material.AIR) {
                                b.setType(material);
                            }

                            if (!blockLocations.containsKey(garrison)) {
                                blockLocations.put(garrison, new ArrayList<>());
                            }

                            blockLocations.get(garrison).add(new xyz.velocity.modules.util.Block(b.getLocation(), garrison.getWallRegions().getUses()));
                        }
                    }
                }
            }
        }
    }*/

    public static Pair<String, Block> getGarrisonBlockData(org.bukkit.block.Block blockMined) {
        String garrison = null;
        xyz.velocity.modules.util.Block block = null;

        for (Object2ObjectMap.Entry<GarrisonSave, List<xyz.velocity.modules.util.Block>> list : Garrison.getInstance().blockLocations.object2ObjectEntrySet()) {
            garrison = list.getKey().getName();
            for (xyz.velocity.modules.util.Block block1 : list.getValue()) {
                if (getInstance().isSameCoord(block1.getLocation(), blockMined.getLocation())) {
                    block = block1;
                }
            }
        }

        return new Pair<>(garrison, block);
    }

    public BoostCache getCurrentBoost() {
        return Garrison.getInstance().boostCaches
                .stream()
                .filter(obj -> obj.boost.getName().equals(this.mode.name().toLowerCase()))
                .findFirst()
                .orElse(null);
    }

    public void setNonDisabledBoosts() {
        mode = mode.next();
        if (!getCurrentBoost().getBoost().isEnabled()) setNonDisabledBoosts();
    }

    public String formatTime(int n) {
        int h = n / 3600;
        int m = (n % 3600) / 60;
        int s = n % 3600 % 60;

        String hDisplay = h > 0 ? h + ("h ") : "";
        String mDisplay = m > 0 ? m + ("m ") : "";
        String sDisplay = s > 0 ? s + ("s") : "";

        return hDisplay + mDisplay + sDisplay;
    }

    private boolean isSameCoord(Location block1, Location block2) {
        if (block1.getX() == block2.getX() && block1.getY() == block2.getY() && block1.getZ() == block2.getZ()) return true;
        return false;
    }

    public void resetBoosts() {
        for (BoostCache boostCache : boostCaches) {
            BoostSave bs = boostCache.getBoost();

            boostCache.setMultiplier(1 + bs.getMultiplierPerTier());
            boostCache.setTier(1);
            boostCache.setXp(0);
            boostCache.setXpTillUpgrade(boostCache.getBoost().getXpIncrement());

            setNonDisabledBoosts();
        }
    }

        public static double getHeadhuntingMultiplier(UUID uuid) {
        Player player = Bukkit.getPlayer(uuid);

        if (player == null) return 1.0;

        String factionID = FactionsKore.getIntegration().getPlayerFactionId(player);
        BoostCache boostCache = getInstance().getCurrentBoost();

        if (!getInstance().isEligible(factionID, EnumBoostMode.HEADHUNTING)) return 1.0;

        return boostCache.getMultiplier();
    }

    public static double getBlueprintMultiplier(UUID uuid) {
        Player player = Bukkit.getPlayer(uuid);

        if (player == null) return 1.0;

        String factionID = FactionsKore.getIntegration().getPlayerFactionId(player);
        BoostCache boostCache = getInstance().getCurrentBoost();

        if (!getInstance().isEligible(factionID, EnumBoostMode.BLUEPRINT)) return 1.0;

        return boostCache.getMultiplier();
    }

    public void handleXp(Player player, String type) {
        String factionID = FactionsKore.getIntegration().getPlayerFactionId(player);

        if (!Objects.equals(capturePoint.getFactionOwning(), factionID)) return;

        XpSave xpSave = GarrisonConfig.getInstance().getGarrison().getXp().stream().filter(obj -> Objects.equals(obj.getName(), type)).findFirst().orElse(null);

        if (xpSave == null) return;

        addXp(xpSave.getXp());
    }

    public void addXp(int xp) {
        GarrisonConfig config = GarrisonConfig.getInstance();
        BoostCache boostCache = getCurrentBoost();

        if (boostCache == null) return;
        if (boostCache.getTier() < boostCache.getBoost().getMaxTier()) boostCache.addXp(xp);

        if (boostCache.getXp() >= boostCache.getXpTillUpgrade()) {
            int tier = boostCache.getTier() + 1;
            int finalXp = 0;

            if (tier > boostCache.getBoost().getMaxTier()) return;
            if (boostCache.getXp() - boostCache.getXpTillUpgrade() > 0) finalXp = boostCache.getXp() - boostCache.getXpTillUpgrade();

            boostCache.setTier(tier);
            boostCache.setMultiplier(boostCache.getMultiplier() + boostCache.getBoost().getMultiplierPerTier());
            boostCache.setXp(finalXp);
            boostCache.setXpTillUpgrade(boostCache.getBoost().getXpIncrement() * tier);

            if (config.announceUpgrade) {
                Bukkit.broadcastMessage(VelocityFeatures.chat(config.tierUpgrade
                        .replace("<faction>", FactionsKore.getIntegration().getTagFromId(capturePoint.getFactionOwning()))
                        .replace("<mode>", boostCache.getBoost().getDisplayName())
                        .replace("<tier>", tier + "")
                ));
            }

            Garrison.getInstance().hologram.updateLines();
            Garrison.getInstance().updateData(capturePoint);
        }
    }

    public boolean isDivineBoostEnabled() {
        int enabledBoosts = (int) boostCaches.stream().filter(obj -> obj.getBoost().isEnabled()).count();
        int maxedBoosts = (int) boostCaches.stream().filter(obj -> obj.getBoost().isEnabled() && obj.getTier() == obj.getBoost().getMaxTier()).count();

        return maxedBoosts == enabledBoosts;
    }

    public boolean isEligible(String faction, EnumBoostMode mode) {
        if (Garrison.getInstance().capturePoint.getFactionOwning() == null) return false;

        String owning = Garrison.getInstance().capturePoint.getFactionOwning();
        BoostCache boostCache = Garrison.getInstance().getCurrentBoost();

        if (owning != faction) return false;
        if (!boostCache.boost.isEnabled()) return false;
        if (boostCache.getBoost().getName() != mode.name().toLowerCase()) return false;

        return true;
    }

    public boolean isOnGrace(CapturePoint capturePoint) {
        if (System.currentTimeMillis() > capturePoint.getProtectionTime()) {
            resetGarrison(capturePoint);
            return false;
        }

        return true;
    }

    public boolean isBoostMaxed(BoostCache boostCache) {
        return boostCache.getTier() == boostCache.getBoost().getMaxTier();
    }

    public void resetGarrison(CapturePoint capturePoint) {
        capturePoint.setFactionOwning(null);
        capturePoint.setNeutral(true);
        capturePoint.setTotalCaptureTime(0);

        resetBoosts();
        updateData(capturePoint);
    }

    public double getPetXP(Player player) {
        if (!this.isEnabled()) return 0;

        String factionID = FactionsKore.getIntegration().getPlayerFactionId(player);

        if (!isEligible(factionID, EnumBoostMode.PETXP)) return 0;

        return getCurrentBoost().multiplier;
    }

    public int getBookSuccessIncrease(Player player) {
        if (!this.isEnabled()) return 0;
        if (!isDivineBoostEnabled()) return 0;
        if (!isFactionOwning(capturePoint, player)) return 0;

        String factionID = FactionsKore.getIntegration().getPlayerFactionId(player);

        return (int) (0.5 * FactionsKore.getIntegration().getOnlineMembers(factionID).size());
    }

    public void addPlayerGear(Player player) {
        PlayerGearSave playerGearSave = GarrisonConfig.getInstance().getGarrison().getPlayerGear();

        if (CustomEnchants.getInstance().isEnabled()) {
            CustomEnchants.getInstance().addPlayerGear(player, playerGearSave);
        }
    }

    public boolean isFactionOwning(CapturePoint capturePoint, Player player) {
        return Objects.equals(capturePoint.getFactionOwning(), FactionsKore.getIntegration().getPlayerFactionId(player));
    }

    public boolean isDeathBanned(Player player) {
        UUID id = player.getUniqueId();

        if (!deathBanMap.containsKey(id)) return false;
        if (System.currentTimeMillis() >= deathBanMap.get(id)) {
            deathBanMap.remove(id);
            return false;
        }

        return true;
    }

    public int longToSeconds(Player player) {
        long time = deathBanMap.get(player.getUniqueId());
        return (int) ((time - System.currentTimeMillis()) / 1000L);
    }

    public String progressBar(BoostCache boostCache) {
        int xp = boostCache.getXp();
        int xpTillLevelUp = boostCache.getXpTillUpgrade();

        int lines = 10;
        int percent = (xp * 100) / xpTillLevelUp;

        int completeLines = (int) (lines * (percent / 100.0f));

        StringBuilder finalStr = new StringBuilder();

        for (int i = 0; i < completeLines; i++) {
            finalStr.append("&a|");
        }

        int remaining = lines - completeLines;

        for (int i = 0; i < remaining; i++) {
            finalStr.append("&c|");
        }

        return "&8[" + finalStr + "&8]";
    }

    public String tierBar(BoostCache boostCache) {
        int tier = boostCache.getTier();
        int maxTier = boostCache.getBoost().getMaxTier();

        StringBuilder finalStr = new StringBuilder();

        for (int i = 0; i < tier; i++) {
            finalStr.append("&a●");
        }

        int remaining = maxTier - tier;

        for (int i = 0; i < remaining; i++) {
            finalStr.append("&c●");
        }

        return "&8[" + finalStr + "&8]";
    }

    private String getStatus(CapturePoint capturePoint) {
        if (capturePoint.getFactionContesting() != null && !capturePoint.getFactionContesting().equals(capturePoint.getFactionOwning())) {
            return "&c&lContested";
        }

        if (capturePoint.isNeutral()) return "&7&lNeutral";
        if (!capturePoint.isNeutral()) return "&a&lControlled";

        return "";
    }

    public double roundAvoid(double value, int places) {
        double scale = Math.pow(10, places);
        return Math.round(value * scale) / scale;
    }

    private String graceFormat() {
        DataSave dataSave = DataConfig.getInstance().getGarrison().get(capturePoint.getGarrison().getName());

        long protectionTime = dataSave.getProtectionTime();
        int seconds = (int) ((protectionTime - System.currentTimeMillis()) / 1000);

        if (System.currentTimeMillis() >= protectionTime) return "&c&l✘ NONE";
        return "&a&l✔ ACTIVE &8(&7" + formatTime(seconds) + "&8)";
    }

    private void addPlaceholders(CapturePoint capturePoint) {
        ModuleManager.placeholders.put(capturePoint.getGarrison().getName() + "_ownedBy", this);
        ModuleManager.placeholders.put(capturePoint.getGarrison().getName() + "_percent", this);
        ModuleManager.placeholders.put(capturePoint.getGarrison().getName() + "_wallsregen", this);
        ModuleManager.placeholders.put(capturePoint.getGarrison().getName() + "_heldfor", this);
        ModuleManager.placeholders.put(capturePoint.getGarrison().getName() + "_contestedBy", this);
        ModuleManager.placeholders.put(capturePoint.getGarrison().getName() + "_status", this);
        ModuleManager.placeholders.put(capturePoint.getGarrison().getName() + "_grace", this);
        ModuleManager.placeholders.put(capturePoint.getGarrison().getName() + "_divineStatus", this);
        ModuleManager.placeholders.put(capturePoint.getGarrison().getName() + "_currentMode", this);

        for (BoostSave boost : capturePoint.getGarrison().getBoosts()) {
            ModuleManager.placeholders.put(capturePoint.getGarrison().getName() + "_" + boost.getName() + "_name", this);
            ModuleManager.placeholders.put(capturePoint.getGarrison().getName() + "_" + boost.getName() + "_tier", this);
            ModuleManager.placeholders.put(capturePoint.getGarrison().getName() + "_" + boost.getName() + "_maxtier", this);
            ModuleManager.placeholders.put(capturePoint.getGarrison().getName() + "_" + boost.getName() + "_multiplier", this);
            ModuleManager.placeholders.put(capturePoint.getGarrison().getName() + "_" + boost.getName() + "_tierBar", this);
            ModuleManager.placeholders.put(capturePoint.getGarrison().getName() + "_" + boost.getName() + "_progressBar", this);
            ModuleManager.placeholders.put(capturePoint.getGarrison().getName() + "_" + boost.getName() + "_requiredXp", this);
        }
    }

    private String boostPlaceholder(String type, String arg) {
        BoostCache boostCache = boostCaches.stream().filter(obj -> Objects.equals(obj.boost.getName(), type)).findFirst().orElse(null);

        if (boostCache == null) return "";

        switch (arg) {
            case "name":
                return boostCache.getBoost().getDisplayName();
            case "tier":
                return boostCache.getTier() + "";
            case "maxtier":
                return boostCache.getBoost().getMaxTier() + "";
            case "multiplier":
                return roundAvoid(boostCache.getMultiplier(), 2) + "";
            case "tierBar":
                return tierBar(boostCache);
            case "progressBar":
                return progressBar(boostCache);
            case "requiredXp":
                return isBoostMaxed(boostCache) ? "MAX TIER" : boostCache.getXpTillUpgrade() + "";
            /*case "time":
                return formatTime(boostCache.getTime());*/
        }

        return "";
    }

    @Override
    public String getName() {
        return "garrison";
    }

    @Override
    public boolean isEnabled() {
        return GarrisonConfig.getInstance().isEnabled();
    }

    @Override
    public void onEnable() {
        loadData();
        DataConfig.getInstance().addToList();
        new GarrisonCap();
        //new BoostInterval();

        VelocityFeatures.registerEvent(new GarrisonListener());
        CommandAPI.getInstance().enableCommand(new GarrisonCommand());

        GarrisonConfig.getInstance().setEnabled(true);

        this.hologram = new Hologram(xyz.velocity.modules.util.Location.parseToLocation(GarrisonConfig.getInstance().hologram.getLocation()));
    }

    @Override
    public void onDisable() {
        try {
            GarrisonCap.getInstance().bukkitTask.cancel();
        } catch (Throwable ignored) { }

        /*try {
            BoostInterval.getInstance().bukkitTask.cancel();
        } catch (Throwable ignored) { }*/

        if (this.hologram != null) this.hologram.deleteHologram();

        VelocityFeatures.unregisterEvent(GarrisonListener.getInstance());
        CommandAPI.getInstance().disableCommand(GarrisonCommand.class);

        GarrisonConfig.getInstance().setEnabled(false);
    }

    @Override
    public String placeholderRequest(Player player, String arg) {
        String[] args = arg.split("_");

        if (args.length == 1) return "";

        if (capturePoint == null) return "";

        switch (args[1]) {
            case "ownedBy":
                return capturePoint.getFactionOwning() == null ? "None" : FactionsKore.getIntegration().getTagFromId(capturePoint.getFactionOwning());
            case "contestedBy":
                return capturePoint.getFactionContesting() == null ? "None" : FactionsKore.getIntegration().getTagFromId(capturePoint.getFactionContesting());
            case "percent":
                return capturePoint.getPercentage() + "";
            case "wallsregen":
                return formatTime(capturePoint.getWallRegenTime());
            case "heldfor":
                return formatTime(capturePoint.getTotalCaptureTime());
            case "status":
                return getStatus(capturePoint);
            case "divineStatus":
                return isDivineBoostEnabled() ? "&aEnabled" : "&cDisabled";
            case "currentMode":
                return getCurrentBoost().getBoost().getName();
            case "exp":
                return boostPlaceholder("exp", args[2]);
            case "headhunting":
                return boostPlaceholder("headhunting", args[2]);
            case "damage":
                return boostPlaceholder("damage", args[2]);
            case "reduce":
                return boostPlaceholder("reduce", args[2]);
            case "crop":
                return boostPlaceholder("crop", args[2]);
            case "blueprint":
                return boostPlaceholder("blueprint", args[2]);
            case "petxp":
                return boostPlaceholder("petxp", args[2]);
            case "mining":
                return boostPlaceholder("mining", args[2]);
            case "grace":
                return VelocityFeatures.chat(graceFormat());
        }

        return "";
    }

}
