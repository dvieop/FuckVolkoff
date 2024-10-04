package FuckParty.fuckVolkoff.src.main.java.xyz.velocity.modules.foundry.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import xyz.velocity.VelocityFeatures;
import xyz.velocity.commands.ChildCommand;
import xyz.velocity.MainConfig;
import xyz.velocity.modules.foundry.config.CreditsConfig;
import xyz.velocity.modules.foundry.config.FoundryConfig;

import java.util.ArrayList;
import java.util.List;

public class CreditsSubCommand extends ChildCommand {

    public CreditsSubCommand() {
        super();
    }

    @Override
    protected boolean noChildrenExecute(CommandSender commandSender, String string, String[] args) {

        if (args.length < 1) {
            sendUseMessage(commandSender, string, args);
            return false;
        }

        if (args[0].equals(null)) {
            sendUseMessage(commandSender, string, args);
            return false;
        }

        if (args.length > 0 && !args[0].equals("bal")) {
            if (!commandSender.isOp() || !commandSender.hasPermission("foundry.credits.*")) {
                commandSender.sendMessage(VelocityFeatures.chat(MainConfig.getInstance().commandPrefix + "No permission to execute this command."));
                return false;
            }
        }


        Player player = null;
        int credits = 0;

        try {
            if (args[0].equals("bal") && (!commandSender.isOp() || !commandSender.hasPermission("foundry.credits.*"))) {
                player = (Player) commandSender;
            } else {
                if (args.length < 2) {
                    player = (Player) commandSender;
                } else {
                    player = Bukkit.getPlayer(args[1]);
                }
            }

            if (player == null) {
                throw new NullPointerException();
            }

        } catch (NullPointerException err) {
            sendUseMessage(commandSender, string, args);
            return false;
        } catch (Throwable err) {
            throw new RuntimeException(err);
        }

        if (args.length > 2) {
            credits = Integer.parseInt(args[2]);
        }

        switch (args[0]) {

            case "give":
            {
                giveCredits(commandSender, player, credits);
            }
                break;

            case "set":
            {
                setCredits(commandSender, player, credits);
            }
                break;

            case "bal":
            {
                if (commandSender.isOp() || commandSender.hasPermission("foundry.credits.*")) {
                    checkBal(commandSender, player.equals(null) ? (Player) commandSender : player);
                } else {
                    checkBal(commandSender, (Player) commandSender);
                }
            }
                break;
        }

        return false;
    }

    private void giveCredits(CommandSender commandSender, Player player, int creditsToGive) {

        CreditsConfig config = CreditsConfig.getInstance();

        int credits = config.getPlayerCredits(player);

        config.getData().put(player.getUniqueId(), credits + creditsToGive);
        //config.saveData();

        formatMessage(commandSender, player, FoundryConfig.getInstance().getCreditsGive(), creditsToGive);
    }

    private void setCredits(CommandSender commandSender, Player player, int credits) {

        CreditsConfig config = CreditsConfig.getInstance();

        config.getData().put(player.getUniqueId(), credits);
        //config.saveConfig();

        formatMessage(commandSender, player, FoundryConfig.getInstance().getCreditsSet(), credits);
    }

    private void checkBal(CommandSender commandSender, Player player) {
        CreditsConfig config = CreditsConfig.getInstance();

        if (!config.getData().containsKey(player.getUniqueId())) {
            config.getData().put(player.getUniqueId(), 0);

            //config.saveData();
        }

        formatMessage(commandSender, player, FoundryConfig.getInstance().getCreditsBalance(), config.getPlayerCredits(player));
    }

    private void formatMessage(CommandSender commandSender, Player p, String s, int credits) {
        commandSender.sendMessage(VelocityFeatures.chat(s)
            .replace("<player>", p.getName())
            .replace("<credits>", credits + "")
        );
    }

    @Override
    public String getName() {
        return "credits";
    }

    @Override
    public List<String> getAliases() {
        List<String> aliases = new ArrayList<>();
        aliases.add("credits");
        return aliases;
    }

    @Override
    protected void sendUseMessage(CommandSender commandSender, String string, String[] args) {
        commandSender.sendMessage(VelocityFeatures.chat(MainConfig.getInstance().commandPrefix + "/foundry credits give <player> <amount>"));
        commandSender.sendMessage(VelocityFeatures.chat(MainConfig.getInstance().commandPrefix + "/foundry credits set <player> <amount>"));
        commandSender.sendMessage(VelocityFeatures.chat(MainConfig.getInstance().commandPrefix + "/foundry credits bal [player]"));
    }
}
