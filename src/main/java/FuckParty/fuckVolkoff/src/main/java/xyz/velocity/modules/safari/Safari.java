package FuckParty.fuckVolkoff.src.main.java.xyz.velocity.modules.safari;

import de.tr7zw.nbtapi.NBTCompound;
import de.tr7zw.nbtapi.NBTItem;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;
import lombok.Getter;
import net.minecraft.server.v1_8_R3.Entity;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Creature;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.MagmaCube;
import org.bukkit.entity.Player;
import org.bukkit.entity.Skeleton;
import org.bukkit.entity.Slime;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.potion.PotionEffect;
import xyz.velocity.VelocityFeatures;
import xyz.velocity.commands.CommandAPI;
import xyz.velocity.modules.AbstractModule;
import xyz.velocity.modules.Module;
import xyz.velocity.modules.ModuleManager;
import xyz.velocity.modules.customenchants.CustomEnchants;
import xyz.velocity.modules.fishing.config.saves.FishItemSave;
import xyz.velocity.modules.pets.Pets;
import xyz.velocity.modules.safari.commands.SafariCommand;
import xyz.velocity.modules.safari.config.SafariConfig;
import xyz.velocity.modules.safari.config.StatsConfig;
import xyz.velocity.modules.safari.config.saves.*;
import xyz.velocity.modules.util.*;

@Module
public class Safari extends AbstractModule {

  public static ObjectList<SafariCache> safariCache = new ObjectArrayList<>();
  public static Object2ObjectOpenHashMap<UUID, MobCache> mobCache = new Object2ObjectOpenHashMap<>();

  public static Object2ObjectOpenHashMap<UUID, Pair<List<SpecialRewardSave>, List<ItemStack>>> rewardCache = new Object2ObjectOpenHashMap<>();

  public Safari() {
    instance = this;
  }

  @Getter
  private static Safari instance;

  public void loadSafaris() {
    safariCache.clear();
    mobCache.clear();

    SafariConfig config = SafariConfig.getInstance();

    for (SafariTierSave safariTier : config.safariTiers) {

      for (String s : safariTier.getLocation()) {
        Location location = xyz.velocity.modules.util.Location.parseToLocation(s);

        Block b = location.getWorld()
                .getBlockAt(location.getBlockX(), location.getBlockY(), location.getBlockZ());

        b.setType(Material.DRAGON_EGG);

        safariCache.add(new SafariCache(safariTier, b.getLocation()));
      }

    }

  }

  public void spawnMobs(SafariCache safariCache, Player summoner) {
    int amountToSpawn = safariCache.safariTierSave.getMobsToSpawn();

    for (int i = 0; i < amountToSpawn; i++) {
      MobSave mobSave = getRandomMob(safariCache.safariTierSave.getMobs());

      if (mobSave == null) return;

      Location location = new Location(safariCache.location.getWorld(), safariCache.location.getX(),
              safariCache.getLocation().getY(), safariCache.getLocation().getZ());

      location.add(1, 0, 0);

      String type = mobSave.getType().equals("WITHER_SKELETON") ? "SKELETON" : mobSave.getType();

      World world = location.getWorld();
      Entity e = ((CraftWorld) world).createEntity(location,
              EntityType.valueOf(type).getEntityClass());

      LivingEntity entity = (LivingEntity) e.getBukkitEntity();

      String name = mobSave.getDisplayName();

      int min = safariCache.getSafariTierSave().getPriority() - 2;
      int max = safariCache.getSafariTierSave().getPriority() + 2;

      if (min < 1) {
        min = 1;
      }

      int level = ThreadLocalRandom.current().nextInt(min, max);

      entity.setCustomName(VelocityFeatures.chat(name
              .replace("<level>", String.valueOf(level))
              .replace("<health>", String.valueOf(mobSave.getHealth()))
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

      if (entity.getType() == EntityType.SKELETON && mobSave.getType().equals("WITHER_SKELETON")) {
        Skeleton skeleton = (Skeleton) entity;
        skeleton.setSkeletonType(Skeleton.SkeletonType.WITHER);
      }

      ((CraftWorld) world).getHandle().addEntity(e, CreatureSpawnEvent.SpawnReason.CUSTOM);

      if (!entity.isValid() || e.dead) return;

      setEquipment(mobSave, entity);
      addEffects(mobSave, entity);

      safariCache.mobEntities.add(entity);
      mobCache.put(entity.getUniqueId(),
              new MobCache(safariCache, summoner, name, mobSave, level, mobSave.getDamage()));

      targetPlayer(entity, summoner);
    }
  }

  public MobSave getRandomMob(List<MobSave> list) {
    if (list.isEmpty()) {
      return null;
    }

    double totalChances = 0.0;

    for (MobSave mob : list) {
      totalChances += mob.getChance();
    }

    int index = 0;

    for (double r = Math.random() * totalChances; index < list.size() - 1; ++index) {
      r -= list.get(index).getChance();
      if (r <= 0.0) {
        break;
      }
    }

    return list.get(index);
  }

  public SpecialRewardSave getRandomSpecialReward(List<SpecialRewardSave> list, ItemStack itemStack) {
    if (list.isEmpty()) {
      return null;
    }

    double maxChanceToMulti = SafariConfig.getInstance().mobSword.getMaxChanceToIncrease();
    double luckBoost = getLuckBoost(itemStack);
    double totalChances = 0.0;

    for (SpecialRewardSave mob : list) {
      if (SafariConfig.getInstance().mobSword.isLuckboost()) {
        totalChances += (mob.getChance() >= maxChanceToMulti ? mob.getChance()
                : mob.getChance() * luckBoost);
      } else {
        totalChances += mob.getChance();
      }
    }

    int index = 0;

    for (double r = Math.random() * totalChances; index < list.size() - 1; ++index) {
      r -= list.get(index).getChance();

      if (r <= 0.0) {
        break;
      }
    }

    return list.get(index);
  }

  public ItemStack getDropItem(DropItemSave drop, int amount) {
    ItemStack item;

    if (drop.getMaterial().startsWith("head")) {
      item = createDropItem(drop, SkullUtil.skullItem(drop.getMaterial().replace("head-", "")), amount);
    } else {
      item = createDropItem(drop, amount);
    }

    NBTItem nbtItem = new NBTItem(item);
    NBTCompound nbtCompound = nbtItem.addCompound("velocity_safari_drop");

    nbtCompound.setString("id", drop.getId());
    nbtCompound.setInteger("sellPrice", drop.getSellPrice());

    return nbtItem.getItem();
  }

  private ItemStack createDropItem(DropItemSave drop, int amount) {
    ItemStack item = new ItemStack(Material.getMaterial(drop.getMaterial()), amount, (byte) drop.getData());
    ItemMeta itemMeta = item.getItemMeta();

    itemMeta.setDisplayName(VelocityFeatures.chat(drop.getDisplayName()));

    String lore = VelocityFeatures.chat(String.join("VDIB", drop.getLore()));

    itemMeta.setLore(Arrays.asList(lore.split("VDIB")));

    //itemMeta.spigot().setUnbreakable(true);
    item.setItemMeta(itemMeta);

    if (drop.isGlow()) item = addGlow(item);

    return item;
  }

  private ItemStack createDropItem(DropItemSave drop, ItemStack item, int amount) {
    item.setAmount(amount);

    ItemMeta itemMeta = item.getItemMeta();

    itemMeta.setDisplayName(VelocityFeatures.chat(drop.getDisplayName()));

    String lore = VelocityFeatures.chat(String.join("VDIB", drop.getLore()));

    itemMeta.setLore(Arrays.asList(lore.split("VDIB")));

    //itemMeta.spigot().setUnbreakable(true);
    item.setItemMeta(itemMeta);

    if (drop.isGlow()) item = addGlow(item);

    return item;
  }

  public DropItemSave getRandomDrop(List<String> dropIds, ItemStack itemStack) {
    List<DropItemSave> list = getDropItems(dropIds);

    if (list.isEmpty()) return null;

    double maxChanceToMulti = SafariConfig.getInstance().mobSword.getMaxChanceToIncrease();
    double luckBoost = getLuckBoost(itemStack);
    double totalChances = 0.0;

    for (DropItemSave drop : list) {
      if (SafariConfig.getInstance().mobSword.isLuckboost()) {
        totalChances += (drop.getDropChance() >= maxChanceToMulti ? drop.getDropChance()
                : drop.getDropChance() * luckBoost);
      } else {
        totalChances += drop.getDropChance();
      }
    }

    int index = 0;

    for (double r = Math.random() * totalChances; index < list.size() - 1; ++index) {
      r -= list.get(index).getDropChance();

      if (r <= 0.0) {
        break;
      }
    }

    return list.get(index);
  }

  private double getLuckBoost(ItemStack itemStack) {
    double luckBoost = 1.0;

    if (!ItemUtil.isAirOrNull(itemStack)) {
      NBTItem nbtItem = new NBTItem(itemStack);
      NBTCompound compound = nbtItem.getCompound("velocity_safari_sword");

      if (compound != null) {
        luckBoost = compound.getDouble("luckBoost");
      }
    }

    return luckBoost;
  }

  public List<DropItemSave> getDropItems(List<String> dropIds) {
    List<DropItemSave> drops = SafariConfig.getInstance().itemDrops;
    List<DropItemSave> newList = new ArrayList<>();

    for (String dropId : dropIds) {
      DropItemSave drop = drops.stream().filter(obj -> obj.getId().equalsIgnoreCase(dropId)).findFirst().orElse(null);

      if (drop == null) continue;

      newList.add(drop);
    }

    return newList;
  }

  public boolean isInventoryEmpty(Player player) {
    for (ItemStack item : player.getInventory().getContents()) {
      if (!ItemUtil.isAirOrNull(item)) {
        return false;
      }
    }

    for (ItemStack item : player.getInventory().getArmorContents()) {
      if (!ItemUtil.isAirOrNull(item)) {
        return false;
      }
    }

    return true;
  }

  public void createMobSword(Player player) {
    MobSwordSave sword = SafariConfig.getInstance().mobSword;

    StatsSave statsSave = StatsConfig.getInstance().getPlayerStats(player);

    ItemStack item = new ItemStack(Material.getMaterial(sword.getMaterial()), 1);
    ItemMeta itemMeta = item.getItemMeta();

    itemMeta.spigot().setUnbreakable(true);
    item.setItemMeta(itemMeta);

    NBTItem nbtItem = new NBTItem(item);

    NBTCompound nbtCompound = nbtItem.addCompound("velocity_safari_sword");

    nbtCompound.setString("ownerID", player.getUniqueId().toString());
    nbtCompound.setInteger("mobsKilled", statsSave.getMobsKilled());
    nbtCompound.setInteger("chatWins", statsSave.getChatWins());
    nbtCompound.setDouble("luckBoost", statsSave.getLuckBoost());
    nbtCompound.setInteger("mobCounter", statsSave.getMobCounter());

    updateLore(nbtItem, player);

    addEnchantsToItem(nbtItem.getItem(), sword.getEnchants());

    player.getInventory().addItem(nbtItem.getItem());
  }

  private void addEnchantsToItem(ItemStack item, List<String> enchants) {

    for (String enchant : enchants) {

      String[] split = enchant.split(":");

      if (split.length < 2) {
        continue;
      }

      try {
        Enchantment enchantment = Enchantment.getByName(split[0].toUpperCase());
        item.addUnsafeEnchantment(enchantment, Integer.parseInt(split[1]));
      } catch (Throwable e) {
        e.printStackTrace();
      }

    }

  }

  public void updateLore(NBTItem nbtItem, Player player) {
    MobSwordSave sword = SafariConfig.getInstance().mobSword;

    ItemStack itemStack = nbtItem.getItem();
    ItemMeta itemMeta = itemStack.getItemMeta();

    NBTCompound compound = nbtItem.getCompound("velocity_safari_sword");

    int mobsKilled = compound.getInteger("mobsKilled");
    int chatWins = compound.getInteger("chatWins");
    double luckBoost = compound.getDouble("luckBoost");

    itemMeta.setDisplayName(VelocityFeatures.chat(sword.getDisplayName()));

    String lore = VelocityFeatures.chat(String.join("VDIB", sword.getLore())
            .replace("<owner>", player.getName())
            .replace("<mobs_killed>", String.valueOf(mobsKilled))
            .replace("<chat_wins>", String.valueOf(chatWins))
            .replace("<luck_boost>", String.valueOf(luckBoost))
    );

    itemMeta.setLore(Arrays.asList(lore.split("VDIB")));
    itemStack.setItemMeta(itemMeta);
  }

  public void updateScore(Player player, NBTItem nbtItem, boolean isChatWin, boolean isMobKill) {
    NBTCompound compound = nbtItem.getCompound("velocity_safari_sword");

    int mobsKilled = compound.getInteger("mobsKilled");
    int mobCounter = compound.getInteger("mobCounter");
    int chatWins = compound.getInteger("chatWins");
    double luckBoost = compound.getDouble("luckBoost");

    if (isMobKill) {
      mobsKilled += 1;
      mobCounter += 1;
    }

    if (isChatWin) {
      chatWins += 1;
    }

    if (mobCounter >= SafariConfig.getInstance().mobSword.getMobsToKill()) {
      mobCounter = 0;
      luckBoost += SafariConfig.getInstance().mobSword.getIncrease();
      luckBoost = roundAvoid(luckBoost, 1);
    }

    compound.setInteger("mobsKilled", mobsKilled);
    compound.setInteger("chatWins", chatWins);
    compound.setInteger("mobCounter", mobCounter);
    compound.setDouble("luckBoost", luckBoost);

    if (!compound.getString("ownerID").equals(player.getUniqueId().toString())) {
      return;
    }

    StatsSave statsSave = StatsConfig.getInstance().getPlayerStats(player);

    statsSave.setMobsKilled(mobsKilled);
    statsSave.setMobCounter(mobCounter);
    statsSave.setChatWins(chatWins);
    statsSave.setLuckBoost(luckBoost);

    StatsConfig.getInstance().getData().put(player.getUniqueId(), statsSave);
    //StatsConfig.getInstance().saveData();
  }

  public ItemStack getSwordFromInventory(Player player) {
    for (ItemStack item : player.getInventory().getContents()) {
      if (item == null) {
        continue;
      }

      NBTItem nbtItem = new NBTItem(item);
      NBTCompound compound = nbtItem.getCompound("velocity_safari_sword");

      if (compound == null) {
        continue;
      }

      return nbtItem.getItem();
    }

    return null;
  }

  public double roundAvoid(double value, int places) {
    double scale = Math.pow(10, places);
    return Math.round(value * scale) / scale;
  }

  public boolean isAtLimit(Player player, SafariTierSave safariTierSave) {
    return (int) safariCache.stream().filter(obj ->
            obj.getStarter() != null &&
                    obj.getStarter().getUniqueId().equals(player.getUniqueId())
    ).count() + 1 > safariTierSave.getMaxPerPlayer();
  }

  public boolean canOpenSafari(Player player, SafariTierSave safariTierSave) {
    if (!SafariConfig.getInstance().rewardsPriority.isEnabled()) {
      return true;
    }
    if (player.isOp()) {
      return true;
    }

    List<Priority> priorityList = SafariConfig.getInstance().rewardsPriority.getPriority();

    Priority findPriority = priorityList.stream()
            .filter(obj -> obj.getPriority() == safariTierSave.getPriority()).findFirst().orElse(null);

    if (findPriority == null) {
      return false;
    }
    if (!player.hasPermission(findPriority.getPermission())) {
      return false;
    }

    StatsSave statsSave = StatsConfig.getInstance().getPlayerStats(player);

    if (findPriority.isMobsRequirement()) {
      if (statsSave.getMobsKilled() < findPriority.getMobsToKill()) {
        return false;
      }
    }
    if (findPriority.isLevelRequirement()) {
      return statsSave.getLevel() >= findPriority.getLevel();
    }

    return true;
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
      } else if (type.endsWith("_CHESTPLATE")) {
        entity.getEquipment().setChestplate(item);
      } else if (type.endsWith("_LEGGINGS")) {
        entity.getEquipment().setLeggings(item);
      } else if (type.endsWith("_BOOTS")) {
        entity.getEquipment().setBoots(item);
      } else if (type.endsWith("_SWORD") || type.endsWith("_AXE") || type.endsWith("_PICKAXE")
              || type.endsWith("_SHOVEL")) {
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

  public void addCommand(UUID uuid, SpecialRewardSave rewardSave) {
    if (!rewardCache.containsKey(uuid)) {
      rewardCache.put(uuid, new Pair<>(new ArrayList<>(), new ArrayList<>()));
    }

    rewardCache.get(uuid).first.add(rewardSave);
  }

  public void addItem(UUID uuid, ItemStack item) {
    if (!rewardCache.containsKey(uuid)) {
      rewardCache.put(uuid, new Pair<>(new ArrayList<>(), new ArrayList<>()));
    }

    rewardCache.get(uuid).second.add(item);
  }

  public void targetPlayer(LivingEntity livingEntity, Player player) {
    if (livingEntity == null || player == null || !player.isOnline()) {
      return;
    }

    try {
      ((Creature) livingEntity).setTarget(player);
    } catch (Throwable ignored) {

    }
  }

  public void addPlayerGear(Player player) {
      PlayerGearSave playerGearSave = SafariConfig.getInstance().playerGear;

      if (CustomEnchants.getInstance().isEnabled()) {
        CustomEnchants.getInstance().addPlayerGear(player, playerGearSave);
      }
  }

  public ItemStack addGlow(ItemStack item) {
    item.addUnsafeEnchantment(Enchantment.DURABILITY, 1);
    ItemMeta meta = item.getItemMeta();
    meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
    item.setItemMeta(meta);

    return item;
  }

  public int calculateXP(int level) {
    String calculation = SafariConfig.getInstance().getXpRequirement().replace("<level>",
            String.valueOf(level));

    return (int) roundAvoid(StringCalculator.getCalculateStringValue(calculation), 0);
  }

  public void handleXP(Player player, int xp) {
    StatsSave playerStats = StatsConfig.getInstance().getPlayerStats(player);

    double multiplier = 1.0;
    int calcXP = playerStats.getXp();

    multiplier += Pets.getInstance().getPetMultiplier(player, "safarixp");
    calcXP += (int) (xp * multiplier);

    playerStats.setXp(calcXP);

    if (calcXP >= playerStats.getXpToLevelUp()) {
      playerStats.setLevel(playerStats.getLevel() + 1);
      playerStats.setXp(0);
      playerStats.setXpToLevelUp(calculateXP(playerStats.getLevel()));

      player.sendMessage(VelocityFeatures.chat(SafariConfig.getInstance().getLevelUp()
              .replace("<level>", String.valueOf(playerStats.getLevel()))
      ));
    }

    StatsConfig.getInstance().getData().put(player.getUniqueId(), playerStats);
    //StatsConfig.getInstance().saveData();
  }

  public String progressBar(Player player) {
    StatsSave statsSave = StatsConfig.getInstance().getPlayerStats(player);

    int xp = statsSave.getXp();
    int xpTillLevelUp = statsSave.getXpToLevelUp();

    int lines = 20;
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

  public void updateInventory(Player player) {
    player.getInventory().clear();

    player.getInventory().setHelmet(null);
    player.getInventory().setChestplate(null);
    player.getInventory().setLeggings(null);
    player.getInventory().setBoots(null);

    player.updateInventory();

    for (PotionEffect activePotionEffect : player.getActivePotionEffects()) {
      player.removePotionEffect(activePotionEffect.getType());
    }

    UUID id = player.getUniqueId();

    if (rewardCache.containsKey(id) && (!rewardCache.get(id).first.isEmpty() || !rewardCache.get(id).second.isEmpty())) {
      int amount = rewardCache.get(id).first.size() + rewardCache.get(id).second.size();

      player.sendMessage(VelocityFeatures.chat(SafariConfig.getInstance().safariLeave
              .replace("<rewards>", amount + "")
      ));
    }
  }

  public Pair<List<SpecialRewardSave>, List<ItemStack>> getPlayerRewards(Player player) {
    if (!rewardCache.containsKey(player.getUniqueId())) {
      rewardCache.put(player.getUniqueId(), new Pair<>(new ArrayList<>(), new ArrayList<>()));
    }

    return rewardCache.get(player.getUniqueId());
  }

  public void updatePlayerRewards(Player player, SpecialRewardSave specialRewardSave) {
    rewardCache.get(player.getUniqueId()).first.add(specialRewardSave);
  }

  public void updatePlayerRewards(Player player, ItemStack item) {
    rewardCache.get(player.getUniqueId()).second.add(item);
  }

  private void addPlaceholders() {
    ModuleManager.placeholders.put("safari_mobKills", this);
    ModuleManager.placeholders.put("safari_chatWins", this);
    ModuleManager.placeholders.put("safari_luckBoost", this);
    ModuleManager.placeholders.put("safari_level", this);
    ModuleManager.placeholders.put("safari_xp", this);
    ModuleManager.placeholders.put("safari_xpToLevelUp", this);
    ModuleManager.placeholders.put("safari_progressBar", this);
  }

  @Override
  public String getName() {
    return "safari";
  }

  @Override
  public boolean isEnabled() {
    return SafariConfig.getInstance().isEnabled();
  }

  @Override
  public void onEnable() {
    VelocityFeatures.registerEvent(new SafariListener());
    CommandAPI.getInstance().enableCommand(new SafariCommand());

    loadSafaris();
    addPlaceholders();

    new SafariInterval();

    SafariConfig.getInstance().setEnabled(true);
  }

  @Override
  public void onDisable() {
    VelocityFeatures.unregisterEvent(SafariListener.getInstance());
    CommandAPI.getInstance().disableCommand(SafariCommand.class);
    SafariInterval.getInstance().bukkitTask.cancel();

    for (SafariCache cache : safariCache) {
      cache.getHologram().deleteHologram();
    }

    SafariConfig.getInstance().setEnabled(false);
  }

  @Override
  public String placeholderRequest(Player player, String arg) {
    String[] args = arg.split("_");

    if (args.length == 1) {
      return "";
    }

    StatsSave statsSave = StatsConfig.getInstance().getPlayerStats(player);

    switch (args[1]) {
      case "mobKills":
        return String.valueOf(statsSave.getMobsKilled());
      case "chatWins":
        return String.valueOf(statsSave.getChatWins());
      case "luckBoost":
        return String.valueOf(statsSave.getLuckBoost());
      case "level":
        return String.valueOf(statsSave.getLevel());
      case "xp":
        return String.valueOf(statsSave.getXp());
      case "xpToLevelUp":
        return String.valueOf(statsSave.getXpToLevelUp());
      case "progressBar":
        return progressBar(player);
    }

    return "";
  }

}
