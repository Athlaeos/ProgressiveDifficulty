package me.athlaeos.progressivelydifficultmobs.commands;

import me.athlaeos.progressivelydifficultmobs.main.Main;
import me.athlaeos.progressivelydifficultmobs.managers.PlayerKarmaManager;
import me.athlaeos.progressivelydifficultmobs.managers.PluginConfigurationManager;
import me.athlaeos.progressivelydifficultmobs.utils.Utils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class GetKarmaCommand implements Command{

    private final PlayerKarmaManager karmaManager;
    private final PluginConfigurationManager config;

    public GetKarmaCommand(){
        config = PluginConfigurationManager.getInstance();
        karmaManager = PlayerKarmaManager.getInstance();
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)){
            sender.sendMessage(Utils.chat(config.getErrorNoPermission()));
            return true;
        }
        Player p = (Player) sender;
        if (args.length == 2) {
            if (sender.hasPermission("pdm.managekarma")){
                if (Main.getInstance().getServer().getPlayer(args[1]) != null){
                    p = Main.getInstance().getServer().getPlayer(args[1]);
                } else {
                    sender.sendMessage(Utils.chat(config.getPlayerNotFoundError()));
                    return true;
                }
            } else {
                sender.sendMessage(Utils.chat(config.getErrorNoPermission()));
                return true;
            }
        }
        assert p != null;
        double karma = karmaManager.getKarma(p.getUniqueId());

        sender.sendMessage(Utils.chat(config.getGetKarmaMessage().replace("{player}", p.getName()).replace("{karma}", String.format("%.2f", karma))));
        return true;
    }

    @Override
    public String[] getRequiredPermission() {
            return new String[]{"pdm.managekarma", "pdm.getkarma", "pdm.getownkarma"};
    }

    @Override
    public String getFailureMessage() {
        return Utils.chat("&c/pdm getkarma <player>");
    }

    @Override
    public String[] getHelpEntry() {
        return new String[]{
                Utils.chat("&8&m                                             "),
                Utils.chat("&e/pdm getkarma [player]"),
                Utils.chat("&7" + config.getGetKarmaCommandDescription()),
                Utils.chat("&7>" + config.getTranslationPermissions() + " &epdm.managekarma | pdm.getkarma | pdm.getownkarma")
        };
    }

    @Override
    public List<String> getSubcommandArgs(CommandSender sender, String[] args) {
        return null;
    }
}
