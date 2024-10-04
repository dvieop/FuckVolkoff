package FuckParty.fuckVolkoff.src.main.java.xyz.velocity.modules.pets;

import de.tr7zw.nbtapi.NBTCompound;
import de.tr7zw.nbtapi.NBTItem;
import eu.decentsoftware.holograms.api.DHAPI;
import eu.decentsoftware.holograms.api.holograms.HologramLine;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import xyz.velocity.VelocityFeatures;
import xyz.velocity.commands.CommandAPI;
import xyz.velocity.modules.customenchants.CustomEnchants;
import xyz.velocity.modules.customenchants.EnchantManager;
import xyz.velocity.modules.garrison.Garrison;
import xyz.velocity.modules.pets.commands.PetsCommand;
import xyz.velocity.modules.pets.config.StatsConfig;
import xyz.velocity.modules.pets.config.saves.*;
import xyz.velocity.modules.pets.config.PetsConfig;
import xyz.velocity.modules.AbstractModule;
import xyz.velocity.modules.Module;
import xyz.velocity.modules.pets.util.Cooldown;
import xyz.velocity.modules.util.EnchantUtil;
import xyz.velocity.modules.util.StringCalculator;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

@Module
public class Pets extends AbstractModule {

    public static Object2ObjectOpenHashMap<String, CustomPet> petList = new Object2ObjectOpenHashMap<>();
    public static Object2ObjectOpenHashMap<UUID, PetWrapper> equippedPets = new Object2ObjectOpenHashMap<>();
    public static Object2ObjectOpenHashMap<UUID, ObjectList<Cooldown>> cooldownCache = new Object2ObjectOpenHashMap<>();
    public static Object2ObjectOpenHashMap<UUID, Boolean> toggledPets = new Object2ObjectOpenHashMap<>();

    public Pets() {
        instance = this;
    }

    @Getter
    private static Pets instance;

    public void loadPets() {
        CustomPet.skullCache.clear();
        
        petList.clear();
        equippedPets.clear();

        for (PetSave pet : PetsConfig.getInstance().pets) {
            petList.put(pet.getName(), new CustomPet(pet));
        }
    }

    public double calculateXP(PetTierSave petTierSave, int level) {
        String calculation = petTierSave.getXpRequirement().replace("<level>", level + "");

        return roundAvoid(StringCalculator.getCalculateStringValue(calculation), 0);
    }

    public double roundAvoid(double value, int places) {
        double scale = Math.pow(10, places);
        return Math.round(value * scale) / scale;
    }

    public LevelEffectSave getEffectsByLevel(List<LevelEffectSave> list, PetStats petStats) {
        List<LevelEffectSave> sort =  list.stream().sorted(Comparator.comparing(o -> o.getLevel())).collect(Collectors.toList());
        int level = petStats.getLevel();

        if (sort.size() < 2) return sort.get(0);

        for (int i = 1; i < sort.size(); i++) {
            LevelEffectSave previous = sort.get(i - 1);
            LevelEffectSave current = sort.get(i);

            if (level < previous.getLevel()) return previous;
            if (level >= previous.getLevel() && level < current.getLevel()) return current;
            if (level > previous.getLevel() && level == current.getLevel()) return current;
        }

        return null;
    }

    public void handleEffects(Player player, EntityDamageByEntityEvent e, boolean isDamager) {

        UUID id = player.getUniqueId();
        if (Pets.equippedPets.containsKey(id)) {
            PetWrapper petWrapper = Pets.equippedPets.get(id);

            LevelEffectSave effects =  Pets.getInstance().getEffectsByLevel(petWrapper.customPet.getPetSave().getLevelEffects(), petWrapper.petStats);

            if (effects == null) return;
            if (!CustomEnchants.getInstance().canDamage(player, e.getEntity())) return;

            for (String customEffect : effects.getEffects()) {
                if (customEffect.contains("potion:")
                        || customEffect.contains("debuff:")
                        || customEffect.contains("pearlland:")
                        || customEffect.contains("watereffects:")
                ) continue;

                String[] split = customEffect.split(":");

                String effect = split[0];
                double multiplier = Double.parseDouble(split[1]);

                switch (effect.toLowerCase()) {
                    case "enchantcooldown":
                        if (isDamager) handleEnchantEffect(player, customEffect);
                        break;
                    case "reduce":
                        if (!isDamager) handleXP(player, petWrapper, getXpSaveFromEffect(petWrapper, "DAMAGE:OUTGOING"));
                        break;
                    case "damage":
                        if (isDamager) handleXP(player, petWrapper, getXpSaveFromEffect(petWrapper, "DAMAGE:INCOMING"));
                        break;
                    case "waterdamage":
                        if (isDamager && isInWater(player)) handleXP(player, petWrapper, getXpSaveFromEffect(petWrapper, "DAMAGE:INCOMING"));
                    break;
                }
            }
        }
    }

    public double getPetMultiplier(Player player, String lookup) {
        PetWrapper petWrapper = Pets.equippedPets.get(player.getUniqueId());

        LevelEffectSave effects;

        try {
            effects =  Pets.getInstance().getEffectsByLevel(petWrapper.customPet.getPetSave().getLevelEffects(), petWrapper.petStats);
        } catch (Throwable err) {
            return 0;
        }

        if (effects == null) return 0;

        List<String> list = effects.getEffects();

        if (lookup.contains("damage:") && list.stream().anyMatch(obj -> obj.contains("waterdamage:"))) {
            lookup = "waterdamage:";
        }

        String lookup1 = lookup.toUpperCase();
        String effect = list
                .stream()
                .filter(obj -> obj.toUpperCase().contains(lookup1))
                .findFirst()
                .orElse(null);

        if (effect == null) return 0;
        if (effect.contains("waterdamage:") && !isInWater(player)) return 0;

        String[] split = effect.split(":");

        double multiplier = Double.parseDouble(split[1]);

        return multiplier - 1.0;
    }

    public int getPetNegationChance(Player player) {
        if (!Pets.equippedPets.containsKey(player.getUniqueId())) return 0;

        PetWrapper petWrapper = Pets.equippedPets.get(player.getUniqueId());

        LevelEffectSave effects =  Pets.getInstance().getEffectsByLevel(petWrapper.customPet.getPetSave().getLevelEffects(), petWrapper.petStats);

        if (effects == null) return 0;

        List<String> list = effects.getEffects();

        String effect = list
                .stream()
                .filter(obj -> obj.toUpperCase().contains("NEGATE"))
                .findFirst()
                .orElse(null);

        if (effect == null) return 0;

        String[] split = effect.split(":");

        return (Integer.parseInt(split[1]) * 100) - 100;
    }

    private boolean isInWater(Player player) {
        Material blockType = player.getLocation().getBlock().getType();

        return blockType.equals(Material.WATER) || blockType.equals(Material.STATIONARY_WATER);
    }

    public void handleXP(Player player, PetWrapper petWrapper, XpSave xpSave) {
        UUID id = player.getUniqueId();

        if (xpSave == null) return;
        if (isOnCooldown(id, xpSave.getEffect())) return;

        int xp = (int) (getXpFromEffect(xpSave) * (petWrapper.petStats.getXpBoost() + Garrison.getInstance().getPetXP(player)));

        PetStats petStats = petWrapper.petStats;
        PetTierSave petTierSave = PetsConfig.getInstance().petTiers
                .stream()
                .filter(obj -> obj.getTier() == petWrapper.customPet.getPetSave().getTier())
                .findFirst()
                .orElse(null);

        if (petTierSave == null) return;
        if (petStats.getLevel() >= petTierSave.getLevelLimit()) return;

        int calcXP = petStats.getXp() + xp;

        petStats.setXp(calcXP);

        if (calcXP >= petStats.getXpToLevelUp()) {
            petStats.setLevel(petStats.getLevel() + 1);
            petStats.setXp(0);
            petStats.setXpToLevelUp((int) Pets.getInstance().calculateXP(petTierSave, petStats.getLevel()));

            HologramLine line = DHAPI.getHologram(player.getName()).getPage(0).getLine(0);

            line.setText(VelocityFeatures.chat(petWrapper.customPet.petSave.getDisplayName())
                    .replace("<level>", petStats.getLevel() + "")
            );

            player.sendMessage(VelocityFeatures.chat(PetsConfig.getInstance().petLeveled
                    .replace("<pet>", petWrapper.customPet.petSave.getDisplayName())
                    .replace("<level>", petStats.getLevel() + "")
            ));
        }

        StatsSave statsSave = StatsConfig.getInstance().getPlayerPets(player);

        statsSave.setEquippedPet(petStats);

        statsSave.getPetInventory().removeIf(obj -> obj.getName().equals(petStats.getName()));
        statsSave.getPetInventory().add(petStats);

        StatsConfig.getInstance().getData().put(player.getUniqueId(), statsSave);
        //StatsConfig.getInstance().saveData();

        Cooldown cooldown = new Cooldown(xpSave.getEffect());

        cooldown.setCooldown(System.currentTimeMillis() + (xpSave.getCooldown() * 1000L));

        addCooldown(id, cooldown);
    }

    public XpSave getXpSaveFromEffect(PetWrapper petWrapper, String effect) {
        PetTierSave petTierSave = PetsConfig.getInstance().petTiers.stream().filter(obj -> obj.getTier() == petWrapper.customPet.getPetSave().getTier()).findFirst().orElse(null);

        if (petTierSave == null) return null;
        if (effect.contains("FARM") && petTierSave.getXpGains().stream().anyMatch(obj -> obj.getEffect().contains("FARM:ALL"))) effect = "FARM:ALL";
        if (effect.contains("KILL") && petTierSave.getXpGains().stream().anyMatch(obj -> obj.getEffect().contains("KILL:MOBS"))) effect = "KILL:MOBS";

        String effect1 = effect;
        XpSave xpSave = petTierSave.getXpGains().stream().filter(obj -> obj.getEffect().contains(effect1)).findFirst().orElse(null);;

        if (xpSave == null) return null;

        return xpSave;
    }

    public int getXpFromEffect(XpSave xpSave) {
        String[] effectSplit = xpSave.getEffect().split(":");

        int xp = 0;

        try {
            xp = Integer.parseInt(effectSplit[effectSplit.length - 1]);
        } catch (Throwable e) {
            xp = (int) StringCalculator.getCalculateStringValue(effectSplit[effectSplit.length - 1]);
        }

        return xp;
    }

    public String getStatsLore(PetSave petSave, PetStats petStats) {
        String statsLore = "";

        LevelEffectSave les = getEffectsByLevel(petSave.getLevelEffects(), petStats);
        List<EffectSave> es = PetsConfig.getInstance().effectList;

        if (les == null) return "";

        for (String effect : les.getEffects()) {
            if (effect.contains("potion")) {
                String potion = effect.split(":")[1];
                EffectSave getEffectSave = es.stream().filter(obj -> obj.getName().contains(potion)).findFirst().orElse(null);

                if (getEffectSave == null) continue;

                statsLore += getEffectSave.getEffectLore().replace("<stat>", EnchantUtil.toRomanNumerals(Integer.parseInt(effect.split(":")[2]) + 1)) + "VDIB";
            } else {
                String type = effect.split(":")[0];
                String stat = effect.split(":")[1];

                EffectSave getEffectSave = es.stream().filter(obj -> obj.getName().equals(type)).findFirst().orElse(null);

                if (getEffectSave == null) continue;

                statsLore += getEffectSave.getEffectLore().replace("<stat>", roundAvoid((Double.parseDouble(stat) * 100) - 100, 0)+ "%") + "VDIB";
            }
        }

        return statsLore;
    }

    public void givePotionEffect(Player player, String type) {
        PotionEffect pot = EnchantUtil.getEffect(type);

        if (player.hasPotionEffect(pot.getType())) {
            PotionEffect potion = player.getActivePotionEffects().stream().filter(obj -> obj.getType().equals(pot.getType())).findFirst().orElse(null);

            if (potion != null && potion.getAmplifier() == pot.getAmplifier()) {
                player.removePotionEffect(pot.getType());
            }
        }

        player.addPotionEffect(new PotionEffect(pot.getType(), 400, pot.getAmplifier()));
    }

    public void givePotionEffect(Player player, String type, int duration) {
        PotionEffect pot = EnchantUtil.getEffect(type);

        if (player.hasPotionEffect(pot.getType())) {
            PotionEffect potion = player.getActivePotionEffects().stream().filter(obj -> obj.getType().equals(pot.getType())).findFirst().orElse(null);

            if (potion != null && potion.getAmplifier() == pot.getAmplifier()) {
                player.removePotionEffect(pot.getType());
            }
        }

        player.addPotionEffect(new PotionEffect(pot.getType(), duration * 20, pot.getAmplifier()));
    }

    public void handleLowHP(Player player) {
        PetWrapper pW = equippedPets.get(player.getUniqueId());

        LevelEffectSave effects =  Pets.getInstance().getEffectsByLevel(pW.customPet.getPetSave().getLevelEffects(), pW.petStats);

        for (String effect : effects.getEffects()) {
            if (effect.startsWith("lowhp")) {
                String[] split = effect.split(":");

                int chance = ThreadLocalRandom.current().nextInt(100);

                if (chance < Integer.parseInt(split[5]) && player.getHealth() < 6) {
                    if (isOnCooldown(player.getUniqueId(), "lowhp")) continue;

                    String pot = (split[2] + ":" + split[3]).toUpperCase();

                    givePotionEffect(player, pot, Integer.parseInt(split[4]));

                    int cd = Integer.parseInt(split[6]);

                    Cooldown cooldown = new Cooldown("lowhp");
                    cooldown.setCooldown(System.currentTimeMillis() + (cd * 1000L));

                    addCooldown(player.getUniqueId(), cooldown);
                }
            }
        }
    }

    public void handleDebuff(Player attacker, Player defender) {
        PetWrapper pW = equippedPets.get(attacker.getUniqueId());

        LevelEffectSave effects =  Pets.getInstance().getEffectsByLevel(pW.customPet.getPetSave().getLevelEffects(), pW.petStats);

        for (String effect : effects.getEffects()) {
            if (effect.startsWith("debuff")) {
                String[] split = effect.split(":");

                double chance = EnchantUtil.getRandomDouble();

                if (chance <= Integer.parseInt(split[4])) {
                    if (isOnCooldown(attacker.getUniqueId(), "debuff")) continue;

                    String pot = (split[1] + ":" + split[2]).toUpperCase();

                    givePotionEffect(defender, pot, Integer.parseInt(split[3]));

                    int cd = Integer.parseInt(split[5]);

                    Cooldown cooldown = new Cooldown("debuff");
                    cooldown.setCooldown(System.currentTimeMillis() + (cd * 1000L));

                    addCooldown(attacker.getUniqueId(), cooldown);
                }
            }
        }
    }

    public void handleEnchantEffect(Player player, String effect) {
        String[] split = effect.split(":");

        double chance = EnchantUtil.getRandomDouble();

        if (chance <= Integer.parseInt(split[1])) {
            if (isOnCooldown(player.getUniqueId(), "enchantcooldown")) return;

            EnchantManager.cooldownCache.get(player.getUniqueId()).clear();

            int cd = Integer.parseInt(split[2]);

            Cooldown cooldown = new Cooldown("enchantcooldown");
            cooldown.setCooldown(System.currentTimeMillis() + (cd * 1000L));

            addCooldown(player.getUniqueId(), cooldown);
        }
    }

    public void handlePearlLand(Player player) {
        PetWrapper pW = equippedPets.get(player.getUniqueId());

        LevelEffectSave effects =  Pets.getInstance().getEffectsByLevel(pW.customPet.getPetSave().getLevelEffects(), pW.petStats);

        if (effects == null) return;

        for (String effect : effects.getEffects()) {
            if (effect.startsWith("pearlland")) {
                String[] split = effect.split(":");

                String pot = (split[1] + ":" + split[2]).toUpperCase();

                givePotionEffect(player, pot, Integer.parseInt(split[3]));
            }
        }
    }

    public String getPetType(Player player) {
        UUID id = player.getUniqueId();

        if (!Pets.equippedPets.containsKey(id)) return "";

        PetWrapper petWrapper = Pets.equippedPets.get(id);

        return petWrapper.customPet.getPetSave().getType();
    }

    public void removePet(Player player, PetWrapper petWrapper) {
        //petWrapper.petStand.runnable.cancel();
        petWrapper.deleteHologram();

        Pets.equippedPets.remove(player.getUniqueId());
    }

    public ItemStack updateItemLore(ItemStack item, PetSave petSave, PetStats petStats) {
        ItemMeta itemMeta = item.getItemMeta();

        itemMeta.setDisplayName(VelocityFeatures.chat(petSave.getDisplayName()
                .replace("<level>", petStats.getLevel() + "")
        ));

        //List<String> originalLore = petSave.getItemLore();

        List<String> originalLore = new ArrayList<>();

        originalLore.addAll(petSave.getItemLore());

        if (petStats.getXpBoost() > 1.0) {
            AttachableItemSave ais = PetsConfig.getInstance().attachableItems.stream()
                    .filter(obj -> obj.getName().equals("xpshard"))
                    .findFirst()
                    .orElse(null);

            if (ais != null) {
                String aisLore = VelocityFeatures.chat(ais.getAttachedLore().replace("<multiplier>", petStats.getXpBoost() + ""));

                originalLore.add("&7 ");
                originalLore.add(aisLore);
            }
        }

        String joinLore = VelocityFeatures.chat(String.join("VDIB", originalLore)
                .replace("<xp>", petStats.getXp() + "")
                .replace("<level>", petStats.getLevel() + "")
                .replace("<xpToLevelUp>", petStats.getXpToLevelUp() + "")
        );

        itemMeta.setLore(Arrays.asList(joinLore.split("VDIB")));

        item.setItemMeta(itemMeta);

        return item;
    }

    public ItemStack updateItemNBT(ItemStack item, PetStats petStats) {
        NBTItem nbtItem = new NBTItem(item);
        NBTCompound compound = nbtItem.addCompound("velocity_pets_item");

        compound.setInteger("level", petStats.getLevel());
        compound.setInteger("xp", petStats.getXp());
        compound.setInteger("xpToLevelUp", petStats.getXpToLevelUp());

        return nbtItem.getItem();
    }

    public ItemStack buildItem(String type, int amount, double multiplier) {

        AttachableItemSave ais = PetsConfig.getInstance().attachableItems.stream().filter(obj -> obj.getName().equals(type)).findFirst().orElse(null);

        if (ais == null) return null;

        ItemStack item = new ItemStack(Material.getMaterial(ais.getMaterial()), amount, (byte) ais.getData());

        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.setDisplayName(VelocityFeatures.chat(ais.getDisplayName()
                .replace("<multiplier>", multiplier + "")
        ));

        String lore = VelocityFeatures.chat(String.join("VDIB", ais.getLore()));

        itemMeta.setLore(Arrays.asList(lore.split("VDIB")));
        item.setItemMeta(itemMeta);

        NBTItem nbtItem = new NBTItem(item);

        NBTCompound nbtCompound = nbtItem.addCompound("velocity_pets_attachable");

        nbtCompound.setString("id", type);
        nbtCompound.setDouble("multiplier", multiplier);

        return nbtItem.getItem();

    }

    public boolean isPet(ItemStack itemStack) {
        NBTItem nbtItem = new NBTItem(itemStack);

        NBTCompound nbtCompound = nbtItem.getCompound("velocity_pets_item");

        if (nbtCompound == null) return false;
        return true;
    }

    public boolean isOnCooldown(UUID uuid, String id) {
        if (!cooldownCache.containsKey(uuid)) return false;

        Cooldown cd = cooldownCache.get(uuid).stream().filter(obj -> obj.getType() == id).findFirst().orElse(null);

        if (cd == null) return false;

        return cd.isOnCooldown();
    }

    public void addCooldown(UUID id, Cooldown cooldown) {
        if (!cooldownCache.containsKey(id)) cooldownCache.put(id, new ObjectArrayList<>());

        cooldownCache.get(id).add(cooldown);
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

    public void updateItem(Player player, ItemStack itemStack) {
        int set = itemStack.getAmount() - 1;

        if (set == 0) {
            int slot = player.getInventory().getHeldItemSlot();

            player.getInventory().setItem(slot, new ItemStack(Material.AIR));
            player.updateInventory();
            return;
        }

        player.getItemInHand().setAmount(set);
        player.updateInventory();
    }

    public boolean isPetToggled(Player player) {
        UUID id = player.getUniqueId();

        if (!toggledPets.containsKey(id)) return true;

        return toggledPets.get(id);
    }

    public void updatePetVisibility(PetWrapper petWrapper) {
        for (Object2ObjectMap.Entry<UUID, Boolean> entry : Pets.toggledPets.object2ObjectEntrySet()) {
            boolean isEnabled = entry.getValue();

            if (!isEnabled) {
                Player player = Bukkit.getPlayer(entry.getKey());
                petWrapper.hologram.setHidePlayer(player);
            }
        }
    }

    public void unequipPet(Player player) {
        UUID id = player.getUniqueId();

        PetWrapper petWrapper = equippedPets.get(id);

        try {
            petWrapper.deleteHologram();
        } catch (Throwable ignored) {

        }

        equippedPets.remove(id);
    }

    public void forceUnequipPets() {
        for (Object2ObjectMap.Entry<UUID, PetWrapper> map : equippedPets.object2ObjectEntrySet()) {
            Player player = Bukkit.getPlayer(map.getKey());
            unequipPet(player);
        }
    }

    @Override
    public String getName() {
        return "pets";
    }

    @Override
    public boolean isEnabled() {
        return PetsConfig.getInstance().isEnabled();
    }

    @Override
    public void onEnable() {
        loadPets();

        VelocityFeatures.registerEvent(new PetsListener());
        CommandAPI.getInstance().enableCommand(new PetsCommand());

        new EffectInterval();
        new PetsRunnable().start(VelocityFeatures.getInstance());

        PetsConfig.getInstance().setEnabled(true);
    }

    @Override
    public void onDisable() {
        VelocityFeatures.unregisterEvent(PetsListener.getInstance());
        CommandAPI.getInstance().disableCommand(PetsCommand.class);

        EffectInterval.getInstance().bukkitTask.cancel();
        PetsRunnable.getInstance().runnable.cancel();

        PetsConfig.getInstance().setEnabled(false);
    }

    @Override
    public String placeholderRequest(Player player, String arg) {
        return "";
    }

}
