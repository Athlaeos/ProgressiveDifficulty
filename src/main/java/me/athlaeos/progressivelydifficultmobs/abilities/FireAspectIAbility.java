package me.athlaeos.progressivelydifficultmobs.abilities;

import me.athlaeos.progressivelydifficultmobs.pojo.Ability;
import me.athlaeos.progressivelydifficultmobs.utils.Utils;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

import java.util.Arrays;

public class FireAspectIAbility extends Ability {
    public FireAspectIAbility(){
        this.name = Utils.chat("&6&lFire Aspect I");
        this.description = Arrays.asList(
                Utils.chat("&7Monsters with this ability"),
                Utils.chat("&7will light the player hit"),
                Utils.chat("&7on fire for 4 seconds."));
        this.icon = Material.FLINT_AND_STEEL;
        this.mobWhiteList = null;
    }

    @Override
    public void execute(Entity entity, Player player, Event event) {
        player.setFireTicks(80);
    }
}
