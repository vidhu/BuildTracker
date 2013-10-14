package uk.co.vidhucraft.buildtracker.datasource;

import java.sql.SQLException;

import uk.co.vidhucraft.buildtracker.RecordType;

public interface DataSource {

	public enum DataSourceProvider{
		MySql, SQLite
	}
	
	boolean connect();
	
	boolean setup();
	
	double[] getPlayerRecord(String player);
	
	boolean isPlayerRecordExist(String player);
	
	boolean modifyPlayerRecord(String player, RecordType recordType, double ammount, boolean add);	
	
	boolean removePlayerRecord(String player);

}
