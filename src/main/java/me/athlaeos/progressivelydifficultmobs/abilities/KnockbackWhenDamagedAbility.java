package me.athlaeos.progressivelydifficultmobs.abilities;

import me.athlaeos.progressivelydifficultmobs.pojo.Ability;
import me.athlaeos.progressivelydifficultmobs.utils.Utils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.util.Vector;

import java.util.Arrays;

public class KnockbackWhenDamagedAbility extends Ability {
    public KnockbackWhenDamagedAbility(){
        this.name = Utils.chat("&7&lKnockback");
        this.description = Arrays.asList(
                Utils.chat("&7Monsters with this ability"),
                Utils.chat("&7will knock the player back"),
                Utils.chat("&7when they get damaged"));
        this.icon = Material.PISTON;
        this.mobWhiteList = null;
    }


    @Override
    public void execute(Entity entity, Player player, Event event) {
        Location eLoc = entity.getLocation();
        Location pLoc = player.getLocation();
        Vector throwBack = new Vector(pLoc.getX() - eLoc.getX(), pLoc.getY() - eLoc.getY() + 2, pLoc.getZ() - eLoc.getZ());
        throwBack.multiply(0.2);
        player.setVelocity(throwBack);
    }
}
