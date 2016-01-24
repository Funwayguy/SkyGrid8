package skygrid8.handlers;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDynamicLiquid;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraftforge.fml.common.IWorldGenerator;
import org.apache.logging.log4j.Level;
import skygrid8.core.SG_Settings;
import skygrid8.core.SkyGrid8;

public class GridGenerator implements IWorldGenerator
{
	public static ArrayList<Block> blockCache = new ArrayList<Block>();
	
	Field iUpdate;
	
	@Override
	public void generate(Random random, int chunkX, int chunkZ, World world, IChunkProvider chunkGenerator, IChunkProvider chunkProvider)
	{
		if(world.isRemote)
		{
			return;
		}
		
		if(iUpdate == null)
		{
			try
			{
				iUpdate = World.class.getDeclaredField("field_72999_e");
				iUpdate.setAccessible(true);
			} catch(Exception e1)
			{
				try
				{
					iUpdate = World.class.getDeclaredField("scheduledUpdatesAreImmediate");
					iUpdate.setAccessible(true);
				} catch(Exception e2)
				{
					SkyGrid8.logger.log(Level.ERROR, "Unable to get field for disabling instant updates. Crash highly likely soon!", e2);
				}
			}
		}
		
		boolean flag = false;
		
		if(iUpdate != null) // If I don't do this, blocks like BlockDynamicLiquid throw infinite loops and crash Minecraft
		{
			try
			{
				flag = iUpdate.getBoolean(world);
				iUpdate.setBoolean(world, false);
				
				if(iUpdate.getBoolean(world) != false)
				{
					throw new IllegalArgumentException("Value must be false from here onward");
				}
			} catch(Exception e)
			{
				SkyGrid8.logger.log(Level.ERROR, "An error occured while getting/setting instant update boolean", e);
				return;
			}
		} else
		{
			SkyGrid8.logger.log(Level.ERROR, "Unable to disable immediate updates. Sky grid gen will not continue due to the potential for crashing");
			return;
		}
		
		int x = chunkX*16;
		int z = chunkZ*16;
		
		for(int i = 0; i < 16; i++)
		{
			for(int j = 0; j < 256 && j < SG_Settings.height; j++)
			{
				for(int k = 0; k < 16; k++)
				{
					if((i+x)%SG_Settings.dist != 0 || j%SG_Settings.dist != 0 || (k+z)%SG_Settings.dist != 0)
					{
						world.setBlockState(new BlockPos(i+x, j, k+z), Blocks.air.getDefaultState(), 2);
					} else
					{
						Block b = blockCache.get(random.nextInt(blockCache.size()));
						world.setBlockState(new BlockPos(i+x, j, k+z), b.getDefaultState(), 2);
					}
				}
			}
		}
		
		if(iUpdate != null)
		{
			try
			{
				iUpdate.setBoolean(world, flag);
			} catch(Exception e)
			{
				SkyGrid8.logger.log(Level.ERROR, "An error occured while restoring instant update boolean", e);
				return;
			}
		}
	}
	
	static
	{
		for(Block b : Block.blockRegistry)
		{
			if(b instanceof BlockDynamicLiquid)
			{
				continue;
			}
			
			blockCache.add(b);
		}
	}
}
