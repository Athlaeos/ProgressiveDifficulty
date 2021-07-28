package me.athlaeos.progressivelydifficultmobs.items;

import me.athlaeos.progressivelydifficultmobs.main.Main;
import me.athlaeos.progressivelydifficultmobs.managers.*;
import me.athlaeos.progressivelydifficultmobs.pojo.ActiveItem;
import me.athlaeos.progressivelydifficultmobs.utils.Utils;
import org.bukkit.*;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Cleanse1CurseItem extends ActiveItem {
    private final int cooldown;
    YamlConfiguration config;

    public Cleanse1CurseItem(){
        key = new NamespacedKey(Main.getInstance(), "pdm-cleanse_single_curse");
        Main.getInstance().getServer().removeRecipe(key);
        config = ConfigManager.getInstance().getConfig("items.yml").get();
        curseLimit = config.getInt("cleanse_single_curse_item.curse_limit");
        useRecipe = config.getBoolean("cleanse_single_curse_item.use_recipe");
        cooldown = config.getInt("cleanse_single_curse_item.cooldown");

        Material m;
        try {
            m = Material.valueOf(config.getString("cleanse_single_curse_item.item.material"));
        } catch (IllegalArgumentException e) {
            System.out.println("The cleanse 1 curse item's material could not be found, defaulting to STICK");
            m = Material.STICK;
        }
        ItemStack item = Utils.createItemStack(
                m,
                Utils.chat(config.getString("cleanse_single_curse_item.item.name")),
                config.getStringList("cleanse_single_curse_item.item.lore"));
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
        List<String> shape = config.getStringList("cleanse_single_curse_item.recipe.shape");
        try {
            r.shape(shape.get(0), shape.get(1), shape.get(2));
        } catch (NullPointerException e){
            System.out.println("Invalid crafting shape for the cleanse 1 curse item, cancelled crafting recipe");
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
            if (config.getString("cleanse_single_curse_item.recipe." + c) == null){
                System.out.println("Error: Cleanse 1 curse item. Material " + c + " in the shape grid was not defined after, cancelled crafting recipe");
                return;
            } else {
                try {
                    r.setIngredient(c, Material.valueOf(config.getString("cleanse_single_curse_item.recipe." + c)));
                } catch (IllegalArgumentException e){
                    System.out.println("Invalid crafting material for the cleanse 1 curse item at " + c + ", cancelled crafting recipe");
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
        if (PlayerCurseManager.getInstance().getCurse(p.getUniqueId()) == 0) return;
        if (PlayerCurseManager.getInstance().getCurse(p.getUniqueId()) < curseLimit){
            if (CooldownManager.getInstance().cooldownLowerThanZero(p, "cleanse_one_curse_cooldown") || p.hasPermission("pdm.cancelitemcooldowns")){
                if (p.getGameMode() != GameMode.CREATIVE){
                    p.getInventory().getItemInMainHand().setAmount(p.getInventory().getItemInMainHand().getAmount() - 1);
                }
                if (!p.hasPermission("pdm.cancelitemcooldowns")){
                    CooldownManager.getInstance().setCooldown(p, cooldown * 1000, "cleanse_one_curse_cooldown");
                }
                PlayerCurseManager.getInstance().addCurse(p.getUniqueId(), -1);
                if (PluginConfigurationManager.getInstance().useAnimationRunnables()){
                    new BukkitRunnable(){
                        int timer = 0;
                        @Override
                        public void run() {
                            if (timer < 2){
                                p.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 20, 0));
                            } else {
                                p.playSound(p.getLocation(), Sound.ENTITY_WITHER_AMBIENT, 0.2F, 1.0F);
                                p.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 60, 0));
                                p.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 60, 0));
                                if (PluginConfigurationManager.getInstance().useAnimationParticles()){
                                    for (int i = 0; i < 10; i++){
                                        p.spawnParticle(Particle.SMOKE_LARGE, p.getLocation(), 0, 0, 0.1 + (i*0.01), 0);
                                    }
                                }
                                cancel();
                            }
                            timer++;
                        }
                    }.runTaskTimer(Main.getInstance(), 0, 20);
                } else {
                    p.playSound(p.getLocation(), Sound.ENTITY_WITHER_AMBIENT, 0.2F, 1.0F);
                    p.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 60, 0));
                    p.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 60, 0));
                    if (PluginConfigurationManager.getInstance().useAnimationParticles()){
                        for (int i = 0; i < 10; i++){
                            p.spawnParticle(Particle.SMOKE_LARGE, p.getLocation(), 0, 0, 0.1 + (i*0.01), 0);
                        }
                    }
                }
            } else {
                p.sendMessage(Utils.chat(PluginConfigurationManager.getInstance().getPlayerCantUseItemError()
                        .replace("{duration}", String.format("%s", Utils.msToTimestamp(CooldownManager.getInstance().getCooldown(p, "cleanse_one_curse_cooldown"))))));
            }
        } else {
            p.sendMessage(Utils.chat(PluginConfigurationManager.getInstance().getPlayerTooMuchCurseError()));
        }
    }
}
