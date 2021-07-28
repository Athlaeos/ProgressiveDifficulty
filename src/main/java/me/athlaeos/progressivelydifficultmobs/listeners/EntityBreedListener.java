package me.athlaeos.progressivelydifficultmobs.listeners;

import me.athlaeos.progressivelydifficultmobs.managers.ConfigManager;
import me.athlaeos.progressivelydifficultmobs.managers.PlayerKarmaManager;
import me.athlaeos.progressivelydifficultmobs.managers.PlayerPerksManager;
import me.athlaeos.progressivelydifficultmobs.perks.Perk;
import me.athlaeos.progressivelydifficultmobs.pojo.Config;
import me.athlaeos.progressivelydifficultmobs.states.KarmaGainReason;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityBreedEvent;

import java.util.*;

public class EntityBreedListener implements Listener {

    private Map<EntityType, Double> karmaGrantedOnAnimalBred;
    private static EntityBreedListener listener = null;

    public EntityBreedListener(){
        listener = this;
        loadConfig();
    }

    public static EntityBreedListener getInstance(){
        return listener;
    }

    @EventHandler
    public void onBlockBreak(EntityBreedEvent e){
        if (!e.isCancelled()){
            if (e.getBreeder() instanceof Player){
                Player p = (Player) e.getBreeder();
                PlayerPerksManager manager = PlayerPerksManager.getInstance();
                for (Perk perk : manager.sortPerksByPriority(manager.getPlayersTotalPerks(p))){
                    perk.execute(e);
                }
                if (karmaGrantedOnAnimalBred.containsKey(e.getFather().getType())){
                    PlayerKarmaManager.getInstance().addKarma(p,
                            karmaGrantedOnAnimalBred.get(e.getFather().getType()),
                            KarmaGainReason.ENTITY_BREED,
                            true, false);
                }
            }
        }
    }

    public void loadConfig(){
        karmaGrantedOnAnimalBred = new HashMap<>();

        Config config = ConfigManager.getInstance().getConfig("karma.yml");
        YamlConfiguration yaml = config.get();
        for (String p : yaml.getConfigurationSection("karma_animal_breed").getKeys(false)){
            EntityType a;
            try {
                a = EntityType.valueOf(p);
            } catch (IllegalArgumentException e){
                System.out.println("Could not register entity type " + p + ". Are you sure you spelled it correctly?");
                continue;
            }
            karmaGrantedOnAnimalBred.put(a, yaml.getDouble("karma_animal_breed." + p));
        }

        config.copyDefaults(true).save();
    }
}
