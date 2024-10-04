package FuckParty.fuckVolkoff.src.main.java.xyz.velocity.modules.stronghold;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import net.minecraft.server.v1_8_R3.Entity;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import xyz.velocity.VelocityFeatures;
import xyz.velocity.modules.safari.config.saves.EquipmentSave;
import xyz.velocity.modules.stronghold.config.saves.MobSave;
import xyz.velocity.modules.util.CapturePoint;
import xyz.velocity.modules.stronghold.util.MobWrapper;
import xyz.velocity.modules.util.ColorUtil;
import xyz.velocity.modules.util.EnchantUtil;
import xyz.velocity.modules.util.SkullUtil;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

public class MobSpawnInterval {

    public MobSpawnInterval(MobSave mobSave, CapturePoint capturePoint) {
        instance = this;
        this.mobSave = mobSave;
        startSpawnInterval(mobSave.getSpawnInterval(), capturePoint);
    }

    public MobSave mobSave;
    public BukkitTask bukkitTask;
    public ObjectArrayList<LivingEntity> entities = new ObjectArrayList<>();

    private void startSpawnInterval(int interval, CapturePoint capturePoint) {
        for (org.bukkit.entity.Entity entity : capturePoint.getLocation1().getWorld().getEntities()) {
            
        }
        BukkitRunnable bukkitRunnable = new BukkitRunnable() {
            @Override
            public void run() {
                if (capturePoint.isNeutral()) return;
                if (!mobSave.isEnabled()) return;
                if (entities.size() >= mobSave.getMaxMobs()) {
                    for (ObjectIterator<LivingEntity> iterator = entities.iterator(); iterator.hasNext();) {
                        try {
                            if (iterator.next().isDead() || !iterator.next().isValid()) {
                                iterator.remove();
                            } else {
                                targetPlayer(iterator.next());
                            }
                        } catch (Throwable err) {

                        }
                    }
                    return;
                }

                Location location = Stronghold.getInstance().getRandomSpawnLocation(mobSave);

                if (location == null) return;

                String type = mobSave.getMobType()
                        .replace("WITHER_", "")
                        .replace("BABY_", "");;

                World world = location.getWorld();
                Entity e = ((CraftWorld) world).createEntity(location, EntityType.valueOf(type).getEntityClass());

                LivingEntity entity = (LivingEntity) e.getBukkitEntity();

                String name = mobSave.getDisplayName();

                int min = 5 - 3;
                int max = 5 + 2;

                int level = ThreadLocalRandom.current().nextInt(min, max);

                entity.setCustomName(VelocityFeatures.chat(name
                        .replace("<level>", level + "")
                        .replace("<health>", mobSave.getHealth() + "")
                ));
                entity.setMaxHealth(mobSave.getHealth());
                entity.setHealth(mobSave.getHealth());
                entity.setFireTicks(0);

                if (entity.getType() == EntityType.SLIME) {
                    Slime slime = (Slime) entity;
                    slime.setSize(3);
                }

                if (entity.getType() == EntityType.MAGMA_CUBE) {
                    MagmaCube magmaCube = (MagmaCube) entity;
                    magmaCube.setSize(3);
                }

                if (entity.getType() == EntityType.SKELETON && mobSave.getMobType().equals("WITHER_SKELETON")) {
                    Skeleton skeleton = (Skeleton) entity;
                    skeleton.setSkeletonType(Skeleton.SkeletonType.WITHER);
                }

                if (mobSave.getMobType().contains("BABY")) {
                    Zombie zombie = (Zombie) entity;
                    zombie.setBaby(true);
                }

                ((CraftWorld) world).getHandle().addEntity(e, CreatureSpawnEvent.SpawnReason.CUSTOM);

                if (!entity.isValid() || e.dead) {
                    return;
                }

                setEquipment(mobSave, entity);
                addEffects(mobSave, entity);

                entities.add(entity);
                Stronghold.getInstance().spawnedMobs.put(entity.getUniqueId(), new MobWrapper(capturePoint.getStronghold().getName(), mobSave, level));

                targetPlayer(entity);
            }
        };

        bukkitTask = bukkitRunnable.runTaskTimer(VelocityFeatures.getInstance(), 0, interval * 20);
    }

    private void setEquipment(MobSave mob, LivingEntity entity) {
        for (EquipmentSave equipment : mob.getEquipment()) {
            ItemStack item = loadItem(equipment);

            String type = equipment.getMaterial();

            if (type.endsWith("_HELMET")) {
                if (!equipment.getTexture().isEmpty()) {
                    entity.getEquipment().setHelmet(SkullUtil.skullItem(equipment.getTexture()));
                } else {
                    entity.getEquipment().setHelmet(item);
                }
            }

            else if (type.endsWith("_CHESTPLATE")) {
                entity.getEquipment().setChestplate(item);
            }

            else if (type.endsWith("_LEGGINGS")) {
                entity.getEquipment().setLeggings(item);
            }

            else if (type.endsWith("_BOOTS")) {
                entity.getEquipment().setBoots(item);
            }

            else if (type.endsWith("_SWORD") || type.endsWith("_AXE") || type.endsWith("_PICKAXE") || type.endsWith("_SHOVEL")) {
                entity.getEquipment().setItemInHand(item);
            }
        }
    }

    private void addEffects(MobSave mob, LivingEntity entity) {
        for (String effect : mob.getEffects()) {
            PotionEffect potionEffect = EnchantUtil.getEffect(effect);
            entity.addPotionEffect(potionEffect);
        }
    }

    private ItemStack loadItem(EquipmentSave item) {

        ItemStack itemStack = new ItemStack(Material.getMaterial(item.getMaterial()), 1);

        if (itemStack.getType().name().contains("LEATHER")) {
            LeatherArmorMeta meta = (LeatherArmorMeta) itemStack.getItemMeta();
            meta.setColor(ColorUtil.getColor(item.getColor()));

            itemStack.setItemMeta(meta);
        }

        addEnchantsToItem(itemStack, item.getEnchants());

        return itemStack;

    }

    private void addEnchantsToItem(ItemStack item, List<String> enchants) {

        for (String enchant : enchants) {

            String[] split = enchant.split(":");

            if (split.length < 2) continue;

            try {
                Enchantment enchantment = Enchantment.getByName(split[0].toUpperCase());
                item.addUnsafeEnchantment(enchantment, Integer.parseInt(split[1]));
            } catch (Throwable e) {
                e.printStackTrace();
            }

        }

    }

    private void targetPlayer(LivingEntity livingEntity) {
        Player closestPlayer = null;

        double distance = 100;

        for (org.bukkit.entity.Entity entity : livingEntity.getNearbyEntities(25.0, 25.0, 25.0).stream().filter(z -> !z.hasMetadata("NPC") && z instanceof Player).collect(Collectors.toList())) {
            if (livingEntity.getLocation().toVector().distance(entity.getLocation().toVector()) < distance) {
                closestPlayer = (Player) entity;
                distance = livingEntity.getLocation().toVector().distance(entity.getLocation().toVector());
            }
        }

        if(closestPlayer == null)return;

        ((Creature) livingEntity).setTarget(closestPlayer);
    }

    private static MobSpawnInterval instance;

    public static MobSpawnInterval getInstance() {
        return instance;
    }
}
