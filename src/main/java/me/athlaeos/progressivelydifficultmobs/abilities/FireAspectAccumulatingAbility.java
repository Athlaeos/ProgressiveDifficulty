package me.athlaeos.progressivelydifficultmobs.abilities;

import me.athlaeos.progressivelydifficultmobs.pojo.Ability;
import me.athlaeos.progressivelydifficultmobs.utils.Utils;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

import java.util.Arrays;

public class FireAspectAccumulatingAbility extends Ability {
    public FireAspectAccumulatingAbility(){
        this.name = Utils.chat("&6&lFire Aspect : Accumulating");
        this.description = Arrays.asList(
                Utils.chat("&7Monsters with this ability"),
                Utils.chat("&7will light the player hit"),
                Utils.chat("&7on fire for 2 seconds,"),
                Utils.chat("&7but every successive hit adds"),
                Utils.chat("&7another 2 seconds of being on"),
                Utils.chat("&7fire."));
        this.icon = Material.LAVA_BUCKET;
        this.mobWhiteList = null;
    }

    @Override
    public void execute(Entity entity, Player player, Event event) {
        player.setFireTicks(player.getFireTicks() + 40);
    }
}
