package me.athlaeos.progressivelydifficultmobs.commands;

import me.athlaeos.progressivelydifficultmobs.managers.CompatibleEntityManager;
import me.athlaeos.progressivelydifficultmobs.managers.LeveledMonsterManager;
import me.athlaeos.progressivelydifficultmobs.managers.PlayerMenuUtilManager;
import me.athlaeos.progressivelydifficultmobs.managers.PluginConfigurationManager;
import me.athlaeos.progressivelydifficultmobs.menus.EditLeveledMonsterMenu;
import me.athlaeos.progressivelydifficultmobs.pojo.LeveledMonster;
import me.athlaeos.progressivelydifficultmobs.utils.Utils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CreateNewMonsterCommand implements Command{
    private final CompatibleEntityManager entityManager;
    private final LeveledMonsterManager monsterManager;
    private final PlayerMenuUtilManager utilManager;
    private final PluginConfigurationManager config;

    public CreateNewMonsterCommand(){
        config = PluginConfigurationManager.getInstance();
        utilManager = PlayerMenuUtilManager.getInstance();
        monsterManager = LeveledMonsterManager.getInstance();
        entityManager = CompatibleEntityManager.getInstance();
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)){
            sender.sendMessage(Utils.chat(config.getErrorNoPermission()));
            return true;
        }
        Player p = (Player) sender;
        if (args.length >= 4){
            String name = args[1];
            String displayName = null;
            int level;
            boolean spawnGlobal = false;
            try {
                level = Integer.parseInt(args[3]);
            } catch (NumberFormatException e){
                if (args[3].equalsIgnoreCase("global")){
                    spawnGlobal = true;
                    level = 0;
                } else {
                    sender.sendMessage(Utils.chat(config.getInvalidNumberError()));
                    return true;
                }
            }

            if (args.length >= 5){
                displayName = String.join(" ", Arrays.copyOfRange(args, 4, args.length));
            }

            EntityType type;
            try {
                type = EntityType.valueOf(args[2]);
                if (!entityManager.getHostileMobIcons().containsKey(type)){
                    throw new IllegalArgumentException();
                }
            } catch (IllegalArgumentException e){
                sender.sendMessage(Utils.chat(config.getCreateMonsterInvalidMonsterError()));
                return true;
            }

            if (monsterManager.getMonster(name) != null){
                sender.sendMessage(Utils.chat(config.getCreateMonsterInvalidNameError()));
                return true;
            }

            utilManager.getPlayerMenuUtility(p).setCurrentMonster(
                    new LeveledMonster(
                            level,
                            0,
                            name,
                            type,
                            false,
                            (displayName == null) ? null : Utils.chat(displayName),
                            false,
                            new ArrayList<>(),
                            new ArrayList<>(),
                            true,
                            0D,
                            5,
                            20D,
                            false,
                            null,
                            null,
                            null,
                            null,
                            null,
                            null,
                            new ArrayList<>(),
                            new ArrayList<>(),
                            new ArrayList<>(),
                            0,
                            255,
                            0D,
                            0D,
                            0D,
                            0D,
                            0D,
                            0D,
                            spawnGlobal));

            new EditLeveledMonsterMenu(PlayerMenuUtilManager.getInstance().getPlayerMenuUtility(p), false).open();
            sender.sendMessage(Utils.chat(config.getCreateMonsterMessage()));
            return true;
        }
        return false;
    }

    @Override
    public String[] getRequiredPermission() {
        return new String[]{"pdm.managemobs"};
    }

    @Override
    public String getFailureMessage() {
        return "&c/pdm newmonster [name] [entity type] [level/global] <displayname>";
    }

    @Override
    public String[] getHelpEntry() {
        return new String[]{
                Utils.chat("&8&m                                             "),
                Utils.chat("&e/pdm newmonster [name] [entity type] [level/global] <displayname>"),
                Utils.chat("&7" + config.getCreateMonsterCommandDescription()),
                Utils.chat("&7>" + config.getTranslationPermissions() + " &epdm.managemobs")
        };
    }

    @Override
    public List<String> getSubcommandArgs(CommandSender sender, String[] args) {
        if (args.length == 2){
            return Arrays.asList("name");
        }
        if (args.length == 3){
            List<String> types = new ArrayList<>();
            for (EntityType t : entityManager.getHostileMobIcons().keySet()){
                types.add(t.toString());
            }
            return types;
        }
        if (args.length == 4) {
            List<String> returnArray = new ArrayList<>();
            for (int i = -10; i < 11; i++){
                returnArray.add("" + i);
            }
            returnArray.add("global");
            return returnArray;
        }
        if (args.length == 5){
            return Arrays.asList("displayname");
        }
        return null;
    }
}
