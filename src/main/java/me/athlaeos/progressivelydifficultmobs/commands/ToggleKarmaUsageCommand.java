package me.athlaeos.progressivelydifficultmobs.commands;

import me.athlaeos.progressivelydifficultmobs.main.Main;
import me.athlaeos.progressivelydifficultmobs.managers.JudgedPlayersManager;
import me.athlaeos.progressivelydifficultmobs.managers.PluginConfigurationManager;
import me.athlaeos.progressivelydifficultmobs.utils.Utils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

public class ToggleKarmaUsageCommand implements Command{

    private final PluginConfigurationManager config;

    public ToggleKarmaUsageCommand(){
        config = PluginConfigurationManager.getInstance();
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)){
            sender.sendMessage(Utils.chat(config.getErrorNoPermission()));
            return true;
        }
        Player p = (Player) sender;
        boolean forceChance = false;
        boolean forceChangeTo = false;
        if (args.length >= 2) {
            if (p.hasPermission("pdm.managekarma")){
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
            if (args.length >= 3){
                forceChangeTo = Boolean.parseBoolean(args[2]);
                forceChance = true;
            }
        }
        if (config.isForceKarma() && !sender.hasPermission("pdm.managekarma")){
            sender.sendMessage(Utils.chat(config.getKarmaEnabledByDefault()));
            return true;
        }
        assert p != null;
        if (config.isPermanentEnableOnToggle()){
            if (!sender.hasPermission("pdm.managekarma")){
                if (JudgedPlayersManager.getInstance().isPlayerJudged(p)){
                    sender.sendMessage(Utils.chat(config.getKarmaCantBeDisabled()));
                    return true;
                }
            }
        }
        if (forceChance){
            JudgedPlayersManager.getInstance().setPlayerJudged(p, forceChangeTo);
        } else {
            JudgedPlayersManager.getInstance().togglePlayerJudged(p);
        }
        boolean isKarmaEnabled = JudgedPlayersManager.getInstance().isPlayerJudged(p);
        sender.sendMessage(Utils.chat((isKarmaEnabled) ? config.getKarmaEnabledMessage().replace("{player}", p.getName()) : config.getKarmaDisabledMessage().replace("{player}", p.getName())));
        return true;
    }

    @Override
    public String[] getRequiredPermission() {
        return new String[]{"pdm.managekarma", "pdm.toggleownkarma"};
    }

    @Override
    public String getFailureMessage() {
        return Utils.chat("&c/pdm togglekarma <player> <true/false>");
    }

    @Override
    public String[] getHelpEntry() {
        return new String[]{
                Utils.chat("&8&m                                             "),
                Utils.chat("&e/pdm togglekarma <player> <true/false>"),
                Utils.chat("&7" + config.getToggleKarmaCommandDescription()),
                Utils.chat("&7>" + config.getTranslationPermissions() + " &epdm.managekarma | pdm.toggleownkarma")
        };
    }

    @Override
    public List<String> getSubcommandArgs(CommandSender sender, String[] args) {
        if (args.length == 3){
            return Arrays.asList("true", "false");
        }
        return null;
    }
}
