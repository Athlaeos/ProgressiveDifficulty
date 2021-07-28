package me.athlaeos.progressivelydifficultmobs.commands;

import me.athlaeos.progressivelydifficultmobs.main.Main;
import me.athlaeos.progressivelydifficultmobs.managers.PlayerKarmaManager;
import me.athlaeos.progressivelydifficultmobs.managers.PluginConfigurationManager;
import me.athlaeos.progressivelydifficultmobs.states.KarmaGainReason;
import me.athlaeos.progressivelydifficultmobs.utils.Utils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class AddKarmaCommand implements Command{

    private final PlayerKarmaManager karmaManager;
    private final PluginConfigurationManager config;

    public AddKarmaCommand(){
        config = PluginConfigurationManager.getInstance();
        karmaManager = PlayerKarmaManager.getInstance();
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        if (args.length > 1){
            boolean applyMitigations = false;
            boolean ignoreMultiplier = true;
            if (args.length >= 4){
                applyMitigations = Boolean.parseBoolean(args[3]);
            }
            if (args.length >= 5){
                ignoreMultiplier = Boolean.parseBoolean(args[4]);
            }
            Player p = null;
            if (sender instanceof Player){
                p = (Player) sender;
            }
            if (args.length >= 3){
                if (sender.hasPermission("pdm.managekarma")){
                    if (Main.getInstance().getServer().getPlayer(args[2]) != null) {
                        p = Main.getInstance().getServer().getPlayer(args[2]);
                    } else {
                        sender.sendMessage(Utils.chat(config.getPlayerNotFoundError()));
                        return true;
                    }
                } else {
                    sender.sendMessage(Utils.chat(config.getErrorNoPermission()));
                    return true;
                }
            }
            double addedKarma = 0.0;
            try {
                addedKarma = Double.parseDouble(args[1]);
            } catch (Exception e){
                sender.sendMessage(Utils.chat(config.getInvalidNumberError()));
                return true;
            }
            if (p != null){
                karmaManager.addKarma(p, addedKarma,
                        KarmaGainReason.COMMAND, applyMitigations, ignoreMultiplier);
                sender.sendMessage(Utils.chat(config.getAddKarmaMessage()
                        .replace("{player}", p.getName())
                        .replace("{amount}", "" + addedKarma)
                        .replace("{newamount}", String.format("%.2f", karmaManager.getKarma(p.getUniqueId())))));
            } else {
                sender.sendMessage(Utils.chat(config.getPlayerNotFoundError()));
            }
            return true;
        }
        return false;
    }

    @Override
    public String[] getRequiredPermission() {
        return new String[]{"pdm.managekarma", "pdm.setownkarma"};
    }

    @Override
    public String getFailureMessage() {
        return Utils.chat("&c/pdm addkarma [amount] <player> <withMitigations> <ignoreMultiplier>");
    }

    @Override
    public String[] getHelpEntry() {
        return new String[]{
                Utils.chat("&8&m                                             "),
                Utils.chat("&e/pdm addkarma [amount] <player> <withMitigations> <ignoreMultiplier>"),
                Utils.chat("&7" + config.getAddKarmaDescription()),
                Utils.chat("&7>" + config.getTranslationPermissions() + " &epdm.managekarma | pdm.setownkarma")
        };
    }

    @Override
    public List<String> getSubcommandArgs(CommandSender sender, String[] args) {
        return null;
    }
}
