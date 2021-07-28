package me.athlaeos.progressivelydifficultmobs.perks.ondamagedperks;

import me.athlaeos.progressivelydifficultmobs.main.Main;
import me.athlaeos.progressivelydifficultmobs.managers.CooldownManager;
import me.athlaeos.progressivelydifficultmobs.managers.WorldguardManager;
import me.athlaeos.progressivelydifficultmobs.perks.Perk;
import me.athlaeos.progressivelydifficultmobs.perks.PerkTriggerPriority;
import me.athlaeos.progressivelydifficultmobs.utils.Utils;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityPotionEffectEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class ResilientPerk extends Perk {
    private int cooldown;
    private int duration;
    private int amplifier;
    public ResilientPerk(){
        this.id = 2048;
        this.icon = Material.IRON_BLOCK;
        this.perkPriority = PerkTriggerPriority.SOONER;
        this.description = Utils.seperateStringIntoLines(Utils.chat("&7Players with this perk" +
                " receive a resistance buff upon being damaged by an entity"), 36, "&7");
        this.name = "resilient";
        this.displayName = Utils.chat(config.getString("perks." + name + ".display_name"));
        this.cooldown = config.getInt("perks." + name + ".cooldown");
        this.duration = config.getInt("perks." + name + ".duration");
        this.amplifier = config.getInt("perks." + name + ".amplifier");
    }

    @Override
    public void execute(Event e) {
        if (e instanceof EntityDamageByEntityEvent){
            EntityDamageByEntityEvent event = (EntityDamageByEntityEvent) e;
            if (!event.isCancelled()){
                Entity defender = event.getEntity();
                if (WorldguardManager.getWorldguardManager().isLocationInRegionWithFlag(defender.getLocation(), "pdm-resilient-deny")
                        || WorldguardManager.getWorldguardManager().isLocationInRegionWithFlag(defender.getLocation(), "pdm-perks-deny-all")) {
                    return;
                }

                if (defender instanceof Player){
                    Player p = (Player) defender;
                    if (p.hasPotionEffect(PotionEffectType.DAMAGE_RESISTANCE)){
                        if (p.getPotionEffect(PotionEffectType.DAMAGE_RESISTANCE).getAmplifier() > amplifier){
                            return;
                        }
                    }
                    if (CooldownManager.getInstance().cooldownLowerThanZero(p, "perks_resilient_cooldown")){
                        PotionEffect effect = new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, duration, amplifier);
                        EntityPotionEffectEvent potionEvent = new EntityPotionEffectEvent(p, null, effect, EntityPotionEffectEvent.Cause.PLUGIN, EntityPotionEffectEvent.Action.ADDED, true);
                        Main.getInstance().getServer().getPluginManager().callEvent(potionEvent);
                        if (!potionEvent.isCancelled()){
                            p.addPotionEffect(effect);
                            CooldownManager.getInstance().setCooldown(p, cooldown * 1000, "perks_resilient_cooldown");
                        }
                    }
                }
            }
        }
    }
}
