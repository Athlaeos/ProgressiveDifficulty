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

public class LungeTowardsPlayerAbility extends Ability {
    public LungeTowardsPlayerAbility(){
        this.name = Utils.chat("&7&lLeap");
        this.description = Arrays.asList(
                Utils.chat("&7Monsters with this ability"),
                Utils.chat("&7will leap towards their target"),
                Utils.chat("&7when the player is near."),
                Utils.chat("&77 second cooldown."));
        this.icon = Material.FEATHER;
        this.mobWhiteList = null;
    }


    @Override
    public void execute(Entity entity, Player player, Event event) {
        new BukkitRunnable(){
            int cooldownTimer = 0;
            @Override
            public void run() {
                if (!entity.isDead()){
                    Location eLoc = entity.getLocation();
                    if (cooldownTimer == 0){
                        Player nearestPlayer = Utils.getNearestPlayer(entity.getLocation());

                        Location pLoc = null;
                        if (nearestPlayer != null){
                            pLoc = nearestPlayer.getLocation();
                        }

                        if (pLoc != null){
                            double distance = Math.sqrt(Math.pow(eLoc.getX() - pLoc.getX(), 2) + Math.pow(eLoc.getY() - pLoc.getY(), 2) + Math.pow(eLoc.getZ() - pLoc.getZ(), 2));

                            if (distance <= 8){
                                Vector leap = new Vector(pLoc.getX() - eLoc.getX(), pLoc.getY() - eLoc.getY() + 1,  pLoc.getZ() - eLoc.getZ());
                                leap.multiply(0.25);
                                entity.setVelocity(leap);
                                cooldownTimer = 7;
                            }
                        }
                    }
                    if (cooldownTimer > 0){
                        cooldownTimer--;
                    }
                } else {
                    cancel();
                }
            }
        }.runTaskTimer(Main.getInstance(), 100, 20);
    }
}
