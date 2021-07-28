package me.athlaeos.progressivelydifficultmobs.abilities;

import me.athlaeos.progressivelydifficultmobs.pojo.Ability;
import me.athlaeos.progressivelydifficultmobs.utils.Utils;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageEvent;

import java.util.Arrays;

public class FallDamageImmunityAbility extends Ability {
    public FallDamageImmunityAbility(){
        this.name = Utils.chat("&7&lImmunity: Fall Damage");
        this.description = Arrays.asList(
                Utils.chat("&7Monsters with this ability"),
                Utils.chat("&7will not be damaged by"),
                Utils.chat("&7falling long distances."));
        this.icon = Material.ELYTRA;
        this.mobWhiteList = null;
    }


    @Override
    public void execute(Entity entity, Player player, Event event) {
        if (event instanceof EntityDamageEvent){
            EntityDamageEvent e = (EntityDamageEvent) event;
            if (e.getCause() == EntityDamageEvent.DamageCause.FALL){
                e.setCancelled(true);
            }
        }
    }
}
