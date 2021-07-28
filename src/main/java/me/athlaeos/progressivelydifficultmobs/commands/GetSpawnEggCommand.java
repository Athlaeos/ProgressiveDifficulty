package me.athlaeos.progressivelydifficultmobs.commands;

import me.athlaeos.progressivelydifficultmobs.main.Main;
import me.athlaeos.progressivelydifficultmobs.managers.CompatibleEntityManager;
import me.athlaeos.progressivelydifficultmobs.managers.LeveledMonsterManager;
import me.athlaeos.progressivelydifficultmobs.managers.PluginConfigurationManager;
import me.athlaeos.progressivelydifficultmobs.pojo.LeveledMonster;
import me.athlaeos.progressivelydifficultmobs.utils.Utils;
import org.apache.commons.lang.WordUtils;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GetSpawnEggCommand implements Command{
    private NamespacedKey eggMetaKey = new NamespacedKey(Main.getInstance(), "pdm-custom_egg");

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        PluginConfigurationManager config = PluginConfigurationManager.getInstance();
        if (args.length > 2){
            Player p;
            int amount = 1;
            if (args.length >= 4){
                p = Main.getInstance().getServer().getPlayer(args[3]);
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
            if (args.length == 5){
                try {
                    amount = Integer.parseInt(args[4]);
                } catch (IllegalArgumentException e){
                    sender.sendMessage(Utils.chat(config.getInvalidNumberError()));
                    return true;
                }
            }
            if (args[1].equalsIgnoreCase("vanilla")){
                EntityType chosenMonster;
                try {
                    chosenMonster = EntityType.valueOf(args[2]);
                    if (!CompatibleEntityManager.getInstance().getAllMobIcons().containsKey(chosenMonster)){
                        sender.sendMessage(Utils.chat(config.getGetSpawnEggInvalidNameError()));
                        return true;
                    }
                } catch (IllegalArgumentException e){
                    sender.sendMessage(Utils.chat(config.getGetSpawnEggInvalidNameError()));
                    return true;
                }
                ItemStack egg = Utils.createItemStack(
                        Material.SKELETON_SPAWN_EGG,
                        Utils.chat("&fSpawn vanilla " + WordUtils.capitalize(args[2].toLowerCase().replace("_", " "))),
                        null);
                egg.setAmount(amount);
                ItemMeta eggMeta = egg.getItemMeta();
                eggMeta.getPersistentDataContainer().set(eggMetaKey, PersistentDataType.STRING, args[2]);
                egg.setItemMeta(eggMeta);
                p.getInventory().addItem(egg);
                sender.sendMessage(Utils.chat(config.getGetItemMessage()));
                return true;
            } else if (args[1].equalsIgnoreCase("custom")){
                LeveledMonster chosenMonster = LeveledMonsterManager.getInstance().getMonster(args[2]);
                if (chosenMonster != null){
                    ItemStack egg = Utils.createItemStack(
                            Material.WITHER_SKELETON_SPAWN_EGG,
                            Utils.chat("&fSpawn custom " + args[2]),
                            null);
                    egg.setAmount(amount);
                    ItemMeta eggMeta = egg.getItemMeta();
                    eggMeta.getPersistentDataContainer().set(eggMetaKey, PersistentDataType.STRING, args[2]);
                    egg.setItemMeta(eggMeta);
                    p.getInventory().addItem(egg);
                    sender.sendMessage(Utils.chat(config.getGetItemMessage()));
                    return true;
                } else {
                    sender.sendMessage(Utils.chat(config.getGetSpawnEggInvalidNameError()));
                    return true;
                }
            } else {
                return false;
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
        return Utils.chat("&c/pdm getspawnegg [vanilla/custom] [type/name] <player> <amount>");
    }

    @Override
    public String[] getHelpEntry() {
        return new String[]{
                Utils.chat("&8&m                                             "),
                Utils.chat("&e/pdm getspawnegg [vanilla/custom] [type/name] <player> <amount>"),
                Utils.chat("&7" + PluginConfigurationManager.getInstance().getGetSpawnEggCommandDescription()),
                Utils.chat("&7>" + PluginConfigurationManager.getInstance().getTranslationPermissions() + " &epdm.giveitems")
        };
    }

    @Override
    public List<String> getSubcommandArgs(CommandSender sender, String[] args) {
        if (args.length == 2){
            return Arrays.asList("vanilla", "custom");
        } else if (args.length == 3){
            if (args[1].equalsIgnoreCase("vanilla")){
                List<String> mobs = new ArrayList<>();
                for (EntityType type : CompatibleEntityManager.getInstance().getHostileMobIcons().keySet()){
                    mobs.add(type.toString());
                }
                return mobs;
            } else if (args[1].equalsIgnoreCase("custom")){
                List<String> mobs = new ArrayList<>();
                for (LeveledMonster monster : LeveledMonsterManager.getInstance().getAllMonsters()){
                    mobs.add(monster.getName());
                }
                return mobs;
            }
        }
        return null;
    }
}
