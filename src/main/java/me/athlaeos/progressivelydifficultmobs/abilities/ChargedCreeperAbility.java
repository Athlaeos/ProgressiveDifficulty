package me.athlaeos.progressivelydifficultmobs.abilities;

import me.athlaeos.progressivelydifficultmobs.pojo.Ability;
import me.athlaeos.progressivelydifficultmobs.utils.Utils;
import org.bukkit.Material;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

import java.util.Arrays;

public class ChargedCreeperAbility extends Ability {
    public ChargedCreeperAbility(){
        this.name = Utils.chat("&9&lCharge Creeper");
        this.description = Arrays.asList(
                Utils.chat("&7Creepers with this ability"),
                Utils.chat("&7will spawn as charged creepers."),
                Utils.chat("&7Charged creepers have increased"),
                Utils.chat("&7explosion radius and damage."));
        this.icon = Material.CREEPER_HEAD;
        this.mobWhiteList = Arrays.asList(EntityType.CREEPER);
    }

    @Override
    public void execute(Entity entity, Player player, Event event) {
        if (entity instanceof Creeper){
            Creeper creeper = (Creeper) entity;
            creeper.setPowered(true);
        }
    }
}
