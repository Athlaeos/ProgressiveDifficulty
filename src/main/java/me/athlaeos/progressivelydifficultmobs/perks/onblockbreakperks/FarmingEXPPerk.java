package me.athlaeos.progressivelydifficultmobs.perks.onblockbreakperks;

import me.athlaeos.progressivelydifficultmobs.managers.WorldguardManager;
import me.athlaeos.progressivelydifficultmobs.perks.Perk;
import me.athlaeos.progressivelydifficultmobs.perks.PerkTriggerPriority;
import me.athlaeos.progressivelydifficultmobs.utils.Utils;
import org.bukkit.Material;
import org.bukkit.event.Event;
import org.bukkit.event.block.BlockBreakEvent;

import java.util.Arrays;

public class FarmingEXPPerk extends Perk {
    private double expDropChance;
    private int expAmount;

    public FarmingEXPPerk(){
        this.id = 1001;
        this.icon = Material.EXPERIENCE_BOTTLE;
        this.perkPriority = PerkTriggerPriority.SOONER;
        this.description = Utils.seperateStringIntoLines(Utils.chat("&7When players with this perk" +
                " harvest a crop of some sort, there's a chance the crop will drop some EXP-orbs" +
                " for the player to collect."), 36, "&7");
        this.name = "farming_exp";
        this.displayName = Utils.chat(config.getString("perks." + name + ".display_name"));
        this.expDropChance = config.getDouble("perks." + name + ".exp_chance");
        this.expAmount = config.getInt("perks." + name + ".exp_amount");
    }

    @Override
    public void execute(Event e) {
        if (e instanceof BlockBreakEvent){
            BlockBreakEvent event = (BlockBreakEvent) e;
            if (!event.isCancelled()){
                if (WorldguardManager.getWorldguardManager().isLocationInRegionWithFlag(event.getPlayer().getLocation(), "pdm-farm-exp-deny")
                        || WorldguardManager.getWorldguardManager().isLocationInRegionWithFlag(event.getPlayer().getLocation(), "pdm-perks-deny-all")) {
                    return;
                }
                if (Arrays.asList(Material.WHEAT, Material.BEETROOTS, Material.CARROTS, Material.POTATOES, Material.NETHER_WART, Material.COCOA)
                        .contains(event.getBlock().getType())){
                    if (Utils.getRandom().nextDouble() < expDropChance){
                        event.setExpToDrop(event.getExpToDrop() + expAmount);
                    }
                }
            }
        }
    }
}
