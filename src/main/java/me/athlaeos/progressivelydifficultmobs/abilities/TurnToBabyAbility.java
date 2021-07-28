package me.athlaeos.progressivelydifficultmobs.abilities;

import me.athlaeos.progressivelydifficultmobs.pojo.Ability;
import me.athlaeos.progressivelydifficultmobs.utils.Utils;
import org.bukkit.Material;
import org.bukkit.entity.*;
import org.bukkit.event.Event;

import java.util.ArrayList;
import java.util.Arrays;

public class TurnToBabyAbility extends Ability {
    public TurnToBabyAbility(){
        this.name = Utils.chat("&7&lConvert to Baby");
        this.description = Arrays.asList(
                Utils.chat("&7Zombies and other zombie-types"),
                Utils.chat("&7will spawn as babies instead."),
                Utils.chat("&7Baby zombies have increased"),
                Utils.chat("&7speed and a much smaller hitbox."));
        this.icon = Material.ZOMBIE_HEAD;
        this.mobWhiteList = new ArrayList<>();
        addWhitelistedMonster("ZOMBIE");
        addWhitelistedMonster("ZOMBIFIED_PIGLIN");
        addWhitelistedMonster("PIG_ZOMBIE");
        addWhitelistedMonster("ZOMBIE_VILLAGER");
        addWhitelistedMonster("DROWNED");
        addWhitelistedMonster("HUSK");
    }

    private void addWhitelistedMonster(String entityType){
        try {
            EntityType type = EntityType.valueOf(entityType);
            mobWhiteList.add(type);
        } catch (IllegalArgumentException e){
        }
    }

    @Override
    public void execute(Entity entity, Player player, Event event) {
        if (entity instanceof Zombie){
            Zombie z = (Zombie) entity;
            z.setBaby(true);
        }
    }
}
