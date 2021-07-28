package me.athlaeos.progressivelydifficultmobs.abilities;

import me.athlaeos.progressivelydifficultmobs.pojo.Ability;
import me.athlaeos.progressivelydifficultmobs.utils.Utils;
import org.bukkit.Material;
import org.bukkit.entity.*;
import org.bukkit.event.Event;

import java.util.Arrays;

public class RaidCaptainAbility extends Ability {
    public RaidCaptainAbility(){
        this.name = Utils.chat("&7&lRaid Captain");
        this.description = Arrays.asList(
                Utils.chat("&7Illagers with this ability"),
                Utils.chat("&7will spawn as raid captains."));
        this.icon = Material.GRAY_BANNER;
        this.mobWhiteList = Arrays.asList(EntityType.PILLAGER, EntityType.VINDICATOR, EntityType.EVOKER, EntityType.ILLUSIONER);
    }

    @Override
    public void execute(Entity entity, Player player, Event event) {
        if (entity instanceof Raider){
            Raider raider = (Raider) entity;
            raider.setPatrolLeader(true);
        }
    }
}
