package FuckParty.fuckVolkoff.src.main.java.xyz.velocity.modules.stronghold.commands;

import com.golfing8.kore.FactionsKore;
import dev.lyons.configapi.ConfigAPI;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import xyz.velocity.VelocityFeatures;
import xyz.velocity.commands.BaseCommand;
import xyz.velocity.commands.annotations.Command;
import xyz.velocity.MainConfig;
import xyz.velocity.modules.stronghold.config.DataConfig;
import xyz.velocity.modules.stronghold.config.StrongholdConfig;
import xyz.velocity.modules.stronghold.config.saves.DataSave;
import xyz.velocity.modules.stronghold.config.saves.StrongholdSave;
import xyz.velocity.modules.util.CapturePoint;
import xyz.velocity.modules.stronghold.Stronghold;
import xyz.velocity.modules.util.InventoryUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Command(name = "stronghold")
public class StrongholdCommand extends BaseCommand {

    public StrongholdCommand() {
        super(StrongholdCommand.class.getAnnotation(Command.class).name());

        this.setWorksWithNoArgs(true);
    }

    @Override
    protected boolean noChildrenExecute(CommandSender commandSender, String string, String[] args) {
        if (args.length < 1) {
            this.openInventory((Player) commandSender);
            return false;
        }

        if (args.length > 0) {
            switch (args[0]) {
                case "help":
                    sendUseMessage(commandSender, string, args);
                    break;
                case "forfeit":
                    forfeitStronghold(commandSender);
                    break;
                case "reload":
                    reloadConfig(commandSender);
                    break;
                case "set":
                    setStronghold(commandSender, string, args);
                    break;
                case "list":
                    list(commandSender);
                    break;
            }
        }

        return false;
    }

    private void openInventory(Player player) {

        StrongholdConfig config = StrongholdConfig.getInstance();

        Inventory inv = Bukkit.createInventory(null, config.getGuiSize(), VelocityFeatures.chat(config.getGuiName()));
        addInvContents(inv, config);

        player.openInventory(inv);

    }

    private void addInvContents(Inventory inv, StrongholdConfig config) {

        Stronghold.getInstance().capturePoints.forEach(capturePoint -> {
            StrongholdSave strongholdSave = capturePoint.getStronghold();

            String lore = String.join("VFSH", strongholdSave.getLore());

            String factionOwning = capturePoint.getFactionOwning() == null
                    ? "None"
                    : FactionsKore.getIntegration().getTagFromId(capturePoint.getFactionOwning());
            String factionContesting = capturePoint.getFactionContesting() == null
                    ? "None"
                    : FactionsKore.getIntegration().getTagFromId(capturePoint.getFactionContesting());
            String captureTime = formatTime(capturePoint.getTotalCaptureTime());
            String percent = capturePoint.getPercentage() + "";
            String status = getStatus(capturePoint);

            lore = VelocityFeatures.chat(lore
                    .replace("<status>", status)
                    .replace("<percent>", percent)
                    .replace("<faction_controlling>", factionOwning)
                    .replace("<faction_contesting>", factionContesting)
                    .replace("<time_controlled>", captureTime)
            );

            ItemStack itemStack = new ItemStack(Material.getMaterial(strongholdSave.getDisplayItem()), 1);
            ItemMeta meta = itemStack.getItemMeta();

            meta.setDisplayName(VelocityFeatures.chat(strongholdSave.getDisplayName()));
            meta.setLore(Arrays.asList(lore.split("VFSH")));

            itemStack.setItemMeta(meta);

            inv.setItem(strongholdSave.getSlot(), itemStack);

        });

        if (config.fillEmptyUiSlots) {
            InventoryUtil.fillEmptySlots(inv, config.fillMaterial, config.fillMaterialDamage);
        }

    }

    private String formatTime(int n) {
        int h = n / 3600;
        int m = (n % 3600) / 60;
        int s = n % 3600 % 60;

        String hDisplay = h > 0 ? h + (h == 1 ? "h " : "h ") : "";
        String mDisplay = m > 0 ? m + (m == 1 ? "m " : "m ") : "";
        String sDisplay = s > 0 ? s + (s == 1 ? "s" : "s") : "";

        return hDisplay + mDisplay + sDisplay;
    }

    private String getStatus(CapturePoint capturePoint) {
        if (capturePoint.getFactionContesting() != null && !capturePoint.getFactionContesting().equals(capturePoint.getFactionOwning())) {
            return "&c&lContested";
        }

        if (capturePoint.isNeutral()) return "&7&lNeutral";
        if (!capturePoint.isNeutral()) return "&a&lControlled";

        return "";
    }

    private void reloadConfig(CommandSender commandSender) {
        if (!commandSender.isOp() || !commandSender.hasPermission("stronghold.*")) {
            commandSender.sendMessage(VelocityFeatures.chat(MainConfig.getInstance().commandPrefix + "No permission to execute this command."));
            return;
        }

        ConfigAPI.getInstance().loadSpecificConfig(StrongholdConfig.getInstance(), false, false);

        //for (CapturePoint capturePoint : Stronghold.getInstance().capturePoints) {
            //capturePoint.getHologram().deleteHologram();
        //}

        Stronghold.getInstance().loadData();

        commandSender.sendMessage(VelocityFeatures.chat("&8(&9Velocity&8) &fReloaded the configs."));
    }

    private void forfeitStronghold(CommandSender commandSender) {
        Player player = (Player) commandSender;
        String getFaction = FactionsKore.getIntegration().getPlayerFactionId(player);

        Player leader = FactionsKore.getIntegration().getLeader(getFaction);

        if (leader == null || leader != player) {
            commandSender.sendMessage(VelocityFeatures.chat(MainConfig.getInstance().commandPrefix + "You must be the leader of the faction!"));
            return;
        }

        CapturePoint capturePoint = Stronghold.getInstance().capturePoints.stream().filter(obj -> obj.getFactionOwning() != null && obj.getFactionOwning().equals(getFaction)).findFirst().orElse(null);

        if (capturePoint == null) {
            commandSender.sendMessage(VelocityFeatures.chat(MainConfig.getInstance().commandPrefix + "You do not own any stronghold!"));
            return;
        }

        capturePoint.setFactionOwning(null);
        capturePoint.setPercentage(0);
        capturePoint.setNeutral(true);
        capturePoint.setTotalCaptureTime(0);

        DataSave dataSave = DataConfig.getInstance().strongholds.get(capturePoint.getStronghold().getName());

        dataSave.setFaction("");
        dataSave.setPercentage(0);
        dataSave.setNeutral(true);
        dataSave.setCaptureTime(0);

        commandSender.sendMessage(VelocityFeatures.chat(MainConfig.getInstance().commandPrefix + "You have forfeited the stronghold!"));
    }

    private void setStronghold(CommandSender commandSender, String string, String[] args) {
        if (!commandSender.isOp() || !commandSender.hasPermission("stronghold.*")) {
            commandSender.sendMessage(VelocityFeatures.chat(MainConfig.getInstance().commandPrefix + "No permission to execute this command."));
            return;
        }

        if (args.length < 3) {
            sendUseMessage(commandSender, string, args);
            return;
        }

        String faction = args[1];
        String stronghold = args[2];

        if (faction == null || stronghold == null) {
            sendUseMessage(commandSender, string, args);
            return;
        }

        String factionId = FactionsKore.getIntegration().getIdFromTag(faction);

        if (factionId == null) {
            commandSender.sendMessage(VelocityFeatures.chat(MainConfig.getInstance().commandPrefix + "Found no faction with that tag"));
            return;
        }

        CapturePoint capturePoint = Stronghold.getInstance().capturePoints.stream().filter(obj -> obj.getStronghold().getName().equals(stronghold)).findFirst().orElse(null);

        if (capturePoint == null) {
            commandSender.sendMessage(VelocityFeatures.chat(MainConfig.getInstance().commandPrefix + "Found no stronghold with that name"));
            return;
        }

        capturePoint.setFactionOwning(factionId);
        capturePoint.setPercentage(100);
        capturePoint.setNeutral(false);
        capturePoint.setTotalCaptureTime(0);

        DataSave dataSave = DataConfig.getInstance().strongholds.get(capturePoint.getStronghold().getName());

        dataSave.setFaction(factionId);
        dataSave.setPercentage(100);
        dataSave.setNeutral(false);
        dataSave.setCaptureTime(0);

        commandSender.sendMessage(VelocityFeatures.chat(MainConfig.getInstance().commandPrefix + "Successfully force set the stronghold"));
    }

    private void list(CommandSender commandSender) {
        if (!commandSender.isOp() || !commandSender.hasPermission("stronghold.*")) {
            commandSender.sendMessage(VelocityFeatures.chat(MainConfig.getInstance().commandPrefix + "No permission to execute this command."));
            return;
        }

        List<String> strongholds = new ArrayList<>();

        for (StrongholdSave strongholdSave : StrongholdConfig.getInstance().strongholds) {
            strongholds.add(strongholdSave.getName());
        }

        String msg = MainConfig.getInstance().commandPrefix + "Strongholds: " + String.join("&7, ", strongholds);

        commandSender.sendMessage(VelocityFeatures.chat(msg));
    }

    @Override
    protected void sendUseMessage(CommandSender sender, String string, String[] args) {
        sender.sendMessage(VelocityFeatures.chat(MainConfig.getInstance().commandPrefix + "/stronghold forfeit"));

        if (sender.isOp() || sender.hasPermission("stronghold.*")) {
            sender.sendMessage(VelocityFeatures.chat(MainConfig.getInstance().commandPrefix + "/stronghold set <faction> <stronghold>"));
            sender.sendMessage(VelocityFeatures.chat(MainConfig.getInstance().commandPrefix + "/stronghold list"));
        }
    }

}
