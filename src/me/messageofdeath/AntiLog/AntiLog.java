package me.messageofdeath.AntiLog;

import net.milkycraft.Scheduler.PlayerTimer;
import net.milkycraft.Scheduler.PlayerTimerEndEvent;
import net.milkycraft.Scheduler.Scheduler;
import net.milkycraft.Scheduler.Time;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class AntiLog extends JavaPlugin implements Listener{
		
	public void onEnable(){
		getLogger().info("version v" + this.getDescription().getVersion() + " is enabled!");
        getServer().getPluginManager().registerEvents(this, this);
	}
	public void onDisable(){
		getLogger().info("version v" + this.getDescription().getVersion() + " is disabled!");
	}
	public void addToCombat(final Player attacker, final Player victim) {
		if(!PlayerTimer.isCoolingDown(victim.getName(), Time.pvpTime)) {
			Scheduler.schedulePlayerCooldown(Scheduler.schedule(this, victim.getName(), Time.pvpTime));
			victim.sendMessage(ChatColor.GOLD + "[Cheesium] " + ChatColor.DARK_RED + "You are now in combat! " + getTimeLeft(victim.getName(), Time.pvpTime));
		}
		if(!PlayerTimer.isCoolingDown(attacker.getName(), Time.pvpTime)) {
			Scheduler.schedulePlayerCooldown(Scheduler.schedule(this, attacker.getName(), Time.pvpTime));
			attacker.sendMessage(ChatColor.GOLD + "[Cheesium] " + ChatColor.DARK_RED + "You are now in combat! " + getTimeLeft(attacker.getName(), Time.pvpTime));
		}
	}
	
	public static String getTimeLeft(String name, Time time) {
		int i = PlayerTimer.getRemainingTime(name, time);
		int remainder = i % 3600, minutes = remainder / 60, seconds = remainder % 60;
		if(minutes == 0) {
			return ChatColor.GRAY + "You have " + seconds + " second(s) left!";
		}
		return ChatColor.GRAY + "You have " + String.valueOf(minutes) + " minutes and " + seconds + " second(s) left!";
	}
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onInteract(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		if(event.getPlayer().isOp())return;
		if(PlayerTimer.isCoolingDown(player.getName(), Time.pvpTime)) {
			if(event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
				Material mat = event.getPlayer().getItemInHand().getType();
				if(mat == Material.APPLE || mat == Material.BAKED_POTATO || mat == Material.BREAD || mat == Material.CAKE || mat == Material.CAKE_BLOCK
						|| mat == Material.CARROT || mat == Material.CARROT_ITEM || mat == Material.CARROT_STICK || mat == Material.POISONOUS_POTATO 
						|| mat == Material.PUMPKIN_PIE || mat == Material.POTATO || mat == Material.POTATO_ITEM || mat == Material.PORK 
						|| mat == Material.RAW_BEEF || mat == Material.RAW_CHICKEN || mat == Material.RAW_FISH || mat == Material.ROTTEN_FLESH
						|| mat == Material.GOLDEN_APPLE || mat == Material.GOLDEN_CARROT || mat == Material.COOKIE || mat == Material.COOKED_BEEF 
						|| mat == Material.COOKED_CHICKEN || mat == Material.COOKED_FISH || mat == Material.MUSHROOM_SOUP || mat == Material.MELON 
						|| mat == Material.SPIDER_EYE || mat == Material.PUMPKIN_PIE) {
					event.setCancelled(true);
					player.sendMessage(ChatColor.GOLD + "[Cheesium] " + ChatColor.DARK_RED + "You are in combat you noob! " + getTimeLeft(player.getName(), Time.pvpTime));
				}
			}
		}
	}
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void combatOff(PlayerTimerEndEvent event) {
		event.getPlayer().sendMessage(ChatColor.GOLD + "[Cheesium] " + ChatColor.GREEN + "You are now out of combat");
	}
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void pvpSet(EntityDamageByEntityEvent event){
		// **** Use of Hands or Weapons ****
    	if(event.getEntity() instanceof Player) {
    		if(event.getDamager() instanceof Player) {
    			addToCombat((Player)event.getDamager(), (Player)event.getEntity());
    		}
    	}
    	// **** Use of Bows ****
    	if(event.getEntity() instanceof Player) {
    		if(event.getDamager() instanceof Projectile) {
    			Projectile p = (Projectile)event.getDamager();
    			if(p.getShooter() instanceof Player) {
    				addToCombat((Player)p.getShooter(), (Player)event.getEntity());
    			}
    		}
    	}
    }
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onCommand(PlayerCommandPreprocessEvent event) {
    	Player player = event.getPlayer();
    	if(player.isOp())return;
    	if(PlayerTimer.isCoolingDown(event.getPlayer().getName(), Time.pvpTime)) {
    		if(event.getMessage().startsWith("/")) {
    			event.setCancelled(true);
    			player.sendMessage(ChatColor.GOLD + "[Cheesium] " + ChatColor.DARK_RED + "You are in combat you noob! " + getTimeLeft(player.getName(), Time.pvpTime));
    		}
    	}
    }
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onDeath(PlayerDeathEvent event) {
    	if(PlayerTimer.isCoolingDown(event.getEntity().getName(), Time.pvpTime)) {
    		PlayerTimer.remove(event.getEntity().getName(), Time.pvpTime);
    	}
    }
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void quitInCombat(PlayerQuitEvent event) {
    	Player player = event.getPlayer();
    	if(Time.pvpTime == null)return;
    	if(PlayerTimer.isCoolingDown(player.getName(), Time.pvpTime)) {
    		PlayerTimer.remove(player.getName(), Time.pvpTime);
			player.setHealth(0);
			event.setQuitMessage(ChatColor.GOLD + "[Cheesium] " + ChatColor.RED + player.getName() + ChatColor.AQUA + " has just pvp-logged! Shame on him!");
    	}
    }
    @EventHandler(priority = EventPriority.MONITOR)
    public void quitInCombat(PlayerKickEvent event) {
    	Player player = event.getPlayer();
    	if(PlayerTimer.isCoolingDown(player.getName(), Time.pvpTime)) {
    		PlayerTimer.remove(player.getName(), Time.pvpTime);
			player.setHealth(0);
    	}
    }
}