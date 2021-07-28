package me.athlaeos.progressivelydifficultmobs.listeners;

import me.athlaeos.progressivelydifficultmobs.managers.CooldownManager;
import me.athlaeos.progressivelydifficultmobs.managers.PlayerPerksManager;
import me.athlaeos.progressivelydifficultmobs.perks.Perk;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPotionEffectEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Arrays;
import java.util.List;

public class PotionEffectChangeListener implements Listener {
    private final List<PotionEffectType> potionEffectsToCleanse = Arrays.asList(
            PotionEffectType.BLINDNESS,
            PotionEffectType.CONFUSION,
            PotionEffectType.POISON,
            PotionEffectType.WITHER,
            PotionEffectType.SLOW,
            PotionEffectType.HUNGER,
            PotionEffectType.LEVITATION,
            PotionEffectType.SLOW_DIGGING,
            PotionEffectType.UNLUCK,
            PotionEffectType.WEAKNESS
    );

    public PotionEffectChangeListener(){

    }

    @EventHandler
    public void onPotionEffect(EntityPotionEffectEvent e){
        if (!e.isCancelled()){
            if (e.getEntity() instanceof Player){
                Player p = (Player) e.getEntity();

                if (e.getAction() == EntityPotionEffectEvent.Action.ADDED){
                    if (!CooldownManager.getInstance().cooldownLowerThanZero(p, "cleanse_debuffs_duration")){
                        if (potionEffectsToCleanse.contains(e.getModifiedType())){
                            e.setCancelled(true);
                        }
                    }
                }

                for (Perk perk : PlayerPerksManager.getInstance().sortPerksByPriority(PlayerPerksManager.getInstance().getPlayersTotalPerks(p))){
                    perk.execute(e);
                }
            }
        }
    }
}
