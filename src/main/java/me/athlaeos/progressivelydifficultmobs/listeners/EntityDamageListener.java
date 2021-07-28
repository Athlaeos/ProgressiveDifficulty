package me.athlaeos.progressivelydifficultmobs.listeners;

import me.athlaeos.progressivelydifficultmobs.main.Main;
import me.athlaeos.progressivelydifficultmobs.managers.AbilityManager;
import me.athlaeos.progressivelydifficultmobs.managers.LeveledMonsterManager;
import me.athlaeos.progressivelydifficultmobs.managers.PlayerPerksManager;
import me.athlaeos.progressivelydifficultmobs.perks.Perk;
import me.athlaeos.progressivelydifficultmobs.pojo.Ability;
import me.athlaeos.progressivelydifficultmobs.pojo.LeveledMonster;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.persistence.PersistentDataType;

public class EntityDamageListener implements Listener {
    private final NamespacedKey monsterKey = new NamespacedKey(Main.getInstance(), "pdm-monsterID");

    @EventHandler (priority = EventPriority.MONITOR)
    public void onEntityDamage(EntityDamageEvent e){
        if (!e.isCancelled()){
            if (e.getEntity() instanceof Player){
                PlayerPerksManager manager = PlayerPerksManager.getInstance();
                for (Perk perk : manager.sortPerksByPriority(manager.getPlayersTotalPerks((Player) e.getEntity()))){
                    perk.execute(e);
                }
            }
            if (e.getEntity().getPersistentDataContainer().has(monsterKey, PersistentDataType.STRING)){
                LeveledMonster monster = LeveledMonsterManager.getInstance().getMonster(e.getEntity().getPersistentDataContainer().get(monsterKey, PersistentDataType.STRING));
                if (monster != null){
                    for (String a : monster.getAbilities()){
                        Ability ability = AbilityManager.getInstance().getOnMobDamagedAbilities().get(a);
                        if (ability != null){
                            ability.execute(e.getEntity(), null, e);
                        }
                    }
                }
            }
        }
    }
}
