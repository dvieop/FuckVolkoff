package FuckParty.fuckVolkoff.src.main.java.xyz.velocity.modules.garrison;

public enum EnumBoostMode {
    EXP,
    HEADHUNTING,
    DAMAGE,
    REDUCE,
    CROP,
    BLUEPRINT,
    PETXP,
    MINING;

    private static final EnumBoostMode[] vals = values();

    public EnumBoostMode next() {
        return vals[(this.ordinal() + 1) % vals.length];
    }
}
