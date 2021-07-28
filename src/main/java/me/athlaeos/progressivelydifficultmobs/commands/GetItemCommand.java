package me.athlaeos.progressivelydifficultmobs.commands;

import me.athlaeos.progressivelydifficultmobs.main.Main;
import me.athlaeos.progressivelydifficultmobs.managers.ActiveItemManager;
import me.athlaeos.progressivelydifficultmobs.managers.PluginConfigurationManager;
import me.athlaeos.progressivelydifficultmobs.utils.Utils;
import org.bukkit.NamespacedKey;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class GetItemCommand implements Command{

    private final ActiveItemManager activeItemManager;
    private final PluginConfigurationManager config;

    public GetItemCommand(){
        config = PluginConfigurationManager.getInstance();
        activeItemManager = ActiveItemManager.getInstance();
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        if (args.length > 1){
            Player p;
            int amount = 1;
            if (args.length >= 3){
                p = Main.getInstance().getServer().getPlayer(args[2]);
                if (p == null){
                    sender.sendMessage(Utils.chat(config.getPlayerNotFoundError()));
                    return true;
                }
            } else {
                if (!(sender instanceof Player)){
                    sender.sendMessage(Utils.chat(config.getErrorNoPermission()));
                    return true;
                }
                p = (Player) sender;
            }
            if (args.length == 4){
                try {
                    amount = Integer.parseInt(args[3]);
                } catch (IllegalArgumentException e){
                    sender.sendMessage(Utils.chat(config.getInvalidNumberError()));
                    return true;
                }
            }
            for (NamespacedKey key : activeItemManager.getAllActiveItems().keySet()){
                if (args[1].equalsIgnoreCase(key.getKey())){
                    ItemStack itemGiven = activeItemManager.getAllActiveItems().get(key).getItem();
                    itemGiven.setAmount(amount);
                    p.getInventory().addItem(itemGiven);
                    sender.sendMessage(Utils.chat(config.getGetItemMessage()));
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public String[] getRequiredPermission() {
        return new String[]{"pdm.giveitems"};
    }

    @Override
    public String getFailureMessage() {
        return Utils.chat("&c/pdm getitem [item key]");
    }

    @Override
    public String[] getHelpEntry() {
        return new String[]{
                Utils.chat("&8&m                                             "),
                Utils.chat("&e/pdm getitem [item key] <player> <amount>"),
                Utils.chat("&7" + config.getGetItemCommandDescription()),
                Utils.chat("&7>" + config.getTranslationPermissions() + " &epdm.giveitems")
        };
    }

    @Override
    public List<String> getSubcommandArgs(CommandSender sender, String[] args) {
        if (args.length == 2) {
            List<String> items = new ArrayList<>();
            for (NamespacedKey key : activeItemManager.getAllActiveItems().keySet()) {
                items.add(key.getKey());
            }
            return items;
        }
        return null;
    }
}
