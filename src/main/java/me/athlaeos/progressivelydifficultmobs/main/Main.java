package me.athlaeos.progressivelydifficultmobs.main;

import me.athlaeos.progressivelydifficultmobs.hooks.WorldguardHook;
import me.athlaeos.progressivelydifficultmobs.listeners.*;
import me.athlaeos.progressivelydifficultmobs.managers.*;
import me.athlaeos.progressivelydifficultmobs.persistence.AbilityPersister;
import me.athlaeos.progressivelydifficultmobs.persistence.LeveledMonsterPersister;
import me.athlaeos.progressivelydifficultmobs.persistence.LootTablePersister;
import me.athlaeos.progressivelydifficultmobs.pojo.Ability;
import me.athlaeos.progressivelydifficultmobs.pojo.LeveledMonster;
import me.athlaeos.progressivelydifficultmobs.utils.Utils;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.Listener;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public final class Main extends JavaPlugin {

    private static Main plugin;
    private LeveledMonsterManager monsterManager;

    @Override
    public void onEnable() {
        plugin = this;
        monsterManager = LeveledMonsterManager.getInstance();

        if (!(new File(this.getDataFolder(), "karma.yml").exists())){
            this.saveResource("karma.yml", false);
        }
        if (!(new File(this.getDataFolder(), "custom-abilities.yml").exists())){
            this.saveResource("custom-abilities.yml", false);
        }
        if (!(new File(this.getDataFolder(), "translations.yml").exists())){
            this.saveResource("translations.yml", false);
        }
        if (!(new File(this.getDataFolder(), "config.yml").exists())){
            this.saveResource("config.yml", false);
        }
        if (!(new File(this.getDataFolder(), "items.yml").exists())){
            this.saveResource("items.yml", false);
        }
        if (!(new File(this.getDataFolder(), "perks.yml").exists())){
            this.saveResource("perks.yml", false);
        }
        WorldguardManager.getWorldguardManager().registerWorldGuard();
        if (WorldguardManager.getWorldguardManager().useWorldGuard()){
            WorldguardHook.getInstance().registerFlags();
        }

        registerListeners();
        registerManagers();

        LootTablePersister.loadLootTables();
        LeveledMonsterPersister.loadMonsters();
        AbilityPersister.loadAbilities();
        PlayerPerksManager.getInstance().loadLevelPerks();

        EntityRunnableReloader();
    }

    @Override
    public void onDisable() {
        LootTablePersister.saveLootTables();
        LeveledMonsterPersister.saveLeveledMonsters();
        PlayerPerksManager.getInstance().saveLevelPerks();
    }

    private void registerListeners(){
        YamlConfiguration config = ConfigManager.getInstance().getConfig("config.yml").get();
        if (config.getBoolean("block_place")) registerListener(new BlockPlaceListener());
        if (config.getBoolean("block_break")) registerListener(new BlockBreakListener());
        if (config.getBoolean("animal_breed")) registerListener(new EntityBreedListener());
        if (config.getBoolean("entity_damage_entity")) registerListener(new EntityDamageByEntityListener());
        if (config.getBoolean("entity_damage")) registerListener(new EntityDamageListener());
        if (config.getBoolean("entity_killed")) registerListener(new EntityKilledListener());
        if (config.getBoolean("entity_spawn")) registerListener(new EntitySpawnListener());
        if (config.getBoolean("entity_target")) registerListener(new EntityTargetEntityListener());
        if (config.getBoolean("player_interact")) registerListener(new ItemInteractListener());
        if (config.getBoolean("player_interact_entity")) registerListener(new ItemInteractOnEntityListener());
        if (config.getBoolean("potion_effect_change")) registerListener(new PotionEffectChangeListener());
        if (config.getBoolean("raid_end")) registerListener(new RaidWonListener());

        getServer().getPluginManager().registerEvents(new MenuListener(), this);
        getServer().getPluginManager().registerEvents(new KarmaAquisitionListener(), this);
    }

    private void registerListener(Listener listener){
        getServer().getPluginManager().registerEvents(listener, this);
    }

    private void registerManagers(){
        new CommandManager(this);
        ActiveItemManager.getInstance();
        CooldownManager.getInstance();
    }

    public static Main getInstance(){
        return plugin;
    }

    private void EntityRunnableReloader(){
        NamespacedKey key = new NamespacedKey(Main.getInstance(), "pdm-monsterID");
        NamespacedKey monsterCursedKey = new NamespacedKey(Main.getInstance(), "pdm-curse");
        for (World w : this.getServer().getWorlds()){
            for (Entity e : w.getEntities()){
                if (!(e instanceof LivingEntity)) continue;
                PersistentDataContainer container = e.getPersistentDataContainer();
                if(container.has(key, PersistentDataType.INTEGER)) {
                    LeveledMonster monster = monsterManager.getMonster(container.get(key, PersistentDataType.STRING));
                    if (monster != null){
                        if (monster.isBoss()){
                            Utils.createBossBar(this, (LivingEntity) e, (monster.getDisplayName() == null) ? Utils.chat("&c&lBoss") : monster.getDisplayName(), BarColor.RED, BarStyle.SOLID, this.getConfig().getInt("boss_bar_view_distance"));
                        }
                    }

                    if (monster.getAbilities().size() != 0){
                        for (String a : monster.getAbilities()){
                            Ability ability = AbilityManager.getInstance().getRunningAbilities().get(a);
                            if (ability != null){
                                ability.execute(e, null, null);
                            }
                        }
                    }
                }

                if (container.has(monsterCursedKey, PersistentDataType.STRING)){
                    CursedMonsterManager.getInstance().addCursedEntity(e);
                }
            }
        }
    }
}
