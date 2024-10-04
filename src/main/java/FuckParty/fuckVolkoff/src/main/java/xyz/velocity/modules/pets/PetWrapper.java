package FuckParty.fuckVolkoff.src.main.java.xyz.velocity.modules.pets;

import eu.decentsoftware.holograms.api.DHAPI;
import eu.decentsoftware.holograms.api.holograms.Hologram;
import eu.decentsoftware.holograms.api.holograms.HologramLine;
import eu.decentsoftware.holograms.api.holograms.enums.HologramLineType;
import lombok.Getter;
import org.bukkit.entity.Player;
import xyz.velocity.VelocityFeatures;
import xyz.velocity.modules.pets.config.saves.PetStats;

public class PetWrapper {

    Player player;
    PetStats petStats;
    Hologram hologram;

    @Getter
    CustomPet customPet;

    @Getter
    long time;

    public PetWrapper(Player player, CustomPet customPet, PetStats petStats) {
        this.player = player;
        this.customPet = customPet;
        this.petStats = petStats;

        spawnPet();

        this.time = System.currentTimeMillis() + 1000;
    }

    public void deleteHologram() {
        if (this.hologram != null) {
            this.hologram.delete();
        }
    }

    public void spawnPet() {
        String petName = (VelocityFeatures.chat(customPet.petSave.getDisplayName()
                .replace("<level>", petStats.getLevel() + "")
        ));

        this.hologram = DHAPI.createHologram(player.getName(), player.getLocation().add(0, -1.5, 0));

        HologramLine nameHologram = DHAPI.addHologramLine(hologram, petName);
        HologramLine petHologram = DHAPI.addHologramLine(hologram, customPet.getItem());

        nameHologram.setOffsetY(0.35);

        petHologram.setType(HologramLineType.SMALLHEAD);
        petHologram.setFacing(player.getLocation().getYaw());
    }

}
