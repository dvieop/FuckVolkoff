package FuckParty.fuckVolkoff.src.main.java.xyz.velocity.modules.customenchants;

import com.golfing8.kore.FactionsKore;
import com.golfing8.kore.object.TranslatedRelation;
import com.google.common.collect.Lists;
import com.sk89q.worldguard.bukkit.ProtectionQuery;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import de.tr7zw.nbtapi.NBTCompound;
import de.tr7zw.nbtapi.NBTItem;
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import kore.ArmorEquipEvent;
import kore.ArmorListener;
import lombok.Getter;
import net.minecraft.server.v1_8_R3.EntityPlayer;
import net.minecraft.server.v1_8_R3.NBTTagCompound;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import xyz.velocity.VelocityFeatures;
import xyz.velocity.commands.CommandAPI;
import xyz.velocity.modules.customenchants.commands.CustomEnchantCommand;
import xyz.velocity.modules.customenchants.config.CustomEnchantConfig;
import xyz.velocity.modules.customenchants.config.KitsConfig;
import xyz.velocity.modules.customenchants.config.saves.*;
import xyz.velocity.modules.AbstractModule;
import xyz.velocity.modules.Module;
import xyz.velocity.modules.customenchants.enchants.AbstractEnchant;
import xyz.velocity.modules.customenchants.enchants.Silence;
import xyz.velocity.modules.customenchants.events.EnchantProcEvent;
import xyz.velocity.modules.customenchants.listeners.EnchantListener;
import xyz.velocity.modules.customenchants.listeners.InventoryListener;
import xyz.velocity.modules.garrison.Garrison;
import xyz.velocity.modules.safari.config.saves.PlayerGearSave;
import xyz.velocity.modules.util.EnchantUtil;
import xyz.velocity.modules.util.ItemUtil;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

@Module
public class CustomEnchants extends AbstractModule {

    public CustomEnchants() {
        new EnchantManager();
        new ItemManager();

        VelocityFeatures.getInstance().getServer().getPluginManager().registerEvents(new ArmorListener(new ArrayList<>()), VelocityFeatures.getInstance());
        instance = this;
    }

    @Getter
    private static CustomEnchants instance;

    public List<EnchantSave> getEnchantsWithLevel(int level) {
        return CustomEnchantConfig.getInstance().enchantList.stream().filter(obj -> obj.getEnchantTier() == level && obj.isEnabled()).collect(Collectors.toList());
    }

    public ItemStack buildBook(EnchantSave enchant) {

        int level = (int) (Math.random() * ((enchant.getMaxLevel() + 1) - 1)) + 1;

        ItemStack item = new ItemStack(Material.ENCHANTED_BOOK, 1);

        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.setDisplayName(VelocityFeatures.chat(enchant.getDisplayName() + " " + EnchantUtil.toRomanNumerals(level)));

        int success = getRandomChance();
        int fail = getRandomChance();

        return applyBookData(enchant, level, success, fail, item, itemMeta);
    }

    public ItemStack buildBook(EnchantSave enchant, boolean maxSuccess, boolean randomLevel, int level) {

        if (randomLevel) {
            level = (int) (Math.random() * ((enchant.getMaxLevel() + 1) - 1)) + 1;
        }
        else {
            if (enchant.getMaxLevel() < level) level = enchant.getMaxLevel();
        }

        ItemStack item = new ItemStack(Material.ENCHANTED_BOOK, 1);

        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.setDisplayName(VelocityFeatures.chat(enchant.getDisplayName() + " " + EnchantUtil.toRomanNumerals(level)));

        int success = getRandomChance();
        int fail = getRandomChance();

        if (maxSuccess) {
            success = 100;
        }

        return applyBookData(enchant, level, success, fail, item, itemMeta);
    }

    public ItemStack buildBook(EnchantSave enchant, int level, int success, int fail) {
        ItemStack item = new ItemStack(Material.ENCHANTED_BOOK, 1);

        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.setDisplayName(VelocityFeatures.chat(enchant.getDisplayName() + " " + EnchantUtil.toRomanNumerals(level)));

        if (enchant.getMaxLevel() < level) level = enchant.getMaxLevel();

        return applyBookData(enchant, level, success, fail, item, itemMeta);
    }

    private ItemStack applyBookData(EnchantSave enchant, int level, int success, int fail, ItemStack item, ItemMeta itemMeta) {

        String enchantLore = VelocityFeatures.chat(String.join("VDIB", enchant.getLore())
                .replace("<current_level>", level + "")
                .replace("<success>", success + "")
                .replace("<fail>", fail + "")
        );

        List<String> lore = Arrays.asList(enchantLore.split("VDIB"));

        itemMeta.setLore(lore);
        item.setItemMeta(itemMeta);

        NBTItem nbtItem = new NBTItem(item);

        NBTCompound nbtCompound = nbtItem.addCompound("velocity_customenchants_book");

        nbtCompound.setString("unstackable", UUID.randomUUID().toString());
        nbtCompound.setInteger("level", level);
        nbtCompound.setString("id", enchant.getName());
        nbtCompound.setBoolean("is-book", true);

        if (CustomEnchantConfig.getInstance().chance.isEnabled()) {
            nbtCompound.setInteger("success", success);
            nbtCompound.setInteger("fail", fail);
        }

        return nbtItem.getItem();
    }

    public List<String> resetLore(List<String> lore, NBTCompound levels, EnchantSave justApplied, int level, boolean organize) {

        List<String> toReturn = Lists.newArrayList();
        List<EnchantSave> enchantList;

        if (organize) {
            enchantList = CustomEnchantConfig.getInstance().enchantList.stream().sorted(Comparator.comparingInt(EnchantSave::getEnchantTier)).collect(Collectors.toList());
        } else {
            enchantList = CustomEnchantConfig.getInstance().enchantList;
        }

        for (String string : lore) {
            boolean shouldAdd = true;

            for (EnchantSave enchant : enchantList) {

                int myLevel = enchant == justApplied ? level : levels.getInteger(enchant.getName());
                String c = VelocityFeatures.chat(enchant.getDisplayName().replace("<level>", EnchantUtil.toRomanNumerals(myLevel)));

                if(string.contains(c)){
                    shouldAdd = false;
                    break;
                }

            }

            if (shouldAdd) toReturn.add(string);
        }

        return toReturn;
    }

    public EnchantSave getEnchant(String enchantName) {
        return CustomEnchantConfig.getInstance().getEnchantList().stream().filter(obj -> obj.getName().equals(enchantName)).findFirst().orElse(null);
    }

    public Object2ObjectMap<EnchantSave, Integer> getEnchantsOnItem(ItemStack itemStack) {
        if(ItemUtil.hasNoItemMeta(itemStack))
            return new Object2ObjectOpenHashMap<>();

        NBTItem nbtItem = new NBTItem(itemStack);

        Object2ObjectOpenHashMap<EnchantSave, Integer> toReturn = new Object2ObjectOpenHashMap<>();

        for(EnchantSave enchant : CustomEnchantConfig.getInstance().enchantList) {
            int level = getLevel(nbtItem, enchant.getName());

            if(level > 0)
                if (!toReturn.containsKey(enchant)) toReturn.put(enchant, level);
        }

        return toReturn;
    }

    public int getLevel(NBTItem nbtItem, String enchantName) {
        NBTCompound compound = nbtItem.getCompound("velocity_customenchants_enchants");

        if(compound == null)
            return 0;

        return compound.getInteger(enchantName);
    }

    public void runArmorEquip(ArmorEquipEvent event) {
        Player player = event.getPlayer();

        removeOldEnchants(player, event.getOldArmorPiece());

        addNewEnchants(player, event.getNewArmorPiece());
    }

    public void addNewEnchants(Player player, ItemStack itemStack) {
        Object2ObjectMap<EnchantSave, Integer> newEnchants = getEnchantsOnItem(itemStack);

        for (Object2ObjectMap.Entry<EnchantSave, Integer> entry : newEnchants.object2ObjectEntrySet()) {

            EnchantSave enchant = entry.getKey();

            if (!EnchantManager.enchantList.get(enchant.getName()).isVanillaEnchant() || !enchant.isEnabled()) continue;

            player.addPotionEffect(EnchantUtil.getEffect(enchant.getEnchant().replace("<level>", entry.getValue() - 1 + "")));
        }
    }

    private void removeOldEnchants(Player player, ItemStack itemStack) {

        Object2ObjectMap<EnchantSave, Integer> enchants = getEnchantsOnItem(itemStack);

        enchants.forEach((enchant, level) -> {
            if (!EnchantManager.enchantList.get(enchant.getName()).isVanillaEnchant()) return;

            player.removePotionEffect(EnchantUtil.getEffect(enchant.getEnchant().replace("<level>", level - 1 + "")).getType());
        });

    }

    public void handleEnchants(Player player, boolean doAdd) {
        Object2ObjectMap<EnchantSave, Integer> allEnchants = getEnchantsFromPlayer(player);

        allEnchants.forEach((enchant, level) -> {

            if (!EnchantManager.enchantList.get(enchant.getName()).isVanillaEnchant()) return;

            if (doAdd) player.addPotionEffect(EnchantUtil.getEffect(enchant.getEnchant().replace("<level>", level - 1 + "")));
            else player.removePotionEffect(EnchantUtil.getEffect(enchant.getEnchant().replace("<level>", level - 1 + "")).getType());

        });
    }

    public Object2ObjectMap<EnchantSave, Integer> getEnchantsFromPlayer(Player player) {
        EntityPlayer entityPlayer = ((CraftPlayer) player).getHandle();

        Object2ObjectMap<EnchantSave, Integer> toReturn = new Object2ObjectOpenHashMap<>();

        for(net.minecraft.server.v1_8_R3.ItemStack nmsStack : entityPlayer.getEquipment()) {
            gatherEnchantsFromItemFast(nmsStack, toReturn);
        }

        gatherEnchantsFromItemFast(entityPlayer.inventory.getItemInHand(), toReturn);

        return toReturn;
    }

    public void gatherEnchantsFromItemFast(net.minecraft.server.v1_8_R3.ItemStack nmsStack, Object2ObjectMap<EnchantSave, Integer> map) {
        if(nmsStack == null)
            return;

        NBTTagCompound tag = nmsStack.getTag();

        if(tag == null)
            return;

        NBTTagCompound subCompound = tag.getCompound("velocity_customenchants_enchants");

        if(subCompound == null)
            return;

        for(String enchantKey : subCompound.c()) {
            EnchantSave enchant = getEnchant(enchantKey);

            if(enchant == null)
                continue;

            int level = subCompound.getInt(enchantKey);

            map.put(enchant, level);
        }
    }

    public boolean hasEnoughXP(Player player, int enchantCost) {
        int playerEXP = Exp.getTotalExperience(player);

        if(playerEXP < enchantCost) {

            player.sendMessage(VelocityFeatures.chat(CustomEnchantConfig.getInstance().getNotEnoughXP())
                    .replace("<player_xp>", playerEXP + "")
                    .replace("<required>", enchantCost + "")
            );

            return false;
        }

        return true;
    }

    public Object2ObjectMap.Entry<EnchantSave, Integer> getEnchantByEffect(Player player, String effect) {
        return getEnchantsFromPlayer(player).object2ObjectEntrySet().stream().filter(obj -> obj.getKey().getEnchant().contains(effect)).findFirst().orElse(null);
    }

    public <T, Q> Object2ObjectLinkedOpenHashMap<T, Q> reverseMap(Object2ObjectLinkedOpenHashMap<T, Q> toReverse) {
        Object2ObjectLinkedOpenHashMap<T, Q> reversedMap = new Object2ObjectLinkedOpenHashMap<>();
        ObjectArrayList<T> reverseOrderedKeys = new ObjectArrayList<>(toReverse.keySet());

        Collections.reverse(reverseOrderedKeys);

        reverseOrderedKeys.forEach((key) -> reversedMap.put(key, toReverse.get(key)));

        return reversedMap;
    }

    public Object2ObjectLinkedOpenHashMap<EnchantSave, Integer> sortMap(Object2ObjectLinkedOpenHashMap<EnchantSave, Integer> map) {

        return map.entrySet().stream().sorted(Comparator.comparing(o -> o.getKey().getEnchantTier()))
                .collect(Collectors.toMap(
                        k -> k.getKey(), v -> v.getValue(),
                        (a1, a2) -> {
                            throw new IllegalStateException();
                        },
                        () -> new Object2ObjectLinkedOpenHashMap<>()
                        )
                );

    }

    public boolean isInRegion(Location source) {
        boolean isInside = false;

        /*for (BoundsSave safeRegion : CustomEnchantConfig.getInstance().safeRegions) {
            Location loc1 = xyz.velocity.modules.util.Location.parseToLocation(safeRegion.getCorner1());
            Location loc2 = xyz.velocity.modules.util.Location.parseToLocation(safeRegion.getCorner2());

            if (!source.getWorld().equals(loc1.getWorld())) continue;
            if (!source.getWorld().equals(loc2.getWorld())) continue;

            isInside = source.getX() >= Math.min(loc1.getX(), loc2.getX()) &&
                    source.getY() >= Math.min(loc1.getY(), loc2.getY()) &&
                    source.getZ() >= Math.min(loc1.getZ(), loc2.getZ()) &&
                    source.getX() <= Math.max(loc1.getX(), loc2.getX()) &&
                    source.getY() <= Math.max(loc1.getY(), loc2.getY()) &&
                    source.getZ() <= Math.max(loc1.getZ(), loc2.getZ());
        }*/

        return isInside;
    }

    public boolean canDamage(Player violator, Player violated) {
        WorldGuardPlugin worldGuardPlugin = (WorldGuardPlugin) Bukkit.getPluginManager().getPlugin("WorldGuard");

        if(worldGuardPlugin == null) return true;
        if(!worldGuardPlugin.isEnabled()) return true;

        ProtectionQuery protectionQuery = worldGuardPlugin.createProtectionQuery();

        return protectionQuery.testEntityDamage(violator, violated);
    }

    public boolean canDamage(Player violator, Entity violated) {
        WorldGuardPlugin worldGuardPlugin = (WorldGuardPlugin) Bukkit.getPluginManager().getPlugin("WorldGuard");

        if(worldGuardPlugin == null) return true;
        if(!worldGuardPlugin.isEnabled()) return true;

        ProtectionQuery protectionQuery = worldGuardPlugin.createProtectionQuery();

        return protectionQuery.testEntityDamage(violator, violated);
    }

    public boolean isAlly(Player violator, Player violated) {
        return FactionsKore.getIntegration().hasFaction(violator) && FactionsKore.getIntegration().getRelationToPlayer(violator, violated).isGreaterThan(TranslatedRelation.NEUTRAL);
    }

    public boolean canBuild(Player player, Location location) {

        if (!FactionsKore.getIntegration().playerCanBuildThere(player, location)) return false;

        WorldGuardPlugin worldGuardPlugin = (WorldGuardPlugin) Bukkit.getPluginManager().getPlugin("WorldGuard");

        if(worldGuardPlugin == null) return false;
        if(!worldGuardPlugin.isEnabled()) return false;

        ProtectionQuery protectionQuery = worldGuardPlugin.createProtectionQuery();

        return protectionQuery.testBlockPlace(player, player.getLocation(), player.getItemInHand().getType());

    }

    public int getRandomChance() {
        return ThreadLocalRandom.current().nextInt(100);
    }

    public boolean checkBookChances(InventoryClickEvent event, Player player, NBTCompound compound, ItemStack item, NBTItem nbtItem) {
        CustomEnchantConfig config = CustomEnchantConfig.getInstance();

        if (!config.chance.isEnabled()) return true;

        EnchantSave id = getEnchant(compound.getString("id"));

        int success = compound.getInteger("success") + Garrison.getInstance().getBookSuccessIncrease(player);
        int fail = compound.getInteger("fail");

        int chance = ThreadLocalRandom.current().nextInt(100);

        if (chance <= success) {
            return true;
        } else {
            if (config.chance.isBreakOnFail()) {
                int newChance = ThreadLocalRandom.current().nextInt(100);

                if (newChance <= fail) {

                    NBTCompound scrollCompound = nbtItem.getCompound("velocity_enchantItem_whiteScroll");

                    if (scrollCompound == null || !scrollCompound.getBoolean("protected")) {
                        updateItem(player, item, event.getSlot());

                        event.setCurrentItem(null);
                    } else {
                        scrollCompound.setBoolean("protected", false);
                        EnchantItems.getInstance().removeLore(nbtItem.getItem(), config.chance.getItems().stream().filter(obj -> obj.getName().equals("white_scroll")).findFirst().orElse(null).getAppliedLore());

                        event.setCurrentItem(nbtItem.getItem());
                    }

                    player.sendMessage(VelocityFeatures.chat(config.enchantFailedBrokeMsg
                            .replace("<enchant>", id.getDisplayName())
                    ));

                    return false;
                }
            }

            player.sendMessage(VelocityFeatures.chat(config.enchantFailedMsg
                    .replace("<enchant>", id.getDisplayName())
            ));

            return false;
        }
    }

    public NBTCompound getCompound(NBTItem nbtItem) {

        try {
            if (nbtItem.getCompound("velocity_customenchants_book") != null) {
                return nbtItem.getCompound("velocity_customenchants_book");
            }

            if (nbtItem.getCompound("velocity_enchantItem_item") != null) {
                return nbtItem.getCompound("velocity_enchantItem_item");
            }
        } catch (NoClassDefFoundError err) {
            return null;
        }

        return null;
    }

    public boolean isSilenced(Player player) {
        UUID id = player.getUniqueId();

        if (Silence.silenced.containsKey(id)) {
            if (System.currentTimeMillis() >= Silence.silenced.get(id)) {
                Silence.silenced.remove(id);
                return false;
            } else {
                return true;
            }
        }

        return false;
    }

    public void addEnchantNBT(NBTItem toApply, NBTItem takeFrom) {
        NBTCompound compound = takeFrom.getCompound("velocity_customenchants_enchants");
        NBTCompound compound1 = toApply.getCompound("velocity_customenchants_enchants");

        if (compound == null) return;
        if (compound1 == null) compound1 = toApply.addCompound("velocity_customenchants_enchants");

        for (String key : compound.getKeys()) {
            compound1.setInteger(key, compound.getInteger(key));
        }
    }

    public boolean isApplicable(ItemStack item, String applicable) {
        String t = item.getType().name();

        if (applicable.equalsIgnoreCase("weapon")) {
            if (t.endsWith("SWORD")) return true;
            if (t.endsWith("AXE")) return true;
            if (t.endsWith("HOE")) return true;
        }

        if (applicable.equalsIgnoreCase("armor")) {
            if (t.endsWith("HELMET")) return true;
            if (t.endsWith("CHESTPLATE")) return true;
            if (t.endsWith("LEGGINGS")) return true;
            if (t.endsWith("BOOTS")) return true;
        }

        return t.endsWith(applicable);

    }

    public void giveKit(Player player, String kitName) {

        KitSave kit = KitsConfig.getInstance().kits.stream().filter(obj -> obj.getName().equals(kitName)).findFirst().orElse(null);

        if (kit == null) return;

        kit.getItems().forEach(item -> {

            ItemStack itemStack = createItem(kit, item);

            if (itemStack == null) return;

            player.getInventory().addItem(itemStack);

        });

        try {
            for (CommandSave command : kit.getCommands()) {
                double chance = EnchantUtil.getRandomDouble();

                if (chance <= command.getChance()) {
                    Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(),
                            command.getCommand().replace("<player>", player.getName()));
                }
            }
        } catch (Throwable err) {
            return;
        }

    }

    public ItemStack createItem(KitSave kit, Item item) {
        ItemStack itemStack = new ItemStack(Material.getMaterial(item.getItem()), 1);

        ItemMeta meta = itemStack.getItemMeta();

        String lore = VelocityFeatures.chat(String.join("VDIB", item.getLore()));

        meta.setDisplayName(VelocityFeatures.chat(item.getDisplayName()));
        meta.setLore(Arrays.asList(lore.split("VDIB")));

        itemStack.setItemMeta(meta);

        addEnchantsToItem(itemStack, item.getEnchants());
        ItemStack finishedItem = addCEtoItem(kit, item, itemStack);

        return finishedItem;
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

    private ItemStack addCEtoItem(KitSave kit, Item item, ItemStack itemStack) {

        List<EnchantSave> enchantList = CustomEnchantConfig.getInstance().getEnchantList();

        if (enchantList == null) return null;

        CustomEnchants cE = CustomEnchants.getInstance();

        ItemStack finishedItem = itemStack;
        List<String> finishedLore = new ArrayList<>();
        LinkedHashMap<EnchantSave, Integer> enchants = new LinkedHashMap<>();

        NBTItem nbtItem = new NBTItem(finishedItem);

        if (nbtItem == null) return null;

        NBTCompound nbtCompound = nbtItem.getCompound("velocity_customenchants_enchants");

        if (nbtCompound == null) nbtCompound = nbtItem.addCompound("velocity_customenchants_enchants");

        for (KitEnchant customEnchant : item.getCustomEnchants()) {
            EnchantSave enchant = enchantList.stream().filter(obj -> obj.getName().equals(customEnchant.getEnchantName())).findFirst().orElse(null);

            if (enchant == null) continue;

            int chance = ThreadLocalRandom.current().nextInt(100);
            if (chance > customEnchant.getChance()) continue;

            int onLevel = nbtCompound.getInteger(enchant.getName());
            int toApply = customEnchant.isRandomLevel() ? (int) (Math.random() * ((customEnchant.getMaxLevel() + 1) - 1)) + 1 : customEnchant.getMaxLevel();

            if (customEnchant.getMaxLevel() < toApply) toApply = customEnchant.getMaxLevel();
            if (onLevel > 0) continue;

            nbtCompound.setInteger(enchant.getName(), toApply);

            ItemStack finished = nbtItem.getItem();

            for (String key : nbtCompound.getKeys()) {

                EnchantSave ench = cE.getEnchant(key);

                if (ench == null) continue;

                enchants.put(enchant, enchant.getMaxLevel());

            }

            ItemMeta im = finished.getItemMeta();
            finishedItem.setItemMeta(im);
        }

        ItemMeta meta = finishedItem.getItemMeta();

        if (itemStack.getItemMeta().hasDisplayName() && itemStack.getItemMeta().getDisplayName().contains("<enchantSize>")) {
            meta.setDisplayName(itemStack.getItemMeta().getDisplayName().replace("<enchantSize>", enchants.size() + ""));
        }

        meta.setLore(EnchantItems.getInstance().getNewLore(finishedItem, nbtCompound, true));

        finishedItem.setItemMeta(meta);

        return finishedItem;

    }

    public EnchantSave getRandomEnchant(List<EnchantSave> enchantSaves) {

        if (enchantSaves.size() < 1) {
            return null;
        }

        double totalChances = 0.0;

        for (EnchantSave enchant : enchantSaves) {
            totalChances += enchant.getChance();
        }

        int index = 0;

        for (double r = Math.random() * totalChances; index < enchantSaves.size() - 1; ++index) {
            r -= enchantSaves.get(index).getChance();
            if (r <= 0.0) break;
        }

        EnchantSave enchant = enchantSaves.get(index);

        if (enchant == null) return null;

        return enchant;
    }

    public EnchantProcEvent callEvent(Player activator, Entity victim, AbstractEnchant enchant) {
        EnchantProcEvent event = new EnchantProcEvent(activator, victim, enchant);
        Bukkit.getServer().getPluginManager().callEvent(event);

        return event;
    }

    public void addPlayerGear(Player player, PlayerGearSave playerGearSave) {
        KitSave kit = KitsConfig.getInstance().kits.stream()
                .filter(obj -> obj.getName().equals(playerGearSave.getCustomEnchantKit())).findFirst()
                .orElse(null);

        if (kit == null) {
            return;
        }

        addKit(player, kit);

        for (String command : playerGearSave.getCommands()) {
            Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(),
                    command.replace("<player>", player.getName()));
        }

    }

    public void addPlayerGear(Player player, String kitName) {
        KitSave kit = KitsConfig.getInstance().kits.stream()
                .filter(obj -> obj.getName().equals(kitName)).findFirst()
                .orElse(null);

        if (kit == null) {
            return;
        }

        addKit(player, kit);
    }

    public void addKit(Player player, KitSave kit) {
        kit.getItems().forEach(item -> {

            ItemStack itemStack = CustomEnchants.getInstance().createItem(kit, item);

            if (itemStack == null) {
                return;
            }

            String type = itemStack.getType().name();

            if (type.endsWith("_HELMET")) {
                player.getEquipment().setHelmet(itemStack);
            }

            else if (type.endsWith("_CHESTPLATE")) {
                player.getEquipment().setChestplate(itemStack);
            }

            else if (type.endsWith("_LEGGINGS")) {
                player.getEquipment().setLeggings(itemStack);
            }

            else if (type.endsWith("_BOOTS")) {
                player.getEquipment().setBoots(itemStack);
            }

            else {
                player.getInventory().addItem(itemStack);
            }

            CustomEnchants.getInstance().addNewEnchants(player, itemStack);
        });
    }

    public void updateItem(Player player, ItemStack itemStack, int slot) {
        int set = itemStack.getAmount() - 1;

        if (set == 0) {
            player.getInventory().setItem(slot, new ItemStack(Material.AIR));
            player.updateInventory();
            return;
        }

        player.getItemInHand().setAmount(set);
        player.updateInventory();
    }

    @Override
    public String getName() {
        return "custom_enchants";
    }

    @Override
    public boolean isEnabled() {
        return CustomEnchantConfig.getInstance().isEnabled();
    }

    @Override
    public void onEnable() {
        new EnchantItems();

        VelocityFeatures.registerEvent(new EnchantListener());
        VelocityFeatures.registerEvent(new InventoryListener());
        CommandAPI.getInstance().enableCommand(new CustomEnchantCommand());

        CustomEnchantConfig.getInstance().setEnabled(true);
    }

    @Override
    public void onDisable() {
        VelocityFeatures.unregisterEvent(EnchantListener.getInstance());
        VelocityFeatures.unregisterEvent(InventoryListener.getInstance());
        CommandAPI.getInstance().disableCommand(CustomEnchantCommand.class);

        CustomEnchantConfig.getInstance().setEnabled(false);
    }

    @Override
    public String placeholderRequest(Player player, String arg) {
        return "";
    }

}
