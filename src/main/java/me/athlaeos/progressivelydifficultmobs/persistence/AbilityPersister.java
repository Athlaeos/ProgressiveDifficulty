package me.athlaeos.progressivelydifficultmobs.persistence;

import me.athlaeos.progressivelydifficultmobs.abilities.CustomizableImmediatePotionEffectAbility;
import me.athlaeos.progressivelydifficultmobs.abilities.CustomizablePotionEffectRetaliationAbility;
import me.athlaeos.progressivelydifficultmobs.abilities.CustomizablePotionEffectOnHitPlayerAbility;
import me.athlaeos.progressivelydifficultmobs.abilities.CustomizablePotionEffectWhenDamagedAbility;
import me.athlaeos.progressivelydifficultmobs.main.Main;
import me.athlaeos.progressivelydifficultmobs.managers.AbilityManager;
import me.athlaeos.progressivelydifficultmobs.managers.ConfigManager;
import me.athlaeos.progressivelydifficultmobs.managers.PluginConfigurationManager;
import me.athlaeos.progressivelydifficultmobs.pojo.Config;
import me.athlaeos.progressivelydifficultmobs.utils.Utils;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;

public class AbilityPersister {

    public static void loadAbilities(){
        AbilityManager abilityManager = AbilityManager.getInstance();
        ConfigManager configManager = ConfigManager.getInstance();
        Config config = configManager.getConfig("custom-abilities.yml");
        YamlConfiguration yaml = config.get();
        for (String key : yaml.getConfigurationSection("on_damage_player_abilities").getKeys(false)){
            try{
                PotionEffectType potionEffectType = PotionEffectType.getByName(yaml.getString("on_damage_player_abilities." + key + ".effect"));
                if (potionEffectType == null) throw new IllegalArgumentException();
                List<String> abilityLore = new ArrayList<>();
                for (String s : yaml.getStringList("on_damage_player_abilities." + key + ".description")){
                    abilityLore.add(Utils.chat("&r" + s));
                }
                abilityManager.getOnPlayerDamagedAbilities().put(key, new CustomizablePotionEffectOnHitPlayerAbility(
                        Utils.chat("&r" + yaml.getString("on_damage_player_abilities." + key + ".name")),
                        abilityLore,
                        Material.valueOf(yaml.getString("on_damage_player_abilities." + key + ".icon")),
                        potionEffectType,
                        yaml.getInt("on_damage_player_abilities." + key + ".amplifier"),
                        yaml.getInt("on_damage_player_abilities." + key + ".duration")
                ));
            } catch (IllegalArgumentException e){
                Main.getInstance().getServer().getConsoleSender().sendMessage(Utils.chat("&c" +
                        "For ability " + key + " in the config an invalid material or potion effect type were given."));
            }
        }
        for (String key : yaml.getConfigurationSection("on_damaged_by_player_abilities").getKeys(false)){
            try{
                PotionEffectType potionEffectType = PotionEffectType.getByName(yaml.getString("on_damaged_by_player_abilities." + key + ".effect"));
                if (potionEffectType == null) throw new IllegalArgumentException();
                List<String> abilityLore = new ArrayList<>();
                for (String s : yaml.getStringList("on_damaged_by_player_abilities." + key + ".description")){
                    abilityLore.add(Utils.chat("&r" + s));
                }
                abilityManager.getOnMobDamagedByPlayerAbilities().put(key, new CustomizablePotionEffectWhenDamagedAbility(
                        Utils.chat("&r" + yaml.getString("on_damaged_by_player_abilities." + key + ".name")),
                        abilityLore,
                        Material.valueOf(yaml.getString("on_damaged_by_player_abilities." + key + ".icon")),
                        potionEffectType,
                        yaml.getInt("on_damaged_by_player_abilities." + key + ".amplifier"),
                        yaml.getInt("on_damaged_by_player_abilities." + key + ".duration")
                ));
            } catch (IllegalArgumentException e){
                Main.getInstance().getServer().getConsoleSender().sendMessage(Utils.chat("&c" +
                        "For ability " + key + " in the config an invalid material or potion effect type were given."));
            }
        }
        for (String key : yaml.getConfigurationSection("on_spawn_abilities").getKeys(false)){
            try{
                PotionEffectType potionEffectType = PotionEffectType.getByName(yaml.getString("on_spawn_abilities." + key + ".effect"));
                if (potionEffectType == null) throw new IllegalArgumentException();
                List<String> abilityLore = new ArrayList<>();
                for (String s : yaml.getStringList("on_spawn_abilities." + key + ".description")){
                    abilityLore.add(Utils.chat("&r" + s));
                }
                abilityManager.getInstantAbilities().put(key, new CustomizableImmediatePotionEffectAbility(
                        Utils.chat("&r" + yaml.getString("on_spawn_abilities." + key + ".name")),
                        abilityLore,
                        Material.valueOf(yaml.getString("on_spawn_abilities." + key + ".icon")),
                        potionEffectType,
                        yaml.getInt("on_spawn_abilities." + key + ".amplifier"),
                        yaml.getInt("on_spawn_abilities." + key + ".duration")
                ));
            } catch (IllegalArgumentException e){
                Main.getInstance().getServer().getConsoleSender().sendMessage(Utils.chat("&c" +
                        "For ability " + key + " in the config an invalid material or potion effect type were given."));
            }
        }
        for (String key : yaml.getConfigurationSection("retaliation_abilities").getKeys(false)){
            try{
                PotionEffectType potionEffectType = PotionEffectType.getByName(yaml.getString("retaliation_abilities." + key + ".effect"));
                if (potionEffectType == null) throw new IllegalArgumentException();
                List<String> abilityLore = new ArrayList<>();
                for (String s : yaml.getStringList("retaliation_abilities." + key + ".description")){
                    abilityLore.add(Utils.chat("&r" + s));
                }
                abilityManager.getOnMobDamagedByPlayerAbilities().put(key, new CustomizablePotionEffectRetaliationAbility(
                        Utils.chat("&r" + yaml.getString("retaliation_abilities." + key + ".name")),
                        abilityLore,
                        Material.valueOf(yaml.getString("retaliation_abilities." + key + ".icon")),
                        potionEffectType,
                        yaml.getInt("retaliation_abilities." + key + ".amplifier"),
                        yaml.getInt("retaliation_abilities." + key + ".duration")
                ));
            } catch (IllegalArgumentException e){
                Main.getInstance().getServer().getConsoleSender().sendMessage(Utils.chat("&c" +
                        "For ability " + key + " in the config an invalid material, particle, or potion effect type were given."));
            }
        }
        config.copyDefaults(true);
    }
}
