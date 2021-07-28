package me.athlaeos.progressivelydifficultmobs.commands;

import me.athlaeos.progressivelydifficultmobs.managers.PluginConfigurationManager;
import me.athlaeos.progressivelydifficultmobs.utils.Utils;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class HelpCommand implements Command {

	private List<Command> commands = new ArrayList<Command>();
	private final PluginConfigurationManager config;

	public HelpCommand() {
		config = PluginConfigurationManager.getInstance();
	}
	
	@Override
	public boolean execute(CommandSender sender, String[] args) {
		Map<Integer, ArrayList<String>> helpCommandList;
		List<String[]> helpEntries = new ArrayList<String[]>();
		List<String> helpLines = new ArrayList<String>();
		
		for (Command c : commands) {
			for (String permission : c.getRequiredPermission()) {
				if (sender.hasPermission(permission)) {
					helpEntries.add(c.getHelpEntry());
					break;
				}
			}
		}
		for (String[] commandHelp : helpEntries) {
			helpLines.addAll(Arrays.asList(commandHelp));
		}

		helpCommandList = Utils.paginateTextList(12, helpLines);
		
		if (helpCommandList.size() == 0) {
			return true;
		}
		
		// args[0] is "help" and args.length > 0
		if (args.length == 1) {
			for (String line : helpCommandList.get(0)) {
				sender.sendMessage(Utils.chat(line));
			}
			Utils.chat("&8&m                                             ");
			sender.sendMessage(Utils.chat(String.format("&8[&e1&8/&e%s&8]", helpCommandList.size())));
			return true;
		}

		if (args.length == 2) {
			try {
				Integer.parseInt(args[1]);
			} catch (NumberFormatException nfe) {
				sender.sendMessage(Utils.chat(config.getInvalidNumberError()));
				return true;
			}

			int pageNumber = Integer.parseInt(args[1]);
			if (pageNumber < 1) {
				pageNumber = 1;
			}
			if (pageNumber > helpCommandList.size()) {
				pageNumber = helpCommandList.size();
			}
			
			for (String entry : helpCommandList.get(pageNumber - 1)) {
				sender.sendMessage(Utils.chat(entry));
			}
			Utils.chat("&8&m                                             ");
			sender.sendMessage(Utils.chat(String.format("&8[&e%s&8/&e%s&8]", pageNumber, helpCommandList.size())));
			return true;
		}

		return false;
	}

	@Override
	public String[] getRequiredPermission() {
		return new String[]{"pdm.help"};
	}

	@Override
	public String getFailureMessage() {
		return "&4/pdm help";
	}

	@Override
	public String[] getHelpEntry() {
		return new String[]{};
	}

	public void giveCommandMap(Map<String, Command> commandMap) {
		commands = new ArrayList<>();
		for (String key : commandMap.keySet()) {
			commands.add(commandMap.get(key));
		}
	}

	@Override
	public List<String> getSubcommandArgs(CommandSender sender, String[] args) {
		if (args.length == 2) {
			List<String> subargs = new ArrayList<String>();
			subargs.add("1");
			subargs.add("2");
			subargs.add("3");
			subargs.add("...");
			return subargs;
		}
		List<String> subargs = new ArrayList<String>();
		subargs.add(" ");
		return subargs;
	}
}
