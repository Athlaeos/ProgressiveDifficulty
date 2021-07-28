package me.athlaeos.progressivelydifficultmobs.perks.onpotioneffectperks;

import me.athlaeos.progressivelydifficultmobs.managers.CooldownManager;
import me.athlaeos.progressivelydifficultmobs.managers.WorldguardManager;
import me.athlaeos.progressivelydifficultmobs.perks.Perk;
import me.athlaeos.progressivelydifficultmobs.perks.PerkTriggerPriority;
import me.athlaeos.progressivelydifficultmobs.utils.Utils;
import org.bukkit.Material;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityPotionEffectEvent;
import org.bukkit.potion.PotionEffectType;

public class SlowImmunityPerk extends Perk {
    private int cooldown;
    private int duration;

    public SlowImmunityPerk(){
        this.id = 1044;
        this.icon = Material.MILK_BUCKET;
        this.perkPriority = PerkTriggerPriority.LATER;
        this.description = Utils.seperateStringIntoLines(Utils.chat("&7Players with this perk" +
                " are cleansed of slowness when afflicted by it, and are immune for an additional duration. Has a cooldown"), 36, "&7");
        this.name = "slow_immunity";
        this.displayName = Utils.chat(config.getString("perks." + name + ".display_name"));
        this.cooldown = config.getInt("perks." + name + ".cooldown");
        this.duration = config.getInt("perks." + name + ".duration");
    }

    @Override
    public void execute(Event e) {
        if (e instanceof EntityPotionEffectEvent){
            EntityPotionEffectEvent event = (EntityPotionEffectEvent) e;
            if (!event.isCancelled()){
                if (WorldguardManager.getWorldguardManager().isLocationInRegionWithFlag(event.getEntity().getLocation(), "pdm-immunity-slow-deny")
                        || WorldguardManager.getWorldguardManager().isLocationInRegionWithFlag(event.getEntity().getLocation(), "pdm-perks-deny-all")) {
                    return;
                }
                if (event.getModifiedType().equals(PotionEffectType.SLOW)){
                    CooldownManager manager = CooldownManager.getInstance();
                    if (!manager.cooldownLowerThanZero(event.getEntity(), "perks_slow_immunity_duration")){
                        event.setCancelled(true);
                    } else if (manager.cooldownLowerThanZero(event.getEntity(), "perks_slow_immunity_cooldown")){
                        manager.setCooldown(event.getEntity(), cooldown * 1000, "perks_slow_immunity_cooldown");
                        manager.setCooldown(event.getEntity(), duration * 1000, "perks_slow_immunity_duration");
                        event.setCancelled(true);
                    }
                }
            }
        }
    }
}
