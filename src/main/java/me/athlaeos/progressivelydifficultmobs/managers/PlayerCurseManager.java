package me.athlaeos.progressivelydifficultmobs.managers;

import me.athlaeos.progressivelydifficultmobs.filters.PlayerFilter;
import me.athlaeos.progressivelydifficultmobs.main.Main;
import me.athlaeos.progressivelydifficultmobs.utils.Utils;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;

import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.function.Predicate;

public class PlayerCurseManager {

    private static PlayerCurseManager manager = null;
    private final NamespacedKey key = new NamespacedKey(Main.getInstance(), "pdm-curse");
    private PluginConfigurationManager config;

    public PlayerCurseManager() {
        config = PluginConfigurationManager.getInstance();
    }

    public static PlayerCurseManager getInstance() {
        if (manager == null) {
            manager = new PlayerCurseManager();
        }
        return manager;
    }

    public void reload(){
        manager = null;
        config = PluginConfigurationManager.getInstance();
    }

    /**
     * Get the amount of curse points the player has, cannot be lower than 0
     *
     * @param uuid
     * @return the integer amount of curse points the player has, or 0 if they have none.
     */
    public int getCurse(UUID uuid) {
        Player p = Main.getInstance().getServer().getPlayer(uuid);
        if (p == null){
            return 0;
        }
        if (!p.getPersistentDataContainer().has(key, PersistentDataType.INTEGER)){
            p.getPersistentDataContainer().set(key, PersistentDataType.INTEGER, 0);
        }
        return p.getPersistentDataContainer().get(key, PersistentDataType.INTEGER);
    }

    public double getMonsterSpawnCursedChance(int curse){
        return PluginConfigurationManager.getInstance().getCursedEnemyChance() * curse;
    }

    /**
     * Set the amount of curse points the player has to the given amount
     * Can be any amount, but will not be set to exceed the maximum or go below 0
     * Ex: newCurse = 155 with a maximum curse of 10, the player's curse will be set to 10.
     * @param uuid
     * @param newCurse
     */
    public void setCurse(UUID uuid, int newCurse) {
        Player p = Main.getInstance().getServer().getPlayer(uuid);
        if (p == null){
            return;
        }
        if (newCurse < 0){
            newCurse = 0;
        } else if (newCurse > config.getMaxCurse()){
            newCurse = config.getMaxCurse();
        }
        p.getPersistentDataContainer().set(key, PersistentDataType.INTEGER, newCurse);
    }

    /**
     * Adds the amount of curse to a player's curse points.
     * The player's curse will never exceed the maximum or go below 0
     * @param uuid
     * @param amount
     */
    public void addCurse(UUID uuid, int amount){
        Player p = Main.getInstance().getServer().getPlayer(uuid);
        if (p == null){
            return;
        }
        int playersCurse = getCurse(uuid);
        if (amount > 0) {
            if (playersCurse >= config.getMaxCurse()){
                return;
            }
        } else {
            if (playersCurse <= 0){
                return;
            }
        }

        if (playersCurse + amount >= config.getMaxCurse()){
            p.getPersistentDataContainer().set(key, PersistentDataType.INTEGER, config.getMaxCurse());
        } else if (playersCurse + amount <= 0){
            p.getPersistentDataContainer().set(key, PersistentDataType.INTEGER, 0);
        } else {
            p.getPersistentDataContainer().set(key, PersistentDataType.INTEGER, playersCurse + amount);
        }
    }

    /**
     * Get the highest amount of curse of players surrounding the given Location
     * This will consider players within a radius of 128 blocks around the location.
     * Ex: if one player has 3 curse, and another 9, both within the radius, this method will return 9
     *
     * @param l
     * @return the integer amount of the highest curse of the all players in a 128 radius around the given location
     */
    public int getMaxCurseSurroundingPlayers(Location l){
        List<Player> players = Utils.getPlayersInArea(l, 128);
        int maxCurse = 0;
        for (Player player : players){
            if (maxCurse < getCurse(player.getUniqueId())){
                maxCurse = getCurse(player.getUniqueId());
            }
        }

        return maxCurse;
    }
}
