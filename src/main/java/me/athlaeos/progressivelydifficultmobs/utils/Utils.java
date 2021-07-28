package me.athlaeos.progressivelydifficultmobs.utils;

import me.athlaeos.progressivelydifficultmobs.filters.PlayerFilter;
import me.athlaeos.progressivelydifficultmobs.main.Main;
import me.athlaeos.progressivelydifficultmobs.managers.MinecraftVersion;
import me.athlaeos.progressivelydifficultmobs.managers.MinecraftVersionManager;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarFlag;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.*;
import java.util.*;
import java.util.function.Predicate;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {

	private static Random random = null;

	public static Random getRandom(){
		if (random == null){
			random = new Random();
		}
		return random;
	}

	public static void applyBadOmen(Player p){
		if (p.hasPotionEffect(PotionEffectType.BAD_OMEN)){
			int currentAmplifier = p.getPotionEffect(PotionEffectType.BAD_OMEN).getAmplifier();
			if (currentAmplifier < 4){
				p.addPotionEffect(new PotionEffect(PotionEffectType.BAD_OMEN, 120000, currentAmplifier + 1, false, false));
			} else {
				p.addPotionEffect(new PotionEffect(PotionEffectType.BAD_OMEN, 120000, 4, false, false));
			}
		} else {
			p.addPotionEffect(new PotionEffect(PotionEffectType.BAD_OMEN, 120000, 0, false, false));
		}
	}

	public static String chat (String s) {
		if (MinecraftVersionManager.getInstance().currentVersionNewerThan(MinecraftVersion.MINECRAFT_1_16)){
			return newChat(s);
		} else {
			return oldChat(s);
		}
	}

	public static String newChat(String message) {
		char COLOR_CHAR = net.md_5.bungee.api.ChatColor.COLOR_CHAR;
		final Pattern hexPattern = Pattern.compile("&#([A-Fa-f0-9]{6})");
		Matcher matcher = hexPattern.matcher(message);
		StringBuffer buffer = new StringBuffer(message.length() + 4 * 8);
		while (matcher.find())
		{
			String group = matcher.group(1);
			matcher.appendReplacement(buffer, COLOR_CHAR + "x"
					+ COLOR_CHAR + group.charAt(0) + COLOR_CHAR + group.charAt(1)
					+ COLOR_CHAR + group.charAt(2) + COLOR_CHAR + group.charAt(3)
					+ COLOR_CHAR + group.charAt(4) + COLOR_CHAR + group.charAt(5)
			);
		}
		return Utils.oldChat(matcher.appendTail(buffer).toString());
	}

	public static String oldChat(String message) {
		return net.md_5.bungee.api.ChatColor.translateAlternateColorCodes('&', message + "");
	}

	public static Map<Integer, ArrayList<String>> paginateTextList(int pageSize, List<String> allEntries) {
		Map<Integer, ArrayList<String>> pages = new HashMap<>();
		int stepper = 0;

		for (int pageNumber = 0; pageNumber < Math.ceil((double)allEntries.size()/(double)pageSize); pageNumber++) {
			ArrayList<String> pageEntries = new ArrayList<>();
			for (int pageEntry = 0; pageEntry < pageSize && stepper < allEntries.size(); pageEntry++, stepper++) {
				pageEntries.add(allEntries.get(stepper));
			}
			pages.put(pageNumber, pageEntries);
		}
		return pages;
	}

	public static Map<Integer, ArrayList<ItemStack>> paginateItemStackList(int pageSize, List<ItemStack> allEntries) {
		Map<Integer, ArrayList<ItemStack>> pages = new HashMap<>();
		int stepper = 0;

		for (int pageNumber = 0; pageNumber < Math.ceil((double)allEntries.size()/(double)pageSize); pageNumber++) {
			ArrayList<ItemStack> pageEntries = new ArrayList<>();
			for (int pageEntry = 0; pageEntry < pageSize && stepper < allEntries.size(); pageEntry++, stepper++) {
				pageEntries.add(allEntries.get(stepper));
			}
			pages.put(pageNumber, pageEntries);
		}
		return pages;
	}

	public static String msToTimestamp(Long ms){
		Long timeLeft = ms;
		int hours = (int)(ms/(1000*60*60));
		timeLeft %= 1000*60*60;
		int minutes = (int)(timeLeft/(1000*60));
		timeLeft %= 1000*60;
		int seconds = (int)(timeLeft/1000);
		timeLeft %= 1000;
		int millis = (int)(timeLeft/100);

		String returnString;
		if (hours > 0){
			returnString = hours + "h";
		} else if (minutes > 0) {
			returnString = minutes + "m";
		} else {
			returnString = seconds + "." + millis + "s";
		}

		//String.format("%s%s%s.%s",
		//            ((hours == 0) ? "" : hours + ":"),
		//            ((minutes == 0) ? minutes + "" : ":"),
		//            seconds,
		//            millis);
		return returnString;
	}

	public static void changeDisplayName(ItemStack item, String name) {
		ItemMeta itemmeta = item.getItemMeta();
		itemmeta.setDisplayName(Utils.chat(name));
		item.setItemMeta(itemmeta);
	}
	public static void setLoreLines(ItemStack item, List<String> loreLines) {
		if (item == null) return;
		if (item.getType() == Material.AIR) return;
		if (loreLines != null){
			ItemMeta itemMeta = item.getItemMeta();
			itemMeta.setLore(loreLines);
			item.setItemMeta(itemMeta);
		}
	}

	public static ItemStack createItemStack(Material material, String displayname, List<String> lore){
		ItemStack item = new ItemStack(material);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(displayname);
		if (lore != null){
			List<String> coloredLore = new ArrayList<>();
			for (String l : lore){
				coloredLore.add(Utils.chat(l));
			}
			meta.setLore(coloredLore);
		}
		item.setItemMeta(meta);
		return item;
	}

	public static List<String> getLoreLines(ItemStack item){
		if (item == null) return new ArrayList<>();
		if (item.getType() == Material.AIR) return new ArrayList<>();
		ItemMeta meta = item.getItemMeta();
		if (meta.hasLore()){
			return meta.getLore();
		} else {
			return new ArrayList<>();
		}
	}

	public static List<Player> getPlayersInArea(Location l, int radius){
		Predicate<Entity> filter = new PlayerFilter<Player>();
		Collection<Entity> nearbyPlayers = l.getWorld().getNearbyEntities(l, radius, radius, radius, filter);
		List<Player> playerList = new ArrayList<>();
		for (Entity player : nearbyPlayers){
			if (player instanceof Player) {
				Player p = (Player) player;
				playerList.add(p);
			}
		}

		return playerList;
	}
	
	public static void save(File file, Object object) {
		try {
			if (!file.exists()) file.createNewFile();
			
			ObjectOutputStream output = new ObjectOutputStream(new FileOutputStream(file));
			output.writeObject(object);
			output.flush();
			output.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static Object load(File file){
		try {
			if (!file.exists()) {
				try {
					file.createNewFile();
					return null;
				} catch (Exception e) {
					e.printStackTrace();
					System.out.println("Could not create file for file " + file.getName());
					return null;
				}
			}
			
 			ObjectInputStream input = new ObjectInputStream(new FileInputStream(file));
			Object readObject = input.readObject();
			input.close();
			return readObject;
		} catch(Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static ArrayList<Location> getCircle(Location center, double radius, int amount)
	{
		World world = center.getWorld();
		ArrayList<Location> locations = new ArrayList<>();
		for(int i = 1;i < amount + 1; i++)
		{
			double theta = Utils.getRandom().nextDouble() * 2 * Math.PI;
			double x = (center.getX() + (radius * Math.cos(theta)));
			double z = (center.getZ() + (radius * Math.sin(theta)));
			locations.add(new Location(world, x, center.getY(), z));
		}
		return locations;
	}

	public static ArrayList<Location> getRandomPointsInCircle(Location center, double radius, int amount)
	{
		World world = center.getWorld();
		ArrayList<Location> locations = new ArrayList<>();
		for(int i = 1;i < amount + 1; i++)
		{
			double r = radius * Math.sqrt(Utils.getRandom().nextDouble());
			double theta = Utils.getRandom().nextDouble() * 2 * Math.PI;
			double x = (center.getX() + (r * Math.cos(theta)));
			double z = (center.getZ() + (r * Math.sin(theta)));
			locations.add(new Location(world, x, center.getY(), z));
		}
		return locations;
	}

	public static void createBossBar(Main plugin, LivingEntity livingEntity, String title, BarColor color, BarStyle style, int radius, BarFlag... flags) {
		BossBar bossBar = plugin.getServer().createBossBar(title, color, style, flags);
		new BukkitRunnable() {
			@Override
			public void run() {
				Collection<Entity> nearbyEntityPlayers = livingEntity.getWorld().getNearbyEntities(livingEntity.getLocation(), radius, radius, radius, new PlayerFilter<Player>());
				List<Player> nearbyPlayers = new ArrayList<>();
				for (Entity p : nearbyEntityPlayers){
					if (p instanceof Player){
						nearbyPlayers.add((Player) p);
					}
				}
				for (Player p : Main.getInstance().getServer().getOnlinePlayers()){
					if (nearbyPlayers.contains(p)){
						bossBar.addPlayer(p);
					} else {
						bossBar.removePlayer(p);
					}
				}
				if (!livingEntity.isDead()) {
					bossBar.setProgress(livingEntity.getHealth() / livingEntity.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue());
				} else {
					List<Player> players = bossBar.getPlayers();
					for (Player player : players) {
						bossBar.removePlayer(player);
					}
					bossBar.setVisible(false);
					cancel();
				}
			}
		}.runTaskTimer(plugin, 0, 5);
	}

	public static Player getNearestPlayer(Location l){
		double lowestDistance;
		Player nearestPlayer = null;
		if (l.getWorld().getPlayers().size() != 0){
			nearestPlayer = l.getWorld().getPlayers().get(0);
			lowestDistance = l.distance(nearestPlayer.getLocation());
		} else {
			return null;
		}
		for (Player p : l.getWorld().getPlayers()){
			if (lowestDistance > l.distance(p.getLocation())){
				nearestPlayer = p;
				lowestDistance = l.distance(p.getLocation());
			}
		}
		return nearestPlayer;
	}

	public static int[] toIntArray(List<Integer> list){
		int[] ret = new int[list.size()];
		for(int i = 0;i < ret.length;i++)
			ret[i] = list.get(i);
		return ret;
	}

	public static List<Integer> toIntList(int[] array){
		List<Integer> list = new ArrayList<>();
		for (Integer i : array){
			list.add(i);
		}
		return list;
	}

	public static List<String> seperateStringIntoLines(String string, int maxLength, String linePrefix){
		List<String> lines = new ArrayList<>();
		String[] words = string.split(" ");
		if (words.length == 0) return lines;
		StringBuilder word = new StringBuilder();
		for (String s : words){
			if (word.length() + s.length() > maxLength){
				lines.add(word.toString());
				word = new StringBuilder();
				word.append(Utils.chat(linePrefix)).append(s);
			} else if (words[0].equals(s)){
				word.append(s);
			} else {
				word.append(" ").append(s);
			}
			if (words[words.length - 1].equals(s)){
				lines.add(word.toString());
			}
		}
		return lines;
	}

	public static double eval(final String str) {
		return new Object() {
			int pos = -1, ch;

			void nextChar() {
				ch = (++pos < str.length()) ? str.charAt(pos) : -1;
			}

			boolean eat(int charToEat) {
				while (ch == ' ') nextChar();
				if (ch == charToEat) {
					nextChar();
					return true;
				}
				return false;
			}

			double parse() {
				nextChar();
				double x = parseExpression();
				if (pos < str.length()) throw new RuntimeException("Unexpected symbol in parse: " + (char)ch);
				return x;
			}

			// Grammar:
			// expression = term | expression `+` term | expression `-` term
			// term = factor | term `*` factor | term `/` factor
			// factor = `+` factor | `-` factor | `(` expression `)`
			//        | number | functionName factor | factor `^` factor

			double parseExpression() {
				double x = parseTerm();
				for (;;) {
					if      (eat('+')) x += parseTerm(); // addition
					else if (eat('-')) x -= parseTerm(); // subtraction
					else return x;
				}
			}

			double parseTerm() {
				double x = parseFactor();
				for (;;) {
					if      (eat('*')) x *= parseFactor(); // multiplication
					else if (eat('/')) x /= parseFactor(); // division
					else return x;
				}
			}

			double parseFactor() {
				if (eat('+')) return parseFactor(); // unary plus
				if (eat('-')) return -parseFactor(); // unary minus

				double x;
				int startPos = this.pos;
				if (eat('(')) { // parentheses
					x = parseExpression();
					eat(')');
				} else if ((ch >= '0' && ch <= '9') || ch == '.') { // numbers
					while ((ch >= '0' && ch <= '9') || ch == '.') nextChar();
					x = Double.parseDouble(str.substring(startPos, this.pos));
				} else if (ch >= 'a' && ch <= 'z') { // functions
					while (ch >= 'a' && ch <= 'z') nextChar();
					String func = str.substring(startPos, this.pos);
					x = parseFactor();
					if (func.equals("sqrt")) x = Math.sqrt(x);
					else if (func.equals("sin")) x = Math.sin(Math.toRadians(x));
					else if (func.equals("cos")) x = Math.cos(Math.toRadians(x));
					else if (func.equals("tan")) x = Math.tan(Math.toRadians(x));
					else throw new RuntimeException("Unknown function: " + func);
				} else {
					throw new RuntimeException("Unexpected: " + (char)ch);
				}

				if (eat('^')) x = Math.pow(x, parseFactor()); // exponentiation

				return x;
			}
		}.parse();
	}

	public static List<Location> getCubeWithLines(Location center, int lineDensity, double radius){
		List<Location> square = new ArrayList<>();

		Location p1 = new Location(center.getWorld(), center.getX()-radius, center.getY()-radius, center.getZ()-radius);
		Location p2 = new Location(center.getWorld(), center.getX()-radius, center.getY()-radius, center.getZ()+radius);
		Location p3 = new Location(center.getWorld(), center.getX()-radius, center.getY()+radius, center.getZ()-radius);
		Location p4 = new Location(center.getWorld(), center.getX()-radius, center.getY()+radius, center.getZ()+radius);
		Location p5 = new Location(center.getWorld(), center.getX()+radius, center.getY()-radius, center.getZ()-radius);
		Location p6 = new Location(center.getWorld(), center.getX()+radius, center.getY()-radius, center.getZ()+radius);
		Location p7 = new Location(center.getWorld(), center.getX()+radius, center.getY()+radius, center.getZ()-radius);
		Location p8 = new Location(center.getWorld(), center.getX()+radius, center.getY()+radius, center.getZ()+radius);

		square.addAll(Utils.getPointsInLine(p1, p2, lineDensity));
		square.addAll(Utils.getPointsInLine(p1, p3, lineDensity));
		square.addAll(Utils.getPointsInLine(p2, p4, lineDensity));
		square.addAll(Utils.getPointsInLine(p3, p4, lineDensity));
		square.addAll(Utils.getPointsInLine(p5, p6, lineDensity));
		square.addAll(Utils.getPointsInLine(p5, p7, lineDensity));
		square.addAll(Utils.getPointsInLine(p6, p8, lineDensity));
		square.addAll(Utils.getPointsInLine(p7, p8, lineDensity));
		square.addAll(Utils.getPointsInLine(p1, p5, lineDensity));
		square.addAll(Utils.getPointsInLine(p2, p6, lineDensity));
		square.addAll(Utils.getPointsInLine(p3, p7, lineDensity));
		square.addAll(Utils.getPointsInLine(p4, p8, lineDensity));

		return square;
	}

	public static List<Location> getSquareWithLines(Location center, int lineDensity, double radius){
		List<Location> square = new ArrayList<>();

		Location p1 = new Location(center.getWorld(), center.getX()-radius, center.getY(), center.getZ()-radius);
		Location p2 = new Location(center.getWorld(), center.getX()-radius, center.getY(), center.getZ()+radius);
		Location p3 = new Location(center.getWorld(), center.getX()+radius, center.getY(), center.getZ()-radius);
		Location p4 = new Location(center.getWorld(), center.getX()+radius, center.getY(), center.getZ()+radius);

		square.addAll(Utils.getPointsInLine(p1, p2, lineDensity));
		square.addAll(Utils.getPointsInLine(p1, p3, lineDensity));
		square.addAll(Utils.getPointsInLine(p2, p4, lineDensity));
		square.addAll(Utils.getPointsInLine(p3, p4, lineDensity));

		return square;
	}

	public static List<Location> getPointsInLine(Location point1, Location point2, int amount){
		double xStep = (point1.getX() - point2.getX()) / amount;
		double yStep = (point1.getY() - point2.getY()) / amount;
		double zStep = (point1.getZ() - point2.getZ()) / amount;
		List<Location> points = new ArrayList<>();
		for (int i = 0; i < amount + 1; i++){
			points.add(new Location(
					point1.getWorld(),
					point1.getX() - xStep * i,
					point1.getY() - yStep * i,
					point1.getZ() - zStep * i));
		}
		return points;
	}

	public static List<Location> transformPoints(Location center, List<Location> points, double yaw, double pitch, double roll, double scale) {
		// Convert to radians
		yaw = Math.toRadians(yaw);
		pitch = Math.toRadians(pitch);
		roll = Math.toRadians(roll);
		List<Location> list = new ArrayList<>();

		// Store the values so we don't have to calculate them again for every single point.
		double cp = Math.cos(pitch);
		double sp = Math.sin(pitch);
		double cy = Math.cos(yaw);
		double sy = Math.sin(yaw);
		double cr = Math.cos(roll);
		double sr = Math.sin(roll);
		double x, bx, y, by, z, bz;
		for (Location point : points) {
			x = point.getX() - center.getX();
			bx = x;
			y = point.getY() - center.getY();
			by = y;
			z = point.getZ() - center.getZ();
			bz = z;
			x = ((x*cy-bz*sy)*cr+by*sr)*scale;
			y = ((y*cp+bz*sp)*cr-bx*sr)*scale;
			z = ((z*cp-by*sp)*cy+bx*sy)*scale;
			list.add(new Location(point.getWorld(), (center.getX()+x), (center.getY()+y), (center.getZ()+z)));
		}
		return list;
	}
}
