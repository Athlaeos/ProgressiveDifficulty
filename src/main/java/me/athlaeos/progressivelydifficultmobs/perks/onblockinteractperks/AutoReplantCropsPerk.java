package me.athlaeos.progressivelydifficultmobs.perks.onblockinteractperks;

import me.athlaeos.progressivelydifficultmobs.main.Main;
import me.athlaeos.progressivelydifficultmobs.managers.WorldguardManager;
import me.athlaeos.progressivelydifficultmobs.perks.Perk;
import me.athlaeos.progressivelydifficultmobs.perks.PerkTriggerPriority;
import me.athlaeos.progressivelydifficultmobs.utils.Utils;
import org.bukkit.Material;
import org.bukkit.block.data.Ageable;
import org.bukkit.event.Event;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.Collection;

public class AutoReplantCropsPerk extends Perk {
    public AutoReplantCropsPerk(){
        this.id = 1000;
        this.icon = Material.DIAMOND_HOE;
        this.perkPriority = PerkTriggerPriority.LATER;
        this.description = Utils.seperateStringIntoLines(Utils.chat("&7Players with this perk can" +
                " right-click fully grown crops and the plant will instantly be harvested, leaving newly planted seeds" +
                " in its place."), 36, "&7");
        this.name = "auto_harvest";
        this.displayName = Utils.chat(config.getString("perks." + name + ".display_name"));
    }

    @Override
    public void execute(Event e) {
        if (e instanceof PlayerInteractEvent){
            PlayerInteractEvent event = (PlayerInteractEvent) e;
            if (event.getClickedBlock() == null) return;
            if (Arrays.asList(Material.WHEAT, Material.BEETROOTS, Material.CARROTS, Material.POTATOES, Material.NETHER_WART, Material.COCOA)
                    .contains(event.getClickedBlock().getType())){
                if (WorldguardManager.getWorldguardManager().isLocationInRegionWithFlag(event.getPlayer().getLocation(), "pdm-farm-autoreplant-deny")
                        || WorldguardManager.getWorldguardManager().isLocationInRegionWithFlag(event.getPlayer().getLocation(), "pdm-perks-deny-all")) {
                    return;
                }
                if (event.getClickedBlock().getBlockData() instanceof Ageable){
                    Ageable crop = (Ageable) event.getClickedBlock().getBlockData();
                    if (crop.getAge() >= crop.getMaximumAge()){
                        BlockBreakEvent breakEvent = new BlockBreakEvent(event.getClickedBlock(), event.getPlayer());
                        Main.getInstance().getServer().getPluginManager().callEvent(breakEvent);
                        if (!breakEvent.isCancelled()){
                            Collection<ItemStack> drops =  event.getClickedBlock().getDrops(event.getPlayer().getInventory().getItemInMainHand());

                            crop.setAge(0);
                            event.getClickedBlock().setBlockData(crop);
                            for (ItemStack drop : drops){
                                event.getClickedBlock().getWorld().dropItem(event.getClickedBlock().getLocation().add(0.5, 0.5, 0.5), drop);
                            }
                        }
                    }
                }
            }
        }
    }
}
