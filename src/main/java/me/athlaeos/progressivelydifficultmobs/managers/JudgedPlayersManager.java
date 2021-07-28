package me.athlaeos.progressivelydifficultmobs.managers;

import me.athlaeos.progressivelydifficultmobs.main.Main;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;

public class JudgedPlayersManager {

    private static JudgedPlayersManager manager = null;
    private final NamespacedKey key = new NamespacedKey(Main.getInstance(), "pdm-isjudged");

    public JudgedPlayersManager(){

    }

    public static JudgedPlayersManager getInstance(){
        if (manager == null){
            manager = new JudgedPlayersManager();
        }
        return manager;
    }

    /**
     * @param p
     * @return true if the Player participates with the karma system, false otherwise
     */
    public boolean isPlayerJudged(Player p){
        if (!p.getPersistentDataContainer().has(key, PersistentDataType.INTEGER)){
            p.getPersistentDataContainer().set(key, PersistentDataType.INTEGER, (PluginConfigurationManager.getInstance().isForceKarma()) ? 1 : 0);
        }
        return p.getPersistentDataContainer().get(key, PersistentDataType.INTEGER) == 1;
    }

    /**
     * Toggles the player's involvement with the karma system
     * @param p
     */
    public void togglePlayerJudged(Player p){
        if (!p.getPersistentDataContainer().has(key, PersistentDataType.INTEGER)){
            p.getPersistentDataContainer().set(key, PersistentDataType.INTEGER, 0);
        }
        if (p.getPersistentDataContainer().get(key, PersistentDataType.INTEGER) == 0){
            p.getPersistentDataContainer().set(key, PersistentDataType.INTEGER, 1);
        } else {
            p.getPersistentDataContainer().set(key, PersistentDataType.INTEGER, 0);
        }
    }

    public void setPlayerJudged(Player p, boolean status){
        p.getPersistentDataContainer().set(key, PersistentDataType.INTEGER, (status) ? 1 : 0);
    }
}
