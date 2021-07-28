package me.athlaeos.progressivelydifficultmobs.commands;

import me.athlaeos.progressivelydifficultmobs.main.Main;
import me.athlaeos.progressivelydifficultmobs.managers.PlayerCurseManager;
import me.athlaeos.progressivelydifficultmobs.managers.PlayerKarmaManager;
import me.athlaeos.progressivelydifficultmobs.managers.PluginConfigurationManager;
import me.athlaeos.progressivelydifficultmobs.utils.Utils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class AddCurseCommand implements Command{

    private final PlayerCurseManager curseManager;
    private final PluginConfigurationManager config;

    public AddCurseCommand(){
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
            if (args.length >= 3){
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
            int addedCurse = 0;
            try {
                addedCurse = Integer.parseInt(args[1]);
            } catch (Exception e){
                sender.sendMessage(Utils.chat(config.getInvalidNumberError()));
                return true;
            }
            if (p != null){
                curseManager.addCurse(p.getUniqueId(), addedCurse);
                sender.sendMessage(Utils.chat(config.getAddCurseMessage()
                        .replace("{player}", p.getName())
                        .replace("{amount}", "" + addedCurse)
                        .replace("{newamount}", "" + curseManager.getCurse(p.getUniqueId()))));
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
        return Utils.chat("&c/pdm addcurse [amount] <player>");
    }

    @Override
    public String[] getHelpEntry() {
        return new String[]{
                Utils.chat("&8&m                                             "),
                Utils.chat("&e/pdm addcurse [amount] <player>"),
                Utils.chat("&7" + config.getAddCurseDescription()),
                Utils.chat("&7>" + config.getTranslationPermissions() + " &epdm.managecurse | pdm.setowncurse")
        };
    }

    @Override
    public List<String> getSubcommandArgs(CommandSender sender, String[] args) {
        return null;
    }
}
