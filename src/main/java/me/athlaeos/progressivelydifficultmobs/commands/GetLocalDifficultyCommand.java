package me.athlaeos.progressivelydifficultmobs.commands;

import me.athlaeos.progressivelydifficultmobs.main.Main;
import me.athlaeos.progressivelydifficultmobs.managers.PlayerKarmaManager;
import me.athlaeos.progressivelydifficultmobs.managers.PluginConfigurationManager;
import me.athlaeos.progressivelydifficultmobs.utils.Utils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class GetLocalDifficultyCommand implements Command{

    private final PlayerKarmaManager karmaManager;
    private final PluginConfigurationManager config;

    public GetLocalDifficultyCommand(){
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

        int localizedLevel = karmaManager.getKarmaLevel(karmaManager.getAverageKarmaSurroundingPlayers(p.getLocation()));

        sender.sendMessage(Utils.chat(config.getLocalDifficultyMessage().replace("{level}", "" + localizedLevel)));
        return true;
    }

    @Override
    public String[] getRequiredPermission() {
            return new String[]{"pdm.getlocaldifficulty"};
    }

    @Override
    public String getFailureMessage() {
        return Utils.chat("&c/pdm localdifficulty");
    }

    @Override
    public String[] getHelpEntry() {
        return new String[]{
                Utils.chat("&8&m                                             "),
                Utils.chat("&e/pdm localdifficulty"),
                Utils.chat("&7" + config.getLocalDifficultyCommandDescription()),
                Utils.chat("&7>" + config.getTranslationPermissions() + " &epdm.getlocaldifficulty")
        };
    }

    @Override
    public List<String> getSubcommandArgs(CommandSender sender, String[] args) {
        return null;
    }
}
