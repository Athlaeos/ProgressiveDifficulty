package me.athlaeos.progressivelydifficultmobs.pojo;

import org.bukkit.block.Biome;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class LeveledMonster {
    private int level;
    private int spawnWeight;
    private EntityType entityType;
    private boolean isBoss;
    private String name;
    private String displayName;
    private boolean displayNameVisible;
    private int expDropped;
    private List<String> lootTables;
    private List<String> abilities;
    private boolean dropsDefaultLootTable;
    private double baseHealth;
    private boolean enabled;
    private double karmaInfluence;
    private ItemStack helmet;
    private ItemStack chestPlate;
    private ItemStack leggings;
    private ItemStack boots;
    private ItemStack mainHand;
    private ItemStack offHand;
    private List<String> biomeFilter;
    private List<String> worldFilter;
    private List<String> regionFilter;
    private int minYRange;
    private int maxYRange;
    private double helmetDropChance;
    private double chestplateDropChance;
    private double leggingsDropChance;
    private double bootsDropChance;
    private double mainHandDropChance;
    private double offHandDropChance;
    private boolean spawnsWithoutLevel;

    public LeveledMonster(int level,
                          int weight,
                          String name,
                          EntityType entity,
                          boolean isBoss,
                          String displayName,
                          boolean displayNameVisible,
                          List<String> lootTables,
                          List<String> abilities,
                          boolean dropsDefaultLootTable,
                          double karmaInfluence,
                          int expDropped,
                          double baseHealth,
                          boolean enabled,
                          ItemStack helmet,
                          ItemStack chestPlate,
                          ItemStack leggings,
                          ItemStack boots,
                          ItemStack mainHand,
                          ItemStack offHand,
                          List<String> biomeFilter,
                          List<String> worldFilter,
                          List<String> regionFilter,
                          int minYRange,
                          int maxYRange,
                          double helmetDropChance,
                          double chestplateDropChance,
                          double leggingsDropChance,
                          double bootsDropChance,
                          double mainHandDropChance,
                          double offHandDropChance,
                          boolean spawnsWithoutLevel){
        this.level = level;
        this.spawnWeight = weight;
        this.name = name;
        this.entityType = entity;
        this.isBoss = isBoss;
        this.displayName = displayName;
        this.displayNameVisible = displayNameVisible;
        this.lootTables = lootTables;
        this.abilities = abilities;
        this.dropsDefaultLootTable = dropsDefaultLootTable;
        this.karmaInfluence = karmaInfluence;
        this.expDropped = expDropped;
        this.baseHealth = baseHealth;
        this.enabled = enabled;
        this.helmet = helmet;
        this.chestPlate = chestPlate;
        this.leggings = leggings;
        this.boots = boots;
        this.mainHand = mainHand;
        this.offHand = offHand;
        this.biomeFilter = biomeFilter;
        this.worldFilter = worldFilter;
        this.regionFilter = regionFilter;
        this.minYRange = minYRange;
        this.maxYRange = maxYRange;
        this.helmetDropChance = helmetDropChance;
        this.chestplateDropChance = chestplateDropChance;
        this.leggingsDropChance = leggingsDropChance;
        this.bootsDropChance = bootsDropChance;
        this.mainHandDropChance = mainHandDropChance;
        this.offHandDropChance = offHandDropChance;
        this.spawnsWithoutLevel = spawnsWithoutLevel;
    }

    public List<String> getWorldFilter() {
        return worldFilter;
    }

    public void setWorldFilter(List<String> worldFilter) {
        this.worldFilter = worldFilter;
    }

    public int getMaxYRange() {
        return maxYRange;
    }

    public void setMaxYRange(int maxYRange) {
        this.maxYRange = maxYRange;
    }

    public int getMinYRange() {
        return minYRange;
    }

    public void setMinYRange(int minYRange) {
        this.minYRange = minYRange;
    }

    public List<String> getBiomeFilter() {
        return biomeFilter;
    }

    public void setBiomeFilter(List<String> biomeFilter) {
        this.biomeFilter = biomeFilter;
    }

    public boolean isBoss() {
        return isBoss;
    }

    public void setBoss(boolean boss) {
        isBoss = boss;
    }

    public boolean isDropsDefaultLootTable() {
        return dropsDefaultLootTable;
    }

    public void setDropsDefaultLootTable(boolean dropsDefaultLootTable) {
        this.dropsDefaultLootTable = dropsDefaultLootTable;
    }

    public boolean isDisplayNameVisible() {
        return displayNameVisible;
    }

    public void setDisplayNameVisible(boolean displayNameVisible) {
        this.displayNameVisible = displayNameVisible;
    }

    public List<String> getAbilities() {
        return abilities;
    }

    public void setAbilities(List<String> abilities) {
        this.abilities = abilities;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public void setEquipment(ItemStack helmet, ItemStack chestPlate, ItemStack leggings
            , ItemStack boots, ItemStack mainHand, ItemStack offHand){
        this.helmet = helmet;
        this.chestPlate = chestPlate;
        this.leggings = leggings;
        this.boots = boots;
        this.mainHand = mainHand;
        this.offHand = offHand;
    }

    public String getName() {
        return name;
    }

    public ItemStack getHelmet() {
        return helmet;
    }

    public ItemStack getBoots() {
        return boots;
    }

    public ItemStack getChestPlate() {
        return chestPlate;
    }

    public ItemStack getLeggings() {
        return leggings;
    }

    public ItemStack getMainHand() {
        return mainHand;
    }

    public ItemStack getOffHand() {
        return offHand;
    }

    public void setChestPlate(ItemStack chestPlate) {
        this.chestPlate = chestPlate;
    }

    public void setMainHand(ItemStack mainHand) {
        this.mainHand = mainHand;
    }

    public void setOffHand(ItemStack offHand) {
        this.offHand = offHand;
    }

    public double getKarmaInfluence() {
        return karmaInfluence;
    }

    public void setKarmaInfluence(double karmaInfluence) {
        this.karmaInfluence = karmaInfluence;
    }

    public EntityType getEntityType() {
        return entityType;
    }

    public double getBaseHealth() {
        return baseHealth;
    }

    public int getExpDropped() {
        return expDropped;
    }

    public int getSpawnWeight() {
        return spawnWeight;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public List<String> getLootTables() {
        return lootTables;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setBaseHealth(double baseHealth) {
        this.baseHealth = baseHealth;
    }

    public void setBoots(ItemStack boots) {
        this.boots = boots;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public void setEntityType(EntityType entity) {
        this.entityType = entity;
    }

    public void setExpDropped(int expDropped) {
        this.expDropped = expDropped;
    }

    public void setHelmet(ItemStack helmet) {
        this.helmet = helmet;
    }

    public void setLootTables(List<String> lootTables) {
        this.lootTables = lootTables;
    }

    public void setOffhand(ItemStack offhand) {
        this.offHand = offhand;
    }

    public void setSpawnWeight(int spawnWeight) {
        this.spawnWeight = spawnWeight;
    }

    public void setLeggings(ItemStack leggings) {
        this.leggings = leggings;
    }

    public double getBootsDropChance() {
        return bootsDropChance;
    }

    public double getChestplateDropChance() {
        return chestplateDropChance;
    }

    public double getHelmetDropChance() {
        return helmetDropChance;
    }

    public double getLeggingsDropChance() {
        return leggingsDropChance;
    }

    public double getMainHandDropChance() {
        return mainHandDropChance;
    }

    public double getOffHandDropChance() {
        return offHandDropChance;
    }

    public void setBootsDropChance(double bootsDropChance) {
        this.bootsDropChance = bootsDropChance;
    }

    public void setChestplateDropChance(double chestplateDropChance) {
        this.chestplateDropChance = chestplateDropChance;
    }

    public void setHelmetDropChance(double helmetDropChance) {
        this.helmetDropChance = helmetDropChance;
    }

    public void setLeggingsDropChance(double leggingsDropChance) {
        this.leggingsDropChance = leggingsDropChance;
    }

    public void setMainHandDropChance(double mainHandDropChance) {
        this.mainHandDropChance = mainHandDropChance;
    }

    public void setOffHandDropChance(double offHandDropChance) {
        this.offHandDropChance = offHandDropChance;
    }

    public void removeLootTable(String i){
        lootTables.remove(i);
    }

    public List<String> getRegionFilter() {
        return regionFilter;
    }

    public void setRegionFilter(List<String> regionFilter) {
        this.regionFilter = regionFilter;
    }

    public boolean doesSpawnWithoutLevel() {
        return spawnsWithoutLevel;
    }

    public void setSpawnsWithoutLevel(boolean spawnsWithoutLevel) {
        this.spawnsWithoutLevel = spawnsWithoutLevel;
    }
}
