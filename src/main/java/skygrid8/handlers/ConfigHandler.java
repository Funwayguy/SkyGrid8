package skygrid8.handlers;

import java.util.ArrayList;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraftforge.common.config.Configuration;
import org.apache.logging.log4j.Level;
import skygrid8.core.SG_Settings;
import skygrid8.core.SkyGrid8;

public class ConfigHandler
{
	public static Configuration config;
	
	public static void initConfigs()
	{
		if(config == null)
		{
			SkyGrid8.logger.log(Level.ERROR, "Config attempted to be loaded before it was initialised!");
			return;
		}
		
		config.load();
		
		SG_Settings.height = config.getInt("Grid Height", Configuration.CATEGORY_GENERAL, 128, 1, 255, "How high should the grid of blocks be");
		SG_Settings.dist = config.getInt("Grid Spacing", Configuration.CATEGORY_GENERAL, 3, 0, 15, "How much open space should there be between blocks") + 1;
		SG_Settings.populate = config.getBoolean("Natural Populate", Configuration.CATEGORY_GENERAL, false, "Naturally populate the grid with trees and plants");
		String[] tmp = config.getStringList("Overworld Grid Blocks", Configuration.CATEGORY_GENERAL, SG_Settings.defBlockList, "Which blocks should be present in the grid");
		
		SG_Settings.oBlockList = new ArrayList<IBlockState>();
		
		for(String s : tmp)
		{
			Block b = Block.getBlockFromName(s);
			
			if(b != null)
			{
				SG_Settings.oBlockList.add(b.getDefaultState());
			}
		}
		
		tmp = config.getStringList("Nether Grid Blocks", Configuration.CATEGORY_GENERAL, SG_Settings.defBlockList, "Which blocks should be present in the grid");
		
		SG_Settings.nBlockList = new ArrayList<IBlockState>();
		
		for(String s : tmp)
		{
			Block b = Block.getBlockFromName(s);
			
			if(b != null)
			{
				SG_Settings.nBlockList.add(b.getDefaultState());
			}
		}
		
		tmp = config.getStringList("Crops", Configuration.CATEGORY_GENERAL, SG_Settings.defFarmList, "Which blocks should be present in the grid");
		
		SG_Settings.fBlockList = new ArrayList<IBlockState>();
		
		for(String s : tmp)
		{
			Block b = Block.getBlockFromName(s);
			
			if(b != null)
			{
				SG_Settings.fBlockList.add(b.getDefaultState());
			}
		}
		
		config.save();
		
		SkyGrid8.logger.log(Level.INFO, "Loaded configs...");
	}
}
