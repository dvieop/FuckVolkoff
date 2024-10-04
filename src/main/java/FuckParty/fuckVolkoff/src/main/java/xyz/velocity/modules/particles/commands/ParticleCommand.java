package FuckParty.fuckVolkoff.src.main.java.xyz.velocity.modules.particles.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import xyz.velocity.commands.BaseCommand;
import xyz.velocity.commands.annotations.Command;
import xyz.velocity.modules.particles.config.ParticleConfig;

@Command(name = "ptoggle")
public class ParticleCommand extends BaseCommand {

    public ParticleCommand() {
        super(ParticleCommand.class.getAnnotation(Command.class).name());

        this.setWorksWithNoArgs(true);
    }

    @Override
    protected boolean noChildrenExecute(CommandSender commandSender, String string, String[] args) {
        Player player = (Player) commandSender;
        boolean toggle = !ParticleConfig.getInstance().toggles.get(player.getUniqueId());

        ParticleConfig.getInstance().toggles.put(((Player) commandSender).getUniqueId(), toggle);

        commandSender.sendMessage(ParticleConfig.getInstance().toggleMsg
                .replace("<toggle>", toggle + "")
        );

        ParticleConfig.getInstance().saveData();
        return false;
    }

}
