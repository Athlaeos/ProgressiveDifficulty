package me.athlaeos.progressivelydifficultmobs.items;

import me.athlaeos.progressivelydifficultmobs.main.Main;
import me.athlaeos.progressivelydifficultmobs.managers.*;
import me.athlaeos.progressivelydifficultmobs.pojo.ActiveItem;
import me.athlaeos.progressivelydifficultmobs.pojo.LeveledMonster;
import me.athlaeos.progressivelydifficultmobs.utils.Utils;
import org.bukkit.*;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class LargeAOEDamageItem extends ActiveItem {
    private final NamespacedKey monsterKey = new NamespacedKey(Main.getInstance(), "pdm-monsterID");
    private final double damage;
    private final int cooldown;
    private final int radius;
    YamlConfiguration config;

    public LargeAOEDamageItem(){
        key = new NamespacedKey(Main.getInstance(), "pdm-large_aoe_damage");
        config = ConfigManager.getInstance().getConfig("items.yml").get();
        curseInfluence = config.getInt("mob_area_damage_item.curse_influence");
        curseLimit = config.getInt("mob_area_damage_item.curse_limit");
        useRecipe = config.getBoolean("mob_area_damage_item.use_recipe");
        damage = config.getInt("mob_area_damage_item.damage");
        radius = config.getInt("mob_area_damage_item.radius");
        cooldown = config.getInt("mob_area_damage_item.cooldown");

        Material m;
        try {
            m = Material.valueOf(config.getString("mob_area_damage_item.item.material"));
        } catch (IllegalArgumentException e) {
            System.out.println("The mob area damage item's material could not be found, defaulting to STICK");
            m = Material.STICK;
        }
        ItemStack item = Utils.createItemStack(
                m,
                Utils.chat(config.getString("mob_area_damage_item.item.name")),
                config.getStringList("mob_area_damage_item.item.lore"));
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
        List<String> shape = config.getStringList("mob_area_damage_item.recipe.shape");
        try {
            r.shape(shape.get(0), shape.get(1), shape.get(2));
        } catch (NullPointerException e){
            System.out.println("Invalid crafting shape for the mob area damage item, cancelled crafting recipe");
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
            if (config.getString("mob_area_damage_item.recipe." + c) == null){
                System.out.println("Error: Mob area damage item. Material " + c + " in the shape grid was not defined after, cancelled crafting recipe");
                return;
            } else {
                try {
                    r.setIngredient(c, Material.valueOf(config.getString("mob_area_damage_item.recipe." + c)));
                } catch (IllegalArgumentException e){
                    System.out.println("Invalid crafting material for the mob area damage item at " + c + ", cancelled crafting recipe");
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
            if (CooldownManager.getInstance().cooldownLowerThanZero(p, "aoe_damage_cooldown") || p.hasPermission("pdm.cancelitemcooldowns")){
                PlayerCurseManager.getInstance().addCurse(p.getUniqueId(), curseInfluence);
                if (p.getGameMode() != GameMode.CREATIVE){
                    p.getInventory().getItemInMainHand().setAmount(p.getInventory().getItemInMainHand().getAmount() - 1);
                }
                if (!p.hasPermission("pdm.cancelitemcooldowns")){
                    CooldownManager.getInstance().setCooldown(p, cooldown * 1000, "aoe_damage_cooldown");
                }
                final Location triggerLocation = p.getLocation();
                final boolean useAnimations = PluginConfigurationManager.getInstance().useAnimationParticles();
                if (PluginConfigurationManager.getInstance().useAnimationRunnables()){
                    new BukkitRunnable(){
                        int timer = 0;
                        @Override
                        public void run() {
                            if (useAnimations){
                                if (timer < 30){
                                    for (Location l : Utils.getCircle(triggerLocation, radius, 32)){
                                        triggerLocation.getWorld().spawnParticle(Particle.FLAME, l, 0, 0, 0, 0);
                                    }
                                    for (Location l : Utils.getRandomPointsInCircle(triggerLocation, radius, 4)){
                                        triggerLocation.getWorld().spawnParticle(Particle.SMOKE_LARGE, l,
                                                0,
                                                0,
                                                0.2,
                                                0);
                                    }
                                    if (timer < 15){
                                        for (Location l : Utils.getRandomPointsInCircle(triggerLocation, radius, 16)){
                                            triggerLocation.getWorld().spawnParticle(Particle.FLAME, l,
                                                    0,
                                                    (triggerLocation.getX() - l.getX()) * 0.05,
                                                    0,
                                                    (triggerLocation.getZ() - l.getZ()) * 0.05);
                                        }
                                    }
                                }
                            }
                            if (timer >= 30){
                                if (timer == 31){
                                    triggerLocation.getWorld().playSound(p.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 0.3F, 1.0F);
                                    for (Entity e : triggerLocation.getWorld().getNearbyEntities(triggerLocation,
                                            radius, radius, radius)){
                                        if (CompatibleEntityManager.getInstance().getHostileMobIcons().containsKey(e.getType())){
                                            LivingEntity monster = (LivingEntity) e;
                                            String mobId;
                                            if(monster.getPersistentDataContainer().has(monsterKey, PersistentDataType.STRING)) {
                                                mobId = monster.getPersistentDataContainer().get(monsterKey, PersistentDataType.STRING);
                                                LeveledMonster m = LeveledMonsterManager.getInstance().getMonster(mobId);
                                                if (m != null){
                                                    if (!m.isBoss()){
                                                        monster.damage(damage, p);
                                                    }
                                                }
                                            } else {
                                                monster.damage(damage, p);
                                            }
                                        }
                                    }
                                }
                                if (useAnimations){
                                    for (Location l : Utils.getCircle(triggerLocation, 0.5, 128)){
                                        triggerLocation.getWorld().spawnParticle(Particle.FLAME, l,
                                                0,
                                                (l.getX() - triggerLocation.getX()) * 2,
                                                0,
                                                (l.getZ() - triggerLocation.getZ()) * 2);
                                    }
                                }
                            }
                            if (timer >= 33){
                                cancel();
                            }
                            timer++;
                        }
                    }.runTaskTimer(Main.getInstance(), 0, 2);
                } else {
                    for (Entity en : triggerLocation.getWorld().getNearbyEntities(triggerLocation,
                            radius, radius, radius)){
                        if (CompatibleEntityManager.getInstance().getHostileMobIcons().containsKey(en.getType())){
                            LivingEntity monster = (LivingEntity) en;
                            String mobId;
                            if(monster.getPersistentDataContainer().has(monsterKey, PersistentDataType.STRING)) {
                                mobId = monster.getPersistentDataContainer().get(monsterKey, PersistentDataType.STRING);
                                LeveledMonster m = LeveledMonsterManager.getInstance().getMonster(mobId);
                                if (m != null){
                                    if (!m.isBoss()){
                                        monster.damage(damage, p);
                                    }
                                }
                            } else {
                                monster.damage(damage, p);
                            }
                        }
                    }
                }
            } else {
                p.sendMessage(Utils.chat(PluginConfigurationManager.getInstance().getPlayerCantUseItemError()
                        .replace("{duration}", String.format("%s", Utils.msToTimestamp(CooldownManager.getInstance().getCooldown(p, "aoe_damage_cooldown"))))));
            }
        } else {
            p.sendMessage(Utils.chat(PluginConfigurationManager.getInstance().getPlayerTooMuchCurseError()));
        }
    }
}
