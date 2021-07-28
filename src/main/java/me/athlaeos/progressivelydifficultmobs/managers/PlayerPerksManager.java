package me.athlaeos.progressivelydifficultmobs.managers;

import me.athlaeos.progressivelydifficultmobs.main.Main;
import me.athlaeos.progressivelydifficultmobs.perks.Perk;
import me.athlaeos.progressivelydifficultmobs.perks.PerkTriggerPriority;
import me.athlaeos.progressivelydifficultmobs.perks.onattackperks.DamageBuffPerk;
import me.athlaeos.progressivelydifficultmobs.perks.onattackperks.DamageNerfPerk;
import me.athlaeos.progressivelydifficultmobs.perks.onblockbreakperks.ExtraFarmingDropsPerk;
import me.athlaeos.progressivelydifficultmobs.perks.onblockbreakperks.FarmingEXPPerk;
import me.athlaeos.progressivelydifficultmobs.perks.onblockinteractperks.AutoReplantCropsPerk;
import me.athlaeos.progressivelydifficultmobs.perks.ondamagedperks.*;
import me.athlaeos.progressivelydifficultmobs.perks.onentitybreedperks.ExtraBreedingEXPPerk;
import me.athlaeos.progressivelydifficultmobs.perks.onpotioneffectperks.BlindImmunityPerk;
import me.athlaeos.progressivelydifficultmobs.perks.onpotioneffectperks.SlowImmunityPerk;
import me.athlaeos.progressivelydifficultmobs.utils.Utils;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;

import java.util.*;

public class PlayerPerksManager {
    private static PlayerPerksManager manager = null;
    private Map<Integer, Perk> allPerks = new HashMap<>();

    private Map<Integer, List<Integer>> levelPerks = new HashMap<>();
    private NamespacedKey playerPerksKey = new NamespacedKey(Main.getInstance(), "pdm-player-perks");
    private final YamlConfiguration config = ConfigManager.getInstance().getConfig("perks.yml").get();

    public PlayerPerksManager(){
        registerStandardPerks();
    }

    private void registerStandardPerks(){
        registerPerk(new AutoReplantCropsPerk()); //1000
        registerPerk(new FarmingEXPPerk()); //1001
        registerPerk(new ExtraFarmingDropsPerk()); //1002
        registerPerk(new ExtraBreedingEXPPerk()); //1003
        registerPerk(new RevivePerk()); //1004
        registerPerk(new DamageMitigationsPerk(0.05, 1005));
        registerPerk(new DamageMitigationsPerk(0.10, 1006));
        registerPerk(new DamageMitigationsPerk(0.25, 1007));
        registerPerk(new DamageMitigationsPerk(0.50, 1008));
        registerPerk(new DamageMitigationsPerk(0.75, 1009));
        registerPerk(new DamageMitigationsPerk(1.0, 1010));
        registerPerk(new FragilityPerk(0.05, 1011));
        registerPerk(new FragilityPerk(0.1, 1012));
        registerPerk(new FragilityPerk(0.25, 1013));
        registerPerk(new FragilityPerk(0.5, 1014));
        registerPerk(new FragilityPerk(0.75, 1015));
        registerPerk(new FragilityPerk(1D, 1016));
        registerPerk(new FragilityPerk(2D, 1017));
        registerPerk(new FragilityPerk(3D, 1018));
        registerPerk(new FragilityPerk(4D, 1019));
        registerPerk(new DamageNerfPerk(0.05, 1020));
        registerPerk(new DamageNerfPerk(0.10, 1021));
        registerPerk(new DamageNerfPerk(0.25, 1022));
        registerPerk(new DamageNerfPerk(0.50, 1023));
        registerPerk(new DamageNerfPerk(0.75, 1024));
        registerPerk(new DamageNerfPerk(1.0, 1025));
        registerPerk(new DamageBuffPerk(0.05, 1026));
        registerPerk(new DamageBuffPerk(0.1, 1027));
        registerPerk(new DamageBuffPerk(0.25, 1028));
        registerPerk(new DamageBuffPerk(0.5, 1029));
        registerPerk(new DamageBuffPerk(0.75, 1030));
        registerPerk(new DamageBuffPerk(1D, 1031));
        registerPerk(new DamageBuffPerk(2D, 1032));
        registerPerk(new DamageBuffPerk(3D, 1033));
        registerPerk(new DamageBuffPerk(4D, 1034));
        registerPerk(new ExplosionImmunityPerk()); //1035
        registerPerk(new ProjectileImmunityPerk()); //1036
        registerPerk(new MeleeImmunityPerk()); //1037
        registerPerk(new FireImmunityPerk()); //1038
        registerPerk(new PoisonImmunityPerk()); //1039
        registerPerk(new ImmortalityPerk()); //1040
        registerPerk(new WitherImmunityPerk()); //1041
        registerPerk(new FallingImmunityPerk()); //1042
        registerPerk(new MagicImmunityPerk()); //1043
        registerPerk(new SlowImmunityPerk()); //1044
        registerPerk(new BlindImmunityPerk()); //1045
        registerPerk(new RagePerk()); //1046
        registerPerk(new AdrenalinePerk()); //1047
        registerPerk(new ResilientPerk()); //1048
    }

    public Perk getPerkByName(String name){
        for (Integer i : allPerks.keySet()){
            Perk p = allPerks.get(i);
            if (p.getName().equals(name)){
                return p;
            }
        }
        return null;
    }

    public void saveLevelPerks(){
        for (Integer level : levelPerks.keySet()){
            config.set("level_perks."+level, levelPerks.get(level));
        }
        ConfigManager.getInstance().saveConfig("perks.yml");
    }

    public void loadLevelPerks(){
        if (config.getConfigurationSection("level_perks") == null) return;
        Set<String> section = config.getConfigurationSection("level_perks").getKeys(false);
        for (String level : section){
            try {
                int lv = Integer.parseInt(level);
                levelPerks.put(lv, config.getIntegerList("level_perks."+lv));
            } catch (IllegalArgumentException ignored){
            }
        }
    }

    public void addLevelPerk(int level, int perkID){
        if (levelPerks.get(level) != null){
            List<Integer> perks = levelPerks.get(level);
            perks.add(perkID);
            levelPerks.put(level, perks);
        } else {
            List<Integer> perks = new ArrayList<>();
            perks.add(perkID);
            levelPerks.put(level, perks);
        }
    }

    public void removeLevelPerk(int level, int perkID){
        if (levelPerks.get(level) != null){
            List<Integer> perks = levelPerks.get(level);
            perks.remove(Integer.valueOf(perkID));
            levelPerks.put(level, perks);
        }
    }

    public Map<Integer, Perk> getAllPerks() {
        return allPerks;
    }

    public void reload(){
        saveLevelPerks();
        this.allPerks.clear();
        this.levelPerks.clear();
        manager = new PlayerPerksManager();
        loadLevelPerks();
    }

    public void registerPerk(Perk p){
        if (p == null) return;
        if (allPerks.get(p.getId()) != null) {
            throw new IllegalArgumentException("Attempted to register perk " + p.getName() + " but its ID has already been registered");
        }
        allPerks.put(p.getId(), p);
    }

    public List<Perk> getPlayersTotalPerks(Player p){
        if (p == null) return new ArrayList<>();
        int playerLevel = PlayerKarmaManager.getInstance().getKarmaLevel(p.getUniqueId());
        List<Perk> playersPerks = new ArrayList<>(getPlayersPerks(p));
        if (levelPerks.containsKey(playerLevel)){
            for (Integer perkID : levelPerks.get(playerLevel)){
                Perk perk = allPerks.get(perkID);
                if (perk != null){
                    if (!playersPerks.contains(perk)){
                        playersPerks.add(perk);
                    }
                }
            }
        }
        return playersPerks;
    }

    public List<Perk> sortPerksByPriority(List<Perk> perks){
        List<Perk> sortedPerks = new ArrayList<>();
        PerkTriggerPriority[] priorities = new PerkTriggerPriority[]{PerkTriggerPriority.SOONEST, PerkTriggerPriority.SOONER,
        PerkTriggerPriority.NEUTRAL, PerkTriggerPriority.LATER, PerkTriggerPriority.LATEST};

        for (PerkTriggerPriority p : priorities){
            for (Perk perk : perks){
                if (perk.getPerkPriority() == p){
                    sortedPerks.add(perk);
                }
            }
        }
        return sortedPerks;
    }

    public List<Perk> getLevelsPerks(int i){
        List<Perk> returnList = new ArrayList<>();
        if (levelPerks.get(i) == null) return returnList;
        for (Integer integer : levelPerks.get(i)){
            Perk p = allPerks.get(integer);
            if (p != null){
                returnList.add(p);
            }
        }
        return returnList;
    }

    public List<Perk> getPlayersPerks(Player p){
        if (p.getPersistentDataContainer().has(playerPerksKey, PersistentDataType.INTEGER_ARRAY)){
            List<Perk> playerPerks = new ArrayList<>();
            for (Integer perkID : p.getPersistentDataContainer().get(playerPerksKey, PersistentDataType.INTEGER_ARRAY)){
                if (allPerks.get(perkID) != null){
                    playerPerks.add(allPerks.get(perkID));
                }
            }
            return playerPerks;
        }
        return new ArrayList<>();
    }

    public boolean addPlayerPerk(Player p, int id){
        if (allPerks.get(id) == null) return false;
        if (!p.getPersistentDataContainer().has(playerPerksKey, PersistentDataType.INTEGER_ARRAY)){
            p.getPersistentDataContainer().set(playerPerksKey, PersistentDataType.INTEGER_ARRAY, new int[0]);
        }
        List<Integer> playersPerks = Utils.toIntList(p.getPersistentDataContainer().get(playerPerksKey, PersistentDataType.INTEGER_ARRAY));
        if (playersPerks.contains(id)) return false;
        playersPerks.add(id);
        p.getPersistentDataContainer().set(playerPerksKey, PersistentDataType.INTEGER_ARRAY, Utils.toIntArray(playersPerks));
        return true;
    }

    public boolean removePlayerPerk(Player p, int id){
        if (allPerks.get(id) == null) return false;
        if (!p.getPersistentDataContainer().has(playerPerksKey, PersistentDataType.INTEGER_ARRAY)){
            return false;
        }
        List<Integer> playersPerks = Utils.toIntList(p.getPersistentDataContainer().get(playerPerksKey, PersistentDataType.INTEGER_ARRAY));
        if (playersPerks.contains(id)) {
            playersPerks.remove(Integer.valueOf(id));
            p.getPersistentDataContainer().set(playerPerksKey, PersistentDataType.INTEGER_ARRAY, Utils.toIntArray(playersPerks));
            return true;
        }
        return false;
    }

    public static PlayerPerksManager getInstance(){
        if (manager == null){
            manager = new PlayerPerksManager();
        }
        return manager;
    }
}
