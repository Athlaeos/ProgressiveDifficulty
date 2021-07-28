package me.athlaeos.progressivelydifficultmobs.abilities;

import me.athlaeos.progressivelydifficultmobs.pojo.Ability;
import me.athlaeos.progressivelydifficultmobs.utils.Utils;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageEvent;

import java.util.Arrays;

public class AllDamageImmunityAbility extends Ability {
    public AllDamageImmunityAbility(){
        this.name = Utils.chat("&7&lImmunity: All Damage");
        this.description = Arrays.asList(
                Utils.chat("&7Monsters with this ability"),
                Utils.chat("&7will not be damaged."));
        this.icon = Material.DIAMOND_BLOCK;
        this.mobWhiteList = null;
    }


    @Override
    public void execute(Entity entity, Player player, Event event) {
        if (event instanceof EntityDamageEvent){
            EntityDamageEvent e = (EntityDamageEvent) event;
            e.setCancelled(true);
        }
    }
}
