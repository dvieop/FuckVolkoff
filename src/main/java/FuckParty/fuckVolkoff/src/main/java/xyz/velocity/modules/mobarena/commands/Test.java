package FuckParty.fuckVolkoff.src.main.java.xyz.velocity.modules.mobarena.commands;

import dev.lyons.configapi.ConfigAPI;
import net.minecraft.server.v1_8_R3.Entity;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.entity.*;
import org.bukkit.event.entity.CreatureSpawnEvent;
import xyz.velocity.MainConfig;
import xyz.velocity.VelocityFeatures;
import xyz.velocity.commands.ChildCommand;
import xyz.velocity.modules.masks.Masks;
import xyz.velocity.modules.masks.config.MaskConfig;
import xyz.velocity.modules.safari.MobCache;
import xyz.velocity.modules.safari.Safari;
import xyz.velocity.modules.safari.SafariCache;
import xyz.velocity.modules.safari.config.saves.MobSave;
import xyz.velocity.modules.util.ConfigUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class Test extends ChildCommand {

    @Override
    protected boolean noChildrenExecute(CommandSender commandSender, String string, String[] args) {
        /*if (!commandSender.isOp() || !commandSender.hasPermission("masks.*")) {
            commandSender.sendMessage(VelocityFeatures.chat(MainConfig.getInstance().commandPrefix + "No permission to execute this command."));
            return false;
        }

        ConfigAPI.getInstance().loadSpecificConfig(MaskConfig.getInstance(), false, false);
        Masks.getInstance().loadMasks();

        commandSender.sendMessage(VelocityFeatures.chat("&8(&9Velocity&8) &fReloaded the configs."));*/
        //ConfigUtil.saveThread.submit(this::spawnMobs);
        spawnMobs((Player) commandSender);
        return false;
    }

    @Override
    public String getName() {
        return "test";
    }

    @Override
    public List<String> getAliases() {
        List<String> aliases = new ArrayList<>();
        aliases.add("test");
        return aliases;
    }

    public void spawnMobs(Player summoner) {
        //Player summoner = Bukkit.getPlayer("Volkoff0");

        int amountToSpawn = 100;

        for (int i = 0; i < amountToSpawn; i++) {
            //MobSave mobSave = getRandomMob(safariCache.safariTierSave.getMobs());

            //if (mobSave == null) {
                //return;
            //}

            Location location = summoner.getLocation();

            location.add(1, 0, 0);

            String type = "ZOMBIE";

            World world = location.getWorld();
            Entity e = ((CraftWorld) world).createEntity(location,
                    EntityType.valueOf(type).getEntityClass());

            LivingEntity entity = (LivingEntity) e.getBukkitEntity();

            String name = "&6&lZOMBIE ASDASD";

            entity.setCustomName(VelocityFeatures.chat(name
                    .replace("<level>", String.valueOf("5"))
                    .replace("<health>", String.valueOf("150"))
            ));
            entity.setMaxHealth(150);
            entity.setHealth(150);
            entity.setFireTicks(0);

            if (entity.getType() == EntityType.SLIME) {
                Slime slime = (Slime) entity;
                slime.setSize(3);
            }

            if (entity.getType() == EntityType.MAGMA_CUBE) {
                MagmaCube magmaCube = (MagmaCube) entity;
                magmaCube.setSize(3);
            }

            ((CraftWorld) world).getHandle().addEntity(e, CreatureSpawnEvent.SpawnReason.CUSTOM);

            if (!entity.isValid() || e.dead) {
                return;
            }

            //setEquipment(mobSave, entity);
            //addEffects(mobSave, entity);

            //safariCache.mobEntities.add(entity);
            //mobCache.put(entity.getUniqueId(),
                    //new MobCache(safariCache, summoner, name, mobSave, level, mobSave.getDamage()));

            //Safari.getInstance().targetPlayer(entity, summoner);
        }
    }

}
