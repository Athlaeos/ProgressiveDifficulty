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

public class ChangeMonsterDisplayNameCommand implements Command{
    private final LeveledMonsterManager monsterManager;
    private final PlayerMenuUtilManager utilManager;
    private final PluginConfigurationManager config;

    public ChangeMonsterDisplayNameCommand(){
        config = PluginConfigurationManager.getInstance();
        utilManager = PlayerMenuUtilManager.getInstance();
        monsterManager = LeveledMonsterManager.getInstance();
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        if (args.length >= 2){
            String name = args[1];
            String displayName = null;

            if (args.length >= 3){
                displayName = String.join(" ", Arrays.copyOfRange(args, 2, args.length));
            }

            LeveledMonster monster = monsterManager.getMonster(name);

            if (monster == null){
                sender.sendMessage(Utils.chat(config.getCreateMonsterInvalidNameError()));
                return true;
            }

            monster.setDisplayName(Utils.chat(displayName));
            monsterManager.updateMonster(monster);
            if (displayName == null){
                sender.sendMessage(Utils.chat("&aRemoved nickname"));
            } else {
                sender.sendMessage(Utils.chat("&aName changed to " + displayName));
            }
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
        return "&c/pdm changenick [name] <displayname>";
    }

    @Override
    public String[] getHelpEntry() {
        return new String[]{
                Utils.chat("&8&m                                             "),
                Utils.chat("&e/pdm changenick [name] <displayname>"),
                Utils.chat("&7" + config.getChangeNickCommandDescription()),
                Utils.chat("&7>" + config.getTranslationPermissions() + " &epdm.managemobs")
        };
    }

    @Override
    public List<String> getSubcommandArgs(CommandSender sender, String[] args) {
        if (args.length == 2) {
            List<String> returnArray = new ArrayList<>();
            for (LeveledMonster monster : monsterManager.getAllMonsters()){
                returnArray.add(monster.getName());
            }
            return returnArray;
        }
        return null;
    }
}
