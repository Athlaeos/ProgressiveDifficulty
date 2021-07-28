package me.athlaeos.progressivelydifficultmobs.abilities;

import me.athlaeos.progressivelydifficultmobs.pojo.Ability;
import me.athlaeos.progressivelydifficultmobs.utils.Utils;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageEvent;

import java.util.Arrays;

public class MeleeImmunityAbility extends Ability {
    public MeleeImmunityAbility(){
        this.name = Utils.chat("&7&lImmunity: Melee Damage");
        this.description = Arrays.asList(
                Utils.chat("&7Monsters with this ability"),
                Utils.chat("&7will not be damaged by"),
                Utils.chat("&7melee hits."));
        this.icon = Material.IRON_CHESTPLATE;
        this.mobWhiteList = null;
    }


    @Override
    public void execute(Entity entity, Player player, Event event) {
        if (event instanceof EntityDamageEvent){
            EntityDamageEvent e = (EntityDamageEvent) event;
            if (e.getCause() == EntityDamageEvent.DamageCause.ENTITY_ATTACK || e.getCause() == EntityDamageEvent.DamageCause.ENTITY_SWEEP_ATTACK){
                e.setCancelled(true);
            }
        }
    }
}
