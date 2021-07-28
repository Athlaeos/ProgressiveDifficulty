package me.athlaeos.progressivelydifficultmobs.commands;

import me.athlaeos.progressivelydifficultmobs.managers.PluginConfigurationManager;
import me.athlaeos.progressivelydifficultmobs.persistence.LeveledMonsterPersister;
import me.athlaeos.progressivelydifficultmobs.persistence.LootTablePersister;
import me.athlaeos.progressivelydifficultmobs.utils.Utils;
import org.bukkit.command.CommandSender;

import java.util.List;

public class SaveChangesCommand implements Command{

    private final PluginConfigurationManager config;

    public SaveChangesCommand(){
        config = PluginConfigurationManager.getInstance();
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        sender.sendMessage(Utils.chat(config.getSaveChangesMessage()));
        LeveledMonsterPersister.saveLeveledMonstersAsynchronously();
        LootTablePersister.saveLootTables();
        return true;
    }

    @Override
    public String[] getRequiredPermission() {
        return new String[]{"pdm.manageloottables", "pdm.managemobs"};
    }

    @Override
    public String getFailureMessage() {
        return Utils.chat("&c/pdm savechanges");
    }

    @Override
    public String[] getHelpEntry() {
        return new String[]{
                Utils.chat("&8&m                                             "),
                Utils.chat("&e/pdm savechanges"),
                Utils.chat("&7" + config.getSaveChangesCommandDescription()),
                Utils.chat("&7>" + config.getTranslationPermissions() + " &epdm.manageloottables | pdm.managemobs")
        };
    }

    @Override
    public List<String> getSubcommandArgs(CommandSender sender, String[] args) {
        return null;
    }
}
