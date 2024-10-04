package FuckParty.fuckVolkoff.src.main.java.xyz.velocity.modules.generators.config.saves;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class GenDataSave {

    String genType;
    String location;
    int tier;
    int capacity;
    int storage;
    List<String> logs = new ArrayList<>();

    public GenDataSave(String genType, String location, int tier, int capacity, int storage) {
        this.genType = genType;
        this.location = location;
        this.tier = tier;
        this.capacity = capacity;
        this.storage = storage;
    }

}
