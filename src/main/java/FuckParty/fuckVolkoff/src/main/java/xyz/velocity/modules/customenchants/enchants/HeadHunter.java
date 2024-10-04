package FuckParty.fuckVolkoff.src.main.java.xyz.velocity.modules.customenchants.enchants;

import com.google.common.util.concurrent.AtomicDouble;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;
import net.minecraft.server.v1_8_R3.EntityPlayer;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;
import xyz.velocity.modules.customenchants.CustomEnchants;
import xyz.velocity.modules.customenchants.EnchantManager;
import xyz.velocity.modules.customenchants.EnumEnchantType;
import xyz.velocity.modules.customenchants.annotations.Enchant;
import xyz.velocity.modules.customenchants.config.CustomEnchantConfig;
import xyz.velocity.modules.customenchants.config.saves.EnchantSave;
import xyz.velocity.modules.customenchants.events.EnchantProcEvent;
import xyz.velocity.modules.util.EnchantUtil;

import java.util.Arrays;

@Enchant
public class HeadHunter extends AbstractEnchant {

    private final CustomEnchantConfig config;

    public HeadHunter() {
        this.config = CustomEnchantConfig.getInstance();

        EnchantSave enchant = new EnchantSave(true, "HeadHunter", "&cHeadhunter", Arrays.asList("&7Chance to drop 2x heads!"), "WEAPON", "HEADHUNTER:<level>", 3, 3, 50, 1, false, this.extraInfo());

        if (!config.getEnchantList().stream().anyMatch(obj -> obj.getName().equals(enchant.getName()))) {
            config.getEnchantList().add(enchant);
        }

        EnchantManager.nonVanillaEnchants.put(this.getName(), this);
    }

    @Override
    public boolean isEnabled() {
        return CustomEnchantConfig.getInstance().getEnchantList().stream().filter(obj -> obj.getName().equals(this.getName())).findFirst().get().isEnabled();
    }

    @Override
    public String getName() {
        return "HeadHunter";
    }

    @Override
    public boolean isVanillaEnchant() {
        return false;
    }

    @Override
    public String getEnchant() {
        return "HEADHUNTER";
    }

    @Override
    public EnumEnchantType getEnchantType() {
        return EnumEnchantType.GRINDING;
    }

    @Override
    public <T extends Event> void runTask(T event) {
        EntityDeathEvent e;

        try {
            e = (EntityDeathEvent) event;
        } catch (Throwable err) {
            return;
        }

        Player p = e.getEntity().getKiller();

        double enchantInfo = getEnchantInformation(p);
        double chance = EnchantUtil.getRandomDouble();

        if (chance < enchantInfo) {
            EnchantProcEvent procEvent = CustomEnchants.getInstance().callEvent(p, null, this);
            if (procEvent.isCancelled()) return;

            procEvent.activationMessage();

            ObjectList<ItemStack> drops = new ObjectArrayList<>();

            for (ItemStack itemStack : e.getDrops()) {
                if (itemStack.getType() == Material.SKULL_ITEM) {
                    int amt = (int) (itemStack.getAmount() * 2);

                    itemStack.setAmount(amt);
                }

                drops.add(itemStack);
            }

            e.getDrops().clear();
            e.getDrops().addAll(drops);
        }
    }

    private JsonObject extraInfo() {
        JsonObject info = new JsonObject();

        info.addProperty("chancePerLevel", 5);

        info.toString();

        return info;
    }

    private double getEnchantInformation(Player player) {
        EntityPlayer entityPlayer = ((CraftPlayer) player).getHandle();

        AtomicDouble chance = new AtomicDouble(0);

        Object2ObjectMap<EnchantSave, Integer> toReturn = CustomEnchants.getInstance().getEnchantsFromPlayer(player);

        toReturn.forEach((enchant, level) -> {
            if (!enchant.getName().equals(this.getName())) return;
            JsonObject obj = new Gson().fromJson(enchant.getExtra().get(0), JsonObject.class);

            chance.set(obj.get("chancePerLevel").getAsDouble() * level);
        });

        return chance.get();
    }

}
