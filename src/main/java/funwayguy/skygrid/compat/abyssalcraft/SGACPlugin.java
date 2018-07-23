package funwayguy.skygrid.compat.abyssalcraft;

import com.shinoow.abyssalcraft.api.integration.ACPlugin;
import com.shinoow.abyssalcraft.api.integration.IACPlugin;
import com.shinoow.abyssalcraft.common.util.ACLogger;
import com.shinoow.abyssalcraft.lib.ACConfig;
import com.shinoow.abyssalcraft.lib.ACLib;
import funwayguy.skygrid.core.SG_Settings;
import funwayguy.skygrid.core.SkyGrid;
import net.minecraft.world.DimensionType;
import net.minecraftforge.common.MinecraftForge;

import java.util.List;

@ACPlugin
public class SGACPlugin implements IACPlugin {

	@Override
	public boolean canLoad() {

		return true;
	}

	@Override
	public String getModName() {

		return SkyGrid.NAME;
	}

	@Override
	public void preInit() {}

	@Override
	public void init() {

		SkyGrid.logger.info("Hey AbyssalCraft, I'm turning your dimensions into grids!");
		ACLogger.info("Wait... YOU'RE DOING WHAT?!");
		
		SkyGrid.abyssNorm = new DimensionType[4]; // We set it here incase AbyssalCraft adds or removes dimensions. We don't actually need to populate it
		SkyGrid.abyssDimIDs = new int[4];
		SkyGrid.abyssDimIDs = new int[]{ACLib.abyssal_wasteland_id, ACLib.dreadlands_id, ACLib.omothol_id, ACLib.dark_realm_id};
		
		//Since everything related to dimensions in my mod relies on the dim IDs, I'll use the same ones here (the keepLoaded part can be hardcoded if you want that)
		SkyGrid.abyssGrids = new DimensionType[4];
		SkyGrid.abyssGrids[0] = DimensionType.register("The Abyssal Wasteland Grid", "_awgrid", ACLib.abyssal_wasteland_id, WorldProviderAbyssalWastelandGrid.class, ACConfig.keepLoaded1);
		SkyGrid.abyssGrids[1] = DimensionType.register("The Dreadlands Grid", "_dlgrid", ACLib.dreadlands_id, WorldProviderDreadlandsGrid.class, ACConfig.keepLoaded2);
		SkyGrid.abyssGrids[2] = DimensionType.register("Omothol Grid", "_omtgrid", ACLib.omothol_id, WorldProviderOmotholGrid.class, ACConfig.keepLoaded3);
		SkyGrid.abyssGrids[3] = DimensionType.register("The Dark Realm Grid", "_drgrid", ACLib.dark_realm_id, WorldProviderDarkRealmGrid.class, ACConfig.keepLoaded4);
		
		MinecraftForge.EVENT_BUS.register(new DarkRealmSpawnBlockHandler());

		SkyGrid.logger.info("... And they've become grids.");
		ACLogger.info("Oh well, probably about time I change name to AbyssalGrid...");
	}

	@Override
	public void postInit() {}

	public static List<String> assignList(int dim){
		if(dim == ACLib.abyssal_wasteland_id) return SG_Settings.spawnAW;
		if(dim == ACLib.dreadlands_id) return SG_Settings.spawnDL;
		if(dim == ACLib.omothol_id) return SG_Settings.spawnOMT;
		if(dim == ACLib.dark_realm_id) return SG_Settings.spawnDR;
		return SG_Settings.spawnO;
	}
}