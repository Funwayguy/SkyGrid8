package skygrid8.core;

import java.util.ArrayList;
import net.minecraft.block.Block;
import net.minecraft.block.BlockChest;
import net.minecraft.block.BlockDynamicLiquid;
import net.minecraft.block.BlockFarmland;
import net.minecraft.block.state.IBlockState;
import net.minecraftforge.common.IPlantable;


/**
 * A container for all the configurable settings in the mod
 */
public class SG_Settings
{
	public static boolean populate = false;
	public static int height = 128;
	public static int dist = 4;
	public static ArrayList<IBlockState> oBlockList = new ArrayList<IBlockState>();
	public static ArrayList<IBlockState> nBlockList = new ArrayList<IBlockState>();
	public static ArrayList<IBlockState> fBlockList = new ArrayList<IBlockState>();
	public static String[] defBlockList;
	public static String[] defFarmList;
	
	static
	{
		ArrayList<String> tmp1 = new ArrayList<String>();
		ArrayList<String> tmp2 = new ArrayList<String>();
		
		for(Block b : Block.blockRegistry)
		{
			String s = Block.blockRegistry.getNameForObject(b).toString();
			
			if(b instanceof IPlantable)
			{
				tmp2.add(s);
				continue;
			} else if(b instanceof BlockFarmland || b.getClass() == BlockChest.class)
			{
				tmp1.add(s);
				continue;
			} else if(b instanceof BlockDynamicLiquid || !b.isFullCube())
			{
				continue;
			}
			
			tmp1.add(s);
		}
		
		defBlockList = tmp1.toArray(new String[0]);
		defFarmList = tmp2.toArray(new String[0]);
	}
}
