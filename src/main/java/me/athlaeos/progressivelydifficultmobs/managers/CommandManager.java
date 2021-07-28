package me.athlaeos.progressivelydifficultmobs.managers;

import me.athlaeos.progressivelydifficultmobs.commands.*;
import me.athlaeos.progressivelydifficultmobs.main.Main;
import me.athlaeos.progressivelydifficultmobs.utils.Utils;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommandManager implements TabExecutor {
	
	private Main plugin;
	private Map<String, Command> commands = new HashMap<>();
	private PluginConfigurationManager config;
	
	public CommandManager(Main plugin) {
		this.plugin = plugin;
		config = PluginConfigurationManager.getInstance();
		commands.put("help", new HelpCommand());
		commands.put("localdifficulty", new GetLocalDifficultyCommand());
		commands.put("togglekarma", new ToggleKarmaUsageCommand());
		commands.put("newloottable", new AddLootTableCommand());
		commands.put("newmonster", new CreateNewMonsterCommand());
		commands.put("loottables", new ManageLootTablesCommand());
		commands.put("monsters", new ManageLeveledMonstersCommand());
		commands.put("perks", new ManagePerksCommand());
		commands.put("cooldowns", new ManageCooldownsCommand());
		commands.put("addkarma", new AddKarmaCommand());
		commands.put("getkarma", new GetKarmaCommand());
		commands.put("setkarma", new SetKarmaCommand());
		commands.put("getkarmamult", new GetKarmaMultiplierCommand());
		commands.put("setkarmamult", new SetKarmaMultiplierCommand());
		commands.put("addcurse", new AddCurseCommand());
		commands.put("getcurse", new GetCurseCommand());
		commands.put("setcurse", new SetCurseCommand());
		commands.put("getitem", new GetItemCommand());
		commands.put("getspawnegg", new GetSpawnEggCommand());
		commands.put("reload", new ReloadCommand());
		commands.put("changenick", new ChangeMonsterDisplayNameCommand());
		commands.put("savechanges", new SaveChangesCommand());

	    ((HelpCommand) commands.get("help")).giveCommandMap(commands);
		
		plugin.getCommand("pdm").setExecutor(this);
	}

	/**
	 * Registers a custom command argument to be used with /pdm
	 * @param commandArgument
	 * @param command
	 */
	public void registerCommand(String commandArgument, Command command){
		commands.put(commandArgument, command);
		((HelpCommand) commands.get("help")).giveCommandMap(commands);
	}

	@Override
	public boolean onCommand(CommandSender sender, org.bukkit.command.Command cmd, String name, String[] args) {
		if (args.length == 0) {
			sender.sendMessage(Utils.chat(String.format("&aProgressive Difficulty : Mobs (PDM) v%s by Athlaeos", plugin.getDescription().getVersion())));
			sender.sendMessage(Utils.chat(config.getBaseCommandHelp()));
			return true;
		}
		
		for (String subCommand : commands.keySet()) {
			if (args[0].equalsIgnoreCase(subCommand)) {
				boolean hasPermission = false;
				for (String permission : commands.get(subCommand).getRequiredPermission()){
					if (sender.hasPermission(permission)){
						hasPermission = true;
						break;
					}
				}
				if (!hasPermission){
					sender.sendMessage(Utils.chat(config.getErrorNoPermission()));
					return true;
				}
				if (!commands.get(subCommand).execute(sender, args)) {
					sender.sendMessage(Utils.chat(commands.get(subCommand).getFailureMessage()));
				}
				return true;
			}
		}
		sender.sendMessage(Utils.chat(config.getCommandNotFoundError()));
		return true;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, org.bukkit.command.Command cmd, String name, String[] args) {
		if (args.length == 1) {
			List<String> completeList = new ArrayList<>();
			completeList.addAll(commands.keySet());
			return completeList;
		} else if (args.length > 1) {
			for (String arg : commands.keySet()) {
				if (args[0].equalsIgnoreCase(arg)) {
					return commands.get(arg).getSubcommandArgs(sender, args);
				}
			}
		}
		return null;
	}
}
