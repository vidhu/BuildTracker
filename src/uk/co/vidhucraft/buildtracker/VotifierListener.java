package uk.co.vidhucraft.buildtracker;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import com.vexsoftware.votifier.model.VotifierEvent;

public class VotifierListener implements Listener {
	BuildTracker bt;
	
	public VotifierListener(BuildTracker buildTracker) {
		this.bt = buildTracker;
	}
	
	@EventHandler
	public void vote(VotifierEvent e) {
		String playerName = "";
		playerName = e.getVote().getUsername();
		
		if(!playerName.equals("")){
			System.out.println("[BuildTracker]" + playerName + " voted!");
			bt.ds.modifyPlayerRecord(playerName, RecordType.VOTE, 1, true);
		}else{
			System.out.println("[BuildTracker] Player name wasn't given. :(");
		}
	}

}
