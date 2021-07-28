package me.athlaeos.progressivelydifficultmobs.managers;

import me.athlaeos.progressivelydifficultmobs.main.Main;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class CompatibleEntityManager {

    private static CompatibleEntityManager manager = null;

    private final Map<EntityType, Material> passiveMobIcons = new HashMap<>();
    private final Map<EntityType, Material> hostileMobIcons = new HashMap<>();

    public CompatibleEntityManager(){

        registerHostileMobIcon("BLAZE", "BLAZE_ROD");
        registerHostileMobIcon("CAVE_SPIDER", "SPIDER_EYE");
        registerHostileMobIcon("CREEPER", "CREEPER_HEAD");
        registerHostileMobIcon("DROWNED", "TRIDENT");
        registerHostileMobIcon("ENDERMAN", "ENDER_PEARL");
        registerHostileMobIcon("ENDERMITE", "CHORUS_FRUIT");
        registerHostileMobIcon("EVOKER", "TOTEM_OF_UNDYING");
        registerHostileMobIcon("GHAST", "GHAST_TEAR");
        registerHostileMobIcon("GIANT", "BARRIER");
        registerHostileMobIcon("GUARDIAN", "PRISMARINE_SHARD");
        registerHostileMobIcon("HUSK", "SAND");
        registerHostileMobIcon("ILLUSIONER", "BARRIER");
        registerHostileMobIcon("MAGMA_CUBE", "MAGMA_CREAM");
        registerHostileMobIcon("PHANTOM", "PHANTOM_MEMBRANE");
        registerHostileMobIcon("PIGLIN", "GOLDEN_AXE");
        registerHostileMobIcon("PIGLIN_BRUTE", "GOLDEN_CHESTPLATE");
        registerHostileMobIcon("ZOMBIFIED_PIGLIN", "GOLD_INGOT");
        registerHostileMobIcon("HOGLIN", "WARPED_FUNGUS");
        registerHostileMobIcon("ZOGLIN", "CRIMSON_FUNGUS");
        registerHostileMobIcon("PIG_ZOMBIE", "GOLD_INGOT");
        registerHostileMobIcon("PILLAGER", "CROSSBOW");
        registerHostileMobIcon("RAVAGER", "RAVAGER_SPAWN_EGG");
        registerHostileMobIcon("SHULKER", "SHULKER_SHELL");
        registerHostileMobIcon("SILVERFISH", "STONE_BRICKS");
        registerHostileMobIcon("SKELETON", "SKELETON_SKULL");
        registerHostileMobIcon("SLIME", "SLIME_BALL");
        registerHostileMobIcon("SPIDER", "STRING");
        registerHostileMobIcon("STRAY", "TIPPED_ARROW");
        registerHostileMobIcon("VEX", "GOLDEN_SWORD");
        registerHostileMobIcon("VINDICATOR", "IRON_AXE");
        registerHostileMobIcon("WITCH", "POTION");
        registerHostileMobIcon("WITHER_SKELETON", "WITHER_SKELETON_SKULL");
        registerHostileMobIcon("ZOMBIE", "ZOMBIE_HEAD");
        registerHostileMobIcon("ZOMBIE_VILLAGER", "GOLDEN_APPLE");
        registerHostileMobIcon("ENDER_DRAGON", "DRAGON_BREATH");
        registerHostileMobIcon("ELDER_GUARDIAN", "PRISMARINE_CRYSTALS");
        registerHostileMobIcon("WITHER", "NETHER_STAR");

        registerPassiveMobIcon("BAT", "BLACK_DYE");
        registerPassiveMobIcon("BEE", "HONEYCOMB");
        registerPassiveMobIcon("CHICKEN", "FEATHER");
        registerPassiveMobIcon("COD", "COD");
        registerPassiveMobIcon("COW", "LEATHER");
        registerPassiveMobIcon("DONKEY", "CHEST");
        registerPassiveMobIcon("DOLPHIN", "HEART_OF_THE_SEA");
        registerPassiveMobIcon("FOX", "SWEET_BERRY_BUSH");
        registerPassiveMobIcon("HORSE", "DIAMOND_HORSE_ARMOR");
        registerPassiveMobIcon("IRON_GOLEM", "IRON_INGOT");
        registerPassiveMobIcon("LLAMA", "RED_CARPET");
        registerPassiveMobIcon("MULE", "LEATHER_HORSE_ARMOR");
        registerPassiveMobIcon("MUSHROOM_COW", "RED_MUSHROOM");
        registerPassiveMobIcon("OCELOT", "OCELOT_SPAWN_EGG");
        registerPassiveMobIcon("PANDA", "BAMBOO");
        registerPassiveMobIcon("PARROT", "PARROT_SPAWN_EGG");
        registerPassiveMobIcon("PIG", "PORKCHOP");
        registerPassiveMobIcon("POLAR_BEAR", "SNOW");
        registerPassiveMobIcon("PUFFERFISH", "PUFFERFISH");
        registerPassiveMobIcon("RABBIT", "RABBIT");
        registerPassiveMobIcon("SALMON", "SALMON");
        registerPassiveMobIcon("SHEEP", "WHITE_WOOL");
        registerPassiveMobIcon("SKELETON_HORSE", "SKELETON_HORSE_SPAWN_EGG");
        registerPassiveMobIcon("SNOWMAN", "PUMPKIN");
        registerPassiveMobIcon("SQUID", "INK_SAC");
        registerPassiveMobIcon("TRADER_LLAMA", "PURPLE_CARPET");
        registerPassiveMobIcon("TROPICAL_FISH", "TROPICAL_FISH");
        registerPassiveMobIcon("TURTLE", "SCUTE");
        registerPassiveMobIcon("VILLAGER", "EMERALD");
        registerPassiveMobIcon("WANDERING_TRADER", "EMERALD_BLOCK");
        registerPassiveMobIcon("WOLF", "BONE");
        registerPassiveMobIcon("ZOMBIE_HORSE", "ROTTEN_FLESH");
    }

    public static CompatibleEntityManager getInstance(){
        if (manager == null){
            manager = new CompatibleEntityManager();
        }
        return manager;
    }

    /**
     * @return a TreeMap of all the passive mobs the plugin can edit, along with what icons should be used in the GUI menus
     */
    public Map<EntityType, Material> getPassiveMobIcons(){
        return new TreeMap<>(passiveMobIcons);
    }

    /**
     * @return a TreeMap of all the hostile mobs the plugin can edit, along with what icons should be used in the GUI menus
     */
    public Map<EntityType, Material> getHostileMobIcons() {
        return new TreeMap<>(hostileMobIcons);
    }

    /**
     * @return a TreeMap of all mobs the plugin can edit, along with what icons should be used in the GUI menus
     */
    public Map<EntityType, Material> getAllMobIcons(){
        Map<EntityType, Material> map = new HashMap<>();
        map.putAll(passiveMobIcons);
        map.putAll(hostileMobIcons);
        return new TreeMap<>(map);
    }

    /**
     * Registers a new mob in the hostile mob category.
     * The EntityType and Material ENUMS are done with strings, so that in case they don't exist
     * in this version of minecraft it won't cause any problems.
     * @param entityType
     * @param itemMaterial
     */
    public void registerHostileMobIcon(String entityType, String itemMaterial){
        EntityType type;
        try{
            type = EntityType.valueOf(entityType);
        } catch (IllegalArgumentException ignored) {
            return;
        }
        Material material;
        try {
            material = Material.valueOf(itemMaterial);
        } catch (IllegalArgumentException ignored) {
            material = Material.valueOf("DIAMOND");
        }
        hostileMobIcons.put(type, material);
    }

    /**
     * Registers a new mob in the passive mob category.
     * The EntityType and Material ENUMS are done with strings, so that in case they don't exist
     * in this version of minecraft it won't cause any problems.
     * @param entityType
     * @param itemMaterial
     */
    public void registerPassiveMobIcon(String entityType, String itemMaterial){
        EntityType type;
        try{
            type = EntityType.valueOf(entityType);
        } catch (IllegalArgumentException ignored) {
            return;
        }
        Material material;
        try {
            material = Material.valueOf(itemMaterial);
        } catch (IllegalArgumentException ignored) {
            material = Material.valueOf("DIAMOND");
        }
        passiveMobIcons.put(type, material);
    }
}
