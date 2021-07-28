package me.athlaeos.progressivelydifficultmobs.abilities;

import me.athlaeos.progressivelydifficultmobs.main.Main;
import me.athlaeos.progressivelydifficultmobs.pojo.Ability;
import me.athlaeos.progressivelydifficultmobs.utils.Utils;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.*;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;

import java.util.ArrayList;
import java.util.Arrays;

public class PeacefulToVillagersAbility extends Ability {
    public PeacefulToVillagersAbility(){
        this.name = Utils.chat("&7&lIgnore Villages");
        this.description = Arrays.asList(
                Utils.chat("&7Monsters with this ability will"),
                Utils.chat("&7ignore villagers and iron golems."),
                Utils.chat("&7Iron golems will ignore these monsters."));
        this.icon = Material.EMERALD;
        this.mobWhiteList = null;
    }

    private void addWhitelistedMonster(String entityType){
        try {
            EntityType type = EntityType.valueOf(entityType);
            mobWhiteList.add(type);
        } catch (IllegalArgumentException e){
        }
    }

    @Override
    public void execute(Entity entity, Player player, Event event) {
        if (event instanceof EntityTargetLivingEntityEvent) {
            EntityTargetLivingEntityEvent e = (EntityTargetLivingEntityEvent) event;
            if (!e.isCancelled()){
                if (e.getTarget() instanceof Villager || e.getTarget() instanceof IronGolem){
                    e.setCancelled(true);
                }
            }
        }
    }
}
