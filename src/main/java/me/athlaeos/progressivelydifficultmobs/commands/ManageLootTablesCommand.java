package me.athlaeos.progressivelydifficultmobs.commands;

import me.athlaeos.progressivelydifficultmobs.managers.PlayerMenuUtilManager;
import me.athlaeos.progressivelydifficultmobs.managers.PluginConfigurationManager;
import me.athlaeos.progressivelydifficultmobs.menus.LootTableOverviewMenu;
import me.athlaeos.progressivelydifficultmobs.utils.Utils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class ManageLootTablesCommand implements Command{

    private final PluginConfigurationManager config;

    public ManageLootTablesCommand(){
        config = PluginConfigurationManager.getInstance();
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)){
            sender.sendMessage(Utils.chat(config.getErrorNoPermission()));
            return true;
        }
        Player p = (Player) sender;

        new LootTableOverviewMenu(PlayerMenuUtilManager.getInstance().getPlayerMenuUtility(p)).open();

        return true;
    }

    @Override
    public String[] getRequiredPermission() {
        return new String[]{"pdm.manageloottables"};
    }

    @Override
    public String getFailureMessage() {
        return Utils.chat("&c/pdm loottables");
    }

    @Override
    public String[] getHelpEntry() {
        return new String[]{
                Utils.chat("&8&m                                             "),
                Utils.chat("&e/pdm loottables"),
                Utils.chat("&7" + config.getManageLootTablesCommandDescription()),
                Utils.chat("&7>" + config.getTranslationPermissions() + " &epdm.manageloottables")
        };
    }

    @Override
    public List<String> getSubcommandArgs(CommandSender sender, String[] args) {
        return null;
    }
}
