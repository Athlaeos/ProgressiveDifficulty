package me.athlaeos.progressivelydifficultmobs.managers;

import me.athlaeos.progressivelydifficultmobs.events.PlayerKarmaGainEvent;
import me.athlaeos.progressivelydifficultmobs.main.Main;
import me.athlaeos.progressivelydifficultmobs.states.KarmaGainReason;
import me.athlaeos.progressivelydifficultmobs.utils.Utils;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;

import java.util.List;
import java.util.UUID;

public class PlayerKarmaManager {

    private static PlayerKarmaManager manager = null;
    private final NamespacedKey keyKarma = new NamespacedKey(Main.getInstance(), "pdm-karma");
    private final NamespacedKey keyBadKarmaMult = new NamespacedKey(Main.getInstance(), "pdm-bad_karma_multiplier");
    private final NamespacedKey keyGoodKarmaMult = new NamespacedKey(Main.getInstance(), "pdm-good_karma_multiplier");
    private PluginConfigurationManager config;
    private double minKarma;
    private double maxKarma;

    public PlayerKarmaManager() {
        config = PluginConfigurationManager.getInstance();
        minKarma = config.getMinKarmaLevel() * 100 - config.getKarmaBuffer();
        maxKarma = config.getMaxKarmaLevel() * 100 + config.getKarmaBuffer();
    }

    public static PlayerKarmaManager getInstance() {
        if (manager == null) {
            manager = new PlayerKarmaManager();
        }
        return manager;
    }

    /**
     * Reloads the class and its configuration settings.
     */
    public void reload(){
        manager = null;
        config = PluginConfigurationManager.getInstance();
    }

    /**
     * Get the karma of a player by their UUID
     * @param uuid the UUID of the player
     * @return the double amount of karma a player has
     */
    public double getKarma(UUID uuid) {
        Player p = Main.getInstance().getServer().getPlayer(uuid);
        if (p == null){
            return 0D;
        }
        if (!p.getPersistentDataContainer().has(keyKarma, PersistentDataType.DOUBLE)){
            p.getPersistentDataContainer().set(keyKarma, PersistentDataType.DOUBLE, config.getBaseKarma());
        }
        return p.getPersistentDataContainer().get(keyKarma, PersistentDataType.DOUBLE);
    }

    /**
     * Set the karma of the player to the given amount
     *
     * This does not trigger a PlayerKarmaGainEvent
     * @param uuid the UUID of the player
     * @param newKarma the amount of karma the player's karma is being set to
     */
    public void setKarma(UUID uuid, double newKarma) {
        Player p = Main.getInstance().getServer().getPlayer(uuid);
        if (p == null){
            return;
        }
        if (newKarma < minKarma){
            newKarma = minKarma;
        } else if (newKarma > maxKarma){
            newKarma = maxKarma;
        }
        p.getPersistentDataContainer().set(keyKarma, PersistentDataType.DOUBLE, newKarma);
    }

    /**
     * Get the bad karma multiplier of a player by their UUID
     * This karma multiplier multiplies any negative karma they obtain by the returned amount
     * @param uuid the UUID of the player
     * @return the double amount the player has as their bad karma multiplier, or the default amount if none was found.
     */
    public double getBadKarmaMultiplier(UUID uuid){
        Player p = Main.getInstance().getServer().getPlayer(uuid);
        if (p == null){
            return 0D;
        }
        if (!p.getPersistentDataContainer().has(keyBadKarmaMult, PersistentDataType.DOUBLE)){
            p.getPersistentDataContainer().set(keyBadKarmaMult, PersistentDataType.DOUBLE, PluginConfigurationManager.getInstance().getBaseBadKarmaMultiplier());
        }
        return p.getPersistentDataContainer().get(keyBadKarmaMult, PersistentDataType.DOUBLE);
    }

    /**
     * Get the good karma multiplier of a player by their UUID
     * This karma multiplier multiplies any positive karma they obtain by the returned amount.
     * @param uuid the UUID of the player
     * @return the double amount the player has as their good karma multiplier, or the default amount if none was found.
     */
    public double getGoodKarmaMultiplier(UUID uuid){
        Player p = Main.getInstance().getServer().getPlayer(uuid);
        if (p == null){
            return 0D;
        }
        if (!p.getPersistentDataContainer().has(keyGoodKarmaMult, PersistentDataType.DOUBLE)){
            p.getPersistentDataContainer().set(keyGoodKarmaMult, PersistentDataType.DOUBLE, PluginConfigurationManager.getInstance().getBaseGoodKarmaMultiplier());
        }
        return p.getPersistentDataContainer().get(keyGoodKarmaMult, PersistentDataType.DOUBLE);
    }

    /**
     * Sets the bad karma multiplier of the player to the given amount
     * This karma multiplier multiplies any negative karma they obtain by the given amount
     * @param uuid the UUID of the player
     * @param multiplier the bad karma multiplier of the player
     */
    public void setBadKarmaMultiplier(UUID uuid, double multiplier){
        Player p = Main.getInstance().getServer().getPlayer(uuid);
        if (p == null){
            return;
        }
        p.getPersistentDataContainer().set(keyBadKarmaMult, PersistentDataType.DOUBLE, multiplier);
    }

    /**
     * Sets the good karma multiplier of the player to the given amount
     * This karma multiplier multiplies any positive karma they obtain by the given amount
     * @param uuid the UUID of the player
     * @param multiplier the good karma multiplier of the player
     */
    public void setGoodKarmaMultiplier(UUID uuid, double multiplier){
        Player p = Main.getInstance().getServer().getPlayer(uuid);
        if (p == null){
            return;
        }
        p.getPersistentDataContainer().set(keyBadKarmaMult, PersistentDataType.DOUBLE, multiplier);
    }

    /**
     * Adds the given amount of karma to a player.
     * This amount will be reduced based on their current amount of karma if so configured.
     * The equation for this reduction can be defined in the config
     *
     * @param p the player given karma
     * @param amount the amount of karma given to the player
     * @param withMitigations set to true if karma gain should be reduced based on the player's current karma
     * @param ignoreMultiplier set to true if karma gain should not be increased based on the player's karma multiplier
     */
    public void addKarma(Player p, double amount, KarmaGainReason state, boolean withMitigations, boolean ignoreMultiplier){
        Main.getInstance().getServer().getPluginManager().callEvent(new PlayerKarmaGainEvent(p, amount, state, withMitigations, ignoreMultiplier));
    }

    /**
     * Get the current difficulty level of the given Player by their UUID
     *
     * @param uuid the UUID of the player
     * @return the integer difficulty level of the player
     */
    public int getKarmaLevel(UUID uuid) {
        double playerKarma = getKarma(uuid);
        int level = getKarmaLevel(playerKarma);
        return level;
    }

    /**
     * Get the difficulty level based on the given amount of karma
     * Ex: karma = 788 will return a difficulty level of 7
     * Ex: karma = -199 will return a difficulty level of -1
     *
     * @param karma the amount of karma the level is determined from
     * @return the integer difficulty level of the given amount of karma
     */
    public int getKarmaLevel(double karma) {
        int level = 0;
        if (karma < 0){
            level = ((int) Math.floor(-karma / 100));
            if (level > -config.getMinKarmaLevel()){
                return config.getMinKarmaLevel();
            }
            return -level;
        } else {
            level = ((int) Math.floor(karma / 100));
            if (level > config.getMaxKarmaLevel()){
                return config.getMaxKarmaLevel();
            }
            return level;
        }
    }

    /**
     * Get the average amount of karma of players surrounding the given Location
     * This will consider players within a radius of 128 blocks around the location.
     *
     * @param l the location the average karma of surrounding players is determined from
     * @return the double amount of karma of the average of all players in a 128 radius around the given location
     */
    public double getAverageKarmaSurroundingPlayers(Location l){
        List<Player> players = Utils.getPlayersInArea(l, 128);
        double collectiveKarma = 0.0;
        int playerCount = 0;
        for (Player player : players){
            if (JudgedPlayersManager.getInstance().isPlayerJudged(player)){
                collectiveKarma += manager.getKarma(player.getUniqueId());
                playerCount++;
            }
        }
        return collectiveKarma / playerCount;
    }

    public double getMinKarma() {
        return minKarma;
    }

    public double getMaxKarma() {
        return maxKarma;
    }
}
