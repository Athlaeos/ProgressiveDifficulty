package me.athlaeos.progressivelydifficultmobs.abilities;

import me.athlaeos.progressivelydifficultmobs.managers.AbilityManager;
import me.athlaeos.progressivelydifficultmobs.pojo.Ability;
import me.athlaeos.progressivelydifficultmobs.utils.Utils;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntitySpawnEvent;

import java.util.Arrays;

public class ExampleAbility extends Ability {
    public ExampleAbility(){
        this.name = Utils.chat("&7&lExample");
        this.description = Arrays.asList(
                Utils.chat("&7Monsters with this ability"),
                Utils.chat("&7will do something idk"));
        this.icon = Material.BARRIER;
        this.mobWhiteList = Arrays.asList(
                EntityType.PANDA
        ); //here should go a list of EntityTypes

        AbilityManager.getInstance().registerInstantAbility("custom_ability_example", this);
    }


    @Override
    public void execute(Entity entity, Player player, Event event) {
        if (event instanceof EntitySpawnEvent){
            EntitySpawnEvent e = (EntitySpawnEvent) event;
            //do stuff
        }
    }
}
