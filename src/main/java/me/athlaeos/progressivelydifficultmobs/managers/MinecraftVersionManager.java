package me.athlaeos.progressivelydifficultmobs.managers;

import me.athlaeos.progressivelydifficultmobs.main.Main;

import java.util.HashMap;
import java.util.Map;

public class MinecraftVersionManager {
    private static MinecraftVersionManager manager = null;
    private Map<MinecraftVersion, Integer> versions = new HashMap<>();
    private MinecraftVersion serverVersion;
    private static Main plugin;

    public MinecraftVersionManager(){
        plugin = Main.getInstance();
        setServerVersion();
        versions.put(MinecraftVersion.MINECRAFT_1_8, 1);
        versions.put(MinecraftVersion.MINECRAFT_1_9, 2);
        versions.put(MinecraftVersion.MINECRAFT_1_10, 3);
        versions.put(MinecraftVersion.MINECRAFT_1_11, 4);
        versions.put(MinecraftVersion.MINECRAFT_1_12, 5);
        versions.put(MinecraftVersion.MINECRAFT_1_13, 6);
        versions.put(MinecraftVersion.MINECRAFT_1_14, 7);
        versions.put(MinecraftVersion.MINECRAFT_1_15, 8);
        versions.put(MinecraftVersion.MINECRAFT_1_16, 9);
        versions.put(MinecraftVersion.MINECRAFT_1_17, 10);
    }

    public static MinecraftVersionManager getInstance(){
        if (manager == null){
            manager = new MinecraftVersionManager();
        }
        return manager;
    }

    public boolean currentVersionOlderThan(MinecraftVersion version){
        if (serverVersion == MinecraftVersion.INCOMPATIBLE) return false;
        return versions.get(serverVersion) <= versions.get(version);
    }

    public boolean currentVersionNewerThan(MinecraftVersion version){
        if (serverVersion == MinecraftVersion.INCOMPATIBLE) return false;
        return versions.get(serverVersion) >= versions.get(version);
    }

    private void setServerVersion(){
        String version = plugin.getServer().getVersion();
        if (version.contains("1_8") || version.contains("1.8")) serverVersion = MinecraftVersion.MINECRAFT_1_8;
        else if (version.contains("1_9") || version.contains("1.9")) serverVersion = MinecraftVersion.MINECRAFT_1_9;
        else if (version.contains("1_10") || version.contains("1.10")) serverVersion = MinecraftVersion.MINECRAFT_1_10;
        else if (version.contains("1_11") || version.contains("1.11")) serverVersion = MinecraftVersion.MINECRAFT_1_11;
        else if (version.contains("1_12") || version.contains("1.12")) serverVersion = MinecraftVersion.MINECRAFT_1_12;
        else if (version.contains("1_13") || version.contains("1.13")) serverVersion = MinecraftVersion.MINECRAFT_1_13;
        else if (version.contains("1_14") || version.contains("1.14")) serverVersion = MinecraftVersion.MINECRAFT_1_14;
        else if (version.contains("1_15") || version.contains("1.15")) serverVersion = MinecraftVersion.MINECRAFT_1_15;
        else if (version.contains("1_16") || version.contains("1.16")) serverVersion = MinecraftVersion.MINECRAFT_1_16;
        else if (version.contains("1_17") || version.contains("1.17")) serverVersion = MinecraftVersion.MINECRAFT_1_17;
        else serverVersion = MinecraftVersion.INCOMPATIBLE;
    }

    public MinecraftVersion getServerVersion() {
        return serverVersion;
    }
}
