package me.ric.xrayhacker;

import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerListener;

/**
* Handle events for all Player related events
*/
public class XRHPlayerListener extends PlayerListener {

    @Override
    /**
    * Send a message to Nic if logging has been enabled
    * Reset flag to false, update and save it to config.yml
    */
    public void onPlayerJoin(PlayerJoinEvent event) {
    	Player player = event.getPlayer();
    	if (XRayHacker.LOGGING_INITIATED && player.getName().equals("Luminus")) {
        	player.sendMessage("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
    		player.sendMessage("XX  xrh logging initiated: see xrh.log  XX");
        	player.sendMessage("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
// set LOGGING_INITIATED to false, update and save it into config.yml file 
    		XRayHacker.updateLoggingInitiatedFlag(false);
    	}
    }

}
