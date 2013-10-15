package uk.co.vidhucraft.buildtracker.datasource;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLTimeoutException;
import java.sql.Statement;
import java.util.logging.Level;

import org.bukkit.Bukkit;

import uk.co.vidhucraft.buildtracker.RecordType;

public class SqliteDataSource implements DataSource {

	private Connection con;
	
	/**
	 * Connects the the BuildTracker Database
	 */
	@Override
	public boolean connect(){
		try {
			//Create Data folder
			new File("plugins/BuildTracker").mkdir();
			
			//Connect to database and create if not exist
			Class.forName("org.sqlite.JDBC");
			this.con = DriverManager.getConnection("jdbc:sqlite:plugins/BuildTracker/Build.db");
		} catch (ClassNotFoundException e) {
			Bukkit.getLogger().log(Level.SEVERE, "[BuildTracker] Couldn't find SQL Driver");
			e.printStackTrace();
			return false;
		} catch (SQLException e) {
			Bukkit.getLogger().log(Level.SEVERE, "[BuildTracker] Error connecting to db");
			e.printStackTrace();
			return false;
		}
		
		System.out.println("[BuildTracker] Connected to SQL Database");
		return true;
	}
	
	/**
	 * Sets up the build tracker database incase its emptry
	 * @return true if setup was successful
	 */
	public boolean setup(){
		Statement st = null;
        
        try{
        	
        	st = con.createStatement();
        	st.executeUpdate(
    			"CREATE TABLE IF NOT EXISTS Players ("
    			+ "ID_Player INTEGER PRIMARY KEY AUTOINCREMENT, "
    			+ "PlayerName TEXT,"
    			+ "BP NUMERIC DEFAULT 0,"
    			+ "VOTE NUMERIC DEFAULT 0,"
    			+ "RP NUMERIC DEFAULT 0)"
			);
        	
        }catch(SQLException e){
        	
        	e.printStackTrace();
        	return false;
        	
        }finally{
        	
        	close(st);
        	
        }
        
        System.out.println("[BuildTracker] Finished setting up BuildTracker Database");
		return true;
	}
	
	/**
	 * Checks is a player's record exists
	 * 
	 */
	@Override
	public boolean isPlayerRecordExist(String player) {
		PreparedStatement pst = null;
		ResultSet rs = null;
		
		try{
			pst = con.prepareStatement("SELECT * FROM Players WHERE PlayerName = ?");
			pst.setString(1, player);
			rs = pst.executeQuery();
			return rs.next();
		}catch(Exception ex){
			ex.printStackTrace();
		}finally{
			close(pst);
		}
		
		return false;
	}

	@Override
	public double[] getPlayerRecord(String player){
		double points[] = new double[4];
		
		PreparedStatement pst = null;
        ResultSet rs = null;
        
		try{
			pst = con.prepareStatement("SELECT * FROM Players WHERE PlayerName = ?");
			pst.setString(1, player);
			rs = pst.executeQuery();
			while(rs.next()){
				points[1] = rs.getDouble("BP");
				points[2] = rs.getDouble("RP");
				points[3] = rs.getDouble("VOTE");
			}
			//Indication that query was successful
			points[0] = 1;
		}catch(SQLException ex){
			ex.printStackTrace();
		}finally{
			close(pst);
			close(rs);
		}
		
		return points;
	}
	
	
	@Override
	public boolean modifyPlayerRecord(String player, RecordType recordType, double ammount, boolean add) {
		PreparedStatement pst = null;
		String sql = null;
		
		try{
			//Construct the SQL
			if(isPlayerRecordExist(player)){
				sql = "UPDATE Players SET " + recordType.toString() + " = "+ recordType.toString() +"+ ? WHERE PlayerName = ?";
				pst = con.prepareStatement(sql);
				pst.setDouble(1, ammount);
				pst.setString(2, player);
			}else{
				sql = "INSERT INTO Players (PlayerName, " + recordType.toString() + ") " + "VALUES (?, ?)";
				pst = con.prepareStatement(sql);
				pst.setString(1, player);
				pst.setDouble(2, ammount);
			}

			pst.executeUpdate();
			
			//DEBUG:check sql generated
			//System.out.println(sql);
			
		}catch(Exception ex){
			ex.printStackTrace();
		}finally{
			this.close(pst);
		}
		return false;
	}

	@Override
	public boolean removePlayerRecord(String player) {
		PreparedStatement pst = null;
		
		try{
			pst = con.prepareStatement("DELETE FROM Players WHERE PlayerName = ?");
			pst.setString(1, player);
			int rowsAffected = pst.executeUpdate();
			if(rowsAffected == 1)
				return true;
			return false;
		}catch(SQLException e){
			e.printStackTrace();
		}finally{
			close(pst);
		}
		
		return false;
	}

	/**
     * Closes a connection to the SQL statement
     * @param con
     */
	private void close(Statement st) {
        if (st != null) {
            try {
                st.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }

	/**
     * Closes a connection to the ResultSet
     * @param con
     */
    private void close(ResultSet rs) {
        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException ex) {
            	ex.printStackTrace();
            }
        }
    }

    /**
     * Closes a connection to the SQL database
     * @param con
     */
    private void close(Connection con) {
        if (con != null) {
            try {
                con.close();
            } catch (SQLException ex) {
            	ex.printStackTrace();
            }
        }
    }
}
