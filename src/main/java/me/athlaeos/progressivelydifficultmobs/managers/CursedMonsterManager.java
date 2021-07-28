package me.athlaeos.progressivelydifficultmobs.managers;

import me.athlaeos.progressivelydifficultmobs.main.Main;
import me.athlaeos.progressivelydifficultmobs.pojo.LeveledMonster;
import org.bukkit.Color;
import org.bukkit.NamespacedKey;
import org.bukkit.Particle;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Boss;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashSet;
import java.util.Set;

public class CursedMonsterManager {
    private static CursedMonsterManager manager = null;
    private final NamespacedKey monsterCursedKey = new NamespacedKey(Main.getInstance(), "pdm-curse");
    private Set<Entity> cursedMonsters = new HashSet<>();
    private BukkitTask particleRunnable;

    public CursedMonsterManager() {
        if (PluginConfigurationManager.getInstance().useAnimationRunnables()){
            if (PluginConfigurationManager.getInstance().useAnimationParticles()){
                particleRunnable = new BukkitRunnable() {
                    @Override
                    public void run() {
                        Set<Entity> allCursedEntities = new HashSet<>(cursedMonsters);
                        for (Entity e : allCursedEntities){
                            if (e.isDead()){
                                cursedMonsters.remove(e);
                                continue;
                            }
                            double monsterSizeRadius = e.getWidth()/2;
                            double monsterHeight = e.getHeight()/2;
                            Particle.DustOptions dustOptions = new Particle.DustOptions(Color.fromRGB(242, 13, 5), 2);
                            e.getWorld().spawnParticle(Particle.REDSTONE, e.getLocation(), 2, monsterSizeRadius, monsterHeight, monsterSizeRadius, 0, dustOptions);
                            dustOptions = new Particle.DustOptions(Color.fromRGB(0, 0, 0), 2);
                            e.getWorld().spawnParticle(Particle.REDSTONE, e.getLocation(), 2, monsterSizeRadius, monsterHeight, monsterSizeRadius, 0, dustOptions);
                        }
                    }
                }.runTaskTimer(Main.getInstance(), 0, 20);
            }
        }
    }

    /**
     * Curses a monster, multiplying their health and adds them to a list of cursed entities
     * Cursed entities in this list also have their damage multiplied
     * @param mob the LivingEntity monster to be cursed
     * @param leveledMonster may be null, used to determine if the mob is a boss monster. If null, it's assumed the monster is vanilla
     */
    public void curseMonster(LivingEntity mob, LeveledMonster leveledMonster){
        if (mob.getPersistentDataContainer().has(monsterCursedKey, PersistentDataType.STRING)) {
            return;
        }
        mob.getPersistentDataContainer().set(monsterCursedKey, PersistentDataType.STRING, "cursed");
        if (leveledMonster != null){
            if (leveledMonster.isBoss() || mob instanceof Boss){
                mob.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(mob.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue() * PluginConfigurationManager.getInstance().getCurseBossHealthMultiplier());
                mob.setHealth(mob.getHealth() * PluginConfigurationManager.getInstance().getCurseBossHealthMultiplier());
                addCursedEntity(mob);
                return;
            }
        }
        if (mob instanceof Boss){
            mob.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(mob.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue() * PluginConfigurationManager.getInstance().getCurseBossHealthMultiplier());
        } else {
            mob.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(mob.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue() * PluginConfigurationManager.getInstance().getCurseHealthMultiplier());
        }
        mob.setHealth(mob.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue());
        addCursedEntity(mob);
    }

    /**
     * Removes curse from a monster, decreasing their health and removing them from the list of cursed monsters.
     * By being removed from this list they also deal less damage.
     * @param mob the cursed monster to be removed curse from
     * @param leveledMonster may be null, used to determine if the mob is a boss monster. If null, it's assumed the monster is vanilla
     */
    public void unCurseMonster(LivingEntity mob, LeveledMonster leveledMonster){
        if (!mob.getPersistentDataContainer().has(monsterCursedKey, PersistentDataType.STRING)){
            return;
        }
        mob.getPersistentDataContainer().remove(monsterCursedKey);
        if (leveledMonster != null){
            if (leveledMonster.isBoss() || mob instanceof Boss){
                mob.setHealth(mob.getHealth() / PluginConfigurationManager.getInstance().getCurseBossHealthMultiplier());
                mob.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(mob.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue() / PluginConfigurationManager.getInstance().getCurseBossHealthMultiplier());
                removeCursedEntity(mob);
                return;
            }
        } else {
            if (mob instanceof Boss){
                mob.setHealth(mob.getHealth() / PluginConfigurationManager.getInstance().getCurseBossHealthMultiplier());
                mob.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(mob.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue() / PluginConfigurationManager.getInstance().getCurseBossHealthMultiplier());
            } else {
                mob.setHealth(mob.getHealth() / PluginConfigurationManager.getInstance().getCurseHealthMultiplier());
                mob.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(mob.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue() / PluginConfigurationManager.getInstance().getCurseHealthMultiplier());
            }
        }
        removeCursedEntity(mob);
    }

    public static CursedMonsterManager getInstance() {
        if (manager == null) {
            manager = new CursedMonsterManager();
        }
        return manager;
    }

    public void reload(){
        if (particleRunnable != null){
            particleRunnable.cancel();
        }
        manager = null;
    }

    /**
     * Adds an entity to the list of cursed entities, causing them to emit black smoke,
     * do more damage, and stops them from dropping items when killed.
     * This will not buff their HP, this would have to be done manually.
     * @param e
     */
    public void addCursedEntity(Entity e){
        cursedMonsters.add(e);
    }

    /**
     * Removes an entity from the list of cursed entities
     * @param e
     */
    public void removeCursedEntity(Entity e){
        cursedMonsters.remove(e);
    }
}
