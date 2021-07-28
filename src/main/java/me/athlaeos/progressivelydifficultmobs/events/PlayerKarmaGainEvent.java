package me.athlaeos.progressivelydifficultmobs.events;

import me.athlaeos.progressivelydifficultmobs.states.KarmaGainReason;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.UUID;

public class PlayerKarmaGainEvent extends Event implements Cancellable {
    private boolean isCancelled = false;
    private static final HandlerList HANDLER_LIST = new HandlerList();
    private Player player;
    private double amount;
    private boolean hasDiminishingEffects;
    private boolean ignoreKarmaMultiplier;
    private KarmaGainReason state;

    public PlayerKarmaGainEvent(Player player, double amount, KarmaGainReason state, boolean withDiminishingEffects, boolean ignoreKarmaMultiplier){
        this.player = player;
        this.amount = amount;
        this.state = state;
        hasDiminishingEffects = withDiminishingEffects;
        this.ignoreKarmaMultiplier = ignoreKarmaMultiplier;
    }

    /**
     * Says if the event is cancelled or not. If true, the player does not receive any karma.
     * @return true if the event is cancelled, false otherwise.
     */
    @Override
    public boolean isCancelled() {
        return isCancelled;
    }

    /**
     * Set if the event is cancelled or not. If cancelled (true) the player does not receive karma.
     * @param cancel
     */
    @Override
    public void setCancelled(boolean cancel) {
        isCancelled = cancel;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLER_LIST;
    }

    /**
     * @return The amount of karma attempted to be given to the player
     */
    public double getAmount() {
        return amount;
    }

    /**
     * Sets the amount of karma attempted to be given to the player
     * @param amount the amount of karma given
     */
    public void setAmount(double amount) {
        this.amount = amount;
    }

    /**
     * @return the UUID of the player given karma
     */
    public Player getPlayer() {
        return player;
    }

    /**
     * Sets the player who's being given karma
     * @param player the UUID of the player being given karma
     */
    public void setPlayer(Player player) {
        this.player = player;
    }

    /**
     * The circumstances in which the player is given karma
     * @return a KarmaGainState
     */
    public KarmaGainReason getState() {
        return state;
    }

    /**
     * Changes the circumstances in which the player is given karma
     * @param state the circumstance in which player is given karma
     */
    public void setState(KarmaGainReason state) {
        this.state = state;
    }

    /**
     * Sets whether or not karma given is reduced based on the player's current karma.
     * Ex: If false, Players with 700 karma given 100 karma will now have 800 karma,
     * If true, they will not get the full 100 karma.
     * @param mitigated sets if karma is reduced by diminishing returns
     */
    public void setWithDiminishingEffects(boolean mitigated) {
        this.hasDiminishingEffects = mitigated;
    }

    /**
     * @return true if karma is reduced based on diminishing returns, false otherwise.
     */
    public boolean hasDiminishingEffects(){
        return hasDiminishingEffects;
    }

    /**
     * Sets whether or not karma given ignores the player's karma multiplier
     * Ex: If true, when a player gets 100 karma and they have a good karma multiplier of 1.5x,
     * they will be given 100 before mitigations. If false, this would be 150 karma.
     * @param ignoreKarmaMultiplier sets if karma given ignores the player's karma multiplier
     */
    public void setIgnoreKarmaMultiplier(boolean ignoreKarmaMultiplier) {
        this.ignoreKarmaMultiplier = ignoreKarmaMultiplier;
    }

    /**
     * @return true if karma given ignores the player's karma multiplier, false otherwise.
     */
    public boolean ignoreKarmaMultiplier(){
        return ignoreKarmaMultiplier;
    }

    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }
}
