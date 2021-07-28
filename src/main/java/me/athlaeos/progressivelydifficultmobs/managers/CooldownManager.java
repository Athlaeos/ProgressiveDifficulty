package me.athlaeos.progressivelydifficultmobs.managers;

import org.bukkit.entity.Entity;

import java.util.*;

public class CooldownManager {

    private static CooldownManager manager = null;

    private Map<String, Map<Entity, Long>> allCooldowns = new HashMap<>();

    public CooldownManager(){
        allCooldowns.put("cleanse_all_curse_cooldown", new HashMap<>());
        allCooldowns.put("cleanse_one_curse_cooldown", new HashMap<>());
        allCooldowns.put("enable_curse_drops_cooldown", new HashMap<>());
        allCooldowns.put("aoe_damage_cooldown", new HashMap<>());
        allCooldowns.put("local_peaceful_mode_cooldown", new HashMap<>());
        allCooldowns.put("cleanse_debuffs_cooldown", new HashMap<>());

        allCooldowns.put("enable_curse_drops_duration", new HashMap<>());
        allCooldowns.put("local_peaceful_mode_duration", new HashMap<>());
        allCooldowns.put("cleanse_debuffs_duration", new HashMap<>());

        allCooldowns.put("perks_revive_cooldown", new HashMap<>());
        allCooldowns.put("perks_slow_immunity_cooldown", new HashMap<>());
        allCooldowns.put("perks_slow_immunity_duration", new HashMap<>());
        allCooldowns.put("perks_blind_immunity_cooldown", new HashMap<>());
        allCooldowns.put("perks_blind_immunity_duration", new HashMap<>());
        allCooldowns.put("perks_rage_cooldown", new HashMap<>());
        allCooldowns.put("perks_adrenaline_cooldown", new HashMap<>());
        allCooldowns.put("perks_resilient_cooldown", new HashMap<>());
    }

    public Map<String, Map<Entity, Long>> getAllCooldowns() {
        return allCooldowns;
    }

    /**
     * Adds a cooldown to the cooldown manager. This key can be used by any entity but is mostly used
     * by the plugin to keep track of custom item or perk/ability cooldowns.
     * @param key
     */
    public void registerCooldownKey(String key){
        allCooldowns.put(key, new HashMap<>());
    }

    public static CooldownManager getInstance(){
        if (manager == null){
            manager = new CooldownManager();
        }
        return manager;
    }

    /**
     * Should be called when an entity dies or otherwise is removed to remove it from memory.
     * This is to prevent lots of entities that have actually already died from accumulating in memory
     * @param e
     */
    public void removeEntity(Entity e){
        for (String key : allCooldowns.keySet()){
            allCooldowns.get(key).remove(e);
        }
    }

    /**
     * Sets the cooldown of a cooldown key.
     * Default keys are:
     * "cleanse_all_curse_cooldown"</br>
     * "cleanse_one_curse_cooldown"</br>
     * "enable_curse_drops_cooldown"</br>
     * "aoe_damage_cooldown"</br>
     * "local_peaceful_mode_cooldown"</br>
     * "enable_curse_drops_duration"</br>
     * "local_peaceful_mode_duration"
     */
    public void setCooldown(Entity entity, int timems, String cooldownKey){
        allCooldowns.get(cooldownKey).put(entity, System.currentTimeMillis() + timems);
    }

    /**
     * Gets the player's cooldown of an item given their cooldown key.
     * Default keys are:
     * "cleanse_all_curse_cooldown"</br>
     * "cleanse_one_curse_cooldown"</br>
     * "enable_curse_drops_cooldown"</br>
     * "aoe_damage_cooldown"</br>
     * "local_peaceful_mode_cooldown"</br>
     * "enable_curse_drops_duration"</br>
     * "local_peaceful_mode_duration"
     *
     * @return the remaining cooldown of the item in milliseconds, or 0 if there is none
     */
    public long getCooldown(Entity entity, String cooldownKey){
        if (allCooldowns.get(cooldownKey).containsKey(entity)){
            return Math.max(allCooldowns.get(cooldownKey).get(entity) - System.currentTimeMillis(), 0);
        }
        return 0;
    }

    /**
     * Check if the entity can use something given a cooldown key.
     * @return false if the player has a cooldown on the item left, true otherwise
     */
    public boolean cooldownLowerThanZero(Entity entity, String cooldownKey){
        if (allCooldowns.get(cooldownKey).containsKey(entity)){
            if (allCooldowns.get(cooldownKey).get(entity) > System.currentTimeMillis()){
                return false;
            }
        }
        return true;
    }
}
