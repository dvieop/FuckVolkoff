package FuckParty.fuckVolkoff.src.main.java.xyz.velocity.modules.tntpouch;

import lombok.Getter;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import xyz.velocity.modules.util.ItemUtil;

public class PouchListener implements Listener {

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent e) {
        if (ItemUtil.isAirOrNull(e.getItemInHand())) return;
        if (TNTPouch.getInstance().isTNTPouch(e.getItemInHand())) {
            e.setCancelled(true);
        }
    }

    public PouchListener() {
        instance = this;
    }

    @Getter
    private static PouchListener instance;

}
