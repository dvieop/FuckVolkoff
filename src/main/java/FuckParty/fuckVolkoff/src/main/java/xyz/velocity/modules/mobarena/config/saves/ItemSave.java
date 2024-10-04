package FuckParty.fuckVolkoff.src.main.java.xyz.velocity.modules.mobarena.config.saves;

import lombok.Getter;

@Getter
public class ItemSave {

    String item;
    int damage;
    int amount;

    public ItemSave(String item, int damage, int amount) {
        this.item = item;
        this.damage = damage;
        this.amount = amount;
    }

}
