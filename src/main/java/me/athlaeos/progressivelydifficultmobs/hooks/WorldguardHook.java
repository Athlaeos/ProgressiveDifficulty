package me.athlaeos.progressivelydifficultmobs.hooks;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.flags.registry.FlagRegistry;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import me.athlaeos.progressivelydifficultmobs.main.Main;
import me.athlaeos.progressivelydifficultmobs.managers.WorldguardManager;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.ArrayList;
import java.util.List;

public class WorldguardHook {
    private static WorldguardHook manager = null;
    private StateFlag PDM_CUSTOM_MONSTER_DENY;
    private StateFlag PDM_DAMAGE_BUFF_DENY;
    private StateFlag PDM_DAMAGE_NERF_DENY;
    private StateFlag PDM_FARM_LOOTING_DENY;
    private StateFlag PDM_FARM_EXP_DENY;
    private StateFlag PDM_FARM_AUTOREPLANT_DENY;
    private StateFlag PDM_DAMAGE_MITIGATION_BUFF_DENY;
    private StateFlag PDM_DAMAGE_MITIGATION_NERF_DENY;
    private StateFlag PDM_IMMUNITY_EXPLOSION_DENY;
    private StateFlag PDM_IMMUNITY_FALL_DENY;
    private StateFlag PDM_IMMUNITY_FIRE_DENY;
    private StateFlag PDM_IMMUNITY_ALL_DENY;
    private StateFlag PDM_IMMUNITY_MAGIC_DENY;
    private StateFlag PDM_IMMUNITY_MELEE_DENY;
    private StateFlag PDM_IMMUNITY_POISON_DENY;
    private StateFlag PDM_IMMUNITY_WITHER_DENY;
    private StateFlag PDM_IMMUNITY_PROJECTILE_DENY;
    private StateFlag PDM_REVIVE_DENY;
    private StateFlag PDM_BREEDING_EXP_DENY;
    private StateFlag PDM_PERKS_DENY_ALL;
    private StateFlag PDM_IMMUNITY_SLOW_DENY;
    private StateFlag PDM_IMMUNITY_BLIND_DENY;
    private StateFlag PDM_CUSTOM_ITEM_DENY;
    private StateFlag PDM_ADRENALINE_DENY;
    private StateFlag PDM_RESILIENT_DENY;
    private StateFlag PDM_RAGE_DENY;


    public WorldguardHook(){

    }

    public static WorldguardHook getInstance(){
        if (manager == null){
            manager = new WorldguardHook();
        }
        return manager;
    }

    private StateFlag setFlag(String s){
        StateFlag newFlag = null;
        FlagRegistry registry = WorldGuard.getInstance().getFlagRegistry();
        try {
            StateFlag flag = new StateFlag(s, true);
            registry.register(flag);
            newFlag = flag;
        } catch (Exception e) {
//            Flag<?> existing = registry.get(s);
//            if (existing instanceof StateFlag) {
//                newFlag = (StateFlag) existing;
//            } else {
                System.out.println("[EnchantsSquared] Something went wrong with WorldguardHook#setFlag for flag " + s + ", contact the plugin developer!");
//            }
        }
        return newFlag;
    }

    public boolean isLocationInFlaggedRegion(Location l, String flag){
        if (WorldguardManager.getWorldguardManager().useWorldGuard()){
            RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
            RegionManager regions = container.get(BukkitAdapter.adapt(l.getWorld()));
            if (regions == null) return false;
            for (String region : regions.getRegions().keySet()) {
                if (regions.getRegion(region).contains((int)l.getX(), (int)l.getY(), (int)l.getZ())) {
                    assert regions.getRegion(region) != null;
                    for (Flag f : regions.getRegion(region).getFlags().keySet()) {
                        if (f.getName().equals(flag)) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    public List<String> getLocationRegions(Location l){
        List<String> regions = new ArrayList<>();
        if (WorldguardManager.getWorldguardManager().useWorldGuard()){
            RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
            RegionManager worldRegions = container.get(BukkitAdapter.adapt(l.getWorld()));
            if (worldRegions == null) return regions;
            for (String region : worldRegions.getRegions().keySet()) {
                if (worldRegions.getRegion(region).contains((int)l.getX(), (int)l.getY(), (int)l.getZ())) {
                    regions.add(region);
                }
            }
        }
        return regions;
    }

    public List<String> getAllRegions(){
        List<String> regions = new ArrayList<>();
        if (WorldguardManager.getWorldguardManager().useWorldGuard()){
            RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
            for (World w : Main.getInstance().getServer().getWorlds()){
                RegionManager worldRegions = container.get(BukkitAdapter.adapt(w));
                if (worldRegions == null) {
                    continue;
                }
                for (String region : worldRegions.getRegions().keySet()){
                    regions.add(region);
                }
            }
        }
        return regions;
    }

    public void registerFlags(){
        this.PDM_CUSTOM_MONSTER_DENY = setFlag("pdm-custom-monster-deny");
        this.PDM_BREEDING_EXP_DENY = setFlag("pdm-breeding-exp-deny");
        this.PDM_DAMAGE_BUFF_DENY = setFlag("pdm-damage-buff-deny");
        this.PDM_DAMAGE_NERF_DENY = setFlag("pdm-damage-nerf-deny");
        this.PDM_DAMAGE_MITIGATION_BUFF_DENY = setFlag("pdm-damage-mitigations-buff-deny");
        this.PDM_DAMAGE_MITIGATION_NERF_DENY = setFlag("pdm-damage-mitigations-nerf-deny");
        this.PDM_FARM_AUTOREPLANT_DENY = setFlag("pdm-farm-autoreplant-deny");
        this.PDM_FARM_EXP_DENY = setFlag("pdm-farm-exp-deny");
        this.PDM_FARM_LOOTING_DENY = setFlag("pdm-farm-looting-deny");
        this.PDM_IMMUNITY_ALL_DENY = setFlag("pdm-immunity-all-deny");
        this.PDM_IMMUNITY_EXPLOSION_DENY = setFlag("pdm-immunity-explosions-deny");
        this.PDM_IMMUNITY_FALL_DENY = setFlag("pdm-immunity-fall-deny");
        this.PDM_IMMUNITY_FIRE_DENY = setFlag("pdm-immunity-fire-deny");
        this.PDM_IMMUNITY_MAGIC_DENY = setFlag("pdm-immunity-magic-deny");
        this.PDM_IMMUNITY_MELEE_DENY = setFlag("pdm-immunity-melee-deny");
        this.PDM_IMMUNITY_POISON_DENY = setFlag("pdm-immunity-poison-deny");
        this.PDM_IMMUNITY_PROJECTILE_DENY = setFlag("pdm-immunity-projectile-deny");
        this.PDM_IMMUNITY_WITHER_DENY = setFlag("pdm-immunity-wither-deny");
        this.PDM_REVIVE_DENY = setFlag("pdm-revive-deny");
        this.PDM_PERKS_DENY_ALL = setFlag("pdm-perks-deny-all");
        this.PDM_IMMUNITY_SLOW_DENY = setFlag("pdm-immunity-slow-deny");
        this.PDM_IMMUNITY_BLIND_DENY = setFlag("pdm-immunity-blind-deny");
        this.PDM_CUSTOM_ITEM_DENY = setFlag("pdm-custom-item-deny");
        this.PDM_RAGE_DENY = setFlag("pdm-rage-deny");
        this.PDM_RESILIENT_DENY = setFlag("pdm-resilient-deny");
        this.PDM_ADRENALINE_DENY = setFlag("pdm-adrenaline-deny");
    }
}
