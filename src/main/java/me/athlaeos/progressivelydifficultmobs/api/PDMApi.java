package me.athlaeos.progressivelydifficultmobs.api;

import me.athlaeos.progressivelydifficultmobs.main.Main;
import me.athlaeos.progressivelydifficultmobs.managers.*;
import org.bukkit.plugin.java.JavaPlugin;

public class PDMApi {

    private static PDMApi api = null;
    private PDMApi(){

    }

    public static PDMApi getAPI(){
        if (api == null){
            api = new PDMApi();
        }
        return api;
    }

    public PlayerKarmaManager getKarmaManager(){
        return PlayerKarmaManager.getInstance();
    }

    public PlayerCurseManager getCurseManager(){
        return PlayerCurseManager.getInstance();
    }

    public LootTableManager getLootTableManager(){
        return LootTableManager.getInstance();
    }

    public LeveledMonsterManager getMonsterManager(){
        return LeveledMonsterManager.getInstance();
    }

    public JudgedPlayersManager getJudgedPlayersManager(){
        return JudgedPlayersManager.getInstance();
    }

    public CursedMonsterManager getCursedMonsterManager(){
        return CursedMonsterManager.getInstance();
    }

    public CooldownManager getCooldownManager(){
        return CooldownManager.getInstance();
    }

    public ActiveItemManager getActiveItemManager(){
        return ActiveItemManager.getInstance();
    }

    public AbilityManager getAbilityManager(){
        return AbilityManager.getInstance();
    }

    public JavaPlugin getPlugin(){
        return Main.getInstance();
    }
}
