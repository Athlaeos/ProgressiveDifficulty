package me.athlaeos.progressivelydifficultmobs.commands;

import me.athlaeos.progressivelydifficultmobs.managers.LootTableManager;
import me.athlaeos.progressivelydifficultmobs.managers.PlayerMenuUtilManager;
import me.athlaeos.progressivelydifficultmobs.managers.PluginConfigurationManager;
import me.athlaeos.progressivelydifficultmobs.menus.LootTableOverviewMenu;
import me.athlaeos.progressivelydifficultmobs.persistence.LootTablePersister;
import me.athlaeos.progressivelydifficultmobs.pojo.LootTable;
import me.athlaeos.progressivelydifficultmobs.utils.Utils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AddLootTableCommand implements Command{

    private final LootTableManager manager;
    private final PluginConfigurationManager config;

    public AddLootTableCommand(){
        config = PluginConfigurationManager.getInstance();
        manager = LootTableManager.getInstance();
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)){
            sender.sendMessage(Utils.chat(config.getErrorNoPermission()));
            return true;
        }

        if (args.length == 2 || args.length == 3){
            String name = ChatColor.stripColor(Utils.chat(args[1]));
            if (manager.getLootTableByName(name) != null){
                sender.sendMessage(Utils.chat(config.getCreateLootTableInvalidNameError()));
                return true;
            }
            if (args.length == 3){
                Material icon = Material.getMaterial(args[2]);
                if (icon == null){
                    sender.sendMessage(Utils.chat(config.getCreateLootTableInvalidIconError()));
                    return true;
                }
                manager.registerLootTable(new LootTable(name, new ArrayList<>(), icon));
            } else {
                manager.registerLootTable(new LootTable(name, new ArrayList<>(), Material.BOOK));
            }
            PlayerMenuUtilManager.getInstance().getPlayerMenuUtility((Player) sender).setLootTable(LootTableManager.getInstance().getLootTableByName(name));
            new LootTableOverviewMenu(PlayerMenuUtilManager.getInstance().getPlayerMenuUtility((Player) sender)).open();
            LootTablePersister.saveLootTables();
            return true;
        }
        return false;
    }

    @Override
    public String[] getRequiredPermission() {
        return new String[]{"pdm.manageloottables"};
    }

    @Override
    public String getFailureMessage() {
        return Utils.chat("&c/pdm newloottable [name] <icon>");
    }

    @Override
    public String[] getHelpEntry() {
        return new String[]{
                Utils.chat("&8&m                                             "),
                Utils.chat("&e/pdm newloottable [name] <icon>"),
                Utils.chat("&7" + config.getCreateLootTableCommandDescription()),
                Utils.chat("&7>" + config.getTranslationPermissions() + " &epdm.manageloottables")
        };
    }

    @Override
    public List<String> getSubcommandArgs(CommandSender sender, String[] args) {
        if (args.length == 2){
            return Arrays.asList("name");
        }
        if (args.length == 3){
            List<String> materialList = new ArrayList<>();
            for (Material m : Material.values()){
                materialList.add(m.name());
            }
            return materialList;
        }
        return null;
    }
}
