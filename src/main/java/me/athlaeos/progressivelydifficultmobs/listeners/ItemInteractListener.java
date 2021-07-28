package me.athlaeos.progressivelydifficultmobs.listeners;

import me.athlaeos.progressivelydifficultmobs.main.Main;
import me.athlaeos.progressivelydifficultmobs.managers.ActiveItemManager;
import me.athlaeos.progressivelydifficultmobs.managers.LeveledMonsterManager;
import me.athlaeos.progressivelydifficultmobs.managers.PlayerPerksManager;
import me.athlaeos.progressivelydifficultmobs.managers.WorldguardManager;
import me.athlaeos.progressivelydifficultmobs.perks.Perk;
import me.athlaeos.progressivelydifficultmobs.pojo.LeveledMonster;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.persistence.PersistentDataType;

public class ItemInteractListener implements Listener {
    private NamespacedKey eggMetaKey = new NamespacedKey(Main.getInstance(), "pdm-custom_egg");

    @EventHandler
    public void onItemClick(PlayerInteractEvent e){
        if (e.getHand() == EquipmentSlot.OFF_HAND){
            return;
        }
        if (e.getHand() == EquipmentSlot.HAND){
            PlayerPerksManager manager = PlayerPerksManager.getInstance();
            for (Perk perk : manager.sortPerksByPriority(manager.getPlayersTotalPerks(e.getPlayer()))){
                perk.execute(e);
            }

            if (WorldguardManager.getWorldguardManager().isLocationInRegionWithFlag(e.getPlayer().getLocation(), "pdm-custom-item-deny")){
                return;
            }

            ItemStack clickedItem = e.getPlayer().getInventory().getItemInMainHand();
            if (clickedItem.getType() != Material.AIR){
                if (clickedItem.getItemMeta().getPersistentDataContainer().has(eggMetaKey, PersistentDataType.STRING) && e.getClickedBlock() != null){
                    e.setCancelled(true);

                    if (e.getClickedBlock() != null && e.getAction() == Action.RIGHT_CLICK_BLOCK){
                        Location spawnOnBlock = e.getClickedBlock().getLocation().add(0.5, 0.1, 0.5);
                        switch(e.getBlockFace()){
                            case NORTH: spawnOnBlock.setZ(spawnOnBlock.getZ() - 1);
                                break;
                            case SOUTH: spawnOnBlock.setZ(spawnOnBlock.getZ() + 1);
                                break;
                            case UP: spawnOnBlock.setY(spawnOnBlock.getY() + 1);
                                break;
                            case DOWN: spawnOnBlock.setY(spawnOnBlock.getY() - 1);
                                break;
                            case EAST: spawnOnBlock.setX(spawnOnBlock.getX() + 1);
                                break;
                            case WEST: spawnOnBlock.setX(spawnOnBlock.getX() - 1);
                                break;
                        }
                        String entityKey = clickedItem.getItemMeta().getPersistentDataContainer().get(eggMetaKey, PersistentDataType.STRING);
                        try{
                            Entity spawnedEntity = spawnOnBlock.getWorld().spawnEntity(spawnOnBlock, EntityType.valueOf(entityKey));
                            spawnedEntity.setMetadata("pdm-cancel_monster_custom", new FixedMetadataValue(Main.getInstance(), true));
//                            if (spawnedEntity.getCustomName() != null){
//                                if (spawnedEntity.getCustomName().contains("Spawn vanilla")){
//                                    spawnedEntity.setCustomName("");
//                                }
//                            }
                            if (e.getPlayer().getGameMode() != GameMode.CREATIVE){
                                e.getPlayer().getInventory().getItemInMainHand().setAmount(e.getPlayer().getInventory().getItemInMainHand().getAmount() - 1);
                            }
                        } catch (IllegalArgumentException iae){
                            LeveledMonster chosenMonster = LeveledMonsterManager.getInstance().getMonster(entityKey);
                            if (chosenMonster != null){
                                Entity spawnedEntity = spawnOnBlock.getWorld().spawnEntity(spawnOnBlock, chosenMonster.getEntityType());
                                if (spawnedEntity instanceof LivingEntity){
//                                    if (spawnedEntity.getCustomName() != null){
//                                        if (spawnedEntity.getCustomName().contains("Spawn custom")){
//                                            spawnedEntity.setCustomName("");
//                                        }
//                                    }
                                    LeveledMonsterManager.getInstance().customizeMob((LivingEntity) spawnedEntity, chosenMonster, CreatureSpawnEvent.SpawnReason.SPAWNER_EGG);
                                    if (e.getPlayer().getGameMode() != GameMode.CREATIVE){
                                        e.getPlayer().getInventory().getItemInMainHand().setAmount(e.getPlayer().getInventory().getItemInMainHand().getAmount() - 1);
                                    }
                                }
                            }
                        }
                    }
                    return;
                }

                for (NamespacedKey key : ActiveItemManager.getInstance().getStandaloneActiveItems().keySet()){
                    if (clickedItem.getItemMeta().getPersistentDataContainer().has(key, PersistentDataType.INTEGER)){
                        ActiveItemManager.getInstance().getStandaloneActiveItems().get(key).execute(e);
                    }
                }
            }
        }
    }
}
