package FuckParty.fuckVolkoff.src.main.java.xyz.velocity.modules.masks;

import de.tr7zw.nbtapi.NBTCompound;
import de.tr7zw.nbtapi.NBTItem;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import xyz.velocity.VelocityFeatures;
import xyz.velocity.commands.CommandAPI;
import xyz.velocity.modules.masks.commands.MaskCommand;
import xyz.velocity.modules.masks.config.MaskConfig;
import xyz.velocity.modules.masks.config.MaskSave;
import xyz.velocity.modules.AbstractModule;
import xyz.velocity.modules.Module;
import xyz.velocity.modules.armorsets.ArmorSets;
import xyz.velocity.modules.customenchants.CustomEnchants;
import xyz.velocity.modules.util.EnchantUtil;
import xyz.velocity.modules.util.ItemUtil;
import xyz.velocity.modules.util.Pair;
import xyz.velocity.modules.util.SkullUtil;

import java.util.*;

@Module
public class Masks extends AbstractModule {

    public Masks() {
        instance = this;
    }

    @Getter
    private static Masks instance;

    public static Object2ObjectMap<String, CustomMask> maskList = new Object2ObjectOpenHashMap<>();
    public static Object2ObjectMap<UUID, ItemStack> equippedMasks = new Object2ObjectOpenHashMap<>();

    public void loadMasks() {
        maskList.clear();

        for (MaskSave mask : MaskConfig.getInstance().masks) {
            maskList.put(mask.getName(), new CustomMask(mask));
        }
    }

    public boolean hasMask(ItemStack item) {
        if (item == null) return false;

        NBTItem nbtItem = new NBTItem(item);

        if (nbtItem == null) return false;

        NBTCompound nbtCompound = nbtItem.getCompound("velocity_custommasks_mask");

        if (nbtCompound != null) return true;
        return false;
    }

    public boolean hasItem(ItemStack item) {
        if (item == null) return false;

        NBTItem nbtItem = new NBTItem(item);

        if (nbtItem == null) return false;

        NBTCompound nbtCompound = nbtItem.getCompound("velocity_custommasks_item");

        if (nbtCompound != null) return true;
        return false;
    }

    public CustomMask getMask(ItemStack item) {
        NBTItem nbtItem = new NBTItem(item);
        NBTCompound nbtCompound = nbtItem.getCompound("velocity_custommasks_mask");

        if (nbtCompound == null) return null;

        return maskList.get(nbtCompound.getString("id"));
    }

    public void removeEffects(Player p, ItemStack itemStack) {
        if (itemStack == null || itemStack.getType() == Material.AIR) return;
        if (hasMask(itemStack)) {
            NBTItem nbtItem = new NBTItem(itemStack);
            NBTCompound nbtCompound = nbtItem.getCompound("velocity_custommasks_mask");

            CustomMask mask = Masks.maskList.get(nbtCompound.getString("id"));

            if (mask == null) return;

            Pair<List<String>, List<String>> multiMaskList = getMultiMaskEffects(itemStack);

            for (String effect : mask.maskSave.getMultiMask().isEnabled() ? multiMaskList.second : mask.maskSave.getVanillaEffects()) {
                p.removePotionEffect(EnchantUtil.getEffect(effect).getType());
            }

            String effect = getEffect("HEALTH", mask.maskSave.getMultiMask().isEnabled() ? multiMaskList.first : mask.maskSave.getCustomEffects());

            if (effect == null) return;

            double healthToDouble = Double.parseDouble(effect);
            int toAdd = (int) healthToDouble;
            double newHP = getHealth(p, toAdd, true);

            if (newHP < 20) newHP = 20;

            p.setMaxHealth(newHP);
        }
    }

    public void addEffects(Player p, ItemStack item, CustomMask mask) {
        Pair<List<String>, List<String>> multiMaskList = getMultiMaskEffects(item);
        List<String> effectList = mask.maskSave.getMultiMask().isEnabled() ? multiMaskList.second : mask.maskSave.getVanillaEffects();

        for (String effect : effectList) {
            PotionEffect potionEffect = EnchantUtil.getEffect(effect);

            if (p.hasPotionEffect(potionEffect.getType())) {
                p.removePotionEffect(potionEffect.getType());
            }

            p.addPotionEffect(potionEffect);
        }

        String effect = getEffect("HEALTH", mask.maskSave.getMultiMask().isEnabled() ? multiMaskList.first : mask.maskSave.getCustomEffects());

        if (effect == null) return;

        double healthToDouble = Double.parseDouble(effect);
        int toAdd = (int) healthToDouble;

        double newHP = getHealth(p, toAdd, false);

        p.setMaxHealth(newHP);
    }

    public void equipMask(Player player, ItemStack armorPiece) {
        NBTItem originalItem = new NBTItem(armorPiece);
        NBTCompound originalCompound = originalItem.getCompound("velocity_custommasks_mask");

        CustomMask mask = maskList.get(originalCompound.getString("id"));

        if (mask == null) return;

        Masks.equippedMasks.put(player.getUniqueId(), armorPiece.clone());

        BukkitRunnable bukkitRunnable = new BukkitRunnable() {

            @Override
            public void run() {
                ItemStack itemStack = mask.getItem();

                NBTItem originalNBT = new NBTItem(armorPiece);

                originalNBT.applyNBT(itemStack);

                NBTItem nbtItem = new NBTItem(itemStack);

                NBTCompound nbtCompound = nbtItem.getCompound("velocity_custommasks_mask");

                if (nbtCompound == null) nbtCompound = nbtItem.addCompound("velocity_custommasks_mask");

                nbtCompound.setString("id", mask.maskSave.getName());
                nbtCompound.setString("type", armorPiece.getType().toString());

                if (mask.maskSave.getMultiMask().isEnabled()) {
                    nbtCompound.setString("masks", originalCompound.hasKey("masks") ? originalCompound.getString("masks") : "");
                }

                ItemStack finishedMask = SkullUtil.skullMeta(nbtItem.getItem(), mask.getTexture());

                if (!ItemUtil.isAirOrNull(player.getInventory().getHelmet())) {
                    player.getInventory().setHelmet(finishedMask);
                }

                player.updateInventory();
            }

        };

        bukkitRunnable.runTaskLater(VelocityFeatures.getInstance(), 2L);

        addEffects(player, armorPiece, mask);
    }

    public void unequipMask(Player p) {
        UUID id = p.getUniqueId();
        if (Masks.equippedMasks.containsKey(id)) {
            if (hasMask(p.getInventory().getHelmet())) {
                ItemStack original = Masks.equippedMasks.get(id);

                p.getInventory().setHelmet(original);
                p.updateInventory();

                removeEffects(p, p.getInventory().getHelmet());
                Masks.equippedMasks.remove(id);

                // TODO: safer way of storing helmets is putting in nbt values of original helmet
            }
        }
    }

    public void handleEffects(Player player, EntityDamageByEntityEvent e, boolean isDamager) {
        /*ItemStack helmetItem = player.getInventory().getHelmet();

        if (helmetItem != null && this.hasMask(helmetItem)) {
            if (!isDamager) helmetItem.setDurability((short) 3);

            CustomMask mask = getMask(helmetItem);
            if (mask == null) return;
            for (String customEffect : mask.maskSave.getCustomEffects()) {
                String[] split = customEffect.split(":");

                String effect = split[0];
                double multiplier = Double.parseDouble(split[1]);

                switch (effect.toLowerCase()) {
                    case "reduce":
                        if (!isDamager)
                            e.setDamage(e.getDamage() * multiplier);
                        break;
                    case "damage":
                        if (isDamager)
                            e.setDamage(e.getDamage() * multiplier);
                        break;
                    case "waterdamage":
                    {
                        if (isDamager) {
                            Material blockType = player.getLocation().getBlock().getType();

                            if (blockType.equals(Material.WATER) || blockType.equals(Material.STATIONARY_WATER)) {
                                e.setDamage(e.getDamage() * multiplier);
                            }
                        }
                    }
                        break;
                    case "cursedmark":
                        if (isDamager) {
                            AbilityUtil.cursedAbility(e);
                        }
                        break;
                }
            }
        }*/
    }

    public int getMaskNegateChance(Player player) {
        ItemStack helmet = player.getInventory().getHelmet();

        if (helmet != null && hasMask(helmet)) {
            CustomMask mask = getMask(helmet);

            String effect = getEffect("NEGATE", mask.maskSave);

            if (effect == null) return 0;

            return Integer.parseInt(effect);
        }

        return 0;
    }

    public double getMaskMultiplier(Player player, String lookup) {
        ItemStack helmetItem = player.getInventory().getHelmet();

        if (helmetItem != null && this.hasMask(helmetItem)) {
            CustomMask mask = getMask(helmetItem);

            if (mask == null) return 0;

            List<String> effects = mask.maskSave.getMultiMask().isEnabled() ? getMultiMaskEffects(helmetItem).first : mask.maskSave.getCustomEffects();

            if (lookup.contains("damage") && effects.stream().anyMatch(obj -> obj.contains("waterdamage"))) {
                lookup = "waterdamage";
            }

            String lookup1 = lookup.toUpperCase();
            String effect = effects
                    .stream()
                    .filter(obj -> obj.toUpperCase().contains(lookup1))
                    .findFirst()
                    .orElse(null);

            if (effect == null) return 0;
            if (effect.contains("waterdamage") && !isInWater(player)) return 0;

            String[] split = effect.split(":");

            double multiplier = Double.parseDouble(split[1]);

            return multiplier - 1.0;
        }

        return 0;
    }

    private boolean isInWater(Player player) {
        Material blockType = player.getLocation().getBlock().getType();

        return blockType.equals(Material.WATER) || blockType.equals(Material.STATIONARY_WATER);
    }

    public void removeLore(ItemStack item, String lookup) {
        ItemMeta meta = item.getItemMeta();
        List<String> newLore = meta.getLore();

        if (meta.getLore() == null) return;

        for (String s : meta.getLore()) {
            //if (s.equals(VelocityFeatures.chat(lookup))) newLore.remove(s);
            if (s.contains(lookup)) newLore.remove(s);
        }

        meta.setLore(newLore);

        item.setItemMeta(meta);
    }

    public String getEffect(String lookup, MaskSave maskSave) {
        String effect = maskSave.getCustomEffects().stream().filter(obj -> obj.contains(lookup)).findFirst().orElse(null);

        if (effect == null) return null;

        String[] split = effect.split(":");

        return split[1];
    }

    public String getEffect(String lookup, List<String> list) {
        String effect = list.stream().filter(obj -> obj.contains(lookup)).findFirst().orElse(null);

        if (effect == null) return null;

        String[] split = effect.split(":");

        return split[1];
    }

    private double getHealth(Player p, double toAdd, boolean subtract) {
        double currentHP = p.getMaxHealth();
        double amplifier = 0;

        if (p.hasPotionEffect(PotionEffectType.HEALTH_BOOST)) {
            amplifier = p.getActivePotionEffects().stream().filter(obj -> obj.getType().equals(PotionEffectType.HEALTH_BOOST)).findFirst().orElse(null).getAmplifier();
            amplifier += 1;

            currentHP = currentHP - (amplifier * 4);
        }

        if (subtract) return currentHP - toAdd;
        return currentHP + toAdd;
    }

    public ItemStack createOriginal(ItemStack itemStack) {
        NBTItem oldNBT = new NBTItem(itemStack);
        NBTCompound oldNBTCompound = oldNBT.getCompound("velocity_custommasks_mask");

        String material = oldNBTCompound.getString("type");

        if (material == null || material.isEmpty()) return itemStack;

        ItemStack originalItem = new ItemStack(Material.getMaterial(oldNBTCompound.getString("type")));

        oldNBT.applyNBT(originalItem);

        NBTItem nbtItem = new NBTItem(originalItem);

        return nbtItem.getItem();
    }

    public void updateLore(ItemStack item, String lookup, String replace) {
        ItemMeta meta = item.getItemMeta();

        List<String> oldLore = meta.getLore();
        List<String> newLore = new ArrayList<>();

        if (meta.getLore() == null) return;

        for (int i = 0; i < oldLore.size(); i++) {
            String s = oldLore.get(i);

            if (s.contains(lookup)) {
                s = replace;
            }

            //newLore.remove(i);
            newLore.add(i, s);
        }

        //meta.setLore(removeRepeatedLore(newLore, lookup));
        meta.setLore(newLore);

        item.setItemMeta(meta);
    }

    private List<String> removeRepeatedLore(List<String> list, String lookup) {
        Set<String> set = new HashSet<String>(list);
        list.clear();
        list.addAll(set);

        return list;
    }

    public Pair<List<String>, List<String>> getMultiMaskEffects(ItemStack item) {
        ObjectList<String> customEffectList = new ObjectArrayList<>();
        ObjectList<String> vanillaEffectList = new ObjectArrayList<>();

        Object2ObjectMap<String, Double> customEffectMap = new Object2ObjectOpenHashMap<>();
        Object2ObjectMap<String, Integer> vanillaEffectMap = new Object2ObjectOpenHashMap<>();

        NBTItem maskItem = new NBTItem(item);
        NBTCompound maskCompound = maskItem.getCompound("velocity_custommasks_mask");

        if (!maskCompound.hasKey("masks") || maskCompound.getString("masks").isEmpty()) return new Pair<>(customEffectList, vanillaEffectList);

        String[] masks = maskCompound.getString("masks").split("_");

        for (String mask : masks) {
            CustomMask customMask = Masks.maskList.get(mask);

            if (customMask == null) continue;

            for (String customEffect : customMask.maskSave.getCustomEffects()) {
                String[] splitEffect = customEffect.split(":");

                String effect = splitEffect[0];
                double amplifier = Double.parseDouble(splitEffect[1]);

                switch (effect.toLowerCase()) {
                    case "damage":
                    case "reduce":
                    case "waterdamage":
                        amplifier = amplifier - 1;
                }

                if (!customEffectMap.containsKey(splitEffect[0])) customEffectMap.put(effect, Double.parseDouble(splitEffect[1]));
                else customEffectMap.put(effect, customEffectMap.get(effect) + amplifier);
            }

            for (String vanillaEffect : customMask.maskSave.getVanillaEffects()) {
                String[] splitEffect = vanillaEffect.split(":");

                String effect = splitEffect[0];
                int amplifier = Integer.parseInt(splitEffect[1]);

                if (!vanillaEffectMap.containsKey(splitEffect[0]) || (vanillaEffectMap.get(effect) < amplifier)) vanillaEffectMap.put(effect, amplifier);
            }
        }

        for (Object2ObjectMap.Entry<String, Double> entry : customEffectMap.object2ObjectEntrySet()) {
            customEffectList.add(entry.getKey() + ":" + entry.getValue());
        }

        for (Object2ObjectMap.Entry<String, Integer> entry : vanillaEffectMap.object2ObjectEntrySet()) {
            vanillaEffectList.add(entry.getKey() + ":" + entry.getValue());
        }

        return new Pair<>(customEffectList, vanillaEffectList);
    }

    public String getMultiMaskLore(String[] maskList) {
        StringBuilder maskListLore = new StringBuilder();

        for (int i = 0; i < maskList.length; i++) {
            CustomMask customMask = Masks.maskList.get(maskList[i]);

            if (customMask == null) continue;

            maskListLore.append(customMask.maskSave.getDisplayName());
            if (i < maskList.length - 1) maskListLore.append("&7, ");
        }

        return VelocityFeatures.chat(maskListLore.toString());
    }

    public void editLoot(PlayerDeathEvent e) {
        Player p = e.getEntity();
        UUID id = p.getUniqueId();

        if (Masks.equippedMasks.containsKey(id)) {
            ListIterator<ItemStack> list = e.getDrops().listIterator();

            while(list.hasNext()) {
                ItemStack item = list.next();

                if (hasMask(item)) {
                    list.remove();
                    list.add(Masks.equippedMasks.get(id));
                }
            }
        }
    }

    public void forceUnequipMasks() {
        equippedMasks.forEach((uuid, item) -> {
            Player player = Bukkit.getPlayer(uuid);
            if (hasMask(player.getInventory().getHelmet())) {
                ItemStack original = Masks.equippedMasks.get(uuid);

                player.getInventory().setHelmet(original);
                player.updateInventory();

                removeEffects(player, player.getInventory().getHelmet());
            }
        });
    }

    @Override
    public String getName() {
        return "masks";
    }

    @Override
    public boolean isEnabled() {
        return MaskConfig.getInstance().isEnabled();
    }

    @Override
    public void onEnable() {
        VelocityFeatures.registerEvent(new MaskListener());
        CommandAPI.getInstance().enableCommand(new MaskCommand());

        MaskConfig.getInstance().setEnabled(true);

        loadMasks();
    }

    @Override
    public void onDisable() {
        VelocityFeatures.unregisterEvent(MaskListener.getInstance());
        CommandAPI.getInstance().disableCommand(MaskCommand.class);

        MaskConfig.getInstance().setEnabled(false);
    }

    @Override
    public String placeholderRequest(Player player, String arg) {
        return "";
    }

}
