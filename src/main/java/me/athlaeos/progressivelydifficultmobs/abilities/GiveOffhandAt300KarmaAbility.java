package me.athlaeos.progressivelydifficultmobs.abilities;

import me.athlaeos.progressivelydifficultmobs.main.Main;
import me.athlaeos.progressivelydifficultmobs.pojo.Ability;
import me.athlaeos.progressivelydifficultmobs.utils.Utils;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Particle;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import java.util.Arrays;

public class GiveOffhandAt300KarmaAbility extends Ability {
    private final NamespacedKey key = new NamespacedKey(Main.getInstance(), "pdm-karma");
    private final NamespacedKey cursedKey = new NamespacedKey(Main.getInstance(), "pdm-curse");

    public GiveOffhandAt300KarmaAbility(){
        this.name = Utils.chat("&a&lGive off-hand item to 300+ karma");
        this.description = Arrays.asList(
                Utils.chat("&7Monsters with this ability"),
                Utils.chat("&7will give the item they hold in"),
                Utils.chat("&7their off-hand to players with"),
                Utils.chat("&7300 or more karma when"),
                Utils.chat("&7right-clicked with an empty hand,"),
                Utils.chat("&7but only if the entity isn't cursed."));
        this.icon = Material.CAKE;
        this.mobWhiteList = null;
    }

    @Override
    public void execute(Entity entity, Player player, Event event) {
        if (entity instanceof LivingEntity){
            LivingEntity clickedEntity = (LivingEntity) entity;
            if (player.getPersistentDataContainer().has(key, PersistentDataType.DOUBLE)){
                if (player.getPersistentDataContainer().get(key, PersistentDataType.DOUBLE) >= 300){
                    if (!clickedEntity.getPersistentDataContainer().has(cursedKey, PersistentDataType.STRING)){
                        if (clickedEntity.getEquipment() != null){
                            player.spawnParticle(Particle.HEART, clickedEntity.getLocation().add(0, 1, 0), 0, 0, 0.5, 0);
                            player.getInventory().setItemInMainHand(clickedEntity.getEquipment().getItemInOffHand());
                            clickedEntity.getEquipment().setItemInOffHand(new ItemStack(Material.AIR));
                        }
                    }
                }
            }
        }
    }
}
