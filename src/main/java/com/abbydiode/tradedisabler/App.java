package com.abbydiode.tradedisabler;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import com.abbydiode.tradedisabler.listeners.PlayerInteractEntityListener;

import java.util.ArrayList;
import java.util.List;

public class App extends JavaPlugin {
	public static App getInstance() { return getPlugin(App.class); }
	
	@Override
	public void onEnable() {
		saveDefaultConfig();

		new PlayerInteractEntityListener(this);
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!sender.hasPermission("tradedisabler.commands"))
			return false;

		if (args.length == 1 && args[0].equalsIgnoreCase("reload")) {
			reloadConfig();

			sender.sendMessage("Config successfully reloaded");
			return true;
		}

		if (args.length == 1 && args[0].equalsIgnoreCase("enable")) {
			getConfig().set("enable", !getConfig().getBoolean("enable"));
			saveConfig();

			sender.sendMessage("Enable plugin state now is " + (getConfig().getBoolean("enable") ? "enabled" : "disabled"));
			return true;
		}

		if (args.length == 1 && args[0].equalsIgnoreCase("info")) {
			sender.sendMessage(
					"Enabled: " + getConfig().getBoolean("enable") + "\n"
			);
			return true;
		}

		return false;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
		if (args.length == 1) {
			if (args[0].trim().isEmpty())
				return List.of("reload", "enable","info");

			ArrayList<String> hints = new ArrayList<>();

			if ("reload".startsWith(args[0]) && !"reload".equals(args[0]))
				hints.add("reload");

			if ("enable".startsWith(args[0]) && !"enable".equals(args[0]))
				hints.add("enable");

			if ("info".startsWith(args[0]) && !"info".equals(args[0]))
				hints.add("info");

			return hints;
		}

		return new ArrayList<>();
	}
}
