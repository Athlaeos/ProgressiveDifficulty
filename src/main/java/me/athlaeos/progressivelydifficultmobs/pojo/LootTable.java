package me.athlaeos.progressivelydifficultmobs.pojo;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class LootTable {
    private List<Drop> drops;
    private String name;
    private Material icon = null;

    public LootTable(String name, List<Drop> drops){
        this.name = name;
        this.drops = drops;
    }

    public LootTable(String name, List<Drop> drops, Material icon){
        this.name = name;
        this.drops = drops;
        this.icon = icon;
    }

    public Material getIcon() {
        return icon;
    }

    public void setIcon(Material icon) {
        this.icon = icon;
    }

    public List<Drop> getDrops() {
        return drops;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDrops(List<Drop> drops) {
        this.drops = drops;
    }

    public void addDrop(Drop d){
        if (drops.contains(d)){
            return;
        }
        drops.add(d);
    }

    public void updateDrop(Drop d, ItemStack item, double dropChance, double dropChanceLooting, int minDrop, int maxDrop, int minDropLooting, int maxDropLooting){
        d.setItem(item);
        d.setDropChance(dropChance);
        d.setDropChanceLootingBonus(dropChanceLooting);
        d.setMinAmountDrop(minDrop);
        d.setMaxAmountDrop(maxDrop);
        d.setMinAmountDropLootingBonus(minDropLooting);
        d.setMaxAmountDropLootingBonus(maxDropLooting);
    }

    public void removeDrop(Drop d){
        drops.remove(d);
    }
}
