package uk.co.vidhucraft.buildtracker;

import uk.co.vidhucraft.buildtracker.datasource.DataSource;
import uk.co.vidhucraft.buildtracker.datasource.SqliteDataSource;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class BuildTracker extends JavaPlugin{
	public DataSource ds = new SqliteDataSource();
	
	@Override
	public void onEnable(){
		//Initialize the Database
		ds.connect();
		ds.setup();
		
		//Set command executer
		getCommand("bt").setExecutor(new BuildTrackerCommandExecuter(this));
		
		//Enable Votifier Listener
		if(Bukkit.getPluginManager().getPlugin("Votifier") != null){
			Bukkit.getPluginManager().registerEvents(new VotifierListener(this), this);
		}
		
	}
}
