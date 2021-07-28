package me.athlaeos.progressivelydifficultmobs.perks.onblockbreakperks;

import me.athlaeos.progressivelydifficultmobs.managers.WorldguardManager;
import me.athlaeos.progressivelydifficultmobs.perks.Perk;
import me.athlaeos.progressivelydifficultmobs.perks.PerkTriggerPriority;
import me.athlaeos.progressivelydifficultmobs.utils.Utils;
import org.bukkit.Material;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.event.Event;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;

public class ExtraFarmingDropsPerk extends Perk {
    private int wheatBonus;
    private int carrotBonus;
    private int potatoBonus;
    private int beetBonus;
    private int wartBonus;
    private int cocoaBonus;
    private double bonusDropChance;

    public ExtraFarmingDropsPerk(){
        this.id = 1002;
        this.icon = Material.WHEAT;
        this.perkPriority = PerkTriggerPriority.LATER;
        this.description = Utils.seperateStringIntoLines(Utils.chat("&7Players with this perk " +
                "have a chance to receive more drops from their crops when harvesting"), 36, "&7");
        this.name = "farming_bonus";
        this.displayName = Utils.chat(config.getString("perks." + name + ".display_name"));
        this.wheatBonus = config.getInt("perks." + name + ".wheat_bonus");
        this.carrotBonus = config.getInt("perks." + name + ".carrot_bonus");
        this.potatoBonus = config.getInt("perks." + name + ".potato_bonus");
        this.beetBonus = config.getInt("perks." + name + ".beetroot_bonus");
        this.wartBonus = config.getInt("perks." + name + ".netherwart_bonus");
        this.cocoaBonus = config.getInt("perks." + name + ".cocoa_bonus");
        this.bonusDropChance = config.getDouble("perks." + name + ".bonus_chance");
    }

    @Override
    public void execute(Event e) {
        if (e instanceof BlockBreakEvent){
            BlockBreakEvent event = (BlockBreakEvent) e;
            if (!event.isCancelled()){
                if (WorldguardManager.getWorldguardManager().isLocationInRegionWithFlag(event.getPlayer().getLocation(), "pdm-farm-looting-deny")
                        || WorldguardManager.getWorldguardManager().isLocationInRegionWithFlag(event.getPlayer().getLocation(), "pdm-perks-deny-all")) {
                    return;
                }
                if (Arrays.asList(Material.WHEAT, Material.BEETROOTS, Material.CARROTS, Material.POTATOES, Material.NETHER_WART, Material.COCOA)
                        .contains(event.getBlock().getType())){
                    if (Utils.getRandom().nextDouble() < bonusDropChance){
                        ItemStack itemsToDrop = null;
                        switch (event.getBlock().getType()){
                            case WHEAT:
                                itemsToDrop = new ItemStack(Material.WHEAT, wheatBonus);
                                break;
                            case BEETROOTS:
                                itemsToDrop = new ItemStack(Material.BEETROOT, beetBonus);
                                break;
                            case CARROTS:
                                itemsToDrop = new ItemStack(Material.CARROT, carrotBonus);
                                break;
                            case POTATOES:
                                itemsToDrop = new ItemStack(Material.POTATO, potatoBonus);
                                break;
                            case NETHER_WART:
                                itemsToDrop = new ItemStack(Material.NETHER_WART, wartBonus);
                                break;
                            case COCOA:
                                itemsToDrop = new ItemStack(Material.COCOA_BEANS, cocoaBonus);
                                break;
                        }
                        if (itemsToDrop != null){
                            event.getBlock().getWorld().dropItem(event.getBlock().getLocation(), itemsToDrop);
                        }
                    }
                }
            }
        }
    }
}
