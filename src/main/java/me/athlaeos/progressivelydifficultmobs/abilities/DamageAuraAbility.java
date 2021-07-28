package me.athlaeos.progressivelydifficultmobs.abilities;

import me.athlaeos.progressivelydifficultmobs.filters.PlayerFilter;
import me.athlaeos.progressivelydifficultmobs.main.Main;
import me.athlaeos.progressivelydifficultmobs.managers.PluginConfigurationManager;
import me.athlaeos.progressivelydifficultmobs.pojo.Ability;
import me.athlaeos.progressivelydifficultmobs.utils.Utils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DamageAuraAbility extends Ability {

    public DamageAuraAbility(){
        this.name = Utils.chat("&6&lDamage aura");
        this.description = Arrays.asList(
                Utils.chat("&7Deals 1 point of true damage"),
                Utils.chat("&7to every player in a 4 block"),
                Utils.chat("&7radius."),
                Utils.chat("&cWarning: should be used sparingly"),
                Utils.chat("&cto prevent particle lag."));
        this.icon = Material.MAGMA_CREAM;
        this.mobWhiteList = null;
    }

    @Override
    public void execute(Entity entity, Player player, Event event) {
        final boolean useAnimations = PluginConfigurationManager.getInstance().useAnimationParticles();
        new BukkitRunnable(){
            final int radius = 4;
            @Override
            public void run() {
                if (entity.isDead()){
                    cancel();
                } else {
                    if (useAnimations){
                        for (Location l : Utils.getCircle(entity.getLocation(), radius, 32)){
                            entity.getWorld().spawnParticle(Particle.FLAME, l, 0,
                                    0,
                                    0.5,
                                    0,
                                    0.1, null, false);
                        }
                    }
                    for (Entity p : entity.getWorld().getNearbyEntities(entity.getLocation(), radius, radius, radius, new PlayerFilter<>())){
                        if (p instanceof Player){
                            Main.getInstance().getServer().getPluginManager().callEvent(new
                                    EntityDamageByEntityEvent(entity, p, EntityDamageEvent.DamageCause.ENTITY_ATTACK, 1));
                            ((Player) p).damage(1);
                        }
                    }
                }
            }
        }.runTaskTimer(Main.getInstance(), 0, 20);
    }
}
