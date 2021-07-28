package me.athlaeos.progressivelydifficultmobs.abilities;

import me.athlaeos.progressivelydifficultmobs.main.Main;
import me.athlaeos.progressivelydifficultmobs.pojo.Ability;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityPotionEffectEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;

public class CustomizablePotionEffectWhenDamagedAbility extends Ability {
    private final PotionEffectType effect;
    private final int amplifier;
    private final int duration;
    public CustomizablePotionEffectWhenDamagedAbility(String name, List<String> description, Material icon, PotionEffectType effect, int amplifier, int duration){
        this.name = name;
        this.description = description;
        this.icon = icon;
        this.mobWhiteList = new ArrayList<>();
        this.effect = effect;
        this.amplifier = amplifier;
        this.duration = duration;
    }

    @Override
    public void execute(Entity entity, Player player, Event event) {
        if (entity instanceof LivingEntity){
            LivingEntity e = (LivingEntity) entity;
            PotionEffect potionEffect = new PotionEffect(effect, duration, amplifier);
            EntityPotionEffectEvent potionEvent = new EntityPotionEffectEvent(e, null, potionEffect, EntityPotionEffectEvent.Cause.PLUGIN, EntityPotionEffectEvent.Action.ADDED, false);
            Main.getInstance().getServer().getPluginManager().callEvent(potionEvent);
            if (!potionEvent.isCancelled()){
                e.addPotionEffect(potionEffect);
            }
        }
    }
}
