package me.athlaeos.progressivelydifficultmobs.items;

import me.athlaeos.progressivelydifficultmobs.main.Main;
import me.athlaeos.progressivelydifficultmobs.managers.CooldownManager;
import me.athlaeos.progressivelydifficultmobs.managers.PlayerCurseManager;
import me.athlaeos.progressivelydifficultmobs.pojo.ActiveItem;
import me.athlaeos.progressivelydifficultmobs.utils.Utils;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.Arrays;

public class ExampleActiveItem extends ActiveItem {

    public ExampleActiveItem(){
        key = new NamespacedKey(Main.getInstance(), "pdm-custom");

        ItemStack item = Utils.createItemStack(
                    Material.DIAMOND,
                    Utils.chat("&c&lvery cool sword"),
                    Arrays.asList(
                            Utils.chat("&7kinda sharp too")
                    ));
        this.item = item;

        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.getPersistentDataContainer().set(key, PersistentDataType.INTEGER, 1);
        item.setItemMeta(itemMeta);

        createRecipe();

        CooldownManager.getInstance().registerCooldownKey(key.getKey());
    }

    private void createRecipe(){
        ShapedRecipe r = new ShapedRecipe(key, item);

        r.shape(" D ", "DDD", " D ");

        r.setIngredient('D', Material.DIAMOND);
        recipe = r;
        Main.getInstance().getServer().addRecipe(recipe);
    }

    @Override
    public void execute(Event e) {
        //This would be PlayerInteractEntityEvent if you wanted to register this item under apply-on-entity active items
        PlayerInteractEvent event = (PlayerInteractEvent) e;
        event.setCancelled(true);
        Player p = event.getPlayer();
        //Entity entity = event.getRightClicked(); //Would be used in case of PlayerInteractEntityEvent

        if (PlayerCurseManager.getInstance().getCurse(p.getUniqueId()) < curseLimit){
            if (CooldownManager.getInstance().cooldownLowerThanZero(p, key.getKey()) || p.hasPermission("pdm.cancelitemcooldowns")) {
                PlayerCurseManager.getInstance().addCurse(p.getUniqueId(), curseInfluence);
                if (p.getGameMode() != GameMode.CREATIVE) {
                    p.getInventory().getItemInMainHand().setAmount(p.getInventory().getItemInMainHand().getAmount() - 1);
                }
                if (!p.hasPermission("pdm.cancelitemcooldowns")) {
                    CooldownManager.getInstance().setCooldown(p, 5000, key.getKey());
                }

                //do stuff
            } else {
                p.sendMessage(Utils.chat(String.format("&cCooldown: %.2fs", (CooldownManager.getInstance().getCooldown(p, key.getKey()) / 1000D))));
            }
        } else {
            p.sendMessage(Utils.chat("&cNo can do pal you got too much curse"));
        }
    }
}
