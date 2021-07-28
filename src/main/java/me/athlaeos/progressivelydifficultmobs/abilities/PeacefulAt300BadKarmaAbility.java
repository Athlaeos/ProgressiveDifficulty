package me.athlaeos.progressivelydifficultmobs.abilities;

import me.athlaeos.progressivelydifficultmobs.main.Main;
import me.athlaeos.progressivelydifficultmobs.pojo.Ability;
import me.athlaeos.progressivelydifficultmobs.utils.Utils;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;
import org.bukkit.persistence.PersistentDataType;

import java.util.Arrays;
import java.util.List;

public class PeacefulAt300BadKarmaAbility extends Ability {
    private final NamespacedKey key = new NamespacedKey(Main.getInstance(), "pdm-karma");
    private final NamespacedKey cursedKey = new NamespacedKey(Main.getInstance(), "pdm-curse");

    public PeacefulAt300BadKarmaAbility(){
        this.name = Utils.chat("&c&lPeaceful to -300 or lower karma");
        this.description = Arrays.asList(
                Utils.chat("&7Non-cursed monsters with this ability"),
                Utils.chat("&7will not go aggressive to players"),
                Utils.chat("&7with &c-300&7 or less karma (bad karma)"));
        this.icon = Material.WITHER_ROSE;
        this.mobWhiteList = null;
    }

    @Override
    public void execute(Entity entity, Player player, Event event) {
        if (event instanceof EntityTargetLivingEntityEvent){
            EntityTargetLivingEntityEvent e = (EntityTargetLivingEntityEvent) event;
            if (!e.isCancelled()){
                if (e.getTarget() instanceof Player){
                    Player p = (Player) e.getTarget();
                    if (p.getPersistentDataContainer().has(key, PersistentDataType.DOUBLE)){
                        if (p.getPersistentDataContainer().get(key, PersistentDataType.DOUBLE) <= -300){
                            if (!e.getEntity().getPersistentDataContainer().has(cursedKey, PersistentDataType.STRING)){
                                e.setCancelled(true);
                            }
                        }
                    }
                }
            }
        }
    }
}
