package me.athlaeos.progressivelydifficultmobs.persistence;

import me.athlaeos.progressivelydifficultmobs.main.Main;
import me.athlaeos.progressivelydifficultmobs.managers.ConfigManager;
import me.athlaeos.progressivelydifficultmobs.managers.LeveledMonsterManager;
import me.athlaeos.progressivelydifficultmobs.pojo.Config;
import me.athlaeos.progressivelydifficultmobs.pojo.LeveledMonster;
import me.athlaeos.progressivelydifficultmobs.utils.Utils;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.IOException;

public class LeveledMonsterPersister {

    public static void deleteLeveledMonster(LeveledMonster monster){
        File file = new File(Main.getInstance().getDataFolder() + "\\monsters\\" + monster.getEntityType() + "\\" + monster.getLevel() + "\\" + monster.getName() + ".yml");
        file.delete();
    }

    public static void saveLeveledMonstersAsynchronously(){
        LeveledMonsterManager monsterManager = LeveledMonsterManager.getInstance();
        new BukkitRunnable(){
            @Override
            public void run() {
                for (LeveledMonster m : monsterManager.getAllMonsters()){
                    saveLeveledMonster(m);
                }
            }
        }.runTaskAsynchronously(Main.getInstance());
    }

    public static void saveLeveledMonsters(){
        LeveledMonsterManager monsterManager = LeveledMonsterManager.getInstance();
        for (LeveledMonster m : monsterManager.getAllMonsters()){
            saveLeveledMonster(m);
        }
    }

    public static void saveLeveledMonster(LeveledMonster m){
        ConfigManager configManager = ConfigManager.getInstance();
        String configPath = "monsters" + File.separator + m.getEntityType().toString() + File.separator + ((m.doesSpawnWithoutLevel()) ? "global" : m.getLevel()) + File.separator + m.getName() + ".yml";
        YamlConfiguration config = configManager.getConfig(configPath).get();
        config.set("level", m.getLevel());
        config.set("spawn_weight", m.getSpawnWeight());
        config.set("save_name", m.getName());
        config.set("entity_type", "" + m.getEntityType().toString());
        config.set("is_boss", m.isBoss());
        config.set("display_name", m.getDisplayName());
        config.set("loot_tables", m.getLootTables());
        config.set("abilities", m.getAbilities());
        config.set("display_name_visible", m.isDisplayNameVisible());
        config.set("drops_default_loot_table", m.isDropsDefaultLootTable());
        config.set("karma_influence", m.getKarmaInfluence());
        config.set("exp_on_death", m.getExpDropped());
        config.set("base_health", m.getBaseHealth());
        config.set("enabled", m.isEnabled());
        config.set("equipment_helmet", m.getHelmet());
        config.set("equipment_chest_plate", m.getChestPlate());
        config.set("equipment_leggings", m.getLeggings());
        config.set("equipment_boots", m.getBoots());
        config.set("equipment_main_hand", m.getMainHand());
        config.set("equipment_off_hand", m.getOffHand());
        config.set("biome_filter", m.getBiomeFilter());
        config.set("world_filter", m.getWorldFilter());
        config.set("region_filter", m.getRegionFilter());
        config.set("min_y_range", m.getMinYRange());
        config.set("max_y_range", m.getMaxYRange());
        config.set("helmet_drop_chance", m.getHelmetDropChance());
        config.set("chestplate_drop_chance", m.getChestplateDropChance());
        config.set("leggings_drop_chance", m.getLeggingsDropChance());
        config.set("boots_drop_chance", m.getBootsDropChance());
        config.set("main_hand_drop_chance", m.getMainHandDropChance());
        config.set("off_hand_drop_chance", m.getOffHandDropChance());
        configManager.saveConfig(configPath);
    }

    public static void loadMonsters(){
        ConfigManager configManager = ConfigManager.getInstance();
        LeveledMonsterManager monsterManager = LeveledMonsterManager.getInstance();
        new BukkitRunnable(){
            @Override
            public void run() {
                try{
                    File monstersDirectory = new File(Main.getInstance().getDataFolder(), "monsters");
                    if (!monstersDirectory.exists()) monstersDirectory.mkdir();
                    for (String monsterType : monstersDirectory.list()){
                        File monsterTypeDirectory = new File(monstersDirectory, monsterType);
                        EntityType type;
                        try {
                            type = EntityType.valueOf(monsterType);
                        } catch (IllegalArgumentException e){
                            Main.getInstance().getServer().getConsoleSender().sendMessage(Utils.chat("&cMonster type " + monsterType + " was not recognized for this version of minecraft"));
                            Main.getInstance().getServer().getConsoleSender().sendMessage(Utils.chat("&cso it was skipped. If you want this monster to be implemented anyway,"));
                            Main.getInstance().getServer().getConsoleSender().sendMessage(Utils.chat("&center the monsters' yml files at ProgressivelyDifficultMobs/monsters"));
                            Main.getInstance().getServer().getConsoleSender().sendMessage(Utils.chat("&cand replace the folder name with the corrected version."));
                            Main.getInstance().getServer().getConsoleSender().sendMessage(Utils.chat("&cExample: ZOMBIE_PIGMAN > ZOMBIFIED_PIGLIN"));
                            Main.getInstance().getServer().getConsoleSender().sendMessage(Utils.chat("&cThen do '/pdm reload' ingame"));
                            continue;
                        }
                        for (String level : monsterTypeDirectory.list()){
                            File monsterLevelDirectory = new File(monsterTypeDirectory, level);
                            int lv = 0;
                            boolean globalSpawn = false;
                            try{
                                lv = Integer.parseInt(level);
                            } catch (IllegalArgumentException e){
                                if (level.equalsIgnoreCase("global")){
                                    globalSpawn = true;
                                } else {
                                    System.out.println("Level directory name " + level + " is not a number, could not be read");
                                    continue;
                                }
                            }
                            for (String monsterConfig : monsterLevelDirectory.list()){
                                Config config = configManager.getConfig(File.separator + "monsters" + File.separator + monsterType + File.separator + level + File.separator + monsterConfig);
                                YamlConfiguration yaml = config.get();
                                LeveledMonster monsterToRegister = new LeveledMonster(
                                        lv,
                                        yaml.getInt("spawn_weight"),
                                        monsterConfig.replace(".yml", ""),
                                        type,
                                        yaml.getBoolean("is_boss"),
                                        yaml.getString("display_name"),
                                        yaml.getBoolean("display_name_visible"),
                                        yaml.getStringList("loot_tables"),
                                        yaml.getStringList("abilities"),
                                        yaml.getBoolean("drops_default_loot_table"),
                                        yaml.getDouble("karma_influence"),
                                        yaml.getInt("exp_on_death"),
                                        yaml.getDouble("base_health"),
                                        yaml.getBoolean("enabled"),
                                        yaml.getItemStack("equipment_helmet"),
                                        yaml.getItemStack("equipment_chest_plate"),
                                        yaml.getItemStack("equipment_leggings"),
                                        yaml.getItemStack("equipment_boots"),
                                        yaml.getItemStack("equipment_main_hand"),
                                        yaml.getItemStack("equipment_off_hand"),
                                        yaml.getStringList("biome_filter"),
                                        yaml.getStringList("world_filter"),
                                        yaml.getStringList("region_filter"),
                                        yaml.getInt("min_y_range"),
                                        (yaml.getInt("max_y_range") == 0) ? 255 : yaml.getInt("max_y_range"),
                                        yaml.getDouble("helmet_drop_chance"),
                                        yaml.getDouble("chestplate_drop_chance"),
                                        yaml.getDouble("leggings_drop_chance"),
                                        yaml.getDouble("boots_drop_chance"),
                                        yaml.getDouble("main_hand_drop_chance"),
                                        yaml.getDouble("off_hand_drop_chance"),
                                        globalSpawn);

                                config.copyDefaults(true).save();
                                monsterManager.registerMonster(monsterToRegister);
                            }
                        }
                    }
                } catch (NullPointerException npe){
                    System.out.println("Error: Given path does not exist");
                    npe.printStackTrace();
                }
                Main.getInstance().getServer().broadcast(Utils.chat("Finished loading ProgressiveDifficulty: MOBS - Monsters"), "pdm.reload");
                LeveledMonsterManager.getInstance().enableMonsters();
            }
        }.runTaskAsynchronously(Main.getInstance());
    }
}
