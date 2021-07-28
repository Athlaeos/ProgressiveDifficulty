package me.athlaeos.progressivelydifficultmobs.commands;

import me.athlaeos.progressivelydifficultmobs.main.Main;
import me.athlaeos.progressivelydifficultmobs.managers.PlayerKarmaManager;
import me.athlaeos.progressivelydifficultmobs.managers.PluginConfigurationManager;
import me.athlaeos.progressivelydifficultmobs.utils.Utils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

public class SetKarmaMultiplierCommand implements Command{

    private final PlayerKarmaManager karmaManager;
    private final PluginConfigurationManager config;

    public SetKarmaMultiplierCommand(){
        config = PluginConfigurationManager.getInstance();
        karmaManager = PlayerKarmaManager.getInstance();
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        double newKarmaMultiplier = 1.0;
        if (args.length > 2){
            if (!(args[1].equalsIgnoreCase("good") || args[1].equalsIgnoreCase("bad"))){
                return false;
            }
            try {
                newKarmaMultiplier = Double.parseDouble(args[2]);
            } catch (Exception e){
                sender.sendMessage(Utils.chat(config.getInvalidNumberError()));
                return true;
            }
        }
        if (args.length == 3){
            if (!(sender instanceof Player)){
                sender.sendMessage(Utils.chat(config.getErrorNoPermission()));
                return true;
            }
            Player p = (Player) sender;
            if (args[1].equalsIgnoreCase("good")){
                karmaManager.setGoodKarmaMultiplier(p.getUniqueId(), newKarmaMultiplier);
                sender.sendMessage(Utils.chat(config.getSetGoodKarmaMultiplierMessage().replace("{player}", p.getName()).replace("{amount}", "" + newKarmaMultiplier)));
            } else if (args[1].equalsIgnoreCase("bad")){
                karmaManager.setBadKarmaMultiplier(p.getUniqueId(), newKarmaMultiplier);
                sender.sendMessage(Utils.chat(config.getSetBadKarmaMultiplierMessage().replace("{player}", p.getName()).replace("{amount}", "" + newKarmaMultiplier)));
            }
            return true;
        }
        if (args.length == 4) {
            Player p;
            if (sender.hasPermission("pdm.managekarma")){
                if (Main.getInstance().getServer().getPlayer(args[3]) != null){
                    p = Main.getInstance().getServer().getPlayer(args[3]);
                    assert p != null;
                    if (args[1].equalsIgnoreCase("good")){
                        karmaManager.setGoodKarmaMultiplier(p.getUniqueId(), newKarmaMultiplier);
                        sender.sendMessage(Utils.chat(config.getSetGoodKarmaMultiplierMessage()
                                .replace("{player}", p.getName())
                                .replace("{amount}", "" + newKarmaMultiplier)));
                    } else if (args[1].equalsIgnoreCase("bad")){
                        karmaManager.setBadKarmaMultiplier(p.getUniqueId(), newKarmaMultiplier);
                        sender.sendMessage(Utils.chat(config.getSetBadKarmaMultiplierMessage()
                                .replace("{player}", p.getName())
                                .replace("{amount}", "" + newKarmaMultiplier)));
                    }
                    return true;
                } else {
                    sender.sendMessage(Utils.chat(config.getPlayerNotFoundError()));
                    return true;
                }
            } else {
                sender.sendMessage(Utils.chat(config.getErrorNoPermission()));
                return true;
            }
        }
        return false;
    }

    @Override
    public String[] getRequiredPermission() {
        return new String[]{"pdm.managekarma", "pdm.setownkarma"};
    }

    @Override
    public String getFailureMessage() {
        return Utils.chat("&c/pdm setkarmamult [amount] <player>");
    }

    @Override
    public String[] getHelpEntry() {
        return new String[]{
                Utils.chat("&8&m                                             "),
                Utils.chat("&e/pdm setkarmamult [good/bad] [amount] <player>"),
                Utils.chat("&7" + config.getSetKarmaMultiplierDescription()),
                Utils.chat("&7>" + config.getTranslationPermissions() + " &epdm.managekarma | pdm.setownkarma")
        };
    }

    @Override
    public List<String> getSubcommandArgs(CommandSender sender, String[] args) {
        if (args.length == 2){
            return Arrays.asList(
                    "good",
                    "bad"
            );
        }
        return null;
    }
}
