package FuckParty.fuckVolkoff.src.main.java.xyz.velocity.modules.stronghold.util;

public class Hologram {

    /*private static Hologram instance;

    public static Hologram getInstance() {
        return instance;
    }

    public com.gmail.filoghost.holographicdisplays.api.Hologram hologram = null;
    public LinkedList<TextLine> lines = new LinkedList();
    public List<String> original = new ArrayList<>();

    org.bukkit.Location location;
    Plugin plugin;
    CapturePoint capturePoint;

    public Hologram(Plugin plugin, org.bukkit.Location location, CapturePoint capturePoint) {
        this.plugin = plugin;
        this.location = location;
        this.capturePoint = capturePoint;
        this.original = capturePoint.getStronghold().getHologramLore();

        instance = this;
        spawnHologram();
    }

    public void spawnHologram() {

        hologram = HologramsAPI.createHologram(this.plugin, this.location);

        int i = 0;

        for (String s : original) {
            lines.add(hologram.insertTextLine(i, VelocityFeatures.chat(returnFormattedString(s))));
            i++;
        }

    }

    public void updateHologram() {
        for (int i = 0; i < lines.size(); i++) {
            TextLine line = lines.get(i);
            String org = original.get(i);

            line.setText(VelocityFeatures.chat(returnFormattedString(org)));
        }
    }

    private String formatTime(int n) {
        String time = "";

        int hours = n / 3600;
        int minutes = (n % 3600) / 60;
        n = n % 60;

        time += hours > 0 ? hours + "h " : "";

        return time += minutes + "m " + n + "s";
    }

    private String getStatus(CapturePoint capturePoint) {
        if (capturePoint.getFactionContesting() != null && !capturePoint.getFactionContesting().equals(capturePoint.getFactionOwning())) {
            return "&c&lContested";
        }

        if (capturePoint.isNeutral()) return "&7&lNeutral";
        if (!capturePoint.isNeutral()) return "&a&lControlled";

        return "";
    }

    private String returnFormattedString(String s) {
        String factionOwning = "None";

        try {
            factionOwning = (capturePoint.getFactionOwning() == null || capturePoint.getFactionOwning() == "")
                    ? "None"
                    : FactionsKore.getIntegration().getTagFromId(capturePoint.getFactionOwning());
        } catch (Throwable e) {
            DataConfig.getInstance().getStrongholds().get(capturePoint.getStronghold().getName()).setFaction("");
            factionOwning = "None";
        }

        String factionContesting = (capturePoint.getFactionContesting() == null || capturePoint.getFactionContesting() == "")
                ? "None"
                : FactionsKore.getIntegration().getTagFromId(capturePoint.getFactionContesting());
        String captureTime = formatTime(capturePoint.getTotalCaptureTime());
        String percent = capturePoint.getPercentage() + "";
        String status = getStatus(capturePoint);

        return s
                .replace("<status>", status)
                .replace("<percent>", percent)
                .replace("<faction_controlling>", factionOwning)
                .replace("<faction_contesting>", factionContesting)
                .replace("<time_controlled>", captureTime);
    }

    public void deleteHologram() {
        if (!this.hologram.isDeleted()) {
            this.hologram.delete();
        }
    }*/

}
