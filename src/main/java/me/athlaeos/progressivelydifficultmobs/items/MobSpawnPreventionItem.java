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

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MobSpawnPreventionItem extends ActiveItem {
    private final int duration;
    private final int cooldown;
    YamlConfiguration config;

    public MobSpawnPreventionItem(){
        key = new NamespacedKey(Main.getInstance(), "pdm-mob_spawn_prevention");
        config = ConfigManager.getInstance().getConfig("items.yml").get();
        curseInfluence = config.getInt("mob_spawn_prevention_item.curse_influence");
        curseLimit = config.getInt("mob_spawn_prevention_item.curse_limit");
        useRecipe = config.getBoolean("mob_spawn_prevention_item.use_recipe");
        duration = config.getInt("mob_spawn_prevention_item.duration");
        cooldown = config.getInt("mob_spawn_prevention_item.cooldown");

        Material m;
        try {
            m = Material.valueOf(config.getString("mob_spawn_prevention_item.item.material"));
        } catch (IllegalArgumentException e) {
            System.out.println("The mob spawn prevention item's material could not be found, defaulting to STICK");
            m = Material.STICK;
        }
        ItemStack item = Utils.createItemStack(
                m,
                Utils.chat(config.getString("mob_spawn_prevention_item.item.name")),
                config.getStringList("mob_spawn_prevention_item.item.lore"));
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
        List<String> shape = config.getStringList("mob_spawn_prevention_item.recipe.shape");
        try {
            r.shape(shape.get(0), shape.get(1), shape.get(2));
        } catch (NullPointerException e){
            System.out.println("Invalid crafting shape for the mob spawn prevention item, cancelled crafting recipe");
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
            if (config.getString("mob_spawn_prevention_item.recipe." + c) == null){
                System.out.println("Error: Mob spawn prevention item. Material " + c + " in the shape grid was not defined after, cancelled crafting recipe");
                return;
            } else {
                try {
                    r.setIngredient(c, Material.valueOf(config.getString("mob_spawn_prevention_item.recipe." + c)));
                } catch (IllegalArgumentException e){
                    System.out.println("Invalid crafting material for the mob spawn prevention item at " + c + ", cancelled crafting recipe");
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
            if (CooldownManager.getInstance().cooldownLowerThanZero(p, "local_peaceful_mode_cooldown") || p.hasPermission("pdm.cancelitemcooldowns")){
                PlayerCurseManager.getInstance().addCurse(p.getUniqueId(), curseInfluence);
                if (p.getGameMode() != GameMode.CREATIVE){
                    p.getInventory().getItemInMainHand().setAmount(p.getInventory().getItemInMainHand().getAmount() - 1);
                }
                if (!p.hasPermission("pdm.cancelitemcooldowns")){
                    CooldownManager.getInstance().setCooldown(p, cooldown * 1000, "local_peaceful_mode_cooldown");
                }
                CooldownManager.getInstance().setCooldown(p, duration * 1000, "local_peaceful_mode_duration");
                p.playSound(p.getLocation(), Sound.ENTITY_WITHER_SPAWN, 0.3F, 1.0F);
                if (PluginConfigurationManager.getInstance().useAnimationParticles()){
                    for (Location l : Utils.getCircle(p.getLocation(), 2, 64)){
                        p.getWorld().spawnParticle(Particle.FLAME, l, 0,
                                0,
                                0.1,
                                0);
                        p.getWorld().spawnParticle(Particle.SMOKE_LARGE, l, 0,
                                0,
                                0.05,
                                0);
                    }
                }
            } else {
                p.sendMessage(Utils.chat(PluginConfigurationManager.getInstance().getPlayerCantUseItemError()
                .replace("{duration}", String.format("%s", Utils.msToTimestamp(CooldownManager.getInstance().getCooldown(p, "local_peaceful_mode_cooldown"))))));

            }
        } else {
            p.sendMessage(Utils.chat(PluginConfigurationManager.getInstance().getPlayerTooMuchCurseError()));
        }
    }
}
