package me.athlaeos.progressivelydifficultmobs.perks.ondamagedperks;

import me.athlaeos.progressivelydifficultmobs.managers.CooldownManager;
import me.athlaeos.progressivelydifficultmobs.managers.PluginConfigurationManager;
import me.athlaeos.progressivelydifficultmobs.managers.WorldguardManager;
import me.athlaeos.progressivelydifficultmobs.perks.Perk;
import me.athlaeos.progressivelydifficultmobs.perks.PerkTriggerPriority;
import me.athlaeos.progressivelydifficultmobs.utils.Utils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;

public class RevivePerk extends Perk {
    private int reviveCooldown;
    private int resistanceAmplifier;
    private int resistanceDuration;
    private double healthScale;

    public RevivePerk(){
        this.id = 1004;
        this.icon = Material.BLAZE_POWDER;
        this.perkPriority = PerkTriggerPriority.LATEST;
        this.description = Utils.seperateStringIntoLines(Utils.chat("&7Whenever a player with this" +
                " perk dies, they're revived with some of their HP as well as with a short" +
                " resistance buff. Has a cooldown"), 36, "&7");
        this.name = "revive";
        this.displayName = Utils.chat(config.getString("perks." + name + ".display_name"));
        this.reviveCooldown = config.getInt("perks." + name + ".cooldown");
        this.resistanceAmplifier = config.getInt("perks." + name + ".resistance_amplifier");
        this.resistanceDuration = config.getInt("perks." + name + ".resistance_duration");
        this.healthScale = config.getDouble("perks." + name + ".health_scale");
        if (healthScale < 0 || healthScale > 1) healthScale = 0.5;
    }

    @Override
    public void execute(Event e) {
        if (e instanceof EntityDamageEvent){
            EntityDamageEvent event = (EntityDamageEvent) e;
            if (!event.isCancelled()){
                if (event.getEntity() instanceof Player){
                    Player p = (Player) event.getEntity();
                    if (WorldguardManager.getWorldguardManager().isLocationInRegionWithFlag(event.getEntity().getLocation(), "pdm-perk-revive-deny")
                            || WorldguardManager.getWorldguardManager().isLocationInRegionWithFlag(event.getEntity().getLocation(), "pdm-perks-deny-all")) {
                        return;
                    }
                    if (p.getHealth() - event.getFinalDamage() <= 0){
                        if (CooldownManager.getInstance().cooldownLowerThanZero(p, "perks_revive_cooldown")){
                            p.setHealth(p.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue() * healthScale);
                            p.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, resistanceDuration, resistanceAmplifier));
                            event.setCancelled(true);
                            p.damage(0);
                            p.getWorld().playSound(p.getLocation(), Sound.ITEM_TOTEM_USE, 1F, 1F);
                            if (PluginConfigurationManager.getInstance().useAnimationParticles()){
                                List<Location> particles = Utils.transformPoints(p.getLocation(), Utils.getSquareWithLines(p.getLocation(), 16, 2), 45, 0, 0, 1);
                                particles.addAll(Utils.transformPoints(p.getLocation(), Utils.getSquareWithLines(p.getLocation(), 8, 0.75), 45, 0, 0, 1));
                                for (Location l : particles){
                                    p.getWorld().spawnParticle(Particle.FLAME, l, 0, 0, 0.02, 0);
                                }
                                p.getWorld().spawnParticle(Particle.LAVA, p.getLocation(), 15);
                            }
                            CooldownManager.getInstance().setCooldown(p, reviveCooldown * 1000, "perks_revive_cooldown");
                        }
                    }
                }
            }
        }
    }
}
