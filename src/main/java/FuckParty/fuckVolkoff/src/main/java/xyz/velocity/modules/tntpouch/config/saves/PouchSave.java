package FuckParty.fuckVolkoff.src.main.java.xyz.velocity.modules.tntpouch.config.saves;

import lombok.Getter;

@Getter
public class PouchSave {

    int tier;
    int maxStorage;
    PouchItemSave item;

    public PouchSave(int tier, int maxStorage, PouchItemSave item) {
        this.tier = tier;
        this.maxStorage = maxStorage;
        this.item = item;
    }

}
