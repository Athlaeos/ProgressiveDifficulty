package me.athlaeos.progressivelydifficultmobs.filters;

import me.athlaeos.progressivelydifficultmobs.managers.JudgedPlayersManager;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.function.Predicate;

public class PlayerKarmaFilter<E> implements Predicate<Entity>{
	@Override
	public boolean test(Entity e) {
		if (e instanceof Player){
			Player p = (Player) e;
			return JudgedPlayersManager.getInstance().isPlayerJudged(p);
		}
		return false;
	}
	
}