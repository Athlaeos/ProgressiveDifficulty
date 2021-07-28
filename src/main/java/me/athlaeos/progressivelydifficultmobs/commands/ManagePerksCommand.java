package me.athlaeos.progressivelydifficultmobs.commands;

import me.athlaeos.progressivelydifficultmobs.main.Main;
import me.athlaeos.progressivelydifficultmobs.managers.PlayerMenuUtilManager;
import me.athlaeos.progressivelydifficultmobs.managers.PlayerPerksManager;
import me.athlaeos.progressivelydifficultmobs.managers.PluginConfigurationManager;
import me.athlaeos.progressivelydifficultmobs.menus.LevelPerkModificationMenu;
import me.athlaeos.progressivelydifficultmobs.perks.Perk;
import me.athlaeos.progressivelydifficultmobs.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class ManagePerksCommand implements Command{

    private final PluginConfigurationManager config;
    private final PlayerPerksManager perkManager;

    public ManagePerksCommand(){
        this.config = PluginConfigurationManager.getInstance();
        this.perkManager = PlayerPerksManager.getInstance();
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        if (args.length <= 1){
            if (sender instanceof Player){
                Player p = (Player) sender;
                new LevelPerkModificationMenu(PlayerMenuUtilManager.getInstance().getPlayerMenuUtility(p)).open();
            } else {
                sender.sendMessage(Utils.chat(config.getErrorNoPermission()));
                return true;
            }
        } else {
            Player p;
            if (args.length >= 3){
                if (sender.hasPermission("pdm.manageperks")){
                    if (Main.getInstance().getServer().getPlayer(args[1]) != null) {
                        p = Main.getInstance().getServer().getPlayer(args[1]);
                    } else {
                        sender.sendMessage(Utils.chat(config.getPlayerNotFoundError()));
                        return true;
                    }
                } else {
                    sender.sendMessage(Utils.chat(config.getErrorNoPermission()));
                    return true;
                }
            } else {
                return false;
            }
            if (p == null){
                sender.sendMessage(Utils.chat(config.getPlayerNotFoundError()));
                return true;
            }
            if (args[2].equalsIgnoreCase("remove")){
                if (args.length > 3){
                    if (sender.hasPermission("pdm.manageperks")){
                        if (perkManager.getPlayersPerks(p).contains(perkManager.getPerkByName(args[3]))){
                            perkManager.removePlayerPerk(p, perkManager.getPerkByName(args[3]).getId());
                            sender.sendMessage(Utils.chat(config.getRemovePerkSuccessMessage()));
                        } else {
                            sender.sendMessage(Utils.chat(config.getRemovePerkErrorMessage()));
                        }
                    } else {
                        sender.sendMessage(Utils.chat(config.getErrorNoPermission()));
                        return true;
                    }
                }
            } else if (args[2].equalsIgnoreCase("add")){
                if (args.length > 3){
                    if (sender.hasPermission("pdm.manageperks")) {
                        if (!perkManager.getPlayersPerks(p).contains(perkManager.getPerkByName(args[3]))) {
                            perkManager.addPlayerPerk(p, perkManager.getPerkByName(args[3]).getId());
                            sender.sendMessage(Utils.chat(config.getAddPerkSuccessMessage()));
                        } else {
                            sender.sendMessage(Utils.chat(config.getAddPerkErrorMessage()));
                        }
                    } else {
                        sender.sendMessage(Utils.chat(config.getErrorNoPermission()));
                        return true;
                    }
                }
            } else if (args[2].equalsIgnoreCase("get")){
                if (sender.hasPermission("pdm.manageperks") || sender.hasPermission("pdm.getownperks")){
                    sender.sendMessage(Utils.chat(config.getGetPerksMessage().replace("{player}", p.getName())));
                    for (Perk perk : perkManager.getPlayersTotalPerks(p)){
                        sender.sendMessage(Utils.chat(config.getGetPerksSyntax().replace("{perk}", perk.getDisplayName())));
                    }
                }
            }
        }
        return true;
    }

    @Override
    public String[] getRequiredPermission() {
        return new String[]{"pdm.manageperks", "pdm.getownperks"};
    }

    @Override
    public String getFailureMessage() {
        return Utils.chat("&c/pdm perks <player> <add/remove/get> <perk>");
    }

    @Override
    public String[] getHelpEntry() {
        return new String[]{
                Utils.chat("&8&m                                             "),
                Utils.chat("&e/pdm perks <player> <add/remove/get> <perk>"),
                Utils.chat("&7" + config.getPerksCommandDescription()),
                Utils.chat("&7>" + config.getTranslationPermissions() + " &epdm.manageperks | pdm.getownperks")
        };
    }

    @Override
    public List<String> getSubcommandArgs(CommandSender sender, String[] args) {
        List<String> options = new ArrayList<>();
        if (args.length == 2){
            return null;
        }
        if (args.length == 3){
            options.add("add");
            options.add("remove");
            options.add("get");
        }
        if (args.length == 4){
            Player p;
            if (Bukkit.getPlayer(args[1]) != null) {
                p = Bukkit.getPlayer(args[1]);
            } else {
                options.add("player_not_found");
                return options;
            }
            assert p != null;
            List<Perk> playerPerks = perkManager.getPlayersPerks(p);
            List<Perk> allPerks = new ArrayList<>(perkManager.getAllPerks().values());
            if (args[2].equalsIgnoreCase("remove")){
                for (Perk perk : playerPerks){
                    if (perk.getName() != null){
                        options.add(perk.getName());
                    }
                }
                if (options.size() == 0){
                    options.add("none_available");
                }
            } else if (args[2].equalsIgnoreCase("add")){
                for (Perk perk : allPerks){
                    if (!playerPerks.contains(perk)){
                        if (perk.getName() != null){
                            options.add(perk.getName());
                        }
                    }
                }
                if (options.size() == 0){
                    options.add("none_available");
                }
            }
        }
        return options;
    }
}
