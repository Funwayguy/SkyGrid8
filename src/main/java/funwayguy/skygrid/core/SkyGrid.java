package funwayguy.skygrid.core;

import funwayguy.skygrid.config.GridRegistry;
import funwayguy.skygrid.core.proxies.CommonProxy;
import funwayguy.skygrid.handlers.ConfigHandler;
import funwayguy.skygrid.world.PostGenerator;
import funwayguy.skygrid.world.WorldProviderEndGrid;
import funwayguy.skygrid.world.WorldProviderNetherGrid;
import funwayguy.skygrid.world.WorldProviderOverworldGrid;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraft.world.DimensionType;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.WorldType;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.*;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import org.apache.logging.log4j.Logger;

@Mod(modid = SkyGrid.MODID, version = "@VERSION@", name = SkyGrid.NAME, guiFactory = "funwayguy.skygrid.handlers.ConfigGuiFactory")
public class SkyGrid
{
    public static final String MODID = "skygrid";
    public static final String NAME = "Sky Grid";
    public static final String PROXY = "funwayguy.skygrid.core.proxies";
    public static final String CHANNEL = "SKY_CHAN";
    
    public static WorldType gridWorld;
	
	@Instance(MODID)
	public static SkyGrid instance;
	
	@SidedProxy(clientSide = PROXY + ".ClientProxy", serverSide = PROXY + ".CommonProxy")
	public static CommonProxy proxy;
	public SimpleNetworkWrapper network ;
	public static Logger logger;
    
    @EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
    	logger = event.getModLog();
    	network = NetworkRegistry.INSTANCE.newSimpleChannel(CHANNEL);
    	ConfigHandler.config = new Configuration(event.getSuggestedConfigurationFile(), true);
    	ConfigHandler.initConfigs();
    	
    	proxy.registerHandlers();
		MinecraftForge.EVENT_BUS.register(this);
    }
    
    @EventHandler
    public void init(FMLInitializationEvent event)
    {
    	proxy.registerRenderers();
    	
    	gridWorld = new WorldType("skygrid");
    	
    	gridCache[0] = DimensionType.register("NetherGrid", "_nethergrid", -1, WorldProviderNetherGrid.class, true);
    	gridCache[1] = DimensionType.register("SkyGrid", "_skygrid", 0, WorldProviderOverworldGrid.class, true);
    	gridCache[2] = DimensionType.register("EndGrid", "_endgrid", 1, WorldProviderEndGrid.class, true);
    	
    	GameRegistry.registerWorldGenerator(new PostGenerator(), 0);
    }
    
    @EventHandler
    public void postInit(FMLPostInitializationEvent event)
    {
    	GridRegistry.loadBlocks();
		//GridRegistry.saveBlocks();
    }
    
    private static boolean inUse = false;
    private static DimensionType[] origCache = new DimensionType[3];
    private static DimensionType[] gridCache = new DimensionType[3];
    
    public static DimensionType[] abyssGrids;
    public static DimensionType[] abyssNorm;
    public static int[] abyssDimIDs;
    
    @SubscribeEvent
	public void onWorldLoad(WorldEvent.Load event)
	{
	}
    
    @EventHandler
    public void serverStarting(FMLServerAboutToStartEvent event)
	{
		if(!inUse)
		{
			boolean enable = false;
			
			if(!proxy.isClient() && event.getServer() instanceof DedicatedServer)
			{
				enable = WorldType.parseWorldType(((DedicatedServer)event.getServer()).getStringProperty("level-type", "DEFAULT")) == gridWorld;
			} else if(event.getServer() instanceof IntegratedServer)
			{
				try
				{
					WorldSettings set = ReflectionHelper.getPrivateValue(IntegratedServer.class, (IntegratedServer)event.getServer(), "field_71350_m", "worldSettings");
					enable = set.getTerrainType() == gridWorld;
				} catch(Exception e)
				{
					SkyGrid.logger.error("Unable to force read server settings", e);
					enable = false;
				}
			}
			
			if(enable)
			{
				SkyGrid.logger.info("Overriding dimension providers with skygrids...");
				
				for(int i = 0; i < 3; i++)
				{
					origCache[i] = DimensionManager.getProviderType(i - 1);
					DimensionManager.unregisterDimension(i - 1);
					DimensionManager.registerDimension(i - 1, gridCache[i]);
				}
				
				if(abyssGrids != null)
				{
					for(int i = 0; i < abyssDimIDs.length; i++)
					{
						abyssNorm[i] = DimensionManager.getProviderType(abyssDimIDs[i]);
						DimensionManager.unregisterDimension(abyssDimIDs[i]);
						DimensionManager.registerDimension(abyssDimIDs[i], abyssGrids[i]);
					}
				}
				
				inUse = true;
			}
		}
	}
	
	@EventHandler
	public void serverStopped(FMLServerStoppedEvent event)
	{
		if(inUse)
		{
			SkyGrid.logger.info("Restoring dimension providers...");
			
			for(int i = 0; i < 3; i++)
			{
				DimensionManager.unregisterDimension(i - 1);
				DimensionManager.registerDimension(i - 1, origCache[i]);
			}
			
			if(abyssGrids != null)
			{
				for(int i = 0; i < abyssDimIDs.length; i++)
				{
					DimensionManager.unregisterDimension(abyssDimIDs[i]);
					DimensionManager.registerDimension(abyssDimIDs[i], abyssNorm[i]);
				}
			}
			
			inUse = false;
		}
	}
}
