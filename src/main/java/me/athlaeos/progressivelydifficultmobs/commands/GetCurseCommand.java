package me.athlaeos.progressivelydifficultmobs.commands;

import me.athlaeos.progressivelydifficultmobs.main.Main;
import me.athlaeos.progressivelydifficultmobs.managers.PlayerCurseManager;
import me.athlaeos.progressivelydifficultmobs.managers.PlayerKarmaManager;
import me.athlaeos.progressivelydifficultmobs.managers.PluginConfigurationManager;
import me.athlaeos.progressivelydifficultmobs.utils.Utils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class GetCurseCommand implements Command{

    private final PlayerCurseManager curseManager;
    private final PluginConfigurationManager config;

    public GetCurseCommand(){
        config = PluginConfigurationManager.getInstance();
        curseManager = PlayerCurseManager.getInstance();
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)){
            sender.sendMessage(Utils.chat(config.getErrorNoPermission()));
            return true;
        }
        Player p = (Player) sender;
        if (args.length == 2) {
            if (sender.hasPermission("pdm.managecurse") || sender.hasPermission("pdm.getcurse")){
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
        int curse = curseManager.getCurse(p.getUniqueId());

        sender.sendMessage(Utils.chat(config.getGetCurseMessage()
                .replace("{player}", p.getName())
                .replace("{curse}", String.format("%s", curse))));
        return true;
    }

    @Override
    public String[] getRequiredPermission() {
            return new String[]{"pdm.managecurse", "pdm.getcurse", "pdm.getowncurse"};
    }

    @Override
    public String getFailureMessage() {
        return Utils.chat("&c/pdm getcurse <player>");
    }

    @Override
    public String[] getHelpEntry() {
        return new String[]{
                Utils.chat("&8&m                                             "),
                Utils.chat("&e/pdm getcurse [player]"),
                Utils.chat("&7" + config.getGetCurseCommandDescription()),
                Utils.chat("&7>" + config.getTranslationPermissions() + " &epdm.managecurse | pdm.getcurse | pdm.getowncurse")
        };
    }

    @Override
    public List<String> getSubcommandArgs(CommandSender sender, String[] args) {
        return null;
    }
}
