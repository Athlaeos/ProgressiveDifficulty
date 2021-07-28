package me.athlaeos.progressivelydifficultmobs.items;

import me.athlaeos.progressivelydifficultmobs.main.Main;
import me.athlaeos.progressivelydifficultmobs.managers.ActiveItemManager;
import me.athlaeos.progressivelydifficultmobs.managers.ConfigManager;
import me.athlaeos.progressivelydifficultmobs.managers.LeveledMonsterManager;
import me.athlaeos.progressivelydifficultmobs.managers.PluginConfigurationManager;
import me.athlaeos.progressivelydifficultmobs.pojo.ActiveItem;
import me.athlaeos.progressivelydifficultmobs.pojo.LeveledMonster;
import me.athlaeos.progressivelydifficultmobs.utils.Utils;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffectType;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class RevealEvilItem extends ActiveItem {

    private final NamespacedKey curseKey = new NamespacedKey(Main.getInstance(), "pdm-curse");
    private final NamespacedKey karmaKey = new NamespacedKey(Main.getInstance(), "pdm-karma");
    private final NamespacedKey monsterKey = new NamespacedKey(Main.getInstance(), "pdm-monsterID");
    YamlConfiguration config;

    private final boolean counterInvisibility;

    public RevealEvilItem(){
        key = new NamespacedKey(Main.getInstance(), "pdm-revealing");
        config = ConfigManager.getInstance().getConfig("items.yml").get();
        curseInfluence = config.getInt("evil_revealing_item.curse_influence");
        curseLimit = config.getInt("evil_revealing_item.curse_limit");
        useRecipe = config.getBoolean("evil_revealing_item.use_recipe");
        counterInvisibility = config.getBoolean("evil_revealing_item.counter_invisibility");

        Material m;
        try {
            m = Material.valueOf(config.getString("evil_revealing_item.item.material"));
        } catch (IllegalArgumentException e) {
            System.out.println("The evil revealing item's material could not be found, defaulting to STICK");
            m = Material.STICK;
        }
        ItemStack item = Utils.createItemStack(
                m,
                Utils.chat(config.getString("evil_revealing_item.item.name")),
                config.getStringList("evil_revealing_item.item.lore"));
        this.item = item;

        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.getPersistentDataContainer().set(key, PersistentDataType.INTEGER, 1);
        item.setItemMeta(itemMeta);

        if (useRecipe){
            createRecipe();
        }
    }

    private void createRecipe(){
        ShapedRecipe r = new ShapedRecipe(key, item);
        List<String> shape = config.getStringList("evil_revealing_item.recipe.shape");
        try {
            r.shape(shape.get(0), shape.get(1), shape.get(2));
        } catch (NullPointerException e){
            System.out.println("Invalid crafting shape for the evil revealing item, cancelled crafting recipe");
            return;
        }
        Set<Character> materials = new HashSet<>();
        for (String s : shape){
            for (char c : s.toCharArray()){
                if (c != ' '){
                    materials.add(c);
                }
            }
        }
        for (char c : materials){
            if (config.getString("evil_revealing_item.recipe." + c) == null){
                System.out.println("Error: Evil revealing item. Material " + c + " in the shape grid was not defined after, cancelled crafting recipe");
                return;
            } else {
                try {
                    r.setIngredient(c, Material.valueOf(config.getString("evil_revealing_item.recipe." + c)));
                } catch (IllegalArgumentException e){
                    System.out.println("Invalid crafting material for the evil revealing item at " + c + ", cancelled crafting recipe");
                    return;
                }
            }
        }
        recipe = r;
        Main.getInstance().getServer().addRecipe(recipe);
    }


    @Override
    public void execute(Event e) {
        PlayerInteractEntityEvent event = (PlayerInteractEntityEvent) e;
        event.setCancelled(true);
        Entity entity = event.getRightClicked();
        Player whoClicked = event.getPlayer();
        if (entity instanceof Player){
            Player p = (Player) entity;
            int playerCurse = 0;
            double playerKarma = 0;
            if (counterInvisibility){
                if (p.hasPotionEffect(PotionEffectType.INVISIBILITY)){
                    p.removePotionEffect(PotionEffectType.INVISIBILITY);
                }
            }
            if (p.getPersistentDataContainer().has(curseKey, PersistentDataType.INTEGER)){
                playerCurse = p.getPersistentDataContainer().get(curseKey, PersistentDataType.INTEGER);
            }
            if (p.getPersistentDataContainer().has(karmaKey, PersistentDataType.DOUBLE)){
                playerKarma = p.getPersistentDataContainer().get(karmaKey, PersistentDataType.DOUBLE);
            }
            for (String line : PluginConfigurationManager.getInstance().getEvilRevealingPlayer()){
                whoClicked.sendMessage(Utils.chat(line
                        .replace("{player}", p.getName())
                        .replace("{curse}", "" + playerCurse)
                        .replace("{karma}", "" + playerKarma)));
            }
        } else if (entity instanceof LivingEntity){
            LivingEntity clickedEntity = (LivingEntity) entity;
            boolean entityIsCursed = false;
            int entityDifficultyLevel = 0;
            if (counterInvisibility){
                if (clickedEntity.hasPotionEffect(PotionEffectType.INVISIBILITY)){
                    clickedEntity.removePotionEffect(PotionEffectType.INVISIBILITY);
                }
            }
            if (clickedEntity.getPersistentDataContainer().has(curseKey, PersistentDataType.STRING)){
                entityIsCursed = true;
            }
            PersistentDataContainer container = clickedEntity.getPersistentDataContainer();
            String mobId;
            if(container.has(monsterKey, PersistentDataType.STRING)) {
                mobId = container.get(monsterKey, PersistentDataType.STRING);
                LeveledMonster monster = LeveledMonsterManager.getInstance().getMonster(mobId);
                if (monster != null){
                    entityDifficultyLevel = monster.getLevel();
                }
            }
            for (String line : PluginConfigurationManager.getInstance().getEvilRevealingEntity()){
                whoClicked.sendMessage(Utils.chat(line
                        .replace("{type}", clickedEntity.getType().toString().toLowerCase().replace("_", " "))
                        .replace("{cursed}", (entityIsCursed) ? PluginConfigurationManager.getInstance().getTranslationYes() : PluginConfigurationManager.getInstance().getTranslationNo())
                        .replace("{level}", "" + entityDifficultyLevel)));
            }
        }
    }
}
