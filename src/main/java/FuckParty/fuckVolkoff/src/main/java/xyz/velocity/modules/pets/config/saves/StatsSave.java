package FuckParty.fuckVolkoff.src.main.java.xyz.velocity.modules.pets.config.saves;

import lombok.Getter;

import java.util.List;

@Getter
public class StatsSave {

    PetStats equippedPet;
    int slots;
    List<PetStats> petInventory;

    public StatsSave(PetStats equippedPet, int slots, List<PetStats> petInventory) {
        this.equippedPet = equippedPet;
        this.slots = slots;
        this.petInventory = petInventory;
    }

    public void setEquippedPet(PetStats equippedPet) {
        this.equippedPet = equippedPet;
    }

    public void setSlots(int slots) {
        this.slots = slots;
    }

}
