package funwayguy.skygrid.core;

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
import funwayguy.skygrid.core.proxies.CommonProxy;
import funwayguy.skygrid.handlers.ConfigHandler;
import funwayguy.skygrid.world.PostGenerator;
import funwayguy.skygrid.world.WorldProviderEndGrid;
import funwayguy.skygrid.world.WorldProviderNetherGrid;
import funwayguy.skygrid.world.WorldProviderOverworldGrid;

@Mod(modid = SkyGrid.MODID, version = SkyGrid.VERSION, name = SkyGrid.NAME, guiFactory = "funwayguy.skygrid.handlers.ConfigGuiFactory")
public class SkyGrid
{
    public static final String MODID = "skygrid";
    public static final String VERSION = "CI_MOD_VERSION";
    public static final String NAME = "Sky Grid";
    public static final String PROXY = "funwayguy.skygrid.core.proxies";
    public static final String CHANNEL = "SKY_CHAN";
	
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
