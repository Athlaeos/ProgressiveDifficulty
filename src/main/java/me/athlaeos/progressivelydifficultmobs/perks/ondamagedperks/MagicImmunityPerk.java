package me.athlaeos.progressivelydifficultmobs.perks.ondamagedperks;

import me.athlaeos.progressivelydifficultmobs.managers.WorldguardManager;
import me.athlaeos.progressivelydifficultmobs.perks.Perk;
import me.athlaeos.progressivelydifficultmobs.perks.PerkTriggerPriority;
import me.athlaeos.progressivelydifficultmobs.utils.Utils;
import org.bukkit.Material;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageEvent;

public class MagicImmunityPerk extends Perk {
    public MagicImmunityPerk(){
        this.id = 1043;
        this.icon = Material.POTION;
        this.perkPriority = PerkTriggerPriority.SOONER;
        this.description = Utils.seperateStringIntoLines(Utils.chat("&7Players with this perk" +
                " are immune to magic damage, including ender dragon breath"), 36, "&7");
        this.name = "magic_immunity";
        this.displayName = Utils.chat(config.getString("perks." + name + ".display_name"));
    }

    @Override
    public void execute(Event e) {
        if (e instanceof EntityDamageEvent){
            EntityDamageEvent event = (EntityDamageEvent) e;
            if (!event.isCancelled()){
                if (WorldguardManager.getWorldguardManager().isLocationInRegionWithFlag(event.getEntity().getLocation(), "pdm-immunity-magic-deny")
                        || WorldguardManager.getWorldguardManager().isLocationInRegionWithFlag(event.getEntity().getLocation(), "pdm-perks-deny-all")) {
                    return;
                }
                if (event.getCause() == EntityDamageEvent.DamageCause.MAGIC || event.getCause() == EntityDamageEvent.DamageCause.DRAGON_BREATH){
                    event.setCancelled(true);
                }
            }
        }
    }
}
