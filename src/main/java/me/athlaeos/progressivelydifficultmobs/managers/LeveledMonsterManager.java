package me.athlaeos.progressivelydifficultmobs.managers;

import me.athlaeos.progressivelydifficultmobs.main.Main;
import me.athlaeos.progressivelydifficultmobs.pojo.Ability;
import me.athlaeos.progressivelydifficultmobs.pojo.Container;
import me.athlaeos.progressivelydifficultmobs.pojo.LeveledMonster;
import me.athlaeos.progressivelydifficultmobs.utils.Utils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.block.Biome;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Slime;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.persistence.PersistentDataType;

import java.util.*;

public class LeveledMonsterManager {

    private static LeveledMonsterManager manager = null;
    private final NamespacedKey monsterIdKey = new NamespacedKey(Main.getInstance(), "pdm-monsterID");
    public List<LeveledMonster> allMonsters = new ArrayList<>();
    public boolean enabled = false;

    public LeveledMonsterManager(){

    }

    /**
     * @return true if LeveledMonsters have finished loading in, false otherwise
     */
    public boolean isEnabled() {
        return enabled;
    }

    public static LeveledMonsterManager getInstance(){
        if (manager == null){
            manager = new LeveledMonsterManager();
        }
        return manager;
    }

    /**
     * Converts a vanilla mob into a custom one
     * This cannot be done to monsters that are already converted
     * @param mob the mob to be converted
     * @param leveledMonster the LeveledMonster the mob is to be converted into
     */
    public void customizeMob(LivingEntity mob, LeveledMonster leveledMonster, CreatureSpawnEvent.SpawnReason spawnReason){
        mob.getPersistentDataContainer().set(monsterIdKey, PersistentDataType.STRING, leveledMonster.getName());
        assert mob.getEquipment() != null;
        mob.getEquipment().setHelmet(leveledMonster.getHelmet());
        mob.getEquipment().setChestplate(leveledMonster.getChestPlate());
        mob.getEquipment().setLeggings(leveledMonster.getLeggings());
        mob.getEquipment().setBoots(leveledMonster.getBoots());
        mob.getEquipment().setItemInMainHand(leveledMonster.getMainHand());
        mob.getEquipment().setItemInOffHand(leveledMonster.getOffHand());

        mob.getEquipment().setHelmetDropChance((float) leveledMonster.getHelmetDropChance());
        mob.getEquipment().setChestplateDropChance((float) leveledMonster.getChestplateDropChance());
        mob.getEquipment().setLeggingsDropChance((float) leveledMonster.getLeggingsDropChance());
        mob.getEquipment().setBootsDropChance((float) leveledMonster.getBootsDropChance());
        mob.getEquipment().setItemInMainHandDropChance((float) leveledMonster.getMainHandDropChance());
        mob.getEquipment().setItemInOffHandDropChance((float) leveledMonster.getOffHandDropChance());

        if (leveledMonster.getMainHand() != null){
            mob.setCanPickupItems(false);
        }

        AttributeInstance attribute = mob.getAttribute(Attribute.GENERIC_MAX_HEALTH);
        assert attribute != null;
        if (mob instanceof Slime){
            Slime slime = (Slime) mob;
            attribute.setBaseValue(leveledMonster.getBaseHealth() / (16/Math.pow(slime.getSize(), 2)));
            mob.setHealth(leveledMonster.getBaseHealth() / (16/Math.pow(slime.getSize(), 2)));
        } else {
            attribute.setBaseValue(leveledMonster.getBaseHealth());
            mob.setHealth(leveledMonster.getBaseHealth());
        }
        if (leveledMonster.getDisplayName() != null){
            if (!leveledMonster.getDisplayName().equals("null")){
                mob.setCustomName(leveledMonster.getDisplayName());
                mob.setCustomNameVisible(leveledMonster.isDisplayNameVisible());
            }
        }
        if (leveledMonster.isBoss()){
            String title = leveledMonster.getDisplayName();
            if (leveledMonster.getDisplayName() == null){
                title = Utils.chat("&c&lBoss");
            } else if (leveledMonster.getDisplayName().equals("null")){
                title = Utils.chat("&c&lBoss");
            }
            Utils.createBossBar(Main.getInstance(), mob, title, BarColor.RED, BarStyle.SOLID, Main.getInstance().getConfig().getInt("boss_bar_view_distance"));
        }

        for (String key : leveledMonster.getAbilities()){
            Ability instantAbility = AbilityManager.getInstance().getInstantAbilities().get(key);
            if (instantAbility != null){
                instantAbility.execute(mob, null, new CreatureSpawnEvent(mob, spawnReason));
            }
            Ability runningAbility = AbilityManager.getInstance().getRunningAbilities().get(key);
            if (runningAbility != null){
                runningAbility.execute(mob, null, new CreatureSpawnEvent(mob, spawnReason));
            }
        }
    }

    /**
     * Tells the plugin monsters have finished loading in
     * This method should not be touched
     */
    public void enableMonsters(){
        enabled = true;
    }

    /**
     * Tells the plugin monsters have not finished loading in
     * Should be used when reloading monster configs
     */
    public void disableMonsters(){
        enabled = false;
    }

    /**
     * @return a list of LeveledMonsters that have been registered
     */
    public List<LeveledMonster> getAllMonsters(){
        return allMonsters;
    }

    /**
     * Registers a new LeveledMonster
     * @param monster
     */
    public void registerMonster(LeveledMonster monster){
        for (LeveledMonster m : allMonsters){
            if (m.getName().equals(monster.getName())){
                updateMonster(monster);
                return;
            }
        }
        if (!allMonsters.contains(monster)){
            allMonsters.add(monster);
        }
    }

    /**
     * Updates a monster's properties to match the given monster's properties
     * If the given monster has not been registered yet, this will not do anything.
     * @param monster
     */
    public void updateMonster(LeveledMonster monster){
        if (enabled){
            for (LeveledMonster m : allMonsters){
                if (m.getName().equals(monster.getName())){
                    m.setSpawnWeight(monster.getSpawnWeight());
                    m.setLootTables(monster.getLootTables());
                    m.setKarmaInfluence(monster.getKarmaInfluence());
                    m.setExpDropped(monster.getExpDropped());
                    m.setEnabled(monster.isEnabled());
                    m.setDropsDefaultLootTable(monster.isDropsDefaultLootTable());
                    m.setEquipment(monster.getHelmet(), monster.getChestPlate(), monster.getLeggings(), monster.getBoots(), monster.getMainHand(), monster.getOffHand());
                    m.setBaseHealth(monster.getBaseHealth());
                    m.setWorldFilter(monster.getWorldFilter());
                    m.setMaxYRange(monster.getMaxYRange());
                    m.setMinYRange(monster.getMinYRange());
                    m.setBiomeFilter(monster.getBiomeFilter());
                    m.setDisplayNameVisible(monster.isDisplayNameVisible());
                    m.setBoss(monster.isBoss());
                    m.setAbilities(monster.getAbilities());
                    m.setHelmetDropChance(monster.getHelmetDropChance());
                    m.setChestplateDropChance(monster.getChestplateDropChance());
                    m.setLeggingsDropChance(monster.getLeggingsDropChance());
                    m.setBootsDropChance(monster.getBootsDropChance());
                    m.setMainHandDropChance(monster.getMainHandDropChance());
                    m.setOffHandDropChance(monster.getOffHandDropChance());
                    m.setBiomeFilter(monster.getBiomeFilter());
                    break;
                }
            }
        }
    }

    /**
     * Gets a list of monsters that can spawn under these conditions
     * @param level
     * @param type
     * @param location
     * @return A list of LeveledMonsters that can spawn with the given conditions
     */
    public List<LeveledMonster> getSpawnableMonsters(int level, EntityType type, Location location){
        if (enabled){
            List<LeveledMonster> spawnableMonsters = new ArrayList<>();
            for (LeveledMonster m : allMonsters){
                if (m.getEntityType() == type && (m.getLevel() == level || m.doesSpawnWithoutLevel()) && m.isEnabled()){
                    if (location != null){
                        if (m.getMinYRange() > location.getY() || m.getMaxYRange() < location.getY()){
                            continue;
                        }
                        if (m.getBiomeFilter().size() > 0){
                            boolean biomeCompatible = false;
                            for (String key : m.getBiomeFilter()){
                                Container<List<Biome>, Material> container = BiomeCategoryManager.getInstance().getAllBiomes().get(key);
                                if (container != null){
                                    if (container.getKey().contains(location.getBlock().getBiome())){
                                        biomeCompatible = true;
                                        break;
                                    }
                                }
                            }
                            if (!biomeCompatible) continue;
                        }
                        if (WorldguardManager.getWorldguardManager().useWorldGuard()){
                            if (m.getRegionFilter().size() > 0){
                                boolean regionCompatible = false;
                                for (String region : m.getRegionFilter()){
                                    if (WorldguardManager.getWorldguardManager().getLocationRegions(location).contains(region)){
                                        regionCompatible = true;
                                        break;
                                    }
                                }
                                if (!regionCompatible) continue;
                            }
                        }
                        if (m.getWorldFilter().size() > 0){
                            if (!m.getWorldFilter().contains(location.getBlock().getWorld().getName())){
                                continue;
                            }
                        }
                    }
                    spawnableMonsters.add(m);
                }
            }
            return spawnableMonsters;
        } else {
            return new ArrayList<>();
        }
    }

    /**
     * @param name
     * @return the LeveledMonster with the given name, or null if it doesn't exist
     */
    public LeveledMonster getMonster(String name){
        if (enabled && name != null){
            for(LeveledMonster m : allMonsters){
                if (m.getName().equals(name)) return m;
            }
        }
        return null;
    }

    /**
     * @param type
     * @return a list of LeveledMonsters containing all of the monsters of the given EntityType
     */
    public List<LeveledMonster> getMonstersByType(EntityType type){
        List<LeveledMonster> typeSortedMonsters = new ArrayList<>();
        if (enabled) {
            for (LeveledMonster m : allMonsters){
                if (m.getEntityType() == type){
                    typeSortedMonsters.add(m);
                }
            }
        }
        return typeSortedMonsters;
    }

    /**
     * @param type
     * @return a list of LeveledMonsters containing all of the monsters of the given EntityType and difficulty level
     */
    public List<LeveledMonster> getMonsterByTypeAndLevel(EntityType type, int level){
        List<LeveledMonster> typeSortedMonsters = new ArrayList<>();
        if (enabled){
            for (LeveledMonster m : allMonsters){
                if (m.getEntityType() == type && m.getLevel() == level){
                    typeSortedMonsters.add(m);
                }
            }
        }
        return typeSortedMonsters;
    }

    public List<LeveledMonster> getMonsterByTypeAndIfGlobal(EntityType type){
        List<LeveledMonster> typeSortedMonsters = new ArrayList<>();
        if (enabled){
            for (LeveledMonster m : allMonsters){
                if (m.getEntityType() == type && m.doesSpawnWithoutLevel() == true){
                    typeSortedMonsters.add(m);
                }
            }
        }
        return typeSortedMonsters;
    }

    /**
     * Deleted a monster
     * @param name
     */
    public void deleteMonster(String name){
        if (enabled){
            for (LeveledMonster m : allMonsters){
                if (m.getName().equals(name)){
                    allMonsters.remove(m);
                    return;
                }
            }
        }
    }

    /**
     * Picks a random monster from the list of available spawnable monsters.
     * This method uses a weighted RNG system, so the monster's spawn weight is used here to determine rarity.
     * @param availableMonsters
     * @return
     */
    public LeveledMonster pickRandomMonster(List<LeveledMonster> availableMonsters){
        List<Entry> entries = new ArrayList<>();
        double accumulatedWeight = 0.0;

        for (LeveledMonster m : availableMonsters){
            accumulatedWeight += m.getSpawnWeight();
            Entry e = new Entry();
            e.object = m;
            e.accumulatedWeight = accumulatedWeight;
            entries.add(e);
        }

        double r = Utils.getRandom().nextDouble() * accumulatedWeight;

        for (Entry e : entries){
            if (e.accumulatedWeight >= r){
                return e.object;
            }
        }
        return null;
    }

    /**
     * Private class used for the weighted RNG system, should not be touched unless you know what you're doing
     */
    private static class Entry{
        double accumulatedWeight;
        LeveledMonster object;
    }
}


