package FuckParty.fuckVolkoff.src.main.java.xyz.velocity.modules;

import com.google.common.collect.Multimap;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import xyz.velocity.modules.armorsets.ArmorSets;
import xyz.velocity.modules.customenchants.CustomEnchants;
import xyz.velocity.modules.customenchants.EnchantItems;
import xyz.velocity.modules.garrison.Garrison;
import xyz.velocity.modules.masks.Masks;
import xyz.velocity.modules.pets.Pets;
import xyz.velocity.modules.util.ItemUtil;
import xyz.velocity.modules.util.Pair;

import java.util.Map;
import java.util.UUID;

public class GlobalDamageModifier implements Listener {

    @EventHandler(priority = EventPriority.LOWEST)
    public void damageCalculation(EntityDamageByEntityEvent e) {
        if (!(e.getDamager() instanceof Player)) return;
        if (!(e.getEntity() instanceof Player)) return;

        Player damager = (Player) e.getDamager();
        Player violated = (Player) e.getEntity();

        double baseDamage = getBaseDamage(damager, damager.getItemInHand()) + 1;
        double multiplier = getDamageMultiplier(damager, violated);

        e.setDamage(baseDamage * multiplier);
    }

    public static void setDamageModifiers(EntityDamageByEntityEvent e) {
        Player violated = (Player) e.getEntity();

        double baseDamage = e.getDamage();

        e.setDamage(EntityDamageEvent.DamageModifier.BASE, baseDamage);

        float f = (float) e.getDamage();

        float armorModifier = -(f - applyArmorModifier(violated, f));
        f += armorModifier;

        float resistanceModifier = applyResistanceModifier(violated, f);
        f += resistanceModifier;

        float magicModifier = -(f - applyMagicModifier(violated, f));

        e.setDamage(EntityDamageEvent.DamageModifier.ARMOR, armorModifier);
        e.setDamage(EntityDamageEvent.DamageModifier.RESISTANCE, resistanceModifier);
        e.setDamage(EntityDamageEvent.DamageModifier.MAGIC, magicModifier);
        e.setDamage(EntityDamageEvent.DamageModifier.BLOCKING, 0);
    }

    private static double getBaseDamage(Player player, ItemStack itemStack) {
        if (ItemUtil.isAirOrNull(itemStack)) return 0.0;
        if (!EnchantItems.isWeapon(itemStack)) return 0.0;
        if (ArmorSets.getInstance().isEnabled() && ArmorSets.getInstance().isSpecialItem(player)) {
            return ArmorSets.getInstance().getWeaponDamage(player) + getSharpnessDamage(itemStack);
        }

        return getAttackDamage(itemStack) * calculateStrength(player) + getSharpnessDamage(itemStack);
    }

    public static double getAttackDamage(ItemStack itemStack) {
        ItemStack item = itemStack.clone();
        net.minecraft.server.v1_8_R3.ItemStack nmsStack = CraftItemStack.asNMSCopy(item);

        Multimap<String, AttributeModifier> map = nmsStack.B();

        try {
            return map.get("generic.attackDamage").stream().findFirst().get().d();
        } catch (Throwable err) {
            return 1.0;
        }
    }

    public static double getSharpnessDamage(ItemStack itemStack) {
        double damage = 0;

        for (Map.Entry<Enchantment, Integer> map : itemStack.getEnchantments().entrySet()) {
            if (!map.getKey().equals(Enchantment.DAMAGE_ALL)) continue;
            damage = 1.25 * map.getValue();
        }

        return damage;
    }

    public static double getDamageMultiplier(Player attacker, Player violated) {
        double multiplier = 1.0;
        
        UUID attackerID = attacker.getUniqueId();
        UUID violatedID = violated.getUniqueId();

        if (ArmorSets.getInstance().isEnabled()) {
            if (ArmorSets.equippedSets.containsKey(attackerID)) multiplier += ArmorSets.getInstance().getArmorMultiplier(attacker, "damage");
            if (ArmorSets.equippedSets.containsKey(violatedID)) multiplier += ArmorSets.getInstance().getArmorMultiplier(violated, "reduce");
            multiplier += ArmorSets.getInstance().getAbilityMultiplier(attacker, true);
            multiplier += ArmorSets.getInstance().getAbilityMultiplier(violated, false);
        }

        if (Masks.getInstance().isEnabled()) {
            if (Masks.equippedMasks.containsKey(attackerID)) multiplier += Masks.getInstance().getMaskMultiplier(attacker, "damage");
            if (Masks.equippedMasks.containsKey(violatedID)) multiplier += Masks.getInstance().getMaskMultiplier(violated, "reduce");
        }

        if (Pets.getInstance().isEnabled()) {
            if (Pets.equippedPets.containsKey(attackerID) && Pets.getInstance().getPetType(attacker).equalsIgnoreCase("COMBAT")) multiplier += Pets.getInstance().getPetMultiplier(attacker, "damage:");
            if (Pets.equippedPets.containsKey(violatedID) && Pets.getInstance().getPetType(violated).equalsIgnoreCase("COMBAT")) multiplier += Pets.getInstance().getPetMultiplier(violated, "reduce:");
        }

        if (CustomEnchants.getInstance().isEnabled()) {
            multiplier += (EnchantItems.getInstance().getHeroicArmorMultiplier(violated) - 1.0);
            multiplier += (EnchantItems.getInstance().getHeroicWeaponMultiplier(attacker) - 1.0);
        }

        if (Garrison.getInstance().isEnabled()) {
            multiplier += Garrison.getInstance().getDamageReduction(violated);
        }

        return multiplier;
    }

    public static Pair<Double, Double> calculateArmorAndMagic(Player player) {
        double armor = 0;
        double magic = 0;

        for (ItemStack armorContent : player.getInventory().getArmorContents()) {
            if (ItemUtil.isAirOrNull(armorContent)) continue;
            if (!armorContent.getEnchantments().containsKey(Enchantment.PROTECTION_ENVIRONMENTAL)) continue;

            String itemName = armorContent.getType().name();

            if (itemName.endsWith("_HELMET") || itemName.endsWith("_BOOTS") || (Masks.getInstance().isEnabled()) && Masks.getInstance().hasMask(armorContent)) {
                armor += (-0.12000000476837158);
                magic += -0.03520005941390991 * armorContent.getEnchantmentLevel(Enchantment.PROTECTION_ENVIRONMENTAL);
            }
            else if (itemName.endsWith("_LEGGINGS")) {
                armor += (-0.24000000953674316);
                magic += -0.030399978160858154 * armorContent.getEnchantmentLevel(Enchantment.PROTECTION_ENVIRONMENTAL);
            }
            else if (itemName.endsWith("_CHESTPLATE")) {
                armor += (-0.3199999928474426);
                magic += -0.027200043201446533 * armorContent.getEnchantmentLevel(Enchantment.PROTECTION_ENVIRONMENTAL);
            }
        }

        return new Pair<>(armor, magic);
    }

    private static double calculateStrength(Player player) {
        double multiplier = 1.0;

        PotionEffect effect = player.getActivePotionEffects().stream().filter(obj -> obj.getType().equals(PotionEffectType.INCREASE_DAMAGE)).findFirst().orElse(null);

        if (effect != null) {
            int strength = effect.getAmplifier();

            multiplier = 2.3 + (strength * 1.3);
        }

        return multiplier;
    }

    private static boolean isFullSet(Player player) {
        for (ItemStack armorContent : player.getInventory().getArmorContents()) {
            if (ItemUtil.isAirOrNull(armorContent)) return false;
        }

        return true;
    }

    public static int getEPF(PlayerInventory inv) {
        ItemStack helm = inv.getHelmet();
        ItemStack chest = inv.getChestplate();
        ItemStack legs = inv.getLeggings();
        ItemStack boot = inv.getBoots();

        int epf = 0;

        if (helm != null) {
            int level = helm.getEnchantmentLevel(Enchantment.PROTECTION_ENVIRONMENTAL);
            if (level >= 4) level += 1;
            epf += level;
        }

        if (chest != null) {
            int level = chest.getEnchantmentLevel(Enchantment.PROTECTION_ENVIRONMENTAL);
            if (level >= 4) level += 1;
            epf += level;
        }

        if (legs != null) {
            int level = legs.getEnchantmentLevel(Enchantment.PROTECTION_ENVIRONMENTAL);
            if (level >= 4) level += 1;
            epf += level;
        }

        if (boot != null) {
            int level = boot.getEnchantmentLevel(Enchantment.PROTECTION_ENVIRONMENTAL);
            if (level >= 4) level += 1;
            epf += level;
        }

        return epf;

    }

    public static int calculateMagic(PlayerInventory inv) {
        int epf = getEPF(inv);

        if (epf > 25) {
            epf = 25;
        } else if (epf < 0) {
            epf = 0;
        }

        return ((epf + 1 >> 1) + ((epf >> 1) + 1) / 2);
    }

    public static int calculateArmorPoints(ItemStack[] equipment) {
        int points = 0;

        for (ItemStack itemStack : equipment) {
            if (ItemUtil.isAirOrNull(itemStack)) continue;

            String itemName = itemStack.getType().name();

            if (itemName.endsWith("_HELMET") || itemName.endsWith("_BOOTS") || (Masks.getInstance().isEnabled()) && Masks.getInstance().hasMask(itemStack)) {
                points += 3;
            }
            else if (itemName.endsWith("_CHESTPLATE")) points += 8;
            else if (itemName.endsWith("_LEGGINGS")) points += 6;

        }

        return points;
    }

    public static float applyArmorModifier(Player player, float damage) {
        int i = 25 - calculateArmorPoints(player.getInventory().getArmorContents());
        float f1 = damage * (float) i;

        // this.damageArmor(f); // CraftBukkit - Moved into d(DamageSource, float)
        damage = f1 / 25.0F;

        return damage;
    }

    public static float applyMagicModifier(Player player, float f) {
        int i;
        int j;
        float f1;

        if (f <= 0.0F) {
            return 0.0F;
        } else {
            i = calculateMagic(player.getInventory());
            if (i > 20) {
                i = 20;
            }

            if (i > 0 && i <= 20) {
                j = 25 - i;
                f1 = f * (float) j;
                f = f1 / 25.0F;
            }

            return f;
        }
    }

    public static float applyResistanceModifier(Player player, float f) {
        if (((CraftPlayer) player).getHandle().hasEffect(MobEffectList.RESISTANCE)) {
            int i = ((((CraftPlayer) player).getHandle()).getEffect(MobEffectList.RESISTANCE).getAmplifier() + 1) * 5;
            int j = 25 - i;
            float f1 = f * (float) j;
            return -(f - (f1 / 25.0F));
        }
        return (float) -0.0;
    }

    public static void setAttribute(ItemStack itemStack, double damage) {
        net.minecraft.server.v1_8_R3.ItemStack nmsStack = CraftItemStack.asNMSCopy(itemStack);

        String idToConvert = "cb3f55d3-645c-4f38-a497-9c13a33db5cf";
        UUID uuid = UUID.fromString(idToConvert);

        AttributeModifier attributeModifier = new AttributeModifier(uuid, "Weapon modifier", damage, 0);
        attributeModifier.a(true);

        nmsStack.B().put("generic.attackDamage", attributeModifier);
    }

}
