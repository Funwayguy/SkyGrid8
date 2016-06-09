package skygrid8.core;

import net.minecraft.world.DimensionType;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.common.registry.GameRegistry;
import org.apache.logging.log4j.Logger;
import skygrid8.core.proxies.CommonProxy;
import skygrid8.handlers.ConfigHandler;
import skygrid8.world.PostGenerator;
import skygrid8.world.WorldProviderEndGrid;
import skygrid8.world.WorldProviderNetherGrid;
import skygrid8.world.WorldProviderOverworldGrid;

@Mod(modid = SkyGrid8.MODID, version = SkyGrid8.VERSION, name = SkyGrid8.NAME, guiFactory = "skygrid8.handlers.ConfigGuiFactory")
public class SkyGrid8
{
    public static final String MODID = "skygrid8";
    public static final String VERSION = "SG_VER_KEY";
    public static final String NAME = "SkyGrid8";
    public static final String PROXY = "skygrid8.core.proxies";
    public static final String CHANNEL = "SG8_CHAN";
	
	@Instance(MODID)
	public static SkyGrid8 instance;
	
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
    }
    
    @EventHandler
    public void init(FMLInitializationEvent event)
    {
    	proxy.registerRenderers();
    	
    	DimensionManager.unregisterDimension(0);
    	DimensionManager.registerDimension(0, DimensionType.register("SkyGrid", "_skygrid", 0, WorldProviderOverworldGrid.class, true));
    	
    	DimensionManager.unregisterDimension(-1);
    	DimensionManager.registerDimension(-1, DimensionType.register("NetherGrid", "_nethergrid", -1, WorldProviderNetherGrid.class, true));
    	
    	DimensionManager.unregisterDimension(1);
    	DimensionManager.registerDimension(1, DimensionType.register("EndGrid", "_endgrid", 1, WorldProviderEndGrid.class, true));
    	
    	GameRegistry.registerWorldGenerator(new PostGenerator(), 0);
    }
    
    @EventHandler
    public void postInit(FMLPostInitializationEvent event)
    {
    }
}
