package FuckParty.fuckVolkoff.src.main.java.xyz.velocity.modules.customenchants;

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import de.tr7zw.nbtapi.NBTCompound;
import de.tr7zw.nbtapi.NBTItem;
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import xyz.velocity.VelocityFeatures;
import xyz.velocity.modules.armorsets.ArmorSets;
import xyz.velocity.modules.customenchants.config.CustomEnchantConfig;
import xyz.velocity.modules.customenchants.config.saves.EnchantItemSave;
import xyz.velocity.modules.customenchants.config.saves.EnchantSave;
import xyz.velocity.modules.customenchants.enchants.util.DeathWrapper;
import xyz.velocity.modules.util.EnchantUtil;
import xyz.velocity.modules.util.ItemUtil;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class EnchantItems {

    public EnchantItems() {
        instance = this;
    }

    public static Object2ObjectMap<UUID, DeathWrapper> toReturn = new Object2ObjectOpenHashMap<>();

    public void applyEnchant(InventoryClickEvent e) {

        CustomEnchants cE = CustomEnchants.getInstance();
        CustomEnchantConfig config = CustomEnchantConfig.getInstance();

        NBTItem cursorIM = new NBTItem(e.getCursor());
        NBTCompound compound = cursorIM.getCompound("velocity_customenchants_book");

        if (compound == null) return;

        int level = compound.getInteger("level");

        ItemStack itemStack = e.getCurrentItem();

        EnchantSave id = cE.getEnchant(compound.getString("id"));

        if (id == null) return;

        if (!cE.isApplicable(itemStack, id.getApplicableTo())) {

            e.getWhoClicked().sendMessage(VelocityFeatures.chat(config.getCannotApply()
                    .replace("<applicable>", id.getApplicableTo())
            ));

            return;

        }

        NBTItem nbtItem = new NBTItem(itemStack);
        NBTCompound nbtCompound = nbtItem.getCompound("velocity_customenchants_enchants");

        if (nbtCompound == null) nbtCompound = nbtItem.addCompound("velocity_customenchants_enchants");

        boolean checkEnchants = checkArmorOrb(nbtItem.getItem(), nbtItem);

        if (!checkEnchants) return;

        int onLevel = nbtCompound.getInteger(id.getName());
        int toApply = level;

        if (onLevel > 0) return;

        if (!cE.checkBookChances(e, ((Player) e.getWhoClicked()).getPlayer(), compound, itemStack, nbtItem)) {
            e.setCursor(null);
            e.setCancelled(true);
            return;
        }

        nbtCompound.setInteger(id.getName(), toApply);

        ItemStack finished = nbtItem.getItem();

        ItemMeta im = finished.getItemMeta();

        im.setLore(getNewLore(finished, nbtCompound, false));

        finished.setItemMeta(im);

        e.setCurrentItem(finished);
        e.setCursor(null);
        e.setCancelled(true);

        ((Player) e.getWhoClicked()).playSound(e.getWhoClicked().getLocation(), Sound.ORB_PICKUP, 50.0F, 50.0F);
        ((Player) e.getWhoClicked()).sendMessage(VelocityFeatures.chat(config.getEnchantAppliedMsg().replace("<enchant>", id.getDisplayName() + " " + EnchantUtil.toRomanNumerals(toApply))));

    }

    public List<String> getNewLore(ItemStack itemStack, NBTCompound compound, boolean organize) {
        List<String> newLore = new ArrayList<>();
        List<String> finishedLore = itemStack.getItemMeta().getLore() != null ? CustomEnchants.getInstance().resetLore(itemStack.getItemMeta().getLore(), compound, null, 0, organize) : new ArrayList<>();
        Object2ObjectLinkedOpenHashMap<EnchantSave, Integer> enchants = new Object2ObjectLinkedOpenHashMap<>();

        for (String key : compound.getKeys()) {

            EnchantSave enchant = CustomEnchants.getInstance().getEnchant(key);

            if (enchant == null) continue;

            int level = compound.getInteger(key);
            enchants.put(enchant, level);

        }

        if (organize) {
            Object2ObjectLinkedOpenHashMap<EnchantSave, Integer> newList = CustomEnchants.getInstance().sortMap(enchants);

            for (Object2ObjectMap.Entry<EnchantSave, Integer> enchant : CustomEnchants.getInstance().reverseMap(newList).object2ObjectEntrySet()) {
                newLore.add(VelocityFeatures.chat(enchant.getKey().getDisplayName() + " " + EnchantUtil.toRomanNumerals(enchant.getValue())));
            }
        } else {
            for (Map.Entry<EnchantSave, Integer> enchant : enchants.entrySet()) {
                newLore.add(VelocityFeatures.chat(enchant.getKey().getDisplayName() + " " + EnchantUtil.toRomanNumerals(enchant.getValue())));
            }
        }

        newLore.addAll(finishedLore);

        return newLore;
    }

    public void applyItem(InventoryClickEvent e) {

        NBTItem cursorIM = new NBTItem(e.getCursor());
        NBTCompound compound = cursorIM.getCompound("velocity_enchantItem_item");

        if (compound == null) return;
        if (!isApplicable(e.getCurrentItem())) return;

        String id = compound.getString("id");

        ItemManager.itemList.get(id).runTask(e);

    }

    public ItemStack buildItem(String type, int amount) {

        EnchantItemSave item = CustomEnchantConfig.getInstance().chance.getItems().stream().filter(obj -> obj.getName().equals(type)).findFirst().orElse(null);

        if (item == null || !item.isEnabled()) return null;

        ItemStack itemStack = new ItemStack(Material.getMaterial(item.getMaterial()), amount, (byte) item.getDamage());

        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName(VelocityFeatures.chat(item.getDisplayName()));

        String join = String.join("VDIB", item.getLore());

        int success = 0;
        int fail = 0;

        if (join.contains("<success>")) {
            success = CustomEnchants.getInstance().getRandomChance();
            fail = CustomEnchants.getInstance().getRandomChance();

            join = join
                    .replace("<success>", success + "")
                    .replace("<fail>", fail + "");
        }

        String lore = VelocityFeatures.chat(join);

        itemMeta.setLore(Arrays.asList(lore.split("VDIB")));
        itemStack.setItemMeta(itemMeta);

        NBTItem nbtItem = new NBTItem(itemStack);

        NBTCompound nbtCompound = nbtItem.addCompound("velocity_enchantItem_item");

        nbtCompound.setString("id", type);
        nbtCompound.setBoolean("glow", item.isGlow());
        nbtCompound.setInteger("success", success);
        nbtCompound.setInteger("fail", fail);

        return nbtItem.getItem();

    }

    public boolean doesGlow(ItemStack itemStack) {
        NBTItem nbtItem = new NBTItem(itemStack);

        NBTCompound compound = nbtItem.getCompound("velocity_enchantItem_item");

        if (compound == null) return false;

        return compound.getBoolean("glow");
    }

    public ItemStack addGlow(ItemStack item) {
        if (!doesGlow(item)) return item;

        item.addUnsafeEnchantment(Enchantment.DURABILITY, 1);
        ItemMeta meta = item.getItemMeta();
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        item.setItemMeta(meta);

        return item;
    }

    public void updateItem(InventoryClickEvent e) {
        ItemStack itemStack = e.getCursor();

        int set = itemStack.getAmount() - 1;

        if (set == 0) {
            e.setCursor(null);
            return;
        }

        itemStack.setAmount(set);
        e.setCursor(itemStack);
    }

    private boolean isApplicable(ItemStack itemStack) {
        String name = itemStack.getType().name();

        if (name.endsWith("_HELMET")) return true;
        if (name.endsWith("_CHESTPLATE")) return true;
        if (name.endsWith("_LEGGINGS")) return true;
        if (name.endsWith("_BOOTS")) return true;
        if (name.endsWith("_SWORD")) return true;
        if (name.endsWith("_AXE")) return true;
        if (name.endsWith("_PICKAXE")) return true;
        if (name.endsWith("_HOE")) return true;

        return false;
    }

    public void removeLore(ItemStack item, List<String> lookup) {
        ItemMeta meta = item.getItemMeta();
        List<String> newLore = meta.getLore();

        if (meta.getLore() == null) return;

        for (String s : meta.getLore()) {
            for (String s1 : lookup) {
                if (s == "" || s1 == "") continue;

                if (s.equals(VelocityFeatures.chat(s1))) newLore.remove(s);
            }
        }

        meta.setLore(newLore);

        item.setItemMeta(meta);
    }

    public void updateLore(ItemStack item, String lookup, String replace) {
        ItemMeta meta = item.getItemMeta();
        List<String> newLore = meta.getLore();

        if (meta.getLore() == null) return;

        for (int i = 0; i < newLore.size(); i++) {
            String s = newLore.get(i);

            if (s.contains(lookup)) {
                s = s.replace(lookup, replace);
            }

            newLore.remove(i);
            newLore.add(i, s);
        }

        meta.setLore(newLore);

        item.setItemMeta(meta);
    }

    public void updateArmorNBT(ItemStack itemStack, String name) {
        NBTItem armorNBT = new NBTItem(itemStack);
        NBTCompound armorCompound = armorNBT.getCompound("velocity_armorsets_armor");

        if (!armorCompound.getString("ability").isEmpty()) return;

        armorCompound.setString("ability", name);

        itemStack.setData(armorNBT.getItem().getData());
        itemStack.setItemMeta(armorNBT.getItem().getItemMeta());
        itemStack.setType(armorNBT.getItem().getType());
    }

    private boolean checkArmorOrb(ItemStack item, NBTItem originalNBT) {
        NBTCompound compound = originalNBT.getCompound("velocity_customenchants_enchants");

        List<String> enchants = new ArrayList<>();

        try {
            for (String key : compound.getKeys()) {

                EnchantSave enchant = CustomEnchants.getInstance().getEnchant(key);

                if (enchant == null) continue;

                enchants.add(enchant.getName());

            }
        } catch (Throwable e) {
            return true;
        }

        if (enchants.size() >= CustomEnchantConfig.getInstance().maxEnchantsWithUpgrade) {
            return false;
        }

        if (enchants.size() >= CustomEnchantConfig.getInstance().defaultMaxEnchants) {
            NBTCompound orbCompound = originalNBT.getCompound("velocity_enchantItem_armorOrb");

            if (orbCompound != null && orbCompound.getInteger("slots") != null && orbCompound.getInteger("slots") >= 1) {
                removeLore(item, CustomEnchantConfig.getInstance().chance.getItems().stream().filter(obj -> obj.getName().equals("armor_orb")).findFirst().orElse(null).getAppliedLore());
                orbCompound.setInteger("slots", 0);
                return true;
            }
            return false;
        }

        return true;

    }

    public void deathEvent(PlayerDeathEvent e) {
        ObjectArrayList<ItemStack> getItems = getProtectedItems(e.getDrops());

        toReturn.put(e.getEntity().getUniqueId(), new DeathWrapper(getItems, System.currentTimeMillis() + 5000L));
    }

    public ObjectArrayList<ItemStack> getProtectedItems(List<ItemStack> inventory) {
        ObjectArrayList<ItemStack> items = new ObjectArrayList<>();

        for (ItemStack item : inventory) {
            if (item == null) continue;
            if (isApplicable(item)) {
                NBTItem nbtItem = new NBTItem(item);
                NBTCompound compound = nbtItem.getCompound("velocity_enchantItem_holyWhiteScroll");

                if (compound == null) continue;

                if (compound.getBoolean("deathProtection") == true) items.add(item);
            }
        }

        return items;
    }

    private void transmogLore(InventoryClickEvent e) {
        ItemStack itemStack = e.getCurrentItem();

        NBTItem nbtItem = new NBTItem(itemStack);
        NBTCompound enchantsCompound = nbtItem.getCompound("velocity_customenchants_enchants");

        if (enchantsCompound == null) {
            e.setCancelled(true);
            return;
        }

        ItemStack finished = nbtItem.getItem();

        List<String> lore = getNewLore(finished, enchantsCompound, true);

        ItemMeta itemMeta = finished.getItemMeta();
        itemMeta.setDisplayName(getTransmogDisplayName(nbtItem.getItem()));

        itemMeta.setLore(lore);
        finished.setItemMeta(itemMeta);

        e.setCurrentItem(finished);
        e.setCancelled(true);

        updateItem(e);
    }

    public String getTransmogDisplayName(ItemStack itemStack) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        String enchantSize = getEnchantSize(itemStack);

        if (enchantSize == null) return null;
        if (itemMeta.hasDisplayName()) {
            String displayName = removeTransmogLore(itemMeta.getDisplayName());

            displayName += CustomEnchantConfig.getInstance().enchantSizeLayout.replace("<enchantSize>", enchantSize);

            return VelocityFeatures.chat(displayName);
        }

        return "";
    }

    private String getEnchantSize(ItemStack itemStack) {
        NBTItem nbtItem = new NBTItem(itemStack);
        NBTCompound enchantsCompound = nbtItem.getCompound("velocity_customenchants_enchants");

        if (enchantsCompound == null) {
            return null;
        }

        return enchantsCompound.getKeys().size() + "";
    }

    private String removeTransmogLore(String string) {
        String regex = CustomEnchantConfig.getInstance().enchantSizeLayout
                .replace("(", "\\(")
                .replace(")", "\\)")
                .replace("[", "\\[")
                .replace("]", "\\]")
                .replace("<enchantSize>", "[0-9]+");

        return string
                .replaceAll("\\u00A7", "&")
                .replaceAll(regex, "");
    }

    public void addEnchantsToItem(ItemStack item, Enchantment enchantment, int level, boolean addOntoExisting) {
        try {
            int lvl = level;

            if (addOntoExisting) {
                if (item.getEnchantments().containsKey(enchantment)) {
                    lvl = item.getEnchantmentLevel(enchantment) + 1;
                }
            }

            item.addUnsafeEnchantment(enchantment, lvl);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public static boolean isWeapon(ItemStack item) {
        String t = item.getType().name();

        if (t.endsWith("_AXE")) return true;
        if (t.endsWith("_SWORD")) return true;
        if (t.endsWith("_HOE")) return true;

        return false;
    }

    public boolean isArmor(ItemStack item) {
        String t = item.getType().name();

        if (t.endsWith("_HELMET")) return true;
        if (t.endsWith("_CHESTPLATE")) return true;
        if (t.endsWith("_LEGGINGS")) return true;
        if (t.endsWith("_BOOTS")) return true;

        return false;
    }

    public void updateDisplayName(NBTItem item, EnchantItemSave enchantItemSave) {
        JsonObject obj = new Gson().fromJson(enchantItemSave.getExtra().get(0), JsonObject.class);

        ItemStack itemStack = item.getItem();
        ItemMeta meta = itemStack.getItemMeta();

        String displayName = removeTransmogLore(meta.getDisplayName());//.replaceAll("&[0-9a-z]", "");

        String updated = (obj.get("displayName").getAsString()).replace("<original>", displayName);

        if (updated == null) return;

        updated += CustomEnchantConfig.getInstance().enchantSizeLayout.replace("<enchantSize>", getEnchantSize(item.getItem()));

        meta.setDisplayName(VelocityFeatures.chat(updated));
        itemStack.setItemMeta(meta);
    }

    private void blackScroll(InventoryClickEvent e) {
        Player player = (Player) e.getWhoClicked();
        ItemStack itemStack = e.getCurrentItem();

        NBTItem nbtItem = new NBTItem(itemStack);
        NBTItem blackScroll = new NBTItem(e.getCursor());

        NBTCompound enchantsCompound = nbtItem.getCompound("velocity_customenchants_enchants");
        NBTCompound scrollCompound = blackScroll.getCompound("velocity_enchantItem_item");

        if (enchantsCompound == null || enchantsCompound.getKeys().size() < 1) {
            e.setCancelled(true);
            return;
        }

        int success = scrollCompound.getInteger("success");
        int fail = scrollCompound.getInteger("fail");

        if (!checkChances(e, itemStack, success, fail)) {
            e.setCancelled(true);
            updateItem(e);

            return;
        }

        List<EnchantSave> enchantList = getEnchants(enchantsCompound);
        EnchantSave randomEnchant = CustomEnchants.getInstance().getRandomEnchant(enchantList);

        if (randomEnchant == null) return;

        int level = enchantsCompound.getInteger(randomEnchant.getName());
        enchantsCompound.removeKey(randomEnchant.getName());

        ItemStack newItem = nbtItem.getItem();
        ItemStack book = CustomEnchants.getInstance().buildBook(randomEnchant, level, success, fail);

        List<String> lore = getNewLore(newItem, enchantsCompound, false);

        ItemMeta itemMeta = newItem.getItemMeta();

        itemMeta.setLore(lore);
        newItem.setItemMeta(itemMeta);

        e.setCurrentItem(newItem);
        e.setCancelled(true);

        player.getInventory().addItem(book);
        player.sendMessage(VelocityFeatures.chat(CustomEnchantConfig.getInstance().blackScrollSuccess
                .replace("<enchant>", randomEnchant.getDisplayName())
        ));

        updateItem(e);
    }

    public double getHeroicWeaponMultiplier(Player player) {
        if (!ItemUtil.isAirOrNull(player.getItemInHand())) {
            NBTItem item = new NBTItem(player.getItemInHand());
            NBTCompound compound = item.getCompound("velocity_enchantItem_heroicWeapon");

            if (compound == null) return 1;
            EnchantItemSave eis = CustomEnchantConfig.getInstance().chance.getItems().stream().filter(
                    obj -> obj.getName().equals(compound.getString("id"))
            ).findFirst().orElse(null);

            if (eis == null || !eis.isEnabled()) return 1;

            try {
                JsonObject obj = new Gson().fromJson(eis.getExtra().get(0), JsonObject.class);
                double multiplier = obj.get("damageMulti").getAsDouble();

                return multiplier;
            } catch (Throwable err) {
                err.printStackTrace();
            }

        }

        return 1;
    }

    public double getHeroicArmorMultiplier(Player player) {
        double damageReduction = 1.0;

        for (ItemStack armorContent : player.getInventory().getArmorContents()) {
            if (ItemUtil.isAirOrNull(armorContent)) continue;

            NBTItem item = new NBTItem(armorContent);
            NBTCompound compound = item.getCompound("velocity_enchantItem_heroicArmor");

            if (compound == null) continue;

            EnchantItemSave eis = CustomEnchantConfig.getInstance().chance.getItems().stream().filter(
                    obj -> obj.getName().equals(compound.getString("id"))
            ).findFirst().orElse(null);

            if (eis == null || !eis.isEnabled()) continue;

            try {
                JsonObject obj = new Gson().fromJson(eis.getExtra().get(0), JsonObject.class);
                double toReduce = obj.get("damageReduce").getAsDouble();

                damageReduction -= toReduce;
            } catch (Throwable err) {
                err.printStackTrace();
            }
        }

        return damageReduction;
    }

    public boolean checkChances(InventoryClickEvent e, ItemStack item, int success, int fail) {
        CustomEnchantConfig config = CustomEnchantConfig.getInstance();
        Player player = (Player) e.getWhoClicked();

        int chance = ThreadLocalRandom.current().nextInt(100);

        if (chance <= success) {
            return true;
        } else {
            int newChance = ThreadLocalRandom.current().nextInt(100);

            if (newChance <= fail) {

                NBTItem nbtItem = new NBTItem(item);
                NBTCompound scrollCompound = nbtItem.getCompound("velocity_enchantItem_whiteScroll");

                if (scrollCompound == null || !scrollCompound.getBoolean("protected")) {
                    player.getInventory().remove(item);

                    e.setCurrentItem(null);
                } else {
                    scrollCompound.setBoolean("protected", false);
                    //EnchantItems.getInstance().removeLore(nbtItem.getItem(), config.chance.getItems().stream().filter(obj -> obj.getName().equals("white_scroll")).findFirst().orElse(null).getAppliedLore());

                    e.setCurrentItem(nbtItem.getItem());
                }

                player.sendMessage(VelocityFeatures.chat(config.blackScrollFailBreak));

                return false;
            }

            player.sendMessage(VelocityFeatures.chat(config.blackScrollFail));

            return false;
        }
    }

    public List<EnchantSave> getEnchants(NBTCompound compound) {
        List<EnchantSave> list = new ArrayList<>();

        for (String key : compound.getKeys()) {
            EnchantSave enchant = CustomEnchants.getInstance().getEnchant(key);

            if (enchant == null) continue;

            list.add(enchant);
        }

        return list;
    }

    public void applyAbilityOrb(InventoryClickEvent e, Player player, NBTCompound itemCompound) {
        UUID id = player.getUniqueId();
        ItemStack itemStack = e.getCurrentItem();

        NBTItem nbtItem = new NBTItem(itemStack);
        NBTCompound armorsetCompound = nbtItem.getCompound("velocity_armorsets_armor");

        if (armorsetCompound == null) return;

        EnchantItemSave enchantItemSave = CustomEnchantConfig.getInstance().chance.getItems().stream().filter(obj -> obj.getName().equals(itemCompound.getString("id"))).findFirst().orElse(null);

        if (enchantItemSave == null) return;
        if (!enchantItemSave.isEnabled()) return;
        if (!armorsetCompound.getString("ability").isEmpty()) return;

        armorsetCompound.setString("ability", enchantItemSave.getName());

        JsonObject obj = new Gson().fromJson(enchantItemSave.getExtra().get(0), JsonObject.class);

        String bindID = obj.get("setBind").getAsString();

        if (!bindID.isEmpty() && !ArmorSets.equippedSets.get(id).equals(bindID)) return;

        ItemStack finished = nbtItem.getItem();

        e.setCurrentItem(finished);
        e.setCancelled(true);

        updateItem(e);

        for (ItemStack armorContent : player.getInventory().getArmorContents()) {
            String lookup = VelocityFeatures.chat(obj.get("lookupString").getAsString());
            String replace = VelocityFeatures.chat(obj.get("replaceString").getAsString());

            updateLore(armorContent, lookup, replace);
            updateArmorNBT(armorContent, enchantItemSave.getName());
        }
    }

    private int getItemDropChance(ItemStack[] itemStacks) {
        return 0;
    }

    private static EnchantItems instance;

    public static EnchantItems getInstance() {
        return instance;
    }

}
