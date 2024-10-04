package FuckParty.fuckVolkoff.src.main.java.xyz.velocity.modules.pets.util;

import net.minecraft.server.v1_8_R3.*;

public class PetArmorStand extends EntityArmorStand {

    public PetArmorStand(World world) {
        super(world);
        super.noclip = false;
        this.setBoundingBox(new ArmorstandBB());
    }

    public void setBoundingBox(AxisAlignedBB boundingBox) {
        super.a(boundingBox);
    }

}
