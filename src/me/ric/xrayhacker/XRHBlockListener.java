package me.ric.xrayhacker;

import java.util.HashMap;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockListener;

public class XRHBlockListener extends BlockListener {
	public static HashMap<Player, Integer> diamondMiners = new HashMap<Player, Integer>();
	public static HashMap<Player, Integer> lapisMiners = new HashMap<Player, Integer>();
	public static HashMap<Player, Integer> goldMiners = new HashMap<Player, Integer>();
	private boolean playerOnWatchList;
	private Block block;
	private Player player;
	private Integer threshhold;
	private Integer multiple;
	

    public void onBlockBreak(BlockBreakEvent event) {
    	player = event.getPlayer();
        block = event.getBlock();
    	playerOnWatchList = XRayHacker.watchedPlayers.contains(player.getName());

        if (XRayHacker.WATCH_DIAMOND_MINERS && block.getType() == Material.DIAMOND_ORE) {
        	threshhold = XRayHacker.DIAMOND_LOG_THRESHOLD;
        	multiple = XRayHacker.DIAMOND_MULTIPLE;
        	processLogging(diamondMiners, "diamond", "D");
	    }
        else if (XRayHacker.WATCH_LAPIS_MINERS && block.getType() == Material.LAPIS_ORE) {
        	threshhold = XRayHacker.LAPIS_LOG_THRESHOLD;
        	multiple = XRayHacker.LAPIS_MULTIPLE;
        	processLogging(lapisMiners, "lapis", "L");
	    }
       
        else if (XRayHacker.WATCH_GOLD_MINERS && block.getType() == Material.GOLD_ORE) {
        	threshhold = XRayHacker.GOLD_LOG_THRESHOLD;
        	multiple = XRayHacker.GOLD_MULTIPLE;
        	processLogging(goldMiners, "gold", "G");
	    }
	}
    
	/**
	 * Send message if require to Admins and Mods
	 * Logs mining activity if required
	 * @param miners - list of all miners for type of ore
	 * @param String oreType - eg "diamond"
	 * @param String oreAbbrev - eg "D"
	 */
	private void processLogging(HashMap<Player, Integer> miners, String oreType, String oreAbbrev) {
		Integer updatedOre;
		Integer existingOre=miners.get(player);
		String message;
		if (existingOre == null) {
			updatedOre=1;
		}
		else {
			updatedOre=existingOre+1;
		}
		miners.put(player, updatedOre);
		// send a message to admins and mods if a player has mined a multiple of ore blocks
		if (updatedOre%multiple==0) {
			message = player.getName() + " has broken a total of " + updatedOre + " diamond ore blocks";
			sendMessageToAdmins(player, message);
		}
		if (XRayHacker.LOGGING_ENABLED) {
	    	if ((updatedOre == threshhold && !playerOnWatchList)) {
	    		XRayHacker.updateLoggingInitiatedFlag(true);
	    		message = "Logging of diamond mining started for " + player.getName() + ".";
	    		sendMessageToAdmins(player, message);
	    	}
	    	if (updatedOre >= threshhold || playerOnWatchList) {
	        	logMiningActivity(player, block, oreAbbrev, updatedOre);
	    	}
		}
	}
    
	private void sendMessageToAdmins(Player player, String message) {
		String world=player.getWorld().getName();
		Player[] playerList = player.getServer().getOnlinePlayers();
		for (Player p: playerList) {
			  String name = p.getName();
		    if (XRayHacker.permissions.inGroup(world, name, "Admins") || XRayHacker.permissions.inGroup(world, name, "CMods") || XRayHacker.permissions.inGroup(world, name, "ZMods")) {
				  p.sendMessage(message);
			}    	       
		}
	}

    private void logMiningActivity(Player player, Block block, String blockType, int runningTotal) {
    	Location location = block.getLocation();
    	int x = (int)Math.floor(location.getX());
    	int y = (int)Math.floor(location.getY());
    	int z = (int)Math.floor(location.getZ());
    	MineLogger.log(player.getName() + " " + blockType + " " + x + " " + y + " " + z + " (" + runningTotal + ")");
	}
}
//System.out.println("XRHBlockListenre.java variable " + variable);
//System.out.println( "XRHBlockListenre.java variable: Got to line xxxxx");
