package me.ric.xrayhacker;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;
import me.ric.xrayhacker.XRHBlockListener;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.Event.Priority;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.config.Configuration;

import com.nijiko.permissions.PermissionHandler;
import com.nijikokun.bukkit.Permissions.Permissions;

public class XRayHacker extends JavaPlugin {
	public static Configuration config;
	public static boolean WATCH_DIAMOND_MINERS;
	public static boolean WATCH_LAPIS_MINERS;
	public static boolean WATCH_GOLD_MINERS;
	public static int DIAMOND_MULTIPLE;
	public static int LAPIS_MULTIPLE;
	public static int GOLD_MULTIPLE;
	public static boolean LOGGING_ENABLED;
	public static int DIAMOND_LOG_THRESHOLD;
	public static int LAPIS_LOG_THRESHOLD;
	public static int GOLD_LOG_THRESHOLD;
	public static boolean LOGGING_INITIATED; // use to send Nic a message when he logs in.
	public static List<String> watchedPlayers = new ArrayList<String>();
	
	public static PermissionHandler permissions = null;
    private final XRHBlockListener blockListener = new XRHBlockListener();
    private final XRHPlayerListener playerListener = new XRHPlayerListener();
	protected static final Logger log = Logger.getLogger("Minecraft");
	private boolean console;
	
	@Override
	public void onDisable() {
		System.out.println("[" + getDescription().getName() + "] " + getDescription().getVersion() + " successfully disabled.");
	    if (LOGGING_ENABLED) {
		    MineLogger.stop();
	    }
	}
	
	@Override
	public void onEnable() {
    	setupConfigVariables();
    	setupPermissions();
	    PluginManager pm = getServer().getPluginManager();
	    pm.registerEvent(Event.Type.BLOCK_BREAK, blockListener, Priority.Normal, this);
	    pm.registerEvent(Event.Type.PLAYER_JOIN, playerListener, Priority.Normal, this);

	    PluginDescriptionFile pdfFile = this.getDescription();
	    System.out.println("[" + pdfFile.getName() + "] version [" + pdfFile.getVersion() + "] enabled! " + getSettings() );
	    System.out.println("[" + pdfFile.getName() + "]: Watched players: " + watchedPlayers);
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String commandLabel, String[] args) {
	    String[] split = args;
	    String commandName = command.getName().toLowerCase();
	    if (commandName.equalsIgnoreCase("xrh")) {
		    Player player = null;
		    if (sender instanceof Player) {
		        player = (Player) sender;
		        console=false;
		    }
		    else {
		    	console = false; //init
	            if (sender instanceof ConsoleCommandSender) {
	            	console=true;
	            }
		    }
		    if (split.length == 0) {
		    	return false;
		    }
		    else if (split[0].equalsIgnoreCase("list")) {
            	if (check(sender, "xrh.list")) { // will need to add this permission to Moderators in groups.yml of Permissions
		    		if (split.length == 1) {
	            		displayMiningActivity(XRHBlockListener.diamondMiners, player, "diamond", 0);
	            		displayMiningActivity(XRHBlockListener.lapisMiners, player, "lapis", 0);
	            		displayMiningActivity(XRHBlockListener.goldMiners, player, "gold", 0);
	               		return true;
		    		}
		    		else if (split.length == 2) {
			    		if (isInteger(split[1])) {
		            		Integer threshhold = Integer.parseInt(split[1]);
		            		displayMiningActivity(XRHBlockListener.diamondMiners, player, "diamond", threshhold);
		            		displayMiningActivity(XRHBlockListener.lapisMiners, player, "lapis", threshhold);
		            		displayMiningActivity(XRHBlockListener.goldMiners, player, "gold", threshhold);
		               		return true;
			    		}
			    		else {
			    			String oreType = split[1];
			    			if (oreType.equalsIgnoreCase("diamond")) {
			            		displayMiningActivity(XRHBlockListener.diamondMiners, player, "diamond", 0);
			            		return true;
			    			}
			    			else if (oreType.equalsIgnoreCase("lapis")) {
			            		displayMiningActivity(XRHBlockListener.lapisMiners, player, "lapis", 0);
			            		return true;
			    			}
			    			else if (oreType.equalsIgnoreCase("gold")) {
			            		displayMiningActivity(XRHBlockListener.goldMiners, player, "gold", 0);
			            		return true;
			    			}
			    		}
		    		}
		    		else if (split.length == 3) {
			    		if (isInteger(split[2])) {
		            		Integer threshhold = Integer.parseInt(split[2]);
			    			String oreType = split[1];
			    			if (oreType.equalsIgnoreCase("diamond")) {
			            		displayMiningActivity(XRHBlockListener.diamondMiners, player, "diamond", threshhold);
			            		return true;
			    			}
			    			else if (oreType.equalsIgnoreCase("lapis")) {
			            		displayMiningActivity(XRHBlockListener.lapisMiners, player, "lapis", threshhold);
			            		return true;
			    			}
			    			else if (oreType.equalsIgnoreCase("gold")) {
			            		displayMiningActivity(XRHBlockListener.goldMiners, player, "gold", threshhold);
			            		return true;
			    			}
			    		}
		    		}
            	}
	    	}
            else if (split[0].equalsIgnoreCase("help")) {
            	if (check(sender, "xrh.list")) {
            		displayHelpMessage(player);
            		return true;
            	}
            }
            else if (split[0].equalsIgnoreCase("watch") || console) {
            	if (check(sender, "xrh.watch") || console) {
                    if (split.length == 2 && split[1].equalsIgnoreCase("list")) {
                		sender.sendMessage(watchedPlayers.toString());
                		return true;
                }
                    else if (split.length == 3 && split[1].equalsIgnoreCase("add")) {
                    	if (watchedPlayers.add(split[2])) {
                    		sender.sendMessage(split[2] + " was added to the watch list.");
                   		 	config.setProperty("watched_players", watchedPlayers); 
                    		config.save();
                    	}
                    	else {
                    		player.sendMessage(split[2] + " is not on the watch list.");
                    	}
                		return true;
                    }
                    else if (split.length == 3 && split[1].equalsIgnoreCase("remove")) {
                    	if (watchedPlayers.remove(split[2])) {
                    		sender.sendMessage(split[2] + " was removed from the watch list.");
                   		 	config.setProperty("watched_players", watchedPlayers); 
                    		config.save();
                    	}
                    	else {
                    		player.sendMessage(split[2] + " is not on the watch list.");
                    	}
                		return true;
                    }
            	}
           }
	    }
	    return false;
	}
	
	private void setupConfigVariables() {
		config = new Configuration(new File(getDataFolder() , "config.yml"));
		
		//Load the config if it's there
		try {
			config.load();
		}
		catch(Exception ex){
			//Ignore the errors
		}
		
		//Load our variables from configuration
		WATCH_DIAMOND_MINERS = config.getBoolean("watch_diamond_miners", true);
		WATCH_LAPIS_MINERS = config.getBoolean("watch_lapis_miners", true);
		WATCH_GOLD_MINERS = config.getBoolean("watch_gold_miners", true);
		DIAMOND_MULTIPLE = config.getInt("diamond_multiple", 10);
		LAPIS_MULTIPLE = config.getInt("lapis_multiple", 10);
		GOLD_MULTIPLE = config.getInt("gold_multiple", 20);
		LOGGING_ENABLED = config.getBoolean("logging_enabled", true);
		DIAMOND_LOG_THRESHOLD = config.getInt("diamond_log_threshhold", 20);
		LAPIS_LOG_THRESHOLD = config.getInt("lapis_log_threshhold", 20);
		GOLD_LOG_THRESHOLD = config.getInt("gold_log_threshhold", 40);
		LOGGING_INITIATED = config.getBoolean("logging_initiated", false);
		watchedPlayers = config.getStringList("watched_players", null); 

		//Save the configuration(especially if it wasn't before)
		config.save();
	}
	
	public void setupPermissions() {
    	Plugin test = this.getServer().getPluginManager().getPlugin("Permissions");
    	
    	if (permissions == null) {
    		if (test != null) {
    			this.getServer().getPluginManager().enablePlugin(test);
    			permissions = ((Permissions) test).getHandler();
    		}
    		else {
    			System.out.println("[" + getDescription().getName() + "] Permissions not detected.");
    		}
    	}
    }

	public boolean check(CommandSender sender, String permNode) {
		if (sender instanceof Player) {
			if (permissions == null) {
				return sender.isOp();
			}
			else {
				Player player = (Player) sender;
				return permissions.has(player, permNode);
			}
		}
		else if (sender instanceof ConsoleCommandSender) {
			return true;
		}
		else {
			return false;
		}
	}
	
	public static boolean isInteger(String string) {
	    try {
	        Integer.parseInt(string);
	    } catch (Exception e) {
	        return false;
	    }
	    return true;
	}
	
    private void displayMiningActivity(HashMap <Player, Integer> miners, Player player, String blockType, Integer threshhold ) {
   		if ((blockType == "gold" && WATCH_GOLD_MINERS) || (blockType == "diamond" && WATCH_DIAMOND_MINERS) || (blockType == "lapis" && WATCH_LAPIS_MINERS)) {
    		String message;
	    	if (miners.size() == 0) {
	    		message = "No one has mined any "  + blockType + " ore.";
	    		sendMessage(message, player);
	   		}
	   		else {
	   			Integer numBlocks = 0;
	   			Boolean listing = false;
	    		for (Iterator<Player> i = miners.keySet().iterator(); i.hasNext();) {
	        		Player miner = (Player) i.next();
	        		numBlocks = miners.get(miner);
	        		if (numBlocks >= threshhold){
	        			listing=true;
	    	    		message = miner.getDisplayName() + " " + numBlocks + " " + blockType + " ore.";
	    	    		sendMessage(message, player);
	        		}
	    		}
	    		if (!listing) {
    	    		message = "No one has mined more than " + threshhold + " " + blockType + " ore.";
    	    		sendMessage(message, player);
	    		}
	   		}
   		}
    }

    private void displayHelpMessage(Player player) {
		String message = "/xrh list       lists all mining of watched blocks";
		sendMessage(message, player);
		message = "/xrh list [num]        lists only if more than [num] blocks";
		sendMessage(message, player);
		message = "/xrh list [ore]        lists for [ore] (diamond, lapis or gold)";
		sendMessage(message, player);
		message = "/xrh list [ore] [num]  lists for [ore] if more that [num]";
		sendMessage(message, player);
    	if (check(player, "xrh.add")) {
    		message = "/xrh watch list      display watched players";
    		sendMessage(message, player);
    		message = "/xrh watch add [player]      add player to watch list";
    		sendMessage(message, player);
    		message = "/xrh watch remove [player]   removes player from watch list";
    		sendMessage(message, player);
    	}
    }
    
    /**
	 * Outputs a message, either in game or on the console
	 * If entered from the console then console will be true.
	 * @param message - the message to be displayed.
	 * @param player - the player who entered the command if from in game.
	 */
   private void sendMessage(String message, Player player) {
		if (console) {
			System.out.println(message);
		}
		else {
    		player.sendMessage(message);
		}
    }

   public static void updateLoggingInitiatedFlag(boolean set) {
	   if (LOGGING_INITIATED != set) {
			LOGGING_INITIATED = set;
			config.setProperty("logging_initiated", set);
			config.save();
	   }
	}

   /**
  * gets settings string
  * @return
  */
   private String getSettings() {
	    String D = "";
	    String L = "";
	    String G = "";
	    String E = "";
	    if (WATCH_DIAMOND_MINERS) {
	    	D = " D:" + DIAMOND_MULTIPLE;
		    if (LOGGING_ENABLED) {
		    	D += "," + DIAMOND_LOG_THRESHOLD;
		    }
	    }
	    if (WATCH_LAPIS_MINERS) {
	    	L = " L:" + LAPIS_MULTIPLE;
		    if (LOGGING_ENABLED) {
		    	L += "," + LAPIS_LOG_THRESHOLD;
		    }
	    }
	    if (WATCH_GOLD_MINERS) {
	    	G = " G:" + GOLD_MULTIPLE;
		    if (LOGGING_ENABLED) {
		    	G += "," + GOLD_LOG_THRESHOLD;
		    }
	    }
	    if (LOGGING_ENABLED) {
	    	E = " Logging enabled.";
	    }
	    if (LOGGING_ENABLED) {
		    MineLogger.start();
	    }
	    return D + L + G + E;
   }

   // end of class
 //System.out.println("XRayHacker.java variable " + variable);
 //System.out.println( "XRayHacker.java variable: Got to line xxxxx");
}
