package FuckParty.fuckVolkoff.src.main.java.xyz.velocity.modules.mobarena.config;

import dev.lyons.configapi.ConfigAPI;
import dev.lyons.configapi.ConfigClass;
import dev.lyons.configapi.EnumFormattingType;
import dev.lyons.configapi.annotations.Config;
import lombok.Getter;
import lombok.Setter;
import xyz.velocity.VelocityFeatures;
import xyz.velocity.modules.mobarena.config.saves.*;
import xyz.velocity.modules.pets.config.saves.InventoryItemSave;
import xyz.velocity.modules.safari.config.saves.EquipmentSave;
import xyz.velocity.modules.safari.config.saves.SpecialRewardSave;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

//@Config("mobArena")
public class MobArenaConfig implements ConfigClass {

    public static MobArenaConfig getInstance() {
        return ConfigAPI.getInstance().getConfigObject(MobArenaConfig.class);
    }

    @Getter
    @Setter
    @Config
    public boolean enabled = false;

    @Getter
    @Config
    public String arenaStart = "Mob Arena has been started! Type /mobarena join";

    @Getter
    @Config
    public String arenaNotActive = "Mob Arena is currently not active";

    @Getter
    @Config
    public String playerJoin = "You have successfully joined mobarena!";

    @Getter
    @Config
    public int autoStartIn = 60;

    @Getter
    @Config
    public int minPlayers = 10;

    @Getter
    @Config
    public String notEoughPlayers = "Mob arena didn't start, there isnt enough players!";

    @Getter
    @Config
    public String arenaFull = "Mob arena didn't start, there isnt enough players!";

    @Getter
    @Config
    public String lobbyLocation = "0:0:0:world";

    @Getter
    @Config
    public String playerTpLocation = "0:0:0:world";

    @Getter
    @Config
    public SchedulerSave schedule = new SchedulerSave("America/New_York", Collections.singletonList("05:00"));

    @Getter
    @Config
    public InventorySave inventory = exampleInv();

    @Getter
    @Config
    public List<ClassSave> classes = Collections.singletonList(exampleClass());
    
    @Getter
    @Config
    public MobArenaSave arenas = exampleArena();


    @Override
    public EnumFormattingType getFormatType() {
        return EnumFormattingType.YAML;
    }

    @Override
    public File getFile() {
        return new File(
                VelocityFeatures.getFileUtils().getConfigDir("MobArena"),
                "mob-arena.yml"
        );
    }

    @Override
    public boolean isStatsConfig() {
        return false;
    }

    public void saveConfig() {
        ConfigAPI.getInstance().saveConfig(this);
    }

    public ClassSave exampleClass() {
        String name = "warrior";
        String displayName = "&6&lWarrior";
        EquipmentSave equipmentSave = new EquipmentSave("DIAMOND_HELMET", "", "", Collections.singletonList("protection_environmental:4"));
        ItemSave itemSave = new ItemSave("potion", 16453, 16);
        return new ClassSave(name, displayName, Collections.singletonList(equipmentSave), Collections.singletonList(itemSave));
    }

    public MobArenaSave exampleArena() {
        String world = "world";
        int maxPlayers = 40;
        int maxMobs = 80;
        int maxRounds = 50;

        int phase = 1;
        int mobsPerPlayer = 1;
        int maxRound = 10;
        double rewardMulti = 1.0;

        String type = "ZOMBIE";
        String displayName = "&6ZOMBIE";
        int chance = 20;
        int health = 40;
        int damage = 30;
        EquipmentSave equipmentSave = new EquipmentSave("DIAMOND_HELMET", "", "", Collections.singletonList("protection_environmental:4"));
        MonsterSave monsterSave = new MonsterSave(type, displayName, chance, health, damage, Collections.singletonList(equipmentSave));

        PhaseSave phaseSave = new PhaseSave(phase, mobsPerPlayer, maxRound, rewardMulti, Collections.singletonList(monsterSave));
        SpecialRewardSave rewardSave = new SpecialRewardSave("money", "&25,000", 10, "eco give <player> 5000", false);

        return new MobArenaSave(world, maxPlayers, maxMobs, maxRounds, Collections.singletonList(phaseSave), Collections.singletonList(rewardSave));
    }

    private InventorySave exampleInv() {
        String name = "&8Generator";
        int size = 27;
        List<InventoryItemSave> classItem = Collections.singletonList(new InventoryItemSave("DIAMOND_SWORD", "&6Warrior Class", new ArrayList<>(), 0, 13));
        InventoryItemSave filler = new InventoryItemSave("STAINED_GLASS_PANE", "&7 ", new ArrayList<>(), 7, 0);

        return new InventorySave(name, size, classItem, filler);
    }

}
