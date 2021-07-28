package me.athlaeos.progressivelydifficultmobs.listeners;

import me.athlaeos.progressivelydifficultmobs.managers.ConfigManager;
import me.athlaeos.progressivelydifficultmobs.managers.PlayerKarmaManager;
import me.athlaeos.progressivelydifficultmobs.managers.PlayerPerksManager;
import me.athlaeos.progressivelydifficultmobs.perks.Perk;
import me.athlaeos.progressivelydifficultmobs.pojo.Config;
import me.athlaeos.progressivelydifficultmobs.states.KarmaGainReason;
import org.bukkit.CropState;
import org.bukkit.Material;
import org.bukkit.block.data.Ageable;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import java.util.*;

public class BlockBreakListener implements Listener {

    private Map<Material, Double> karmaGrantedOnBlockBreak;
    private List<CropState> states;
    private static BlockBreakListener listener = null;

    public BlockBreakListener(){
        listener = this;
        loadConfig();
    }

    public static BlockBreakListener getInstance(){
        return listener;
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent e){
        if (!e.isCancelled()){
            PlayerPerksManager manager = PlayerPerksManager.getInstance();
            for (Perk perk : manager.sortPerksByPriority(manager.getPlayersTotalPerks(e.getPlayer()))){
                perk.execute(e);
            }
            if (karmaGrantedOnBlockBreak.keySet().contains(e.getBlock().getType())){
                if (e.getBlock().getBlockData() instanceof Ageable){
                    Ageable crop = (Ageable) e.getBlock().getBlockData();
                    if (crop.getAge() != crop.getMaximumAge()){
                        return;
                    }
                }

                PlayerKarmaManager.getInstance().addKarma(
                        e.getPlayer(),
                        karmaGrantedOnBlockBreak.get(e.getBlock().getType()),
                        KarmaGainReason.BLOCK_BREAK,
                        true, false);
            }
        }
    }

    public void loadConfig(){
        karmaGrantedOnBlockBreak = new HashMap<>();
        states = new ArrayList<>(Arrays.asList(CropState.values()));
        states.remove(CropState.RIPE);

        Config config = ConfigManager.getInstance().getConfig("karma.yml");
        YamlConfiguration yaml = config.get();
        for (String p : yaml.getConfigurationSection("karma_block_break").getKeys(false)){
            Material m;
            try {
                m = Material.valueOf(p);
            } catch (IllegalArgumentException e){
                System.out.println("Could not register type " + p + ". Are you sure you spelled it correctly?");
                continue;
            }
            karmaGrantedOnBlockBreak.put(m, yaml.getDouble("karma_block_break." + p));
        }

        config.copyDefaults(true).save();
    }
}
