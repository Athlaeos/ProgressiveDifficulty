package me.athlaeos.progressivelydifficultmobs.managers;

import me.athlaeos.progressivelydifficultmobs.hooks.WorldguardHook;
import me.athlaeos.progressivelydifficultmobs.main.Main;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.List;

public class WorldguardManager {
    private static WorldguardManager hook = null;
    private boolean useWorldGuard = false;

    public static WorldguardManager getWorldguardManager(){
        if (hook == null){
            hook = new WorldguardManager();
        }
        return hook;
    }

    public void registerWorldGuard(){
        if (Main.getInstance().getServer().getPluginManager().getPlugin("WorldGuard") == null){
            useWorldGuard = false;
        } else {
            useWorldGuard = true;
        }
    }

    public boolean useWorldGuard(){
        return useWorldGuard;
    }

    public boolean isLocationInRegionWithFlag(Location l, String flag){
        if (useWorldGuard){
            return WorldguardHook.getInstance().isLocationInFlaggedRegion(l, flag);
        } else {
            return false;
        }
    }

    public List<String> getLocationRegions(Location l){
        if (useWorldGuard){
            return WorldguardHook.getInstance().getLocationRegions(l);
        } else {
            return new ArrayList<>();
        }
    }

    public List<String> getAllRegions(){
        if (useWorldGuard){
            return WorldguardHook.getInstance().getAllRegions();
        } else {
            return new ArrayList<>();
        }
    }

    public void registerFlags(){
        if (useWorldGuard){
            WorldguardHook.getInstance().registerFlags();
        }
    }
}
