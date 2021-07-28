package me.athlaeos.progressivelydifficultmobs.abilities;

import me.athlaeos.progressivelydifficultmobs.pojo.Ability;
import me.athlaeos.progressivelydifficultmobs.utils.Utils;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.Arrays;

public class InstantDeathAbility extends Ability {
    public InstantDeathAbility(){
        this.name = Utils.chat("&4&lInstant Death");
        this.description = Arrays.asList(
                Utils.chat("&7Monsters with this ability"),
                Utils.chat("&7will instantly kill the player."));
        this.icon = Material.SKELETON_SKULL;
        this.mobWhiteList = null;
    }

    @Override
    public void execute(Entity entity, Player player, Event event) {
        EntityDamageByEntityEvent e = (EntityDamageByEntityEvent) event;
        e.setDamage(32767D);
    }
}
