package FuckParty.fuckVolkoff.src.main.java.xyz.velocity.modules.customenchants.commands;

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
import xyz.velocity.modules.customenchants.config.CustomEnchantConfig;
import xyz.velocity.modules.customenchants.config.KitsConfig;
import xyz.velocity.modules.customenchants.config.saves.EnchantSave;
import xyz.velocity.modules.customenchants.CustomEnchants;
import xyz.velocity.modules.customenchants.EnchantItems;

import java.util.*;

@Command(name = "enchants")
public class CustomEnchantCommand extends BaseCommand {

    public CustomEnchantCommand() {
        super(CustomEnchantCommand.class.getAnnotation(Command.class).name());

        this.setWorksWithNoArgs(true);
    }

    @Override
    protected boolean noChildrenExecute(CommandSender commandSender, String string, String[] args) {
        if (args.length < 1) {
            this.openInventory((Player) commandSender);
            return false;
        }

        if (!commandSender.isOp() || !commandSender.hasPermission("enchants.*")) {
            commandSender.sendMessage(VelocityFeatures.chat(MainConfig.getInstance().commandPrefix + "No permission to execute this command."));
            return false;
        }

        if (args.length > 0 && args[0].equals("reload")) {
            reloadConfig(commandSender);
            return false;
        }

        if (args.length > 0 && args[0].equals("help")) {
            sendHelp(commandSender, string, args);
            return false;
        }

        if (args.length < 3) return false;

        switch (args[0]) {
            case "givekit":
                {
                    Player toGive = Bukkit.getPlayer(args[1]);
                    String kitName = args[2];

                    if (toGive != null && kitName != null) {
                        CustomEnchants.getInstance().giveKit(toGive, kitName);
                    }
                }
                break;

            case "giveitem":
                {
                    Player toGive = Bukkit.getPlayer(args[1]);
                    String item = args[2];
                    int amount = Integer.parseInt(args[3]);

                    ItemStack itemStack = EnchantItems.getInstance().buildItem(item, amount);

                    if (itemStack == null || toGive == null) return false;

                    toGive.getInventory().addItem(EnchantItems.getInstance().addGlow(itemStack));
                }
                break;

            case "giveenchant":
                {
                    Player toGive = Bukkit.getPlayer(args[1]);
                    String enchant = args[2];
                    boolean max = Boolean.parseBoolean(args[3]);
                    boolean random = Boolean.parseBoolean(args[4]);
                    int level = Integer.parseInt(args[5]);

                    EnchantSave enchantSave = CustomEnchants.getInstance().getEnchant(enchant);

                    if (enchantSave == null) return false;

                    toGive.getInventory().addItem(CustomEnchants.getInstance().buildBook(enchantSave, max, random, level));
                }
                break;
            case "giverandomenchant":
            {
                Player toGive = Bukkit.getPlayer(args[1]);
                int tier = Integer.parseInt(args[2]);
                boolean max = Boolean.parseBoolean(args[3]);
                boolean random = Boolean.parseBoolean(args[4]);
                int level = Integer.parseInt(args[5]);

                List<EnchantSave> enchants = CustomEnchants.getInstance().getEnchantsWithLevel(tier);

                EnchantSave enchant = CustomEnchants.getInstance().getRandomEnchant(enchants);

                if (enchant == null) return false;

                toGive.getInventory().addItem(CustomEnchants.getInstance().buildBook(enchant, max, random, level));
            }
            break;
        }

        return false;
    }

    @Override
    public void setWorksWithNoArgs(boolean worksWithNoArgs) {
        super.setWorksWithNoArgs(worksWithNoArgs);
    }

    private void openInventory(Player player) {

        CustomEnchantConfig config = CustomEnchantConfig.getInstance();

        Inventory inv = Bukkit.createInventory(null, config.getGuiSlots(), VelocityFeatures.chat(config.getGuiName()));
        addInvContents(inv, config);

        player.openInventory(inv);

    }

    private void addInvContents(Inventory inv, CustomEnchantConfig config) {

        config.mainUI.forEach(content -> {

            List<String> lore = Arrays.asList(VelocityFeatures.chat(content.getLore()).split("\\|"));

            ItemStack itemStack = new ItemStack(Material.getMaterial(content.getDisplayItem()), 1);
            ItemMeta meta = itemStack.getItemMeta();

            meta.setDisplayName(VelocityFeatures.chat(content.getDisplayName()));
            meta.setLore(lore);

            itemStack.setItemMeta(meta);

            inv.setItem(content.getSlot(), itemStack);

        });

    }

    private void reloadConfig(CommandSender commandSender) {
        ConfigAPI.getInstance().loadSpecificConfig(CustomEnchantConfig.getInstance(), false, false);
        ConfigAPI.getInstance().loadSpecificConfig(KitsConfig.getInstance(), false, false);

        commandSender.sendMessage(VelocityFeatures.chat("&8(&9Velocity&8) &fReloaded the configs."));
    }

    private void sendHelp(CommandSender commandSender, String string, String[] args) {
        commandSender.sendMessage(VelocityFeatures.chat(MainConfig.getInstance().commandPrefix + "/enchants reload"));
        commandSender.sendMessage(VelocityFeatures.chat(MainConfig.getInstance().commandPrefix + "/enchants givekit <player> <kit name>"));
        commandSender.sendMessage(VelocityFeatures.chat(MainConfig.getInstance().commandPrefix + "/enchants giveitem <player> <item> <amount>"));
        commandSender.sendMessage(VelocityFeatures.chat(MainConfig.getInstance().commandPrefix + "/enchants giveenchant <player> <enchant> <max success true/false> <random level true/false> <level>"));
        commandSender.sendMessage(VelocityFeatures.chat(MainConfig.getInstance().commandPrefix + "/enchants giverandomenchant <player> <tier> <max success true/false> <random level true/false> <level>"));
    }

}
