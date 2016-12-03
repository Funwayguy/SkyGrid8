package skygrid8.core.proxies;

import net.minecraftforge.common.MinecraftForge;
import skygrid8.handlers.EventHandler;

public class CommonProxy
{
	public boolean isClient()
	{
		return false;
	}
	
	public void registerHandlers()
	{
		EventHandler handler = new EventHandler();
		MinecraftForge.EVENT_BUS.register(handler);
	}

	public void registerRenderers()
	{
	}
}
