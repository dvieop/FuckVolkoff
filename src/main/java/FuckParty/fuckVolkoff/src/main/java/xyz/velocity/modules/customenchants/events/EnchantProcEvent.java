package FuckParty.fuckVolkoff.src.main.java.xyz.velocity.modules.customenchants.events;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import xyz.velocity.VelocityFeatures;
import xyz.velocity.modules.customenchants.CustomEnchants;
import xyz.velocity.modules.customenchants.EnchantManager;
import xyz.velocity.modules.customenchants.config.CustomEnchantConfig;
import xyz.velocity.modules.customenchants.config.saves.EnchantSave;
import xyz.velocity.modules.customenchants.enchants.AbstractEnchant;

public class EnchantProcEvent extends PlayerEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();
    private final Entity victim;
    private final AbstractEnchant abstractEnchant;
    private boolean cancel = false;

    public EnchantProcEvent(Player activator, Entity victim, AbstractEnchant abstractEnchant) {
        super(activator);
        this.victim = victim;
        this.abstractEnchant = abstractEnchant;

        handleCooldown(activator);

        if (CustomEnchants.getInstance().isSilenced(this.player)) this.setCancelled(true);
    }

    public void activationMessage() {
        CustomEnchantConfig config = CustomEnchantConfig.getInstance();
        EnchantSave enchantSave = CustomEnchantConfig.getInstance().getEnchantList().stream().filter(obj -> obj.getName().equals(abstractEnchant.getName())).findFirst().get();

        if (!enchantSave.isProcAlert()) return;

        if (this.victim == null) {
            this.player.sendMessage(VelocityFeatures.chat(config.defenseProcMsg
                    .replace("<enchant>", enchantSave.getDisplayName())
            ));
        } else {
            this.player.sendMessage(VelocityFeatures.chat(config.attackProcMsg
                    .replace("<player>", victim.getName())
                    .replace("<enchant>", enchantSave.getDisplayName())
            ));
        }
    }

    private void handleCooldown(Player activator) {
        EnchantSave enchantSave = CustomEnchants.getInstance().getEnchant(abstractEnchant.getName());

        if (enchantSave == null) return;
        if (EnchantManager.isOnCooldown(activator, enchantSave.getName())) {
            this.setCancelled(true);
            return;
        }
        if (enchantSave.getCooldown() < 1) return;

        EnchantManager.addCooldown(activator, enchantSave);
    }

    public Entity getVictim() {
        return victim;
    }

    @Override
    public boolean isCancelled() {
        return this.cancel;
    }

    @Override
    public void setCancelled(boolean b) {
        this.cancel = b;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

}
