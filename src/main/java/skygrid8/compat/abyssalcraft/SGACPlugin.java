package skygrid8.compat.abyssalcraft;

import java.util.ArrayList;

import net.minecraft.world.DimensionType;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.MinecraftForge;
import skygrid8.core.SG_Settings;
import skygrid8.core.SkyGrid8;

import com.shinoow.abyssalcraft.AbyssalCraft;
import com.shinoow.abyssalcraft.api.integration.ACPlugin;
import com.shinoow.abyssalcraft.api.integration.IACPlugin;
import com.shinoow.abyssalcraft.common.util.ACLogger;
import com.shinoow.abyssalcraft.lib.ACLib;

@ACPlugin
public class SGACPlugin implements IACPlugin {

	@Override
	public boolean canLoad() {

		return true;
	}

	@Override
	public String getModName() {

		return SkyGrid8.NAME;
	}

	@Override
	public void preInit() {}

	@Override
	public void init() {

		SkyGrid8.logger.info("Hey AbyssalCraft, I'm turning your dimensions into grids!");
		ACLogger.info("Wait... YOU'RE DOING WHAT?!");

		//Since everything related to dimensions in my mod relies on the dim IDs, I'll use the same ones here (the keepLoaded part can be hardcoded if you want that)
		DimensionManager.unregisterDimension(ACLib.abyssal_wasteland_id);
		DimensionManager.registerDimension(ACLib.abyssal_wasteland_id, DimensionType.register("The Abyssal Wasteland Grid", "_awgrid", ACLib.abyssal_wasteland_id, WorldProviderAbyssalWastelandGrid.class, AbyssalCraft.keepLoaded1));

		DimensionManager.unregisterDimension(ACLib.dreadlands_id);
		DimensionManager.registerDimension(ACLib.dreadlands_id, DimensionType.register("The Dreadlands Grid", "_dlgrid", ACLib.dreadlands_id, WorldProviderDreadlandsGrid.class, AbyssalCraft.keepLoaded2));

		DimensionManager.unregisterDimension(ACLib.omothol_id);
		DimensionManager.registerDimension(ACLib.omothol_id, DimensionType.register("Omothol Grid", "_omtgrid", ACLib.omothol_id, WorldProviderOmotholGrid.class, AbyssalCraft.keepLoaded3));

		DimensionManager.unregisterDimension(ACLib.dark_realm_id);
		DimensionManager.registerDimension(ACLib.dark_realm_id, DimensionType.register("The Dark Realm Grid", "_drgrid", ACLib.dark_realm_id, WorldProviderDarkRealmGrid.class, AbyssalCraft.keepLoaded4));

		MinecraftForge.EVENT_BUS.register(new DarkRealmSpawnBlockHandler());

		SkyGrid8.logger.info("... And they've become grids.");
		ACLogger.info("Oh well, probably about time I change name to AbyssalGrid...");
	}

	@Override
	public void postInit() {}

	public static ArrayList<String> assignList(int dim){
		if(dim == ACLib.abyssal_wasteland_id) return SG_Settings.spawnAW;
		if(dim == ACLib.dreadlands_id) return SG_Settings.spawnDL;
		if(dim == ACLib.omothol_id) return SG_Settings.spawnOMT;
		if(dim == ACLib.dark_realm_id) return SG_Settings.spawnDR;
		return SG_Settings.spawnO;
	}
}