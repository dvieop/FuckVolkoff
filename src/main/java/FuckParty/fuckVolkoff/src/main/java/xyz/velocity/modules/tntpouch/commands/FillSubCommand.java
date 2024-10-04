package FuckParty.fuckVolkoff.src.main.java.xyz.velocity.modules.tntpouch.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import xyz.velocity.VelocityFeatures;
import xyz.velocity.commands.ChildCommand;
import xyz.velocity.MainConfig;
import xyz.velocity.modules.tntpouch.TNTPouch;

import java.util.Arrays;
import java.util.List;

public class FillSubCommand extends ChildCommand {

    @Override
    protected boolean noChildrenExecute(CommandSender commandSender, String string, String[] args) {
        if (args.length < 2) {
            this.sendUseMessage(commandSender, string, args);
            return false;
        }

        Player player = (Player) commandSender;
        int amount = Integer.parseInt(args[1]);

        if (player == null) return false;

        TNTPouch.getInstance().fillPouch(player, amount);

        return false;
    }

    @Override
    public String getName() {
        return "withdraw";
    }

    @Override
    public List<String> getAliases() {
        return Arrays.asList("withdraw");
    }

    @Override
    protected void sendUseMessage(CommandSender commandSender, String string, String[] args) {
        commandSender.sendMessage(VelocityFeatures.chat(MainConfig.getInstance().commandPrefix + "/tntpouch fill <amount>"));
    }

}
