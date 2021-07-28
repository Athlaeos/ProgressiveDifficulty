package me.athlaeos.progressivelydifficultmobs.persistence;

import me.athlaeos.progressivelydifficultmobs.main.Main;
import me.athlaeos.progressivelydifficultmobs.managers.ConfigManager;
import me.athlaeos.progressivelydifficultmobs.managers.LeveledMonsterManager;
import me.athlaeos.progressivelydifficultmobs.managers.LootTableManager;
import me.athlaeos.progressivelydifficultmobs.pojo.Config;
import me.athlaeos.progressivelydifficultmobs.pojo.Drop;
import me.athlaeos.progressivelydifficultmobs.pojo.LootTable;
import me.athlaeos.progressivelydifficultmobs.utils.Utils;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class LootTablePersister {

    public static void deleteLootTable(LootTable table){
        File file = new File(Main.getInstance().getDataFolder() + File.separator + "loottables" + File.separator + table.getName() + ".yml");
        if (file.exists()){
            file.delete();
            System.out.println("File existed and was deleted");
        } else {
            System.out.println("File didn't exist");
        }
    }

    public static void saveLootTables(){
        ConfigManager configManager = ConfigManager.getInstance();
        LootTableManager lootTableManager = LootTableManager.getInstance();
        for (LootTable l : lootTableManager.getAllLootTables()){
            String configPath = "loottables" + File.separator + l.getName() + ".yml";
            YamlConfiguration config = configManager.getConfig(configPath).get();
            config.set("name", l.getName());
            config.set("icon", (l.getIcon() != null) ? l.getIcon().toString() : "BOOK");
            for (int i = 0; i < l.getDrops().size(); i++){
                Drop d = l.getDrops().get(i);
                config.set("drops." + i + ".item", d.getItem());
                config.set("drops." + i + ".drop_chance", d.getDropChance());
                config.set("drops." + i + ".drop_chance_looting", d.getDropChanceLootingBonus());
                config.set("drops." + i + ".min_drops", d.getMinAmountDrop());
                config.set("drops." + i + ".max_drops", d.getMaxAmountDrop());
                config.set("drops." + i + ".min_drops_looting", d.getMinAmountDropLootingBonus());
                config.set("drops." + i + ".max_drops_looting", d.getMaxAmountDropLootingBonus());
            }
            configManager.saveConfig(configPath);
        }
    }

    public static void loadLootTables(){
        ConfigManager configManager = ConfigManager.getInstance();
        LootTableManager lootTableManager = LootTableManager.getInstance();
        new BukkitRunnable(){
            @Override
            public void run() {
                try{
                    File lootTableDirectory = new File(Main.getInstance().getDataFolder(), "loottables");
                    if (!lootTableDirectory.exists()) lootTableDirectory.mkdir();
//                    if (lootTableDirectory.list() == null) return;
                    for (String lootTable : lootTableDirectory.list()){
                        Config config = configManager.getConfig(File.separator + "loottables" + File.separator + lootTable);
                        YamlConfiguration yaml = config.get();

                        List<Drop> drops = new ArrayList<>();
                        ConfigurationSection section = yaml.getConfigurationSection("drops");
                        if (section != null){
                            for (String drop : section.getKeys(false)){
                                String dropItem = yaml.getItemStack("drops." + drop + ".item").getType().toString();
                                try {
                                    Drop d = new Drop(
                                            yaml.getItemStack("drops." + drop + ".item"),
                                            yaml.getDouble("drops." + drop + ".drop_chance"),
                                            yaml.getDouble("drops." + drop + ".drop_chance_looting"),
                                            yaml.getInt("drops." + drop + ".min_drops"),
                                            yaml.getInt("drops." + drop + ".min_drops_looting"),
                                            yaml.getInt("drops." + drop + ".max_drops"),
                                            yaml.getInt("drops." + drop + ".max_drops_looting")
                                    );

                                    drops.add(d);
                                } catch (Exception e){
                                    Main.getInstance().getServer().getConsoleSender().sendMessage(Utils.chat("&cDrop type " + dropItem + " was not recognized for this version of minecraft"));
                                    Main.getInstance().getServer().getConsoleSender().sendMessage(Utils.chat("&cso it was skipped. If you want this monster to be implemented anyway,"));
                                    Main.getInstance().getServer().getConsoleSender().sendMessage(Utils.chat("&center the LootTable's yml file at ProgressivelyDifficultMobs/loottables/"));
                                    Main.getInstance().getServer().getConsoleSender().sendMessage(Utils.chat("&cand replace the dropped item's type value with an alternative up-to-date variant."));
                                    Main.getInstance().getServer().getConsoleSender().sendMessage(Utils.chat("&cExample: NETHERRITE_CHESTPLATE > DIAMOND_CHESTPLATE"));
                                    Main.getInstance().getServer().getConsoleSender().sendMessage(Utils.chat("&cThen do '/pdm reload' ingame"));
                                }
                            }
                        }
                        LootTable tableToRegister = new LootTable(
                                lootTable,
                                drops,
                                (yaml.getString("icon") == null) ? null : Material.valueOf(yaml.getString("icon")));

                        config.copyDefaults(true).save();
                        lootTableManager.registerLootTable(tableToRegister);
                    }
                } catch (NullPointerException npe){
                    System.out.println("Error: Given path does not exist");
                    npe.printStackTrace();
                }
                Main.getInstance().getServer().broadcast(Utils.chat("Finished loading ProgressiveDifficulty: MOBS - Loot Tables"), "pdm.reload");
                LootTableManager.getInstance().setEnabled(true);
            }
        }.runTaskAsynchronously(Main.getInstance());
    }
}
