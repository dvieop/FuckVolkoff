package FuckParty.fuckVolkoff.src.main.java.xyz.velocity.modules.armorsets;

import com.golfing8.kore.FactionsKore;
import com.golfing8.kore.integration.shield.FactionShieldsIntegration;
import com.massivecraft.factions.Board;
import com.massivecraft.factions.FLocation;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.zcore.persist.MemoryBoard;
import de.tr7zw.nbtapi.NBTCompound;
import de.tr7zw.nbtapi.NBTItem;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import xyz.velocity.VelocityFeatures;
import xyz.velocity.commands.CommandAPI;
import xyz.velocity.modules.armorsets.commands.ArmorSetCommand;
import xyz.velocity.modules.armorsets.config.ArmorConfig;
import xyz.velocity.modules.armorsets.config.saves.ArmorSave;
import xyz.velocity.modules.armorsets.config.saves.ItemSave;
import xyz.velocity.modules.armorsets.config.SpecialItemsConfig;
import xyz.velocity.modules.AbstractModule;
import xyz.velocity.modules.Module;
import xyz.velocity.modules.customenchants.enchants.util.DeathWrapper;
import xyz.velocity.modules.masks.Masks;
import xyz.velocity.modules.util.EnchantUtil;
import xyz.velocity.modules.util.ItemUtil;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

@Module
public class ArmorSets extends AbstractModule {

    public static final Object2ObjectOpenHashMap<String, CustomSet> setsCache = new Object2ObjectOpenHashMap<>();
    public static Object2ObjectOpenHashMap<UUID, String> equippedSets = new Object2ObjectOpenHashMap<>();

    @Getter
    private static ArmorSets instance;

    public ArmorSets() {
        instance = this;
    }

    public void loadArmorSets() {
        equippedSets.clear();
        setsCache.clear();

        for (ArmorSave set : ArmorConfig.getInstance().sets) {
            setsCache.put(set.getName(), new CustomSet(set));
        }
    }

    public boolean isSetPiece(ItemStack itemStack) {
        if (itemStack == null) return false;

        NBTItem nbtItem = new NBTItem(itemStack);

        if (nbtItem == null) return false;

        NBTCompound nbtCompound = nbtItem.getCompound("velocity_armorsets_armor");

        if (nbtCompound != null) return true;
        return false;
    }

    private boolean checkItemType(ItemStack item, String id) {
        NBTItem nbtItem = new NBTItem(item);
        NBTCompound nbtCompound = nbtItem.getCompound("velocity_armorsets_armor");

        if (nbtCompound == null) return false;

        String type = nbtCompound.getString("id");

        if (type.equals(id)) return true;
        return false;
    }

    public boolean hasFullSet(PlayerInventory pI, String id) {
        if (pI.getHelmet() == null || pI.getChestplate() == null || pI.getLeggings() == null || pI.getBoots() == null) return false;
        CustomSet customSet = setsCache.get(id);

        if (customSet == null) return false;

        if (!checkItemType(pI.getHelmet(), id)
                || !checkItemType(pI.getChestplate(), id)
                || !checkItemType(pI.getLeggings(), id)
                || !checkItemType(pI.getHelmet(), id)) return false;

        return true;
    }

    public boolean hasFullSet(PlayerInventory pI, String id, ItemStack newPiece) {
        ItemStack helmet = pI.getHelmet();
        ItemStack chest = pI.getChestplate();
        ItemStack legs = pI.getLeggings();
        ItemStack boots = pI.getBoots();

        String type = newPiece.getType().name();

        if (type.endsWith("_HELMET") || Masks.getInstance().hasMask(newPiece)) {
            helmet = newPiece;
        }
        else if (type.endsWith("_CHESTPLATE")) {
            chest = newPiece;
        }
        else if (type.endsWith("_LEGGINGS")) {
            legs = newPiece;
        }
        else if (type.endsWith("_BOOTS")) {
            boots = newPiece;
        }

        if (helmet == null || chest == null || legs == null || boots == null) return false;
        CustomSet customSet = setsCache.get(id);

        if (customSet == null) return false;

        if (!checkItemType(helmet, id)
                || !checkItemType(chest, id)
                || !checkItemType(legs, id)
                || !checkItemType(boots, id)) return false;
        return true;
    }

    private void setArmor(Player p, ItemStack item) {
        if (item == null) return;

        String type = item.getType().name();

        if (type.endsWith("_HELMET")) {
            p.getInventory().setHelmet(item);
        }
        else if (type.endsWith("_CHESTPLATE")) {
            p.getInventory().setChestplate(item);
        }
        else if (type.endsWith("_LEGGINGS")) {
            p.getInventory().setLeggings(item);
        }
        else if (type.endsWith("_BOOTS")) {
            p.getInventory().setBoots(item);
        }
    }

    public void equipSet(Player player, ItemStack item) {
        NBTItem nbtItem = new NBTItem(item);
        NBTCompound nbtCompound = nbtItem.getCompound("velocity_armorsets_armor");

        CustomSet set = setsCache.get(nbtCompound.getString("id"));

        if (set == null) return;
        if (!hasFullSet(player.getInventory(), nbtCompound.getString("id"), item)) return;

        for (String effect : set.armorSave.getVanillaEffects()) {
            PotionEffect potionEffect = EnchantUtil.getEffect(effect);

            if (player.hasPotionEffect(potionEffect.getType())) {
                player.removePotionEffect(potionEffect.getType());
            }

            player.addPotionEffect(potionEffect);
        }

        equippedSets.put(player.getUniqueId(), nbtCompound.getString("id"));

        player.sendMessage(VelocityFeatures.chat(ArmorConfig.getInstance().getEquippedMessage()
                .replace("<set>", set.armorSave.getChatName())
        ));

        String effect = getEffect("HEALTH", set.armorSave);

        if (effect == null) return;

        int toAdd = Integer.parseInt(effect);
        double newHP = getHealth(player, toAdd, false);

        player.setMaxHealth(newHP);
    }

    public void removeEffects(Player p, ItemStack itemStack) {
        if (itemStack == null || itemStack.getType() == Material.AIR) return;
        if (isSetPiece(itemStack)) {

            UUID id = p.getUniqueId();

            if (equippedSets.containsKey(id)) {
                NBTItem nbtItem = new NBTItem(itemStack);
                NBTCompound nbtCompound = nbtItem.getCompound("velocity_armorsets_armor");

                CustomSet set = setsCache.get(nbtCompound.getString("id"));

                if (set == null) return;

                for (String effect : set.armorSave.getVanillaEffects()) {
                    p.removePotionEffect(EnchantUtil.getEffect(effect).getType());
                }

                equippedSets.remove(id);

                p.sendMessage(VelocityFeatures.chat(ArmorConfig.getInstance().getUnequipMessage()
                        .replace("<set>", set.armorSave.getChatName())
                ));

                String effect = getEffect("HEALTH", set.armorSave);

                if (effect == null) return;

                int toAdd = Integer.parseInt(effect);
                double newHP = getHealth(p, toAdd, true);

                if (newHP < 20) newHP = 20;

                p.setMaxHealth(newHP);
            }
        }
    }

    public String getEffect(String lookup, ArmorSave armorSave) {
        String effect = armorSave.getCustomEffects().stream().filter(obj -> obj.contains(lookup)).findFirst().orElse(null);

        if (effect == null) return null;

        String[] split = effect.split(":");

        return split[1];
    }

    public String getEffect(String lookup, ItemSave weaponSave) {
        String effect = weaponSave.getCustomEffects().getEffects().stream().filter(obj -> obj.contains(lookup)).findFirst().orElse(null);

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
        return 20 + toAdd;
    }

    public void handleEffects(Player player, EntityDamageByEntityEvent e, boolean isDamager) {
        /*if (isDamager) {
            handleWeaponDamage(e, player);
        }*/

        /*if (equippedSets.containsKey(player)) {
            CustomSet set = setsCache.get(equippedSets.get(player));

            if (set == null) return;

            List<String> whitelistedWorlds = set.armorSave.getWhitelistedWorlds();

            if (!whitelistedWorlds.isEmpty()) {
                if (whitelistedWorlds.contains("baseregion")) {
                    if (!isInRegion(player)) return;
                }
                else if (!whitelistedWorlds.contains(player.getWorld().getName())) return;
            }

            if (!isDamager) handleLeatherDurability(player);

            for (String customEffect : set.armorSave.getCustomEffects()) {
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
                    case "bloodsense":
                        if (isDamager) {
                            AbilityUtil.bloodSenseAbility(e);
                        }
                        break;
                    case "realmofdeath":
                        if (isDamager) {
                            AbilityUtil.realmOfDeath(e);
                        }
                        break;
                }
            }
        }*/
    }

    public boolean isInWorld(Player player, CustomSet set) {
        List<String> whitelistedWorlds = set.armorSave.getWhitelistedWorlds();

        if (!whitelistedWorlds.isEmpty()) {
            if (whitelistedWorlds.contains("baseregion")) {
                if (!isInRegion(player)) return false;
            }
            else if (!whitelistedWorlds.contains(player.getWorld().getName())) return false;
        }

        return true;
    }

    public double getArmorMultiplier(Player player, String lookup) {
        CustomSet set = setsCache.get(equippedSets.get(player.getUniqueId()));

        if (set == null) return 0;
        if (!isInWorld(player, set)) return 0;

        List<String> effects = set.armorSave.getCustomEffects();

        if (lookup.contains("damage") && effects.stream().anyMatch(obj -> obj.contains("waterdamage"))) {
            lookup = "waterdamage";
        }

        String lookup1 = lookup.toUpperCase();
        String effect = effects
                .stream()
                .filter(obj -> obj.contains(lookup1))
                .findFirst()
                .orElse(null);

        if (effect == null) return 0;
        if (effect.contains("waterdamage") && !isInWater(player)) return 0;

        String[] split = effect.split(":");

        double multiplier = Double.parseDouble(split[1]);

        return multiplier - 1.0;
    }

    public double getAbilityMultiplier(Player player, boolean increase) {
        if (!AbilityManager.abilityCache.containsKey(player.getUniqueId())) return 0;

        AbilityWrapper abilityWrapper = AbilityManager.abilityCache.get(player.getUniqueId());

        if (increase) {
            return AbilityManager.abilityList.get(abilityWrapper.getAbilityName()).getAbility().getDamageMulti() - 1.0;
        } else {
            return AbilityManager.abilityList.get(abilityWrapper.getAbilityName()).getAbility().getDamageReduction() - 1.0;
        }
    }

    public boolean isInWater(Player player) {
        Material blockType = player.getLocation().getBlock().getType();

        return blockType.equals(Material.WATER) || blockType.equals(Material.STATIONARY_WATER);
    }

    public void setData(ItemStack item, NBTItem nbtItem) {
        if (isSetPiece(item)) {
            NBTItem nbtItem1 = new NBTItem(item);
            NBTCompound compound = nbtItem1.getCompound("velocity_armorsets_armor");

            if (compound == null) return;

            String type = compound.getString("id");

            NBTCompound compound1 = nbtItem.addCompound("velocity_armorsets_armor");
            compound1.setString("id", type);
        }
    }

    /*public void handleNegate(int chance, DeathWrapper deathWrapper, PlayerDeathEvent e) {
        List<ItemStack> getItems = deathWrapper.getItems();

        for (Iterator<ItemStack> iterator = getItems.iterator(); iterator.hasNext();) {
            iterator.next();
            int newChance = ThreadLocalRandom.current().nextInt(100);

            if (newChance < chance) {
                iterator.remove();
            }
        }

        deathWrapper.setItems(getItems);

        ListIterator<ItemStack> list = e.getDrops().listIterator();

        while(list.hasNext()) {
            ItemStack item = list.next();

            if (deathWrapper.getItems().contains(item)) {
                list.remove();
            }
        }
    }*/

    public int getArmorNegateChance(Player player) {
        UUID uuid = player.getUniqueId();

        if (ArmorSets.equippedSets.containsKey(uuid)) {
            CustomSet customSet = ArmorSets.setsCache.get(ArmorSets.equippedSets.get(uuid));

            if (customSet == null) return 0;

            String effect = getEffect("NEGATE", customSet.armorSave);

            if (effect == null) return 0;

            return Integer.parseInt(effect);
        }

        return 0;
    }

    public int getWeaponNegateChance(Player player) {
        UUID uuid = player.getUniqueId();

        if (ItemUtil.isAirOrNull(player.getItemInHand())) return 0;

        ItemStack weapon = player.getItemInHand();

        NBTItem item = new NBTItem(weapon);
        NBTCompound compound = item.getCompound("velocity_armorsets_item");

        if (compound == null) return 0;

        String id = compound.getString("id");

        ItemSave weaponSave = SpecialItemsConfig.getInstance().items.stream().filter(obj -> obj.getName().equals(id)).findFirst().orElse(null);

        if (weaponSave == null) return 0;
        if (weaponSave.getArmorsetBind().isEnabled() && !Objects.equals(weaponSave.getArmorsetBind().getArmor(), ArmorSets.equippedSets.get(uuid))) return 0;
        if (!weaponSave.getCustomEffects().isEnabled()) return 0;

        String effect = getEffect("NEGATE", weaponSave);

        if (effect == null) return 0;

        return Integer.parseInt(effect);
    }

    public boolean isInRegion(Player player) {
        return Board.getInstance().isBaseRegion(player);
    }

    public void handleLeatherDurability(Player player) {
        ItemStack[] armor = player.getInventory().getArmorContents();

        for (ItemStack item : armor) {
            if (ItemUtil.isAirOrNull(item)) continue;
            if (!item.getType().name().startsWith("LEATHER")) continue;

            if (item.getDurability() >= -1) item.setDurability(Short.MIN_VALUE);
        }
    }

    public boolean isSpecialItem(Player player) {
        ItemStack weapon = player.getItemInHand();

        NBTItem item = new NBTItem(weapon);
        NBTCompound compound = item.getCompound("velocity_armorsets_item");

        if (compound == null) return false;

        String id = compound.getString("id");

        ItemSave weaponSave = SpecialItemsConfig.getInstance().items.stream().filter(obj -> obj.getName().equals(id)).findFirst().orElse(null);

        if (weaponSave == null) return false;
        if (weaponSave.getArmorsetBind().isEnabled() && !weaponSave.getArmorsetBind().getArmor().equalsIgnoreCase(equippedSets.get(player.getUniqueId()))) return false;
        if (!weaponSave.getCustomEffects().isEnabled()) return false;

        return true;
    }

    public double getWeaponDamage(Player player) {
        ItemStack weapon = player.getItemInHand();

        NBTItem item = new NBTItem(weapon);
        NBTCompound compound = item.getCompound("velocity_armorsets_item");

        if (compound == null) return 1.0;

        String id = compound.getString("id");

        ItemSave weaponSave = SpecialItemsConfig.getInstance().items.stream().filter(obj -> obj.getName().equals(id)).findFirst().orElse(null);

        String effect = getEffect("DAMAGE", weaponSave);

        if (effect == null) return 1.0;

        return Double.parseDouble(effect);
    }

    public boolean hasAbility(ItemStack[] armorContents) {
        for (ItemStack armorContent : armorContents) {
            NBTItem nbtItem = new NBTItem(armorContent);
            NBTCompound nbtCompound = nbtItem.getCompound("velocity_armorsets_armor");

            String ability = nbtCompound.getString("ability");

            if (ability == null || ability.isEmpty()) return false;
        }

        return true;
    }

    public String getAbilityId(ItemStack helmet) {
        NBTItem nbtItem = new NBTItem(helmet);
        NBTCompound nbtCompound = nbtItem.getCompound("velocity_armorsets_armor");

        return nbtCompound.getString("ability");
    }

    public void forceUnequipSets() {
        for (Object2ObjectMap.Entry<UUID, String> map : equippedSets.object2ObjectEntrySet()) {
            Player player = Bukkit.getPlayer(map.getKey());
            removeEffects(player, player.getInventory().getHelmet());
        }
    }

    @Override
    public String getName() {
        return "armorsets";
    }

    @Override
    public boolean isEnabled() {
        return ArmorConfig.getInstance().isEnabled();
    }

    @Override
    public void onEnable() {
        loadArmorSets();

        VelocityFeatures.registerEvent(new ArmorListener());
        CommandAPI.getInstance().enableCommand(new ArmorSetCommand());

        new AbilityManager();

        ArmorConfig.getInstance().setEnabled(true);
    }

    @Override
    public void onDisable() {
        VelocityFeatures.unregisterEvent(ArmorListener.getInstance());
        CommandAPI.getInstance().disableCommand(ArmorSetCommand.class);

        ArmorConfig.getInstance().setEnabled(false);
    }

    @Override
    public String placeholderRequest(Player player, String arg) {
        return "";
    }

}
