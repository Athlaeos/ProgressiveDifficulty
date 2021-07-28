package me.athlaeos.progressivelydifficultmobs.filters;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.function.Predicate;

public class PlayerFilter<E> implements Predicate<Entity> {
    @Override
    public boolean test(Entity entity) {
        return entity instanceof Player;
    }
}
