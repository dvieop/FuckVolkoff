package FuckParty.fuckVolkoff.src.main.java.xyz.velocity.modules.mobarena;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import xyz.velocity.modules.mobarena.config.saves.ClassSave;

import java.util.UUID;

public class ArenaPlayer {

    UUID id;
    ClassSave playerClass;

    public ArenaPlayer(UUID id) {
        this.id = id;
        this.playerClass = null;
    }

    public Player getPlayer() {
        return Bukkit.getPlayer(this.id);
    }

    public void setPlayerClass(ClassSave playerClass) {
        this.playerClass = playerClass;
    }
}
