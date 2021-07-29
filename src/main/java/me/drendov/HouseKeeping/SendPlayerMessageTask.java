package me.drendov.HouseKeeping;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.logging.Logger;

class SendPlayerMessageTask implements Runnable
{
	private Player player;
	private ChatColor color;
	private String message;
	private static HouseKeeping plugin = HouseKeeping.getInstance();
	private static Logger logger = plugin.getLogger();

	
	public SendPlayerMessageTask(Player player, ChatColor color, String message)
	{
		this.player = player;
		this.color = color;
		this.message = message;
	}

	@Override
	public void run()
	{
		if(player == null)
		{
		    logger.info(color + message);
		    return;
		}
	    
 		HouseKeeping.sendMessage(this.player, this.color, this.message);
	}
}
