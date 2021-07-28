package me.athlaeos.progressivelydifficultmobs.listeners;

import me.athlaeos.progressivelydifficultmobs.events.PlayerKarmaGainEvent;
import me.athlaeos.progressivelydifficultmobs.main.Main;
import me.athlaeos.progressivelydifficultmobs.managers.JudgedPlayersManager;
import me.athlaeos.progressivelydifficultmobs.managers.PlayerKarmaManager;
import me.athlaeos.progressivelydifficultmobs.managers.PlayerPerksManager;
import me.athlaeos.progressivelydifficultmobs.managers.PluginConfigurationManager;
import me.athlaeos.progressivelydifficultmobs.perks.Perk;
import me.athlaeos.progressivelydifficultmobs.utils.Utils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class KarmaAquisitionListener implements Listener {

    @EventHandler (priority = EventPriority.LOWEST)
    public void onKarmaGain(PlayerKarmaGainEvent e){
        PluginConfigurationManager config = PluginConfigurationManager.getInstance();
        PlayerKarmaManager karmaManager = PlayerKarmaManager.getInstance();
        Player onlinePlayer = e.getPlayer();
        PlayerPerksManager manager = PlayerPerksManager.getInstance();
        for (Perk perk : manager.sortPerksByPriority(manager.getPlayersTotalPerks(e.getPlayer()))){
            perk.execute(e);
        }
        boolean judgePlayer;
        if (config.isForceKarma()){
            if (onlinePlayer.hasPermission("pdm.ignoreforcekarma")){
                judgePlayer = JudgedPlayersManager.getInstance().isPlayerJudged(onlinePlayer);
            } else {
                judgePlayer = true;
                JudgedPlayersManager.getInstance().setPlayerJudged(onlinePlayer, true);
            }
        } else {
            judgePlayer = JudgedPlayersManager.getInstance().isPlayerJudged(onlinePlayer);
        }
        if (judgePlayer) {
            if (!e.isCancelled()) {
                double playersKarma = karmaManager.getKarma(onlinePlayer.getUniqueId());
                if ((playersKarma >= (config.getMaxKarmaLevel() * 100) || playersKarma <= (config.getMinKarmaLevel() * 100)) && config.isLockKarma()) {
                    return;
                }
                double karmaMultiplier;
                double karmaGained;

                if (e.hasDiminishingEffects()) {
                    if (e.getAmount() < 0) {
                        if (playersKarma < 0) {
                            karmaMultiplier = Utils.eval(config.getBadKarmaGainNegative().replace("{player_karma}", String.format("%.4f", playersKarma)));
                        } else {
                            karmaMultiplier = Utils.eval(config.getBadKarmaGainPositive().replace("{player_karma}", String.format("%.4f", playersKarma)));
                        }
                    } else {
                        if (playersKarma < 0) {
                            karmaMultiplier = Utils.eval(config.getGoodKarmaGainNegative().replace("{player_karma}", String.format("%.4f", playersKarma)));
                        } else {
                            karmaMultiplier = Utils.eval(config.getGoodKarmaGainPositive().replace("{player_karma}", String.format("%.4f", playersKarma)));
                        }
                    }
                } else {
                    karmaMultiplier = 1D;
                }
                karmaGained = e.getAmount() * karmaMultiplier;
                if (!e.ignoreKarmaMultiplier()) {
                    if (karmaGained < 0) {
                        karmaGained *= karmaManager.getBadKarmaMultiplier(e.getPlayer().getUniqueId());
                    } else if (karmaGained > 0) {
                        karmaGained *= karmaManager.getGoodKarmaMultiplier(e.getPlayer().getUniqueId());
                    }
                }
                if (playersKarma + karmaGained >= (karmaManager.getMaxKarma())) {
                    karmaManager.setKarma(e.getPlayer().getUniqueId(), (karmaManager.getMaxKarma()));
                } else if (playersKarma + karmaGained <= karmaManager.getMinKarma()) {
                    karmaManager.setKarma(e.getPlayer().getUniqueId(), karmaManager.getMinKarma());
                } else {
                    karmaManager.setKarma(e.getPlayer().getUniqueId(), playersKarma + karmaGained);
                }
            }
        }
    }
}
