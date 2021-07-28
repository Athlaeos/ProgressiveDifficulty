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

public class CursedEnemyDropsEnableItem extends ActiveItem {
    private final int cooldown;
    private final int duration;
    private final double meleeDebuff;
    private final double rangedDebuff;
    YamlConfiguration config;

    public CursedEnemyDropsEnableItem(){
        key = new NamespacedKey(Main.getInstance(), "pdm-enable_curse_drops");
        config = ConfigManager.getInstance().getConfig("items.yml").get();
        curseInfluence = config.getInt("enable_cursed_drops_item.curse_influence");
        curseLimit = config.getInt("enable_cursed_drops_item.curse_limit");
        useRecipe = config.getBoolean("enable_cursed_drops_item.use_recipe");
        cooldown = config.getInt("enable_cursed_drops_item.cooldown");
        duration = config.getInt("enable_cursed_drops_item.duration");
        meleeDebuff = config.getDouble("enable_cursed_drops_item.melee_debuff");
        rangedDebuff = config.getDouble("enable_cursed_drops_item.ranged_debuff");

        Material m;
        try {
            m = Material.valueOf(config.getString("enable_cursed_drops_item.item.material"));
        } catch (IllegalArgumentException e) {
            System.out.println("The enable curse drops item's material could not be found, defaulting to STICK");
            m = Material.STICK;
        }
        ItemStack item = Utils.createItemStack(
                m,
                Utils.chat(config.getString("enable_cursed_drops_item.item.name")),
                config.getStringList("enable_cursed_drops_item.item.lore"));
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
        List<String> shape = config.getStringList("enable_cursed_drops_item.recipe.shape");
        try {
            r.shape(shape.get(0), shape.get(1), shape.get(2));
        } catch (NullPointerException e){
            System.out.println("Invalid crafting shape for the enable curse drops item, cancelled crafting recipe");
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
            if (config.getString("enable_cursed_drops_item.recipe." + c) == null){
                System.out.println("Error: Enable curse drops. Material " + c + " in the shape grid was not defined after, cancelled crafting recipe");
                return;
            } else {
                try {
                    r.setIngredient(c, Material.valueOf(config.getString("enable_cursed_drops_item.recipe." + c)));
                } catch (IllegalArgumentException e){
                    System.out.println("Invalid crafting material for the enable curse drops item at " + c + ", cancelled crafting recipe");
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
            if (CooldownManager.getInstance().cooldownLowerThanZero(p, "enable_curse_drops_cooldown") || p.hasPermission("pdm.cancelitemcooldowns")){
                PlayerCurseManager.getInstance().addCurse(p.getUniqueId(), curseInfluence);
                if (p.getGameMode() != GameMode.CREATIVE){
                    p.getInventory().getItemInMainHand().setAmount(p.getInventory().getItemInMainHand().getAmount() - 1);
                }
                if (!p.hasPermission("pdm.cancelitemcooldowns")){
                    CooldownManager.getInstance().setCooldown(p, cooldown * 1000, "enable_curse_drops_cooldown");
                }
                CooldownManager.getInstance().setCooldown(p, duration * 1000, "enable_curse_drops_duration");
                if (PluginConfigurationManager.getInstance().useAnimationParticles()){
                    for (int i = 0; i < 10; i++){
                        p.spawnParticle(Particle.SMOKE_LARGE, p.getLocation(), 0, 0, 0.1 + (i*0.01), 0);
                    }
                }
            } else {
                p.sendMessage(Utils.chat(PluginConfigurationManager.getInstance().getPlayerCantUseItemError()
                        .replace("{duration}", String.format("%s", Utils.msToTimestamp(CooldownManager.getInstance().getCooldown(p, "enable_curse_drops_cooldown"))))));
            }
        } else {
            p.sendMessage(Utils.chat(PluginConfigurationManager.getInstance().getPlayerTooMuchCurseError()));
        }
    }

    public double getMeleeDebuff() {
        return meleeDebuff;
    }

    public double getRangedDebuff() {
        return rangedDebuff;
    }
}
