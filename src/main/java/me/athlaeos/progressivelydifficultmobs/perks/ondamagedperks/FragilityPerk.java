package me.athlaeos.progressivelydifficultmobs.perks.ondamagedperks;

import me.athlaeos.progressivelydifficultmobs.managers.WorldguardManager;
import me.athlaeos.progressivelydifficultmobs.perks.Perk;
import me.athlaeos.progressivelydifficultmobs.perks.PerkTriggerPriority;
import me.athlaeos.progressivelydifficultmobs.utils.Utils;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

public class FragilityPerk extends Perk {
    private double damageMultiplier;
    private boolean includePlayers;
    public FragilityPerk(double damageMultiplier, int id){
        this.id = id;
        this.icon = Material.GLASS;
        this.perkPriority = PerkTriggerPriority.SOONEST;
        this.description = Utils.seperateStringIntoLines(Utils.chat("&7Players with this perk" +
                " have their damage taken increased by " + ((damageMultiplier) * 100) + "%"), 36, "&7");
        this.name = "damage_taken";
        this.damageMultiplier = damageMultiplier;
        this.displayName = Utils.chat(config.getString("perks." + name + ".display_name").replace("{increase}", "" + ((damageMultiplier) * 100)));
        this.includePlayers = config.getBoolean("perks." + name + ".include_players");
    }

    @Override
    public void execute(Event e) {
        if (e instanceof EntityDamageByEntityEvent){
            EntityDamageByEntityEvent event = (EntityDamageByEntityEvent) e;
            if (!event.isCancelled()){
                Entity damager = event.getDamager();
                Entity defender = event.getEntity();
                if (WorldguardManager.getWorldguardManager().isLocationInRegionWithFlag(damager.getLocation(), "pdm-damage-mitigations-nerf-deny")
                        || WorldguardManager.getWorldguardManager().isLocationInRegionWithFlag(damager.getLocation(), "pdm-perks-deny-all")) {
                    return;
                }
                if (damager instanceof Projectile){
                    Projectile projectile = (Projectile) damager;
                    if (projectile.getShooter() instanceof Entity){
                        damager = (Entity) projectile.getShooter();
                    }
                }

                if (defender instanceof Player){
                    if (!includePlayers && damager instanceof Player){
                        return;
                    }

                    event.setDamage(event.getDamage() * (1D + damageMultiplier));
                }
            }
        } else if (e instanceof EntityDamageEvent){
            EntityDamageEvent event = (EntityDamageEvent) e;
            if (!event.isCancelled()){
                event.setDamage(event.getDamage() * (1D + damageMultiplier));
            }
        }
    }
}
