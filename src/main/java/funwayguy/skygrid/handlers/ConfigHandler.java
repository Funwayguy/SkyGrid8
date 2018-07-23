package funwayguy.skygrid.handlers;

import funwayguy.skygrid.config.GridBlock;
import funwayguy.skygrid.config.GridRegistry;
import funwayguy.skygrid.core.SG_Settings;
import funwayguy.skygrid.core.SkyGrid;
import net.minecraft.block.Block;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.Loader;
import org.apache.logging.log4j.Level;

import java.util.ArrayList;
import java.util.Arrays;

public class ConfigHandler
{
	public static Configuration config;
	
	public static void initConfigs()
	{
		if(config == null)
		{
			SkyGrid.logger.log(Level.ERROR, "Config attempted to be loaded before it was initialised!");
			return;
		}
		
		config.load();
		
		SG_Settings.height = config.getInt("Grid Height", Configuration.CATEGORY_GENERAL, 128, 1, 255, "How high should the grid of blocks be");
		SG_Settings.dist = config.getInt("Grid Spacing", Configuration.CATEGORY_GENERAL, 3, 0, 15, "How much open space should there be between blocks") + 1;
		SG_Settings.populate = config.getBoolean("Natural Populate", Configuration.CATEGORY_GENERAL, false, "Naturally populate the grid with trees and plants");
		SG_Settings.rngSpacing = config.getBoolean("Random Spacing", Configuration.CATEGORY_GENERAL, false, "Randomise the spacing between 1 and the configured value (per chunk)");
		SG_Settings.oldRegen = config.getBoolean("Old Regen", Configuration.CATEGORY_CLIENT, true, "Use the old health regen speed at full hunger (easier to navigate grids)");
		SG_Settings.spawnO = new ArrayList<>();
		SG_Settings.spawnO.addAll(Arrays.asList(config.getStringList("Spawners Overworld", Configuration.CATEGORY_GENERAL, new String[] {"minecraft:skeleton", "minecraft:zombie", "minecraft:spider", "minecraft:cave_spider"}, "Sets the possible spawner types in the grid")));
		SG_Settings.spawnN = new ArrayList<>();
		SG_Settings.spawnN.addAll(Arrays.asList(config.getStringList("Spawners Nether", Configuration.CATEGORY_GENERAL, new String[] {"minecraft:blaze", "minecraft:zombie_pigman", "minecraft:magma_cube"}, "Sets the possible spawner types in the grid")));
		SG_Settings.spawnE = new ArrayList<>();
		SG_Settings.spawnE.addAll(Arrays.asList(config.getStringList("Spawners End", Configuration.CATEGORY_GENERAL, new String[] {"minecraft:enderman", "endermite"}, "Sets the possible spawner types in the grid")));

		if(Loader.isModLoaded("abyssalcraft")){
			SG_Settings.spawnAW = new ArrayList<>();
			SG_Settings.spawnAW.addAll(Arrays.asList(config.getStringList("Spawners Abyssal Wasteland", Configuration.CATEGORY_GENERAL, new String[] {"abyssalcraft:depthsghoul", "abyssalcraft:abyssalzombie", "abyssalcraft:gskeleton", "abyssalcraft:lessershoggoth"}, "Sets ths possible spawner types in the grid")));
			SG_Settings.spawnDL = new ArrayList<>();
			SG_Settings.spawnDL.addAll(Arrays.asList(config.getStringList("Spawners Dreadlands", Configuration.CATEGORY_GENERAL, new String[] {"abyssalcraft:dreadspawn", "abyssalcraft:lessershoggoth"}, "Sets ths possible spawner types in the grid")));
			SG_Settings.spawnOMT = new ArrayList<>();
			SG_Settings.spawnOMT.addAll(Arrays.asList(config.getStringList("Spawners Omothol", Configuration.CATEGORY_GENERAL, new String[] {"abyssalcraft:remnant", "abyssalcraft:jzaharminion", "abyssalcraft:omotholghoul", "abyssalcraft:lessershoggoth"}, "Sets ths possible spawner types in the grid")));
			SG_Settings.spawnDR = new ArrayList<>();
			SG_Settings.spawnDR.addAll(Arrays.asList(config.getStringList("Spawners Dark Realm", Configuration.CATEGORY_GENERAL, new String[] {"abyssalcraft:shadowcreature", "abyssalcraft:shadowmonster", "abyssalcraft:shadowbeast", "abyssalcraft:lessershoggoth"}, "Sets ths possible spawner types in the grid")));
		}

		GridRegistry.loadBlocks();
		
		if(config.getCategory(Configuration.CATEGORY_GENERAL).containsKey("Overworld Grid Blocks")) // Legacy config
		{
			String[] tmp = config.getStringList("Overworld Grid Blocks", Configuration.CATEGORY_GENERAL, new String[0], "Which blocks should be present in the grid");
			
			GridRegistry.blocksOverworld = new ArrayList<>();
			
			for(String s : tmp)
			{
				Block b = Block.getBlockFromName(s);
				
				if(b != null)
				{
					GridRegistry.blocksOverworld.add(new GridBlock(b));
				}
			}
			
			config.getCategory(Configuration.CATEGORY_GENERAL).remove("Overworld Grid Blocks");
		}
		
		if(config.getCategory(Configuration.CATEGORY_GENERAL).containsKey("Nether Grid Blocks")) // Legacy config
		{
			String[] tmp = config.getStringList("Nether Grid Blocks", Configuration.CATEGORY_GENERAL, new String[0], "Which blocks should be present in the grid");
			
			GridRegistry.blocksNether = new ArrayList<>();
			
			for(String s : tmp)
			{
				Block b = Block.getBlockFromName(s);
				
				if(b != null)
				{
					GridRegistry.blocksNether.add(new GridBlock(b));
				}
			}
			
			config.getCategory(Configuration.CATEGORY_GENERAL).remove("Nether Grid Blocks");
		}
		
		if(config.getCategory(Configuration.CATEGORY_GENERAL).containsKey("Crops")) // Legacy config
		{
			config.getCategory(Configuration.CATEGORY_GENERAL).remove("Crops");
		}
		
		GridRegistry.saveBlocks();
		
		config.save();
		
		SkyGrid.logger.log(Level.INFO, "Loaded configs...");
	}
}
