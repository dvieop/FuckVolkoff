package FuckParty.fuckVolkoff.src.main.java.xyz.velocity.modules.customenchants.enchants;

import com.google.gson.JsonObject;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import net.minecraft.server.v1_8_R3.EntityPlayer;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.block.BlockDamageEvent;
import xyz.velocity.modules.customenchants.EnumEnchantType;
import xyz.velocity.modules.customenchants.config.CustomEnchantConfig;
import xyz.velocity.modules.customenchants.config.saves.EnchantSave;
import xyz.velocity.modules.customenchants.CustomEnchants;
import xyz.velocity.modules.customenchants.EnchantManager;
import xyz.velocity.modules.customenchants.annotations.Enchant;

import java.util.Arrays;

@Enchant
public class ObsidianBreaker extends AbstractEnchant {

    private final CustomEnchantConfig config;

    public ObsidianBreaker() {
        this.config = CustomEnchantConfig.getInstance();

        EnchantSave enchant = new EnchantSave(true, "ObsidianBreaker", "&cObsidian Breaker", Arrays.asList("&7Instantly remove obsidian upon a hit!"), "PICKAXE", "OBSIDIAN_BREAKER:<level>", 3, 1, 10, 1, false, new JsonObject());

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
        return "ObsidianBreaker";
    }

    @Override
    public boolean isVanillaEnchant() {
        return false;
    }

    @Override
    public String getEnchant() {
        return "OBSIDIAN_BREAKER";
    }

    @Override
    public EnumEnchantType getEnchantType() {
        return EnumEnchantType.MINING;
    }

    @Override
    public <T extends Event> void runTask(T event) {
        BlockDamageEvent e = (BlockDamageEvent) event;

        if (!CustomEnchants.getInstance().canBuild(e.getPlayer(), e.getBlock().getLocation())) return;
        if (!hasBreakerEnchant(e.getPlayer())) return;

        if (e.getBlock().getType().equals(Material.OBSIDIAN)) {
            e.getBlock().setType(Material.AIR);
            e.setCancelled(true);
        }

    }

    private boolean hasBreakerEnchant(Player player) {
        EntityPlayer entityPlayer = ((CraftPlayer) player).getHandle();

        Object2ObjectMap<EnchantSave, Integer> toReturn = CustomEnchants.getInstance().getEnchantsOnItem(player.getItemInHand());

        for (Object2ObjectMap.Entry<EnchantSave, Integer> map : toReturn.object2ObjectEntrySet()) {
            if (map.getKey().getName().equals(this.getName())) return true;
        };

        return false;
    }

}
