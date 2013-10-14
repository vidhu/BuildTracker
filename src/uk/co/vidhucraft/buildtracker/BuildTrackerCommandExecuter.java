package uk.co.vidhucraft.buildtracker;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

public class BuildTrackerCommandExecuter implements CommandExecutor {
	BuildTracker plugin;

	public BuildTrackerCommandExecuter(BuildTracker plugin) {
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		
		if (args != null && args[0].equals("get")) {
			// Sanity Check
			if (args.length != 2)
				return false;
			if(!sender.hasPermission("bt.get")){
				sender.sendMessage(ChatColor.RED + "No permission!");
				return true;
			}
			
			String player = args[1];

			// Execute Command
			double points[] = plugin.ds.getPlayerRecord(player);
			if(points[0] == 0)
				return true;
				
			double buildCount = points[1];
			double rsCount = points[2];
			sender.sendMessage(player + " has ");
			sender.sendMessage(ChatColor.GREEN + String.valueOf(buildCount)
					+ " build points");
			sender.sendMessage(ChatColor.RED + String.valueOf(rsCount)
					+ " rs points");
			
			return true;
			
		} else if (args != null && args[0].equals("add")) {
			// Sanity Check
			if (args.length != 4)
				return false;
			if(!sender.hasPermission("bt.add")){
				sender.sendMessage(ChatColor.RED + "No permission!");
				return true;
			}

			// Set the playername
			String playername = args[1];

			// Set the record type
			RecordType recordtype = null;
			if (args[2].equals("rp")) {
				recordtype = RecordType.RP;
			} else if (args[2].equals("bp")) {
				recordtype = RecordType.BP;
			} else {
				return false;
			}

			// Set the ammount to add
			double ammount = 0;
			try {
				ammount = Double.parseDouble(args[3]);
			} catch (NumberFormatException ex) {
				ex.printStackTrace();
				return false;
			}

			// Execute commands
			plugin.ds.modifyPlayerRecord(playername, recordtype, ammount, true);
			sender.sendMessage(ChatColor.GREEN + "Added " + ammount + recordtype + " to " + playername);
			
			return true;
			
		}
		return false;
	}

}
