package me.athlaeos.progressivelydifficultmobs.commands;

import me.athlaeos.progressivelydifficultmobs.main.Main;
import me.athlaeos.progressivelydifficultmobs.managers.PlayerCurseManager;
import me.athlaeos.progressivelydifficultmobs.managers.PlayerKarmaManager;
import me.athlaeos.progressivelydifficultmobs.managers.PluginConfigurationManager;
import me.athlaeos.progressivelydifficultmobs.utils.Utils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class SetCurseCommand implements Command{

    private final PlayerCurseManager curseManager;
    private final PluginConfigurationManager config;

    public SetCurseCommand(){
        config = PluginConfigurationManager.getInstance();
        curseManager = PlayerCurseManager.getInstance();
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        if (args.length > 1){
            Player p = null;
            if (sender instanceof Player){
                p = (Player) sender;
            }
            if (args.length == 3){
                if (sender.hasPermission("pdm.managecurse")){
                    if (Main.getInstance().getServer().getPlayer(args[2]) != null) {
                        p = Main.getInstance().getServer().getPlayer(args[2]);
                    } else {
                        sender.sendMessage(Utils.chat(config.getPlayerNotFoundError()));
                        return true;
                    }
                } else {
                    sender.sendMessage(Utils.chat(config.getErrorNoPermission()));
                    return true;
                }
            }
            int newCurse = 0;
            try {
                newCurse = Integer.parseInt(args[1]);
            } catch (Exception e){
                sender.sendMessage(Utils.chat(config.getInvalidNumberError()));
                return true;
            }
            if (p != null){
                curseManager.setCurse(p.getUniqueId(), newCurse);
                sender.sendMessage(Utils.chat(config.getSetCurseMessage()
                        .replace("{player}", p.getName())
                        .replace("{amount}", "" + curseManager.getCurse(p.getUniqueId()))));
            } else {
                sender.sendMessage(Utils.chat(config.getPlayerNotFoundError()));
            }
            return true;
        }
        return false;
    }

    @Override
    public String[] getRequiredPermission() {
        return new String[]{"pdm.managecurse", "pdm.setowncurse"};
    }

    @Override
    public String getFailureMessage() {
        return Utils.chat("&c/pdm setcurse [amount] <player>");
    }

    @Override
    public String[] getHelpEntry() {
        return new String[]{
                Utils.chat("&8&m                                             "),
                Utils.chat("&e/pdm setcurse [amount] <player>"),
                Utils.chat("&7" + config.getSetCurseDescription()),
                Utils.chat("&7>" + config.getTranslationPermissions() + " &epdm.managecurse | pdm.setowncurse")
        };
    }

    @Override
    public List<String> getSubcommandArgs(CommandSender sender, String[] args) {
        return null;
    }
}
