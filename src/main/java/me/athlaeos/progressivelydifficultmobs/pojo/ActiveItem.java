package me.athlaeos.progressivelydifficultmobs.pojo;

import org.bukkit.NamespacedKey;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;

public abstract class ActiveItem {

    protected ItemStack item;
    protected NamespacedKey key;
    protected int curseInfluence;
    protected int curseLimit;
    protected boolean useRecipe;
    protected Recipe recipe;

    public abstract void execute(Event e);

    public ItemStack getItem() {
        return item;
    }

    public NamespacedKey getKey() {
        return key;
    }

    public void setKey(NamespacedKey key) {
        this.key = key;
    }

    public int getCurseInfluence() {
        return curseInfluence;
    }

    public int getCurseLimit() {
        return curseLimit;
    }

    public void setItem(ItemStack item) {
        this.item = item;
    }

    public void setCurseInfluence(int curseInfluence) {
        this.curseInfluence = curseInfluence;
    }

    public void setCurseLimit(int curseLimit) {
        this.curseLimit = curseLimit;
    }

    public boolean isUseRecipe() {
        return useRecipe;
    }

    public void setUseRecipe(boolean useRecipe) {
        this.useRecipe = useRecipe;
    }

    public Recipe getRecipe() {
        return recipe;
    }

    public void setRecipe(Recipe recipe) {
        this.recipe = recipe;
    }
}
