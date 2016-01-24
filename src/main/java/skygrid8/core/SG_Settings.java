package skygrid8.core;

import java.util.ArrayList;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDynamicLiquid;
import net.minecraft.block.state.IBlockState;


/**
 * A container for all the configurable settings in the mod
 */
public class SG_Settings
{
	public static int height = 128;
	public static int dist = 4;
	public static ArrayList<IBlockState> oBlockList = new ArrayList<IBlockState>();
	public static ArrayList<IBlockState> nBlockList = new ArrayList<IBlockState>();
	public static String[] defSubList;
	
	static
	{
		ArrayList<String> tmp = new ArrayList<String>();
		
		for(Block b : Block.blockRegistry)
		{
			if(b instanceof BlockDynamicLiquid || !b.isFullCube())
			{
				continue;
			}
			
			String s = Block.blockRegistry.getNameForObject(b).toString();
			tmp.add(s);
		}
		
		defSubList = tmp.toArray(new String[0]);
	}
}
