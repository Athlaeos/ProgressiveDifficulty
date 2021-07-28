package me.athlaeos.progressivelydifficultmobs.abilities;

import me.athlaeos.progressivelydifficultmobs.filters.PlayerFilter;
import me.athlaeos.progressivelydifficultmobs.main.Main;
import me.athlaeos.progressivelydifficultmobs.pojo.Ability;
import me.athlaeos.progressivelydifficultmobs.utils.Utils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.Arrays;

public class PullWhenDamagedAbility extends Ability {
    public PullWhenDamagedAbility(){
        this.name = Utils.chat("&7&lPull");
        this.description = Arrays.asList(
                Utils.chat("&7Monsters with this ability"),
                Utils.chat("&7will pull the player closer"),
                Utils.chat("&7when they get damaged."),
                Utils.chat("&7Overrides Knockback"));
        this.icon = Material.STICKY_PISTON;
        this.mobWhiteList = null;
    }


    @Override
    public void execute(Entity entity, Player player, Event event) {
        Location eLoc = entity.getLocation();
        Location pLoc = player.getLocation();
        Vector throwBack = new Vector(eLoc.getX() - pLoc.getX(), eLoc.getY() - pLoc.getY() + 1, eLoc.getZ() - pLoc.getZ());
        throwBack.multiply(0.125);
        player.setVelocity(throwBack);
    }
}
