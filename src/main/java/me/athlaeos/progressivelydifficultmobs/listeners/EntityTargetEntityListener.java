package me.athlaeos.progressivelydifficultmobs.listeners;

import me.athlaeos.progressivelydifficultmobs.main.Main;
import me.athlaeos.progressivelydifficultmobs.managers.AbilityManager;
import me.athlaeos.progressivelydifficultmobs.managers.LeveledMonsterManager;
import me.athlaeos.progressivelydifficultmobs.managers.PlayerPerksManager;
import me.athlaeos.progressivelydifficultmobs.perks.Perk;
import me.athlaeos.progressivelydifficultmobs.pojo.Ability;
import me.athlaeos.progressivelydifficultmobs.pojo.LeveledMonster;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.IronGolem;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowman;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;
import org.bukkit.persistence.PersistentDataType;

public class EntityTargetEntityListener implements Listener {

    private final NamespacedKey monsterKey = new NamespacedKey(Main.getInstance(), "pdm-monsterID");

    @EventHandler
    public void onMonsterTarget(EntityTargetLivingEntityEvent e){
        if (!e.isCancelled()){
            if (e.getTarget() instanceof Player){
                PlayerPerksManager manager = PlayerPerksManager.getInstance();
                for (Perk perk : manager.sortPerksByPriority(manager.getPlayersTotalPerks((Player) e.getTarget()))){
                    perk.execute(e);
                }
            }
            if (e.getEntity() instanceof IronGolem || e.getEntity() instanceof Snowman){
                if (e.getTarget() != null){
                    if (e.getTarget().getPersistentDataContainer().has(monsterKey, PersistentDataType.STRING)){
                        LeveledMonster monster = LeveledMonsterManager.getInstance().getMonster(e.getTarget().getPersistentDataContainer().get(monsterKey, PersistentDataType.STRING));
                        if (monster != null){
                            if (monster.getAbilities().contains("on_target_peaceful_villages")){
                                e.setCancelled(true);
                            }
                        }
                    }
                }
            }
            if (e.getEntity().getPersistentDataContainer().has(monsterKey, PersistentDataType.STRING)){
                LeveledMonster monster = LeveledMonsterManager.getInstance().getMonster(e.getEntity().getPersistentDataContainer().get(monsterKey, PersistentDataType.STRING));
                if (monster != null){
                    for (String a : monster.getAbilities()){
                        Ability ability = AbilityManager.getInstance().getOnTargetEntityAbilities().get(a);
                        if (ability != null){
                            ability.execute(e.getEntity(), null, e);
                        }
                    }
                }
            }
        }
    }
}
