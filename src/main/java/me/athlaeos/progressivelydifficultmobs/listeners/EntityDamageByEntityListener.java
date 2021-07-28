package me.athlaeos.progressivelydifficultmobs.listeners;

import me.athlaeos.progressivelydifficultmobs.items.CursedEnemyDropsEnableItem;
import me.athlaeos.progressivelydifficultmobs.main.Main;
import me.athlaeos.progressivelydifficultmobs.managers.*;
import me.athlaeos.progressivelydifficultmobs.perks.Perk;
import me.athlaeos.progressivelydifficultmobs.pojo.Ability;
import me.athlaeos.progressivelydifficultmobs.pojo.LeveledMonster;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public class EntityDamageByEntityListener implements Listener {

    private final NamespacedKey key = new NamespacedKey(Main.getInstance(), "pdm-monsterID");
    private final NamespacedKey monsterCursedKey = new NamespacedKey(Main.getInstance(), "pdm-curse");
    private final NamespacedKey enableCurseDropsItemKey = new NamespacedKey(Main.getInstance(), "pdm-enable_curse_drops");

    @EventHandler
    public void onEntityDamage(EntityDamageByEntityEvent e){
        LeveledMonsterManager monsterManager = LeveledMonsterManager.getInstance();
        AbilityManager abilityManager = AbilityManager.getInstance();
        if (!e.isCancelled()){
            String mobId = null;
            boolean ranged = false;
            Entity damager = e.getDamager();
            Entity defender = e.getEntity();
            if (damager instanceof Projectile){
                Projectile projectile = (Projectile) damager;
                if (projectile.getShooter() instanceof Entity){
                    damager = (Entity) projectile.getShooter();
                    ranged = true;
                }
            }

            PersistentDataContainer container = (defender instanceof Player) ? damager.getPersistentDataContainer() : defender.getPersistentDataContainer();

            if(container.has(key, PersistentDataType.STRING)) {
                mobId = container.get(key, PersistentDataType.STRING);
            } else {
                if (damager.getPersistentDataContainer().has(monsterCursedKey, PersistentDataType.STRING)){
                    e.setDamage(e.getDamage() * PluginConfigurationManager.getInstance().getCurseDamageMultiplier());
                }
            }
            LeveledMonster monster = monsterManager.getMonster(mobId);
            if (defender instanceof Player && !(damager instanceof Player)){ // player damaged event
                Player damagedPlayer = (Player) defender;
                PlayerPerksManager manager = PlayerPerksManager.getInstance();
                for (Perk perk : manager.sortPerksByPriority(manager.getPlayersTotalPerks(damagedPlayer))){
                    perk.execute(e);
                }
                if (monster != null){
                    for (String a : monster.getAbilities()){
                        Ability ability = abilityManager.getOnPlayerDamagedAbilities().get(a);
                        if (ability != null){
                            ability.execute(damager, damagedPlayer, e);
                        }
                    }
                    if (damager.getPersistentDataContainer().has(monsterCursedKey, PersistentDataType.STRING)){
                        if (monster.isBoss()){
                            e.setDamage(e.getDamage() * PluginConfigurationManager.getInstance().getCurseBossDamageMultiplier());
                        } else {
                            e.setDamage(e.getDamage() * PluginConfigurationManager.getInstance().getCurseDamageMultiplier());
                        }
                    }
                }
            } else if (damager instanceof Player && !(defender instanceof Player)){ // mob damaged event
                Player attackerPlayer = (Player) damager;
                PlayerPerksManager manager = PlayerPerksManager.getInstance();
                for (Perk perk : manager.sortPerksByPriority(manager.getPlayersTotalPerks(attackerPlayer))){
                    perk.execute(e);
                }
                if (CooldownManager.getInstance().getCooldown(attackerPlayer, "enable_curse_drops_duration") != 0){
                    CursedEnemyDropsEnableItem item = (CursedEnemyDropsEnableItem) ActiveItemManager.getInstance().getStandaloneActiveItems().get(enableCurseDropsItemKey);
                    if (ranged){
                        e.setDamage(e.getDamage() * item.getRangedDebuff());
                    } else {
                        e.setDamage(e.getDamage() * item.getMeleeDebuff());
                    }
                }
                if (monster != null){
                    for (String a : monster.getAbilities()){
                        Ability ability = abilityManager.getOnMobDamagedByPlayerAbilities().get(a);
                        if (ability != null){
                            ability.execute(defender, attackerPlayer, e);
                        }
                    }
                }
            }
        }
    }
}
