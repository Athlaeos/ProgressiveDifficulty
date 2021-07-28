package me.athlaeos.progressivelydifficultmobs.abilities;

import me.athlaeos.progressivelydifficultmobs.main.Main;
import me.athlaeos.progressivelydifficultmobs.pojo.Ability;
import me.athlaeos.progressivelydifficultmobs.utils.Utils;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;
import org.bukkit.persistence.PersistentDataType;

import java.util.Arrays;

public class PeacefulUnconditionalAbility extends Ability {
    private final NamespacedKey key = new NamespacedKey(Main.getInstance(), "pdm-karma");
    private final NamespacedKey cursedKey = new NamespacedKey(Main.getInstance(), "pdm-curse");

    public PeacefulUnconditionalAbility(){
        this.name = Utils.chat("&a&lPeaceful unconditional");
        this.description = Arrays.asList(
                Utils.chat("&7Non-cursed monsters with this ability"),
                Utils.chat("&7will not go aggressive to players"),
                Utils.chat("&7regardless of karma level."));
        this.icon = Material.ROSE_BUSH;
        this.mobWhiteList = null;
    }

    @Override
    public void execute(Entity entity, Player player, Event event) {
        if (event instanceof EntityTargetLivingEntityEvent){
            EntityTargetLivingEntityEvent e = (EntityTargetLivingEntityEvent) event;
            if (!e.isCancelled()){
                if (e.getTarget() instanceof Player){
                    if (!e.getEntity().getPersistentDataContainer().has(cursedKey, PersistentDataType.STRING)){
                        e.setCancelled(true);
                    }
                }
            }
        }
    }
}
