package me.athlaeos.progressivelydifficultmobs.commands;

import me.athlaeos.progressivelydifficultmobs.managers.LeveledMonsterManager;
import me.athlaeos.progressivelydifficultmobs.managers.PlayerMenuUtilManager;
import me.athlaeos.progressivelydifficultmobs.managers.PluginConfigurationManager;
import me.athlaeos.progressivelydifficultmobs.menus.LeveledMonsterOverviewMenu;
import me.athlaeos.progressivelydifficultmobs.menus.LootTableOverviewMenu;
import me.athlaeos.progressivelydifficultmobs.utils.Utils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class ManageLeveledMonstersCommand implements Command{

    private final PluginConfigurationManager config;

    public ManageLeveledMonstersCommand(){
        config = PluginConfigurationManager.getInstance();
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)){
            sender.sendMessage(Utils.chat(config.getErrorNoPermission()));
            return true;
        }

        Player p = (Player) sender;
        PlayerMenuUtilManager.getInstance().getPlayerMenuUtility(p).setSelectedMonster(null);
        PlayerMenuUtilManager.getInstance().getPlayerMenuUtility(p).setSelectedLevel(0);
        new LeveledMonsterOverviewMenu(PlayerMenuUtilManager.getInstance().getPlayerMenuUtility(p), null, 0, false).open();

        return true;
    }

    @Override
    public String[] getRequiredPermission() {
        return new String[]{"pdm.managemobs"};
    }

    @Override
    public String getFailureMessage() {
        return Utils.chat("&c/pdm monsters");
    }

    @Override
    public String[] getHelpEntry() {
        return new String[]{
                Utils.chat("&8&m                                             "),
                Utils.chat("&e/pdm monsters"),
                Utils.chat("&7" + config.getManageMonstersCommandDescription()),
                Utils.chat("&7>" + config.getTranslationPermissions() + " &epdm.managemobs")
        };
    }

    @Override
    public List<String> getSubcommandArgs(CommandSender sender, String[] args) {
        return null;
    }
}
