package me.athlaeos.progressivelydifficultmobs.commands;

import me.athlaeos.progressivelydifficultmobs.main.Main;
import me.athlaeos.progressivelydifficultmobs.managers.CooldownManager;
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
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ManageCooldownsCommand implements Command{

    private final PluginConfigurationManager config;
    private final CooldownManager cooldownManager;

    public ManageCooldownsCommand(){
        this.config = PluginConfigurationManager.getInstance();
        this.cooldownManager = CooldownManager.getInstance();
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        if (args.length > 3){
            Player p;
            if (Main.getInstance().getServer().getPlayer(args[1]) != null) {
                p = Main.getInstance().getServer().getPlayer(args[1]);
                if (sender instanceof Player){
                    Player sendr = (Player) sender;
                    if (!p.getName().equals(sendr.getName())){
                        if (!sendr.hasPermission("pdm.managecooldowns")){
                            sender.sendMessage(Utils.chat(config.getErrorNoPermission()));
                            return true;
                        }
                    }
                }
            } else {
                sender.sendMessage(Utils.chat(config.getPlayerNotFoundError()));
                return true;
            }
            if (p == null){
                sender.sendMessage(Utils.chat(config.getPlayerNotFoundError()));
                return true;
            }
            if (args[2].equalsIgnoreCase("set")){
                if (args.length > 4){
                    if (sender.hasPermission("pdm.managecooldowns")) {
                        if (cooldownManager.getAllCooldowns().keySet().contains(args[3])){
                            int newCooldown;
                            try {
                                newCooldown = Integer.parseInt(args[4]);
                            } catch (IllegalArgumentException e){
                                sender.sendMessage(Utils.chat(config.getInvalidNumberError()));
                                return true;
                            }
                            cooldownManager.setCooldown(p, newCooldown, args[3]);
                            sender.sendMessage(Utils.chat(config.getGetCooldownMessage()
                                    .replace("{player}", p.getName())
                                    .replace("{key}", args[3])
                                    .replace("{cooldown}", Utils.msToTimestamp(cooldownManager.getCooldown(p, args[3])))));
                        } else {
                            sender.sendMessage(Utils.chat(config.getInvalidCooldownKeyError()));
                        }
                        return true;
                    } else {
                        sender.sendMessage(Utils.chat(config.getErrorNoPermission()));
                        return true;
                    }
                }
            } else if (args[2].equalsIgnoreCase("get")){
                if (sender.hasPermission("pdm.managecooldowns") || sender.hasPermission("pdm.getowncooldowns")){
                    if (cooldownManager.getAllCooldowns().keySet().contains(args[3])){
                        sender.sendMessage(Utils.chat(config.getGetCooldownMessage()
                                .replace("{player}", p.getName())
                                .replace("{key}", args[3])
                                .replace("{cooldown}", Utils.msToTimestamp(cooldownManager.getCooldown(p, args[3])))));
                    } else {
                        sender.sendMessage(Utils.chat(config.getInvalidCooldownKeyError()));
                    }
                    return true;
                }
            }
        }
        return true;
    }

    @Override
    public String[] getRequiredPermission() {
        return new String[]{"pdm.managecooldowns", "pdm.getowncooldowns"};
    }

    @Override
    public String getFailureMessage() {
        return Utils.chat("&c/pdm cooldowns <player> <get/set> <key> <cooldown (ms)>");
    }

    @Override
    public String[] getHelpEntry() {
        return new String[]{
                Utils.chat("&8&m                                             "),
                Utils.chat("&e/pdm cooldowns <player> <get/set> <key> <cooldown (ms)>"),
                Utils.chat("&7" + config.getCooldownCommandDescription()),
                Utils.chat("&7>" + config.getTranslationPermissions() + " &epdm.managecooldowns | pdm.getowncooldowns")
        };
    }

    @Override
    public List<String> getSubcommandArgs(CommandSender sender, String[] args) {
        List<String> options = new ArrayList<>();
        if (args.length == 2){
            if (sender.hasPermission("pdm.managecooldowns")){
                return null;
            } else {
                if (sender instanceof Player){
                    Player sendr = (Player) sender;
                    return Collections.singletonList(sendr.getName());
                }
            }
        }
        if (args.length == 3){
            options.add("set");
            options.add("get");
        }
        if (args.length == 4){
            return new ArrayList<>(cooldownManager.getAllCooldowns().keySet());
        }
        if (args.length == 5){
            return Collections.singletonList("millis");
        }
        return options;
    }
}
