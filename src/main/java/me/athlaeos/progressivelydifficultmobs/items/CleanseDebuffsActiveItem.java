package me.athlaeos.progressivelydifficultmobs.items;

import me.athlaeos.progressivelydifficultmobs.main.Main;
import me.athlaeos.progressivelydifficultmobs.managers.ConfigManager;
import me.athlaeos.progressivelydifficultmobs.managers.CooldownManager;
import me.athlaeos.progressivelydifficultmobs.managers.PlayerCurseManager;
import me.athlaeos.progressivelydifficultmobs.managers.PluginConfigurationManager;
import me.athlaeos.progressivelydifficultmobs.pojo.ActiveItem;
import me.athlaeos.progressivelydifficultmobs.utils.Utils;
import org.bukkit.*;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityPotionEffectEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CleanseDebuffsActiveItem extends ActiveItem {
    private final int cooldown;
    private final int duration;
    private final List<PotionEffectType> potionEffectsToCleanse = Arrays.asList(
            PotionEffectType.BLINDNESS,
            PotionEffectType.CONFUSION,
            PotionEffectType.POISON,
            PotionEffectType.WITHER,
            PotionEffectType.SLOW,
            PotionEffectType.HUNGER,
            PotionEffectType.LEVITATION,
            PotionEffectType.SLOW_DIGGING,
            PotionEffectType.UNLUCK,
            PotionEffectType.WEAKNESS
    );
    YamlConfiguration config;

    public CleanseDebuffsActiveItem(){
        key = new NamespacedKey(Main.getInstance(), "pdm-cleanse-debuffs");
        config = ConfigManager.getInstance().getConfig("items.yml").get();
        curseLimit = config.getInt("cleanse_debuffs.curse_limit");
        useRecipe = config.getBoolean("cleanse_debuffs.use_recipe");
        cooldown = config.getInt("cleanse_debuffs.cooldown");
        duration = config.getInt("cleanse_debuffs.duration");

        Material m;
        try {
            m = Material.valueOf(config.getString("cleanse_debuffs.item.material"));
        } catch (IllegalArgumentException e) {
            System.out.println("The cleanse debuffs item's material could not be found, defaulting to STICK");
            m = Material.STICK;
        }
        ItemStack item = Utils.createItemStack(
                m,
                Utils.chat(config.getString("cleanse_debuffs.item.name")),
                config.getStringList("cleanse_debuffs.item.lore"));
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
        List<String> shape = config.getStringList("cleanse_debuffs.recipe.shape");
        try {
            r.shape(shape.get(0), shape.get(1), shape.get(2));
        } catch (NullPointerException e){
            System.out.println("Invalid crafting shape for the cleanse all curse item, cancelled crafting recipe");
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
            if (config.getString("cleanse_debuffs.recipe." + c) == null){
                System.out.println("Error: Cleanse debuffs item. Material " + c + " in the shape grid was not defined after, cancelled crafting recipe");
                return;
            } else {
                try {
                    r.setIngredient(c, Material.valueOf(config.getString("cleanse_debuffs.recipe." + c)));
                } catch (IllegalArgumentException e){
                    System.out.println("Invalid crafting material for the cleanse debuffs item at " + c + ", cancelled crafting recipe");
                    return;
                }
            }
        }
        recipe = r;
        Main.getInstance().getServer().addRecipe(recipe);
    }

    @Override
    public void execute(Event e) {
        PlayerInteractEvent event = (PlayerInteractEvent) e;
        event.setCancelled(true);
        Player p = event.getPlayer();
        if (PlayerCurseManager.getInstance().getCurse(p.getUniqueId()) < curseLimit){
            if (CooldownManager.getInstance().cooldownLowerThanZero(p, "cleanse_debuffs_cooldown") || p.hasPermission("pdm.cancelitemcooldowns")){
                if (p.getGameMode() != GameMode.CREATIVE){
                    p.getInventory().getItemInMainHand().setAmount(p.getInventory().getItemInMainHand().getAmount() - 1);
                }
                if (!p.hasPermission("pdm.cancelitemcooldowns")){
                    CooldownManager.getInstance().setCooldown(p, cooldown * 1000, "cleanse_debuffs_cooldown");
                }
                CooldownManager.getInstance().setCooldown(p, duration * 1000, "cleanse_debuffs_duration");
                for (PotionEffect type : p.getActivePotionEffects()){
                    if (potionEffectsToCleanse.contains(type.getType())){
                        p.removePotionEffect(type.getType());
                    }
                }
            } else {
                p.sendMessage(Utils.chat(PluginConfigurationManager.getInstance().getPlayerCantUseItemError()
                        .replace("{duration}", String.format("%s", Utils.msToTimestamp(CooldownManager.getInstance().getCooldown(p, "cleanse_debuffs_cooldown"))))));
            }
        } else {
            p.sendMessage(Utils.chat(PluginConfigurationManager.getInstance().getPlayerTooMuchCurseError()));
        }
    }
}
