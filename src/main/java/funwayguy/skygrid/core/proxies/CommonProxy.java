package funwayguy.skygrid.core.proxies;

import funwayguy.skygrid.handlers.EventHandler;
import net.minecraftforge.common.MinecraftForge;

public class CommonProxy
{
	public boolean isClient()
	{
		return false;
	}
	
	public void registerHandlers()
	{
		MinecraftForge.EVENT_BUS.register(new EventHandler());
	}

	public void registerRenderers()
	{
	}
}
