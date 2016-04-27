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
		DimensionManager.unregisterDimension(AbyssalCraft.configDimId1);
		DimensionManager.registerDimension(AbyssalCraft.configDimId1, DimensionType.register("The Abyssal Wasteland Grid", "_awgrid", AbyssalCraft.configDimId1, WorldProviderAbyssalWastelandGrid.class, AbyssalCraft.keepLoaded1));

		DimensionManager.unregisterDimension(AbyssalCraft.configDimId2);
		DimensionManager.registerDimension(AbyssalCraft.configDimId2, DimensionType.register("The Dreadlands Grid", "_dlgrid", AbyssalCraft.configDimId3, WorldProviderDreadlandsGrid.class, AbyssalCraft.keepLoaded2));

		DimensionManager.unregisterDimension(AbyssalCraft.configDimId3);
		DimensionManager.registerDimension(AbyssalCraft.configDimId3, DimensionType.register("Omothol Grid", "_omtgrid", AbyssalCraft.configDimId3, WorldProviderOmotholGrid.class, AbyssalCraft.keepLoaded3));

		DimensionManager.unregisterDimension(AbyssalCraft.configDimId4);
		DimensionManager.registerDimension(AbyssalCraft.configDimId4, DimensionType.register("The Dark Realm Grid", "_drgrid", AbyssalCraft.configDimId4, WorldProviderDarkRealmGrid.class, AbyssalCraft.keepLoaded4));

		MinecraftForge.EVENT_BUS.register(new DarkRealmSpawnBlockHandler());

		SkyGrid8.logger.info("... And they've become grids.");
		ACLogger.info("Oh well, probably about time I change name to AbyssalGrid...");
	}

	@Override
	public void postInit() {}

	public static ArrayList<String> assignList(int dim){
		if(dim == AbyssalCraft.configDimId1) return SG_Settings.spawnAW;
		if(dim == AbyssalCraft.configDimId2) return SG_Settings.spawnDL;
		if(dim == AbyssalCraft.configDimId3) return SG_Settings.spawnOMT;
		if(dim == AbyssalCraft.configDimId4) return SG_Settings.spawnDR;
		return SG_Settings.spawnO;
	}
}