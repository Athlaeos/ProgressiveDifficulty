package me.athlaeos.progressivelydifficultmobs.commands;

import me.athlaeos.progressivelydifficultmobs.main.Main;
import me.athlaeos.progressivelydifficultmobs.managers.PlayerKarmaManager;
import me.athlaeos.progressivelydifficultmobs.managers.PluginConfigurationManager;
import me.athlaeos.progressivelydifficultmobs.utils.Utils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class SetKarmaCommand implements Command{

    private final PlayerKarmaManager karmaManager;
    private final PluginConfigurationManager config;

    public SetKarmaCommand(){
        config = PluginConfigurationManager.getInstance();
        karmaManager = PlayerKarmaManager.getInstance();
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        double newKarma = 0.0;
        if (args.length > 1){
            try {
                newKarma = Double.parseDouble(args[1]);
            } catch (Exception e){
                sender.sendMessage(Utils.chat(config.getInvalidNumberError()));
                return true;
            }
        }
        if (args.length == 2){
            if (!(sender instanceof Player)){
                sender.sendMessage(Utils.chat(config.getErrorNoPermission()));
                return true;
            }
            Player p = (Player) sender;
            karmaManager.setKarma(p.getUniqueId(), newKarma);
            sender.sendMessage(Utils.chat(config.getSetKarmaMessage()
                    .replace("{player}", p.getName())
                    .replace("{amount}", "" + karmaManager.getKarma(p.getUniqueId()))));
            return true;
        }
        if (args.length == 3) {
            Player p;
            if (sender.hasPermission("pdm.managekarma")){
                if (Main.getInstance().getServer().getPlayer(args[2]) != null){
                    p = Main.getInstance().getServer().getPlayer(args[2]);
                    karmaManager.setKarma(p.getUniqueId(), newKarma);
                    sender.sendMessage(Utils.chat(config.getSetKarmaMessage()
                            .replace("{player}", p.getName())
                            .replace("{amount}", "" + karmaManager.getKarma(p.getUniqueId()))));
                } else {
                    sender.sendMessage(Utils.chat(config.getPlayerNotFoundError()));
                }
            } else {
                sender.sendMessage(Utils.chat(config.getErrorNoPermission()));
            }
            return true;
        }
        return false;
    }

    @Override
    public String[] getRequiredPermission() {
        return new String[]{"pdm.managekarma", "pdm.setownkarma"};
    }

    @Override
    public String getFailureMessage() {
        return Utils.chat("&c/pdm setkarma [amount] <player>");
    }

    @Override
    public String[] getHelpEntry() {
        return new String[]{
                Utils.chat("&8&m                                             "),
                Utils.chat("&e/pdm setkarma [amount] <player>"),
                Utils.chat("&7" + config.getSetKarmaDescription()),
                Utils.chat("&7>" + config.getTranslationPermissions() + " &epdm.managekarma | pdm.setownkarma")
        };
    }

    @Override
    public List<String> getSubcommandArgs(CommandSender sender, String[] args) {
        return null;
    }
}
