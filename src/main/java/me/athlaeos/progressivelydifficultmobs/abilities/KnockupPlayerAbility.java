package me.athlaeos.progressivelydifficultmobs.abilities;

import me.athlaeos.progressivelydifficultmobs.main.Main;
import me.athlaeos.progressivelydifficultmobs.pojo.Ability;
import me.athlaeos.progressivelydifficultmobs.utils.Utils;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.Arrays;

public class KnockupPlayerAbility extends Ability {
    public KnockupPlayerAbility(){
        this.name = Utils.chat("&7&lKnockup");
        this.description = Arrays.asList(
                Utils.chat("&7Monsters with this ability"),
                Utils.chat("&7will knock players up in the"),
                Utils.chat("&7air when hit"));
        this.icon = Material.IRON_INGOT;
        this.mobWhiteList = null;
    }

    @Override
    public void execute(Entity entity, Player player, Event event) {
        new BukkitRunnable(){
            @Override
            public void run() {
                player.setVelocity(new Vector(0, 1, 0));
            }
        }.runTaskLater(Main.getInstance(), 1);
    }
}
