package me.athlaeos.progressivelydifficultmobs.listeners;

import me.athlaeos.progressivelydifficultmobs.main.Main;
import me.athlaeos.progressivelydifficultmobs.managers.PlayerKarmaManager;
import me.athlaeos.progressivelydifficultmobs.managers.PluginConfigurationManager;
import me.athlaeos.progressivelydifficultmobs.states.KarmaGainReason;
import me.athlaeos.progressivelydifficultmobs.utils.Utils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.raid.RaidFinishEvent;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

public class RaidWonListener implements Listener {

    @EventHandler
    public void onRaidWon(RaidFinishEvent e){
        PlayerKarmaManager karmaManager = PlayerKarmaManager.getInstance();
        PluginConfigurationManager configManager = PluginConfigurationManager.getInstance();
        new BukkitRunnable(){
            @Override
            public void run() {
                if (e.getWinners().size() == 0){
                    for (Player p : Utils.getPlayersInArea(e.getRaid().getLocation(), 96)){
                        if (!p.hasPotionEffect(PotionEffectType.HERO_OF_THE_VILLAGE)){
                            karmaManager.addKarma(p, configManager.getKarmaOnRaidLoss(),
                                    KarmaGainReason.RAID_LOST,
                                    true, false);
                        } else {
                            karmaManager.addKarma(p, configManager.getKarmaOnRaidWin(),
                                    KarmaGainReason.RAID_WON,
                                    true, false);
                        }
                    }
                } else {
                    for (Player p : e.getWinners()){
                        karmaManager.addKarma(p, configManager.getKarmaOnRaidWin(),
                                KarmaGainReason.RAID_WON,
                                true, false);
                    }
                }
            }
        }.runTaskLater(Main.getInstance(), 20);
    }
}
