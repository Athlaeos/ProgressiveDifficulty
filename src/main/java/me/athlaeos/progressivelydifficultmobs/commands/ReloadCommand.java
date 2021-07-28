package me.athlaeos.progressivelydifficultmobs.commands;

import me.athlaeos.progressivelydifficultmobs.listeners.BlockBreakListener;
import me.athlaeos.progressivelydifficultmobs.listeners.BlockPlaceListener;
import me.athlaeos.progressivelydifficultmobs.main.Main;
import me.athlaeos.progressivelydifficultmobs.managers.*;
import me.athlaeos.progressivelydifficultmobs.persistence.AbilityPersister;
import me.athlaeos.progressivelydifficultmobs.persistence.LeveledMonsterPersister;
import me.athlaeos.progressivelydifficultmobs.persistence.LootTablePersister;
import me.athlaeos.progressivelydifficultmobs.utils.Utils;
import org.bukkit.command.CommandSender;

import java.util.List;

public class ReloadCommand implements Command{

    private final PluginConfigurationManager config;

    public ReloadCommand(){
        config = PluginConfigurationManager.getInstance();
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        if (!LeveledMonsterManager.getInstance().isEnabled()){
            return true;
        }
        sender.sendMessage(Utils.chat(config.getPluginReloadedMessage()));
        for (String config : ConfigManager.getInstance().getConfigs().keySet()){
            ConfigManager.getInstance().getConfigs().get(config).reload();
        }

        PluginConfigurationManager.getInstance().reload();

        LeveledMonsterManager.getInstance().disableMonsters();
        LeveledMonsterManager.getInstance().getAllMonsters().clear();
        LootTableManager.getInstance().getAllLootTables().clear();
        PlayerKarmaManager.getInstance().reload();
        PlayerCurseManager.getInstance().reload();
        ActiveItemManager.getInstance().reload();
        BlockPlaceListener.getInstance().loadConfig();
        BlockBreakListener.getInstance().loadConfig();
        AbilityManager.getInstance().reload();
        PlayerPerksManager.getInstance().reload();

        LeveledMonsterPersister.loadMonsters();
        LootTablePersister.loadLootTables();
        AbilityPersister.loadAbilities();

        new CommandManager(Main.getInstance());
        return true;
    }

    @Override
    public String[] getRequiredPermission() {
        return new String[]{"pdm.reload"};
    }

    @Override
    public String getFailureMessage() {
        return Utils.chat("&c/pdm reload");
    }

    @Override
    public String[] getHelpEntry() {
        return new String[]{
                Utils.chat("&8&m                                             "),
                Utils.chat("&e/pdm reload"),
                Utils.chat("&7" + config.getCreateLootTableCommandDescription()),
                Utils.chat("&7>" + config.getTranslationPermissions() + " &epdm.reload")
        };
    }

    @Override
    public List<String> getSubcommandArgs(CommandSender sender, String[] args) {
        return null;
    }
}
