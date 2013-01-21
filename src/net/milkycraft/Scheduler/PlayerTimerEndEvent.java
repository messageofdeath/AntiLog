package net.milkycraft.Scheduler;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PlayerTimerEndEvent extends Event {

	private static final HandlerList handlers = new HandlerList();
	private String name;
 
    public PlayerTimerEndEvent(String name) {
    	this.name = name;
    }
    
    // My Stuff
    public Player getPlayer() {
    	return Bukkit.getPlayer(name);
    }
    
    // Required Stuff
    public HandlerList getHandlers() {
        return handlers;
    }
 
    public static HandlerList getHandlerList() {
        return handlers;
    }
}
